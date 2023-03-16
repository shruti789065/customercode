package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.crx.JcrConstants;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component(service = Servlet.class, immediate = true)
@ServiceDescription("Pipeline Compound Dropdown option provider Servlet")
@SlingServletPaths(value = {"/bin/menariniStemline/compoundOption", "/bin/menariniStemline/indicationOption"})
public class DropdownServlet extends SlingSafeMethodsServlet {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)  {
        try{
            String siteName = request.getRequestPathInfo().getSuffix().split("/")[2];
            List<KeyValue> dropDownList = new ArrayList<>();
            ResourceResolver resourceResolver = request.getResourceResolver();
            String resourceType = request.getResource().getResourceType();
            String rootkey = resourceType.substring(resourceType.lastIndexOf("/") + 1);
            Resource rootResource = null;
            if(rootkey.equals("compoundOption")){
                rootResource = resourceResolver.getResource("/content/dam/"+ siteName +"/area-content-fragments/compound-dropdown-options");
            }
            if(rootkey.equals("indicationOption")){
                rootResource = resourceResolver.getResource("/content/dam/"+ siteName +"/area-content-fragments/indication-dropdown-options");
            }
            if(rootResource != null){
                Iterator<Resource> compoundOptions =  rootResource.listChildren();
                while(compoundOptions.hasNext()){
                    Resource dropDownOption = (Resource)compoundOptions.next();
                    if(!dropDownOption.getPath().contains("/jcr:content")){
                        String value;
                        ContentFragment cf =  dropDownOption.adaptTo(ContentFragment.class);
                        String label = cf.getElement("label").getContent();
                        if(rootkey.equals("compoundOption")){
                            value = dropDownOption.getPath();
                        }else {
                            value = cf.getElement("value").getContent();
                        }
                        dropDownList.add(new KeyValue() {
                            @Override
                            public Object getKey() {
                                return label;
                            }

                            @Override
                            public Object getValue() {
                                return value;
                            }
                        });
                    }
                }
                @SuppressWarnings("unchecked")
                DataSource ds =
                        new SimpleDataSource(
                                new TransformIterator(
                                        dropDownList.iterator(),
                                        input -> {
                                            KeyValue keyValue = (KeyValue) input;
                                            ValueMap vm = new ValueMapDecorator(new HashMap<>());
                                            vm.put("value", keyValue.getValue());
                                            vm.put("text", keyValue.getKey());
                                            return new ValueMapResource(
                                                    resourceResolver, new ResourceMetadata(),
                                                    JcrConstants.NT_UNSTRUCTURED, vm);
                                        }));
                request.setAttribute(DataSource.class.getName(), ds);
            }
        }catch (Exception e){
            LOG.error("Error in Compound Dropdown servlet Get call: ", e);
        }
    }
}