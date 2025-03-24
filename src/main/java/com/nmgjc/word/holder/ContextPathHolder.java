package com.nmgjc.word.holder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContextPathHolder {
    @Value("${server.servlet.context-path:}") // 默认为空
    private String contextPath;

    public String getContextPath() {
        return contextPath;
    }
}