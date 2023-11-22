package com.adiacent.menarini.menarinimaster.core.schedulers;

import lombok.Getter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Designate(ocd = EncodeDecodeSecretKey.Config.class)
@Component(immediate = true)
public class EncodeDecodeSecretKey {

    private static final Logger logger = LoggerFactory.getLogger(EncodeDecodeSecretKey.class);

    private Config config;
    /**
     * -- GETTER --
     *
     * @return the _instance
     */
    @Getter
    private static EncodeDecodeSecretKey _instance = null;

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
        logger.info("Activating Menarini Encode Secret Key");
        EncodeDecodeSecretKey._instance = this;
        this.config = config;
        logger.info("*********************************************************************************************");
        logger.info("** secret key = " + this.config.getSecretKey());
        logger.info("** Iv Parameter = " + this.config.getIvParameter());
        logger.info("** Alghorithm = " + this.config.getAlgorithm());
        logger.info("*********************************************************************************************");
    }

}
