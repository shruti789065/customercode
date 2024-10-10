package com.jakala.menarini.core.servlets;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;

@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/private/api/isSignIn",
                "sling.servlet.methods={GET}",
                "sling.servlet.extensions=json"

        }
)
public class UserCheckSignIn extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String[] COOKIE_NAMES = {"p-idToken","p-aToken","p-rToken"};
        Cookie[] cookies = request.getCookies();
        int found = 0;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Arrays.asList(COOKIE_NAMES).contains(cookie.getName())) {
                    found++;
                }
            }
            if(found >= COOKIE_NAMES.length) {
                response.setStatus(200);
            }
        } else {
            response.setStatus(401);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"isAuth\":\"true\"}");
    }

}