package com.jakala.menarini.core.service.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface CookieServiceInterface {

    public void setCookie(HttpServletResponse response, Map<String, String> mapCookie, boolean longDuration);
    public void removeCookie(HttpServletResponse response, List<String> cookieToRemove);
}
