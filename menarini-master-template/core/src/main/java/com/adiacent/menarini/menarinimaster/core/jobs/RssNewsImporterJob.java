package com.adiacent.menarini.menarinimaster.core.jobs;

import com.adiacent.menarini.menarinimaster.core.services.RssNewsImporter;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = RssNewsImporterJob.Config.class)
@Component(
        service = Runnable.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Menarini RSS Feed News Importer Job",
                Constants.SERVICE_VENDOR + "=Adiacent"
        },
        immediate = true
)
/**
 * Cron Job service to import product definition into jcr.
 */
public class RssNewsImporterJob implements Runnable, Cloneable{

    private static final Logger logger = LoggerFactory.getLogger(RssNewsImporterJob.class);


    @Reference
    private RssNewsImporter instance;


    @Activate
    @Modified
    protected void activate(final Config config) {
        logger.info("Activating Menarini RSS Feed News Importer  Cron Job");
        logger.info("RSS Feed News Importer instance is null {}",(instance==null));
    }

    public RssNewsImporter getImporterIstance(){
        return instance;
    }
    @Override
    public void run() {
        try {
            logger.info("Starting Menarini RSS Feed News Importer...");
            RssNewsImporter importerClone = (RssNewsImporter) getImporterIstance().clone();
            importerClone.start();
        } catch (CloneNotSupportedException e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * Configuration class
     */
    @ObjectClassDefinition(name = "Menarini RSS Feed News Importer Job", description = "Menarini RSS Feed News Importer Cron Job")
    public static @interface Config {

        @AttributeDefinition(name = "Cron expression", description = "Job Cron expression")
        String scheduler_expression() default "0 0 0 * * ? 2000";

        @AttributeDefinition(name = "scheduler runOn", description = "scheduler.runOn=LEADER")
        String scheduler_runOn() default "LEADER";
    }

}
