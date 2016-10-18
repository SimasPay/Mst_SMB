package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

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

/**
 * PartnerDefaultServices generated by hbm2java
 */
@Entity
@Table(name = "PARTNER_DEFAULT_SERVICES")
public class PartnerDefaultServices  extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_BusinessPartnerType = "businesspartnertype";
	private ServiceDefualtConfig serviceDefualtConfig;
	private long businesspartnertype;
	private Long id;

	public PartnerDefaultServices() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "partner_default_services_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICEDEFAULTCONFIGURATIONID", nullable = false)
	public ServiceDefualtConfig getServiceDefualtConfig() {
		return this.serviceDefualtConfig;
	}

	public void setServiceDefualtConfig(
			ServiceDefualtConfig serviceDefualtConfig) {
		this.serviceDefualtConfig = serviceDefualtConfig;
	}

	

	@Column(name = "BUSINESSPARTNERTYPE", nullable = false, precision = 10, scale = 0)
	public long getBusinesspartnertype() {
		return this.businesspartnertype;
	}

	public void setBusinesspartnertype(long businesspartnertype) {
		this.businesspartnertype = businesspartnertype;
	}

}
