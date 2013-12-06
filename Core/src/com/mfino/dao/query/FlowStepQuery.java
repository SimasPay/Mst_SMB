package com.mfino.dao.query;

public class FlowStepQuery extends BaseQuery{

	private String	name;
	private Long	transactionFlowID;
	private Integer	   executionOrder;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTransactionFlowID() {
		return transactionFlowID;
	}

	public void setTransactionFlowID(Long transactionFlowID) {
		this.transactionFlowID = transactionFlowID;
	}

	public Integer getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(Integer executionOrder) {
		this.executionOrder = executionOrder;
	}

}