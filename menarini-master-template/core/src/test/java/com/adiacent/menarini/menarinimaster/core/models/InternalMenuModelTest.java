package com.adiacent.menarini.menarinimaster.core.models;


import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
@ExtendWith({AemContextExtension.class})
public class InternalMenuModelTest {
	private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

	private InternalMenuI internalMenuI = null;

	@BeforeEach
	void setUp() {
		// ricavare json di pagina col componente dopo averla creata
		//poi in vap verificare la pag con .100.json
		// la pagina va messa al pari delle altr pagine nominarla in modo che si definisca bene all'IM

		InputStream is = InternalMenuModelTest.class.getResourceAsStream("internalMenuPage.json");

		//ctx.create().resource("/apps/menarinimaster/components/internalmenu","sling:resourceSuperType", "core/wcm/components/navigation/v2/navigation");

		ctx.load().json(is, "/content/menarinimaster/language-masters/en");
	}

	@Test
	@Order(1)
	public void testNavigationRoot(){

		Resource im = ctx.resourceResolver().getResource("/content/menarinimaster/language-masters/en/about-us/the-group/jcr:content/root/container/container/internalmenu");

		ctx.currentResource(im);

		InternalMenuI internalMenuI = ctx.request().adaptTo(InternalMenuI.class);

		assertEquals(internalMenuI.getRootNavigation(),"/content/menarinimaster/language-masters/en/about-us");
	}
}
