package com.egrand.sweetapi.core;

import java.util.List;

/**
 * 分页接口
 * @param <T>
 */
public interface IPageResult<T> {

    /**
     * 总记录数
     * @return
     */
    long getTotal();

    /**
     * 记录列表
     * @return
     */
    List<T> getRecords();

    /**
     * 当前页
     * @return
     */
    long getPage();

    /**
     * 总页
     * @return
     */
    long getPages();

    /**
     * 每页显示数量
     * @return
     */
    long getLimit();

    /**
     * 正排序，多个用逗号隔开
     * @return
     */
    String getAscs();

    /**
     * 倒排序，多个用逗号隔开
     * @return
     */
    String getDescs();
}
