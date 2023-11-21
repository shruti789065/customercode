package com.adiacent.menarini.menarinimaster.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {LanguageNavigationI.class}, // Adapts to the CC model interface
        resourceType = LanguageNavigationModel.RESOURCE_TYPE, // Maps to OUR component, not the CC component
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL // No properties? No problem!
)
public class LanguageNavigationModel extends GenericBaseModel implements LanguageNavigationI {

    public static final String RESOURCE_TYPE = "menarinimaster/components/languagenavigation";

    @Self // Indicates that we are resolving the current resource
    @Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC Teaser)
    @Delegate(excludes = DelegationExclusion.class) // Delegate all our methods to the CC Image except those defined below
    private LanguageNavigation delegate;

    @Override
    public List<NavigationItem> getItems() {
        //return delegate.getItems();
        //primo filtraggio per rimozione elementi nel languagenavigation che risultano puntare alla medesima pagina in lingua
        // a seguito di redirect di pagina
        if(delegate.getItems() == null)
            return null;

        List<NavigationItem> res =  delegate.getItems().stream().collect(Collectors.groupingBy(p -> p.getPath())).values().stream()
                .map(plans -> plans.stream().findFirst().get())
                .collect(toList());

        //secondo filtraggio: si rimuovono gli elementi nel languagenavigation che risultano puntare alla home->il puntamento alla home
        //è il comportamento di default del languagenavigation quando manca la pagina corrente tradotta nella i-esima lingua
        //Il puntamento alla home si individua determinanodo qual'è il navigationitem attivo, si verifica la lunghezza del path della risorsa
        //alla quale si riferisce l'elemento attivo e scartando gli elementi del navigation che hanno un path con lunghezza diversa ( e quindi
        //che si riferiscono a risorse==pagine diverse)

        AtomicInteger lenght = new AtomicInteger();
        res.stream().forEach(ni->{
            if(ni.isActive())
                lenght.set(StringUtils.split(ni.getPath(), "/").length);
        });
        if(lenght != null)
        {

            return res.stream().filter(ni->StringUtils.split(ni.getPath(),"/").length == lenght.get()).collect(toList());
        }
        return res;

    }


    private interface DelegationExclusion { // Here we define the methods we want to override
        List<NavigationItem> getItems();
    }

}
