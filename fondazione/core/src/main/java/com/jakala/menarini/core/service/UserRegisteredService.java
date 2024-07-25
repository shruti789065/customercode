package com.jakala.menarini.core.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RegisteredUser user = new RegisteredUser();
                user.setId(rs.getLong("id"));
                user.setBirthDate(rs.getDate("birth_date"));
                user.setCountry(rs.getString("country"));
                user.setCreatedOn(rs.getTimestamp("created_on"));
                user.setEmail(rs.getString("email"));
                user.setFirstname(rs.getString("firstname"));
                user.setGender(rs.getString("gender"));
                user.setLastUpdatedOn(rs.getTimestamp("last_updated_on"));
                user.setLastname(rs.getString("lastname"));
                user.setLegacyId(rs.getInt("legacy_id"));
                user.setLinkedinProfile(rs.getString("linkedin_profile"));
                user.setNewsletterSubscription(rs.getString("newsletter_subscription"));
                user.setNewsletterSubscriptionTs(rs.getTimestamp("newsletter_subscription_ts"));
                user.setOccupation(rs.getString("occupation"));
                user.setPersonalDataProcessingConsent(rs.getString("personal_data_processing_consent"));
                user.setPersonalDataProcessingConsentTs(rs.getTimestamp("personal_data_processing_consent_ts"));
                user.setPhone(rs.getString("phone"));
                user.setProfilingConsent(rs.getString("profiling_consent"));
                user.setProfilingConsentTs(rs.getTimestamp("profiling_consent_ts"));
                user.setTaxIdCode(rs.getString("tax_id_code"));
                user.setUsername(rs.getString("username"));

                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}
