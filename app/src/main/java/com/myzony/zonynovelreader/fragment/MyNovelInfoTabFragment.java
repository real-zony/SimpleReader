package com.myzony.zonynovelreader.fragment;

import com.myzony.zonynovelreader.Common.NovelTab;
import android.support.v4.app.Fragment;
import java.util.List;

/**
 * Created by mo199 on 2016/5/28.
 */
public class MyNovelInfoTabFragment extends BaseTabViewpageFragment {
    @Override
    protected void initViewpagerFragmentList(List<Fragment> viewpagerFragmentList, List<String> mTitles) {
        mTitles.add("推荐小说");
        viewpagerFragmentList.add(AllNovelFragment.newInstance(NovelTab.FeaturedNovel));
        mTitles.add("缓存的小说");
        //viewpagerFragmentList.add(AllNovelFragment.newInstance(NovelTab.CacheNovel));
        mTitles.add("本地TXT小说");
        //viewpagerFragmentList.add(AllNovelFragment.newInstance(NovelTab.LocalNovel));
    }
}
