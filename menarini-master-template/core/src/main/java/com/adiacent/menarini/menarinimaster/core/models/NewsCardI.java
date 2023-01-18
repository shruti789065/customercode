package com.adiacent.menarini.menarinimaster.core.models;

import com.adobe.cq.wcm.core.components.models.Teaser;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface NewsCardI extends Teaser {
    public String getFormattedValue();
}
