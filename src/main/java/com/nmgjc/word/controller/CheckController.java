package com.nmgjc.word.controller;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.result.WordResultHandlers;
import com.github.houbb.sensitive.word.support.result.WordTagsDto;
import com.nmgjc.word.domain.HttpResult;
import com.nmgjc.word.utils.FileContentReader;
import com.nmgjc.word.utils.FileTypeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ Author：enrl
 * @ Date：2025-03-20-09:32
 * @ Version：1.0
 * @ Description：
 */
//@RestController
//@RequestMapping("/sensitiveWordCheck")
public class CheckController {

    public static final Logger log = LoggerFactory.getLogger(CheckController.class);

    @Resource
    private SensitiveWordBs sensitiveWordBs;

    @PostMapping("/sensitive")
    public HttpResult checkSensitive(@RequestBody String content){
        log.info("请求内容：{}", content);
        List<WordTagsDto> all = sensitiveWordBs.findAll(content, WordResultHandlers.wordTags());
        return HttpResult.success(all);
    }

    @PostMapping("/file")
    public HttpResult checkFile(@RequestParam("files") MultipartFile[] files){
        log.info("文件数量：{}, 文件：{}", files.length, files);
        if (files.length == 0){
            return HttpResult.error("文件为空");
        }

        StringBuilder requestBody = new StringBuilder();
        try {
            for (MultipartFile file : files) {
                String ext = FileTypeConfig.ALLOWED_TYPES.get(file.getContentType());

                log.info("文件名：{}", file.getOriginalFilename());
                log.info("文件大小：{}", file.getSize());
                log.info("文件类型：{}", file.getContentType());
                log.info("文件后缀：{}", ext);

                requestBody.append(FileContentReader.readFileContent(ext, file));

            }
            log.info("文件内容：{}", requestBody.toString());
        } catch (Exception e) {
            return HttpResult.error("文件读取失败");
        }

        List<WordTagsDto> all = sensitiveWordBs.findAll(requestBody.toString(), WordResultHandlers.wordTags());

        return HttpResult.success(all);
    }
}
