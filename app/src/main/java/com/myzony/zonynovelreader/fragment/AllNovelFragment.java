package com.myzony.zonynovelreader.fragment;

import android.os.Bundle;

import com.myzony.zonynovelreader.Common.AppContext;

/**
 * Created by mo199 on 2016/5/28.
 */
public class AllNovelFragment extends BaseNovelsRefreshFragment {
    private String ALL_PROJECT_CACHE_PREFIX = "project_all_";
    private String projectType;

    public static AllNovelFragment newInstance(String projectType) {
        AllNovelFragment fragment = new AllNovelFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_TYPE, projectType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectType = getArguments().getString(PROJECT_TYPE);
        }
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
