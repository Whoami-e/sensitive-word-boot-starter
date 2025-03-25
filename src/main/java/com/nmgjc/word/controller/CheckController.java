package com.nmgjc.word.controller;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.result.WordResultHandlers;
import com.github.houbb.sensitive.word.support.result.WordTagsDto;
import com.nmgjc.word.domain.HttpResult;
import com.nmgjc.word.domain.SwSensitiveWordDtlLog;
import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.manager.SwAsyncManager;
import com.nmgjc.word.manager.factory.SwAsyncFactory;
import com.nmgjc.word.utils.DateUtils;
import com.nmgjc.word.utils.FileContentReader;
import com.nmgjc.word.utils.FileTypeConfig;
import com.nmgjc.word.utils.ip.IpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ Author：enrl
 * @ Date：2025-03-20-09:32
 * @ Version：1.0
 * @ Description：
 */
@RestController
@RequestMapping("/sensitiveWordCheck")
public class CheckController {

    public static final Logger log = LoggerFactory.getLogger(CheckController.class);

    @Resource
    private SensitiveWordBs sensitiveWordBs;

    @PostMapping("/sensitive")
    public HttpResult checkSensitive(@RequestBody String content){
        log.info("请求内容：{}", content);
        List<SwSensitiveWordDtlLog> dtlLogs = new ArrayList<>();
        List<WordTagsDto> all = sensitiveWordBs.findAll(content, WordResultHandlers.wordTags());
        if(CollectionUtils.isNotEmpty(all)) {
            for (WordTagsDto bodyWordTag : all) {
                SwSensitiveWordDtlLog swSensitiveWordDtlLog = new SwSensitiveWordDtlLog();
                swSensitiveWordDtlLog.setBusiYear(DateUtils.getYear());
                Set<String> tags = bodyWordTag.getTags();
                if(CollectionUtils.isNotEmpty(tags)) {
                    String next = tags.iterator().next();
                    swSensitiveWordDtlLog.setWordId(Long.parseLong(next.split("\\|")[1]));
                } else {
                    swSensitiveWordDtlLog.setWordId(0L);
                }
                swSensitiveWordDtlLog.setFileContent(content);
                swSensitiveWordDtlLog.setTriggerLocation(1L);
                swSensitiveWordDtlLog.setTriggerCnt(1L);
                dtlLogs.add(swSensitiveWordDtlLog);
            }
            recordLog("/sensitiveWordCheck/sensitive", content, "请求内容监测到敏感词", dtlLogs, "请求内容监测");
        }
        return HttpResult.success(processList(all));
    }

    @PostMapping("/file")
    public HttpResult checkFile(@RequestParam("files") MultipartFile[] files){
        log.info("文件数量：{}, 文件：{}", files.length, files);
        if (files.length == 0){
            return HttpResult.error("文件为空");
        }

        String requestBody = "";

        List<WordTagsDto> all = new ArrayList<>();
        List<SwSensitiveWordDtlLog> dtlLogs = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String ext = FileTypeConfig.ALLOWED_TYPES.get(file.getContentType());

                log.info("文件名：{}", file.getOriginalFilename());
                log.info("文件大小：{}", file.getSize());
                log.info("文件类型：{}", file.getContentType());
                log.info("文件后缀：{}", ext);

                requestBody = FileContentReader.readFileContent(ext, file);

                List<WordTagsDto> fileWordTags = sensitiveWordBs.findAll(requestBody, WordResultHandlers.wordTags());
                log.info("文件内容监测到的敏感词：{}", fileWordTags);
                if(CollectionUtils.isNotEmpty(fileWordTags)) {
                    all.addAll(fileWordTags);
                    for (WordTagsDto wordTagsDto : fileWordTags) {
                        SwSensitiveWordDtlLog swSensitiveWordDtlLog = new SwSensitiveWordDtlLog();
                        swSensitiveWordDtlLog.setBusiYear(DateUtils.getYear());
                        Set<String> tags = wordTagsDto.getTags();
                        if(CollectionUtils.isNotEmpty(tags)) {
                            String next = tags.iterator().next();
                            swSensitiveWordDtlLog.setWordId(Long.parseLong(next.split("\\|")[1]));
                        } else {
                            swSensitiveWordDtlLog.setWordId(0L);
                        }
                        swSensitiveWordDtlLog.setTriggerLocation(2L);
                        swSensitiveWordDtlLog.setTriggerCnt(1L);
                        swSensitiveWordDtlLog.setFileName(file.getOriginalFilename());
                        swSensitiveWordDtlLog.setFileExt(ext);
                        swSensitiveWordDtlLog.setFileContent(requestBody);
                        dtlLogs.add(swSensitiveWordDtlLog);
                    }
                }

            }
            if(CollectionUtils.isNotEmpty(all)) {
                recordLog("/sensitiveWordCheck/file", requestBody, "文件内容监测到敏感词", dtlLogs, "文件内容监测");
            }
            log.info("文件内容：{}", requestBody.toString());
        } catch (Exception e) {
            return HttpResult.error("文件读取失败");
        }

        return HttpResult.success(processList(all));
    }

    private static void recordLog(String url,
                                  String requestBody,
                                  String msg,
                                  List<SwSensitiveWordDtlLog> dtlLogs,
                                  String reqName) {
        SwSensitiveWordLog swSensitiveWordLog = new SwSensitiveWordLog();
        swSensitiveWordLog.setBusiYear(DateUtils.getYear());
        swSensitiveWordLog.setReqMethod("POST");
        swSensitiveWordLog.setReqUrl(url);
        swSensitiveWordLog.setReqName(reqName);
        swSensitiveWordLog.setReqBody(requestBody);
        swSensitiveWordLog.setTriggerType(1L);
        swSensitiveWordLog.setIpaddr(IpUtils.getIpAddr());
        swSensitiveWordLog.setMsg(msg);
        swSensitiveWordLog.setCreateTime(DateUtils.getNowDate());
        swSensitiveWordLog.setDtlLogs(dtlLogs);

        SwAsyncManager.me().execute(SwAsyncFactory.recordLog(swSensitiveWordLog));
    }

    private List<WordTagsDto> processList(List<WordTagsDto> list) {
        return list.stream()
                .peek(dto -> {
                    Set<String> newTags = dto.getTags().stream()
                            .map(tag -> tag.split("\\|")[0])
                            .collect(Collectors.toSet());
                    dto.setTags(newTags);
                })
                .collect(Collectors.toList());
    }
}
