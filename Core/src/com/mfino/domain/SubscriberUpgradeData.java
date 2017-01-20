package com.mfino.domain;

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
	
}
