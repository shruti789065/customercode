package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;

@SuppressWarnings("squid:S2384")
public class RegisteredUserRoleDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String table = "REGISTERED_USER_ROLE";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	//bi-directional many-to-one association to RegisteredUser
	private RegisteredUserDto registeredUser;

	//bi-directional many-to-one association to Role
	private RoleDto role;

	public RegisteredUserRoleDto() {
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

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public RegisteredUserDto getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUserDto registeredUser) {
		this.registeredUser = registeredUser;
	}

	public RoleDto getRole() {
		return this.role;
	}

	public void setRole(RoleDto role) {
		this.role = role;
	}

}