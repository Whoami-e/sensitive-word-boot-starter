package com.nmgjc.word.service.impl;


import com.nmgjc.word.domain.SwSensitveWord;
import com.nmgjc.word.mapper.SwSensitveWordMapper;
import com.nmgjc.word.service.ISwSensitveWordService;
import com.nmgjc.word.utils.DateUtils;
import com.nmgjc.word.utils.SpringUtil;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 敏感词信息Service业务层处理
 * 
 * @author enrl
 * @date 2025-03-06
 */
public class SwSensitveWordServiceImpl implements ISwSensitveWordService {

    private final SwSensitveWordMapper swSensitveWordMapper;

    public SwSensitveWordServiceImpl(SwSensitveWordMapper swSensitveWordMapper) {
        this.swSensitveWordMapper = swSensitveWordMapper;
    }


    /**
     * 查询敏感词信息
     * 
     * @param id 敏感词信息主键
     * @return 敏感词信息
     */
    @Override
    public SwSensitveWord selectSwSensitveWordById(Long id)
    {
        return swSensitveWordMapper.selectSwSensitveWordById(id);
    }

    /**
     * 查询敏感词信息列表
     * 
     * @param swSensitveWord 敏感词信息
     * @return 敏感词信息
     */
    @Override
    public List<SwSensitveWord> selectSwSensitveWordList(SwSensitveWord swSensitveWord)
    {
        swSensitveWord.setDelFlag(1L);
        return swSensitveWordMapper.selectSwSensitveWordList(swSensitveWord);
    }

    @Override
    public List<SwSensitveWord> selectSwSensitveWordList()
    {
        return swSensitveWordMapper.selectSwSensitveWordAll();
    }

    @Override
    public List<SwSensitveWord> selectSwSensitveWordTags(String word)
    {
        return swSensitveWordMapper.selectSwSensitveWordTags(word);
    }

    /**
     * 新增敏感词信息
     * 
     * @param swSensitveWord 敏感词信息
     * @return 结果
     */
    @Override
    public int insertSwSensitveWord(SwSensitveWord swSensitveWord)
    {
        swSensitveWord.setCreateTime(DateUtils.getNowDate());
        String wordName = swSensitveWord.getWordName();
        SpringUtil.getBean(SensitiveWordBs.class).addWord(wordName);
        return swSensitveWordMapper.insertSwSensitveWord(swSensitveWord);
    }

    /**
     * 修改敏感词信息
     * 
     * @param swSensitveWord 敏感词信息
     * @return 结果
     */
    @Override
    public int updateSwSensitveWord(SwSensitveWord swSensitveWord)
    {
        swSensitveWord.setUpdateTime(DateUtils.getNowDate());
        return swSensitveWordMapper.updateSwSensitveWord(swSensitveWord);
    }

    /**
     * 批量删除敏感词信息
     * 
     * @param ids 需要删除的敏感词信息主键
     * @return 结果
     */
    @Override
    public int deleteSwSensitveWordByIds(Long[] ids)
    {
        return swSensitveWordMapper.deleteSwSensitveWordByIds(ids);
    }

    /**
     * 删除敏感词信息信息
     * 
     * @param id 敏感词信息主键
     * @return 结果
     */
    @Override
    public int deleteSwSensitveWordById(Long id)
    {
        return swSensitveWordMapper.deleteSwSensitveWordById(id);
    }

}
