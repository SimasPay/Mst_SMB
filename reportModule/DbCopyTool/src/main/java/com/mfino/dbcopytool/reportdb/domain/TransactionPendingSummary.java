package com.mfino.dbcopytool.reportdb.domain;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;

import com.mfino.dbcopytool.persistence.ReportDbHibernateUtil;

public class TransactionPendingSummary extends TableRow implements java.io.Serializable
{

	private String id;		
	private String sctlId;
	private String csruserId;
	private String csruserName;
	private String csraction;
	private String csrcomment;
	private Date csractionTime;



	// Constructors

	/** default constructor */
	public TransactionPendingSummary() {
	}

	/** minimal constructor */
	public TransactionPendingSummary(String id) {
		this.id = id;
	}


	@Override
	public void initialiseRow(Object[] m) {
		int i = -1;
		this.id = m[++i].toString();
		this.sctlId = m[++i] != null ? m[i].toString() : "";
		this.csruserId = m[++i] != null ? m[i].toString() : "";
		this.csruserName = m[++i] != null ? m[i].toString() : "";
		this.csraction = m[++i] != null ? m[i].toString() : "";
		this.csrcomment = m[++i] != null ? m[i].toString() : "";
		this.csractionTime = (Date) m[++i];
		transfromRow();
	}


	@Override
	public void transfromRow() {
		
	}

	@Override
	public void insertRow() {
		Session session = ReportDbHibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.saveOrUpdate(this);
		session.getTransaction().commit();
		session.close();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSctlId() {
		return sctlId;
	}

	public void setSctlId(String sctlId) {
		this.sctlId = sctlId;
	}

	public String getCsruserId() {
		return csruserId;
	}

	public void setCsruserId(String csruserId) {
		this.csruserId = csruserId;
	}

	public String getCsruserName() {
		return csruserName;
	}

	public void setCsruserName(String csruserName) {
		this.csruserName = csruserName;
	}

	public String getCsraction() {
		return csraction;
	}

	public void setCsraction(String csraction) {
		this.csraction = csraction;
	}

	public String getCsrcomment() {
		return csrcomment;
	}

	public void setCsrcomment(String csrcomment) {
		this.csrcomment = csrcomment;
	}

	public Date getCsractionTime() {
		return csractionTime;
	}

	public void setCsractionTime(Date csractionTime) {
		this.csractionTime = csractionTime;
	}

}
