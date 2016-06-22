package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.bean.NovelInfo;

import java.util.ArrayList;
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
     * 章节列表
     */
    protected ArrayList<ChapterInfo> chapterInfoList = new ArrayList<ChapterInfo>();
    /**
     * 网络请求队列
     */
    protected RequestQueue mQueue;
    /**
     * 回调接口-小说列表
     */
    protected Plug_Callback_Novel callback_novel;
    /**
     * 回调接口-章节列表
     */
    protected Plug_CallBack_Chapter callBack_chapter;
    /**
     * 用于Toast显示的上下文
     */
    protected Context context;



    /**
     * 绑定响应对象的回调接口
     * @param callback_novel 小说列表加载完成回调对象
     */
    public void bindCB_Novel(Plug_Callback_Novel callback_novel) {
        this.callback_novel = callback_novel;
    }

    /**
     * 绑定响应对象回调接口
     * @param callBack_chapter 章节列表加载完成对象
     */
    public void bindCB_Chapter(Plug_CallBack_Chapter callBack_chapter){
        this.callBack_chapter = callBack_chapter;
    }

    /**
     * 获得小说Url列表
     * @param targetHTML 列表页面Html数据
     * @param queue 网络请求队列
     */
    public abstract void getNovelUrl(String targetHTML,RequestQueue queue);

    /**
     * 章节列表获取
     * @param novelUrl 小说URL
     * @param context 调用的Activity上下文对象
     * @param queue 请求队列
     * @return 是否获取成功
     */
    public abstract void getChapterList(final String novelUrl,final Context context,RequestQueue queue);
}
