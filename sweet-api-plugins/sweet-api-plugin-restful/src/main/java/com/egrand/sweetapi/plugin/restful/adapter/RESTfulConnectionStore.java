package com.egrand.sweetapi.plugin.restful.adapter;

import java.util.Map;

public interface RESTfulConnectionStore {

    /**
     * 获取所有RESTful连接
     * @return
     */
    Map<String, RESTfulConnectionInfo> getRestfulMap();

    /**
     * 是否包含key
     * @param key 关键字
     * @return
     */
    Boolean containsKey(String key);
}
