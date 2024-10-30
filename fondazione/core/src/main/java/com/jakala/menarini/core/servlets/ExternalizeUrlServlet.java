package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.ExternalSocialLinkResponseDto;
import com.jakala.menarini.core.dto.ExternalizeLinkResponseDto;
import com.jakala.menarini.core.dto.ExternalizeLinksDto;
import com.jakala.menarini.core.service.interfaces.ExternalizeUrlServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;

@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/bin/api/externalizeUrl",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class ExternalizeUrlServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalizeUrlServlet.class);


    @Reference
    private transient ExternalizeUrlServiceInterface externalizeUrlService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resolver = request.getResource().getResourceResolver();
        Gson gson = new Gson();
        ExternalizeLinksDto requestDto = gson.fromJson(request.getReader(),ExternalizeLinksDto.class);
        response.setContentType("application/json");
        switch(requestDto.getOp()) {
            case SOCIAL:
                LOGGER.error("================== SOCIAL =============");
                String shareLink = (requestDto.getTargetLink() != null && !requestDto.getTargetLink().isBlank())
                        ? requestDto.getTargetLink() : request.getHeader("Referer");
                List<ExternalSocialLinkResponseDto> responseDto = externalizeUrlService.
                        genarateSocialLink(resolver,shareLink);
                response.getWriter().println(gson.toJson(responseDto));
                break;
            case REQUEST:
                LOGGER.error("================== Request =============");
                ExternalizeLinkResponseDto responseRequestDto = new ExternalizeLinkResponseDto();
                String link = externalizeUrlService.getExternalizeUrlFromRequest(requestDto.getAbsolute(),
                        request,requestDto.getTargetLink());
                responseRequestDto.setLink(link);
                response.getWriter().println(gson.toJson(responseRequestDto));
                break;
            case REDIRECT:
                LOGGER.error("================== Redirect =============");
                ExternalizeLinkResponseDto responseRedirectDto = new ExternalizeLinkResponseDto();
                String redirectLink = externalizeUrlService.getRedirect(resolver, requestDto.getPrevLink(),
                        requestDto.getTargetLink());
                responseRedirectDto.setLink(redirectLink);
                response.getWriter().println(gson.toJson(responseRedirectDto));
                break;
            case EXT_URL:
                LOGGER.error("================== External =============");
                ExternalizeLinkResponseDto responseExtDto = new ExternalizeLinkResponseDto();
                String extLink = externalizeUrlService.getExternalizeUrl(resolver,requestDto.getTargetLink());
                responseExtDto.setLink(extLink);
                response.getWriter().println(gson.toJson(responseExtDto));
                break;
            default:
                LOGGER.error("================== Default =============");
                response.setStatus(400);
                response.getWriter().println("{\"error\":\"invalid operation\"}");
                break;
        }
    }


}
