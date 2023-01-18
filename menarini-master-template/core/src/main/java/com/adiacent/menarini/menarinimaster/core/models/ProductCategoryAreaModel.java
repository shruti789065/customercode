package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        adapters = {ComponentExporter.class},
        resourceType = ProductCategoryAreaModel.RESOURCE_TYPE
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION, selector = ExporterConstants.SLING_MODEL_SELECTOR)
@JsonSerialize(as =  ProductCategoryAreaModel.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategoryAreaModel extends GenericBaseModel implements ComponentExporter{
    public static final String RESOURCE_TYPE ="menarinimaster/components/productcategoryarea";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Map<Object, Object> childTemplates = null; // key: templatePath, value: page url contiene i path di tutti i template con i quali sono state istanziate le pagine figlie
    @Inject
    Page currentPage = null;

    List<ProductCategoryAreaItem> items = null;
    boolean hasElementToShow = false;
    @PostConstruct
    protected void init() {
        try{
            //PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            //currentPage = pageManager.getContainingPage(currentResource);
            Iterable<Page> childPages = () -> currentPage.listChildren();
            childTemplates = StreamSupport.stream(childPages.spliterator(), false).filter(child -> {
                String childTemplate = child.getContentResource().getValueMap().containsKey(Constants.TEMPLATE_PROPERTY)?
                        child.getContentResource().getValueMap().get(Constants.TEMPLATE_PROPERTY).toString() : null;
                return StringUtils.isNotBlank(childTemplate);

            }).collect(Collectors.toMap(entry -> entry.getContentResource().getValueMap().get(Constants.TEMPLATE_PROPERTY).toString(), entry -> entry.getPath()));



            Resource categoryAreasChild = currentResource.getChild("categoryareas");
            if(categoryAreasChild != null){
                Iterable<Resource> caChildren = () -> categoryAreasChild.listChildren();
                items = StreamSupport.stream(caChildren.spliterator(), false)
                        .filter(i->{
                            String template = i.getValueMap().containsKey("template") ?i.getValueMap().get("template").toString() : null;

                            return StringUtils.isNotBlank(template) && childTemplates.containsKey(template);
                        })
                        .map(i ->{
                            String template = i.getValueMap().get("template").toString();
                        ProductCategoryAreaItem item = new ProductCategoryAreaItem();
                        item.setLink((String)childTemplates.get(template));
                        item.setImagePath(i.getValueMap().containsKey("icon") ?i.getValueMap().get("icon").toString() : null);
                        item.setTitle(i.getValueMap().containsKey("label") ?i.getValueMap().get("label").toString() : null);

                        return item;

                }).collect(Collectors.toList());
            }


        }catch(Error e) {
            logger.error("Error in {} : {}", this.getClass(), e);

        }
    }

    public List<ProductCategoryAreaItem> getItems() {
        return items;
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }



   /* private static String PHARMA_LABEL = "Pharmaceutical";
    private static String HEALTHCARE_LABEL = "Consumer Healthcare";

    private static String PHARMA_ICON = "/content/dam/menarinimaster/assets/ico_pharma_productcategory.svg";
    private static String HEALTHCARE_ICON = "/content/dam/menarinimaster/assets/ico_health_productcategory.svg";

    @Inject
    Page currentPage;

    List<ProductCategoryAreaItem> items = null;


    @PostConstruct
    protected void init() {
        try{
            if (Objects.nonNull(currentPage)) {
                Iterable<Page> children = () -> currentPage.listChildren();

                items = StreamSupport.stream(children.spliterator(), false).map(child ->{
                        String childTemplate = child.getContentResource().getValueMap().containsKey(Constants.TEMPLATE_PROPERTY)?
                                child.getContentResource().getValueMap().get(Constants.TEMPLATE_PROPERTY).toString() : null;

                    if(StringUtils.isNotBlank(childTemplate)) {
                        ProductCategoryAreaItem item = new ProductCategoryAreaItem();
                        item.setLink(child.getPath());
                        if (Constants.PHARMA_TEMPLATE.equals(childTemplate)) {
                            item.setImagePath(PHARMA_ICON);
                            item.setTitle(PHARMA_LABEL);
                        }
                        if (Constants.HEALTHCARE_TEMPLATE.equals(childTemplate)) {
                            item.setImagePath(HEALTHCARE_ICON);
                            item.setTitle(HEALTHCARE_LABEL);
                        }
                        return item;
                    }
                    return null;
                }).collect(Collectors.toList());

            }

        }catch(Error e) {
            logger.error("Error in {} : {}", this.getClass(), e);

        }
    }

    public List<ProductCategoryAreaItem> getItems() {
        return items;
    }
*/

}
