package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import quan.generator.config.ConfigDefinition;

import java.io.FileInputStream;
import java.io.IOException;
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

        Workbook workbook;
        try {
            if (tableFile.getName().endsWith(".xls")) {
                workbook = new HSSFWorkbook(new FileInputStream(tableFile));
            } else if (tableFile.getName().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(new FileInputStream(tableFile));
            } else {
                logger.error("配置[{}]格式非法", tableFile.getName());
                return;
            }
        } catch (Exception e) {
            logger.error("读取配置[{}]出错", tableFile.getName(), e);
            return;
        }

        try {
            read(workbook);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    private void read(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        //总行数
        int rowNum = sheet.getPhysicalNumberOfRows();
        if (rowNum < 1) {
            return;
        }

        List<String> columnNames = new ArrayList<>();
        Row row1 = sheet.getRow(0);
        for (Cell cell : row1) {
            columnNames.add(dataFormatter.formatCellValue(cell).trim());
        }
        checkColumns(columnNames);

        //第一行是表头，第二行是注释，第三行起是内容
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
    }

}
