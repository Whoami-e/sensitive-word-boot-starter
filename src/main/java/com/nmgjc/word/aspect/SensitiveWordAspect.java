package com.nmgjc.word.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.nmgjc.word.annotation.SensitiveWord;
import com.nmgjc.word.constant.HttpStatus;
import com.nmgjc.word.domain.Result;
import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.manager.SwAsyncManager;
import com.nmgjc.word.manager.factory.SwAsyncFactory;
import com.nmgjc.word.utils.CommonUtil;
import com.nmgjc.word.utils.DateUtils;
import com.nmgjc.word.utils.FileContentReader;
import com.nmgjc.word.utils.FileTypeConfig;
import com.nmgjc.word.utils.ip.IpUtils;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.result.WordResultHandlers;
import com.github.houbb.sensitive.word.support.result.WordTagsDto;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ Author：enrl
 * @ Date：2025-03-17-17:28
 * @ Version：1.0
 * @ Description：敏感词切面
 */
@Aspect
@Component
public class SensitiveWordAspect {

    public static final Logger log = LoggerFactory.getLogger(SensitiveWordAspect.class);

    private final SensitiveWordBs sensitiveWordBs;

    public SensitiveWordAspect(SensitiveWordBs sensitiveWordBs) {
        this.sensitiveWordBs = sensitiveWordBs;
    }

    @Around("@annotation(sensitiveWord)")
    public Object doBefore(ProceedingJoinPoint point, SensitiveWord sensitiveWord) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();

        String header = request.getHeader("X-Ignore-Sensitive-Check");
        log.info("是否二次拦截：{}", header);
        if ("true".equals(header)) {
            return point.proceed();
        }

        if (HttpMethod.POST.name().equals(request.getMethod()) || HttpMethod.PUT.name().equals(request.getMethod())) {
            //  判断是否包含文件
            boolean isFileUpload = false;
            // 1. 检查 Content-Type
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("multipart/")) {
                isFileUpload = true;
            }

            Object[] args = point.getArgs();
            for (Object arg : args) {
                if(arg instanceof MultipartFile || arg instanceof Part) {
                    isFileUpload = true;
                    break;
                }
            }

            log.info("是否包含文件：{}", isFileUpload);

            String requestBody = "";

            if (isFileUpload) {
                MultipartResolver resolver = new StandardServletMultipartResolver();
                MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
                Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
                for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                    String paramName = entry.getKey(); // 参数名（如 "file"、"avatar"）
                    MultipartFile file = entry.getValue();

                    String ext = FileTypeConfig.ALLOWED_TYPES.get(file.getContentType());

                    log.info("参数名：{}", paramName);
                    log.info("文件名：{}", file.getOriginalFilename());
                    log.info("文件大小：{}", file.getSize());
                    log.info("文件类型：{}", file.getContentType());
                    log.info("文件后缀：{}", ext);

                    requestBody = requestBody + FileContentReader.readFileContent(ext, file);
                    log.info("文件内容：{}", requestBody);
                }
            } else {
                ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
                // 缓存请求体内容
                byte[] buffer = new byte[1024];
                while (wrapper.getInputStream().read(buffer) != -1) { /* 读取直到流结束 */ }
                // 读取请求体内容（JSON 字符串）
                requestBody = CommonUtil.getRequestBody(wrapper);
                log.info("请求体内容：{}", requestBody);
            }

            List<WordTagsDto> all = sensitiveWordBs.findAll(requestBody, WordResultHandlers.wordTags());
            log.info("监测到的敏感词：{}", all);
            if(CollectionUtils.isNotEmpty(all)) {
                // 提示性的
                Set<String> indicative = all
                        .stream()
                        .filter(item -> item.getTags() != null && item.getTags().contains("2"))
                        .limit(3)
                        .map(WordTagsDto::getWord).collect(Collectors.toSet());
                // 禁止性的
                Set<String> prohibitive = all
                        .stream()
                        .filter(item -> item.getTags() != null && item.getTags().contains("1"))
                        .limit(3)
                        .map(WordTagsDto::getWord).collect(Collectors.toSet());
                log.info("其中提示性敏感词：{}, 禁止性敏感词：{}", indicative, prohibitive);

                if (CollectionUtils.isNotEmpty(prohibitive)) {
                    recordLog(prohibitive, 1L, request, point, requestBody);
                    CommonUtil.returnJson(response, HttpStatus.SENSITIVE_WORD, "您所提交的信息包含禁止性敏感词，请修改后重新发布！", Result.prohibitive(prohibitive, isFileUpload));
                    return null;
                }

                if (CollectionUtils.isNotEmpty(indicative)) {
                    recordLog(indicative, 2L, request, point, requestBody);
                    CommonUtil.returnJson(response, HttpStatus.SENSITIVE_WORD, "您所提交的信息包含提示性敏感词，是否继续办理！", Result.indicative(indicative, isFileUpload));
                    return null;
                }
            }
        }
        // 继续执行目标方法
        return point.proceed();
    }


    private void recordLog(Set<String> words,
                           Long type,
                           HttpServletRequest request,
                           ProceedingJoinPoint point,
                           String requestBody) {
        SwSensitiveWordLog swSensitiveWordLog = new SwSensitiveWordLog();
        swSensitiveWordLog.setReqMethod(request.getMethod());
        swSensitiveWordLog.setReqUrl(request.getRequestURI());
        swSensitiveWordLog.setReqName(point.getSignature().getName());
        swSensitiveWordLog.setReqBody(requestBody);
        swSensitiveWordLog.setTriggerType(type);
        swSensitiveWordLog.setIpaddr(IpUtils.getIpAddr());
        swSensitiveWordLog.setCreateTime(DateUtils.getNowDate());
        swSensitiveWordLog.setCheckConf(JSONObject.toJSONString(words));

        SwAsyncManager.me().execute(SwAsyncFactory.recordLog(swSensitiveWordLog));
    }
}
