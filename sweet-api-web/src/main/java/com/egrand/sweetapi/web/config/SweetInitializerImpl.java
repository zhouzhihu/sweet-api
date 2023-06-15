package com.egrand.sweetapi.web.config;

import com.egrand.sweetapi.core.initialize.SweetInitializer;
import com.egrand.sweetapi.web.service.ApiService;
import com.egrand.sweetapi.web.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;

public class SweetInitializerImpl extends SweetInitializer {

    @Autowired
    private ApiService apiService;

    @Autowired
    private ConnectionService connectionService;

    @Override
    protected void initialize() {
        this.apiService.initialize();
        this.connectionService.initialize();
    }
}
