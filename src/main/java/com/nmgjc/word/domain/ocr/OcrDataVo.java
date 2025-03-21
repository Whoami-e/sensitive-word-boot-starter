package com.nmgjc.word.domain.ocr;

import java.math.BigDecimal;
import java.util.List;

public class OcrDataVo {

    //base64图片
    private String img_detected;

    //输出

    private List<Object> raw_out;

    //速度
    private BigDecimal speed_time;

    //识别输出
    private String data;

    public String getImg_detected() {
        return img_detected;
    }

    public void setImg_detected(String img_detected) {
        this.img_detected = img_detected;
    }

    public List<Object> getRaw_out() {
        return raw_out;
    }

    public void setRaw_out(List<Object> raw_out) {
        this.raw_out = raw_out;
    }

    public BigDecimal getSpeed_time() {
        return speed_time;
    }

    public void setSpeed_time(BigDecimal speed_time) {
        this.speed_time = speed_time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}