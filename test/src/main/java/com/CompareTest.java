package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class CompareTest {

    private static final Logger logger = LoggerFactory.getLogger(CompareTest.class);

    RestTemplate restTemplate = new RestTemplate();

    private String apiKey = "YvdSaxnBZSQJmQFWGqRlkC-RX9eouB8t";
    private String apiSecret = "whu70wGBOShCNRQxlZ1VtBx-HqdAltdN";
    private String url = "https://api-cn.faceplusplus.com/facepp/v3/compare";

    public static void main(String[] args) throws IOException {
        CompareTest compareTest = new CompareTest();
        compareTest.compare();
//        File src = new File("e:/x.jpg"), target = new File("e:/x.compress.jpg");
//        compareTest.compressPhoto(src, target);
    }

    private void compare() throws IOException {

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        params.add("api_key", apiKey);
        params.add("api_secret", apiSecret);
        params.add("image_base64_1", getBase64Image(new FileInputStream("e:/2c9fc0d0710ccecb0171b059900101d6.jpg")));
        params.add("image_base64_2", getBase64Image(new FileInputStream("e:/2c9fc0d0710ccecb0171b059900101d6.jpg")));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("status code : {}", responseEntity.getStatusCodeValue());
        logger.info(responseEntity.getBody());
    }

    private String getBase64Image(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        int srcWidth = image.getWidth(null);//得到文件原始宽度
        int srcHeight = image.getHeight(null);//得到文件原始高度
        int newWidth = 960;
        if (srcWidth > newWidth) {
            double scale_w = (double) newWidth / srcWidth;
            int newHeight = (int) (srcHeight * scale_w);
            BufferedImage newImage = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_INT_RGB);
            newImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight,
                    Image.SCALE_SMOOTH), 0, 0, null);
            image = newImage;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        inputStream.close();
        return Base64.getEncoder().encodeToString(bytes);
    }

    private void compressPhoto(File src, File target) throws IOException {
        FileInputStream fis = new FileInputStream(src);
        String file2Base64 = getBase64Image(fis);
        Files.write(Paths.get(target.getPath()), Base64.getDecoder().decode(file2Base64), StandardOpenOption.CREATE);
    }
}
