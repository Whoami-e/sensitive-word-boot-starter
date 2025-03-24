package com.nmgjc.word.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * 敏感词日志对象 sw_sensitive_word_log
 * 
 * @author geek
 * @date 2025-03-24
 */
public class SwSensitiveWordLog {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 年度 */
    private Long busiYear;

    /** 请求方式;POST，GET等，ALL表示所有 */
    private String reqMethod;

    /** 请求URL */
    private String reqUrl;

    /** 请求说明 */
    private String reqName;

    /** 检查配置;JSON格式 */
    private String checkConf;

    /** 请求报文 */
    private String reqBody;

    /** 触发类型 1-禁止性；2-提示性 */
    private Long triggerType;

    /** 登录IP地址 */
    private String ipaddr;

    /** 提示消息 */
    private String msg;

    /** 登录地点 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private List<SwSensitiveWordDtlLog> dtlLogs;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setBusiYear(Long busiYear) 
    {
        this.busiYear = busiYear;
    }

    public Long getBusiYear() 
    {
        return busiYear;
    }
    public void setReqMethod(String reqMethod) 
    {
        this.reqMethod = reqMethod;
    }

    public String getReqMethod() 
    {
        return reqMethod;
    }
    public void setReqUrl(String reqUrl) 
    {
        this.reqUrl = reqUrl;
    }

    public String getReqUrl() 
    {
        return reqUrl;
    }
    public void setReqName(String reqName) 
    {
        this.reqName = reqName;
    }

    public String getReqName() 
    {
        return reqName;
    }
    public void setCheckConf(String checkConf) 
    {
        this.checkConf = checkConf;
    }

    public String getCheckConf() 
    {
        return checkConf;
    }
    public void setReqBody(String reqBody) 
    {
        this.reqBody = reqBody;
    }

    public String getReqBody() 
    {
        return reqBody;
    }
    public void setTriggerType(Long triggerType) 
    {
        this.triggerType = triggerType;
    }

    public Long getTriggerType() 
    {
        return triggerType;
    }
    public void setIpaddr(String ipaddr) 
    {
        this.ipaddr = ipaddr;
    }

    public String getIpaddr() 
    {
        return ipaddr;
    }
    public void setMsg(String msg) 
    {
        this.msg = msg;
    }

    public String getMsg() 
    {
        return msg;
    }
    public void setLoginLocation(String loginLocation) 
    {
        this.loginLocation = loginLocation;
    }

    public String getLoginLocation() 
    {
        return loginLocation;
    }
    public void setBrowser(String browser) 
    {
        this.browser = browser;
    }

    public String getBrowser() 
    {
        return browser;
    }
    public void setOs(String os) 
    {
        this.os = os;
    }

    public String getOs() 
    {
        return os;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<SwSensitiveWordDtlLog> getDtlLogs() {
        return dtlLogs;
    }

    public void setDtlLogs(List<SwSensitiveWordDtlLog> dtlLogs) {
        this.dtlLogs = dtlLogs;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("busiYear", getBusiYear())
            .append("reqMethod", getReqMethod())
            .append("reqUrl", getReqUrl())
            .append("reqName", getReqName())
            .append("checkConf", getCheckConf())
            .append("reqBody", getReqBody())
            .append("triggerType", getTriggerType())
            .append("ipaddr", getIpaddr())
            .append("msg", getMsg())
            .append("loginLocation", getLoginLocation())
            .append("browser", getBrowser())
            .append("os", getOs())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
