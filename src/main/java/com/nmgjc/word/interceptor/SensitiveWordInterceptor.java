package com.nmgjc.word.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.nmgjc.word.constant.HttpStatus;
import com.nmgjc.word.domain.Result;
import com.nmgjc.word.utils.CommonUtil;
import com.nmgjc.word.utils.DateUtils;
import com.nmgjc.word.utils.FileContentReader;
import com.nmgjc.word.utils.FileTypeConfig;
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


    private static final Logger log = LoggerFactory.getLogger(SensitiveWordInterceptor.class);



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


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

                        requestBody = requestBody + FileContentReader.readFileContent(ext, file);
                        log.info("文件内容：{}", requestBody);
                    }

                } else {
                    // 缓存请求体内容
                    byte[] buffer = new byte[1024];
                    while (wrapper.getInputStream().read(buffer) != -1) { /* 读取直到流结束 */ }
                    // 读取请求体内容（JSON 字符串）
                    requestBody = CommonUtil.getRequestBody(wrapper);
                    log.info("请求体内容：{}", requestBody);
                }

                List<WordTagsDto> all = sensitiveWordBs.findAll(requestBody, WordResultHandlers.wordTags());
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
                    log.info("监测到的敏感词：{}，其中提示性敏感词：{}, 禁止性敏感词：{}", all, indicative, prohibitive);

                    if (CollectionUtils.isNotEmpty(prohibitive)) {
                        CommonUtil.returnJson(response, HttpStatus.SENSITIVE_WORD, "您所提交的信息包含禁止性敏感词，请修改后重新发布！", Result.prohibitive(prohibitive, isFileUpload));
                        return false;
                    }

                    if (CollectionUtils.isNotEmpty(indicative)) {
                        CommonUtil.returnJson(response, HttpStatus.SENSITIVE_WORD, "您所提交的信息包含提示性敏感词，是否继续办理！", Result.indicative(indicative, isFileUpload));
                        return false;
                    }
                }
            }
            return true;
        } else {
            return true;
        }
    }

}
