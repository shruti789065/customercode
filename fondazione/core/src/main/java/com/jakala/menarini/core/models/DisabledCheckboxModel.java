package com.jakala.menarini.core.models;
import com.adobe.granite.ui.components.formbuilder.FormResourceManager;
import com.day.cq.i18n.I18n;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Model(adaptables = SlingHttpServletRequest.class)
public class DisabledCheckboxModel {

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private FormResourceManager formResourceManager;

    private Resource wrappedResource;

    @PostConstruct
    public void init() {
        // Initialize i18n for localization
        I18n i18n = new I18n(request);

        // Get the original resource and its properties
        Resource resource = request.getResource();
        ValueMap properties = resource.getValueMap();

        // Create a new map to hold the modified properties for the checkbox
        HashMap<String, Object> decoratedProperties = new HashMap<>(properties);

        // Set the label for the checkbox, localized
        decoratedProperties.put("text", i18n.get("Disable editing field", "Prevent authoring field"));

        // Set the checkbox to be checked/unchecked based on the 'disabled' property
        Boolean disabled = properties.get("disabled", false); // Default to false if not present
        decoratedProperties.put("checked", disabled);

        // Set the value and name properties so that the checkbox's state is stored correctly
        decoratedProperties.put("value", true); // The value when checked
        decoratedProperties.put("name", "./content/items/" + resource.getName() + "/disabled");
        decoratedProperties.put("disabled", false);  // This adds the HTML disabled attribute


        // Wrap the original resource with the new properties
        this.wrappedResource = formResourceManager.getDefaultPropertyFieldResource(resource, decoratedProperties);
    }

    // This method returns the wrapped resource with the updated properties
    public Resource getResource() {
        return wrappedResource;
    }
}