package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;


public class MagazineSubscriptionDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "MAGAZINE_SUBSCRIPTION";

	private long id;

	private String address1;

	private String address2;

	@Field(name="albo_number")
	private int alboNumber;

	@Field(name="albo_province")
	private String alboProvince;

	private String city;

	private String country;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String firstname;

	@Field(name="graduation_year")
	private int graduationYear;

	@Field(name="have_been_doctor_or_pharmacist")
	private String haveBeenDoctorOrPharmacist;

	@Field(name="house_no")
	private String houseNo;

	private String language;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String lastname;

	private String magazine;

	private String phone;

	private String province;

	@Field(name="registration_year")
	private int registrationYear;

	@Field(name="tax_id_code")
	private String taxIdCode;

	private String zip;

	//bi-directional many-to-one association to RegisteredUser
	private RegisteredUserDto registeredUser;

	public MagazineSubscriptionDto() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public int getAlboNumber() {
		return this.alboNumber;
	}

	public void setAlboNumber(int alboNumber) {
		this.alboNumber = alboNumber;
	}

	public String getAlboProvince() {
		return this.alboProvince;
	}

	public void setAlboProvince(String alboProvince) {
		this.alboProvince = alboProvince;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public int getGraduationYear() {
		return this.graduationYear;
	}

	public void setGraduationYear(int graduationYear) {
		this.graduationYear = graduationYear;
	}

	public String getHaveBeenDoctorOrPharmacist() {
		return this.haveBeenDoctorOrPharmacist;
	}

	public void setHaveBeenDoctorOrPharmacist(String haveBeenDoctorOrPharmacist) {
		this.haveBeenDoctorOrPharmacist = haveBeenDoctorOrPharmacist;
	}

	public String getHouseNo() {
		return this.houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getMagazine() {
		return this.magazine;
	}

	public void setMagazine(String magazine) {
		this.magazine = magazine;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public int getRegistrationYear() {
		return this.registrationYear;
	}

	public void setRegistrationYear(int registrationYear) {
		this.registrationYear = registrationYear;
	}

	public String getTaxIdCode() {
		return this.taxIdCode;
	}

	public void setTaxIdCode(String taxIdCode) {
		this.taxIdCode = taxIdCode;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public RegisteredUserDto getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUserDto registeredUser) {
		this.registeredUser = registeredUser;
	}

}