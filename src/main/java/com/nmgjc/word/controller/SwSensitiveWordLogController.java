package com.nmgjc.word.controller;

import com.nmgjc.word.core.page.TableData;
import com.nmgjc.word.domain.HttpResult;
import com.nmgjc.word.domain.SwSensitiveWordLog;
import com.nmgjc.word.service.ISwSensitiveWordLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 敏感词日志Controller
 * 
 * @author enrl
 * @date 2025-03-06
 */
//@RestController
//@RequestMapping("/system/sensitiveWordLog")
public class SwSensitiveWordLogController extends BaseController
{
    @Resource
    private ISwSensitiveWordLogService swSensitiveWordLogService;

    /**
     * 查询敏感词日志列表
     */
    @GetMapping("/list")
    public TableData list(SwSensitiveWordLog swSensitiveWordLog)
    {
        startPage();
        List<SwSensitiveWordLog> list = swSensitiveWordLogService.selectSwSensitiveWordLogList(swSensitiveWordLog);
        return getDataTable(list);
    }


    /**
     * 获取敏感词日志详细信息
     */
    @GetMapping(value = "/{id}")
    public HttpResult getInfo(@PathVariable("id") Long id)
    {
        return success(swSensitiveWordLogService.selectSwSensitiveWordLogById(id));
    }

    /**
     * 新增敏感词日志
     */
    @PostMapping
    public HttpResult add(@RequestBody SwSensitiveWordLog swSensitiveWordLog)
    {
        return HttpResult.success(swSensitiveWordLogService.insertSwSensitiveWordLog(swSensitiveWordLog));
    }

    /**
     * 修改敏感词日志
     */
    @PutMapping
    public HttpResult edit(@RequestBody SwSensitiveWordLog swSensitiveWordLog)
    {
        return toAjax(swSensitiveWordLogService.updateSwSensitiveWordLog(swSensitiveWordLog));
    }

    /**
     * 删除敏感词日志
     */
	@DeleteMapping("/{ids}")
    public HttpResult remove(@PathVariable Long[] ids)
    {
        return toAjax(swSensitiveWordLogService.deleteSwSensitiveWordLogByIds(ids));
    }
}
