package cn.iutils.office.utils.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

/**
 * PDF读取工具
 * @author iutils.cn
 */
public class PdfHelper {

    /**
     * 读取PDF文件
     * @param pdfFile pdf文件
     * @param startPage 开始页
     * @param endPage 结束页 读取所有赋0
     * @return
     */
    public static String read(File pdfFile,int startPage,int endPage) throws Exception{
        String result = null;//读取结果
        PDDocument document = PDDocument.load(pdfFile);
        // 获取页码
        int pages = document.getNumberOfPages();
        // 读文本内容
        PDFTextStripper stripper=new PDFTextStripper();
        // 设置按顺序输出
        stripper.setSortByPosition(true);
        //最少不能少于第一页
        if(startPage<=1){
            startPage = 1;
        }
        stripper.setStartPage(startPage);
        if(endPage==0){
            stripper.setEndPage(pages);
        }else{
            //不超过最大页
            if(endPage>pages){
                endPage = pages;
            }
            stripper.setEndPage(endPage);
        }
        result = stripper.getText(document);
        return result;
    }

    /**
     * PDF文档写入
     */
    public static void write(){
        //TODO
    }

}
