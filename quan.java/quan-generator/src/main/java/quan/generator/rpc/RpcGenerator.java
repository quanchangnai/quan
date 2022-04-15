package quan.generator.rpc;

import freemarker.template.Configuration;
import freemarker.template.Template;

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
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("quan.rpc.Endpoint")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RpcGenerator extends AbstractProcessor {

    private Messager messager;

    private Filer filer;

    private Types typeUtils;

    private Elements elementUtils;

    private TypeMirror serviceType;

    private TypeMirror asyncResultType;

    private Template proxyTemplate;

    private Template callerTemplate;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        serviceType = elementUtils.getTypeElement("quan.rpc.Service").asType();
        asyncResultType = typeUtils.erasure(elementUtils.getTypeElement("quan.rpc.AsyncResult").asType());

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
            error(typeElement + " cannot declare an endpoint method, because it is not a subtype of " + serviceType);
            return;
        }

        if (typeElement.getNestingKind().isNested()) {
            error(typeElement + " cannot declare an endpoint method, because it is nested kind");
            return;
        }

        RpcClass rpcClass = new RpcClass(typeElement.getQualifiedName().toString());
        rpcClass.setComment(elementUtils.getDocComment(typeElement));
        rpcClass.setOriginalTypeParameters(processTypeParameters(typeElement.getTypeParameters()));

        for (ExecutableElement executableElement : executableElements) {
            if (executableElement.getModifiers().contains(Modifier.PRIVATE)) {
                error(typeElement + "." + executableElement + " cannot be declared as endpoint method, because it is private");
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
        rpcMethod.setOriginalTypeParameters(processTypeParameters(executableElement.getTypeParameters()));

        for (VariableElement parameter : executableElement.getParameters()) {
            rpcMethod.addParameter(parameter.getSimpleName(), parameter.asType().toString());
        }

        TypeMirror returnType = executableElement.getReturnType();

        if (returnType.getKind().isPrimitive()) {
            rpcMethod.setOriginalReturnType(typeUtils.boxedClass((PrimitiveType) returnType).asType().toString());
        } else if (returnType.getKind() == TypeKind.VOID) {
            rpcMethod.setOriginalReturnType(Void.class.getSimpleName());
        } else if (typeUtils.isSameType(typeUtils.erasure(returnType), asyncResultType)) {
            rpcMethod.setOriginalReturnType(((DeclaredType) returnType).getTypeArguments().get(0).toString());
        } else {
            rpcMethod.setOriginalReturnType(returnType.toString());
        }


        return rpcMethod;
    }


    private void generate(RpcClass rpcClass) throws IOException {
        rpcClass.optimizeImport4Proxy();
        JavaFileObject proxyFile = filer.createSourceFile(rpcClass.getFullName() + "Proxy");
        try (Writer proxyWriter = proxyFile.openWriter()) {
            proxyTemplate.process(rpcClass, proxyWriter);
        } catch (Exception e) {
            error(e.toString());
            e.printStackTrace();
        }

        rpcClass.optimizeImport4Caller();
        JavaFileObject callerFile = filer.createSourceFile(rpcClass.getFullName() + "Caller");
        try (Writer callerWriter = callerFile.openWriter()) {
            callerTemplate.process(rpcClass, callerWriter);
        } catch (Exception e) {
            error(e.toString());
            e.printStackTrace();
        }
    }

}
