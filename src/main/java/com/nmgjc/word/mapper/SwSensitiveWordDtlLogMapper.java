package com.nmgjc.word.mapper;


import com.nmgjc.word.domain.SwSensitiveWordDtlLog;

import java.util.List;

/**
 * 敏感词详情日志Mapper接口
 * 
 * @author geek
 * @date 2025-03-24
 */
public interface SwSensitiveWordDtlLogMapper 
{
    /**
     * 查询敏感词详情日志
     * 
     * @param id 敏感词详情日志主键
     * @return 敏感词详情日志
     */
    public SwSensitiveWordDtlLog selectSwSensitiveWordDtlLogById(Long id);

    /**
     * 查询敏感词详情日志列表
     * 
     * @param swSensitiveWordDtlLog 敏感词详情日志
     * @return 敏感词详情日志集合
     */
    public List<SwSensitiveWordDtlLog> selectSwSensitiveWordDtlLogList(SwSensitiveWordDtlLog swSensitiveWordDtlLog);

    /**
     * 新增敏感词详情日志
     * 
     * @param swSensitiveWordDtlLog 敏感词详情日志
     * @return 结果
     */
    public int insertSwSensitiveWordDtlLog(SwSensitiveWordDtlLog swSensitiveWordDtlLog);

    /**
     * 修改敏感词详情日志
     * 
     * @param swSensitiveWordDtlLog 敏感词详情日志
     * @return 结果
     */
    public int updateSwSensitiveWordDtlLog(SwSensitiveWordDtlLog swSensitiveWordDtlLog);

    /**
     * 删除敏感词详情日志
     * 
     * @param id 敏感词详情日志主键
     * @return 结果
     */
    public int deleteSwSensitiveWordDtlLogById(Long id);

    /**
     * 批量删除敏感词详情日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSwSensitiveWordDtlLogByIds(Long[] ids);
}
