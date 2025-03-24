package com.nmgjc.word.manager.factory;

import cn.hutool.core.collection.CollectionUtil;
import com.nmgjc.word.domain.SwSensitiveWordDtlLog;
import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.service.ISwSensitiveWordDtlLogService;
import com.nmgjc.word.service.ISwSensitiveWordLogService;
import com.nmgjc.word.utils.SpringUtil;

import java.util.List;
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
                List<SwSensitiveWordDtlLog> dtlLogs = sensitiveWordLog.getDtlLogs();
                SpringUtil.getBean(ISwSensitiveWordLogService.class).insertSwSensitiveWordLog(sensitiveWordLog);
                dtlLogs.forEach(item -> {
                    item.setWordLogId(sensitiveWordLog.getId());
                    SwSensitiveWordDtlLog swSensitiveWordDtlLog = new SwSensitiveWordDtlLog();
                    swSensitiveWordDtlLog.setWordLogId(sensitiveWordLog.getId());
                    swSensitiveWordDtlLog.setWordId(item.getWordId());
                    List<SwSensitiveWordDtlLog> logs = SpringUtil.getBean(ISwSensitiveWordDtlLogService.class).selectSwSensitiveWordDtlLogList(swSensitiveWordDtlLog);
                    if (CollectionUtil.isNotEmpty(logs)) {
                        SwSensitiveWordDtlLog wordDtlLog = logs.get(0);
                        wordDtlLog.setTriggerCnt(wordDtlLog.getTriggerCnt() + 1);
                        SpringUtil.getBean(ISwSensitiveWordDtlLogService.class).updateSwSensitiveWordDtlLog(wordDtlLog);
                    } else {
                        SpringUtil.getBean(ISwSensitiveWordDtlLogService.class).insertSwSensitiveWordDtlLog(item);
                    }
                });
            }
        };
    }
}
