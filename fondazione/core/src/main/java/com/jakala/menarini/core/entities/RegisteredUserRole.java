package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the REGISTERED_USER_ROLE database table.
 * 
 */
@Entity
@Table(name="REGISTERED_USER_ROLE")
@NamedQuery(name="RegisteredUserRole.findAll", query="SELECT r FROM RegisteredUserRole r")
public class RegisteredUserRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	//bi-directional many-to-one association to RegisteredUser
	@ManyToOne
	@JoinColumn(name="registered_user_id")
	private RegisteredUser registeredUser;

	//bi-directional many-to-one association to Role
	@ManyToOne
	@JoinColumn(name="role_id")
	private Role role;

	public RegisteredUserRole() {
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

	public RegisteredUser getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUser registeredUser) {
		this.registeredUser = registeredUser;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}