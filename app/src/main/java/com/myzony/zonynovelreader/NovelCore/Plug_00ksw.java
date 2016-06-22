package com.myzony.zonynovelreader.NovelCore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.myzony.zonynovelreader.bean.ChapterInfo;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.utils.RegexUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

/**
 * Created by mo199 on 2016/5/25.
 */
public class Plug_00ksw extends NovelCore {
    @Override
    public void bindCB_Novel(Plug_Callback_Novel callback_novel) {
        super.bindCB_Novel(callback_novel);
    }

    @Override
    public void bindCB_Chapter(Plug_CallBack_Chapter callBack_chapter) {
        super.bindCB_Chapter(callBack_chapter);
    }

    @Override
    public void bindCB_Read(Plug_CallBack_Read callBack_read) {
        super.bindCB_Read(callBack_read);
    }

    @Override
    public void getNovelUrl(String targetHTML, RequestQueue queue) {
        this.mQueue = queue;
        // 清空容器
        infoList.clear();
        infoListUrl.clear();

        try {
            String resquest = new String(targetHTML.getBytes("ISO-8859-1"), "gbk");
            if (targetHTML.indexOf("weekvisit") != -1) { // 推荐榜
                // 抓取作品URL地址
                Matcher matcher = RegexUtils.newMatcher("/html/\\d+/\\d+/", resquest, false);
                while (matcher.find()) {
                    // 重构URL
                    String res = String.format("http://m.00ksw.com%s", matcher.group().toString());
                    // 添加到列表
                    infoListUrl.add(res);
                }
            } else { // 搜索
                Matcher matcher = RegexUtils.newMatcher("http://.+.00ksw.com/html/\\d+/\\d+/", resquest, false);
                while (matcher.find()) {
                    if (!infoListUrl.contains(matcher.group().toString().replaceAll("www", "m"))) {
                        infoListUrl.add(matcher.group().toString().replaceAll("www", "m"));
                    }
                }
            }
            getNovelInfo();
        } catch (UnsupportedEncodingException exp) {
            return;
        }
    }

    @Override
    public void getChapterList(final String novelUrl, final Context context, RequestQueue queue) {
        chapterInfoList.clear();
        mQueue = queue;
        StringRequest stringRequest = new StringRequest(novelUrl.replaceAll("html", "ml").replaceAll("www", "m"), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                    // 提取最大页码
                    Matcher matcher = RegexUtils.newMatcher("第\\d+/\\d+页", resquest, true);
                    Matcher matcherYeMa = RegexUtils.newMatcher("\\d+(?=页)", matcher.group().toString(), true);
                    // 加载章节
                    chapterLoad(Integer.parseInt(matcherYeMa.group().toString()), novelUrl.replaceAll("html", "ml").replaceAll("www", "m"));
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
                    String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                    Matcher matcher = RegexUtils.newMatcher("<div id=\"nr1\">.+</div>", resquest, true);
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

    /**
     * 获得小说信息，根据URL列表
     */
    private void getNovelInfo() {
        for (int i = 0; i < infoListUrl.size(); i++) {
            StringRequest stringRequest = new StringRequest(infoListUrl.get(i), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                        NovelInfo info = new NovelInfo();
                        // 搜寻作者信息
                        Matcher matcher_author1 = RegexUtils.newMatcher("<a href=\"/author.+\">.+</a></p>", resquest, true);
                        Matcher matcher_author2 = RegexUtils.newMatcher("r/.+(?=\">)", matcher_author1.group().toString(), true);
                        info.setAuthor(matcher_author2.group().toString().replaceAll("r/", ""));
                        // 搜寻小说名字
                        Matcher matcher_name = RegexUtils.newMatcher("(?<=<p><a ><h2>).+(?=</h2></a></P>)", resquest, true);
                        info.setName(matcher_name.group().toString());

                        // 搜寻更新信息
                        Matcher matcher_update = RegexUtils.newMatcher("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", resquest, true);
                        info.setUpdate(matcher_update.group().toString());
                        // 读取描述信息
                        Matcher matcher_descript = RegexUtils.newMatcher("class=\"intro_info\">.+最新章节预览", resquest.replaceAll("\\s*|\t|\r|\n", ""), true);
                        info.setDescript(matcher_descript.group().toString().replaceAll("class=\"intro_info\">", "").replaceAll("最新章节预览", ""));
                        // 储存小说URL
                        Matcher matcher_imageUrl = RegexUtils.newMatcher("http://www.00ksw.com/img/\\d+/\\d+/\\d+s.jpg", resquest, true);
                        // 设置图像
                        info.setImageUrl(matcher_imageUrl.group().toString());
                        // 设置小说URL
                        info.setUrl(matcher_imageUrl.group().toString().replaceAll("\\d+s.jpg", "").replaceAll("img", "html"));
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
                    info.setImageUrl("http://www.00ksw.com/img/15/15223/15223s.jpg");
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
                    try {
                        String request = new String(s.getBytes("ISO-8859-1"), "gbk");
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
     * 检测小说数据加载是否完成
     *
     * @param data 小说数据
     */
    private void readLoadCheck(String data) {
        callBack_read.call_Read(data);
    }
}
