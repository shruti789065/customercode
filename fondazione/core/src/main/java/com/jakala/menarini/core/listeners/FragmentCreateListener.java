package com.jakala.menarini.core.listeners;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.VariationDef;
import com.jakala.menarini.core.models.ModelHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

@Component(
    service = ResourceChangeListener.class,
    immediate = true,
    property = {
        ResourceChangeListener.PATHS + "=/content/dam/fondazione",
        ResourceChangeListener.CHANGES + "=" + ResourceChangeListener.CHANGE_ADDED
    }
)
public class FragmentCreateListener implements ResourceChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentCreateListener.class);

    private static String SERVICE = "data-migration-service";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Activate
    protected void activate() {
        LOGGER.info("FragmentCreateListener activated");
    }

    @Override
    public void onChange(List<ResourceChange> changes) {
        LOGGER.info("onChange called with {} changes", changes.size());

        ResourceResolver resolver = null;

        try {
            resolver = getResourceResolver();
            for (ResourceChange change : changes) {
                String resourcePath = change.getPath().replace("/jcr:content", "");
                Resource resource = resolver.getResource(resourcePath);
                if (resource != null && resource.getResourceType().equals("dam:Asset")) {
                    ContentFragment fragment = resource.adaptTo(ContentFragment.class);

                    if (fragment != null) {
                        setContent (fragment, resource, resolver);
                    }
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Error getting resource resolver", e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    private void setContent(ContentFragment fragment, Resource resource, ResourceResolver resolver)  {
        ContentElement idElement = fragment.getElement("id");
        String id = idElement.getContent();
        String path = resource.getParent().getPath();

        boolean isIdEmptyOrZero = id.isEmpty() || id.equals("0");

        try {
            if (isIdEmptyOrZero) {
                String newSequence = ModelHelper.nextSequence(resolver, path);
                idElement.setContent(newSequence, idElement.getContentType());

                this.createVariations(fragment);
                resolver.commit();
                resolver.refresh();
            }
        } catch (ContentFragmentException e) {
            LOGGER.error("Error creating variations in content fragment", e);
        } catch (RepositoryException e) {
            LOGGER.error("Repository error while setting id in content fragment", e);
        } catch (PersistenceException e) {
            LOGGER.error("Persistence error while committing changes to the content fragment", e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    /**
     * Create the variations for the given ContentFragment.
     */
    private void createVariations(ContentFragment fragment) throws ContentFragmentException{
        String[] languages = {"it", "en"};
        
        for (String language : languages) {
            String variationName = fragment.getName() + "_" + language;
            Iterator<VariationDef> variations = fragment.listAllVariations();
            boolean variationExists = false;
            
            while (variations.hasNext()) {
                VariationDef variation = variations.next();
                if (variation.getName().equals(variationName)) {
                    variationExists = true;
                    break;
                }
            }

            if (!variationExists) {
                fragment.createVariation(variationName, variationName, "");
            }
        }
    }

    /**
     * Retrieves a ResourceResolver for the data migration service.
     * @throws LoginException 
     */
    private ResourceResolver getResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
        return resolverFactory.getServiceResourceResolver(param);
    }
}