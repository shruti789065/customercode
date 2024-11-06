package com.jakala.menarini.core.service.interfaces;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface JcrResolverServiceInterface {

    public ResourceResolver getResourceResolver() throws LoginException;
}
