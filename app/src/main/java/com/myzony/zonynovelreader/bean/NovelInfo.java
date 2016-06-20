package com.myzony.zonynovelreader.bean;

import java.io.Serializable;

/**
 * 小说信息对象
 * Created by mo199 on 2016/5/25.
 */
public class NovelInfo implements Serializable{
    /**
     * 小说作者
     */
    private String Name;
    /**
     * 小说作者
     */
    private String Author;
    /**
     * 小说更新日期
     */
    private String Update;
    /**
     * 小说URL
     */
    private String Url;
    /**
     * 小说描述信息
     */
    private String descript;
    /**
     * 小说图像URL
     */
    private String ImageUrl;
    private int id;

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getUpdate() {
        return Update;
    }

    public void setUpdate(String update) {
        Update = update;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
