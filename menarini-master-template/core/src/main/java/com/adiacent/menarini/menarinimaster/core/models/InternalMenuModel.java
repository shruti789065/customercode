package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Model(
		adaptables = {Resource.class, SlingHttpServletRequest.class},
		adapters = {InternalMenuI.class, ComponentExporter.class},
		resourceType = InternalMenuModel.RESOURCE_TYPE,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

public class InternalMenuModel extends GenericBaseModel implements InternalMenuI {

	// points to the component resource path in ui.apps
	public static final String RESOURCE_TYPE = "menarinimaster/components/internalmenu";
	//private static final String PARENT_TEMPLATE_NAME = "/conf/menarinimaster/settings/wcm/templates/menarini---homepage";//"Menarini - Homepage";

	@Self
	@Via(type = ResourceSuperType.class)
	@Delegate(excludes = DelegationExclusion.class)
	private Navigation delegate;

	private Page currentPage;
	private Page navigationRoot;

	@PostConstruct
	protected void init() {
		try {
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			if (pageManager != null) {
				currentPage = pageManager.getContainingPage(currentResource);

				Page homepage = ModelUtils.getHomePage(resourceResolver, currentPage.getPath());
				ValueMap properties = homepage.getProperties();
				/*String siteName = properties.containsKey("siteName") ? properties.get("siteName", String.class) : "";
				String PARENT_TEMPLATE_NAME = "/conf/"+siteName+"/settings/wcm/templates/menarini---homepage";*/
				String template = properties.containsKey("cq:template") ? properties.get("cq:template", String.class) : "";

				Page parentPage = ModelUtils.findPageByParentTemplate(currentPage, template);
				navigationRoot = parentPage;
				if(!isPublishMode()){
					Node node = currentResource.adaptTo(Node.class);
					node.setProperty("navigationRoot",navigationRoot.getPath());
					node.setProperty("structureStart",0);
					node.getSession().save();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getRootNavigation() {
		Node node = currentResource.adaptTo(Node.class);
		try {
			return node.getProperty("navigationRoot").getString();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	private interface DelegationExclusion { // Here we define the methods we want to override
	}
}
