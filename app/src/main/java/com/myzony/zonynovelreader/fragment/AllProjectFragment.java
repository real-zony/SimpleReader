package com.myzony.zonynovelreader.fragment;

import android.os.Bundle;

/**
 * Created by mo199 on 2016/5/28.
 */
public class AllProjectFragment extends BaseProjectsRefreshFragment {
    private String ALL_PROJECT_CACHE_PREFIX = "project_all_";
    private String projectType;

    public static AllProjectFragment newInstance(String projectType) {
        AllProjectFragment fragment = new AllProjectFragment();
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
        if(page==1)
        {
            return String.format("http://m.00ksw.com/s_top_weekvisit/");
        }else{
            return String.format("http://m.00ksw.com/s_top_weekvisit/%d/",page);
        }
    }

    /**
     * 获得缓存Key
     * @return Key
     */
    @Override
    protected String getCacheKey() {
        return ALL_PROJECT_CACHE_PREFIX + projectType + "_";
    }
}
