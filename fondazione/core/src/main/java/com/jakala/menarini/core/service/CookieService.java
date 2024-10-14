package com.jakala.menarini.core.service;


import com.jakala.menarini.core.service.interfaces.CookieServiceInterface;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@Component(
        service = CookieServiceInterface.class,
        immediate = true
)
public class CookieService implements CookieServiceInterface {


    private int longDuration;
    private int shortDuration;

    @Activate
    private void activate(Map<String, Object> properties) {
        this.longDuration = Integer.parseInt(properties.get("longDuration").toString());
    }


    @Override
    public void setCookie(HttpServletResponse response, Map<String, String> mapCookie, boolean longDuration) {
        for (String key : mapCookie.keySet()) {
            Cookie cookie = new Cookie(key, mapCookie.get(key));
            cookie.setPath("/");
            if (longDuration) {
                cookie.setMaxAge(this.longDuration);
            }
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);
        }
    }

    @Override
    public void removeCookie(HttpServletResponse response, List<String> cookieToRemove) {
        for (String key : cookieToRemove) {
            Cookie cookie = new Cookie(key, "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);
        }

    }
}
