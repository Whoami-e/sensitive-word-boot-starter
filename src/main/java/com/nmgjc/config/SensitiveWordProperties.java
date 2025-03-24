package com.nmgjc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @ Author：enrl
 * @ Date：2025-03-20-15:24
 * @ Version：1.0
 * @ Description：
 */
@ConfigurationProperties(prefix = "sensitive-word")
public class SensitiveWordProperties {

    private String ocrUrl;

    private List<String> urlPatterns;

    public String getOcrUrl() {
        return ocrUrl;
    }

    public void setOcrUrl(String ocrUrl) {
        this.ocrUrl = ocrUrl;
    }

    public List<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(List<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }
}
