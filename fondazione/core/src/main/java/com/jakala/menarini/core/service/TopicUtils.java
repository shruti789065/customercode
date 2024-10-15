package com.jakala.menarini.core.service;


import com.jakala.menarini.core.dto.RegisteredUserTopicDto;
import com.jakala.menarini.core.dto.TopicDto;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDtoResponse;
import com.jakala.menarini.core.entities.RegisteredUserTopic;
import com.jakala.menarini.core.entities.records.RegisteredUserTopicRecord;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TopicUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegisteredService.class);

    public static List<String> getTopicsRefForUser(long id, DSLContext create) {
        ArrayList<String> topicsExtId = new ArrayList<>();
        try {
            List<RegisteredUserTopicRecord> userTopics = create.select()
                    .from(RegisteredUserTopic.REGISTERED_USER_TOPIC)
                    .where(RegisteredUserTopic.REGISTERED_USER_TOPIC.REGISTERED_USER_ID.eq(id))
                    .fetch().into(RegisteredUserTopicRecord.class);
            if(userTopics != null && !userTopics.isEmpty()) {
                for (RegisteredUserTopicRecord userTopic : userTopics) {
                    topicsExtId.add(userTopic.getTopicId());
                }
                return topicsExtId;
            }
            return topicsExtId;
        }catch (DataAccessException e) {
            return topicsExtId;
        }
    }


}