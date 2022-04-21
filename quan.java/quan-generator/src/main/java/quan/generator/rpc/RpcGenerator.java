package quan.generator.rpc;

import freemarker.template.Configuration;
import freemarker.template.Template;
import quan.rpc.Endpoint;
import quan.rpc.Promise;
import quan.rpc.Service;
import quan.rpc.SingletonService;
import quan.util.CommonUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;

@SupportedAnnotationTypes({"quan.rpc.Endpoint", "quan.rpc.SingletonService"})
@SupportedOptions("rpcProxyPath")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RpcGenerator extends AbstractProcessor {

    private Messager messager;

    private Filer filer;

    private Types typeUtils;

    private Elements elementUtils;

    private TypeMirror serviceType;

    private TypeMirror promiseType;

    /**
     * 自定义代理类的生成路径
     */
    private String proxyPath;

    private static Pattern illegalMethodPattern = Pattern.compile("_.*\\$");

    private Template proxyTemplate;

    private Template callerTemplate;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        serviceType = elementUtils.getTypeElement(Service.class.getName()).asType();
        promiseType = typeUtils.erasure(elementUtils.getTypeElement(Promise.class.getName()).asType());
        proxyPath = processingEnv.getOptions().get("rpcProxyPath");
        try {
            Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
            freemarkerCfg.setClassForTemplateLoading(getClass(), "");
            freemarkerCfg.setDefaultEncoding("UTF-8");
            proxyTemplate = freemarkerCfg.getTemplate("proxy.ftl");
            callerTemplate = freemarkerCfg.getTemplate("caller.ftl");
        } catch (IOException e) {
            error(e);
        }
    }

    private void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    private void error(Exception e) {
        messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
        e.printStackTrace();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, List<ExecutableElement>> elements = new HashMap<>();

        for (TypeElement annotation : annotations) {
            boolean endpoint = annotation.getQualifiedName().contentEquals(Endpoint.class.getName());
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (endpoint) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
                    elements.computeIfAbsent(typeElement, k -> new ArrayList<>()).add(executableElement);
                } else {
                    processSingletonService((TypeElement) element);
                }
            }
        }

        for (Element rootElement : roundEnv.getRootElements()) {
            if (rootElement instanceof TypeElement && typeUtils.isSubtype(rootElement.asType(), serviceType)) {
                TypeElement typeElement = (TypeElement) rootElement;
                if (!elements.containsKey(typeElement)) {
                    //可能会有继承下来的Endpoint方法
                    elements.put(typeElement, new ArrayList<>());
                }
            }
        }

        for (TypeElement typeElement : elements.keySet()) {
            processServiceClass(typeElement, elements.get(typeElement));
        }

        return true;
    }

    private void processSingletonService(TypeElement typeElement) {
        if (!typeUtils.isSubtype(typeElement.asType(), serviceType)) {
            error(typeElement + " cannot declare an SingletonService annotation, because it is not a subtype of " + serviceType);
            return;
        }

        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                if (executableElement.getSimpleName().contentEquals("getId") && executableElement.getParameters().isEmpty()) {
                    error(typeElement + " cannot override getId() method, because it is SingletonService");
                    break;
                }
            }
        }
    }

    private void processServiceClass(TypeElement typeElement, List<ExecutableElement> executableElements) {
        if (!typeUtils.isSubtype(typeElement.asType(), serviceType)) {
            error(typeElement + " cannot declare an endpoint method, because it is not a subtype of " + serviceType);
            return;
        }

        if (typeElement.getNestingKind().isNested()) {
            error(typeElement + " cannot declare an endpoint method, because it is nested kind");
            return;
        }

        if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
            return;
        }

        ServiceClass serviceClass = new ServiceClass(typeElement.getQualifiedName().toString());
        serviceClass.setComment(elementUtils.getDocComment(typeElement));
        serviceClass.setOriginalTypeParameters(processTypeParameters(typeElement.getTypeParameters()));

        SingletonService singletonService = typeElement.getAnnotation(SingletonService.class);
        if (singletonService != null) {
            serviceClass.setServiceId(singletonService.id());
        }

        if (!typeUtils.isSameType(typeElement.getSuperclass(), serviceType)) {
            executableElements = new ArrayList<>();
            for (Element memberElement : elementUtils.getAllMembers(typeElement)) {
                if (memberElement instanceof ExecutableElement && memberElement.getAnnotation(Endpoint.class) != null) {
                    executableElements.add((ExecutableElement) memberElement);
                }
            }
        }

        for (ExecutableElement executableElement : executableElements) {
            if (executableElement.getModifiers().contains(Modifier.PRIVATE)) {
                error(typeElement + "." + executableElement + " cannot be declared as endpoint method, because it is private");
                continue;
            }
            if (illegalMethodPattern.matcher(executableElement.getSimpleName()).matches()) {
                error(typeElement + "." + executableElement + " name is illegal");
                continue;
            }
            ServiceMethod serviceMethod = processServiceMethod(executableElement);
            serviceMethod.setServiceClass(serviceClass);
            serviceClass.getMethods().add(serviceMethod);
        }

        if (serviceClass.getMethods().isEmpty()) {
            return;
        }

        try {
            generateProxy(serviceClass);
            generateCaller(serviceClass);
        } catch (IOException e) {
            error(e);
        }
    }

    private LinkedHashMap<String, List<String>> processTypeParameters(List<? extends TypeParameterElement> typeParameterElements) {
        LinkedHashMap<String, List<String>> typeParameters = new LinkedHashMap<>();

        for (TypeParameterElement typeParameter : typeParameterElements) {
            List<String> typeBounds = new ArrayList<>();
            for (TypeMirror typeBound : typeParameter.getBounds()) {
                typeBounds.add(typeBound.toString());
            }
            typeParameters.put(typeParameter.getSimpleName().toString(), typeBounds);
        }

        return typeParameters;
    }

    private ServiceMethod processServiceMethod(ExecutableElement executableElement) {
        ServiceMethod serviceMethod = new ServiceMethod(executableElement.getSimpleName());
        serviceMethod.setComment(elementUtils.getDocComment(executableElement));
        serviceMethod.setOriginalTypeParameters(processTypeParameters(executableElement.getTypeParameters()));

        Endpoint endpoint = executableElement.getAnnotation(Endpoint.class);
        boolean safeParam = true;

        for (VariableElement parameter : executableElement.getParameters()) {
            TypeMirror parameterType = parameter.asType();
            serviceMethod.addParameter(parameter.getSimpleName(), parameterType.toString());
            if (!CommonUtils.isConstantType(parameterType)) {
                safeParam = false;
            }
        }

        TypeMirror returnType = executableElement.getReturnType();

        if (returnType.getKind().isPrimitive()) {
            serviceMethod.setOriginalReturnType(typeUtils.boxedClass((PrimitiveType) returnType).asType().toString());
        } else if (returnType.getKind() == TypeKind.VOID) {
            serviceMethod.setOriginalReturnType(Void.class.getSimpleName());
        } else if (typeUtils.isSubtype(typeUtils.erasure(returnType), promiseType)) {
            serviceMethod.setOriginalReturnType(((DeclaredType) returnType).getTypeArguments().get(0).toString());
        } else {
            serviceMethod.setOriginalReturnType(returnType.toString());
        }

        if (!safeParam) {
            safeParam = endpoint.safeParam();
        }

        boolean safeResult = CommonUtils.isConstantType(returnType);
        if (!safeResult) {
            safeResult = endpoint.safeResult();
        }

        serviceMethod.setSafeParam(safeParam);
        serviceMethod.setSafeResult(safeResult);

        return serviceMethod;
    }

    /**
     * 自定义递归创建目录，因为使用gradle编译时，File.mkdirs的路径不对
     */
    private boolean mkdirs(File path) {
        if (path.exists()) {
            return false;
        }
        if (path.mkdir()) {
            return true;
        }
        File parent = path.getParentFile();
        return parent != null && mkdirs(parent);
    }

    private void generateProxy(ServiceClass serviceClass) throws IOException {
        serviceClass.setCustomPath(proxyPath != null);
        serviceClass.optimizeImport4Proxy();
        Writer proxyWriter;

        if (proxyPath == null) {
            JavaFileObject proxyFile = filer.createSourceFile(serviceClass.getFullName() + "Proxy");
            proxyWriter = proxyFile.openWriter();
        } else {
            File path = new File(proxyPath, serviceClass.getPackageName().replace(".", "/"));
            mkdirs(path);
            File file = new File(path, serviceClass.getName() + "Proxy.java");
            proxyWriter = new FileWriter(file);
        }

        try {
            proxyTemplate.process(serviceClass, proxyWriter);
        } catch (Exception e) {
            error(e);
        } finally {
            proxyWriter.close();
        }

    }

    private void generateCaller(ServiceClass serviceClass) throws IOException {
        serviceClass.setCustomPath(false);
        serviceClass.optimizeImport4Caller();
        JavaFileObject callerFile = filer.createSourceFile(serviceClass.getFullName() + "Caller");

        try (Writer callerWriter = callerFile.openWriter()) {
            callerTemplate.process(serviceClass, callerWriter);
        } catch (Exception e) {
            error(e);
        }
    }

}
