package com.xuecheng.content;

import com.alibaba.nacos.common.utils.IoUtils;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class FreemarkerTest {

    @Autowired
    CoursePublishService coursePublishService;


    @Test
    void testGenerateHtml() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());

        //1：设置模版路径
        String classpath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        //2:设置编码
        configuration.setDefaultEncoding("utf-8");

        //得到模版
        Template template = configuration.getTemplate("course_template.ftl");
//        准备数据
        CoursePreviewDto coursePreviewDto = coursePublishService.preView(117L);
        Map<String,Object> map = new HashMap<>();
        map.put("model", coursePreviewDto);

        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        //将文件转成流， 准备输出文件 并将流写入输出文件
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\data\\117.html"));
        IOUtils.copy(inputStream, outputStream);
    }
}
