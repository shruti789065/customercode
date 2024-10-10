package com.jakala.menarini.core.servlets;

import com.jakala.menarini.core.service.interfaces.CookieServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.ArrayList;


@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/private/api/signout",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class SignOutServlet extends SlingAllMethodsServlet {


    @Reference
    private transient CookieServiceInterface cookieService;

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        ArrayList<String> toDelete = new ArrayList<String>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                toDelete.add(cookie.getName());
            }
            cookieService.removeCookie(response, toDelete);
        }

    }



}
