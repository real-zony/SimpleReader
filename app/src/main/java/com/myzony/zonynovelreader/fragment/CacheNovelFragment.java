package com.myzony.zonynovelreader.fragment;

/**
 * Created by mo199 on 2016/6/24.
 */
public class CacheNovelFragment extends BaseNovelsRefreshFragment{
    public static String NOVEL_CACHE_PREFIX = "cache_novel_";

    public static CacheNovelFragment newInstance(){
        CacheNovelFragment fragment = new CacheNovelFragment();
        return fragment;
    }

    @Override
    protected String getItemURL(int page) {
        return NOVEL_CACHE_PREFIX;
    }
}
