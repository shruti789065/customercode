package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Model(
		adaptables = {Resource.class, SlingHttpServletRequest.class},
		adapters = {NewsListI.class, ComponentExporter.class},
		resourceType = MegamenuModel.RESOURCE_TYPE,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class NewsListModel extends GenericBaseModel implements NewsListI {
	public static final String RESOURCE_TYPE = "menarinimaster/components/news_list";
	public static final String NEWSDATA_RESOURCE_TYPE = "menarinimaster/components/news_data";
	private static final String NEWSDATA_DATE_PROPERTY_NAME = "newsDate";

	private static final String TAGS_DENY_PROPERTY_NAME = "denyTags";
    private static final String MATCH_TAGS_DENY_PROPERTY_NAME = "denyTagsMatch";
    private static final String TAGS_MATCH_ANY_VALUE = "any";


	private static final String ASC_SORT_ORDER = "asc";
	private static final String PN_LIMIT = "limit";

	@Self // Indicates that we are resolving the current resource
	@Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC List)
	@Delegate(excludes = NewsListModel.DelegationExclusion.class)
	// Delegate all our methods to the CC List except those defined below
	private List delegate;

	private ArrayList<ListItem> tmp;

	@PostConstruct
	protected void init() {

        //si recupera l'elenco delle pagine da componente originale ( delegate )
		this.tmp = (ArrayList) delegate.getListItems();

		ValueMap vm = this.currentResource.adaptTo(ValueMap.class);
		String[] denyTagsProperty = vm.get(TAGS_DENY_PROPERTY_NAME, String[].class);
        String matchDenyTagsProperty = vm.get(MATCH_TAGS_DENY_PROPERTY_NAME, String.class);

        //Se è stata configurata la proprietà TAGS_DENY, occorre filtratre dall'elenco delle pagine quelle
        //i cui tags corrispondono completamente ( match == all ) o parzialmente ( match == any ) a quelli elencati
        //nella proprietà denyTags
        if(denyTagsProperty != null && denyTagsProperty.length > 0){
			 // Stream tagsToExclude = Arrays.stream(denyTagsProperty);
			Supplier<Stream<String>> streamSupplier	= () -> Arrays.stream(denyTagsProperty);
			//per ogni pagina recuperata dal componente delegate
			if(this.tmp != null){
                boolean matchAny = matchDenyTagsProperty.equals(TAGS_MATCH_ANY_VALUE);
				java.util.List<ListItem> filteredPageList = this.tmp.stream().filter(pageListItem -> {
                    //tag Ids della pagina corrente
					java.util.List<String> pageTagIds = ModelUtils.getPageTags(resourceResolver, pageListItem.getPath());
					// ci sono dei tag sulla pagina da controllare per l'esclusione
					if (pageTagIds != null && pageTagIds.size() > 0) {
                        if(matchAny)
                            return !streamSupplier.get().anyMatch(pageTagIds::contains);
                        else
                           return !streamSupplier.get().allMatch(pageTagIds::contains);
					} else return true;
				}).collect(Collectors.toList());

				this.tmp = (ArrayList<ListItem>) filteredPageList;
			}

		}


		String orderByProperty = (String) vm.get(PN_ORDER_BY);
		String sortOrderProperty = (String) vm.get(PN_SORT_ORDER);
		if (StringUtils.isNotBlank(orderByProperty) && NEWSDATA_DATE_PROPERTY_NAME.equals(orderByProperty)) {
			orderByNewsDateValue(sortOrderProperty);
		}


		//filtraggio numerico degli articoli
		String limitProperty =  (String)vm.get(PN_LIMIT);
		if (StringUtils.isNotBlank(limitProperty)){
			int limitValue = Integer.parseInt(limitProperty);
			if(limitValue > 0)
				this.tmp = this.tmp != null ? (ArrayList) this.tmp.stream().limit(limitValue).collect(Collectors.toList()) : this.tmp;
		}


/*VERSIONE CON ORDINAMENTO PREFISSATO A CODICE *****************************
        orderByNewsDateValue(ASC_SORT_ORDER);
/* FINE VERSIONE CON ORDINAMENTO PREFISSATO A CODICE**********************/
	}

	private void orderByNewsDateValue(String sortOrderProperty) {
		if (tmp != null && tmp.size() > 0) {
			//si controlla che sia un elenco di pagine ( non ho accesso alleproprietà PN_XXX di https://github.com/adobe/aem-core-wcm-components/blob/main/bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/List.java)
			// tmp = new ArrayList(delegate.getListItems());
			Optional opt = tmp.stream().findFirst();
			if (opt.isPresent() && opt.get() != null) {
				Collections.sort(tmp, new Comparator<ListItem>() {
					@Override
					public int compare(ListItem o1, ListItem o2) {
						Calendar date1 = null;
						Calendar date2 = null;


						date1 = getNewsDateValue(o1.getPath());
						date2 = getNewsDateValue(o2.getPath());

						if (date1 == null && date2 == null)
							return 0;
						if (date1 == null && date2 != null)
							return 1;
						if (date1 != null && date2 == null)
							return -1;
						return date1.compareTo(date2) * (ASC_SORT_ORDER.equals(sortOrderProperty) ? 1 : -1);
					}
				});
			}
		}
	}


	private Calendar getNewsDateValue(String path) {

		Resource resource = resourceResolver.getResource(path);
		if (resource == null)
			return null;

		if (!resource.getResourceType().equals(Constants.PAGE_PROPERTY_NAME))
			return null;

		Page page = resource.adaptTo(Page.class);
		if (page == null)
			return null;

		Resource newsDataCmp = ModelUtils.findChildComponentByResourceType(page.getContentResource(), NEWSDATA_RESOURCE_TYPE);
		if (newsDataCmp == null || !newsDataCmp.getValueMap().containsKey("newsDate")){
			return page.getProperties().containsKey(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED) ?
					page.getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, Calendar.class) : page.getProperties().get(JcrConstants.JCR_CREATED, Calendar.class);
		}
		else{
			ValueMap properties = newsDataCmp.getValueMap();
			return (Calendar) properties.get(NEWSDATA_DATE_PROPERTY_NAME);
		}
	}

	public Collection<ListItem> getListItems() {
        /*if(delegate.getListItems() == null || delegate.getListItems().size() == 0 )
            return delegate.getListItems();
        return this.tmp;*/

		//return this.tmp != null ? this.tmp : delegate.getListItems();

		if(this.tmp == null)
			return null;
		ListItem[] array = (ListItem[]) this.tmp.toArray(new ListItem[this.tmp.size()]);
		ListItem[] clone = array.clone();
		return Arrays.asList(clone);
	}



	private interface DelegationExclusion { // Here we define the methods we want to override
		default Collection<ListItem> getListItems() {
			return Collections.emptyList();
		}

	}
}
