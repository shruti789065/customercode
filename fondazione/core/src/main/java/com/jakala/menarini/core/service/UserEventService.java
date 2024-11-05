package com.jakala.menarini.core.service;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.entities.EventEnrollment;
import com.jakala.menarini.core.dto.EventEnrollmentExtraData;
import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.entities.records.EventEnrollmentRecord;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserEventServiceInterface;
import org.apache.sling.api.resource.ResourceResolver;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component(service = UserEventServiceInterface.class, immediate = true )
public class UserEventService implements UserEventServiceInterface {



    @Reference(target = "(datasource.name=fondazione-mysql)")
    private DataSource dataSource;

    @Reference
    private EventListingServiceInterface eventService;


    @Override
    public List<EventModelDto> getUserEvent(Long idUser, ResourceResolver resolver, String language) {
        List<EventModelDto> events = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            List<EventEnrollmentRecord> listEnrollment = Objects.requireNonNull(
                    create
                    .select().from(EventEnrollment.EVENT_ENROLLMENT)
                            .where(EventEnrollment.EVENT_ENROLLMENT.REGISTERED_USER_ID.eq(idUser))
                            .fetch().into(EventEnrollmentRecord.class)
            );
            if(!listEnrollment.isEmpty()) {
                List<String> eventIds = new ArrayList<>();
                for(EventEnrollmentRecord enroll : listEnrollment) {
                    eventIds.add(enroll.getEventId());
                }
                events = eventService.getEventsByIds(eventIds,resolver,language);
            }
            return events;
        } catch (SQLException e) {
            return List.of();
        }
    }

    @Override
    public List<EventEnrollmentRecord> getUserEnrollmentEvent(Long idUser) {

        try(Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            return Objects.requireNonNull(
                    create
                            .select().from(EventEnrollment.EVENT_ENROLLMENT)
                            .where(EventEnrollment.EVENT_ENROLLMENT.REGISTERED_USER_ID.eq(idUser))
                            .fetch().into(EventEnrollmentRecord.class)
            );

        } catch (SQLException e) {
            return List.of();
        }
    }


    @Override
    public boolean updateEventEnrollmentDate(String[] newDate, String phone, Long idUser, Long enrollmentId) {
        Gson gson = new Gson();
        try(Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            EventEnrollmentRecord enrollment = Objects.requireNonNull(
                    create.select().from(EventEnrollment.EVENT_ENROLLMENT)
                            .where(EventEnrollment.EVENT_ENROLLMENT.ID.eq(enrollmentId))
                            .fetchOne().into(EventEnrollmentRecord.class)
            );
            EventEnrollmentExtraData extraData = gson.fromJson(enrollment
                    .getInPersonParticipationDateList().data(),EventEnrollmentExtraData.class);
            List<Timestamp> timeDates = new ArrayList<>();
            for(int i = 0; i < newDate.length; i++) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = dateFormat.parse(newDate[i]);
                timeDates.add(new Timestamp(parsedDate.getTime()));
            }
            extraData.setEnrollmentDates(timeDates);
            if(phone != null && !phone.isBlank()) {
                extraData.setPhone(phone);
            }
            return this.saveUpdateEnrollmentData(enrollmentId,extraData);
        }catch (SQLException | ParseException | NullPointerException e) {
            return false;
        }
    }

    private boolean saveUpdateEnrollmentData(Long enrollmentId, EventEnrollmentExtraData data) {
        try(Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            Record updateRecord = this.setUpdateQuery(create,data);
            UpdateConditionStep<EventEnrollmentRecord> update = create.update(EventEnrollment.EVENT_ENROLLMENT)
                    .set(updateRecord)
                    .where(EventEnrollment.EVENT_ENROLLMENT.ID.eq(enrollmentId));
            update.execute();
            return true;

        }catch (SQLException e) {
            return false;
        }
    }

    private Record setUpdateQuery(DSLContext create, EventEnrollmentExtraData extraData) {
        Gson gson = new Gson();
        LocalDateTime now = LocalDateTime.now();
        Record updateRecord = create.newRecord(com.jakala.menarini.core
                .entities.EventEnrollment.EVENT_ENROLLMENT);
        updateRecord.set(com.jakala.menarini.core
                .entities.EventEnrollment.EVENT_ENROLLMENT.LAST_UPDATED_ON,now);
        updateRecord.set(com.jakala.menarini.core
                .entities.EventEnrollment.EVENT_ENROLLMENT.IN_PERSON_PARTICIPATION_DATE_LIST,JSON.json(gson.toJson(extraData)));
        return updateRecord;
    }

    @Override
    public boolean unsubscribeEvent(Long idUser, Long enrollmentId) {
        try(Connection connection = dataSource.getConnection()) {
            DSLContext delete = DSL.using(connection);
            DeleteConditionStep<EventEnrollmentRecord> deleteQuery = delete.deleteFrom(EventEnrollment.EVENT_ENROLLMENT)
                    .where(EventEnrollment.EVENT_ENROLLMENT.ID.eq(enrollmentId))
                    .and(EventEnrollment.EVENT_ENROLLMENT.REGISTERED_USER_ID.eq(idUser));
            deleteQuery.execute();
            return true;
        }catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean subscribeEvent(Long idUser, String idEvent, String[] enrollmentDate, String phone, ResourceResolver resolver, String language, RoleDto role) {
        Timestamp now =new Timestamp(System.currentTimeMillis());
        List<EventModelDto> events = eventService.getEventsByIds(List.of(idEvent),resolver,language);
        Gson gson = new Gson();
        if(events.size() == 1) {
            EventModelDto event = events.get(0);
            if(!this.checkIsSubscriptionPermission(event.getSubscription(),role)) {
                return false;
            }
            try (Connection connection = dataSource.getConnection()) {
                DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
                EventEnrollmentExtraData extraData = new EventEnrollmentExtraData();
                extraData.setPhone(phone);
                List<Timestamp> dates = new ArrayList<>();
                for(int i = 0; i < enrollmentDate.length; i++ ) {
                    dates.add(this.convertStringToTimestamp(enrollmentDate[i]));
                }
                extraData.setEnrollmentDates(dates);
                EventEnrollmentRecord enrollmentRecord = create.newRecord(EventEnrollment.EVENT_ENROLLMENT);
                String dataString = gson.toJson(extraData);
                enrollmentRecord.setInPersonParticipationDateList(JSON.json(dataString));
                enrollmentRecord.setEventId(event.getId());
                enrollmentRecord.setCreatedOn(now.toLocalDateTime());
                enrollmentRecord.setLastUpdatedOn(now.toLocalDateTime());
                boolean isResidential = !dates.isEmpty();
                enrollmentRecord.setIsResidential(convertBool(isResidential));
                enrollmentRecord.setIsLiveStream(convertBool(!isResidential));
                if(isResidential) {
                    enrollmentRecord.setResidentialRegistrationTs(LocalDateTime.now());
                }else {
                    enrollmentRecord.setLiveStreamRegistrationTs(LocalDateTime.now());
                }
                enrollmentRecord.setRegisteredUserId(idUser);
                enrollmentRecord.store();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    private boolean checkIsSubscriptionPermission(String subscription, RoleDto role) {
        if((subscription.equals("none") || subscription.equals("External") )) {
            return false;
        } else if (subscription.equals("All")){
            return true;
        } else {
            return role.getName().equals("UserHealthCare") || role.getName().equals("UserDoctor");
        }
    }

    private String convertBool(boolean bool) {
        return bool ? "1" : "0";
    }

    private Timestamp convertStringToTimestamp(String time) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date parsedDate = dateFormat.parse(time);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            return null;
        }

    }



}
