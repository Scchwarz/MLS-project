package com.edu.zjut.mlsprojectbackend.utils;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PDFUtils {
    /**
     * 直接读取pdf文字信息
     * @param pdfFilePath pdf文件路径
     * @return PDF文件内容
     */
    public String readPDFFile(String pdfFilePath) {
        String textContent = "";
        //本地PDF文件路径
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            textContent = stripper.getText(document);
            // 删除所有换行符
            textContent = textContent.replaceAll("\\n", "").replaceAll("\\r", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textContent;
    }

    /**
     * 将PDF转化为图片
     * @param pdfFilePath 文件路径
     * @return PDF文件内容
     */
public List<BufferedImage> extractImagesFromPDF(String pdfFilePath) {
    List<BufferedImage> images = new ArrayList<>();

    try {
        File file = new File(pdfFilePath);
        PDDocument document = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        int numPages = document.getNumberOfPages();
        for (int pageIndex = 0; pageIndex < numPages; pageIndex++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300); // Adjust DPI as needed
            images.add(image);

            // Save the image to the output folder
        }

        document.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

    return images;
}
    public String readPDFFileByImage(String pdfFilePath) throws Exception {
        List<BufferedImage> images = this.extractImagesFromPDF(pdfFilePath);
        return BaiduOCRUtils.OCRImages(images);
    }
}
