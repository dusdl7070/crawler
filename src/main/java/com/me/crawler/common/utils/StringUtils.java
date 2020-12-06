package com.me.crawler.common.utils;

import org.springframework.util.ObjectUtils;

public class StringUtils {

    public static boolean isNullOrEmpty (String s) {
        if (ObjectUtils.isEmpty(s) || s.equals("")) return true;

        return false;
    }
}
