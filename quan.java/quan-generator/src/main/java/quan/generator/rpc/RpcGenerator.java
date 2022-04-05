package quan.generator.rpc;

import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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

    private Types typeUtils;

    private Filer filer;

    public static final String SERVICE_CLASS_NAME = "quan.rpc.Service";

    private TypeMirror serviceType;

    private Template proxyTemplate;

    private Template callerTemplate;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        serviceType = processingEnv.getElementUtils().getTypeElement(SERVICE_CLASS_NAME).asType();

        try {
            Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
            freemarkerCfg.setClassForTemplateLoading(getClass(), "");
            freemarkerCfg.setDefaultEncoding("UTF-8");
            proxyTemplate = freemarkerCfg.getTemplate("proxy.ftl");
            callerTemplate = freemarkerCfg.getTemplate("caller.ftl");
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            e.printStackTrace();
        }
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
            if (!typeUtils.isSubtype(typeElement.asType(), serviceType)) {
                messager.printMessage(Diagnostic.Kind.WARNING, typeElement + " cannot declare an rpc method because it is not a subtype of " + serviceType);
                continue;
            }

            List<ExecutableElement> executableElements = elements.get(typeElement);
            RpcClass rpcClass = new RpcClass(typeElement.getQualifiedName().toString());

            for (ExecutableElement executableElement : executableElements) {
                RpcMethod rpcMethod = new RpcMethod(executableElement.getSimpleName().toString());

                TypeMirror returnType = executableElement.getReturnType();
                if (returnType.getKind().isPrimitive()) {
                    rpcMethod.setReturnType(typeUtils.boxedClass((PrimitiveType) returnType).asType().toString());
                } else if (returnType.getKind() == TypeKind.VOID) {
                    rpcMethod.setReturnType(Void.class.getName());
                } else {
                    rpcMethod.setReturnType(returnType.toString());
                }

                for (VariableElement parameter : executableElement.getParameters()) {
                    rpcMethod.addParameter(parameter.asType().toString(), parameter.toString());
                }

                rpcClass.getMethods().add(rpcMethod);
            }

            try {
                generate(rpcClass);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    private void generate(RpcClass rpcClass) throws IOException {
        JavaFileObject proxyFile = filer.createSourceFile(rpcClass.getFullName() + "Proxy");
        try (Writer proxyWriter = proxyFile.openWriter()) {
            proxyTemplate.process(rpcClass, proxyWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JavaFileObject callerFile = filer.createSourceFile(rpcClass.getFullName() + "Caller");
        try (Writer callerWriter = callerFile.openWriter()) {
            callerTemplate.process(rpcClass, callerWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
