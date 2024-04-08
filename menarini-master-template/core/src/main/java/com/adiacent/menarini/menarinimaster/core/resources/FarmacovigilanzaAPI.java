package com.adiacent.menarini.menarinimaster.core.resources;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = FarmacovigilanzaAPI.Config.class)
@Component(immediate = true)
public class FarmacovigilanzaAPI {
    private static final Logger logger = LoggerFactory.getLogger(FarmacovigilanzaAPI.class);

    private static FarmacovigilanzaAPI _instance = null;
    private Config config;

    @ObjectClassDefinition(name="Menarini Berlinchemie - Farmacoviglizanza API Integrazione", description = "Menarini Berlinchemie - Farmacoviglizanza API Integrazione")
    public static @interface Config {

        @AttributeDefinition(name="PIM API Endpoint", description = "PIM API Endpoint")
        String getPimApiEndpoint() default "";

        @AttributeDefinition(name="Username", description = "Username")
        String getApiUsername() default "";

        @AttributeDefinition(name = "Password", description = "Password")
        String getApiPassword() default "";

        @AttributeDefinition(name = "Token key", description = "Token key")
        String getTokenKey() default "";
    }

    @Activate
    @Modified
    protected void activate(final Config config){
        logger.info("Activating Menarini Berlinchemie - Farmacoviglizanza API Integrazione");
        FarmacovigilanzaAPI._instance = this;
        this.config = config;
        logger.info("*********************************************************************************************");
        logger.info("** PIM API Endpoint URL = " + this.config.getPimApiEndpoint());
        logger.info("** API Username = " + this.config.getApiUsername());
        logger.info("** API Password = " + this.config.getApiPassword());
        logger.info("** API Token Key = " + this.config.getTokenKey());
        logger.info("*********************************************************************************************");
    }
    public static FarmacovigilanzaAPI get_instanceAPI() {
        return _instance;
    }

    public Config getConfigApi() {
        return config;
    }
}
