package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import com.mfino.hibernate.Timestamp;

/**
 * IntegrationPartnerMap generated by hbm2java
 */
@Entity
@Table(name = "INTEGRATION_PARTNER_MAP")
public class IntegrationPartnerMap extends Base implements java.io.Serializable {

	
	private Partner partner;
	private MfsBiller mfsBiller;
	private String institutionid;
	private String integrationname;
	private String authenticationkey;
	private Short isauthenticationkeyenabled;
	private Short isloginenabled;
	private Short isapptypecheckenabled;
	private Set<IPMapping> ipMappings = new HashSet<IPMapping>(0);

	public IntegrationPartnerMap() {
	}

	public IntegrationPartnerMap(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String institutionid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.institutionid = institutionid;
	}

	public IntegrationPartnerMap(BigDecimal id, Partner partner,
			MfsBiller mfsBiller, Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String institutionid,
			String integrationname, String authenticationkey,
			Short isauthenticationkeyenabled, Short isloginenabled,
			Short isapptypecheckenabled, Set<IPMapping> ipMappings) {
		this.id = id;
		this.partner = partner;
		this.mfsBiller = mfsBiller;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.institutionid = institutionid;
		this.integrationname = integrationname;
		this.authenticationkey = authenticationkey;
		this.isauthenticationkeyenabled = isauthenticationkeyenabled;
		this.isloginenabled = isloginenabled;
		this.isapptypecheckenabled = isapptypecheckenabled;
		this.ipMappings = ipMappings;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARTNERID")
	public Partner getPartner() {
		return this.partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MFSBILLERID")
	public MfsBiller getMfsBiller() {
		return this.mfsBiller;
	}

	public void setMfsBiller(MfsBiller mfsBiller) {
		this.mfsBiller = mfsBiller;
	}

	
	@Column(name = "INSTITUTIONID", nullable = false)
	public String getInstitutionid() {
		return this.institutionid;
	}

	public void setInstitutionid(String institutionid) {
		this.institutionid = institutionid;
	}

	@Column(name = "INTEGRATIONNAME", length = 1020)
	public String getIntegrationname() {
		return this.integrationname;
	}

	public void setIntegrationname(String integrationname) {
		this.integrationname = integrationname;
	}

	@Column(name = "AUTHENTICATIONKEY", length = 1020)
	public String getAuthenticationkey() {
		return this.authenticationkey;
	}

	public void setAuthenticationkey(String authenticationkey) {
		this.authenticationkey = authenticationkey;
	}

	@Column(name = "ISAUTHENTICATIONKEYENABLED", precision = 3, scale = 0)
	public Short getIsauthenticationkeyenabled() {
		return this.isauthenticationkeyenabled;
	}

	public void setIsauthenticationkeyenabled(Short isauthenticationkeyenabled) {
		this.isauthenticationkeyenabled = isauthenticationkeyenabled;
	}

	@Column(name = "ISLOGINENABLED", precision = 3, scale = 0)
	public Short getIsloginenabled() {
		return this.isloginenabled;
	}

	public void setIsloginenabled(Short isloginenabled) {
		this.isloginenabled = isloginenabled;
	}

	@Column(name = "ISAPPTYPECHECKENABLED", precision = 3, scale = 0)
	public Short getIsapptypecheckenabled() {
		return this.isapptypecheckenabled;
	}

	public void setIsapptypecheckenabled(Short isapptypecheckenabled) {
		this.isapptypecheckenabled = isapptypecheckenabled;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "integrationPartnerMap")
	public Set<IPMapping> getIpMappings() {
		return this.ipMappings;
	}

	public void setIpMappings(Set<IPMapping> ipMappings) {
		this.ipMappings = ipMappings;
	}

}
