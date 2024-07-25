package com.jakala.menarini.core.service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BeanProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.jakala.menarini.core.model.RegisteredUser;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;


@Component(service = UserRegisteredServiceInterface.class)
public class UserRegisteredService implements UserRegisteredServiceInterface {

    @Reference(target = "(datasource.name=fondazione-mysql)")
    private DataSource dataSource;

    @Override
    public List<RegisteredUser> getUsers() {
        List<RegisteredUser> users = new ArrayList<>();

        String sql = "SELECT * FROM " + RegisteredUser.table;

        try (Connection connection = dataSource.getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            BeanProcessor beanProcessor = new BeanProcessor();

            while (rs.next()) {
                users.add(beanProcessor.toBean(rs, RegisteredUser.class));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public boolean addUser(RegisteredUser user) {
        String sql = "INSERT INTO " + RegisteredUser.table + " (" +
            "birth_date, country, created_on, email, firstname, gender, last_updated_on, " +
            "lastname, legacy_id, linkedin_profile, newsletter_subscription, newsletter_subscription_ts, " +
            "occupation, personal_data_processing_consent, personal_data_processing_consent_ts, phone, " +
            "profiling_consent, profiling_consent_ts, tax_id_code, username) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setDate(1, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);
            stmt.setString(2, user.getCountry());
            stmt.setTimestamp(3, user.getCreatedOn());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getFirstname());
            stmt.setString(6, user.getGender());
            stmt.setTimestamp(7, user.getLastUpdatedOn());
            stmt.setString(8, user.getLastname());
            stmt.setInt(9, user.getLegacyId());
            stmt.setString(10, user.getLinkedinProfile());
            stmt.setString(11, user.getNewsletterSubscription());
            stmt.setTimestamp(12, user.getNewsletterSubscriptionTs());
            stmt.setString(13, user.getOccupation());
            stmt.setString(14, user.getPersonalDataProcessingConsent());
            stmt.setTimestamp(15, user.getPersonalDataProcessingConsentTs());
            stmt.setString(16, user.getPhone());
            stmt.setString(17, user.getProfilingConsent());
            stmt.setTimestamp(18, user.getProfilingConsentTs());
            stmt.setString(19, user.getTaxIdCode());
            stmt.setString(20, user.getUsername());
        
            stmt.executeUpdate();

            // NON FUNZIONA
            // Field[] fields = RegisteredUser.class.getDeclaredFields();
            // List<String> EXCLUDED_FIELDS = new ArrayList<>();
            // EXCLUDED_FIELDS.addAll(Arrays.asList("serialVersionUID", "id", "table"));
            // int index = 3;
            // for (Field field : fields) {
            //     if (EXCLUDED_FIELDS.contains(field.getName())){
            //         continue;
            //     }
            //     field.setAccessible(true);
            //     ps.setObject(index++, field.get(user));
            // }
            // ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
