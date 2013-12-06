package com.mfino.dao.query;

/**
 * 
 * @author Srikanth
 */
public class IntegrationSummaryQuery extends BaseQuery {
	private Long sctlID;
	private String integrationType;
	private String reconcilationID1;
	private String reconcilationID2;
	private String reconcilationID3;

	public Long getSctlID() {
		return sctlID;
	}

	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
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
