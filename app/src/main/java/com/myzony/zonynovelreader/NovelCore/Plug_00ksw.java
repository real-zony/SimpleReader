package com.myzony.zonynovelreader.NovelCore;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.myzony.zonynovelreader.bean.NovelInfo;
import com.myzony.zonynovelreader.utils.RegexUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

/**
 * Created by mo199 on 2016/5/25.
 */
public class Plug_00ksw extends NovelCore {

    /**
     * 绑定响应对象的回调接口
     * @param callback 目标回调对象
     */
    @Override
    public void bindCallBack(Plug_Callback callback) {
        super.bindCallBack(callback);
    }

    /**
     * 获得小说Url列表
     * @param targetHTML 列表页面Html数据
     * @param queue 网络请求队列
     */
    @Override
    public void getNovelUrl(String targetHTML, RequestQueue queue) {
        this.mQueue = queue;
        // 清空容器
        infoList.clear();
        infoListUrl.clear();

        try {
            String resquest = new String(targetHTML.getBytes("ISO-8859-1"), "gbk");
            if(targetHTML.indexOf("weekvisit") != -1){ // 推荐榜
                // 抓取作品URL地址
                Matcher matcher = RegexUtils.newMatcher("/html/\\d+/\\d+/", resquest,false);
                while (matcher.find()) {
                    // 重构URL
                    String res = String.format("http://m.00ksw.com%s", matcher.group().toString());
                    // 添加到列表
                    infoListUrl.add(res);
                }
            }else{ // 搜索
                Matcher matcher = RegexUtils.newMatcher("http://.+.00ksw.com/html/\\d+/\\d+/",resquest,false);
                while (matcher.find()){
                    if(!infoListUrl.contains(matcher.group().toString().replaceAll("www","m"))){
                        infoListUrl.add(matcher.group().toString().replaceAll("www","m"));
                    }
                }
            }
            getNovelInfo();
        } catch (UnsupportedEncodingException exp) {
            return;
        }

        return;
    }

    /**
     * 获得小说信息，根据URL列表
     */
    private void getNovelInfo(){
        for (int i = 0; i < infoListUrl.size(); i++) {
            StringRequest stringRequest = new StringRequest(infoListUrl.get(i), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        String resquest = new String(s.getBytes("ISO-8859-1"), "gbk");
                        NovelInfo info = new NovelInfo();
                        // 搜寻作者信息
                        Matcher matcher_author1 = RegexUtils.newMatcher("<a href=\"/author.+\">.+</a></p>", resquest,true);
                        Matcher matcher_author2 = RegexUtils.newMatcher("r/.+(?=\">)", matcher_author1.group().toString(),true);
                        info.setAuthor(matcher_author2.group().toString().replaceAll("r/", ""));
                        // 搜寻小说名字
                        Matcher matcher_name = RegexUtils.newMatcher("(?<=<p><a ><h2>).+(?=</h2></a></P>)", resquest,true);
                        info.setName(matcher_name.group().toString());

                        // 搜寻更新信息
                        Matcher matcher_update = RegexUtils.newMatcher("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", resquest,true);
                        info.setUpdate(matcher_update.group().toString());
                        // 读取描述信息
                        Matcher matcher_descript = RegexUtils.newMatcher("class=\"intro_info\">.+最新章节预览", resquest.replaceAll("\\s*|\t|\r|\n", ""),true);
                        info.setDescript(matcher_descript.group().toString().replaceAll("class=\"intro_info\">", "").replaceAll("最新章节预览", ""));
                        // 储存小说URL
                        Matcher matcher_imageUrl = RegexUtils.newMatcher("http://www.00ksw.com/img/\\d+/\\d+/\\d+s.jpg", resquest,true);
                        // 设置图像
                        info.setImageUrl(matcher_imageUrl.group().toString());
                        // 设置小说URL
                        info.setUrl(matcher_imageUrl.group().toString().replaceAll("\\d+s.jpg", "").replaceAll("img", "html"));
                        Log.i("infinifnifnifnifnifn", info.getUrl());
                        infoList.add(info);
                        check();
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
                    check();
                }
            });
            mQueue.add(stringRequest);
        }
    }

    /**
     * 检测是否全部加载，加载完成之后通知目标对象。
     */
    private void check() {
        if (infoList.size() == infoListUrl.size()) {
            callback.call(infoList);
        }
    }
}
