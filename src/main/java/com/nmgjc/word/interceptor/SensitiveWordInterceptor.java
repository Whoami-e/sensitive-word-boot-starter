package com.nmgjc.word.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.nmgjc.config.SensitiveWordProperties;
import com.nmgjc.word.constant.HttpStatus;
import com.nmgjc.word.domain.Result;
import com.nmgjc.word.domain.SwSensitiveWordDtlLog;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ Author：enrl
 * @ Date：2025-03-06-16:38
 * @ Version：1.0
 * @ Description：敏感词拦截器
 */
public class SensitiveWordInterceptor implements HandlerInterceptor {

    @Resource
    private SensitiveWordBs sensitiveWordBs;

    @Resource
    private SensitiveWordProperties properties;

    private static final Logger log = LoggerFactory.getLogger(SensitiveWordInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String header = request.getHeader("X-Ignore-Sensitive-Check");
            log.info("是否二次拦截：{}", header);
            if ("true".equals(header)) {
                return true;
            }
            /**
             * 跨域请求会首先发送一个option请求，这里我们给option请求直接返回正常状态
             */
            if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return true;
            }
            if (handler instanceof HandlerMethod) {
                log.info("敏感词拦截器 SensitiveWordInterceptor 开始拦截");
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                /**
                 * 由于HttpServletRequest的输入流只能读取一次，如果在拦截器中直接读取了，后续的控制器方法可能无法再次读取。
                 * ContentCachingRequestWrapper。这个类可以包装原始的HttpServletRequest，允许我们多次读取请求体的内容。
                 * 在拦截器中，我们可以将原始的请求对象替换为这个包装后的对象，这样在后续的处理中，请求体可以被多次读取。
                 */
                ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
                //  POST/PUT请求
                if (HttpMethod.POST.name().equals(request.getMethod()) || HttpMethod.PUT.name().equals(request.getMethod())) {
                    //  判断是否包含文件
                    boolean isFileUpload = false;
                    // 1. 检查 Content-Type
                    String contentType = request.getContentType();
                    if (contentType != null && contentType.startsWith("multipart/")) {
                        isFileUpload = true;
                    }

                    Method method = handlerMethod.getMethod();

                    for (Parameter parameter : method.getParameters()) {
                        Class<?> paramType = parameter.getType();
                        if (MultipartFile.class.isAssignableFrom(paramType) || Part.class.isAssignableFrom(paramType)) {
                            isFileUpload = true;
                            break;
                        }
                    }

                    log.info("是否包含文件：{}", isFileUpload);

                    String requestBody = "";

                    List<WordTagsDto> all = new ArrayList<>();
                    List<SwSensitiveWordDtlLog> dtlLogs = new ArrayList<>();

                    // 文件
                    if (isFileUpload) {
                        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
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

                            requestBody = FileContentReader.readFileContent(ext, file);
                            log.info("文件内容：{}", requestBody);

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

                    } else {
                        // 缓存请求体内容
                        byte[] buffer = new byte[1024];
                        while (wrapper.getInputStream().read(buffer) != -1) { /* 读取直到流结束 */ }
                        // 读取请求体内容（JSON 字符串）
                        requestBody = CommonUtil.getRequestBody(wrapper);
                        log.info("请求体内容：{}", requestBody);

                        List<WordTagsDto> bodyWordTags = sensitiveWordBs.findAll(requestBody, WordResultHandlers.wordTags());
                        if(CollectionUtils.isNotEmpty(bodyWordTags)) {
                            all.addAll(bodyWordTags);
                            for (WordTagsDto bodyWordTag : bodyWordTags) {
                                SwSensitiveWordDtlLog swSensitiveWordDtlLog = new SwSensitiveWordDtlLog();
                                swSensitiveWordDtlLog.setBusiYear(DateUtils.getYear());
                                Set<String> tags = bodyWordTag.getTags();
                                if(CollectionUtils.isNotEmpty(tags)) {
                                    String next = tags.iterator().next();
                                    swSensitiveWordDtlLog.setWordId(Long.parseLong(next.split("\\|")[1]));
                                } else {
                                    swSensitiveWordDtlLog.setWordId(0L);
                                }
                                swSensitiveWordDtlLog.setFileContent(requestBody);
                                swSensitiveWordDtlLog.setTriggerLocation(1L);
                                swSensitiveWordDtlLog.setTriggerCnt(1L);
                                dtlLogs.add(swSensitiveWordDtlLog);
                            }
                        }
                    }

                    if(CollectionUtils.isNotEmpty(all)) {
                        // 提示性的
                        Set<String> indicative = all.stream()
                                .filter(item -> item.getTags() != null
                                        && item.getTags().stream()  // 遍历每个tag
                                        .anyMatch(tag -> {     // 只要有一个tag满足条件即可
                                            String[] parts = tag.split("\\|");  // 按竖线分割（注意转义）
                                            return parts.length > 0 && parts[0].contains("2"); // 检查第0个元素
                                        }))
                                .limit(3)
                                .map(WordTagsDto::getWord)
                                .collect(Collectors.toSet());
                        // 禁止性的
                        Set<String> prohibitive = all.stream()
                                .filter(item -> item.getTags() != null
                                        && item.getTags().stream()  // 遍历每个tag
                                        .anyMatch(tag -> {     // 只要有一个tag满足条件即可
                                            String[] parts = tag.split("\\|");  // 按竖线分割（注意转义）
                                            return parts.length > 0 && parts[0].contains("1"); // 检查第0个元素
                                        }))
                                .limit(3)
                                .map(WordTagsDto::getWord)
                                .collect(Collectors.toSet());
                        log.info("监测到的敏感词：{}，其中提示性敏感词：{}, 禁止性敏感词：{}", all, indicative, prohibitive);

                        if (CollectionUtils.isNotEmpty(prohibitive)) {
                            // 记录日志
                            CommonUtil.recordLog(1L, request, requestBody, "您所提交的信息包含禁止性敏感词，请修改后重新发布！", dtlLogs, CommonUtil.findMatchedValue(properties.getUrlPatterns(), request.getRequestURI()));
                            CommonUtil.returnJson(response, HttpStatus.SENSITIVE_WORD, "您所提交的信息包含禁止性敏感词，请修改后重新发布！", Result.prohibitive(prohibitive, isFileUpload));
                            return false;
                        }

                        if (CollectionUtils.isNotEmpty(indicative)) {
                            // 记录日志
                            CommonUtil.recordLog(2L, request, requestBody, "您所提交的信息包含提示性敏感词，是否继续办理！", dtlLogs, CommonUtil.findMatchedValue(properties.getUrlPatterns(), request.getRequestURI()));
                            CommonUtil.returnJson(response, HttpStatus.SENSITIVE_WORD, "您所提交的信息包含提示性敏感词，是否继续办理！", Result.indicative(indicative, isFileUpload));
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error("敏感词拦截器 SensitiveWordInterceptor 拦截异常", e);
            return true;
        }
    }
}
