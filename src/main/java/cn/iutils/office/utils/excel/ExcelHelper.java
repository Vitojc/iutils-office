package cn.iutils.office.utils.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Excel工具抽象类
 * @author iutils.cn
 */
public abstract class ExcelHelper {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 对象序列化版本号名称
     */
    public static final String UID = "serialVersionUID";

    /**
     * 将指定excel文件中的数据转换成数据列表
     *
     * @param clazz    数据类型
     * @param sheetNo  工作表编号
     * @param hasTitle 是否带有标题
     * @return 返回转换后的数据列表
     * @throws Exception
     */
    public <T> List<T> readExcel(Class<T> clazz, int sheetNo, boolean hasTitle)
            throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            fieldNames[i] = fieldName;
        }
        return readExcel(clazz, fieldNames, sheetNo, hasTitle);
    }

    /**
     * 将指定excel文件中的数据转换成数据列表
     *
     * @param clazz      数据类型
     * @param fieldNames 属性列表
     * @param hasTitle   是否带有标题
     * @return 返回转换后的数据列表
     * @throws Exception
     */
    public <T> List<T> readExcel(Class<T> clazz, String[] fieldNames,
                                 boolean hasTitle) throws Exception {
        return readExcel(clazz, fieldNames, 0, hasTitle);
    }

    /**
     * 抽象方法：将指定excel文件中的数据转换成数据列表，由子类实现
     *
     * @param clazz      数据类型
     * @param fieldNames 属性列表
     * @param sheetNo    工作表编号
     * @param hasTitle   是否带有标题
     * @return 返回转换后的数据列表
     * @throws Exception
     */
    public abstract <T> List<T> readExcel(Class<T> clazz, String[] fieldNames,
                                          int sheetNo, boolean hasTitle) throws Exception;

    /**
     * 写入数据到指定excel文件中
     * @param clazz      数据类型
     * @param dataModels 数据列表
     * @param sheetName sheet名称
     * @throws Exception
     */
    public <T> void writeExcel(Class<T> clazz, List<T> dataModels,String sheetName)
            throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            fieldNames[i] = fieldName;
        }
        writeExcel(clazz, dataModels, fieldNames, fieldNames,sheetName);
    }

    /**
     * 导出到web
     * @param clazz      数据类型
     * @param dataModels 数据列表
     * @param sheetName sheet名称
     * @throws Exception
     */
    public <T> void writeExcel(HttpServletResponse response,String fileName,Class<T> clazz, List<T> dataModels,String sheetName)
            throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            fieldNames[i] = fieldName;
        }
        writeExcel(response,fileName,clazz, dataModels, fieldNames, fieldNames,sheetName);
    }

    /**
     * 写入数据到指定excel文件中
     * @param clazz      数据类型
     * @param dataModels 数据列表
     * @param fieldNames 属性列表
     * @param sheetName sheet名称
     * @throws Exception
     */
    public <T> void writeExcel(Class<T> clazz, List<T> dataModels,
                               String[] fieldNames,String sheetName) throws Exception {
        writeExcel(clazz, dataModels, fieldNames, sheetName);
    }

    /**
     * 抽象方法：写入数据到指定excel文件中，由子类实现
     * @param clazz      数据类型
     * @param dataModels 数据列表
     * @param fieldNames 属性列表
     * @param titles     标题列表
     * @param sheetName sheet名称
     * @throws Exception
     */
    public abstract <T> void writeExcel(Class<T> clazz, List<T> dataModels,
                                        String[] fieldNames, String[] titles,String sheetName) throws Exception;

    /**
     * 抽象方法：写入数据到指定excel文件中并导出到web，由子类实现
     * @param response   浏览器响应对象
     * @param fileName   文件名
     * @param clazz      数据类型
     * @param dataModels 数据列表
     * @param fieldNames 属性列表
     * @param titles     标题列表
     * @param sheetName sheet名称
     * @throws Exception
     */
    public abstract <T> void writeExcel(HttpServletResponse response,String fileName,Class<T> clazz, List<T> dataModels,
                                        String[] fieldNames, String[] titles,String sheetName) throws Exception;

    /**
     * 判断属性是否为日期类型
     *
     * @param clazz     数据类型
     * @param fieldName 属性名
     * @return 如果为日期类型返回true，否则返回false
     */
    protected <T> boolean isDateType(Class<T> clazz, String fieldName) {
        boolean flag = false;
        try {
            Field field = clazz.getDeclaredField(fieldName);
            //判断是否基础类型
            if(field.getType().isPrimitive()){
                flag = false;
            }else{
                Object typeObj = field.getType().newInstance();
                flag = typeObj instanceof Date;
            }
        } catch (Exception e) {
            logger.error("判断是否日期字段",e.fillInStackTrace());
        }
        return flag;
    }

    /**
     * 根据类型将指定参数转换成对应的类型
     *
     * @param value 指定参数
     * @param type  指定类型
     * @return 返回类型转换后的对象
     */
    protected <T> Object parseValueWithType(String value, Class<?> type) {
        Object result = null;
        try {
            // 根据属性的类型将内容转换成对应的类型
            if (Boolean.TYPE == type) {
                result = Boolean.parseBoolean(value);
            } else if (Byte.TYPE == type) {
                result = Byte.parseByte(value);
            } else if (Short.TYPE == type) {
                result = Short.parseShort(value);
            } else if (Integer.TYPE == type) {
                result = Integer.parseInt(value);
            } else if (Long.TYPE == type) {
                result = Long.parseLong(value);
            } else if (Float.TYPE == type) {
                result = Float.parseFloat(value);
            } else if (Double.TYPE == type) {
                result = Double.parseDouble(value);
            } else {
                result = (Object) value;
            }
        } catch (Exception e) {
            logger.error("类型转化失败",e.fillInStackTrace());
        }
        return result;
    }

}