package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * MfinoTriggers generated by hbm2java
 */
@Entity
@Table(name = "MFINO_TRIGGERS")
public class MfinoTriggers implements java.io.Serializable {

	private MfinoTriggersId id;
	private MfinoJobDetails mfinoJobDetails;
	private String isVolatile;
	private String description;
	private Long nextFireTime;
	private Long prevFireTime;
	private Long priority;
	private String triggerState;
	private String triggerType;
	private long startTime;
	private Long endTime;
	private String calendarName;
	private Byte misfireInstr;
	private Blob jobData;
	private Set<MfinoTriggerListeners> mfinoTriggerListenerses = new HashSet<MfinoTriggerListeners>(
			0);
	private MfinoCronTriggers mfinoCronTriggers;
	private MfinoSimpleTriggers mfinoSimpleTriggers;
	private MfinoBlobTriggers mfinoBlobTriggers;

	public MfinoTriggers() {
	}

	public MfinoTriggers(MfinoTriggersId id, MfinoJobDetails mfinoJobDetails,
			String isVolatile, String triggerState, String triggerType,
			long startTime) {
		this.id = id;
		this.mfinoJobDetails = mfinoJobDetails;
		this.isVolatile = isVolatile;
		this.triggerState = triggerState;
		this.triggerType = triggerType;
		this.startTime = startTime;
	}

	public MfinoTriggers(MfinoTriggersId id, MfinoJobDetails mfinoJobDetails,
			String isVolatile, String description, Long nextFireTime,
			Long prevFireTime, Long priority, String triggerState,
			String triggerType, long startTime, Long endTime,
			String calendarName, Byte misfireInstr, Blob jobData,
			Set<MfinoTriggerListeners> mfinoTriggerListenerses,
			MfinoCronTriggers mfinoCronTriggers,
			MfinoSimpleTriggers mfinoSimpleTriggers,
			MfinoBlobTriggers mfinoBlobTriggers) {
		this.id = id;
		this.mfinoJobDetails = mfinoJobDetails;
		this.isVolatile = isVolatile;
		this.description = description;
		this.nextFireTime = nextFireTime;
		this.prevFireTime = prevFireTime;
		this.priority = priority;
		this.triggerState = triggerState;
		this.triggerType = triggerType;
		this.startTime = startTime;
		this.endTime = endTime;
		this.calendarName = calendarName;
		this.misfireInstr = misfireInstr;
		this.jobData = jobData;
		this.mfinoTriggerListenerses = mfinoTriggerListenerses;
		this.mfinoCronTriggers = mfinoCronTriggers;
		this.mfinoSimpleTriggers = mfinoSimpleTriggers;
		this.mfinoBlobTriggers = mfinoBlobTriggers;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "triggerName", column = @Column(name = "TRIGGER_NAME", nullable = false, length = 200)),
			@AttributeOverride(name = "triggerGroup", column = @Column(name = "TRIGGER_GROUP", nullable = false, length = 200)) })
	public MfinoTriggersId getId() {
		return this.id;
	}

	public void setId(MfinoTriggersId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "JOB_NAME", referencedColumnName = "JOB_NAME", nullable = false),
			@JoinColumn(name = "JOB_GROUP", referencedColumnName = "JOB_GROUP", nullable = false) })
	public MfinoJobDetails getMfinoJobDetails() {
		return this.mfinoJobDetails;
	}

	public void setMfinoJobDetails(MfinoJobDetails mfinoJobDetails) {
		this.mfinoJobDetails = mfinoJobDetails;
	}

	@Column(name = "IS_VOLATILE", nullable = false, length = 1)
	public String getIsVolatile() {
		return this.isVolatile;
	}

	public void setIsVolatile(String isVolatile) {
		this.isVolatile = isVolatile;
	}

	@Column(name = "DESCRIPTION", length = 250)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "NEXT_FIRE_TIME", precision = 13, scale = 0)
	public Long getNextFireTime() {
		return this.nextFireTime;
	}

	public void setNextFireTime(Long nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	@Column(name = "PREV_FIRE_TIME", precision = 13, scale = 0)
	public Long getPrevFireTime() {
		return this.prevFireTime;
	}

	public void setPrevFireTime(Long prevFireTime) {
		this.prevFireTime = prevFireTime;
	}

	@Column(name = "PRIORITY", precision = 13, scale = 0)
	public Long getPriority() {
		return this.priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	@Column(name = "TRIGGER_STATE", nullable = false, length = 16)
	public String getTriggerState() {
		return this.triggerState;
	}

	public void setTriggerState(String triggerState) {
		this.triggerState = triggerState;
	}

	@Column(name = "TRIGGER_TYPE", nullable = false, length = 8)
	public String getTriggerType() {
		return this.triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	@Column(name = "START_TIME", nullable = false, precision = 13, scale = 0)
	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Column(name = "END_TIME", precision = 13, scale = 0)
	public Long getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@Column(name = "CALENDAR_NAME", length = 200)
	public String getCalendarName() {
		return this.calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	@Column(name = "MISFIRE_INSTR", precision = 2, scale = 0)
	public Byte getMisfireInstr() {
		return this.misfireInstr;
	}

	public void setMisfireInstr(Byte misfireInstr) {
		this.misfireInstr = misfireInstr;
	}

	@Column(name = "JOB_DATA")
	public Blob getJobData() {
		return this.jobData;
	}

	public void setJobData(Blob jobData) {
		this.jobData = jobData;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mfinoTriggers")
	public Set<MfinoTriggerListeners> getMfinoTriggerListenerses() {
		return this.mfinoTriggerListenerses;
	}

	public void setMfinoTriggerListenerses(
			Set<MfinoTriggerListeners> mfinoTriggerListenerses) {
		this.mfinoTriggerListenerses = mfinoTriggerListenerses;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "mfinoTriggers")
	public MfinoCronTriggers getMfinoCronTriggers() {
		return this.mfinoCronTriggers;
	}

	public void setMfinoCronTriggers(MfinoCronTriggers mfinoCronTriggers) {
		this.mfinoCronTriggers = mfinoCronTriggers;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "mfinoTriggers")
	public MfinoSimpleTriggers getMfinoSimpleTriggers() {
		return this.mfinoSimpleTriggers;
	}

	public void setMfinoSimpleTriggers(MfinoSimpleTriggers mfinoSimpleTriggers) {
		this.mfinoSimpleTriggers = mfinoSimpleTriggers;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "mfinoTriggers")
	public MfinoBlobTriggers getMfinoBlobTriggers() {
		return this.mfinoBlobTriggers;
	}

	public void setMfinoBlobTriggers(MfinoBlobTriggers mfinoBlobTriggers) {
		this.mfinoBlobTriggers = mfinoBlobTriggers;
	}

}
