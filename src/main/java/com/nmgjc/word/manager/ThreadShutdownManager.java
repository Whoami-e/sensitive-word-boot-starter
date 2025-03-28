package com.nmgjc.word.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 确保应用退出时能关闭后台线程
 *
 * @author geek
 */
@Component
public class ThreadShutdownManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadShutdownManager.class);

    @PreDestroy
    public void destroy()
    {
        shutdownAsyncManager();
    }

    /**
     * 停止异步执行任务
     */
    private void shutdownAsyncManager() {
        try {
            logger.info("====关闭后台任务任务线程池====");
            SwAsyncManager.me().shutdown();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
