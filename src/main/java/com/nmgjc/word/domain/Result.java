package com.nmgjc.word.domain;

import java.util.Set;

/**
 * @ Author：enrl
 * @ Date：2025-03-07-09:47
 * @ Version：1.0
 * @ Description：敏感词结构返回
 */
public class Result {

    private String triggerType;

    private Set<String> words;

    private Boolean fileUpload;


    public static Result indicative(Set<String> words, Boolean isFileUpload){
        Result result = new Result();
        result.setTriggerType("2");
        result.setWords(words);
        result.setFileUpload(isFileUpload);
        return result;
    }

    public static Result prohibitive(Set<String> words, Boolean isFileUpload){
        Result result = new Result();
        result.setTriggerType("1");
        result.setWords(words);
        result.setFileUpload(isFileUpload);
        return result;
    }

    public Boolean getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(Boolean fileUpload) {
        this.fileUpload = fileUpload;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }
}
