package com.myzony.zonynovelreader.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myzony.zonynovelreader.Common.AppContext;
import com.myzony.zonynovelreader.NovelCore.Plug_CallBack_Chapter;
import com.myzony.zonynovelreader.NovelCore.Plug_Callback_Novel;
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.utils.RegexUtils;
import com.myzony.zonynovelreader.widget.TipInfoLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.InjectView;


/**
 * Created by mo199 on 2016/6/5.
 */
public class ChapterActivity extends BaseActivity implements Plug_CallBack_Chapter {
    public static String VIEW_CHAPTER_INFO = "view_chapter_info";

    private NovelInfo currentNovelInfo;
    private final Context context = this;
    private RequestQueue m_Queue;

    private ArrayList<String> chapter_list_url;
    private ArrayAdapter<String> chapter_list_title;

    @InjectView(R.id.chapter_listview)
    ListView chapterListView;

    @InjectView(R.id.chapter_tipinfo)
    TipInfoLayout tipInfoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getExtras() != null){
            currentNovelInfo = (NovelInfo) getIntent().getSerializableExtra(VIEW_CHAPTER_INFO);
        }
        toolbar.setTitle(currentNovelInfo.getName());
        toolbar.setSubtitle(currentNovelInfo.getAuthor());
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));
        // 初始化视图
        initView();
    }

    private void initView(){
        chapter_list_title = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        chapter_list_url = new ArrayList<String>();

        tipInfoLayout.setLoading();
        setListView(false);

        // 设置监听器
        // 阅读章节
        chapterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChapterActivity.this,ReadActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("pos",position);
                bundle.putString("url",chapter_list_url.get(position));
                bundle.putSerializable("url_List",chapter_list_url);
                bundle.putString("chapterName",chapter_list_title.getItem(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 加载章节
        AppContext.getPlug().bindCB_Chapter(this);
        AppContext.getPlug().getChapterList(currentNovelInfo.getUrl(),this,Volley.newRequestQueue(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.chapter_layout;
    }

    /**
     * 设置是否显示列表视图
     * @param visiable true为显示，false为不显示
     */
    private void setListView(boolean visiable){
        if(visiable){
            chapterListView.setVisibility(View.VISIBLE);
            tipInfoLayout.setVisibility(View.GONE);
        }else{
            chapterListView.setVisibility(View.GONE);
            tipInfoLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void call_Chapter(ArrayList<ChapterInfo> list) {
        // 章节排序
        Collections.sort(list, new Comparator<ChapterInfo>() {
            @Override
            public int compare(ChapterInfo lhs, ChapterInfo rhs) {
                return lhs.getId().compareTo(rhs.getId());
            }
        });
        // 循环添加到adapter
        for(int i=0;i<list.size();i++){
            chapter_list_title.add(list.get(i).getTitle());
            chapter_list_url.add(list.get(i).getUrl());
        }
        // 刷新列表
        chapterListView.setAdapter(chapter_list_title);
        setListView(true);
    }
}
