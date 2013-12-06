/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class ActivitiesLogQuery extends BaseQuery {
    private Long transferID;
    private Long parentTransactionId;
    private Integer msgType;
    private Integer commodity;
    private Integer bankRoutingCode;
    private Long sourceSubscriberID;

    public Long getSourceSubscriberID() {
		return sourceSubscriberID;
	}

	public void setSourceSubscriberID(Long sourceSubscriberID) {
		this.sourceSubscriberID = sourceSubscriberID;
	}

    public Integer getCommodity() {
      return commodity;
    }

    public Long getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(Long parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
    
    public Long getTransferID() {
        return transferID;
    }

    public void setTransferID(Long transferID) {
        this.transferID = transferID;
    }

    public void setCommodity(Integer commodity) {
        this.commodity = commodity;      
    }

	public void setBankRoutingCode(Integer bankRoutingCode) {
		this.bankRoutingCode = bankRoutingCode;
	}

	public Integer getBankRoutingCode() {
		return bankRoutingCode;
	}
    
    

}
