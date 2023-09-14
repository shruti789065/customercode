package com.adiacent.menarini.mhos.core.resources;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = ImportLibraryResource.Config.class)
@Component(immediate = true)
public class ImportLibraryResource {
    private static final Logger LOG = LoggerFactory.getLogger(ImportLibraryResource.class);

    private static ImportLibraryResource _instance = null;
    private Config config;

    @ObjectClassDefinition(name = "Menarini Import Library Servlet Config", description = "Menarini Import Library Servlet Config")
    public static @interface Config {

        @AttributeDefinition(name = "Excel file name", description = "Excel file name")
        String getFileName() default "";

        @AttributeDefinition(name = "Header Row in excel file", description = "Header Row in excel file")
        boolean isHeaderRowPresent() default false;

        @AttributeDefinition(name = "File Path", description = "File Path")
        String getFileDAMFolder() default "";


        @AttributeDefinition(name = "Tag Import Enabled", description = "Tag Import Enabled")
        boolean isImportTagEnabled() default false;


        @AttributeDefinition(name = "Article Import Enabled", description = "Tag Import Enabled")
        boolean isImportArticleEnabled() default false;

        @AttributeDefinition(name = "Tag Root Path", description = "getTagsRootPath")
        String getTagsRootPath() default "";

        @AttributeDefinition(name = "Username", description = "Username")
        String getUsername() default "";

        @AttributeDefinition(name = "Password", description = "Password")
        String getPwd() default "";


        @AttributeDefinition(name = "DAM Root Path", description = "getDamRootPath")
        String getDamRootPath() default "";

    }

    @Activate
    @Modified
    protected void activate(final Config config) {
        LOG.info("Activating Import Library  Resource");
        ImportLibraryResource._instance = this;
        this.config = config;

        LOG.debug("Config dam  path:{}", this.config.getFileDAMFolder());
        LOG.debug("Config article import enabled :{}", this.config.isImportArticleEnabled());
        LOG.debug("Config  tag import enabled{}", this.config.isImportTagEnabled());

    }


    /**
     * @return the _instance
     */
    public static ImportLibraryResource get_instance() {
        return _instance;
    }



    public Config getConfig() {
        return config;
    }
}
