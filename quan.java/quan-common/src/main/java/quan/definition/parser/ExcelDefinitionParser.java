package quan.definition.parser;

import org.apache.poi.ss.usermodel.*;
import quan.definition.Category;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Objects;

/**
 * 基于Excel表格的【定义】解析器
 *
 * @see TableDefinitionParser
 */
public class ExcelDefinitionParser extends TableDefinitionParser {

    private static final DataFormatter dataFormatter = new DataFormatter();

    private String definitionType = "xlsx";

    public ExcelDefinitionParser() {
    }

    public ExcelDefinitionParser(String definitionType) {
        Objects.requireNonNull(definitionType, Category.config.alias() + "的定义文件类型[definitionType]不能为空");
        this.definitionType = definitionType;
    }

    public ExcelDefinitionParser(Collection<String> definitionPaths) {
        setDefinitionPaths(definitionPaths);
    }

    public ExcelDefinitionParser(String definitionType, Collection<String> definitionPaths) {
        this(definitionType);
        setDefinitionPaths(definitionPaths);
    }

    @Override
    public String getDefinitionType() {
        return definitionType;
    }

    @Override
    protected boolean checkFile(File definitionFile) {
        //忽略Excel临时文件
        return super.checkFile(definitionFile) && !definitionFile.getName().startsWith("~$");
    }

    @Override
    protected boolean parseTable(ConfigDefinition configDefinition, File definitionFile) {
        try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(definitionFile.toPath()))) {
            Sheet sheet = workbook.getSheetAt(0);
            configDefinition.setComment(sheet.getSheetName());

            Row row0 = sheet.getRow(0);

            if (row0 == null || sheet.getLastRowNum() < 2) {
                addValidatedError(configDefinition.getValidatedName() + "的定义文件不完整，要求表头第1行是字段名、第2行时字段约束、第3行是字段注释");
                return false;
            }

            int c = 0;

            for (Cell cell : row0) {
                String fieldName = dataFormatter.formatCellValue(cell);
                Row row1 = sheet.getRow(1);
                Row row2 = sheet.getRow(2);
                String constraints = row1 == null ? null : dataFormatter.formatCellValue(row1.getCell(c));
                String comment = row2 == null ? null : dataFormatter.formatCellValue(row2.getCell(c));
                addField(configDefinition, fieldName, constraints, comment);
                c++;
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
