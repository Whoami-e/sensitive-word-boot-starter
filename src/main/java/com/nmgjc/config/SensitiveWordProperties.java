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

    private List<UrlConfigItem> urlPatterns;

    public String getOcrUrl() {
        return ocrUrl;
    }

    public void setOcrUrl(String ocrUrl) {
        this.ocrUrl = ocrUrl;
    }

    public List<UrlConfigItem> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(List<UrlConfigItem> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public static class UrlConfigItem {
        private String url;
        private String desc;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
