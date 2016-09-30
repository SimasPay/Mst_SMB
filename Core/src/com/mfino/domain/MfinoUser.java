package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.mfino.hibernate.Timestamp;

/**
 * MfinoUser generated by hbm2java
 */
@Entity
@Table(name = "MFINO_USER", uniqueConstraints = @UniqueConstraint(columnNames = {
		"MSPID", "USERNAME" }))
public class MfinoUser  extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FieldName_mFinoServiceProviderByMSPID = "mfinoServiceProvider";
	public static final String FieldName_Username = "username";
	public static final String FieldName_ConfirmationTime = "confirmationtime";
	public static final String FieldName_UserActivationTime = "useractivationtime";
	public static final String FieldName_FirstName = "firstname";
	public static final String FieldName_LastName = "lastname";
	public static final String FieldName_UserStatus = "status";
	public static final String FieldName_Company = "company";
	public static final String FieldName_UserRestrictions = "restrictions";
	public static final String FieldName_Role = "role";
	private MfinoServiceProvider mfinoServiceProvider;
	private Company company;
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private String email;
	private long language;
	private String timezone;
	private long restrictions;
	private long status;
	private Timestamp statustime;
	private long failedlogincount;
	private Short firsttimelogin;
	private Timestamp lastlogintime;
	private String admincomment;
	private Long role;
	private String securityquestion;
	private String securityanswer;
	private Timestamp confirmationtime;
	private Timestamp useractivationtime;
	private Timestamp rejectiontime;
	private Timestamp expirationtime;
	private String confirmationcode;
	private Timestamp dateofbirth;
	private String forgotpasswordcode;
	private String homephone;
	private String workphone;
	private String oldhomephone;
	private String oldworkphone;
	private String oldsecurityquestion;
	private String oldsecurityanswer;
	private String oldfirstname;
	private String oldlastname;
	private Timestamp lastpasswordchangetime;
	private String passwordhistory;
	private Short isloggedin;
	private Long branchcodeid;
	private Set<Subscriber> subscribersForSubscriberuserid = new HashSet<Subscriber>(
			0);
	private Set<SMSPartner> smsPartners = new HashSet<SMSPartner>(0);
	private Set<Subscriber> subscribersForUserid = new HashSet<Subscriber>(0);
	private Set<BulkUpload> bulkUploads = new HashSet<BulkUpload>(0);
	private Set<Partner> partners = new HashSet<Partner>(0);
	private Set<BankAdmin> bankAdmins = new HashSet<BankAdmin>(0);

	public MfinoUser() {
	}

	public MfinoUser(BigDecimal id, MfinoServiceProvider mfinoServiceProvider,
			Company company, Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, long language,
			long restrictions, long status, Timestamp statustime,
			long failedlogincount) {
		this.id = id;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.language = language;
		this.restrictions = restrictions;
		this.status = status;
		this.statustime = statustime;
		this.failedlogincount = failedlogincount;
	}

	public MfinoUser(BigDecimal id, MfinoServiceProvider mfinoServiceProvider,
			Company company, Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String username,
			String password, String firstname, String lastname, String email,
			long language, String timezone, long restrictions, long status,
			Timestamp statustime, long failedlogincount,
			Short firsttimelogin, Timestamp lastlogintime,
			String admincomment, Long role, String securityquestion,
			String securityanswer, Timestamp confirmationtime,
			Timestamp useractivationtime, Timestamp rejectiontime,
			Timestamp expirationtime, String confirmationcode,
			Timestamp dateofbirth, String forgotpasswordcode,
			String homephone, String workphone, String oldhomephone,
			String oldworkphone, String oldsecurityquestion,
			String oldsecurityanswer, String oldfirstname, String oldlastname,
			Timestamp lastpasswordchangetime, String passwordhistory,
			Short isloggedin, Long branchcodeid,
			Set<Subscriber> subscribersForSubscriberuserid,
			Set<SMSPartner> smsPartners, Set<Subscriber> subscribersForUserid,
			Set<BulkUpload> bulkUploads, Set<Partner> partners,
			Set<BankAdmin> bankAdmins) {
		this.id = id;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.language = language;
		this.timezone = timezone;
		this.restrictions = restrictions;
		this.status = status;
		this.statustime = statustime;
		this.failedlogincount = failedlogincount;
		this.firsttimelogin = firsttimelogin;
		this.lastlogintime = lastlogintime;
		this.admincomment = admincomment;
		this.role = role;
		this.securityquestion = securityquestion;
		this.securityanswer = securityanswer;
		this.confirmationtime = confirmationtime;
		this.useractivationtime = useractivationtime;
		this.rejectiontime = rejectiontime;
		this.expirationtime = expirationtime;
		this.confirmationcode = confirmationcode;
		this.dateofbirth = dateofbirth;
		this.forgotpasswordcode = forgotpasswordcode;
		this.homephone = homephone;
		this.workphone = workphone;
		this.oldhomephone = oldhomephone;
		this.oldworkphone = oldworkphone;
		this.oldsecurityquestion = oldsecurityquestion;
		this.oldsecurityanswer = oldsecurityanswer;
		this.oldfirstname = oldfirstname;
		this.oldlastname = oldlastname;
		this.lastpasswordchangetime = lastpasswordchangetime;
		this.passwordhistory = passwordhistory;
		this.isloggedin = isloggedin;
		this.branchcodeid = branchcodeid;
		this.subscribersForSubscriberuserid = subscribersForSubscriberuserid;
		this.smsPartners = smsPartners;
		this.subscribersForUserid = subscribersForUserid;
		this.bulkUploads = bulkUploads;
		this.partners = partners;
		this.bankAdmins = bankAdmins;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSPID", nullable = false)
	public MfinoServiceProvider getMfinoServiceProvider() {
		return this.mfinoServiceProvider;
	}

	public void setMfinoServiceProvider(
			MfinoServiceProvider mfinoServiceProvider) {
		this.mfinoServiceProvider = mfinoServiceProvider;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANYID", nullable = false)
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	

	@Column(name = "USERNAME", length = 1020)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "PASSWORD", length = 1020)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "FIRSTNAME", length = 1020)
	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Column(name = "LASTNAME", length = 1020)
	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Column(name = "EMAIL", length = 1020)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "LANGUAGE", nullable = false, precision = 10, scale = 0)
	public long getLanguage() {
		return this.language;
	}

	public void setLanguage(long language) {
		this.language = language;
	}

	@Column(name = "TIMEZONE", length = 1020)
	public String getTimezone() {
		return this.timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@Column(name = "RESTRICTIONS", nullable = false, precision = 10, scale = 0)
	public long getRestrictions() {
		return this.restrictions;
	}

	public void setRestrictions(long restrictions) {
		this.restrictions = restrictions;
	}

	@Column(name = "STATUS", nullable = false, precision = 10, scale = 0)
	public long getStatus() {
		return this.status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	@Column(name = "STATUSTIME", nullable = false)
	public Timestamp getStatustime() {
		return this.statustime;
	}

	public void setStatustime(Timestamp statustime) {
		this.statustime = statustime;
	}

	@Column(name = "FAILEDLOGINCOUNT", nullable = false, precision = 10, scale = 0)
	public long getFailedlogincount() {
		return this.failedlogincount;
	}

	public void setFailedlogincount(long failedlogincount) {
		this.failedlogincount = failedlogincount;
	}

	@Column(name = "FIRSTTIMELOGIN", precision = 3, scale = 0)
	public Short getFirsttimelogin() {
		return this.firsttimelogin;
	}

	public void setFirsttimelogin(Short firsttimelogin) {
		this.firsttimelogin = firsttimelogin;
	}

	@Column(name = "LASTLOGINTIME")
	public Timestamp getLastlogintime() {
		return this.lastlogintime;
	}

	public void setLastlogintime(Timestamp lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

	@Column(name = "ADMINCOMMENT", length = 1020)
	public String getAdmincomment() {
		return this.admincomment;
	}

	public void setAdmincomment(String admincomment) {
		this.admincomment = admincomment;
	}

	@Column(name = "ROLE", precision = 10, scale = 0)
	public Long getRole() {
		return this.role;
	}

	public void setRole(Long role) {
		this.role = role;
	}

	@Column(name = "SECURITYQUESTION", length = 1020)
	public String getSecurityquestion() {
		return this.securityquestion;
	}

	public void setSecurityquestion(String securityquestion) {
		this.securityquestion = securityquestion;
	}

	@Column(name = "SECURITYANSWER", length = 1020)
	public String getSecurityanswer() {
		return this.securityanswer;
	}

	public void setSecurityanswer(String securityanswer) {
		this.securityanswer = securityanswer;
	}

	@Column(name = "CONFIRMATIONTIME")
	public Timestamp getConfirmationtime() {
		return this.confirmationtime;
	}

	public void setConfirmationtime(Timestamp confirmationtime) {
		this.confirmationtime = confirmationtime;
	}

	@Column(name = "USERACTIVATIONTIME")
	public Timestamp getUseractivationtime() {
		return this.useractivationtime;
	}

	public void setUseractivationtime(Timestamp useractivationtime) {
		this.useractivationtime = useractivationtime;
	}

	@Column(name = "REJECTIONTIME")
	public Timestamp getRejectiontime() {
		return this.rejectiontime;
	}

	public void setRejectiontime(Timestamp rejectiontime) {
		this.rejectiontime = rejectiontime;
	}

	@Column(name = "EXPIRATIONTIME")
	public Timestamp getExpirationtime() {
		return this.expirationtime;
	}

	public void setExpirationtime(Timestamp expirationtime) {
		this.expirationtime = expirationtime;
	}

	@Column(name = "CONFIRMATIONCODE", length = 1020)
	public String getConfirmationcode() {
		return this.confirmationcode;
	}

	public void setConfirmationcode(String confirmationcode) {
		this.confirmationcode = confirmationcode;
	}

	@Column(name = "DATEOFBIRTH")
	public Timestamp getDateofbirth() {
		return this.dateofbirth;
	}

	public void setDateofbirth(Timestamp dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	@Column(name = "FORGOTPASSWORDCODE", length = 1020)
	public String getForgotpasswordcode() {
		return this.forgotpasswordcode;
	}

	public void setForgotpasswordcode(String forgotpasswordcode) {
		this.forgotpasswordcode = forgotpasswordcode;
	}

	@Column(name = "HOMEPHONE", length = 1020)
	public String getHomephone() {
		return this.homephone;
	}

	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	@Column(name = "WORKPHONE", length = 1020)
	public String getWorkphone() {
		return this.workphone;
	}

	public void setWorkphone(String workphone) {
		this.workphone = workphone;
	}

	@Column(name = "OLDHOMEPHONE", length = 1020)
	public String getOldhomephone() {
		return this.oldhomephone;
	}

	public void setOldhomephone(String oldhomephone) {
		this.oldhomephone = oldhomephone;
	}

	@Column(name = "OLDWORKPHONE", length = 1020)
	public String getOldworkphone() {
		return this.oldworkphone;
	}

	public void setOldworkphone(String oldworkphone) {
		this.oldworkphone = oldworkphone;
	}

	@Column(name = "OLDSECURITYQUESTION", length = 1020)
	public String getOldsecurityquestion() {
		return this.oldsecurityquestion;
	}

	public void setOldsecurityquestion(String oldsecurityquestion) {
		this.oldsecurityquestion = oldsecurityquestion;
	}

	@Column(name = "OLDSECURITYANSWER", length = 1020)
	public String getOldsecurityanswer() {
		return this.oldsecurityanswer;
	}

	public void setOldsecurityanswer(String oldsecurityanswer) {
		this.oldsecurityanswer = oldsecurityanswer;
	}

	@Column(name = "OLDFIRSTNAME", length = 1020)
	public String getOldfirstname() {
		return this.oldfirstname;
	}

	public void setOldfirstname(String oldfirstname) {
		this.oldfirstname = oldfirstname;
	}

	@Column(name = "OLDLASTNAME", length = 1020)
	public String getOldlastname() {
		return this.oldlastname;
	}

	public void setOldlastname(String oldlastname) {
		this.oldlastname = oldlastname;
	}

	@Column(name = "LASTPASSWORDCHANGETIME")
	public Timestamp getLastpasswordchangetime() {
		return this.lastpasswordchangetime;
	}

	public void setLastpasswordchangetime(Timestamp lastpasswordchangetime) {
		this.lastpasswordchangetime = lastpasswordchangetime;
	}

	@Column(name = "PASSWORDHISTORY")
	public String getPasswordhistory() {
		return this.passwordhistory;
	}

	public void setPasswordhistory(String passwordhistory) {
		this.passwordhistory = passwordhistory;
	}

	@Column(name = "ISLOGGEDIN", precision = 3, scale = 0)
	public Short getIsloggedin() {
		return this.isloggedin;
	}

	public void setIsloggedin(Short isloggedin) {
		this.isloggedin = isloggedin;
	}

	@Column(name = "BRANCHCODEID", precision = 10, scale = 0)
	public Long getBranchcodeid() {
		return this.branchcodeid;
	}

	public void setBranchcodeid(Long branchcodeid) {
		this.branchcodeid = branchcodeid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mfinoUserBySubscriberuserid")
	public Set<Subscriber> getSubscribersForSubscriberuserid() {
		return this.subscribersForSubscriberuserid;
	}

	public void setSubscribersForSubscriberuserid(
			Set<Subscriber> subscribersForSubscriberuserid) {
		this.subscribersForSubscriberuserid = subscribersForSubscriberuserid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mfinoUser")
	public Set<SMSPartner> getSmsPartners() {
		return this.smsPartners;
	}

	public void setSmsPartners(Set<SMSPartner> smsPartners) {
		this.smsPartners = smsPartners;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mfinoUserByUserid")
	public Set<Subscriber> getSubscribersForUserid() {
		return this.subscribersForUserid;
	}

	public void setSubscribersForUserid(Set<Subscriber> subscribersForUserid) {
		this.subscribersForUserid = subscribersForUserid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mfinoUser")
	public Set<BulkUpload> getBulkUploads() {
		return this.bulkUploads;
	}

	public void setBulkUploads(Set<BulkUpload> bulkUploads) {
		this.bulkUploads = bulkUploads;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mfinoUser")
	public Set<Partner> getPartners() {
		return this.partners;
	}

	public void setPartners(Set<Partner> partners) {
		this.partners = partners;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mfinoUser")
	public Set<BankAdmin> getBankAdmins() {
		return this.bankAdmins;
	}

	public void setBankAdmins(Set<BankAdmin> bankAdmins) {
		this.bankAdmins = bankAdmins;
	}

}
