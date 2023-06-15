package quan.generator.util;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class CSharpUtils {

    private static Logger logger = LoggerFactory.getLogger(CSharpUtils.class);

    public static void updateProjFile(String codePath, String projFileName, Set<String> addClasses, Set<String> deleteClasses) {
        if (StringUtils.isBlank(projFileName) || CollectionUtils.isEmpty(addClasses) && CollectionUtils.isEmpty(deleteClasses)) {
            return;
        }

        File projFile = new File(projFileName);
        if (!projFile.exists()) {
            logger.error("CS工程文件[{}]不存在", projFileName);
            return;
        }

        Path projPath = projFile.getParentFile().toPath();

        Document document;
        try {
            document = new SAXReader().read(projFile);
        } catch (DocumentException e) {
            logger.error("解析{}出错", projFile, e);
            return;
        }

        //源文件记录在这里
        Element element = null;

        out:
        for (Element element1 : document.getRootElement().elements()) {
            if (element1.getName().equals("ItemGroup")) {
                for (Element element2 : element1.elements()) {
                    if (element2.getName().equals("Compile")) {
                        element = element1;
                        break out;
                    }
                }
            }
        }

        if (element == null) {
            return;
        }

        deleteClasses = deleteClasses.stream().map(c -> resolveInclude(codePath, projPath, c)).collect(Collectors.toSet());
        addClasses = addClasses.stream().map(c -> resolveInclude(codePath, projPath, c)).collect(Collectors.toSet());

        for (Element element2 : element.elements()) {
            String include = element2.attributeValue("Include");
            addClasses.remove(include);
            if (deleteClasses.contains(include)) {
                element.remove(element2);
            }
        }

        for (String addClass : addClasses) {
            element.addElement("Compile").add(new DOMAttribute(QName.get("Include"), addClass));
        }

        try (OutputStream outputStream = Files.newOutputStream(projFile.toPath())) {
            OutputFormat outputFormat = new OutputFormat("  ", true, document.getXMLEncoding());
            outputFormat.setTrimText(true);
            outputFormat.setNewLineAfterDeclaration(false);
            XMLWriter xmlWriter = new XMLWriter(outputStream, outputFormat) {
                protected void writeEmptyElementClose(String qualifiedName) throws IOException {
                    writer.write(" />");
                }
            };
            xmlWriter.write(document);
        } catch (IOException e) {
            logger.error("更新{}出错", projFile, e);
        }
    }

    private static String resolveInclude(String codePath, Path projPath, String classFileFullName) {
        File classFile = new File(codePath, classFileFullName.replace(".", "\\") + ".cs");
        return projPath.relativize(classFile.toPath()).toString();
    }

}
