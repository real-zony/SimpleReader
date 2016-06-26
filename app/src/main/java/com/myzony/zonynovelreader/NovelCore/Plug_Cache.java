package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myzony.zonynovelreader.Common.AppContext;
import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.cache.CacheManager;
import com.myzony.zonynovelreader.fragment.CacheNovelFragment;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo199 on 2016/6/24.
 */
public class Plug_Cache extends NovelCore {
    /**
     * 缓存队列上下文对象
     */
    public Context cacheContext;


    /**
     * 绑定响应对象的回调接口
     * @param callBack_cacheSaved 保存小说数据成功后的回调对象
     */
    public void bindCB_CacheSaved(Plug_CallBack_CacheSaved callBack_cacheSaved){
        this.callBack_cacheSaved = callBack_cacheSaved;
    }

    /**
     * 添加小说信息到缓存对象的列表
     * @param info 小说信息对象
     */
    public void addToList(NovelInfo info){
        infoList.add(info);
    }

    /**
     * 存储缓存，包括小说信息列表、章节列表、小说章节具体信息列表
     * @param context 上下文对象
     * @param list 章节信息列表
     * @param Title 小说名称
     */
    public void saveCache(Context context,ArrayList<ChapterInfo> list,String Title){
        callback_novel.call_Novel(infoList);
        this.chapterInfoList = list;
        new SaveCacheTask(context, (Serializable) infoList, CacheNovelFragment.NOVEL_CACHE_PREFIX).execute();
        new SaveCacheTask(context,chapterInfoList,CacheNovelFragment.NOVEL_CACHE_PREFIX + Title + "_").execute();
        new SaveCacheTask_NovelData(context,CacheNovelFragment.NOVEL_CACHE_PREFIX + Title + "_").execute();
    }

    /**
     * 异步缓存读取
     */
    private class ReadCacheTask extends AsyncTask<String, Void, Serializable> {
        protected final WeakReference<Context> mContext;

        protected ReadCacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected Serializable doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return seri;
            }
        }

        @Override
        protected void onPostExecute(Serializable list) {
            super.onPostExecute(list);
            if (list != null) {
                readCacheListSuccess(list);
            } else {
                readCacheListSuccess(null);
            }
        }
    }

    /**
     * 异步缓存读取章节列表数据
     */
    private class ReadCacheTask_ChapterList extends AsyncTask<String,Void,Serializable>{
        private WeakReference<Context> mContext;

        private ReadCacheTask_ChapterList(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected Serializable doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return seri;
            }
        }

        @Override
        protected void onPostExecute(Serializable list) {
            super.onPostExecute(list);
            if (list != null) {
                chapterInfoList = (ArrayList<ChapterInfo>)list;
                callBack_chapter.call_Chapter((ArrayList<ChapterInfo>) list);
            }
        }
    }

    /**
     * 异步缓存存储
     */
    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable serializable, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = serializable;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            saveCacheListSuccess(mContext.get());
        }
    }

    /**
     * 异步存储小说内容数据
     */
    private class SaveCacheTask_NovelData extends AsyncTask<Void,Void,Void>{
        private final WeakReference<Context> mContext;
        private final String key;
        private RequestQueue queue;
        private SaveCacheTask_NovelData(Context context,String key){
            mContext = new WeakReference<Context>(context);
            this.key = key;
            this.queue = Volley.newRequestQueue(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(int i = 0;i<chapterInfoList.size();i++){
                final int count = i;
                StringRequest stringRequest = new StringRequest(chapterInfoList.get(i).getUrl(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            String request = new String(s.getBytes("ISO-8859-1"), "gbk");
                            CacheManager.saveObject(mContext.get(), AppContext.getPlug().resolveData(request),
                                    key + chapterInfoList.get(count).getTitle());
                        }catch (UnsupportedEncodingException exp){
                            exp.printStackTrace();
                        }
                        check(count);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        CacheManager.saveObject(context,"空白章节",key + chapterInfoList.get(count).getTitle());
                        check(count);
                    }
                });
                stringRequest.setShouldCache(false);
                queue.add(stringRequest);
            }
            return null;
        }
    }

    /**
     * 存储小说成功
     * @param context
     */
    private void saveCacheListSuccess(Context context){
        Toast.makeText(context,"缓存小说信息与章节列表成功，开始缓存小说内容！",Toast.LENGTH_SHORT).show();
    }

    /**
     * 缓存读取成功
     * @param serializable 读取出来的序列化对象 LIST<T>
     */
    private void readCacheListSuccess(Serializable serializable) {
        if (serializable == null) {
            callback_novel.call_Novel(null);
            return;
        }
        List<NovelInfo> list = (List<NovelInfo>) serializable;
        if (list.size() == 0) {
            callback_novel.call_Novel(null);
        } else {
            callback_novel.call_Novel(list);
        }
    }

    /**
     * 从缓存当中获得数据
     * @param key 键
     */
    private void requestDataFromCache(String key) {
        new ReadCacheTask(cacheContext).execute(key);
    }

    /**
     * 是否存在章节缓存数据
     * @param key 小说名称
     * @param context Activity上下文对象引用
     * @return 是否存在，true则是存在，false则是不存在
     */
    public boolean isExitsCachedChapter(String key,Context context){
        return CacheManager.isExistDataCache(context,CacheNovelFragment.NOVEL_CACHE_PREFIX + key + "_");
    }

    /**
     * 检测小说数据是否缓存成功
     * @param i 索引
     */
    private void check(int i){
        if(i == chapterInfoList.size() - 1){
            callBack_cacheSaved.checkCacheSuccess(true);
        }
    }

    @Override
    public void getNovelUrl(String targetHTML, RequestQueue queue) {
        requestDataFromCache(CacheNovelFragment.NOVEL_CACHE_PREFIX);
    }
    @Override
    public void getChapterList(String novelUrl, Context context, RequestQueue queue) {
        new ReadCacheTask_ChapterList(context).execute(CacheNovelFragment.NOVEL_CACHE_PREFIX + novelUrl + "_");
    }
    @Override
    public void getNovelData(String url, RequestQueue queue) {
        String novelData = (String) CacheManager.readObject(cacheContext,url);
        callBack_read.call_Read(novelData);
    }
    @Override
    public String getSearchUrl(String searchKey, int page) {
        return null;
    }
    @Override
    public String getItemURL(int page) {
        return null;
    }
    @Override
    public String resolveData(String source) {
        return null;
    }
}
