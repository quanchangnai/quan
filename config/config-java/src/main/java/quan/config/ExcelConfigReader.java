package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.*;
import quan.generator.config.ConfigDefinition;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/8/3.
 */
public class ExcelConfigReader extends ConfigReader {

    private DataFormatter dataFormatter = new DataFormatter();

    public ExcelConfigReader(String tablePath, String table, ConfigDefinition configDefinition) {
        super(tablePath, table, configDefinition);
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

            //第一行是表头，第二行是注释，第三行起是内容
            List<String> columnNames = new ArrayList<>();
            Row row1 = sheet.getRow(0);
            for (Cell cell : row1) {
                columnNames.add(dataFormatter.formatCellValue(cell).trim());
            }
            validateColumnNames(columnNames);

            if (rowNum <= 2) {
                return;
            }

            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowJson = new JSONObject(true);
                for (int j = 0; j < columnNames.size(); j++) {
                    addColumnToRow(rowJson, columnNames.get(j), dataFormatter.formatCellValue(row.getCell(j)).trim(), i + 1, j + 1);
                }
                jsons.add(rowJson);
            }
        } catch (Exception e) {
            logger.error("读取配置[{}]出错", tableFile.getName(), e);
        }

    }

}
