package com.adiacent.menarini.relife.core.models;

import com.adobe.cq.wcm.core.components.models.Teaser;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    adapters = Teaser.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class DuplaBanner implements Teaser {

    @ValueMapValue
    private String fileReference1;

    @ValueMapValue
    private String fileReference2;

    public String getImagePath1() {
        return fileReference1;
    }

    public String getImagePath2() {
        return fileReference2;
    }

    // Implement other necessary methods from the Teaser interface
}
