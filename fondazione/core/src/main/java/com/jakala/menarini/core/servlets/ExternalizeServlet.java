package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.ExternalSocialLinkResponseDto;
import com.jakala.menarini.core.dto.ExternalizeLinkResponseDto;
import com.jakala.menarini.core.dto.ExternalizeLinksDto;
import com.jakala.menarini.core.service.ExternalizeUrlService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;

@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/bin/api/externalizeService",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class ExternalizeServlet extends SlingAllMethodsServlet {

    @Reference
    private transient ExternalizeUrlService externalizeUrlService;
    @Reference
    private transient ResourceResolver resolver;
    private static final String APP_CONTENT = "application/json";

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        ExternalizeLinksDto requestDto = gson.fromJson(request.getReader(),ExternalizeLinksDto.class);
        switch(requestDto.getOp()) {
            case SOCIAL:
                List<ExternalSocialLinkResponseDto> responseDto = externalizeUrlService.
                        genarateSocialLink(this.resolver,requestDto.getTargetLink());
                response.setContentType(APP_CONTENT);
                response.getWriter().println(gson.toJson(responseDto));
                break;
            case REQUEST:
                ExternalizeLinkResponseDto responseRequestDto = new ExternalizeLinkResponseDto();
                String link = externalizeUrlService.getExternalizeUrlFromRequest(requestDto.getAbsolute(),
                        request,requestDto.getTargetLink());
                responseRequestDto.setLink(link);
                response.setContentType(APP_CONTENT);
                response.getWriter().println(gson.toJson(responseRequestDto));
                break;
            case REDIRECT:
                ExternalizeLinkResponseDto responseRedirectDto = new ExternalizeLinkResponseDto();
                String redirectLink = externalizeUrlService.getRedirect(resolver, requestDto.getPrevLink());
                responseRedirectDto.setLink(redirectLink);
                response.setContentType(APP_CONTENT);
                response.getWriter().println(gson.toJson(responseRedirectDto));
                break;
            case EXT_URL:
                ExternalizeLinkResponseDto responseExtDto = new ExternalizeLinkResponseDto();
                String extLink = externalizeUrlService.getExternalizeUrl(resolver,requestDto.getTargetLink());
                responseExtDto.setLink(extLink);
                response.setContentType(APP_CONTENT);
                response.getWriter().println(gson.toJson(responseExtDto));
                break;
            default:
                response.setStatus(400);
                response.setContentType(APP_CONTENT);
                response.getWriter().println("{\"error\":\"invalid operation\"}");
                break;
        }
    }


}
