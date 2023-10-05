package com.adiacent.menarini.menarinimaster.core.utils;

import com.day.cq.wcm.api.NameConstants;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantsTest {

	@Test
	void testResourceTypeProperty() {
		String expectedResourceTypeProperty = JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;
		String actualResourceTypeProperty = Constants.RESOURCE_TYPE_PROPERTY;

		assertEquals(expectedResourceTypeProperty, actualResourceTypeProperty);
	}

	@Test
	void testFeatureImageNodeName() {
		String expectedFeatureImageNodeName = "cq:featuredimage";
		String actualFeatureImageNodeName = Constants.FEATURE_IMAGE_NODE_NAME;

		assertEquals(expectedFeatureImageNodeName, actualFeatureImageNodeName);
	}

	@Test
	void testPagePropertyName() {
		String expectedPagePropertyName = "cq:Page";
		String actualPagePropertyName = Constants.PAGE_PROPERTY_NAME;

		assertEquals(expectedPagePropertyName, actualPagePropertyName);
	}

	@Test
	void testTemplateProperty() {
		String expectedTemplateProperty = NameConstants.NN_TEMPLATE;
		String actualTemplateProperty = Constants.TEMPLATE_PROPERTY;

		assertEquals(expectedTemplateProperty, actualTemplateProperty);
	}

	@Test
	void testExtentionSeparator() {
		String expectedExtentionSeparator = ".";
		String actualExtentionSeparator = Constants.EXTENTION_SEPARATOR;

		assertEquals(expectedExtentionSeparator, actualExtentionSeparator);
	}

	@Test
	void testMp4FileExt() {
		String expectedMp4FileExt = "mp4";
		String actualMp4FileExt = Constants.MP4_FILE_EXT;

		assertEquals(expectedMp4FileExt, actualMp4FileExt);
	}

	@Test
	void testOggFileExt() {
		String expectedOggFileExt = "ogg";
		String actualOggFileExt = Constants.OGG_FILE_EXT;

		assertEquals(expectedOggFileExt, actualOggFileExt);
	}

	@Test
	void testFormatAnnoMeseGiorno() {
		String expectedFormatAnnoMeseGiorno = "yyyy - MM - dd";
		String actualFormatAnnoMeseGiorno = Constants.FORMAT_ANNO_MESE_GIORNO;

		assertEquals(expectedFormatAnnoMeseGiorno, actualFormatAnnoMeseGiorno);
	}

	@Test
	void testFormatGiornoMeseAnno() {
		String expectedFormatGiornoMeseAnno = "dd - MM - YYYY";
		String actualFormatGiornoMeseAnno = Constants.FORMAT_GIORNO_MESE_ANNO;

		assertEquals(expectedFormatGiornoMeseAnno, actualFormatGiornoMeseAnno);
	}

	@Test
	void testApplicationJson() {
		String expectedApplicationJson = "application/json";
		String actualApplicationJson = Constants.APPLICATION_JSON;

		assertEquals(expectedApplicationJson, actualApplicationJson);
	}

	@Test
	void testJson() {
		String expectedJson = "json";
		String actualJson = Constants.JSON;

		assertEquals(expectedJson, actualJson);
	}

	@Test
	void testServiceName() {
		String expectedServiceName = "menarinimaster";
		String actualServiceName = Constants.SERVICE_NAME;

		assertEquals(expectedServiceName, actualServiceName);
	}

	@Test
	void testInternalMenuTemplateRegexp() {
		String expectedInternalMenuTemplateRegexp = "/conf/[\\w-]+/settings/wcm/templates/(menarini---details-news|menarini---details-page|menarini---details-product|menarini---product-category|menarini---product-list-pharmaceutical|menarini---product-list-healthcare)";
		String actualInternalMenuTemplateRegexp = Constants.INTERNAL_MENU_TEMPLATE_REGEXP;

		assertEquals(expectedInternalMenuTemplateRegexp, actualInternalMenuTemplateRegexp);
	}

}
