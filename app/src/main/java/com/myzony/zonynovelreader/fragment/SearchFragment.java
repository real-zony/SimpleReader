package com.myzony.zonynovelreader.fragment;

import android.os.Bundle;

import com.myzony.zonynovelreader.Common.AppContext;

/**
 * Created by mo199 on 2016/6/6.
 */
public class SearchFragment extends BaseNovelsRefreshFragment {
    private String SEARCH_PROJECT_CACHE_PREFIX = "project_search_";
    private String searchKey;

    @Override
    protected String getCacheKey() {
        return SEARCH_PROJECT_CACHE_PREFIX + searchKey + "_";
    }

    @Override
    protected String getItemURL(int page) {
        return AppContext.getPlug().getSearchUrl(searchKey,page);
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
        }
    }
}
