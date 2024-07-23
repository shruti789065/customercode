package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the REGISTERED_USER_TOPIC database table.
 * 
 */
@Entity
@Table(name="REGISTERED_USER_TOPIC")
@NamedQuery(name="RegisteredUserTopic.findAll", query="SELECT r FROM RegisteredUserTopic r")
public class RegisteredUserTopic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(name="seq_no")
	private int seqNo;

	//bi-directional many-to-one association to RegisteredUser
	@ManyToOne
	@JoinColumn(name="registered_user_id")
	private RegisteredUser registeredUser;

	//bi-directional many-to-one association to Topic
	@ManyToOne
	@JoinColumn(name="topic_id")
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