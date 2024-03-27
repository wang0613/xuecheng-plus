package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.fallback.MediaServiceClientFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 远程调用媒资接口
 */
@FeignClient(value = "media-api", configuration = {MultipartSupportConfig.class},
        fallbackFactory = MediaServiceClientFallbackFactory.class) //配置feign对文件的支持
public interface MediaServiceClient {

    //指定上传类型
    @RequestMapping(value = "/media/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart("filedata") MultipartFile multipartFile, @RequestParam(value = "objectName", required = false) String objectName) throws IOException;


}
