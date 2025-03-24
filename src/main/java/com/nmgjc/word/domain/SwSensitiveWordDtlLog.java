package com.nmgjc.word.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * 敏感词详情日志对象 sw_sensitive_word_dtl_log
 * 
 * @author geek
 * @date 2025-03-24
 */
public class SwSensitiveWordDtlLog {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 年度 */
    private Long busiYear;

    /** 敏感词日志ID;sw_sensitive_word_log.id */
    private Long wordLogId;

    /** 敏感词ID;sw_sensitve_word.id */
    private Long wordId;

    /** 触发位置;1-正文；2-附件 */
    private Long triggerLocation;

    /** 文件名 */
    private String fileName;

    /** 扩展名 */
    private String fileExt;

    /** 文件内容 */
    private String fileContent;

    /** 触发次数 */
    private Long triggerCnt;

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
    public void setWordLogId(Long wordLogId) 
    {
        this.wordLogId = wordLogId;
    }

    public Long getWordLogId() 
    {
        return wordLogId;
    }
    public void setWordId(Long wordId) 
    {
        this.wordId = wordId;
    }

    public Long getWordId() 
    {
        return wordId;
    }
    public void setTriggerLocation(Long triggerLocation) 
    {
        this.triggerLocation = triggerLocation;
    }

    public Long getTriggerLocation() 
    {
        return triggerLocation;
    }
    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setFileExt(String fileExt) 
    {
        this.fileExt = fileExt;
    }

    public String getFileExt() 
    {
        return fileExt;
    }
    public void setFileContent(String fileContent) 
    {
        this.fileContent = fileContent;
    }

    public String getFileContent() 
    {
        return fileContent;
    }
    public void setTriggerCnt(Long triggerCnt) 
    {
        this.triggerCnt = triggerCnt;
    }

    public Long getTriggerCnt() 
    {
        return triggerCnt;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("busiYear", getBusiYear())
            .append("wordLogId", getWordLogId())
            .append("wordId", getWordId())
            .append("triggerLocation", getTriggerLocation())
            .append("fileName", getFileName())
            .append("fileExt", getFileExt())
            .append("fileContent", getFileContent())
            .append("triggerCnt", getTriggerCnt())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
