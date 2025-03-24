package com.nmgjc.word.service.impl;

import com.nmgjc.word.domain.SwSensitiveWordDtlLog;
import com.nmgjc.word.mapper.SwSensitiveWordDtlLogMapper;
import com.nmgjc.word.service.ISwSensitiveWordDtlLogService;
import com.nmgjc.word.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 敏感词详情日志Service业务层处理
 * 
 * @author geek
 * @date 2025-03-24
 */
@Service
public class SwSensitiveWordDtlLogServiceImpl implements ISwSensitiveWordDtlLogService
{
    @Autowired
    private SwSensitiveWordDtlLogMapper swSensitiveWordDtlLogMapper;

    /**
     * 查询敏感词详情日志
     * 
     * @param id 敏感词详情日志主键
     * @return 敏感词详情日志
     */
    @Override
    public SwSensitiveWordDtlLog selectSwSensitiveWordDtlLogById(Long id)
    {
        return swSensitiveWordDtlLogMapper.selectSwSensitiveWordDtlLogById(id);
    }

    /**
     * 查询敏感词详情日志列表
     * 
     * @param swSensitiveWordDtlLog 敏感词详情日志
     * @return 敏感词详情日志
     */
    @Override
    public List<SwSensitiveWordDtlLog> selectSwSensitiveWordDtlLogList(SwSensitiveWordDtlLog swSensitiveWordDtlLog)
    {
        return swSensitiveWordDtlLogMapper.selectSwSensitiveWordDtlLogList(swSensitiveWordDtlLog);
    }

    /**
     * 新增敏感词详情日志
     * 
     * @param swSensitiveWordDtlLog 敏感词详情日志
     * @return 结果
     */
    @Override
    public int insertSwSensitiveWordDtlLog(SwSensitiveWordDtlLog swSensitiveWordDtlLog)
    {
        swSensitiveWordDtlLog.setCreateTime(DateUtils.getNowDate());
        return swSensitiveWordDtlLogMapper.insertSwSensitiveWordDtlLog(swSensitiveWordDtlLog);
    }

    /**
     * 修改敏感词详情日志
     * 
     * @param swSensitiveWordDtlLog 敏感词详情日志
     * @return 结果
     */
    @Override
    public int updateSwSensitiveWordDtlLog(SwSensitiveWordDtlLog swSensitiveWordDtlLog)
    {
        return swSensitiveWordDtlLogMapper.updateSwSensitiveWordDtlLog(swSensitiveWordDtlLog);
    }

    /**
     * 批量删除敏感词详情日志
     * 
     * @param ids 需要删除的敏感词详情日志主键
     * @return 结果
     */
    @Override
    public int deleteSwSensitiveWordDtlLogByIds(Long[] ids)
    {
        return swSensitiveWordDtlLogMapper.deleteSwSensitiveWordDtlLogByIds(ids);
    }

    /**
     * 删除敏感词详情日志信息
     * 
     * @param id 敏感词详情日志主键
     * @return 结果
     */
    @Override
    public int deleteSwSensitiveWordDtlLogById(Long id)
    {
        return swSensitiveWordDtlLogMapper.deleteSwSensitiveWordDtlLogById(id);
    }
}
