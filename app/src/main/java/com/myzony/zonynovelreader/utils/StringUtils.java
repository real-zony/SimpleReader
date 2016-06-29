package com.myzony.zonynovelreader.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by mo199 on 2016/6/29.
 */
public class StringUtils {
    public static String encodingConvert(String source,String charset){
        String request;
        try{
            request = new String(source.getBytes("ISO-8859-1"),charset);
        }catch (UnsupportedEncodingException exp){
            request = "UnsupportedEncodingException";
        }
        return request;
    }
}
