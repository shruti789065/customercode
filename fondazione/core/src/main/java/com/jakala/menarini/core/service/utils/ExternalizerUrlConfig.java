package com.jakala.menarini.core.service.utils;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "externalizerUlrConfig")
public @interface ExternalizerUrlConfig {

    @AttributeDefinition(name = "home_path")
    String home_path() default "content/fondazione/";

    @AttributeDefinition(name = "domain")
    String domain() default "localhost:4502";

    @AttributeDefinition(name = "schema")
    String schema() default "http";

    @AttributeDefinition(name = "socialUrls")
    String[] socialUrls();

    @AttributeDefinition(name = "isAuthor")
    boolean isAuthor() default true;


}
