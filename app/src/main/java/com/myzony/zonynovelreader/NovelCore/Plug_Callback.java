package com.myzony.zonynovelreader.NovelCore;

import com.myzony.zonynovelreader.bean.NovelInfo;

import java.util.List;

/**
 * 回调接口，用于传递加载完成的List列表
 * Created by mo199 on 2016/6/5.
 */
public interface Plug_Callback {
    /**
     * 加载完成时调用
     * @param list 加载完毕的List列表
     */
    void call(List<NovelInfo> list);
}
