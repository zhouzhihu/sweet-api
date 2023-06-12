package com.egrand.sweetapi.plugin.es.module;

import com.egrand.sweetapi.core.TenantService;
import org.frameworkset.elasticsearch.client.ClientInterface;

import java.util.function.Function;

public class BoundESModule extends ESModule {

    private String esKey = "";

    public BoundESModule(TenantService tenantService, String esKey) {
        super(tenantService);
        this.esKey = esKey;
    }

    @Override
    protected <T> T execute(Function<ClientInterface, T> function) {
        return function.apply(this.getClientInterface(this.esKey));
    }
}
