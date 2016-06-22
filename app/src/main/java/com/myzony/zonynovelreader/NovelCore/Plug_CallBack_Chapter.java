package com.myzony.zonynovelreader.NovelCore;

import com.myzony.zonynovelreader.bean.ChapterInfo;

import java.util.ArrayList;

/**
 * Created by mo199 on 2016/6/22.
 */
public interface Plug_CallBack_Chapter {
    /**
     * 章节列表加载完成时调用
     * @param list 加载完成的List容器列表
     */
    void call_Chapter(ArrayList<ChapterInfo> list);
}
