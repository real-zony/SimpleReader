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
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.utils.RegexUtils;
import com.myzony.zonynovelreader.widget.TipInfoLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;

import butterknife.InjectView;


/**
 * Created by mo199 on 2016/6/5.
 */
public class ChapterActivity extends BaseActivity {
    public static String VIEW_CHAPTER_INFO = "view_chapter_info";

    /**
     * 当前小说
     */
    private NovelInfo currentNovelInfo;
    private final Context context = this;
    private RequestQueue m_Queue;

    private ArrayList<String> chapter_list_url;
    private ArrayAdapter<String> chapter_list_title;
    private ArrayList<Title> chapter_list_titleAndId;

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
        chapter_list_titleAndId = new ArrayList<Title>();

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
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        m_Queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(currentNovelInfo.getUrl().replaceAll("html", "ml").replaceAll("www","m"), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                    // 提取最大页码
                    Matcher matcher = RegexUtils.newMatcher("第\\d+/\\d+页",resquest,true);
                    Matcher matcherYeMa = RegexUtils.newMatcher("\\d+(?=页)",matcher.group().toString(),true);
                    // 加载章节
                    chapterLoad(Integer.parseInt(matcherYeMa.group().toString()),currentNovelInfo.getUrl().replaceAll("html", "ml").replaceAll("www","m"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context,"出现错误",Toast.LENGTH_LONG).show();
            }
        });
        m_Queue.add(stringRequest);
    }

    private void chapterLoad(final int page, String url){
        for(int i=1;i<page+1;i++){
            final int finalI = i;
            StringRequest stringRequest = new StringRequest(String.format("%s_%d%s",url.substring(0,url.length()-1),i,"/"), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        String request = new String(s.getBytes("ISO-8859-1"), "gbk");
                        // 提取小说章节URL
                        Matcher matcher_chapter = RegexUtils.newMatcher("/html/\\d+/\\d+/\\d+.html",request,false);
                        Matcher matcher_title = RegexUtils.newMatcher("html'>.+<span>",request,false);
                        while(matcher_chapter.find()){
                            Title title = new Title();
                            title.setUrl(String.format("http://m.00ksw.com/%s",matcher_chapter.group().toString()));
                            // 提取章节id
                            Matcher matcher_id = RegexUtils.newMatcher("\\d+(?=.html)",title.getUrl(),true);
                            title.setId(Integer.parseInt(matcher_id.group().toString()));
                            // 提取标题
                            matcher_title.find();
                            title.setTitle(matcher_title.group().toString().replaceAll("html'>","").replaceAll("<span>",""));
                            chapter_list_titleAndId.add(title);
                        }
                        check(finalI + 1,page);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(context,"出现错误",Toast.LENGTH_LONG).show();
                }
            });
            m_Queue.add(stringRequest);
        }
    }

    private void check(int currentPage,int targetPage){
        if (currentPage == targetPage) {
            // 章节排序
            Collections.sort(chapter_list_titleAndId, new Comparator<Title>() {
                @Override
                public int compare(Title lhs, Title rhs) {
                    return lhs.getId().compareTo(rhs.getId());
                }
            });
            // 循环添加到adapter
            for(int i=0;i<chapter_list_titleAndId.size();i++){
                chapter_list_title.add(chapter_list_titleAndId.get(i).getTitle());
                chapter_list_url.add(chapter_list_titleAndId.get(i).getUrl());
            }
            // 刷新列表
            chapterListView.setAdapter(chapter_list_title);
            setListView(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.chapter_layout;
    }

    private void setListView(boolean visiable){
        if(visiable){
            chapterListView.setVisibility(View.VISIBLE);
            tipInfoLayout.setVisibility(View.GONE);
        }else{
            chapterListView.setVisibility(View.GONE);
            tipInfoLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 章节绑定内容
     */
    private class Title{
        private String title;
        private Integer id;

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }

        private String Url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
