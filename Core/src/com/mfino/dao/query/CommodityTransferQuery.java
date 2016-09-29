/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import java.util.Date;
import java.util.List;

import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;

/**
 *
 * @author xchen
 */
public class CommodityTransferQuery extends BaseQuery {

    private Date _startDate;
    private Date _endDate;
    private String _bankName;
    private Integer _merchantCode;
    private Long _transactionID;
    private Integer _transferStatus;
    private String _sourceMDN;
    private String _destinationMDN;
    private Long _transactionAmount;
    private Long _referenceID;
    private Integer _subTotalBy;
    private boolean _destMDN;
    private String _sourceDestnMDN;
    private Object[] _sourceDestMDNAndID;
    private Integer _sourceApplicationSearch;
    private boolean _LocalRevertRequired;
    private boolean _BankReversalRequired;
    private boolean _OperatorActionRequired;
//    private Long _sourcePocketID;
    private Long _destinationPocketID;
//    private Long _sourceDestnPocketID;
    private String _SourceReferenceID;
    private String _DestinationRefID;
    private Long _bulkuploadID;
    private Integer _msgType;
//    private Long _subscriberMDNID;
    private String _MDN;
    private Boolean isBankChannel;
    private String _BuySell;
    private boolean isDompetTxn;
    private boolean hasExternalCall;
    private Boolean hasCSRAction = null;
    private Integer _transferFailureReason;
    private boolean isBankChannelUnique;
    private Integer bankCode;
    private Integer _bankRoutingCode;
    private Integer bulkUploadLineNumber;
    private Date endTimeLT;
    private Integer commodity;
    private boolean emoneyTxnsOnly;
    private boolean bankTxnsOnly;
    private String bankRRN;
    

    
    private Date _CreateTimeSearchGT;
//    private Long sourceMDNID;
    private List<Integer> messageTypes;
    
    private Pocket sourcePocket;
    private Pocket sourceDestnPocket;
    private SubscriberMdn subscriberMdn;
    private SubscriberMdn sourceSubscMDN;
    private String sourceMessage;
    

    public Date getCreateTimeSearchGT() {
        return _CreateTimeSearchGT;
    }

    public void setCreateTimeSearchGT(Date _CreateTimeSearchGT) {
        this._CreateTimeSearchGT = _CreateTimeSearchGT;
    }

    public Date getCreateTimeSearchLE() {
        return _CreateTimeSearchLE;
    }

    public void setCreateTimeSearchLE(Date _CreateTimeSearchLE) {
        this._CreateTimeSearchLE = _CreateTimeSearchLE;
    }
    private Date _CreateTimeSearchLE;


    public Integer getCommodity() {
        return commodity;
    }

    public void setCommodity(Integer commodity) {
        this.commodity = commodity;
    }

    public Date getEndTimeLT() {
        return endTimeLT;
    }

    public void setEndTimeLT(Date endTimeLE) {
        this.endTimeLT = endTimeLE;
    }

    public Integer getBankCode() {
        return bankCode;
    }

    public void setBankCode(Integer bankCode) {
        this.bankCode = bankCode;
    }

    public Integer getBankRoutingCode() {
		return _bankRoutingCode;
	}

	public void setBankRoutingCode(Integer _bankRoutingCode) {
		this._bankRoutingCode = _bankRoutingCode;
	}

	public Integer getBulkUploadLineNumber() {
        return bulkUploadLineNumber;
    }

    public void setBulkUploadLineNumber(Integer bulkUploadLineNumber) {
        this.bulkUploadLineNumber = bulkUploadLineNumber;
    }

    public boolean isIsBankChannelUnique() {
        return isBankChannelUnique;
    }

    public void setIsBankChannelUnique(boolean isBankChannelUnique) {
        this.isBankChannelUnique = isBankChannelUnique;
    }
    private Date _startTimeGE;
    private Date _startTimeLT;

    public Date getStartTimeGE() {
        return _startTimeGE;
    }

    public void setStartTimeGE(Date _startTimeGE) {
        this._startTimeGE = _startTimeGE;
    }

    public Date getStartTimeLT() {
        return _startTimeLT;
    }

    public void setStartTimeLT(Date _startTimeLT) {
        this._startTimeLT = _startTimeLT;
    }

    public Boolean hasCSRAction() {
        return hasCSRAction;
    }

    public void setHasCSRAction(boolean hasCSRAction) {
        this.hasCSRAction = hasCSRAction;
    }
    private Integer uiCategory;
    private String exactSourceMDN;

    public boolean hasExternalCall() {
        return hasExternalCall;
    }

    public void setHasExternalCall(boolean hasExternalCall) {
        this.hasExternalCall = hasExternalCall;
    }

    public String getBuySell() {
        return _BuySell;
    }

    public void setBuySell(String _BuySell) {
        this._BuySell = _BuySell;
    }

    public String getMDN() {
        return _MDN;
    }

    public void setMDN(String _MDN) {
        this._MDN = _MDN;
    }

    public Integer getMsgType() {
        return _msgType;
    }

    public void setMsgType(Integer _msgType) {
        this._msgType = _msgType;
    }

    public Long getBulkuploadID() {
        return _bulkuploadID;
    }

    public void setBulkuploadID(Long _bulkuploadID) {
        this._bulkuploadID = _bulkuploadID;
    }

    public String getDestinationRefID() {
        return _DestinationRefID;
    }

    public void setDestinationRefID(String _DestinationRefID) {
        this._DestinationRefID = _DestinationRefID;
    }

   /* public Long getSourcePocketID() {
        return _sourcePocketID;
    }

    public void setSourcePocketID(Long _sourcePocketID) {
        this._sourcePocketID = _sourcePocketID;
    }*/

    public String getSourceReferenceID() {
        return _SourceReferenceID;
    }

    public void setSourceReferenceID(String _SourceReferenceID) {
        this._SourceReferenceID = _SourceReferenceID;
    }

    /*public Long getSourceDestnPocketID() {
        return _sourceDestnPocketID;
    }

    public void setSourceDestnPocketID(Long _sourceDestnPocketID) {
        this._sourceDestnPocketID = _sourceDestnPocketID;
    }*/

    public Long getDestinationPocketID() {
        return _destinationPocketID;
    }

    public void setDestinationPocketID(Long _destinationPocketID) {
        this._destinationPocketID = _destinationPocketID;
    }

    public boolean isBankReversalRequired() {
        return _BankReversalRequired;
    }

    public void setBankReversalRequired(boolean _BankReversalRequired) {
        this._BankReversalRequired = _BankReversalRequired;
    }

    public boolean isLocalRevertRequired() {
        return _LocalRevertRequired;
    }

    public void setLocalRevertRequired(boolean _LocalRevertRequired) {
        this._LocalRevertRequired = _LocalRevertRequired;
    }

    public boolean isOperatorActionRequired() {
        return _OperatorActionRequired;
    }

    public void setOperatorActionRequired(boolean _OperatorActionRequired) {
        this._OperatorActionRequired = _OperatorActionRequired;
    }

    public Integer getSourceApplicationSearch() {
        return _sourceApplicationSearch;
    }

    public void setSourceApplicationSearch(Integer _SourceApplicationSearch) {
        this._sourceApplicationSearch = _SourceApplicationSearch;
    }

    public String getSourceDestnMDN() {
        return _sourceDestnMDN;
    }

    public void setSourceDestnMDN(String _SourceDestnMDN) {
        this._sourceDestnMDN = _SourceDestnMDN;
    }

    public boolean isDestMDN() {
        return _destMDN;
    }

    public void setIsDestMDN(boolean _isDestMDN) {
        this._destMDN = _isDestMDN;
    }

    public Integer getSubTotalBy() {
        return _subTotalBy;
    }

    public void setSubTotalBy(Integer _subTotalBy) {
        this._subTotalBy = _subTotalBy;
    }

    public String getBankName() {
        return _bankName;
    }

    public void setBankName(String _bankName) {
        this._bankName = _bankName;
    }

    public String getDestinationMDN() {
        return _destinationMDN;
    }

    public void setDestinationMDN(String _destinationMDN) {
        this._destinationMDN = _destinationMDN;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public void setEndDate(Date _endDate) {
        this._endDate = _endDate;
    }

    public Integer getMerchantCode() {
        return _merchantCode;
    }

    public void setMerchantCode(Integer _merchantCode) {
        this._merchantCode = _merchantCode;
    }

    public Long getReferenceID() {
        return _referenceID;
    }

    public void setReferenceID(Long _referenceID) {
        this._referenceID = _referenceID;
    }

    public String getSourceMDN() {
        return _sourceMDN;
    }

    public void setSourceMDN(String _sourceMDN) {
        this._sourceMDN = _sourceMDN;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setStartDate(Date _startDate) {
        this._startDate = _startDate;
    }

    public Long getTransactionAmount() {
        return _transactionAmount;
    }

    public void setTransactionAmount(Long _transactionAmount) {
        this._transactionAmount = _transactionAmount;
    }

    public Long getTransactionID() {
        return _transactionID;
    }

    public void setTransactionID(Long _transactionID) {
        this._transactionID = _transactionID;
    }

    public Integer getTransferStatus() {
        return _transferStatus;
    }

    public void setTransferStatus(Integer _transactionStatus) {
        this._transferStatus = _transactionStatus;
    }

   /* public Long getSubscriberMDNID() {
        return _subscriberMDNID;
    }

    public void setSubscriberMDNID(Long _subscriberMDNID) {
        this._subscriberMDNID = _subscriberMDNID;
    }*/

    /**
     * @return the _sourceDestMDNAndID
     */
    public Object[] getSourceDestMDNAndID() {
        return _sourceDestMDNAndID;
    }

    /**
     * @param sourceDestMDNAndID the _sourceDestMDNAndID to set
     */
    public void setSourceDestMDNAndID(Object[] sourceDestMDNAndID) {
        this._sourceDestMDNAndID = sourceDestMDNAndID;
    }

    public Boolean isIsBankChannel() {
        return isBankChannel;
    }

    public void setIsBankChannel(Boolean isBankChannel) {
        this.isBankChannel = isBankChannel;
    }

    //FIXME: Rename
    public boolean isDompetTxn() {
        return isDompetTxn;
    }

    public void setIsDompetTxn(boolean isDompetTxn) {
        this.isDompetTxn = isDompetTxn;
    }

    public Integer getUiCategory() {
        return uiCategory;
    }

    public void setUiCategory(Integer uiCategory) {
        this.uiCategory = uiCategory;
    }

    public String getExactSourceMDN() {
        return exactSourceMDN;
    }

    public void setExactSourceMDN(String exactSourceMDN) {
        this.exactSourceMDN = exactSourceMDN;
    }

    public Integer getTransferFailureReason() {
        return this._transferFailureReason;
    }

    public void setTransferFailureReason(Integer transferFailureReason) {
        this._transferFailureReason = transferFailureReason;
    }

    public void setIsBankChannelUnique(Boolean isBankChannelUnique) {
        this.isBankChannelUnique = isBankChannelUnique;
    }

    public boolean isBankChannelUnique() {
        return isBankChannelUnique;
    }

    public void setExactBankCode(Integer bankCode) {
        this.bankCode = bankCode;
    }

    public Integer getExactBankCode() {
        return bankCode;
    }

    public void setOnlyEmoneyTxns(boolean emoneyTxnsOnly) {
        this.emoneyTxnsOnly = emoneyTxnsOnly;
    }

    public Boolean isOnlyEmoneyTxns() {
        return emoneyTxnsOnly;
    }

    public void setOnlyBankTxns(boolean bankTxnsOnly) {
        this.bankTxnsOnly = bankTxnsOnly;
    }

    public Boolean isOnlyBankTxns() {
        return bankTxnsOnly;
    }

	/**
	 * @return the messageTypes
	 */
	public List<Integer> getMessageTypes() {
		return messageTypes;
	}

	/**
	 * @param messageTypes the messageTypes to set
	 */
	public void setMessageTypes(List<Integer> messageTypes) {
		this.messageTypes = messageTypes;
	}

	/**
	 * @return the sourceMDNID
	 */
	/*public Long getSourceMDNID() {
		return sourceMDNID;
	}*/

	/**
	 * @param sourceMDNID the sourceMDNID to set
	 */
	/*public void setSourceMDNID(Long sourceMDNID) {
		this.sourceMDNID = sourceMDNID;
	}*/

	/**
	 * @return the sourcePocket
	 */
	public Pocket getSourcePocket() {
		return sourcePocket;
	}

	/**
	 * @param sourcePocket the sourcePocket to set
	 */
	public void setSourcePocket(Pocket sourcePocket) {
		this.sourcePocket = sourcePocket;
	}

	/**
	 * @return the sourceDestnPocket
	 */
	public Pocket getSourceDestnPocket() {
		return sourceDestnPocket;
	}

	/**
	 * @param sourceDestnPocket the sourceDestnPocket to set
	 */
	public void setSourceDestnPocket(Pocket sourceDestnPocket) {
		this.sourceDestnPocket = sourceDestnPocket;
	}

	/**
	 * @return the SubscriberMdn
	 */
	public SubscriberMdn getSubscriberMDN() {
		return subscriberMdn;
	}

	/**
	 * @param SubscriberMdn the SubscriberMdn to set
	 */
	public void setSubscriberMDN(SubscriberMdn SubscriberMdn) {
		this.subscriberMdn = SubscriberMdn;
	}

	/**
	 * @return the sourceSubscMDN
	 */
	public SubscriberMdn getSourceSubscMDN() {
		return sourceSubscMDN;
	}

	/**
	 * @param sourceSubscMDN the sourceSubscMDN to set
	 */
	public void setSourceSubscMDN(SubscriberMdn sourceSubscMDN) {
		this.sourceSubscMDN = sourceSubscMDN;
	}

	public String getBankRRN() {
		return bankRRN;
	}

	public void setBankRRN(String bankRRN) {
		this.bankRRN = bankRRN;
	}

	public String getSourceMessage() {
		return sourceMessage;
	}

	public void setSourceMessage(String sourceMessage) {
		this.sourceMessage = sourceMessage;
	}

}
