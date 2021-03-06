package cn.iutils.office.utils.excel;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.iutils.common.utils.DateUtils;
import cn.iutils.common.utils.Encodes;
import cn.iutils.common.utils.JStringUtils;
import cn.iutils.common.utils.ReflectUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

import javax.servlet.http.HttpServletResponse;

/**
 * 基于POI实现的Excel工具类
 * 主要用于导出2003的xls表格
 * @author iutils.cn
 */
public class HssfExcelHelper extends ExcelHelper {

    private File file; // 操作文件

    /**
     * 构造方法
     */
    public HssfExcelHelper(){
        super();
    }

    /**
     * 构造方法
     * @param file 文件对象
     */
    public HssfExcelHelper(File file) {
        super();
        this.file = file;
    }

    @Override
    public <T> List<T> readExcel(Class<T> clazz, String[] fieldNames,
                                 int sheetNo, boolean hasTitle) throws Exception {
        List<T> dataModels = new ArrayList<T>();
        // 获取excel工作簿
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = workbook.getSheetAt(sheetNo);
        int start = sheet.getFirstRowNum() + (hasTitle ? 1 : 0); // 如果有标题则从第二行开始
        for (int i = start; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            // 生成实例并通过反射调用setter方法
            T target = clazz.newInstance();
            for (int j = 0; j < fieldNames.length; j++) {
                String fieldName = fieldNames[j];
                if (fieldName == null || UID.equals(fieldName)) {
                    continue; // 过滤serialVersionUID属性
                }
                // 获取excel单元格的内容
                HSSFCell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                String content = cell.getStringCellValue();
                // 如果属性是日期类型则将内容转换成日期对象
                if (isDateType(clazz, fieldName)) {
                    // 如果属性是日期类型则将内容转换成日期对象
                    ReflectUtil.invokeSetter(target, fieldName,
                            DateUtils.parseDate(content));
                } else {
                    Field field = clazz.getDeclaredField(fieldName);
                    ReflectUtil.invokeSetter(target, fieldName,
                            parseValueWithType(content, field.getType()));
                }
            }
            dataModels.add(target);
        }
        return dataModels;
    }

    @Override
    public <T> void writeExcel(Class<T> clazz, List<T> dataModels,
                               String[] fieldNames, String[] titles,String sheetName) throws Exception {
        HSSFWorkbook workbook = null;
        // 检测文件是否存在，如果存在则修改文件，否则创建文件
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            workbook = new HSSFWorkbook(fis);
        } else {
            workbook = new HSSFWorkbook();
        }
        HSSFSheet sheet = workbook.createSheet(sheetName);
        HSSFRow headRow = sheet.createRow(0);
        // 添加表格标题
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = headRow.createCell(i);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(titles[i]);
            // 设置字体加粗
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);
            // 设置自动换行
            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);
            // 设置单元格宽度
            sheet.setColumnWidth(i, titles[i].length() * 1000);
        }
        // 添加表格内容
        for (int i = 0; i < dataModels.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            // 遍历属性列表
            for (int j = 0; j < fieldNames.length; j++) {
                // 通过反射获取属性的值域
                String fieldName = fieldNames[j];
                if (fieldName == null || UID.equals(fieldName)) {
                    continue; // 过滤serialVersionUID属性
                }
                Object result = ReflectUtil.invokeGetterMethod(dataModels.get(i), fieldName);
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(JStringUtils.toString(result));
                // 如果是日期类型则进行格式化处理
                if (isDateType(clazz, fieldName)) {
                    cell.setCellValue(DateUtils.formatDate((Date) result));
                }
            }
        }
        // 将数据写到磁盘上
        FileOutputStream fos = new FileOutputStream(file);
        try {
            workbook.write(new FileOutputStream(file));
        } finally {
            if (fos != null) {
                fos.close(); // 不管是否有异常发生都关闭文件输出流
            }
        }
    }

    @Override
    public <T> void writeExcel(HttpServletResponse response,String fileName, Class<T> clazz, List<T> dataModels, String[] fieldNames, String[] titles,String sheetName) throws Exception {
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
        //输出流
        OutputStream os = response.getOutputStream();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        HSSFRow headRow = sheet.createRow(0);
        // 添加表格标题
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = headRow.createCell(i);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(titles[i]);
            // 设置字体加粗
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);
            // 设置自动换行
            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);
            // 设置单元格宽度
            sheet.setColumnWidth(i, titles[i].length() * 1000);
        }
        // 添加表格内容
        for (int i = 0; i < dataModels.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            // 遍历属性列表
            for (int j = 0; j < fieldNames.length; j++) {
                // 通过反射获取属性的值域
                String fieldName = fieldNames[j];
                if (fieldName == null || UID.equals(fieldName)) {
                    continue; // 过滤serialVersionUID属性
                }
                Object result = ReflectUtil.invokeGetterMethod(dataModels.get(i), fieldName);
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(JStringUtils.toString(result));
                // 如果是日期类型则进行格式化处理
                if (isDateType(clazz, fieldName)) {
                    cell.setCellValue(DateUtils.formatDate((Date) result));
                }
            }
        }
        workbook.write(os);
    }
}