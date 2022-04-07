package quan.generator.rpc;

import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("quan.rpc.RPC")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RpcGenerator extends AbstractProcessor {

    private Messager messager;

    private Filer filer;

    private Types typeUtils;

    private Elements elementUtils;

    public static final String SERVICE_CLASS_NAME = "quan.rpc.Service";

    private TypeMirror serviceType;

    private Template proxyTemplate;

    private Template callerTemplate;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        serviceType = elementUtils.getTypeElement(SERVICE_CLASS_NAME).asType();

        try {
            Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
            freemarkerCfg.setClassForTemplateLoading(getClass(), "");
            freemarkerCfg.setDefaultEncoding("UTF-8");
            proxyTemplate = freemarkerCfg.getTemplate("proxy.ftl");
            callerTemplate = freemarkerCfg.getTemplate("caller.ftl");
        } catch (IOException e) {
            error(e.toString());
            e.printStackTrace();
        }
    }

    private void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, List<ExecutableElement>> elements = new HashMap<>();
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                ExecutableElement executableElement = (ExecutableElement) element;
                TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
                elements.computeIfAbsent(typeElement, k -> new ArrayList<>()).add(executableElement);
            }
        }

        for (TypeElement typeElement : elements.keySet()) {
            processClass(typeElement, elements.get(typeElement));
        }

        return true;
    }

    private void processClass(TypeElement typeElement, List<ExecutableElement> executableElements) {
        if (!typeUtils.isSubtype(typeElement.asType(), serviceType)) {
            error(typeElement + " cannot declare an rpc method, because it is not a subtype of " + serviceType);
            return;
        }

        if (typeElement.getNestingKind().isNested()) {
            error(typeElement + " cannot declare an rpc method, because it is nested kind");
            return;
        }

        RpcClass rpcClass = new RpcClass(typeElement.getQualifiedName().toString());
        rpcClass.setComment(elementUtils.getDocComment(typeElement));
        rpcClass.setTypeParameters(processTypeParameters(typeElement.getTypeParameters()));

        for (ExecutableElement executableElement : executableElements) {
            if (executableElement.getModifiers().contains(Modifier.PRIVATE)) {
                error(typeElement + "." + executableElement + " cannot be declared as rpc method, because it is private");
                continue;
            }
            RpcMethod rpcMethod = processMethod(executableElement);
            rpcMethod.setRpcClass(rpcClass);
            rpcClass.getMethods().add(rpcMethod);
        }

        try {
            generate(rpcClass);
        } catch (IOException e) {
            error(e.toString());
            e.printStackTrace();
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

    private RpcMethod processMethod(ExecutableElement executableElement) {
        RpcMethod rpcMethod = new RpcMethod(executableElement.getSimpleName());
        rpcMethod.setComment(elementUtils.getDocComment(executableElement));
        rpcMethod.setTypeParameters(processTypeParameters(executableElement.getTypeParameters()));

        for (VariableElement parameter : executableElement.getParameters()) {
            rpcMethod.addParameter(parameter.getSimpleName(), parameter.asType().toString());
        }

        TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind().isPrimitive()) {
            rpcMethod.setReturnType(typeUtils.boxedClass((PrimitiveType) returnType).asType().toString());
        } else if (returnType.getKind() == TypeKind.VOID) {
            rpcMethod.setReturnType(Void.class.getSimpleName());
        } else {
            rpcMethod.setReturnType(returnType.toString());
        }

        return rpcMethod;
    }


    private void generate(RpcClass rpcClass) throws IOException {
        JavaFileObject proxyFile = filer.createSourceFile(rpcClass.getFullName() + "Proxy");
        try (Writer proxyWriter = proxyFile.openWriter()) {
            proxyTemplate.process(rpcClass, proxyWriter);
        } catch (Exception e) {
            error(e.toString());
            e.printStackTrace();
        }

        JavaFileObject callerFile = filer.createSourceFile(rpcClass.getFullName() + "Caller");
        try (Writer callerWriter = callerFile.openWriter()) {
            callerTemplate.process(rpcClass, callerWriter);
        } catch (Exception e) {
            error(e.toString());
            e.printStackTrace();
        }
    }

}
