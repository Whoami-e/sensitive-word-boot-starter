package com.nmgjc.word.manager;

import com.nmgjc.word.utils.SpringUtil;
import com.nmgjc.word.utils.Threads;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ Author：enrl
 * @ Date：2025-03-17-15:49
 * @ Version：1.0
 * @ Description：异步任务管理器
 */
public class SwAsyncManager {

    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;

    private ScheduledExecutorService executor = SpringUtil.getBean("swScheduledExecutorService");

    private SwAsyncManager(){}

    private static SwAsyncManager me = new SwAsyncManager();

    public static SwAsyncManager me() {
        return me;
    }

    public void execute(TimerTask task)
    {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        Threads.shutdownAndAwaitTermination(executor);
    }

}
