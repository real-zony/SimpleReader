package com.myzony.zonynovelreader.NovelCore;

import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.bean.NovelInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 回调接口，用于传递加载完成的List列表
 * Created by mo199 on 2016/6/5.
 */
public interface Plug_Callback_Novel {
    /**
     * 小说列表加载完成时调用
     * @param list 加载完成的List容器列表
     */
    void call_Novel(List<NovelInfo> list);
}
