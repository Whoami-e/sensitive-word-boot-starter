package com.nmgjc.word.utils;

public class PathUtils {
    /**
     * 移除上下文路径（自动处理斜杠问题）
     * @param targetPath 原始路径（如 "/api/test/user/upload"）
     * @param contextPath 应用的上下文路径（如 "/api"）
     * @return 处理后的路径（如 "/test/user/upload"）
     */
    public static String removeContextPath(String targetPath, String contextPath) {
        if (contextPath == null || contextPath.isEmpty()) {
            return targetPath; // 无上下文路径，无需处理
        }

        // 确保contextPath以斜杠开头（如 "/api" -> "/api"）
        String normalizedContextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;

        // 处理targetPath是否以contextPath开头
        if (targetPath.startsWith(normalizedContextPath)) {
            String remainingPath = targetPath.substring(normalizedContextPath.length());
            // 确保剩余路径以斜杠开头（如空则返回 "/"）
            return remainingPath.isEmpty() ? "/" : remainingPath;
        } else {
            // 路径不匹配上下文路径（可能是错误请求）
            return targetPath;
        }
    }
}