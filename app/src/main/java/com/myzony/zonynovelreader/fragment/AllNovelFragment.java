package com.myzony.zonynovelreader.fragment;

import android.os.Bundle;

import com.myzony.zonynovelreader.Common.AppContext;

/**
 * Created by mo199 on 2016/5/28.
 */
public class AllNovelFragment extends BaseNovelsRefreshFragment {

    public static AllNovelFragment newInstance() {
        AllNovelFragment fragment = new AllNovelFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 获得推荐页面的URL
     * @param page
     * @return 获得到的URL
     */
    @Override
    protected String getItemURL(int page) {
        return AppContext.getPlug().getItemURL(page);
    }
}
