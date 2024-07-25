package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


/**
 * The persistent class for the ROLE database table.
 * 
 */
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "ROLE";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String description;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String name;

	//bi-directional many-to-one association to RegisteredUserRole
	private List<RegisteredUserRole> registeredUserRoles;

	public Role() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RegisteredUserRole> getRegisteredUserRoles() {
		return this.registeredUserRoles;
	}

	public void setRegisteredUserRoles(List<RegisteredUserRole> registeredUserRoles) {
		this.registeredUserRoles = registeredUserRoles;
	}

	public RegisteredUserRole addRegisteredUserRole(RegisteredUserRole registeredUserRole) {
		getRegisteredUserRoles().add(registeredUserRole);
		registeredUserRole.setRole(this);

		return registeredUserRole;
	}

	public RegisteredUserRole removeRegisteredUserRole(RegisteredUserRole registeredUserRole) {
		getRegisteredUserRoles().remove(registeredUserRole);
		registeredUserRole.setRole(null);

		return registeredUserRole;
	}

}