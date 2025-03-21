package com.nmgjc.word.manager.factory;

import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.service.ISwSensitiveWordLogService;
import com.nmgjc.word.utils.SpringUtil;

import java.util.TimerTask;

/**
 * @ Author：enrl
 * @ Date：2025-03-17-15:52
 * @ Version：1.0
 * @ Description：异步工厂
 */
public class SwAsyncFactory {

    public static TimerTask recordLog(final SwSensitiveWordLog sensitiveWordLog) {
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtil.getBean(ISwSensitiveWordLogService.class).insertSwSensitiveWordLog(sensitiveWordLog);
            }
        };
    }
}
