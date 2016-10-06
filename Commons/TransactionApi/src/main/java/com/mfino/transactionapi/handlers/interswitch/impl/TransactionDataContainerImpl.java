package com.mfino.transactionapi.handlers.interswitch.impl;

import org.springframework.stereotype.Service;

import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.fix.CFIXMsg;
import com.mfino.transactionapi.handlers.interswitch.TransactionDataContainer;

/**
 * This class is used to store the data related to inquiry and confirmation requests
 * @author Sreenath
 *
 */
@Service("TransactionDataContainerImpl")
public class TransactionDataContainerImpl implements TransactionDataContainer{

	private SubscriberMdn partnerMDN;
	private SubscriberMdn destinationMDN;
	private Long	      parentTxnID;
	private boolean	      confirmed;
	private Long	      transferID;
	private Long          sourcePocketID;
	private Long          destPocketID;
	private Transaction transDetails;
	private ServiceChargeTxnLog sctl;
	//set the fix message that is needed for the handlers here
	private CFIXMsg msg;
	
	

	public Long getTransferID() {
		return transferID;
	}

	public void setTransferID(Long transferID) {
		this.transferID = transferID;
	}

	public Long getParentTxnID() {
		return parentTxnID;
	}

	public void setParentTxnID(Long parentTxnID) {
		this.parentTxnID = parentTxnID;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	public SubscriberMdn getPartnerMDN() {
		return partnerMDN;
	}
	public void setPartnerMDN(SubscriberMdn partnerMDN) {
		this.partnerMDN = partnerMDN;
	}
	public SubscriberMdn getDestinationMDN() {
		return destinationMDN;
	}
	public void setDestinationMDN(SubscriberMdn destinationMDN) {
		this.destinationMDN = destinationMDN;
	}

	public Long getSourcePocketID() {
		return sourcePocketID;
	}

	public void setSourcePocketID(Long sourcePocketID) {
		this.sourcePocketID = sourcePocketID;
	}

	public Long getDestPocketID() {
		return destPocketID;
	}

	public void setDestPocketID(Long destPocketID) {
		this.destPocketID = destPocketID;
	}

	public CFIXMsg getMsg() {
		return msg;
	}

	public void setMsg(CFIXMsg msg) {
		this.msg = msg;
	}

	public Transaction getTransDetails() {
		return transDetails;
	}

	public void setTransDetails(Transaction transDetails) {
		this.transDetails = transDetails;
	}

	public ServiceChargeTxnLog getSctl() {
		return sctl;
	}

	public void setSctl(ServiceChargeTxnLog sctl) {
		this.sctl = sctl;
	}

}
