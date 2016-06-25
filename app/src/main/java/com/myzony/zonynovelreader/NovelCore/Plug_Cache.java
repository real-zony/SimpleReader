package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.cache.CacheManager;
import com.myzony.zonynovelreader.fragment.CacheNovelFragment;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by mo199 on 2016/6/24.
 */
public class Plug_Cache extends NovelCore {
    /**
     * 缓存队列上下文对象
     */
    public Context cacheContext;

    public void addToList(NovelInfo info){
        infoList.add(info);
    }

    public void saveCache(Context context){
        callback_novel.call_Novel(infoList);
        new SaveCacheTask(context, (Serializable) infoList, CacheNovelFragment.NOVEL_CACHE_PREFIX).execute();
    }

    /**
     * 异步缓存读取
     */
    private class ReadCacheTask extends AsyncTask<String, Void, Serializable> {
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context) {
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

    @Override
    public void getNovelUrl(String targetHTML, RequestQueue queue) {
        requestDataFromCache(CacheNovelFragment.NOVEL_CACHE_PREFIX);
    }
    @Override
    public void getChapterList(String novelUrl, Context context, RequestQueue queue) {}
    @Override
    public void getNovelData(String url, RequestQueue queue) {}
    @Override
    public String getSearchUrl(String searchKey, int page) {
        return null;
    }
    @Override
    public String getItemURL(int page) {
        return null;
    }
}
