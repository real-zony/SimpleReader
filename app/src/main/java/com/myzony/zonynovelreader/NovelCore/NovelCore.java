package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.bean.NovelInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有Plug_xx的基类，其他阅读源请继承此基类。
 * Created by mo199 on 2016/5/25.
 */
public abstract class NovelCore {
    /**
     * 小说Url列表
     */
    protected ArrayList<String> infoListUrl = new ArrayList<String>();
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
     * 回调接口-小说数据
     */
    protected Plug_CallBack_Read callBack_read;
    /**
     * 回调接口-缓存成功
     */
    protected Plug_CallBack_CacheSaved callBack_cacheSaved;
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
     * 绑定响应对象回调接口
     * @param callBack_read
     */
    public void bindCB_Read(Plug_CallBack_Read callBack_read){
        this.callBack_read = callBack_read;
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

    /**
     * 获得小说内容
     * @param url 阅读页面url
     * @param queue 网络请求队列
     * @return 加载好的数据
     */
    public abstract void getNovelData(String url,RequestQueue queue);

    /**
     * 获得搜索URL
     * @param searchKey 搜索书籍的名字
     * @param page 页码
     * @return URL
     */
    public abstract String getSearchUrl(String searchKey,int page);

    /**
     * 推荐条目URL
     * @param page 页码
     * @return URL
     */
    public abstract String getItemURL(int page);

    /**
     * 清除容器
     */
    public void clear(){
        infoListUrl.clear();
        infoList.clear();
        chapterInfoList.clear();
    }

    /**
     * 提供小说源页面数据，解析完成后返回解析完成的字符串。
     * @return 解析成功的字符串
     */
    public abstract String resolveData(String source);

    /**
     * 获得章节列表
     * @return 章节列表
     */
    public final ArrayList<ChapterInfo> getChapterInfoList(){return chapterInfoList;}
}
