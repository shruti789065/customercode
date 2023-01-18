package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;


@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = ProductCategoryListI.class, // Adapts to the CC model interface
        resourceType = ProductCategoryListModel.RESOURCE_TYPE, // Maps to OUR component, not the CC component
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL // No properties? No problem!
)
public class ProductCategoryListModel extends GenericBaseModel implements  ProductCategoryListI {
    public static final String RESOURCE_TYPE = "menarinimaster/components/productcategorylist";




    @Self // Indicates that we are resolving the current resource
    @Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC Teaser)
    @Delegate(excludes = DelegationExclusion.class) // Delegate all our methods to the CC Image except those defined below
    private List delegate;


    private Collection<ListItem>  listItems;

    @Inject @Named("productArea") @Via("resource")
    @Optional
    private String productArea;

    @Inject @Named("listFrom") @Via("resource")
    private String listFrom;

    @Inject
    private Page resourcePage;

    @PostConstruct
    protected void init() {

        if(StringUtils.isNotBlank(listFrom) && "children".equals(listFrom)){
            if(StringUtils.isNotBlank(productArea))
                listItems = filterProductCategoriesByChildTemplate(delegate.getListItems(), productArea);

        }
        else
            listItems = delegate.getListItems();

    }

    private Collection<ListItem> filterProductCategoriesByChildTemplate(Collection<ListItem> listItems, String productArea) {
        if(listItems == null )
            return null;

        return listItems.stream().filter(li ->{
            Page page =(resourceResolver.getResource(li.getPath())).adaptTo(Page.class);
            if(page == null ||
                    !page.getContentResource().getValueMap().containsKey(Constants.TEMPLATE_PROPERTY))
                return false;

            boolean add = false;
            String pageTemplate = page.getContentResource().getValueMap().get(Constants.TEMPLATE_PROPERTY).toString();

            String templateProdCategory = "/settings/wcm/templates/menarini---product-category";

            if(pageTemplate.contains(templateProdCategory)){
               Iterator<Page> i = page.listChildren();
               while(i.hasNext()){
                   add = false;
                   Page child = (Page)i.next();
                   String childTemplate = child.getContentResource().getValueMap().get(Constants.TEMPLATE_PROPERTY).toString();
                   if(productArea.equals( childTemplate)) {
                       add =  true;
                       break;
                   }
                   if("all".equals(productArea))
                   {
                       add =  true;
                       break;
                   }
               }

           }
            return add;
        }).collect(Collectors.toList());
    }


  public Collection<ListItem> getListItems(){
       return Collections.unmodifiableCollection(listItems);
  }

    private interface DelegationExclusion { // Here we define the methods we want to override
        Collection<ListItem> getListItems();
    }

}
