package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;


/**
 * The persistent class for the REGISTERED_USER_TOPIC database table.
 * 
 */
public class RegisteredUserTopic implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "REGISTERED_USER_TOPIC";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Field(name="seq_no")
	private int seqNo;

	//bi-directional many-to-one association to RegisteredUser
	private RegisteredUser registeredUser;

	//bi-directional many-to-one association to Topic
	private Topic topic;

	public RegisteredUserTopic() {
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

	public int getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public RegisteredUser getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUser registeredUser) {
		this.registeredUser = registeredUser;
	}

	public Topic getTopic() {
		return this.topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}