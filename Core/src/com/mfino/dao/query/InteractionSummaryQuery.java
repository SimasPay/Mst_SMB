package com.mfino.dao.query;

public class InteractionSummaryQuery extends BaseQuery {

	private Long	flowStepID;
	private Long	requestTypeID;
	private Long	reconID;
	private Long	sctlID;
	private Long	integrationID;

	public Long getFlowStepID() {
		return flowStepID;
	}

	public void setFlowStepID(Long flowStepID) {
		this.flowStepID = flowStepID;
	}

	public Long getRequestTypeID() {
		return requestTypeID;
	}

	public void setRequestTypeID(Long requestTypeID) {
		this.requestTypeID = requestTypeID;
	}

	public Long getReconID() {
		return reconID;
	}

	public void setReconID(Long reconID) {
		this.reconID = reconID;
	}

	public Long getSctlID() {
		return sctlID;
	}

	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}

	public Long getIntegrationID() {
		return integrationID;
	}

	public void setIntegrationID(Long integrationID) {
		this.integrationID = integrationID;
	}

}
