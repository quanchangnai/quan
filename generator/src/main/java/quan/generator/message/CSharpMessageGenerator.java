package quan.generator.message;

import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;
import quan.generator.Language;

import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class CSharpMessageGenerator extends MessageGenerator {

    public CSharpMessageGenerator(List<String> srcPaths, String destPath) throws Exception {
        super(srcPaths, destPath);

        basicTypes.put("bool", "bool");
        basicTypes.put("string", "string");
        basicTypes.put("set", "HashSet");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Dictionary");

        classTypes.put("bool", "bool");
        classTypes.put("short", "short");
        classTypes.put("int", "int");
        classTypes.put("long", "long");
        classTypes.put("float", "float");
        classTypes.put("double", "double");
        classTypes.put("string", "string");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "List");
        classTypes.put("map", "Dictionary");
    }

    public CSharpMessageGenerator(String srcPath, String destPath) throws Exception {
        this(Arrays.asList(srcPath), destPath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.cs;
    }

    protected void processClassSelf(ClassDefinition classDefinition) {
        String packageName = classDefinition.getPackageName();
        //C#命名空间首字母大写
        String newPackageName = "";
        for (int i = 0; i < packageName.length(); i++) {
            String c = String.valueOf(packageName.charAt(i));
            if (i == 0 || packageName.charAt(i - 1) == '.') {
                c = c.toUpperCase();
            }
            newPackageName += c;
        }
        classDefinition.setPackageName(newPackageName);

        super.processClassSelf(classDefinition);
    }

    @Override
    protected String resolveFieldImport(FieldDefinition fieldDefinition, boolean fieldSelf) {
        return fieldSelf ? fieldDefinition.getTypePackage() : fieldDefinition.getValueTypePackage();
    }

    @Override
    protected String resolveClassImport(ClassDefinition classDefinition) {
        return classDefinition.getPackageName();
    }

    public static void main(String[] args) throws Exception {

        String srcPath = "generator\\src\\test\\java\\quan\\generator\\message";
        String destPath = "message";
        String packagePrefix = "MessageCS.test";

        CSharpMessageGenerator messageGenerator = new CSharpMessageGenerator(srcPath, destPath);
        messageGenerator.setPackagePrefix(packagePrefix);
        messageGenerator.generate();

    }
}
