package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class MagazineSubscriptionDtoTest {

    @Test
    void testGettersAndSetters() {
        MagazineSubscriptionDto dto = new MagazineSubscriptionDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId(1L);
        dto.setAddress1("Address 1");
        dto.setAddress2("Address 2");
        dto.setAlboNumber(123);
        dto.setAlboProvince("Province");
        dto.setCity("City");
        dto.setCountry("Country");
        dto.setCreatedOn(timestamp);
        dto.setFirstname("First Name");
        dto.setGraduationYear(2020);
        dto.setHaveBeenDoctorOrPharmacist("Yes");
        dto.setHouseNo("123");
        dto.setLanguage("English");
        dto.setLastUpdatedOn(timestamp);
        dto.setLastname("Last Name");
        dto.setMagazine("Magazine");
        dto.setPhone("1234567890");
        dto.setProvince("Province");
        dto.setRegistrationYear(2021);
        dto.setTaxIdCode("Tax ID");
        dto.setZip("12345");
        RegisteredUserDto registeredUser = new RegisteredUserDto();
        dto.setRegisteredUser(registeredUser);

        assertEquals(1L, dto.getId());
        assertEquals("Address 1", dto.getAddress1());
        assertEquals("Address 2", dto.getAddress2());
        assertEquals(123, dto.getAlboNumber());
        assertEquals("Province", dto.getAlboProvince());
        assertEquals("City", dto.getCity());
        assertEquals("Country", dto.getCountry());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals("First Name", dto.getFirstname());
        assertEquals(2020, dto.getGraduationYear());
        assertEquals("Yes", dto.getHaveBeenDoctorOrPharmacist());
        assertEquals("123", dto.getHouseNo());
        assertEquals("English", dto.getLanguage());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Last Name", dto.getLastname());
        assertEquals("Magazine", dto.getMagazine());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("Province", dto.getProvince());
        assertEquals(2021, dto.getRegistrationYear());
        assertEquals("Tax ID", dto.getTaxIdCode());
        assertEquals("12345", dto.getZip());
        assertEquals(registeredUser, dto.getRegisteredUser());
    }
}