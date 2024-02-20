package com.adiacent.menarini.menarinimaster.core.models.contentfragments;


import com.adiacent.menarini.menarinimaster.core.models.rss.BlogItemModel;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ContentFragmentFactory {



    public static TagManager getTagManager(ResourceResolver resolver) {
        if(resolver!= null)
            return resolver.adaptTo(TagManager.class);
        return null;
    }

    public static ContentFragmentM generate(String type, Object dataSource, ResourceResolver resolver, String tagNamespace, String tagCategoryParent, String fragmentModel) {
        if(StringUtils.isBlank(type))
            return null;
        if(dataSource == null)
            return null;
        ContentFragmentM res = null;
        switch(type){
            case "blog":{
                res = createFromRssBlogItem((BlogItemModel)dataSource, getTagManager(resolver), tagNamespace, tagCategoryParent, fragmentModel);
                break;
            }
        }
        return res;
    }

    private static ContentFragmentM createFromRssBlogItem(BlogItemModel dataSource, TagManager tagManager, String tagNamespace, String tagCategoryParent, String fragmentModel) {
        //si recupera l'id del blog item dal guid ex: https://menariniblog.it/?p=8755
        //dataSource.getGuid()to doooooooooo
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'");

        ContentFragmentM<ContentFragmentBlogItemElements> cf = new ContentFragmentM<ContentFragmentBlogItemElements>();

        ContentFragmentPropertiesM<ContentFragmentBlogItemElements> properties = new ContentFragmentPropertiesM<ContentFragmentBlogItemElements>();
        properties.setCqModel(fragmentModel);
        properties.setTitle(dataSource.getTitle());

        ContentFragmentBlogItemElements elements = new ContentFragmentBlogItemElements();

        ContentFragmentElementSingleValue titleE = new ContentFragmentElementSingleValue();
        titleE.setType("string");
        titleE.setValue(dataSource.getTitle());
        elements.setTitle(titleE);


        String dateStr = isoFormat.format(dataSource.getPubDate());
        ContentFragmentElementSingleValue dateE = new ContentFragmentElementSingleValue();
        dateE.setType("Date");
        dateE.setValue(dateStr);
        elements.setDate(dateE);

        if(dataSource.getEnclosure() != null && StringUtils.isNotBlank(dataSource.getEnclosure().getUrl())) {
            ContentFragmentElementSingleValue imageLinkE = new ContentFragmentElementSingleValue();
            imageLinkE.setType("string");
            imageLinkE.setValue( dataSource.getEnclosure().getUrl() );
            elements.setImageLink(imageLinkE);
        }


        ContentFragmentElementSingleValue pageLinkE = new ContentFragmentElementSingleValue();
        pageLinkE.setType("string");
        pageLinkE.setValue(dataSource.getLink());
        elements.setPageLink(pageLinkE);

        if(dataSource.getCategories() != null){
            List categories = dataSource.getCategories().stream().map(c->{
                if(c!= null){
                    //si recupera il tag id associato a quella categoria
                    String tagName = ModelUtils.getNodeName(c);
                    Tag tag =tagManager.resolve((StringUtils.isNotBlank(tagNamespace)?tagNamespace:"" ) +
                            (StringUtils.isNotBlank(tagCategoryParent) ?tagCategoryParent+"/":"")+
                            tagName);
                    return tag != null ? tag.getTagID() : null;
                }
                return null;
            }).filter(e->e != null).collect(Collectors.toList());

            ContentFragmentElementValue categoryE = new ContentFragmentElementValue();
            categoryE.setType("string");
            categoryE.setValue(categories);
            elements.setCategory(categoryE);

        }


        properties.setElements(elements);
        cf.setProperties(properties);

        cf.setProperties(properties);

        return cf ;
    }
}
