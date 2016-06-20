package com.myzony.zonynovelreader.fragment;

import android.app.Fragment;

import com.myzony.zonynovelreader.Common.NovelTab;
import com.myzony.zonynovelreader.R;

import java.util.List;

/**
 * Created by mo199 on 2016/5/28.
 */
public class MyNovelInfoTabFragment extends BaseTabViewpageFragment {
    @Override
    protected void initViewpagerFragmentList(List<android.support.v4.app.Fragment> viewpagerFragmentList, List<String> mTitles) {
        mTitles.add("推荐小说");
        viewpagerFragmentList.add(AllProjectFragment.newInstance(NovelTab.FeaturedNovel));

        //mTitles.add("本地小说");
        //viewpagerFragmentList.add(AllProjectFragment.newInstance(NovelTab.LocalNovel));
    }
}
