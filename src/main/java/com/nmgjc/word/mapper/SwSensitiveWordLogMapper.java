package com.nmgjc.word.mapper;



import com.nmgjc.word.domain.SwSensitiveWordLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 敏感词日志Mapper接口
 * 
 * @author enrl
 * @date 2025-03-06
 */
@Mapper
public interface SwSensitiveWordLogMapper 
{
    /**
     * 查询敏感词日志
     * 
     * @param id 敏感词日志主键
     * @return 敏感词日志
     */
    public SwSensitiveWordLog selectSwSensitiveWordLogById(Long id);

    /**
     * 查询敏感词日志列表
     * 
     * @param swSensitiveWordLog 敏感词日志
     * @return 敏感词日志集合
     */
    public List<SwSensitiveWordLog> selectSwSensitiveWordLogList(SwSensitiveWordLog swSensitiveWordLog);


    /**
     * 新增敏感词日志
     * 
     * @param swSensitiveWordLog 敏感词日志
     * @return 结果
     */
    public int insertSwSensitiveWordLog(SwSensitiveWordLog swSensitiveWordLog);

    /**
     * 修改敏感词日志
     * 
     * @param swSensitiveWordLog 敏感词日志
     * @return 结果
     */
    public int updateSwSensitiveWordLog(SwSensitiveWordLog swSensitiveWordLog);

    /**
     * 删除敏感词日志
     * 
     * @param id 敏感词日志主键
     * @return 结果
     */
    public int deleteSwSensitiveWordLogById(Long id);

    /**
     * 批量删除敏感词日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSwSensitiveWordLogByIds(Long[] ids);
}
