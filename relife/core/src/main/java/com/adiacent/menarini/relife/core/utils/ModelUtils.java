package com.adiacent.menarini.relife.core.utils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;


public class ModelUtils {

	public static Page getHomePage(ResourceResolver resourceResolver, String currentPage) {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if(pageManager == null)
			return null;
		pageManager.getPage(currentPage).getDepth();
		int pathIndex = 3;
		Page homepage = pageManager.getPage(currentPage).getAbsoluteParent(pathIndex);
		return homepage;
	}

}
