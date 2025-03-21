package com.nmgjc.word.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 敏感词信息对象 sw_sensitve_word
 * 
 * @author enrl
 * @date 2025-03-06
 */
public class SwSensitveWord {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 敏感词内容 */

    private String wordName;

    /** 敏感词分类;1.暴恐；2.涉政；3.反动；4.违禁；10-特殊字符；99.其他 */
    private Long wordType;

    /** 触发类型;1-禁止性；2-提示信 */
    private Long triggerType;

    /** 状态;0-正常；1-停用 */
    private Long status;

    /** 删除标志;1-正常；0-删除 */
    private Long delFlag;

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

    /** 备注 */
    private String remark;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setWordName(String wordName) 
    {
        this.wordName = wordName;
    }

    public String getWordName() 
    {
        return wordName;
    }
    public void setWordType(Long wordType) 
    {
        this.wordType = wordType;
    }

    public Long getWordType() 
    {
        return wordType;
    }
    public void setTriggerType(Long triggerType) 
    {
        this.triggerType = triggerType;
    }

    public Long getTriggerType() 
    {
        return triggerType;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("wordName", getWordName())
            .append("wordType", getWordType())
            .append("triggerType", getTriggerType())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
