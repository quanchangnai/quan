package quan.definition.parser;

import org.apache.poi.ss.usermodel.*;
import quan.definition.Category;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

/**
 * 基于Excel表格的定义文件解析器，在表格中直接定义配置，不支持定义复杂结构
 */
public class ExcelDefinitionParser extends TableDefinitionParser {

    private static final DataFormatter dataFormatter = new DataFormatter();

    private String definitionType;

    public ExcelDefinitionParser() {
    }

    public ExcelDefinitionParser(String definitionType) {
        Objects.requireNonNull(definitionType, Category.config.alias() + "的定义文件类型[definitionType]不能为空");
        this.definitionType = definitionType;
    }

    @Override
    public String getDefinitionType() {
        return definitionType;
    }

    @Override
    protected void parseFile(File definitionFile) {
        //忽略Excel临时文件
        if (!definitionFile.getName().startsWith("~$")) {
            super.parseFile(definitionFile);
        }
    }

    @Override
    protected boolean parseTable(ConfigDefinition configDefinition, File definitionFile) {
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(definitionFile))) {
            Sheet sheet = workbook.getSheetAt(0);
            configDefinition.setComment(sheet.getSheetName());
            int totalTowNum = sheet.getPhysicalNumberOfRows();
            if (totalTowNum < 3) {
                addValidatedError(configDefinition.getValidatedName() + "的定义文件不完整，表头要求第1行列名、第2行字段名、第3行字段约束");
                return false;
            }

            int c = 0;
            for (Cell cell : sheet.getRow(0)) {
                String columnName = dataFormatter.formatCellValue(cell).trim();
                String fieldName = dataFormatter.formatCellValue(sheet.getRow(1).getCell(c)).trim();
                String constraint = dataFormatter.formatCellValue(sheet.getRow(2).getCell(c)).trim();
                addField(configDefinition, columnName, fieldName, constraint);
                c++;
            }
            return true;
        } catch (Exception e) {
            logger.error("解析定义文件[{}]错误", definitionFile.getName(), e);
            return false;
        }
    }
}
