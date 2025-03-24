package com.nmgjc.word.utils;

import com.alibaba.fastjson2.JSONObject;
import com.nmgjc.word.domain.HttpResult;
import com.nmgjc.word.holder.ContextPathHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
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

    public static String findMatchedValue(List<String> patternValueList, String targetPath) {

        String processedPath = PathUtils.removeContextPath(
                targetPath,
                contextPathHolder.getContextPath()
        );
        // 遍历List中的每个元素，拆分并匹配
        return patternValueList.stream()
                .map(entry -> {
                    String[] parts = entry.split("\\|"); // 转义竖线分割
                    return parts.length >= 2 ? new String[]{parts[0], parts[1]} : null;
                })
                .filter(parts -> parts != null && pathMatcher.match(parts[0], processedPath))
                .findFirst() // 取第一个匹配项（可优化为最长路径优先）
                .map(parts -> parts[1])
                .orElse("/");
    }

    public static void main(String[] args) {
        List<String> patternValueList = new ArrayList<>();
        patternValueList.add("/test/user/**|用户管理");

        String targetPath = "/test/user/upload";
        String value = findMatchedValue(patternValueList, targetPath);

        System.out.println(value); // 输出："文件上传"
    }

}
