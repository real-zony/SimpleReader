package com.myzony.zonynovelreader.utils;

import java.io.File;

/**
 * Created by mo199 on 2016/6/7.
 */
public class FileUtils {
    /**
     * 转换文件大小
     * @param fileSize 原文件大小
     * @return 转换成功的文本
     */
    public static String formatFileSize(long fileSize){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获得目录下文件大小
     * @param dir 目录
     * @return 大小
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        // 获得目录下所有子文件
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 如果是目录递归其子目录继续统计
            }
        }

        return dirSize;
    }
}
