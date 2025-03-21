package com.nmgjc.config;


import cn.hutool.core.collection.CollectionUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.api.IWordTag;
import com.nmgjc.word.domain.SwSensitveWord;
import com.nmgjc.word.interceptor.SensitiveWordInterceptor;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.nmgjc.word.mapper.SwSensitiveWordLogMapper;
import com.nmgjc.word.mapper.SwSensitveWordMapper;
import com.nmgjc.word.service.ISwSensitiveWordLogService;
import com.nmgjc.word.service.ISwSensitveWordService;
import com.nmgjc.word.service.impl.SwSensitiveWordLogServiceImpl;
import com.nmgjc.word.service.impl.SwSensitveWordServiceImpl;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @ Author：enrl
 * @ Date：2025-03-06-16:30
 * @ Version：1.0
 * @ Description：敏感词过滤配置类
 */
@ComponentScan(basePackages = {"com.nmgjc.word"})
@MapperScan(basePackages = {"com.nmgjc.word.mapper"})
public class SensitiveWordConfig{
    private static final Logger log = LoggerFactory.getLogger(SensitiveWordConfig.class);

    public SensitiveWordConfig(){
        log.info("敏感词拦截器 SensitiveWordConfig 开始初始化");
    }


    @Bean
    @ConditionalOnMissingBean
    public ISwSensitveWordService swSensitveWordService(SwSensitveWordMapper swSensitveWordMapper){
        log.info("敏感词拦截器 swSensitveWordService 开始初始化");
        return new SwSensitveWordServiceImpl(swSensitveWordMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ISwSensitiveWordLogService swSensitiveWordLogService(SwSensitiveWordLogMapper swSensitiveWordLogMapper){
        log.info("敏感词拦截器 swSensitiveWordLogService 开始初始化");
        return new SwSensitiveWordLogServiceImpl(swSensitiveWordLogMapper);
    }

    @Bean
    @ConditionalOnBean(ISwSensitveWordService.class)
    public SensitiveWordBs sensitiveWordBs(ISwSensitveWordService swSensitveWordService) {
        log.info("敏感词拦截器 SensitiveWordConfig 开始初始化敏感词引导类");
        SensitiveWordBs sensitiveWordBs =
                SensitiveWordBs.newInstance()
                        .wordDeny(new IWordDeny() {
                            @Override
                            public List<String> deny() {
                                List<SwSensitveWord> swSensitveWords = swSensitveWordService.selectSwSensitveWordList();
                                log.info("共有 {} 条敏感词", swSensitveWords.size());

                                return swSensitveWords.stream().map(SwSensitveWord::getWordName).collect(Collectors.toList());
                            }
                        })
                        .wordTag(new IWordTag() {
                            @Override
                            public Set<String> getTag(String word) {
                                if(StringUtil.isEmpty(word)) {
                                    return Collections.emptySet();
                                }
                                List<SwSensitveWord> swSensitveWords = swSensitveWordService.selectSwSensitveWordTags(word);
                                if(CollectionUtil.isEmpty(swSensitveWords)) {
                                    return Collections.emptySet();
                                }

                                return swSensitveWords.stream().map(item -> item.getTriggerType().toString()).collect(Collectors.toSet());
                            }
                        })
                        .ignoreNumStyle(false)
                        .init();

        log.info("敏感词拦截器 SensitiveWordConfig 结束初始化敏感词引导类");
        return sensitiveWordBs;
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "swScheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService(){
        return new ScheduledThreadPoolExecutor(50,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy()){
            @Override
            protected void afterExecute(Runnable r, Throwable t){
                super.afterExecute(r, t);
            }
        };
    }

    @Bean
    @DependsOn("sensitiveWordBs")
    public SensitiveWordInterceptor sensitiveWordInterceptor() {
        log.info("敏感词拦截器 SensitiveWordConfig 开始实例化敏感词拦截器");
        return new SensitiveWordInterceptor();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.setConnectTimeout(Duration.ofMillis(3000)).setReadTimeout(Duration.ofMillis(50000)).build();
    }
}
