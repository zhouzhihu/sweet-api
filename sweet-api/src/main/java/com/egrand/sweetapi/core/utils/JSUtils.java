package com.egrand.sweetapi.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * JS帮助工具
 */
public class JSUtils {

    /**
     * 将一个List对象Object元素转换为Long类型
     * @param sourceList 源List
     * @return
     */
    public static List<Long> toLong(List sourceList) {
        if (null != sourceList && sourceList.size() != 0) {
            List<Long> targetList = new ArrayList<>();
            sourceList.forEach(source -> targetList.add(Long.parseLong(String.valueOf(source))));
            return targetList;
        }
        return null;
    }

    /**
     * 将Object对象转为Long类型
     * @param source
     * @return
     */
    public static Long toLong(Object source) {
        return null != source ? Long.parseLong(String.valueOf(source)) : null;
    }
}
