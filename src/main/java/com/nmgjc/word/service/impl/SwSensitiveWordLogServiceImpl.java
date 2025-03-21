package com.nmgjc.word.service.impl;

import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.mapper.SwSensitiveWordLogMapper;
import com.nmgjc.word.service.ISwSensitiveWordLogService;
import com.nmgjc.word.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 敏感词日志Service业务层处理
 * 
 * @author enrl
 * @date 2025-03-06
 */
public class SwSensitiveWordLogServiceImpl implements ISwSensitiveWordLogService {
    private final SwSensitiveWordLogMapper swSensitiveWordLogMapper;

    public SwSensitiveWordLogServiceImpl(SwSensitiveWordLogMapper swSensitiveWordLogMapper) {
        this.swSensitiveWordLogMapper = swSensitiveWordLogMapper;
    }

    /**
     * 查询敏感词日志
     * 
     * @param id 敏感词日志主键
     * @return 敏感词日志
     */
    @Override
    public SwSensitiveWordLog selectSwSensitiveWordLogById(Long id)
    {
        return swSensitiveWordLogMapper.selectSwSensitiveWordLogById(id);
    }

    /**
     * 查询敏感词日志列表
     * 
     * @param swSensitiveWordLog 敏感词日志
     * @return 敏感词日志
     */
    @Override
    public List<SwSensitiveWordLog> selectSwSensitiveWordLogList(SwSensitiveWordLog swSensitiveWordLog)
    {
        return swSensitiveWordLogMapper.selectSwSensitiveWordLogList(swSensitiveWordLog);
    }

    /**
     * 新增敏感词日志
     * 
     * @param swSensitiveWordLog 敏感词日志
     * @return 结果
     */
    @Override
    public int insertSwSensitiveWordLog(SwSensitiveWordLog swSensitiveWordLog)
    {
        swSensitiveWordLog.setCreateTime(DateUtils.getNowDate());
        return swSensitiveWordLogMapper.insertSwSensitiveWordLog(swSensitiveWordLog);
    }

    /**
     * 修改敏感词日志
     * 
     * @param swSensitiveWordLog 敏感词日志
     * @return 结果
     */
    @Override
    public int updateSwSensitiveWordLog(SwSensitiveWordLog swSensitiveWordLog)
    {
        swSensitiveWordLog.setUpdateTime(DateUtils.getNowDate());
        return swSensitiveWordLogMapper.updateSwSensitiveWordLog(swSensitiveWordLog);
    }

    /**
     * 批量删除敏感词日志
     * 
     * @param ids 需要删除的敏感词日志主键
     * @return 结果
     */
    @Override
    public int deleteSwSensitiveWordLogByIds(Long[] ids)
    {
        return swSensitiveWordLogMapper.deleteSwSensitiveWordLogByIds(ids);
    }

    /**
     * 删除敏感词日志信息
     * 
     * @param id 敏感词日志主键
     * @return 结果
     */
    @Override
    public int deleteSwSensitiveWordLogById(Long id)
    {
        return swSensitiveWordLogMapper.deleteSwSensitiveWordLogById(id);
    }
}
