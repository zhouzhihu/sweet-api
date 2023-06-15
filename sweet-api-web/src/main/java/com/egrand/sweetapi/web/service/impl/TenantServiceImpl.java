package com.egrand.sweetapi.web.service.impl;

import com.egrand.sweetapi.core.TenantService;
import org.springframework.stereotype.Component;

@Component
public class TenantServiceImpl implements TenantService {

    @Override
    public String getTenant() {
        return "";
    }

    @Override
    public void setTenant(String tenant) {

    }

    @Override
    public void clear() {
    }
}
