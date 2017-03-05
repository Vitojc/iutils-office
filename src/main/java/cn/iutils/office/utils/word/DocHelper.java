package cn.iutils.office.utils.word;

import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;

/**
 * Doc文档读取工具
 * @author iutils.cn
 */
public class DocHelper {

    /**
     * doc文档读取
     * @param docFile
     * @return
     * @throws Exception
     */
    public static String read(File docFile) throws Exception{
        String result = null;
        FileInputStream in = new FileInputStream(docFile);
        WordExtractor ex = new WordExtractor(in);
        result = ex.getText();
        return result;
    }

    /**
     * doc文档写入
     */
    public static void write(){
        //TODO
    }

}
