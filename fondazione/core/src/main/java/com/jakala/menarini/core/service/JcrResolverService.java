package com.jakala.menarini.core.service;

import com.jakala.menarini.core.service.interfaces.JcrResolverServiceInterface;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Map;

@Component(service = JcrResolverServiceInterface.class)
public class JcrResolverService implements JcrResolverServiceInterface {

    @Reference
    private  ResourceResolverFactory resolverFactory;
    private static final String SERVICE = "data-migration-service";


    @Override
    public  ResourceResolver getResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
        return resolverFactory.getServiceResourceResolver(param);
    }

}
