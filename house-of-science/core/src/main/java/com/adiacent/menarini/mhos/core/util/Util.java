package com.adiacent.menarini.mhos.core.util;

import com.day.cq.wcm.api.Page;

import javax.jcr.RepositoryException;
import java.util.Locale;

public class Util {

    public static Locale getResourceLocale(Page currentPage) throws RepositoryException {
        if (currentPage != null) {
            return currentPage.getLanguage(false);
        }
        return null;
    }

    public static String getFormat(Locale local) throws RepositoryException {
        if (local != null) {
            if (local.toString().equals("en")) {
                return Constants.FORMAT_MESE_GIORNI_ANNO;
            } else {
                return Constants.FORMAT_ANNO_MESE_GIORNO;
            }
        } else {
            return Constants.FORMAT_ANNO_MESE_GIORNO;
        }
    }
}
