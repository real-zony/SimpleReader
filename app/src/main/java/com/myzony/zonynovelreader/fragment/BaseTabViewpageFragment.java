package com.myzony.zonynovelreader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.adapter.ViewpagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mo199 on 2016/5/28.
 */
public abstract class BaseTabViewpageFragment extends Fragment {

    @InjectView(R.id.tab_layout)
    TabLayout tabLayout;

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    private List<Fragment> viewpagerFragmentList;
    private List<String> mTitles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewpagerFragmentList = new ArrayList<>();
        mTitles = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_viewpager_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewpagerFragmentList(viewpagerFragmentList, mTitles);

        ViewpagerAdapter adapter = new ViewpagerAdapter(getActivity().getSupportFragmentManager(), viewpagerFragmentList, mTitles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    protected abstract void initViewpagerFragmentList(List<Fragment> viewpagerFragmentList, List<String> mTitles);

}
