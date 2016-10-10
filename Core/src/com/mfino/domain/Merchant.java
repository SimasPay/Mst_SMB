package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.mfino.hibernate.Timestamp;
/**
 * Merchant generated by hbm2java
 */
@Entity
@Table(name = "MERCHANT")
public class Merchant extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FieldName_TradeName = "tradename";
	public static final String FieldName_MerchantByParentID = "merchant";
	public static final String FieldName_GroupID = "groupid";
	public static final String FieldName_StatusTime = "statustime";
	private Address addressByMerchantaddressid;
	private Address addressByFranchiseoutletaddressid;
	private Region region;
	private Subscriber subscriber;
	private Merchant merchant;
	private Merchant m_pMerchantByParentID;
	private String groupid;
	private String tradename;
	private String typeoforganization;
	private BigDecimal distributionchaintemplateid;
	private String faxnumber;
	private String website;
	private BigDecimal currentweeklypurchaseamount;
	private Timestamp lastloptime;
	private String authorizedrepresentative;
	private String representativename;
	private String designation;
	private String franchisephonenumber;
	private String classification;
	private Long numberofoutlets;
	private String industryclassification;
	private Long yearestablished;
	private String authorizedfaxnumber;
	private String authorizedemail;
	private String admincomment;
	private long status;
	private Timestamp statustime;
	private Long rangecheck;
	private Set<MdnRange> mdnRanges = new HashSet<MdnRange>(0);
	private Set<LetterOfPurchase> letterOfPurchases = new HashSet<LetterOfPurchase>(
			0);
	private Set<BulkLop> bulkLops = new HashSet<BulkLop>(0);
	private Set<Merchant> merchants = new HashSet<Merchant>(0);

	public Merchant() {
	}

	public Merchant(Subscriber subscriber, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String tradename, long status, Timestamp statustime) {
		this.subscriber = subscriber;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.tradename = tradename;
		this.status = status;
		this.statustime = statustime;
	}

	public Merchant(Address addressByMerchantaddressid,
			Address addressByFranchiseoutletaddressid, Region region,
			Subscriber subscriber, Merchant merchant,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String groupid,
			String tradename, String typeoforganization,
			BigDecimal distributionchaintemplateid, String faxnumber,
			String website, BigDecimal currentweeklypurchaseamount,
			Timestamp lastloptime, String authorizedrepresentative,
			String representativename, String designation,
			String franchisephonenumber, String classification,
			Long numberofoutlets, String industryclassification,
			Long yearestablished, String authorizedfaxnumber,
			String authorizedemail, String admincomment, long status,
			Timestamp statustime, Long rangecheck, Set<MdnRange> mdnRanges,
			Set<LetterOfPurchase> letterOfPurchases, Set<BulkLop> bulkLops,
			Set<Merchant> merchants) {
		this.addressByMerchantaddressid = addressByMerchantaddressid;
		this.addressByFranchiseoutletaddressid = addressByFranchiseoutletaddressid;
		this.region = region;
		this.subscriber = subscriber;
		this.merchant = merchant;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.groupid = groupid;
		this.tradename = tradename;
		this.typeoforganization = typeoforganization;
		this.distributionchaintemplateid = distributionchaintemplateid;
		this.faxnumber = faxnumber;
		this.website = website;
		this.currentweeklypurchaseamount = currentweeklypurchaseamount;
		this.lastloptime = lastloptime;
		this.authorizedrepresentative = authorizedrepresentative;
		this.representativename = representativename;
		this.designation = designation;
		this.franchisephonenumber = franchisephonenumber;
		this.classification = classification;
		this.numberofoutlets = numberofoutlets;
		this.industryclassification = industryclassification;
		this.yearestablished = yearestablished;
		this.authorizedfaxnumber = authorizedfaxnumber;
		this.authorizedemail = authorizedemail;
		this.admincomment = admincomment;
		this.status = status;
		this.statustime = statustime;
		this.rangecheck = rangecheck;
		this.mdnRanges = mdnRanges;
		this.letterOfPurchases = letterOfPurchases;
		this.bulkLops = bulkLops;
		this.merchants = merchants;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "subscriber"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MERCHANTADDRESSID")
	public Address getAddressByMerchantaddressid() {
		return this.addressByMerchantaddressid;
	}

	public void setAddressByMerchantaddressid(Address addressByMerchantaddressid) {
		this.addressByMerchantaddressid = addressByMerchantaddressid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRANCHISEOUTLETADDRESSID")
	public Address getAddressByFranchiseoutletaddressid() {
		return this.addressByFranchiseoutletaddressid;
	}

	public void setAddressByFranchiseoutletaddressid(
			Address addressByFranchiseoutletaddressid) {
		this.addressByFranchiseoutletaddressid = addressByFranchiseoutletaddressid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REGIONID")
	public Region getRegion() {
		return this.region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENTID")
	public Merchant getMerchant() {
		return this.merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}


	@Column(name = "GROUPID", length = 1020)
	public String getGroupid() {
		return this.groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	@Column(name = "TRADENAME", nullable = false, length = 1020)
	public String getTradename() {
		return this.tradename;
	}

	public void setTradename(String tradename) {
		this.tradename = tradename;
	}

	@Column(name = "TYPEOFORGANIZATION", length = 1020)
	public String getTypeoforganization() {
		return this.typeoforganization;
	}

	public void setTypeoforganization(String typeoforganization) {
		this.typeoforganization = typeoforganization;
	}

	@Column(name = "DISTRIBUTIONCHAINTEMPLATEID", scale = 0)
	public BigDecimal getDistributionchaintemplateid() {
		return this.distributionchaintemplateid;
	}

	public void setDistributionchaintemplateid(
			BigDecimal distributionchaintemplateid) {
		this.distributionchaintemplateid = distributionchaintemplateid;
	}

	@Column(name = "FAXNUMBER", length = 1020)
	public String getFaxnumber() {
		return this.faxnumber;
	}

	public void setFaxnumber(String faxnumber) {
		this.faxnumber = faxnumber;
	}

	@Column(name = "WEBSITE", length = 1020)
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Column(name = "CURRENTWEEKLYPURCHASEAMOUNT", precision = 25, scale = 4)
	public BigDecimal getCurrentweeklypurchaseamount() {
		return this.currentweeklypurchaseamount;
	}

	public void setCurrentweeklypurchaseamount(
			BigDecimal currentweeklypurchaseamount) {
		this.currentweeklypurchaseamount = currentweeklypurchaseamount;
	}

	@Column(name = "LASTLOPTIME")
	public Timestamp getLastloptime() {
		return this.lastloptime;
	}

	public void setLastloptime(Timestamp lastloptime) {
		this.lastloptime = lastloptime;
	}

	@Column(name = "AUTHORIZEDREPRESENTATIVE", length = 1020)
	public String getAuthorizedrepresentative() {
		return this.authorizedrepresentative;
	}

	public void setAuthorizedrepresentative(String authorizedrepresentative) {
		this.authorizedrepresentative = authorizedrepresentative;
	}

	@Column(name = "REPRESENTATIVENAME", length = 1020)
	public String getRepresentativename() {
		return this.representativename;
	}

	public void setRepresentativename(String representativename) {
		this.representativename = representativename;
	}

	@Column(name = "DESIGNATION", length = 1020)
	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	@Column(name = "FRANCHISEPHONENUMBER", length = 1020)
	public String getFranchisephonenumber() {
		return this.franchisephonenumber;
	}

	public void setFranchisephonenumber(String franchisephonenumber) {
		this.franchisephonenumber = franchisephonenumber;
	}

	@Column(name = "CLASSIFICATION", length = 1020)
	public String getClassification() {
		return this.classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	@Column(name = "NUMBEROFOUTLETS", precision = 10, scale = 0)
	public Long getNumberofoutlets() {
		return this.numberofoutlets;
	}

	public void setNumberofoutlets(Long numberofoutlets) {
		this.numberofoutlets = numberofoutlets;
	}

	@Column(name = "INDUSTRYCLASSIFICATION", length = 1020)
	public String getIndustryclassification() {
		return this.industryclassification;
	}

	public void setIndustryclassification(String industryclassification) {
		this.industryclassification = industryclassification;
	}

	@Column(name = "YEARESTABLISHED", precision = 10, scale = 0)
	public Long getYearestablished() {
		return this.yearestablished;
	}

	public void setYearestablished(Long yearestablished) {
		this.yearestablished = yearestablished;
	}

	@Column(name = "AUTHORIZEDFAXNUMBER", length = 1020)
	public String getAuthorizedfaxnumber() {
		return this.authorizedfaxnumber;
	}

	public void setAuthorizedfaxnumber(String authorizedfaxnumber) {
		this.authorizedfaxnumber = authorizedfaxnumber;
	}

	@Column(name = "AUTHORIZEDEMAIL", length = 1020)
	public String getAuthorizedemail() {
		return this.authorizedemail;
	}

	public void setAuthorizedemail(String authorizedemail) {
		this.authorizedemail = authorizedemail;
	}

	@Column(name = "ADMINCOMMENT", length = 1020)
	public String getAdmincomment() {
		return this.admincomment;
	}

	public void setAdmincomment(String admincomment) {
		this.admincomment = admincomment;
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

	@Column(name = "RANGECHECK", precision = 10, scale = 0)
	public Long getRangecheck() {
		return this.rangecheck;
	}

	public void setRangecheck(Long rangecheck) {
		this.rangecheck = rangecheck;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "merchant")
	public Set<MdnRange> getMdnRanges() {
		return this.mdnRanges;
	}

	public void setMdnRanges(Set<MdnRange> mdnRanges) {
		this.mdnRanges = mdnRanges;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "merchant")
	public Set<LetterOfPurchase> getLetterOfPurchases() {
		return this.letterOfPurchases;
	}

	public void setLetterOfPurchases(Set<LetterOfPurchase> letterOfPurchases) {
		this.letterOfPurchases = letterOfPurchases;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "merchant")
	public Set<BulkLop> getBulkLops() {
		return this.bulkLops;
	}

	public void setBulkLops(Set<BulkLop> bulkLops) {
		this.bulkLops = bulkLops;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "merchant")
	public Set<Merchant> getMerchants() {
		return this.merchants;
	}

	public void setMerchants(Set<Merchant> merchants) {
		this.merchants = merchants;
	}
	
	public Merchant getMerchantByParentID(){
		return m_pMerchantByParentID;
		}
	
}
