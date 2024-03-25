package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


/**
 * 大文件上传接口
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {

    @Autowired
    private MediaFileService mediaFileService;

    /**
     * 文件上传前检查文件
     *
     * @param fileMd5 文件名称
     * @return RestResponse
     * @throws Exception
     */
    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(
            @RequestParam("fileMd5") String fileMd5) throws Exception {


        return mediaFileService.checkFile(fileMd5);
    }

    /**
     * 分块文件上传前的检测
     *
     * @param fileMd5    文件的名称
     * @param chunkIndex 分块的索引
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunkIndex) throws Exception {
        return mediaFileService.checkChunk(fileMd5, chunkIndex);
    }

    /**
     * 有前端进行分块，上传分块数据
     *
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        //1：创建一个临时文件
        File temp = File.createTempFile("minio", ".temp");
        //2：将临时文件转成流 记性存储
        file.transferTo(temp);

        //文件路径
        String localPath = temp.getAbsolutePath();

        return mediaFileService.uploadChunk(fileMd5, chunk, localPath);
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {

        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);

        return mediaFileService.mergeChunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);

    }


}
