package com.nmgjc.word.service;



import com.nmgjc.word.domain.SwSensitveWord;

import java.util.List;

/**
 * 敏感词信息Service接口
 * 
 * @author enrl
 * @date 2025-03-06
 */
public interface ISwSensitveWordService 
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
    public List<SwSensitveWord> selectSwSensitveWordList();

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
     * 批量删除敏感词信息
     * 
     * @param ids 需要删除的敏感词信息主键集合
     * @return 结果
     */
    public int deleteSwSensitveWordByIds(Long[] ids);

    /**
     * 删除敏感词信息信息
     * 
     * @param id 敏感词信息主键
     * @return 结果
     */
    public int deleteSwSensitveWordById(Long id);
}
