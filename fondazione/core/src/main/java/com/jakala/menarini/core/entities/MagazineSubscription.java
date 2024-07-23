package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the MAGAZINE_SUBSCRIPTION database table.
 * 
 */
@Entity
@Table(name="MAGAZINE_SUBSCRIPTION")
@NamedQuery(name="MagazineSubscription.findAll", query="SELECT m FROM MagazineSubscription m")
public class MagazineSubscription implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(length=2000)
	private String address1;

	@Column(length=2000)
	private String address2;

	@Column(name="albo_number")
	private int alboNumber;

	@Column(name="albo_province", length=30)
	private String alboProvince;

	@Column(length=60)
	private String city;

	@Column(length=30)
	private String country;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(length=255)
	private String firstname;

	@Column(name="graduation_year")
	private int graduationYear;

	@Column(name="have_been_doctor_or_pharmacist", length=1)
	private String haveBeenDoctorOrPharmacist;

	@Column(name="house_no", length=10)
	private String houseNo;

	@Column(length=10)
	private String language;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(length=255)
	private String lastname;

	@Column(length=255)
	private String magazine;

	@Column(length=30)
	private String phone;

	@Column(length=30)
	private String province;

	@Column(name="registration_year")
	private int registrationYear;

	@Column(name="tax_id_code", length=30)
	private String taxIdCode;

	@Column(length=15)
	private String zip;

	//bi-directional many-to-one association to RegisteredUser
	@ManyToOne
	@JoinColumn(name="registered_user_id")
	private RegisteredUser registeredUser;

	public MagazineSubscription() {
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

	public RegisteredUser getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUser registeredUser) {
		this.registeredUser = registeredUser;
	}

}