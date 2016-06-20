package com.myzony.zonynovelreader.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类。
 * Created by mo199 on 2016/5/28.
 */
public class RegexUtils {
    /**
     * 根据正则表达式创建一个新的Matcher
     *
     * @param regex  正则表达式文本
     * @param source 要搜寻的文本
     * @param flags  true表示执行Find操作，false则表示不执行
     * @return 创建好的Matcher
     */
    public static Matcher newMatcher(String regex, String source, boolean flags) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        if(flags)matcher.find();
        return matcher;
    }
}
