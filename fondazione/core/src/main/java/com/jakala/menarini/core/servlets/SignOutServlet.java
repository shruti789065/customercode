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

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            ArrayList<String> toDelete = new ArrayList<>();
            toDelete.add("p-idToken");
            toDelete.add("p-aToken");
            toDelete.add("p-rToken");
            cookieService.removeCookie(response, toDelete);
        }

    }



}
