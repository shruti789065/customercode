package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.entities.records.EventEnrollmentRecord;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface UserEventServiceInterface {



    public List<EventModelDto> getUserEvent(Long idUser, ResourceResolver resolver, String language);
    public List<EventEnrollmentRecord> getUserEnrollmentEvent(Long idUser);
    public boolean updateEventEnrollmentDate(String[] newDate, String phone, Long idUser, Long enrollmentId);
    public boolean unsubscribeEvent(Long idUser, Long enrollmentId);
    public boolean subscribeEvent(Long idUser, String idEvent, String[] enrollmentDate, String phone, ResourceResolver resolver, String language, RoleDto role);
}
