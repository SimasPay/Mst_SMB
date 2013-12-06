package com.mfino.dbcopytool.reportdb.domain;

import org.hibernate.Session;

import com.mfino.dbcopytool.persistence.ReportDbHibernateUtil;

public class IntegreationSummary extends TableRow implements java.io.Serializable{
	
	private String integreationSummaryId;
	private String sctlId;
	private String integrationType;
	private String reconcilationID1;
	private String reconcilationID2;
	private String reconcilationID3;
	
	public  IntegreationSummary() {
	}

	@Override
	public void initialiseRow(Object[] m) {
		int i = -1;
		this.integreationSummaryId = m[++i] != null ? m[i].toString() : "";
		this.sctlId = m[++i] != null ? m[i].toString() : "";
		this.integrationType = m[++i] != null ? m[i].toString() : "";
		this.reconcilationID1 = m[++i] != null ? m[i].toString() : "";
		this.reconcilationID2 = m[++i] != null ? m[i].toString() : "";
		this.reconcilationID3 = m[++i] != null ? m[i].toString() : "";
		
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

	@Override
	public void printRow() {
		
	}

	
	public String getIntegreationSummaryId() {
		return integreationSummaryId;
	}

	public void setIntegreationSummaryId(String integreationSummaryId) {
		this.integreationSummaryId = integreationSummaryId;
	}

	public String getSctlId() {
		return sctlId;
	}

	public void setSctlId(String sctlId) {
		this.sctlId = sctlId;
	}


	public String getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}

	public String getReconcilationID1() {
		return reconcilationID1;
	}

	public void setReconcilationID1(String reconcilationID1) {
		this.reconcilationID1 = reconcilationID1;
	}

	public String getReconcilationID2() {
		return reconcilationID2;
	}

	public void setReconcilationID2(String reconcilationID2) {
		this.reconcilationID2 = reconcilationID2;
	}

	public String getReconcilationID3() {
		return reconcilationID3;
	}

	public void setReconcilationID3(String reconcilationID3) {
		this.reconcilationID3 = reconcilationID3;
	}

}
