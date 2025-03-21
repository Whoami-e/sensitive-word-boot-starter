package com.nmgjc.word.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileTypeConfig {
    // 允许的文件类型映射（扩展名 -> MIME类型）
    public static final Map<String, String> ALLOWED_TYPES;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("image/jpeg", "jpg");
        map.put("image/png", "png");
        map.put("application/pdf", "pdf");
        map.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        map.put("application/vnd.ms-excel", "xls"); // 旧版Excel
        map.put("text/plain", "txt");
        map.put("text/csv", "csv");
        map.put("application/msword", "doc");                     // DOC
        map.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"); // DOCX
        ALLOWED_TYPES = Collections.unmodifiableMap(map);
    }
}