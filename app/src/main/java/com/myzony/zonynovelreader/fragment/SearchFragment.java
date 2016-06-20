package com.myzony.zonynovelreader.fragment;

import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by mo199 on 2016/6/6.
 */
public class SearchFragment extends BaseProjectsRefreshFragment {
    private String SEARCH_PROJECT_CACHE_PREFIX = "project_search_";
    private String searchKey;

    @Override
    protected String getCacheKey() {
        return SEARCH_PROJECT_CACHE_PREFIX + searchKey + "_";
    }

    @Override
    protected String getItemURL(int page) {
        return String.format("http://zhannei.baidu.com/cse/search?q=%s&p=%d&s=13150090723783341603",searchKey,page-1);
    }

    public static SearchFragment newInstance(String searchKey) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_TYPE, searchKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchKey = getArguments().getString(PROJECT_TYPE);
            // URL编码
            try {
                searchKey = URLEncoder.encode(searchKey,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
