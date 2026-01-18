package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    // 读取配置文件中定义的本地路径
    @Value("${sky.path}")
    private String basePath;

    /**
     * 文件上传
     * @param files 上传的文件
     * @return 文件的访问路径
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<List<String>> upload(@RequestParam("file") MultipartFile[] files) {
        log.info("文件上传");

        List<String> filePathList = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                // 1. 获取原始文件名
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null) {
                    // 2. 截取后缀 (例如 .png)
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // 3. 构造新文件名 (UUID + 后缀)
                String objectName = UUID.randomUUID() + extension;

                // 4. 创建目录对象
                File dir = new File(basePath);
                // 如果目录不存在，则创建
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // 5. 将文件保存到本地
                file.transferTo(new File(basePath + objectName));

                // 6. 返回访问路径
                String filePath = "http://localhost:8080/images/" + objectName;
                filePathList.add(filePath);
            }
            return Result.success(filePathList);
        } catch (IOException e) {
            log.error("文件上传失败", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
