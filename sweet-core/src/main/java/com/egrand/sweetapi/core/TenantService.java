package com.egrand.sweetapi.core;

public interface TenantService {

    /**
     * 租户信息头
     */
    String TENANT_HEADER = "x-tenant-header";

    /**
     * 获取租户标识
     * @return
     */
    default String getTenant() {
        return "";
    }

    /**
     * 设置租户标识
     */
    default void setTenant(String tenant) {

    }

    /**
     * 清除租户标识
     */
    default void clear() {

    }
}
