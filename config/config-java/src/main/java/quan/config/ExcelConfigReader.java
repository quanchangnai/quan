package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import quan.generator.config.ConfigDefinition;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/8/3.
 */
public class ExcelConfigReader extends ConfigReader {

    public ExcelConfigReader(String tablePath, String table, ConfigDefinition configDefinition) {
        super(tablePath, table, configDefinition);
    }

    @Override
    protected void read() {
        clear();

        Workbook workbook;
        try {
            if (tableFile.getName().endsWith("xls")) {
                workbook = new HSSFWorkbook(new FileInputStream(tableFile));
            } else if (tableFile.getName().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(new FileInputStream(tableFile));
            } else {
                errors.add(String.format("配置[%s]格式非法", table));
                return;
            }
        } catch (Exception e) {
            String error = String.format("读取配置[%s]出错:%s", table, e.getMessage());
            errors.add(error);
            logger.debug(error, e);
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
        if (workbook.getNumberOfSheets() < 1) {
            errors.add(String.format("配置[%s]至少要有一个工作表", table));
            return;
        }

        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        if (rowNum < 1) {
            return;
        }

        List<String> columnNames = new ArrayList<>();
        Row row1 = sheet.getRow(0);
        for (Cell cell : row1) {
            columnNames.add(getCellValue(cell));
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
                addColumnToRow(rowJson, columnNames.get(j), getCellValue(row.getCell(j)), i + 1, j + 1);
            }
            jsons.add(rowJson);
        }
    }

    protected String getCellValue(Cell cell) {
        String cellValue = "";
        switch (cell.getCellType()) {
            case NUMERIC: // 数字
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cellValue = sdf.format(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue()));
                } else {
                    DataFormatter dataFormatter = new DataFormatter();
                    cellValue = dataFormatter.formatCellValue(cell);
                }
                break;
            case STRING: // 字符串
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN: // Boolean
                cellValue = cell.getBooleanCellValue() + "";
                break;
            case FORMULA: // 公式
                cellValue = cell.getCellFormula() + "";
                break;
        }
        return cellValue.trim();

    }
}
