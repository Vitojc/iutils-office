package cn.iutils.office.utils.word;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;

/**
 * Docx文档读取工具
 * @author iutils.cn
 */
public class DocxHelper {

    /**
     * docx文档读取
     * @param docxFile
     * @return
     * @throws Exception
     */
    public static String read(File docxFile) throws Exception{
        String result = null;
        OPCPackage opcPackage = POIXMLDocument.openPackage(docxFile.getPath());
        POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
        result = extractor.getText();
        return result;
    }

    /**
     * docx文档写入
     */
    public static void write(){
        //TODO
    }

}
