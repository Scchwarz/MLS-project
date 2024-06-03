package com.edu.zjut.mlsprojectbackend.utils;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class BaiduOCRUtils {
    public static final String APP_ID = "46879792";
    public static final String API_KEY = "TcwaSQYhN9W89PcE3MMsoVBf";
    public static final String SECRET_KEY = "LxyK23DoQC1CPAuROHOQwTbh0BsGNPPx";

    public static String performOCR(AipOcr client, List<BufferedImage> images) throws IOException {
        // 设置识别参数
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG"); // 识别语言类型：中英文混合
        options.put("detect_direction", "true"); // 是否检测图像朝向
        options.put("detect_language", "true");  // 是否检测语言

        // 存储所有图片的文本
        StringBuilder totalText = new StringBuilder();

        for (BufferedImage image : images) {
            // 进行文字识别
            JSONObject res = client.basicGeneral(toBytes(image), options);

            // 处理识别结果
            if (res.has("words_result")) {
                JSONArray wordsResult = res.getJSONArray("words_result");
                for (int i = 0; i < wordsResult.length(); i++) {
                    totalText.append(wordsResult.getJSONObject(i).getString("words"));
                }
            }
        }

        // 删除所有换行符
        return totalText.toString().replaceAll("\n", "");
    }

    // 将 BufferedImage 转换为 byte 数组
    private static byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (outputStream) {
            // 将 BufferedImage 写入 ByteArrayOutputStream
            ImageIO.write(image, "png", outputStream);
        }

        // 获取字节数组
        return outputStream.toByteArray();
    }

    //进行ocr识别
    public static String OCRImages(List<BufferedImage> images) throws IOException {
        // 初始化 AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 输出识别结果
        return performOCR(client, images);
    }
}
