package com.myzony.zonynovelreader.UI;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.utils.RegexUtils;
import com.myzony.zonynovelreader.widget.TipInfoLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;

import butterknife.InjectView;

/**
 * Created by mo199 on 2016/6/5.
 */
public class ReadActivity extends BaseActivity{

    @InjectView(R.id.webView)
    WebView webView;
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfoLayout;

    // 章节URL
    private String chapter_url;
    // 章节列表
    private ArrayList<String> chapter_list_url;
    private int currentChapterPos;
    private RequestQueue mQueue;

    @Override
    protected int getLayoutView() {
        return R.layout.read_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent()!=null){
            Bundle bundle = getIntent().getExtras();
            chapter_url = bundle.getString("url");
            currentChapterPos = bundle.getInt("pos");
            chapter_list_url = (ArrayList<String>) bundle.getSerializable("url_List");
        }

        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));
        mQueue = Volley.newRequestQueue(this);
        initView();
        loadData();
    }

    private void initView() {
        setWebView(false);
        tipInfoLayout.setLoading();
        // 重试
        tipInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWebView(false);
                tipInfoLayout.setLoading();
                loadData();
            }
        });
    }

    private void loadData() {
        StringRequest stringRequest = new StringRequest(chapter_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                    Matcher matcher = RegexUtils.newMatcher("<div id=\"nr1\">.+</div>",resquest,true);
                    setWebView(true);
                    webView.loadDataWithBaseURL(null, matcher.group().toString(), "text/html", "utf-8", null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setWebView(false);
                tipInfoLayout.setLoadError("加载失败，请点击重试");
            }
        });

        mQueue.add(stringRequest);
    }

    private void setWebView(boolean visiable){
        if(visiable){
            webView.setVisibility(View.VISIBLE);
            tipInfoLayout.setVisibility(View.GONE);
        }else{
            webView.setVisibility(View.GONE);
            tipInfoLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.read_next:
                if(currentChapterPos != chapter_list_url.size()){
                    currentChapterPos++;
                }
                getNovel(chapter_list_url.get(currentChapterPos));
                break;
            case R.id.read_up:
                if(currentChapterPos == 0){
                    getNovel(chapter_list_url.get(currentChapterPos));
                }else{
                    currentChapterPos--;
                    getNovel(chapter_list_url.get(currentChapterPos));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNovel(String url){
        // 加载
        setWebView(false);
        tipInfoLayout.setLoading();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                    Matcher matcher = RegexUtils.newMatcher("<div id=\"nr1\">.+</div>", resquest,true);
                    setWebView(true);
                    webView.loadDataWithBaseURL(null, matcher.group().toString(), "text/html", "utf-8", null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setWebView(false);
                tipInfoLayout.setLoadError("加载失败，请点击重试");
            }
        });
        mQueue.add(stringRequest);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }

    // 创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read,menu);
        return true;
    }
}
