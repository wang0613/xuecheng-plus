package com.xuecheng.content;

import com.xuecheng.ContentApplication;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class FeignTest {

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Test
    void testUpload() throws IOException {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\data\\1.html"));

        String upload = mediaServiceClient.upload(multipartFile, "course/1.html");
        if(upload == null){
            return;
        }
    }

}
