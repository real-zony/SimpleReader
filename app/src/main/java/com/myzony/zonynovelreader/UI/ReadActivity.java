package com.myzony.zonynovelreader.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.myzony.zonynovelreader.Common.AppContext;
import com.myzony.zonynovelreader.NovelCore.Plug_CallBack_Read;
import com.myzony.zonynovelreader.R;
import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.cache.CacheManager;
import com.myzony.zonynovelreader.widget.TipInfoLayout;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by mo199 on 2016/6/5.
 */
public class ReadActivity extends BaseActivity implements Plug_CallBack_Read{

    @InjectView(R.id.webView)
    WebView webView;
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfoLayout;

    /**
     * 章节列表
     */
    private ArrayList<ChapterInfo> chapterInfoArrayList;
    /**
     * 当前章节位置
     */
    private Integer currentChapterPos;
    /**
     * 当前小说URL
     */
    private String currentNovelUrl;
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
            currentChapterPos = bundle.getInt("pos");
            chapterInfoArrayList = (ArrayList<ChapterInfo>) bundle.getSerializable("chapterInfoList");
            currentNovelUrl = bundle.getString("novelUrl");
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
        toolbar.setSubtitle(chapterInfoArrayList.get(currentChapterPos).getTitle());

        AppContext.getPlug().bindCB_Read(this);
        AppContext.getPlug().getNovelData(chapterInfoArrayList.get(currentChapterPos).getUrl(),mQueue);
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
                savePos();
                finish();
                return true;
            // 下一章
            case R.id.read_next:
                if(currentChapterPos != chapterInfoArrayList.size()){
                    currentChapterPos++;
                }
                break;
            // 上一章
            case R.id.read_up:
                if(currentChapterPos != 0) {
                    currentChapterPos--;
                }
                break;
        }
        loadData();
        return super.onOptionsItemSelected(item);
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

    @Override
    public void call_Read(String data) {
        if(data != null){
            setWebView(true);
            webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        }else{
            setWebView(false);
            tipInfoLayout.setLoadError("加载失败，请点击重试");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            savePos();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void savePos(){
        String cacheKey = ChapterActivity.VIEW_CHAPTER_INFO + currentNovelUrl.replace("/","") + "_" +AppContext.flags;
        CacheManager.saveObject(this,currentChapterPos,cacheKey);
    }
}
