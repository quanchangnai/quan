package quan.config.reader;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.*;
import quan.definition.config.ConfigDefinition;

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
            //只解析第一个工作表
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

            //第[tableBodyStartRow]行起是正文
            if (rowNum < tableBodyStartRow) {
                return;
            }

            for (int r = tableBodyStartRow; r <= rowNum; r++) {
                Row row = sheet.getRow(r - 1);
                JSONObject rowJson = null;

                for (int c = 1; c <= columnNames.size(); c++) {
                    String columnValue = dataFormatter.formatCellValue(row.getCell(c - 1)).trim();
                    if (c == 1) {
                        if (columnValue.startsWith("#")) {
                            break;
                        } else {
                            rowJson = new JSONObject(true);
                        }
                    }
                    addColumnToRow(rowJson, columnNames.get(c - 1), columnValue, r, c);
                }

                if (rowJson != null) {
                    jsons.add(rowJson);
                }
            }
        } catch (Exception e) {
            logger.error("读取配置[{}]出错", tableFile.getName(), e);
        }

    }

}
