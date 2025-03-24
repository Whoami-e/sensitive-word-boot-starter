package com.nmgjc.word.utils;

import com.alibaba.fastjson2.JSONObject;
import com.github.houbb.sensitive.word.core.SensitiveWord;
import com.nmgjc.config.SensitiveWordProperties;
import com.nmgjc.word.domain.HttpResult;
import com.nmgjc.word.domain.SwSensitiveWordDtlLog;
import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.holder.ContextPathHolder;
import com.nmgjc.word.manager.SwAsyncManager;
import com.nmgjc.word.manager.factory.SwAsyncFactory;
import com.nmgjc.word.utils.ip.IpUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @ Author：enrl
 * @ Date：2025-03-17-18:35
 * @ Version：1.0
 * @ Description：一些通用方法
 */
public class CommonUtil {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static  final ContextPathHolder contextPathHolder = SpringUtil.getBean(ContextPathHolder.class);


    public static String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            // 获取缓存的请求体字节数组
            byte[] contentBytes = request.getContentAsByteArray();
            if (contentBytes.length == 0) {
                return "";
            }
            // 将字节数组转换为字符串（UTF-8编码）
            return new String(contentBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    public static void returnJson(HttpServletResponse response, Integer code, String msg, Object data) throws Exception {
        PrintWriter writer = null;
        response.setContentType("application/json;charset=UTF-8");
        try {
            HttpResult httpResult = new HttpResult(code, msg, data);
            writer = response.getWriter();
            writer.write(JSONObject.toJSONString(httpResult));
        } catch (IOException e) {

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static String findMatchedValue(List<SensitiveWordProperties.UrlConfigItem> patternValueList, String targetPath) {

        String processedPath = PathUtils.removeContextPath(
                targetPath,
                contextPathHolder.getContextPath()
        );
        // 遍历所有URL模式，找到第一个匹配项
        return patternValueList.stream()
                .filter(pattern -> pathMatcher.match(pattern.getUrl(), processedPath))
                .findFirst() // 默认取第一个匹配项，可优化为最长路径优先
                .map(SensitiveWordProperties.UrlConfigItem::getDesc)
                .orElse("/");
    }

    public static void recordLog(Long type,
                           HttpServletRequest request,
                           String requestBody,
                           String msg,
                           List<SwSensitiveWordDtlLog> dtlLogs,
                           String reqName) {
        SwSensitiveWordLog swSensitiveWordLog = new SwSensitiveWordLog();
        swSensitiveWordLog.setBusiYear(DateUtils.getYear());
        swSensitiveWordLog.setReqMethod(request.getMethod());
        swSensitiveWordLog.setReqUrl(request.getRequestURI());
        swSensitiveWordLog.setReqName(reqName);
        swSensitiveWordLog.setReqBody(requestBody);
        swSensitiveWordLog.setTriggerType(type);
        swSensitiveWordLog.setIpaddr(IpUtils.getIpAddr());
        swSensitiveWordLog.setMsg(msg);
        swSensitiveWordLog.setCreateTime(DateUtils.getNowDate());
        swSensitiveWordLog.setDtlLogs(dtlLogs);

        SwAsyncManager.me().execute(SwAsyncFactory.recordLog(swSensitiveWordLog));
    }

}
