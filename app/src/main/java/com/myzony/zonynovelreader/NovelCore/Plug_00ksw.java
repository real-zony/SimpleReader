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
import com.myzony.zonynovelreader.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;

/**
 * Created by mo199 on 2016/5/25.
 */
public class Plug_00ksw extends NovelCore {
    @Override
    public void getNovelUrl(String targetHTML, RequestQueue queue) {
        this.mQueue = queue;
        clear();

        String request = StringUtils.encodingConvert(targetHTML,"gbk");
        if (targetHTML.indexOf("weekvisit") != -1) { // 推荐榜
            // 抓取作品URL地址
            Matcher matcher = RegexUtils.newMatcher("/html/\\d+/\\d+/", request, false);
            while (matcher.find()) {
                // 重构URL
                String res = String.format("http://m.00ksw.com%s", matcher.group().toString());
                // 添加到列表
                infoListUrl.add(res);
            }
        } else { // 搜索
            Matcher matcher = RegexUtils.newMatcher("http://.+.00ksw.com/html/\\d+/\\d+/", request, false);
            while (matcher.find()) {
                if (!infoListUrl.contains(matcher.group().toString().replaceAll("www", "m"))) {
                    infoListUrl.add(matcher.group().toString().replaceAll("www", "m"));
                }
            }
        }
        AppContext.PAGE_SIZE = infoListUrl.size();
        getNovelInfo();
    }

    @Override
    public void getChapterList(final String novelUrl, final Context context, RequestQueue queue) {
        chapterInfoList.clear();
        mQueue = queue;
        StringRequest stringRequest = new StringRequest(novelUrl.replaceAll("html", "ml").replaceAll("www", "m"), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String request = StringUtils.encodingConvert(s,"gbk");
                // 提取最大页码
                Matcher matcher = RegexUtils.newMatcher("第\\d+/\\d+页", request, true);
                Matcher matcherYeMa = RegexUtils.newMatcher("\\d+(?=页)", matcher.group().toString(), true);
                // 加载章节
                chapterLoad(Integer.parseInt(matcherYeMa.group().toString()), novelUrl.replaceAll("html", "ml").replaceAll("www", "m"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "出现错误", Toast.LENGTH_LONG).show();
            }
        });
        stringRequest.setShouldCache(false);
        mQueue.add(stringRequest);
    }

    @Override
    public void getNovelData(String url, RequestQueue queue) {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String request = StringUtils.encodingConvert(s,"gbk");
                callBack_read.call_Read(resolveData(request));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callBack_read.call_Read(null);
            }
        });
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    @Override
    public String getSearchUrl(String searchKey,int page) {
        // URL编码
        try {
            searchKey = URLEncoder.encode(searchKey,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format("http://zhannei.baidu.com/cse/search?q=%s&p=%d&s=13150090723783341603",searchKey,page-1);
    }

    @Override
    public String getItemURL(int page) {
        if(page==1)
        {
            return String.format("http://m.00ksw.com/s_top_weekvisit/");
        }else{
            return String.format("http://m.00ksw.com/s_top_weekvisit/%d/",page);
        }
    }

    @Override
    public String resolveData(String source) {
        Matcher matcher = RegexUtils.newMatcher("<div id=\"nr1\">.+</div>", source, true);
        return matcher.group().toString();
    }

    /**
     * 获得小说信息，根据URL列表
     */
    private void getNovelInfo() {
        for (int i = 0; i < infoListUrl.size(); i++) {
            StringRequest stringRequest = new StringRequest(infoListUrl.get(i), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    String request = StringUtils.encodingConvert(s,"gbk");
                    NovelInfo info = new NovelInfo();
                    // 搜寻作者信息
                    Matcher matcher_author1 = RegexUtils.newMatcher("<a href=\"/author.+\">.+</a></p>", request, true);
                    Matcher matcher_author2 = RegexUtils.newMatcher("r/.+(?=\">)", matcher_author1.group().toString(), true);
                    info.setAuthor(matcher_author2.group().toString().replaceAll("r/", ""));
                    // 搜寻小说名字
                    Matcher matcher_name = RegexUtils.newMatcher("(?<=<p><a ><h2>).+(?=</h2></a></P>)", request, true);
                    info.setName(matcher_name.group().toString());

                    // 搜寻更新信息
                    Matcher matcher_update = RegexUtils.newMatcher("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", request, true);
                    info.setUpdate(matcher_update.group().toString());
                    // 读取描述信息
                    Matcher matcher_descript = RegexUtils.newMatcher("class=\"intro_info\">.+最新章节预览", request.replaceAll("\\s*|\t|\r|\n", ""), true);
                    info.setDescript(matcher_descript.group().toString().replaceAll("class=\"intro_info\">", "").replaceAll("最新章节预览", ""));
                    // 储存小说URL
                    Matcher matcher_imageUrl = RegexUtils.newMatcher("http://www.00ksw.com/img/\\d+/\\d+/\\d+s.jpg", request, true);
                    // 设置图像
                    info.setImageUrl(matcher_imageUrl.group().toString());
                    // 设置小说URL
                    info.setUrl(matcher_imageUrl.group().toString().replaceAll("\\d+s.jpg", "").replaceAll("img", "html"));
                    infoList.add(info);
                    novelLoadCheck();
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
            stringRequest.setShouldCache(false);
            mQueue.add(stringRequest);
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
            StringRequest stringRequest = new StringRequest(String.format("%s_%d%s", url.substring(0, url.length() - 1), i, "/"), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    String request = StringUtils.encodingConvert(s,"gbk");
                    // 提取小说章节URL
                    Matcher matcher_chapter = RegexUtils.newMatcher("/html/\\d+/\\d+/\\d+.html", request, false);
                    Matcher matcher_title = RegexUtils.newMatcher("html'>.+<span>", request, false);
                    while (matcher_chapter.find()) {
                        ChapterInfo title = new ChapterInfo();
                        title.setUrl(String.format("http://m.00ksw.com/%s", matcher_chapter.group().toString()));
                        // 提取章节id
                        Matcher matcher_id = RegexUtils.newMatcher("\\d+(?=.html)", title.getUrl(), true);
                        title.setId(Integer.parseInt(matcher_id.group().toString()));
                        // 提取标题
                        matcher_title.find();
                        title.setTitle(matcher_title.group().toString().replaceAll("html'>", "").replaceAll("<span>", ""));
                        chapterInfoList.add(title);
                    }
                    chapterLoadCheck(count, page);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(context, "出现错误", Toast.LENGTH_LONG).show();
                }
            });
            stringRequest.setShouldCache(false);
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
}
