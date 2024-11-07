package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.*;
import com.jakala.menarini.core.entities.records.EventEnrollmentRecord;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.service.interfaces.JcrResolverServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserEventServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.jooq.JSON;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@SuppressWarnings("CQRules:CQBP-75")
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/private/api/enrollment",
                "sling.servlet.methods={GET,POST,PUT,DELETE}",
                "sling.servlet.extensions=json"
        }
)
public class UserEventServlet extends BaseRestServlet  {

    @Reference
    private transient UserEventServiceInterface userEventService;
    @Reference
    private transient UserRegisteredServiceInterface userService;
    @Reference
    private transient JcrResolverServiceInterface jcrResolver;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        final Set<Acl> acls = getAcls(request);
        RoleDto[] roles = (RoleDto[]) request.getSession().getAttribute("roles");
        String email = (String)request.getSession().getAttribute("userEmail");
        response.setContentType("application/json");
        try {
            RegisteredUserDto user = userService.getUserByEmail(email,acls, List.of(roles));
            List<EventEnrollmentRecord> enrollments = userEventService.getUserEnrollmentEvent((Long)user.getId());
            List<EventUserResponseDto> enrollmentsResponse = new ArrayList<>();
            for(EventEnrollmentRecord enroll : enrollments) {

                JSON exData = enroll.getInPersonParticipationDateList();
                EventEnrollmentExtraData data = gson.fromJson(exData.data(),EventEnrollmentExtraData.class);
                EventUserResponseDto enrollRes = new EventUserResponseDto();
                enrollRes.setId(enroll.getId());
                enrollRes.setEventId(Long.parseLong(enroll.getEventId()));
                enrollRes.setResidential(!Boolean.parseBoolean(enroll.getIsResidential()));
                enrollRes.setRegisterOn(
                        enroll.getLiveStreamRegistrationTs() != null ? Timestamp.valueOf(enroll.getLiveStreamRegistrationTs()) :
                                Timestamp.valueOf(enroll.getResidentialRegistrationTs())
                );
                enrollRes.setDates(data.getEnrollmentDates());
                enrollRes.setPhone(data.getPhone());
                enrollmentsResponse.add(enrollRes);
            }
            if(enrollments.isEmpty()) {
                response.setStatus(404);
            }
            response.getWriter().println(gson.toJson(enrollmentsResponse));
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println(gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        final Set<Acl> acls = getAcls(request);
        RoleDto[] roles = (RoleDto[]) request.getSession().getAttribute("roles");
        String email = (String)request.getSession().getAttribute("userEmail");
        try {
            RegisteredUserDto user = userService.getUserByEmail(email,acls, List.of(roles));
            EventUserRequestDto enrollData = gson.fromJson(request.getReader(),EventUserRequestDto.class);
            ResourceResolver resolver = jcrResolver.getResourceResolver();
            boolean success = userEventService.subscribeEvent(
                    Long.valueOf(user.getId()),
                    enrollData.getEventId(),
                    enrollData.getDates(),
                    enrollData.getPhone(),
                    resolver,
                    enrollData.getLang(),
                    roles[0]
            );
            if(!success) {
                response.setStatus(400);
            }
        } catch (SQLException | LoginException e) {
            response.setStatus(500);
            response.getWriter().println(gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        final Set<Acl> acls = getAcls(request);
        RoleDto[] roles = (RoleDto[]) request.getSession().getAttribute("roles");
        String email = (String)request.getSession().getAttribute("userEmail");
        try {
            RegisteredUserDto user = userService.getUserByEmail(email,acls, List.of(roles));
            EventUserUpdateRequestDto enrollData = gson.fromJson(request.getReader(),EventUserUpdateRequestDto.class);
            boolean success = userEventService.updateEventEnrollmentDate(
                    enrollData.getDates(),
                    enrollData.getPhone(),
                    Long.valueOf(user.getId()),
                    Long.valueOf(enrollData.getEnrollmentId())
            );
            if(!success) {
                response.setStatus(400);
            }
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println(gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        final Set<Acl> acls = getAcls(request);
        RoleDto[] roles = (RoleDto[]) request.getSession().getAttribute("roles");
        String email = (String)request.getSession().getAttribute("userEmail");
        try {
            RegisteredUserDto user = userService.getUserByEmail(email,acls, List.of(roles));
            EventUserUpdateRequestDto enrollData = gson.fromJson(request.getReader(),EventUserUpdateRequestDto.class);
            boolean success = userEventService.unsubscribeEvent(Long.valueOf(user.getId()),Long.valueOf(enrollData.getEnrollmentId()));
            if(!success) {
                response.setStatus(400);
            }
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println(gson.toJson(e.getMessage()));
        }
    }








}
