package com.xuecheng.content.feignclient.fallback;

import com.xuecheng.content.feignclient.MediaServiceClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//发生熔断时 调用此降级方法
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            @Override
            public String upload(MultipartFile multipartFile, String objectName) throws IOException {
                //降级方法
                log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}", throwable.toString(), throwable);
                return null;
            }
        };
    }
}
