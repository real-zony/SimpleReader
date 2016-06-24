package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
            if(request.indexOf("class=\"t\">玄幻魔法") != -1){
                Matcher matcher = RegexUtils.newMatcher("/binfo/\\d+/\\d+.htm(?=\"><img)",request,false);
                while(matcher.find()){
                    String res = String.format("http://m.bxwx8.org%s",matcher.group().toString());
                    infoListUrl.add(res);
                }
            }else{
                if(request.indexOf("搜索小说") != -1){
                    Matcher matcher = RegexUtils.newMatcher("/binfo/\\d+/\\d+.htm",request,false);
                    while (matcher.find()){
                        String res = String.format("http://m.bxwx8.org%s",matcher.group().toString());
                        infoListUrl.add(res);
                    }
                }else{
                    Matcher matcher = RegexUtils.newMatcher("/b/\\d+/\\d+/\\d+.html",request,true);
                    String res = String.format("http://m.bxwx8.org%s",matcher.group().toString());
                    infoListUrl.add(res.replaceAll("/b","/binfo").replaceAll("/\\d+.html",".htm"));
                }
            }
            AppContext.PAGE_SIZE = infoListUrl.size();
            getNovelInfo();
        } catch (UnsupportedEncodingException exp) {
            return;
        }
    }

    @Override
    public void getChapterList(final String novelUrl, final Context context, RequestQueue queue) {
        chapterInfoList.clear();
        mQueue = queue;
        StringRequest stringRequest = new StringRequest(novelUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                    // 提取最大页码
                    Matcher matcher = RegexUtils.newMatcher("第\\d+页</option></", resquest, true);
                    Matcher matcherYeMa = RegexUtils.newMatcher("\\d+(?=页)", matcher.group().toString(), true);
                    // 加载章节
                    chapterLoad(Integer.parseInt(matcherYeMa.group().toString()), novelUrl + "p/");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "出现错误", Toast.LENGTH_LONG).show();
            }
        });
        mQueue.add(stringRequest);
    }

    @Override
    public void getNovelData(String url, RequestQueue queue) {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    String request = new String(s.getBytes("ISO-8859-1"), "gbk");
                    Matcher matcher = RegexUtils.newMatcher("<divid=\"nr1\">.+<divclass=\"nr_page\">", request.replaceAll("\\s*|\t|\n" +
                            "|\n",""), true);
                    readLoadCheck(matcher.group().toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                readLoadCheck(null);
            }
        });
        queue.add(stringRequest);
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
                                request.replaceAll("\\s*|\t|\r|\n", "").replaceAll("&nbsp;","").replaceAll("<br />","").replaceAll("<br/>",""), true);
                        info.setDescript(matcher_descript.group().toString().replaceAll("line-height:auto\">","").replaceAll("</div></div><divi",""));

                        Matcher matcher_imageUrl = RegexUtils.newMatcher("http://www.bxwx8.org/image/\\d+/\\d+/\\d+s.jpg", request, true);
                        // 设置图像
                        try{
                            info.setImageUrl(matcher_imageUrl.group().toString());
                        }catch (Exception exp){
                            info.setImageUrl("http://www.bxwx8.org/image/99/99491/99491s.jpg");
                        }
                        // 设置小说URL
                        info.setUrl(infoListUrl.get(count).replaceAll("binfo", "b").replaceAll(".htm","/"));
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
    /**
     * 检测章节列表是否加载完成
     *
     * @param currentPage 当前加载到的Page数目
     * @param targetPage  目标需要加载的Page数目
     */
    private void chapterLoadCheck(int currentPage, int targetPage) {
        if (currentPage == targetPage) {
            callBack_chapter.call_Chapter(chapterInfoList);
        }
    }
    /**
     * 加载小说章节列表
     *
     * @param page 最大页码
     * @param url  目录页面地址
     */
    private void chapterLoad(final int page, String url) {
        for (int i = 1; i < page + 1; i++) {
            final int count = i;
            StringRequest stringRequest = new StringRequest(String.format("%s%d.html", url, i), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        String request = new String(s.getBytes("ISO-8859-1"), "gbk");
                        Matcher matcher_filter = RegexUtils.newMatcher("全部章节.+20条/页",request.replaceAll("\\s*|\t|\n" +
                                "|\n",""),true);
                        // 提取小说章节URL
                        Matcher matcher_chapter = RegexUtils.newMatcher("/b/\\d+/\\d+/\\d+.html", matcher_filter.group().toString(), false);
                        // 标题
                        Matcher matcher_title = RegexUtils.newMatcher(".html'>.+", matcher_filter.group().toString().replaceAll("</a></div>","\n"), false);
                        while (matcher_chapter.find()) {
                            ChapterInfo title = new ChapterInfo();
                            title.setUrl(String.format("http://m.bxwx8.org%s", matcher_chapter.group().toString()));
                            // 提取章节id
                            Matcher matcher_id = RegexUtils.newMatcher("\\d+(?=.html)", title.getUrl(), true);
                            title.setId(Integer.parseInt(matcher_id.group().toString()));
                            // 提取标题
                            matcher_title.find();
                            title.setTitle(matcher_title.group().toString().replaceAll(".html'>", ""));
                            chapterInfoList.add(title);
                        }
                        chapterLoadCheck(count, page);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(context, "出现错误", Toast.LENGTH_LONG).show();
                }
            });
            mQueue.add(stringRequest);
        }
    }
    /**
     * 检测小说数据加载是否完成
     *
     * @param data 小说数据
     */
    private void readLoadCheck(String data) {
        callBack_read.call_Read(data);
    }
}
