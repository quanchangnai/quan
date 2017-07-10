package quan.protocol.generator;

import quan.protocol.Bean;
import quan.protocol.Protocol;
import quan.protocol.stream.ReadableStream;
import quan.protocol.stream.WritableStream;

import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaGenerator extends Generator {

    public JavaGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);
    }

    @Override
    protected String getLanguage() {
        return "java";
    }

    @Override
    protected void preprocess(List<Definition> definitions) {
        for (Definition definition : definitions) {
            if (definition instanceof BeanDefinition) {
                BeanDefinition beanDefinition = (BeanDefinition) definition;
                processBean(beanDefinition);
            }
        }
    }

    private void processBean(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof ProtocolDefinition) {
            beanDefinition.getImports().add(Protocol.class.getName());
        } else {
            beanDefinition.getImports().add(Bean.class.getName());
        }
        beanDefinition.getImports().add("java.io.IOException");
        beanDefinition.getImports().add(ReadableStream.class.getName());
        beanDefinition.getImports().add(WritableStream.class.getName());
        for (FieldDefinition fieldDefinition : beanDefinition.getFields()) {
            processField(fieldDefinition);
        }
    }

    private void processField(FieldDefinition fieldDefinition) {
        BeanDefinition beanDefinition = fieldDefinition.getBeanDefinition();
        String type = fieldDefinition.getType();
        if (FieldDefinition.BUILT_IN_TYPES.contains(type)) {
            if (type.equals("bool")) {
                fieldDefinition.setBasicType("boolean");
                fieldDefinition.setClassType("Boolean");
            } else if (type.equals("int")) {
                fieldDefinition.setBasicType(type);
                fieldDefinition.setClassType("Integer");
            } else if (type.equals("bytes")) {
                fieldDefinition.setBasicType("byte[]");
                fieldDefinition.setClassType("byte[]");
                beanDefinition.getImports().add("java.util.Arrays");
            } else if (type.equals("string")) {
                fieldDefinition.setBasicType(type.substring(0, 1).toUpperCase() + type.substring(1));
                fieldDefinition.setClassType(type.substring(0, 1).toUpperCase() + type.substring(1));
            } else if (type.equals("set")) {
                fieldDefinition.setBasicType("HashSet");
                fieldDefinition.setClassType("HashSet");
            } else if (type.equals("list")) {
                fieldDefinition.setBasicType("ArrayList");
                fieldDefinition.setClassType("ArrayList");
            } else if (type.equals("map")) {
                fieldDefinition.setBasicType("HashMap");
                fieldDefinition.setClassType("HashMap");
            } else {
                fieldDefinition.setBasicType(type);
                fieldDefinition.setClassType(type.substring(0, 1).toUpperCase() + type.substring(1));
            }
        } else {
            if (type.contains(".")) {
                beanDefinition.getImports().add(type);
                fieldDefinition.setType(type.substring(type.lastIndexOf(".")+1));
            }
        }

        if (type.equals("set") || type.equals("list") || type.equals("map")) {
            if (type.equals("map")) {
                beanDefinition.getImports().add("java.util.HashMap");
                String keyType = fieldDefinition.getKeyType();
                if (keyType.equals("int")) {
                    fieldDefinition.setBasicKeyType(keyType);
                    fieldDefinition.setClassKeyType("Integer");
                } else {
                    fieldDefinition.setBasicKeyType(keyType);
                    fieldDefinition.setClassKeyType(keyType.substring(0, 1).toUpperCase() + keyType.substring(1));
                }
            } else if (type.equals("set")) {
                beanDefinition.getImports().add("java.util.HashSet");
            } else {
                beanDefinition.getImports().add("java.util.ArrayList");
            }
            String valueType = fieldDefinition.getValueType();
            if (FieldDefinition.BUILT_IN_TYPES.contains(valueType)) {
                if (valueType.equals("int")) {
                    fieldDefinition.setBasicValueType(valueType);
                    fieldDefinition.setClassValueType("Integer");
                } else if (type.equals("bytes")) {
                    fieldDefinition.setBasicValueType("byte[]");
                    fieldDefinition.setBasicValueType("byte[]");
                } else {
                    fieldDefinition.setBasicValueType(valueType);
                    fieldDefinition.setBasicValueType(valueType.substring(0, 1).toUpperCase() + valueType.substring(1));
                }
            }
        }
    }
}
