package com.adiacent.menarini.mhos.core.jobs;

import com.adiacent.menarini.mhos.core.services.LibraryImporter;
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

@Designate(ocd = LibraryImporterJob.Config.class)
@Component(
        service = Runnable.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Menarini Library Importer Job",
                Constants.SERVICE_VENDOR + "=Adiacent"
        },
        immediate = true
)
/**
 * Cron Job service to import product definition into jcr.
 */
public class LibraryImporterJob implements Runnable, Cloneable{

    private static final Logger LOG = LoggerFactory.getLogger(LibraryImporterJob.class);


    @Reference
    private LibraryImporter instance;


    @Activate
    @Modified
    protected void activate(final Config config) {
        LOG.info("Activating Menarini Library Importer  Cron Job");
        LOG.info("Library Importer instance is null {}",(instance==null));
    }

    public LibraryImporter getImporterIstance(){
        return instance;
    }
    @Override
    public void run() {
        try {
            LOG.info("Starting Menarini Library Importer...");
            LibraryImporter importerClone = (LibraryImporter) getImporterIstance().clone();
            importerClone.start();
        } catch (CloneNotSupportedException e) {
            LOG.error(e.getMessage(),e);
        }
    }

    /**
     * Configuration class
     */
    @ObjectClassDefinition(name = "Menarini Library Importer Job", description = "Menarini Library Importer Cron Job")
    public static @interface Config {

        @AttributeDefinition(name = "Cron expression", description = "Job Cron expression")
        String scheduler_expression() default "0 0 0 * * ? *";

        @AttributeDefinition(name = "scheduler runOn", description = "scheduler.runOn=LEADER")
        String scheduler_runOn() default "LEADER";
    }

}
