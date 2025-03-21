package com.nmgjc.word.controller;


import com.nmgjc.word.core.page.TableData;
import com.nmgjc.word.domain.HttpResult;
import com.nmgjc.word.domain.SwSensitveWord;
import com.nmgjc.word.service.ISwSensitveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 敏感词信息Controller
 * 
 * @author enrl
 * @date 2025-03-06
 */
//@RestController
//@RequestMapping("/system/word")
public class SwSensitveWordController extends BaseController
{
    @Resource
    private ISwSensitveWordService swSensitveWordService;

    /**
     * 查询敏感词信息列表
     */

    @GetMapping("/list")
    public TableData list(SwSensitveWord swSensitveWord)
    {
        startPage();
        List<SwSensitveWord> list = swSensitveWordService.selectSwSensitveWordList(swSensitveWord);
        return getDataTable(list);
    }


    /**
     * 获取敏感词信息详细信息
     */
    @GetMapping(value = "/{id}")
    public HttpResult getInfo(@PathVariable("id") Long id)
    {
        return success(swSensitveWordService.selectSwSensitveWordById(id));
    }

    /**
     * 新增敏感词信息
     */
    @PostMapping
    public HttpResult add(@RequestBody SwSensitveWord swSensitveWord)
    {
        return toAjax(swSensitveWordService.insertSwSensitveWord(swSensitveWord));
    }

    /**
     * 修改敏感词信息
     */
    @PutMapping
    public HttpResult edit(@RequestBody SwSensitveWord swSensitveWord)
    {
        return toAjax(swSensitveWordService.updateSwSensitveWord(swSensitveWord));
    }

    /**
     * 删除敏感词信息
     */
	@DeleteMapping("/{ids}")
    public HttpResult remove(@PathVariable Long[] ids)
    {
        return toAjax(swSensitveWordService.deleteSwSensitveWordByIds(ids));
    }
}
