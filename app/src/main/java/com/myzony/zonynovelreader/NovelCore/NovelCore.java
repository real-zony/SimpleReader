package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.myzony.zonynovelreader.bean.NovelInfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mo199 on 2016/5/25.
 */
public abstract class NovelCore {
    /**
     * 小说Url列表
     */
    protected ArrayList<String>infoListUrl = new ArrayList<String>();
    /**
     * 小说信息列表
     */
    protected List<NovelInfo> infoList = new ArrayList<NovelInfo>();
    /**
     * 网络请求队列
     */
    protected RequestQueue mQueue;

    /**
     * 回调接口
     */
    protected Plug_Callback callback;

    /**
     * 绑定响应对象的回调接口
     * @param callback 目标回调对象
     */
    public void bindCallBack(Plug_Callback callback) {
        this.callback = callback;
    }

    /**
     * 获得小说Url列表
     * @param targetHTML 列表页面Html数据
     * @param queue 网络请求队列
     */
    public abstract void getNovelUrl(String targetHTML,RequestQueue queue);

    /**
     * 章节列表获取
     */
    //public abstract void getChapterList(String targetHTML);
}
