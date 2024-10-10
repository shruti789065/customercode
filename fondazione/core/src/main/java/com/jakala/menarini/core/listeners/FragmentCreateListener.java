package com.jakala.menarini.core.listeners;

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

    private ResourceResolver resolver;

    @Activate
    protected void activate() {
        LOGGER.info("FragmentCreateListener activated");
    }

    @Override
    public void onChange(List<ResourceChange> changes) {
        LOGGER.info("onChange called with {} changes", changes.size());

        try {
            resolver = getResourceResolver();
            for (ResourceChange change : changes) {
                String resourcePath = change.getPath().replace("/jcr:content", "");
                Resource resource = resolver.getResource(resourcePath);
                if (resource != null && resource.getResourceType().equals("dam:Asset")) {
                    ContentFragment fragment = resource.adaptTo(ContentFragment.class);

                    if (fragment != null) {
                        ContentElement idElement = fragment.getElement("id");
                        String id = idElement.getContent();
                        String path = resource.getParent().getPath();
                        //String path = fragment.getName();
                        // String pathNumberPart = path.contains("-") ? path.substring(0, path.lastIndexOf('-')) : "";

                        // boolean isPathNumberValid = pathNumberPart.matches("\\d+");
                        boolean isIdEmptyOrZero = id.isEmpty() || id.equals("0");
                        // boolean isIdDifferentFromPathNumber = !id.equals(pathNumberPart);

                        try {
                            //if (isIdEmptyOrZero || !isPathNumberValid || isIdDifferentFromPathNumber) {
                            if (isIdEmptyOrZero) {
                                String newSequence = ModelHelper.nextSequence(resolver, path);
                                idElement.setContent(newSequence, idElement.getContentType());
                                //  Don't move anymore
                                // String pathStringPart = isPathNumberValid && path.contains("-") ? path.substring(path.lastIndexOf('-') + 1) : path;
                                // String newPath = newSequence + "-" + pathStringPart;
                                // resolver.commit();
                                // resolver.refresh();
                                
                                // Move the resource to the new path
                                // this.moveContentFragment(resource, newPath);
                                // this.createVariations(resource, newPath);

                                this.createVariations(fragment);
                                resolver.commit();
                                resolver.refresh();
                            }
                        } catch (ContentFragmentException | RepositoryException e) {
                            e.printStackTrace();
                        } finally {
                            if (resolver != null && resolver.isLive()) {
                                resolver.close();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in FragmentCreateListener", e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    // private void moveContentFragment(Resource resource, String newPath) {
    //     Session session = null;
    //     try {
    //         session = resolver.adaptTo(Session.class);
    //         if (session == null) {
    //             LOGGER.error("Could not obtain JCR session");
    //             return;
    //         }
            
    //         String oldPath = resource.getPath();
    //         String parentPath = resource.getParent().getPath();
    //         String newResourcePath = parentPath + "/" + newPath;
            
    //         LOGGER.info("Attempting to move {} to {}", oldPath, newResourcePath);

    //         if (resolver.getResource(newResourcePath) != null) {
    //             LOGGER.error("Destination path already exists: {}", newResourcePath);
    //             throw new RepositoryException("Destination path already exists");
    //         }

    //         session.move(oldPath, newResourcePath);
    //         session.save();
            
    //         resolver.commit();
    //         resolver.refresh();
            
    //         LOGGER.info("Successfully moved content fragment to: {}", newResourcePath);
            
    //     } catch (RepositoryException e) {
    //         LOGGER.error("Repository exception during move operation", e);
    //         try {
    //             if (session != null) {
    //                 session.refresh(false);
    //             }
    //             resolver.revert();
    //             resolver.refresh();
    //         } catch (RepositoryException re) {
    //             LOGGER.error("Error reverting changes", re);
    //         }
    //         throw new RuntimeException("Failed to move content fragment", e);
    //     } catch (PersistenceException e) {
    //         LOGGER.error("Persistence exception during move operation", e);
    //         resolver.revert();
    //         resolver.refresh();
    //         throw new RuntimeException("Failed to persist changes", e);
    //     }
    // }

    // private ContentFragment reloadContentFragment(Resource resource, String newPath) {
    //     String parentPath = resource.getParent().getPath();
    //     String newResourcePath = parentPath + "/" + newPath;
    //     Resource resourceNew = resolver.getResource(newResourcePath);
    //     if (resourceNew != null && resourceNew.getResourceType().equals("dam:Asset")) {
    //         ContentFragment fragment = resourceNew.adaptTo(ContentFragment.class);
    //         return fragment;
    //     }
    //     return null;
    // }

    //private void createVariations(Resource resource, String newName) throws ContentFragmentException{
    private void createVariations(ContentFragment fragment) throws ContentFragmentException{
        String[] languages = {"it", "en"};

        //ContentFragment fragment = reloadContentFragment(resource, newName);
        
        for (String language : languages) {
            //String variationName = newName + "_" + language;
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
     */
    private ResourceResolver getResourceResolver() throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
        return resolverFactory.getServiceResourceResolver(param);
    }
}