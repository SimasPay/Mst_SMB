package com.mfino.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

@Entity
@Table(name = "SUBSCRIBER_UPGRADE_DATA")
public class SubscriberUpgradeData extends Base implements java.io.Serializable  {
	private static final long serialVersionUID = 1L;
	public static final String FieldName_MdnId = "mdnId";
	public static final String FieldName_SubsActivityStatus = "subsActivityStatus";
	public static final String FieldName_SubActivity = "subActivity";
	
	private Long id;
	private String fullName;
	private String email;
	private String idType;
	private String idNumber;
	private String birthPlace;
	private Timestamp birthDate;
	private String motherMaidenName;
	private String idCardScanPath;
	private Long mdnId;
	private Address address;
	private Integer subActivity;
	private Integer subsActivityStatus;
	private String  subsActivityApprovedBY;
	private Timestamp subsActivityAprvTime;
	private String  subsActivityComments;
	private String  applicationId;
	private String  bankAccountNumber;

	private String comments;
	private Integer adminAction;

	private Integer language;
	private Integer notificationMethod;
	private Integer subscriberRestriction;
	private Integer subscriberStatus;

	private String nationality;
	private String job;
	private String otherJob;
	private String gender;
	private String maritalStatus;
	private String sourceOfFund;
	private BigDecimal avgMonthlyIncome;
	private String emoneyOpeningPurpose;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "SUBSCRIBER_UPGRADE_DATA_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "FULLNAME")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Column(name = "EMAIL")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "IDTYPE")
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}

	@Column(name = "IDNUMBER")
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	@Column(name = "BIRTH_PLACE")
	public String getBirthPlace() {
		return birthPlace;
	}
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
	
	@Type(type = "userDefinedTimeStamp")
	@Column(name = "BIRTH_DATE")
	public Timestamp getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Timestamp birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name = "MOTHER_MAIDEN_NAME")
	public String getMotherMaidenName() {
		return motherMaidenName;
	}
	public void setMotherMaidenName(String motherMaidenName) {
		this.motherMaidenName = motherMaidenName;
	}

	@Column(name = "IDCARD_SCAN_PATH")
	public String getIdCardScanPath() {
		return idCardScanPath;
	}
	public void setIdCardScanPath(String idCardScanPath) {
		this.idCardScanPath = idCardScanPath;
	}

	@Column(name = "MDNID")
	public Long getMdnId() {
		return mdnId;
	}
	public void setMdnId(Long mdnId) {
		this.mdnId = mdnId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ADDRESS_ID")
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	@Column(name = "SUBSCRIBER_ACTIVITY")
	public Integer getSubActivity() {
		return subActivity;
	}
	public void setSubActivity(Integer subActivity) {
		this.subActivity = subActivity;
	}
	
	@Column(name = "SUBSCRIBER_ACTIVITY_STATUS")
	public Integer getSubsActivityStatus() {
		return subsActivityStatus;
	}
	public void setSubsActivityStatus(Integer subsActivityStatus) {
		this.subsActivityStatus = subsActivityStatus;
	}
	
	@Column(name = "SUBSCRIBER_ACTIVITY_APPROVEDBY")
	public String getSubsActivityApprovedBY() {
		return subsActivityApprovedBY;
	}
	public void setSubsActivityApprovedBY(String subsActivityApprovedBY) {
		this.subsActivityApprovedBY = subsActivityApprovedBY;
	}
	
	@Type(type = "userDefinedTimeStamp")
	@Column(name = "SUBSCRIBER_ACTIVITY_APPROVTIME")
	public Timestamp getSubsActivityAprvTime() {
		return subsActivityAprvTime;
	}
	public void setSubsActivityAprvTime(Timestamp subsActivityAprvTime) {
		this.subsActivityAprvTime = subsActivityAprvTime;
	}
	
	@Column(name = "SUBSCRIBER_ACTIVITY_COMMENTS")
	public String getSubsActivityComments() {
		return subsActivityComments;
	}
	public void setSubsActivityComments(String subsActivityComments) {
		this.subsActivityComments = subsActivityComments;
	}
	
	@Column(name = "APPLICATIONID")
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	@Column(name = "BANKACCOUNTNUMBER")
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	
	@Column(name = "COMMENTS")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Column(name = "ADMINACTION")
	public Integer getAdminAction() {
		return adminAction;
	}
	public void setAdminAction(Integer adminAction) {
		this.adminAction = adminAction;
	}
	
	@Column(name = "LANGUAGE")
	public Integer getLanguage() {
		return language;
	}
	public void setLanguage(Integer language) {
		this.language = language;
	}
	
	@Column(name = "NOTIFICATION_METHOD")
	public Integer getNotificationMethod() {
		return notificationMethod;
	}
	public void setNotificationMethod(Integer notificationMethod) {
		this.notificationMethod = notificationMethod;
	}
	
	@Column(name = "SUBSCRIBER_RESTRICTION")
	public Integer getSubscriberRestriction() {
		return subscriberRestriction;
	}
	public void setSubscriberRestriction(Integer subscriberRestriction) {
		this.subscriberRestriction = subscriberRestriction;
	}
	@Column(name = "SUBSCRIBER_STATUS")
	public Integer getSubscriberStatus() {
		return subscriberStatus;
	}
	public void setSubscriberStatus(Integer subscriberStatus) {
		this.subscriberStatus = subscriberStatus;
	}
	
	@Column(name = "NATIONALITY", length = 32)
	public String getNationality() {
		return this.nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	@Column(name = "JOB", length = 32)
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	@Column(name = "OTHERJOB", length = 32)
	public String getOtherJob() {
		return otherJob;
	}
	public void setOtherJob(String otherJob) {
		this.otherJob = otherJob;
	}

	@Column(name = "GENDER", length = 32)
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = "MARITALSTATUS", length = 32)
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	@Column(name = "SOURCEOFFUND", length = 32)
	public String getSourceOfFund() {
		return sourceOfFund;
	}
	public void setSourceOfFund(String sourceOfFund) {
		this.sourceOfFund = sourceOfFund;
	}
	
	@Column(name = "MONTHLYINCOME", length = 32)
	public BigDecimal getAvgMonthlyIncome() {
		return avgMonthlyIncome;
	}
	public void setAvgMonthlyIncome(BigDecimal avgMonthlyIncome) {
		this.avgMonthlyIncome = avgMonthlyIncome;
	}
	
	@Column(name = "ACCTOPENINGPURPOSE", length = 32)
	public String getEmoneyOpeningPurpose() {
		return emoneyOpeningPurpose;
	}
	public void setEmoneyOpeningPurpose(String emoneyOpeningPurpose) {
		this.emoneyOpeningPurpose = emoneyOpeningPurpose;
	}
}
