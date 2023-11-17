package com.adiacent.menarini.menarinimaster.core.schedulers;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = EncodeDecodeSercetKey.Config.class)
@Component(immediate = true)
public class EncodeDecodeSercetKey {

    private static final Logger LOG = LoggerFactory.getLogger(EncodeDecodeSercetKey.class);

    private Config config;
    private static EncodeDecodeSercetKey _instance = null;

    /**
     * Configuration class
     */
    @ObjectClassDefinition(name="Menarini - Encode Decode Value Secret key", description = "Menarini - Encode Decode Value Secret key")
    public static @interface Config {
        @AttributeDefinition(name="Secret key", description = "Sercet key use to encode and decode values")
        String getSecretKey() default "";

        @AttributeDefinition(name="Iv Parameter", description = "Iv Parameter use to encode and decode values")
        String getIvParameter() default "";

        @AttributeDefinition(name = "Alghorithm", description = "Alghorithm use to encode and decode values")
        String getAlgorithm() default "";
    }

    @Activate
    @Modified
    protected void activate(final Config config){
        LOG.info("Activating Menarini Encode Secret Key");
        EncodeDecodeSercetKey._instance = this;
        this.config = config;
        LOG.info("*********************************************************************************************");
        LOG.info("** secret key = " + this.config.getSecretKey());
        LOG.info("** Iv Parameter = " + this.config.getIvParameter());
        LOG.info("** Alghorithm = " + this.config.getAlgorithm());
        LOG.info("*********************************************************************************************");
    }

    /**
     * @return the _instance
     */

    public static EncodeDecodeSercetKey get_instance(){
        return _instance;
    }

    public Config getConfig(){
        return config;
    }

}
