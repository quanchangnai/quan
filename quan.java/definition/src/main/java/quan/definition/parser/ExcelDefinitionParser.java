package quan.definition.parser;

import org.apache.poi.ss.usermodel.*;
import quan.definition.Category;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

/**
 * 在Excel表格中直接定义配置，不支持定义复杂结构
 */
public class ExcelDefinitionParser extends DefinitionParser {

    private static final DataFormatter dataFormatter = new DataFormatter();

    private String definitionType;

    public ExcelDefinitionParser() {
    }

    public ExcelDefinitionParser(String definitionType) {
        Objects.requireNonNull(definitionType, Category.config.alias() + "的定义文件类型[definitionType]不能为空");
        this.definitionType = definitionType;
    }

    @Override
    protected String definitionFileType() {
        return definitionType;
    }

    public void setDefinitionType(String definitionType) {
        this.definitionType = definitionType;
    }

    @Override
    protected void parseClasses(File definitionFile) {
        String name = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("."));
        ConfigDefinition configDefinition = new ConfigDefinition(name, null);
        configDefinition.setParser(this);
        configDefinition.setDefinitionFile(definitionFile.getName());
        configDefinition.setName(name);
        parsedClasses.add(configDefinition);

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(definitionFile))) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalTowNum = sheet.getPhysicalNumberOfRows();
            if (totalTowNum < 3) {
                return;
            }

            int c = 0;
            for (Cell cell : sheet.getRow(0)) {
                String columnName = dataFormatter.formatCellValue(cell).trim();
                String fieldName = dataFormatter.formatCellValue(sheet.getRow(1).getCell(c)).trim();
                String fieldType = dataFormatter.formatCellValue(sheet.getRow(2).getCell(c)).trim();

                FieldDefinition fieldDefinition = new FieldDefinition();
                fieldDefinition.setParser(this);
                fieldDefinition.setCategory(getCategory());
                fieldDefinition.setName(fieldName);
                fieldDefinition.setTypes(fieldType);
                fieldDefinition.setColumn(columnName);

                configDefinition.addField(fieldDefinition);

                c++;
            }
        } catch (Exception e) {
            logger.error("解析定义文件[{}]错误", definitionFile.getName(), e);
        }
    }

}
