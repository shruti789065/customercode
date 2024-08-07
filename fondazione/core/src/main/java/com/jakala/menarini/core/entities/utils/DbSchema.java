/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities.utils;


import java.util.Arrays;
import java.util.List;

import com.jakala.menarini.core.entities.Event;
import com.jakala.menarini.core.entities.EventEnrollment;
import com.jakala.menarini.core.entities.EventSessionDate;
import com.jakala.menarini.core.entities.EventSpeaker;
import com.jakala.menarini.core.entities.EventTopic;
import com.jakala.menarini.core.entities.Location;
import com.jakala.menarini.core.entities.MagazineSubscription;
import com.jakala.menarini.core.entities.NewsletterSubscription;
import com.jakala.menarini.core.entities.RegisteredUser;
import com.jakala.menarini.core.entities.RegisteredUserRole;
import com.jakala.menarini.core.entities.RegisteredUserTopic;
import com.jakala.menarini.core.entities.Role;
import com.jakala.menarini.core.entities.Speaker;
import com.jakala.menarini.core.entities.Topic;
import com.jakala.menarini.core.entities.Venue;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DbSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>menarini_dev</code>
     */
    public static final DbSchema MENARINI = new DbSchema();

    /**
     * The table <code>menarini_dev.EVENT</code>.
     */
    public final Event EVENT = Event.EVENT;

    /**
     * The table <code>menarini_dev.EVENT_ENROLLMENT</code>.
     */
    public final EventEnrollment EVENT_ENROLLMENT = EventEnrollment.EVENT_ENROLLMENT;

    /**
     * The table <code>menarini_dev.EVENT_SESSION_DATE</code>.
     */
    public final EventSessionDate EVENT_SESSION_DATE = EventSessionDate.EVENT_SESSION_DATE;

    /**
     * The table <code>menarini_dev.EVENT_SPEAKER</code>.
     */
    public final EventSpeaker EVENT_SPEAKER = EventSpeaker.EVENT_SPEAKER;

    /**
     * The table <code>menarini_dev.EVENT_TOPIC</code>.
     */
    public final EventTopic EVENT_TOPIC = EventTopic.EVENT_TOPIC;

    /**
     * The table <code>menarini_dev.LOCATION</code>.
     */
    public final Location LOCATION = Location.LOCATION;

    /**
     * The table <code>menarini_dev.MAGAZINE_SUBSCRIPTION</code>.
     */
    public final MagazineSubscription MAGAZINE_SUBSCRIPTION = MagazineSubscription.MAGAZINE_SUBSCRIPTION;

    /**
     * The table <code>menarini_dev.NEWSLETTER_SUBSCRIPTION</code>.
     */
    public final NewsletterSubscription NEWSLETTER_SUBSCRIPTION = NewsletterSubscription.NEWSLETTER_SUBSCRIPTION;

    /**
     * The table <code>menarini_dev.REGISTERED_USER</code>.
     */
    public final RegisteredUser REGISTERED_USER = RegisteredUser.REGISTERED_USER;

    /**
     * The table <code>menarini_dev.REGISTERED_USER_ROLE</code>.
     */
    public final RegisteredUserRole REGISTERED_USER_ROLE = RegisteredUserRole.REGISTERED_USER_ROLE;

    /**
     * The table <code>menarini_dev.REGISTERED_USER_TOPIC</code>.
     */
    public final RegisteredUserTopic REGISTERED_USER_TOPIC = RegisteredUserTopic.REGISTERED_USER_TOPIC;

    /**
     * The table <code>menarini_dev.ROLE</code>.
     */
    public final Role ROLE = Role.ROLE;

    /**
     * The table <code>menarini_dev.SPEAKER</code>.
     */
    public final Speaker SPEAKER = Speaker.SPEAKER;

    /**
     * The table <code>menarini_dev.TOPIC</code>.
     */
    public final Topic TOPIC = Topic.TOPIC;

    /**
     * The table <code>menarini_dev.VENUE</code>.
     */
    public final Venue VENUE = Venue.VENUE;

    /**
     * No further instances allowed
     */
    private DbSchema() {
        super("menarini_dev", null);
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Event.EVENT,
            EventEnrollment.EVENT_ENROLLMENT,
            EventSessionDate.EVENT_SESSION_DATE,
            EventSpeaker.EVENT_SPEAKER,
            EventTopic.EVENT_TOPIC,
            Location.LOCATION,
            MagazineSubscription.MAGAZINE_SUBSCRIPTION,
            NewsletterSubscription.NEWSLETTER_SUBSCRIPTION,
            RegisteredUser.REGISTERED_USER,
            RegisteredUserRole.REGISTERED_USER_ROLE,
            RegisteredUserTopic.REGISTERED_USER_TOPIC,
            Role.ROLE,
            Speaker.SPEAKER,
            Topic.TOPIC,
            Venue.VENUE
        );
    }
}
