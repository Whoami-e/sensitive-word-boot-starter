package com.nmgjc.word.controller;


import com.nmgjc.word.constant.HttpStatus;
import com.nmgjc.word.core.page.PageDomain;
import com.nmgjc.word.core.page.TableData;
import com.nmgjc.word.core.page.TableSupport;
import com.nmgjc.word.domain.HttpResult;
import com.nmgjc.word.utils.DateUtils;
import com.nmgjc.word.utils.PageUtils;
import com.nmgjc.word.utils.StringUtils;
import com.nmgjc.word.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 * 
 * @author enrl
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageUtils.startPage();
    }

    /**
     * 设置请求排序数据
     */
    protected void startOrderBy()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy()))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.orderBy(orderBy);
        }
    }

    /**
     * 清理分页的线程变量
     */
    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableData getDataTable(List<?> list)
    {
        TableData rspData = new TableData();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 返回成功
     */
    public HttpResult success()
    {
        return HttpResult.success();
    }

    /**
     * 返回失败消息
     */
    public HttpResult error()
    {
        return HttpResult.error();
    }

    /**
     * 返回成功消息
     */
    public HttpResult success(String message)
    {
        return HttpResult.success(message);
    }
    
    /**
     * 返回成功消息
     */
    public HttpResult success(Object data)
    {
        return HttpResult.success(data);
    }

    /**
     * 返回失败消息
     */
    public HttpResult error(String message)
    {
        return HttpResult.error(message);
    }

    /**
     * 返回警告消息
     */
    public HttpResult warn(String message)
    {
        return HttpResult.warn(message);
    }

    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected HttpResult toAjax(int rows)
    {
        return rows > 0 ? HttpResult.success() : HttpResult.error();
    }

    /**
     * 响应返回结果
     * 
     * @param result 结果
     * @return 操作结果
     */
    protected HttpResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

}
