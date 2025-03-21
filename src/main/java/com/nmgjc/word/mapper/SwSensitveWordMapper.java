package com.nmgjc.word.mapper;



import com.nmgjc.word.domain.SwSensitveWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 敏感词信息Mapper接口
 * 
 * @author enrl
 * @date 2025-03-06
 */
@Mapper
public interface SwSensitveWordMapper 
{
    /**
     * 查询敏感词信息
     * 
     * @param id 敏感词信息主键
     * @return 敏感词信息
     */
    public SwSensitveWord selectSwSensitveWordById(Long id);

    /**
     * 查询敏感词信息列表
     * 
     * @param swSensitveWord 敏感词信息
     * @return 敏感词信息集合
     */
    public List<SwSensitveWord> selectSwSensitveWordList(SwSensitveWord swSensitveWord);

    @Select("SELECT word_name as wordName FROM sw_sensitve_word where status = 0 and del_flag = 1")
    public List<SwSensitveWord> selectSwSensitveWordAll();

    @Select("SELECT trigger_type as triggerType FROM sw_sensitve_word where status = 0 and del_flag = 1 and word_name = #{word}")
    public List<SwSensitveWord> selectSwSensitveWordTags(String word);

    /**
     * 新增敏感词信息
     * 
     * @param swSensitveWord 敏感词信息
     * @return 结果
     */
    public int insertSwSensitveWord(SwSensitveWord swSensitveWord);

    /**
     * 修改敏感词信息
     * 
     * @param swSensitveWord 敏感词信息
     * @return 结果
     */
    public int updateSwSensitveWord(SwSensitveWord swSensitveWord);

    /**
     * 删除敏感词信息
     * 
     * @param id 敏感词信息主键
     * @return 结果
     */
    public int deleteSwSensitveWordById(Long id);

    /**
     * 批量删除敏感词信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSwSensitveWordByIds(Long[] ids);
}
