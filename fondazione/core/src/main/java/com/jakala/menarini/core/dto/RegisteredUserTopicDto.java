package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;


public class RegisteredUserTopicDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "REGISTERED_USER_TOPIC";

	private String id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Field(name="seq_no")
	private int seqNo;

	//bi-directional many-to-one association to RegisteredUser
	private RegisteredUserDto registeredUser;

	//bi-directional many-to-one association to Topic
	private TopicDto topic;

	public RegisteredUserTopicDto() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
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

	public RegisteredUserDto getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUserDto registeredUser) {
		this.registeredUser = registeredUser;
	}

	public TopicDto getTopic() {
		return this.topic;
	}

	public void setTopic(TopicDto topic) {
		this.topic = topic;
	}

}