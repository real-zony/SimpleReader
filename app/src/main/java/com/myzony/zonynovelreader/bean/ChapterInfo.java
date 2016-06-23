package com.myzony.zonynovelreader.bean;

import java.io.Serializable;

/**
 * 章节信息
 * Created by mo199 on 2016/6/21.
 */
public class ChapterInfo implements Serializable{
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String url;
    private String title;
    private Integer id;
}
