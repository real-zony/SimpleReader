package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myzony.zonynovelreader.Common.AppContext;
import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.utils.RegexUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;

/**
 * Created by mo199 on 2016/6/23.
 */
public class Plug_bxwx extends NovelCore {
    @Override
    public void getNovelUrl(String targetHTML, RequestQueue queue) {
        this.mQueue = queue;
        // 清空容器
        infoList.clear();
        infoListUrl.clear();

        try{
            String request = new String(targetHTML.getBytes("ISO-8859-1"),"gbk");
            AppContext.PAGE_SIZE = 40;
            if(request.indexOf("玄幻魔法") != -1){
                Matcher matcher = RegexUtils.newMatcher("/binfo/\\d+/\\d+.htm(?=\"><img)",request,false);
                while(matcher.find()){
                    String res = String.format("http://m.bxwx8.org%s",matcher.group().toString());
                    infoListUrl.add(res);
                }
            }
            getNovelInfo();
        } catch (UnsupportedEncodingException exp) {
            return;
        }
    }

    @Override
    public void getChapterList(String novelUrl, Context context, RequestQueue queue) {

    }

    @Override
    public void getNovelData(String url, RequestQueue queue) {

    }

    @Override
    public String getSearchUrl(String searchKey, int page) {
        try {
            searchKey = URLEncoder.encode(searchKey,"gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format("http://m.bxwx8.org/modules/article/waps.php?=searchtype=articlename&searchkey=%s&t_btnsearch=",searchKey);
    }

    @Override
    public String getItemURL(int page) {
        return String.format("http://m.bxwx8.org/bsort1/0/%d.htm",page);
    }

    /**
     * 获得小说信息，根据URL列表
     */
    private void getNovelInfo() {
        for (int i = 0; i < infoListUrl.size(); i++) {
            final int count = i;
            StringRequest stringRequest = new StringRequest(infoListUrl.get(i), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        String request = new String(s.getBytes("ISO-8859-1"), "gbk");
                        NovelInfo info = new NovelInfo();
                        // 搜寻小说名字
                        Matcher matcher_name1 = RegexUtils.newMatcher("<div style=\"color:blue; font-weight:bold\">&nbsp;.+</div>", request, true);
                        Matcher matcher_name2 = RegexUtils.newMatcher("p;.+</div>", matcher_name1.group().toString(), true);
                        info.setName(matcher_name2.group().toString().replaceAll("p;", "").replaceAll("</div>",""));
                        // 搜寻作者名字
                        Matcher matcher_author = RegexUtils.newMatcher("作者：.+(?=</div>)", request, true);
                        info.setAuthor(matcher_author.group().toString().replaceAll("作者：",""));

                        // 搜寻更新信息
                        Matcher matcher_update = RegexUtils.newMatcher(".html\">.+</a>", request, true);
                        info.setUpdate(matcher_update.group().toString().replaceAll(".html\">","").replaceAll("</a>",""));
                        // 读取描述信息
                        Matcher matcher_descript = RegexUtils.newMatcher("line-height:auto\">.+<divi",
                                request.replaceAll("\\s*|\t|\r|\n", "").replaceAll("&nbsp;","").replaceAll("<br />",""), true);
                        info.setDescript(matcher_descript.group().toString().replaceAll("line-height:auto\">","").replaceAll("<br/>",""));

                        Matcher matcher_imageUrl = RegexUtils.newMatcher("http://www.bxwx8.org/image/\\d+/\\d+/\\d+s.jpg", request, true);
                        // 设置图像
                        try{
                            info.setImageUrl(matcher_imageUrl.group().toString());
                        }catch (Exception exp){
                            info.setImageUrl("http://www.bxwx8.org/image/99/99491/99491s.jpg");
                        }
                        // 设置小说URL
                        info.setUrl(infoListUrl.get(count).replaceAll("binfo", "b").replaceAll(".html","/"));
                        Log.i("infinifnifnifnifnifn", info.getUrl());
                        infoList.add(info);
                        novelLoadCheck();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    NovelInfo info = new NovelInfo();
                    info.setImageUrl("http://www.bxwx8.org/image/99/99491/99491s.jpg");
                    info.setAuthor("error");
                    info.setDescript("error");
                    info.setName("error");
                    info.setUpdate("error");
                    infoList.add(info);
                    novelLoadCheck();
                }
            });
            mQueue.add(stringRequest);
        }
    }
    /**
     * 检测小说列表是否全部加载，加载完成之后通知目标对象。
     */
    private void novelLoadCheck() {
        if (infoList.size() == infoListUrl.size()) {
            callback_novel.call_Novel(infoList);
        }
    }
}
