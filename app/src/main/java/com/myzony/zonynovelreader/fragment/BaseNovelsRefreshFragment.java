package com.myzony.zonynovelreader.fragment;

import com.myzony.zonynovelreader.adapter.BaseStateRecyclerAdapter;
import com.myzony.zonynovelreader.adapter.NovelInfoAdapter;
import com.myzony.zonynovelreader.bean.NovelInfo;

import java.util.List;

/**
 * Created by mo199 on 2016/5/28.
 */
public abstract class BaseNovelsRefreshFragment extends BaseSwipeRefreshFragment<NovelInfo> {
    protected static final String PROJECT_TYPE = "project_type";

    @Override
    protected boolean itemCompareTo(List<NovelInfo> list, NovelInfo item) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (item.getId() == list.get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回RecylerView的适配器
     * @return Novel适配器
     */
    @Override
    protected BaseStateRecyclerAdapter getRecyclerAdapter() {
        BaseStateRecyclerAdapter mDataAdapter = new NovelInfoAdapter(getActivity());
        return mDataAdapter;
    }
}