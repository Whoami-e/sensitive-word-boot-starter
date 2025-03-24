package com.nmgjc.config;

import com.alibaba.fastjson2.JSONObject;
import com.nmgjc.word.interceptor.SensitiveWordInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ Author：enrl
 * @ Date：2025-03-18-10:53
 * @ Version：1.0
 * @ Description：
 */
@EnableConfigurationProperties(SensitiveWordProperties.class)
@AutoConfigureAfter(SensitiveWordConfig.class)
public class SensitiveWordInterceptorConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SensitiveWordInterceptorConfig.class);


    @Resource
    private SensitiveWordInterceptor sensitiveWordInterceptor;

    @Resource
    private SensitiveWordProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("敏感词拦截器 SensitiveWordConfig --> 开始注册敏感词拦截器");
        List<String> urlPatterns = properties.getUrlPatterns();
        if(CollectionUtils.isEmpty(urlPatterns)) {
            log.error("敏感词拦截器 SensitiveWordConfig --> 拦截路径为空 --> 请检查配置文件 sensitive-word.urlPatterns 是否正确");
        } else{
            List<String> collect = urlPatterns.stream().map(item -> item.split("\\|")[0]).collect(Collectors.toList());
            log.info("敏感词拦截器 SensitiveWordConfig --> 拦截路径：{}", collect);
            registry
                    .addInterceptor(sensitiveWordInterceptor)
                    .addPathPatterns(collect);
        }
        log.info("敏感词拦截器 SensitiveWordConfig --> 结束注册敏感词拦截器");
    }



}
