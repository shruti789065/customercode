package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


public class RoleDto implements Serializable {
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
	private List<RegisteredUserRoleDto> registeredUserRoles;

	public RoleDto() {
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

	public List<RegisteredUserRoleDto> getRegisteredUserRoles() {
		return this.registeredUserRoles;
	}

	public void setRegisteredUserRoles(List<RegisteredUserRoleDto> registeredUserRoles) {
		this.registeredUserRoles = registeredUserRoles;
	}

	public RegisteredUserRoleDto addRegisteredUserRole(RegisteredUserRoleDto registeredUserRole) {
		getRegisteredUserRoles().add(registeredUserRole);
		registeredUserRole.setRole(this);

		return registeredUserRole;
	}

	public RegisteredUserRoleDto removeRegisteredUserRole(RegisteredUserRoleDto registeredUserRole) {
		getRegisteredUserRoles().remove(registeredUserRole);
		registeredUserRole.setRole(null);

		return registeredUserRole;
	}

}