package com.jakala.menarini.core.service.utils;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EventListingServiceConfiguration")
public @interface EventListingServiceConfiguration {


    @AttributeDefinition(name = "event_path")
    String eventPath();
    @AttributeDefinition(name = "topic_path")
    String topicPath();
    @AttributeDefinition(name = "city_path")
    String cityPath();
    @AttributeDefinition(name = "start_date_property")
    String startDateProperty();
    @AttributeDefinition(name = "end_date_property")
    String endDateProperty();
    @AttributeDefinition(name = "base_url")
    String baseUrl();




}
