package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.*;
import quan.definition.ConfigDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/8/3.
 */
public class ExcelConfigReader extends ConfigReader {

    private static final DataFormatter dataFormatter = new DataFormatter();

    public ExcelConfigReader(File tableFile, ConfigDefinition configDefinition) {
        super(tableFile, configDefinition);
    }

    @Override
    protected void read() {
        clear();

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(tableFile))) {
            //只解析第一个工作表，其他的忽略
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                return;
            }

            //第一行是表头
            List<String> columnNames = new ArrayList<>();
            Row row1 = sheet.getRow(0);
            for (Cell cell : row1) {
                columnNames.add(dataFormatter.formatCellValue(cell).trim());
            }
            validateColumnNames(columnNames);

            //第[bodyRowNum]行起是正文
            if (rowNum < bodyRowNum) {
                return;
            }

            for (int i = bodyRowNum; i <= rowNum; i++) {
                Row row = sheet.getRow(i - 1);
                JSONObject rowJson = new JSONObject(true);
                for (int j = 1; j <= columnNames.size(); j++) {
                    addColumnToRow(rowJson, columnNames.get(j - 1), dataFormatter.formatCellValue(row.getCell(j - 1)).trim(), i, j);
                }
                jsons.add(rowJson);
            }
        } catch (Exception e) {
            logger.error("读取配置[{}]出错", tableFile.getName(), e);
        }

    }

}
