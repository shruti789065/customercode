package com.jakala.menarini.core.models;


import com.adobe.cq.wcm.core.components.models.Tabs;
import com.day.cq.wcm.foundation.Image;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface MegamenuI extends Tabs {
    public Image getImage();
}

