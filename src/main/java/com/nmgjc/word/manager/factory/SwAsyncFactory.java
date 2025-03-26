package com.nmgjc.word.manager.factory;

import cn.hutool.core.collection.CollectionUtil;
import com.nmgjc.word.domain.SwSensitiveWordDtlLog;
import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.service.ISwSensitiveWordDtlLogService;
import com.nmgjc.word.service.ISwSensitiveWordLogService;
import com.nmgjc.word.utils.SpringUtil;
import org.springframework.transaction.support.TransactionTemplate;

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
                // 获取Service实例时应确保事务代理
                ISwSensitiveWordLogService logService = SpringUtil.getBean(ISwSensitiveWordLogService.class);
                ISwSensitiveWordDtlLogService dtlLogService = SpringUtil.getBean(ISwSensitiveWordDtlLogService.class);

                // 需要将整个操作包裹在事务中
                TransactionTemplate transactionTemplate = SpringUtil.getBean(TransactionTemplate.class);
                transactionTemplate.execute(status -> {
                    logService.insertSwSensitiveWordLog(sensitiveWordLog);
                    List<SwSensitiveWordDtlLog> dtlLogs = sensitiveWordLog.getDtlLogs();
                    dtlLogs.forEach(item -> {
                        item.setWordLogId(sensitiveWordLog.getId());
                        SwSensitiveWordDtlLog swSensitiveWordDtlLog = new SwSensitiveWordDtlLog();
                        swSensitiveWordDtlLog.setWordLogId(sensitiveWordLog.getId());
                        swSensitiveWordDtlLog.setWordId(item.getWordId());
                        List<SwSensitiveWordDtlLog> logs = dtlLogService.selectSwSensitiveWordDtlLogList(swSensitiveWordDtlLog);
                        if (CollectionUtil.isNotEmpty(logs)) {
                            SwSensitiveWordDtlLog wordDtlLog = logs.get(0);
                            wordDtlLog.setTriggerCnt(wordDtlLog.getTriggerCnt() + 1);
                            dtlLogService.updateSwSensitiveWordDtlLog(wordDtlLog);
                        } else {
                            dtlLogService.insertSwSensitiveWordDtlLog(item);
                        }
                    });
                    return null;
                });
            }
        };
    }
}
