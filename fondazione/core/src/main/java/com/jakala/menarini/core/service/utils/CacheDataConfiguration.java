package com.jakala.menarini.core.service.utils;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@ObjectClassDefinition(name = "CacheDataConfiguration")
public @interface CacheDataConfiguration {

    @AttributeDefinition(name = "maxSize")
    int maxSize();
    @AttributeDefinition(name = "maxTtl")
    int maxTtl();

}
