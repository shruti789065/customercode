package com.adiacent.menarini.menarinimaster.core.utils;

import com.day.cq.wcm.api.NameConstants;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

public class Constants {
    public static final String RESOURCE_TYPE_PROPERTY= JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

    public static final String FEATURE_IMAGE_NODE_NAME="cq:featuredimage";

    public static final String TEMPLATE_PROPERTY= NameConstants.NN_TEMPLATE;

    public static final String EXTENTION_SEPARATOR=".";

    public static final String MP4_FILE_EXT="mp4";

    public static final String OGG_FILE_EXT="ogg";

    public static final String FORMAT_ANNO_MESE_GIORNO="yyyy - MM - dd";

    public static final String FORMAT_GIORNO_MESE_ANNO="dd - MM - YYYY";

    public static final String APPLICATION_JSON = "application/json";

    public static final String JSON = "json";

    public static String SERVICE_NAME = "menarinimaster";
    public static String INTERNAL_MENU_TEMPLATE_REGEXP = "/conf/[\\w-]+/settings/wcm/templates/(menarini---details-news|menarini---details-page|menarini---details-product|menarini---product-category|menarini---product-list-pharmaceutical|menarini---product-list-healthcare)";
}