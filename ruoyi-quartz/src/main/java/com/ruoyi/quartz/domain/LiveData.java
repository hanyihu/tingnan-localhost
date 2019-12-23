package com.ruoyi.quartz.domain;

/**
 * @author hanyihu
 * @title
 * @date 2019/12/22 9:51
 */
public class LiveData {
    private String tagKey ;
    private String value ;
    private String timestamp ;
    private int tagStatus ;

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(int tagStatus) {
        this.tagStatus = tagStatus;
    }
}
