package com.myzony.zonynovelreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo199 on 2016/5/29.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {
    protected List<T> mData;
    protected Context context;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
        this.mData = new ArrayList<T>();
    }

    /**
     * 清空适配器容器数据
     */
    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加数据到mData头部
     * @param list 要添加的List
     */
    public void addDataSetToStart(List<T> list) {
        mData.addAll(0, list);
        notifyDataSetChanged();
    }

    /**
     * 将List数据添加到源mData尾部
     * @param list 要添加的List
     */
    public void addDataSetToEnd(List<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 清空List数据并且重新绑定新的List容器
     * @param list 要添加的List
     */
    public void resetDataSet(List<T> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 获得适配器容器
     * @return 容器引用
     */
    public List<T> getDataSet() {
        return mData;
    }

    /**
     * 获得适配器条目
     * @return 存在的数据条目
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
