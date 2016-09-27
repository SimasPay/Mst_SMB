package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * MfinoTriggerListeners generated by hbm2java
 */
@Entity
@Table(name = "MFINO_TRIGGER_LISTENERS")
public class MfinoTriggerListeners implements java.io.Serializable {

	private MfinoTriggerListenersId id;
	private MfinoTriggers mfinoTriggers;

	public MfinoTriggerListeners() {
	}

	public MfinoTriggerListeners(MfinoTriggerListenersId id,
			MfinoTriggers mfinoTriggers) {
		this.id = id;
		this.mfinoTriggers = mfinoTriggers;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "triggerName", column = @Column(name = "TRIGGER_NAME", nullable = false, length = 200)),
			@AttributeOverride(name = "triggerGroup", column = @Column(name = "TRIGGER_GROUP", nullable = false, length = 200)),
			@AttributeOverride(name = "triggerListener", column = @Column(name = "TRIGGER_LISTENER", nullable = false, length = 200)) })
	public MfinoTriggerListenersId getId() {
		return this.id;
	}

	public void setId(MfinoTriggerListenersId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "TRIGGER_NAME", referencedColumnName = "TRIGGER_NAME", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "TRIGGER_GROUP", referencedColumnName = "TRIGGER_GROUP", nullable = false, insertable = false, updatable = false) })
	public MfinoTriggers getMfinoTriggers() {
		return this.mfinoTriggers;
	}

	public void setMfinoTriggers(MfinoTriggers mfinoTriggers) {
		this.mfinoTriggers = mfinoTriggers;
	}

}
