package com.fccfc.framework.web.utils.excel;

import com.fccfc.framework.db.core.utils.PagerList;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by 14080554 on 2015/5/7 0007.
 */
public class ExcelUtil {

    public static final String TYPE_XLS = "xls";

    public static final String TYPE_XLSX = "xlsx";

    public static final int SHEET_MAX_DATA_SIZE = 20000;

    public static final int IMPORT_MAX_DATA_SIZE = 5000;

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static void importExcel(String excelType, File file, ExcelImportCallback callback) {

        if (!(StringUtils.equals(TYPE_XLS, excelType) || StringUtils.equals(TYPE_XLSX, excelType))) {
            logger.info("Does not support data format, support.Xls,.Xlsx.");
            return;
        }

        if (null == file) {
            logger.info("Need to import the file as null.");
            return;
        }

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
            Workbook wb = StringUtils.equals(TYPE_XLS, excelType) ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
            if (null != wb) {
                Sheet sheet1 = wb.getSheetAt(0);

                boolean hasNext = false;

                for (Row row : sheet1) {
                    int rowNum = row.getRowNum();
                    //不处理表头
                    if (rowNum > 0) {
                        callback.invokeDataHandler(row);

                        if (!hasNext) {
                            hasNext = true;
                        }
                    }
                    //每5000条处理一次 【入库且将集合清空】
                    if (rowNum % IMPORT_MAX_DATA_SIZE == 0) {
                        hasNext = false;
                        callback.invokeInsertHandler();
                    }
                }
                // 不足5000条 或 5000取余不等0的；
                if (hasNext) {
                    callback.invokeInsertHandler();
                }
            }
        }
        catch (FileNotFoundException e) {
            logger.error("Import the file does not exist.", e);
        }
        catch (IOException e) {
            logger.error("Object [HSSFWorkbook or XSSFWorkbook] initialization error.", e);
        }
        finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    logger.error("Close the input stream error.", e);
                }
            }
        }
    }

    public static String getExcelType(String path) {
        return StringUtils.isNotEmpty(path) ? path.substring(path.lastIndexOf(".") + 1) : "";
    }

    public static String encoderFileName(String fileName) {
        String filename = null;
        try {
            filename = URLEncoder.encode(fileName, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public static <T> void exportExcel(OutputStream output, ExcelExportDto dto) {
        HSSFWorkbook workbook = new HSSFWorkbook();// 创建一个Excel文件

        int countDataSize = dto.getCountDataSize();
        if (countDataSize != 0 && CollectionUtils.isEmpty(dto.getDataList())) {

            PagerList pager = new PagerList();
            pager.setPageSize(SHEET_MAX_DATA_SIZE);
            pager.setTotalCount(countDataSize);

            for (int i = 1; i <= pager.getTotalPage(); i++) {
                dto.setDataList(dto.getGetDataCallback().invoke(i, SHEET_MAX_DATA_SIZE));
                fillWorkbook(workbook, "Sheet" + i, dto);
            }
        }
        else {
            fillWorkbook(workbook, "Sheet1", dto);
        }

        try {
            workbook.write(output);
            workbook.cloneSheet(0);
            output.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (null != output) {
                try {
                    output.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void fillWorkbook(HSSFWorkbook workbook, String sheetName, ExcelExportDto dto) {
        HSSFSheet sheet = workbook.createSheet(sheetName);// 创建一个Excel的Sheet
        setSheet(sheet, dto.getFields(), dto.getHeaderColumnWidths());
        setSheetHeader(workbook, sheet, dto.getFields(), dto.getFieldsHeader());
        setSheetDataLine(sheet, dto.getFields(), dto.getDataList(), dto.getConverterMap());
    }

    private static <T> void setSheetDataLine(HSSFSheet sheet, String[] fields, List<T> dataList, Map<String, Converter> converterMap) {
        int rows = 1;
        HSSFRow row = null;
        HSSFCell cell = null;
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (T t : dataList) {
                row = sheet.createRow(rows++);
                for (int i = 0; i < fields.length; i++) {
                    cell = row.createCell(i);
                    String attr = fields[i];
                    String value = getValue(t, attr);

                    Converter converter = null;
                    if (null != converterMap && null != (converter = converterMap.get(attr))) {
                        value = converter.invoke(value);
                    }
                    cell.setCellValue(value);
                }
            }
        }
    }

    private static String getValue(Object obj, String attr) {
        try {
            return BeanUtils.getProperty(obj, attr);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setSheetHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] fields, Map<String, String> headerMap) {
        HSSFRow row0 = sheet.createRow(0);
        HSSFCellStyle columnHeadStyle = createHeaderCellStyle(workbook);

        HSSFCell cell = null;
        for (int i = 0; i < fields.length; i++) {
            cell = row0.createCell(i);
            cell.setCellValue(new HSSFRichTextString(headerMap.get(fields[i])));
            cell.setCellStyle(columnHeadStyle);
        }
    }

    private static HSSFCellStyle createHeaderCellStyle(HSSFWorkbook workbook) {
        HSSFFont columnHeadFont = workbook.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle columnHeadStyle = workbook.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(true);
        columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        columnHeadStyle.setBorderLeft((short) 1);// 边框的大小
        columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        columnHeadStyle.setBorderRight((short) 1);// 边框的大小
        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色
        // 设置单元格的背景颜色（单元格的样式会覆盖列或行的样式）
        columnHeadStyle.setFillForegroundColor(HSSFColor.WHITE.index);

        return columnHeadStyle;
    }

    private static void setSheet(HSSFSheet sheet, String[] fields, Map<String, Integer> columnWidthMap) {
        sheet.createFreezePane(0, 1);// 冻结

        if (MapUtils.isNotEmpty(columnWidthMap)) {
            for (int i = 0; i < fields.length; i++) {
                Integer width = columnWidthMap.get(fields[i]);
                if (null != width && width > 0) {
                    sheet.setColumnWidth(i, width);
                }
            }
        }
    }
}
