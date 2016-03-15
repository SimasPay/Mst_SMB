/**
 * 
 */
package com.mfino.result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Biller;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Denomination;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.SCTLSettlementMap;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.ZTEDataPush;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationService;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Deva
 *
 */
public abstract class Result{

	public enum ResultType{
		XML,
		JSON,
		FIX,
		SMS
	}
	
	private List<CommodityTransfer> transactionList = new ArrayList<CommodityTransfer>();
	
	private List<SCTLSettlementMap> pendingSettlements = new ArrayList<SCTLSettlementMap>();
	private ServiceChargeTransactionLog sctlList = null;
	
	private List<Pocket> pocketList = new ArrayList<Pocket>();
	
	private List<CMGetLastTransactionsFromBank.CGEntries> lastBankTrxnList;
	
	private List<CMGetLastTransactionsFromBank.CGEntries> nfcTransactionHistory;
	
	private List<CMBalanceInquiryFromBank.CGEntries> nfcCardBalances;
	
	private List<Biller> billerList;
	
	private List<Denomination> denominations;
	
	private Company company;
	
	private Integer language;
	
	private Integer notificationCode;
	
	private CMBase sourceMessage;
	
	private CFIXMsg multixResponse;

	private CommodityTransfer DetailsOfPresentTransaction;
	
	private Boolean activityStatus;
	
	private String bankResponseCode;
	
	public abstract void render() throws Exception;
	
	private com.mfino.hibernate.Timestamp TransactionTime;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Long transferID;
	
	private String code;
	
	private String message;
	
	private String destinationMDN;
	
	private String firstName;
	
	private String lastName;
	
	private String receiverAccountName;
	
	private ZTEDataPush zteDataPush;
    
	private Boolean resetPinRequested;

	private String status;
	
	private String mfaMode;
	
	private Boolean isAlreadyActivated;
	
	public Boolean getIsAlreadyActivated() {
		return isAlreadyActivated;
	}

	public void setIsAlreadyActivated(Boolean isAlreadyActivated) {
		this.isAlreadyActivated = isAlreadyActivated;
	}
	
	public Boolean isResetPinRequested() {
		return resetPinRequested;
	}

	public void setResetPinRequested(Boolean resetPinRequested) {
		this.resetPinRequested = resetPinRequested;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Long getTransferID() {
		return transferID;
	}

	public void setTransferID(Long transferID) {
		this.transferID = transferID;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTransactionTime(com.mfino.hibernate.Timestamp transactionTime)
        {
	    TransactionTime = transactionTime;
        }

	public com.mfino.hibernate.Timestamp getTransactionTime()
        {
	    return TransactionTime;
        }

	/**
	 * @param notificationmethod 
	 * @return
	 */
	protected NotificationWrapper getNotificationWrapper(NotificationService notificationService,Integer notificationMethod) {
        NotificationQuery query = new NotificationQuery();
        query.setNotificationCode(notificationCode);
        query.setNotificationMethod(notificationMethod);
        query.setLanguage(language);
        query.setCompany(company);
        List<Notification> list = notificationService.getLanguageBasedNotificationsByQuery(query);
        if (list.size() == 0) {
            log.warn("No notification method found so returning null");
            return null;
        }
		return new NotificationWrapper(list.get(0));
	}

	/**
	 * @return the transactionList
	 */
	public List<CommodityTransfer> getTransactionList() {
		return transactionList;
	}
	
	/**
	 * @return the ServiceChargeTransactionLog List
	 */
	public ServiceChargeTransactionLog getSCTLList() {
		return sctlList;
	}

	/**
	 * @param transactionList the transactionList to set
	 */
	public void setTransactionList(List<CommodityTransfer> transactionList) {
		this.transactionList = transactionList;
	}
	
	/**
	 * @param ServiceChargeTransactionLog to set
	 */
	public void setSCTLList(ServiceChargeTransactionLog sctlList) {
		this.sctlList = sctlList;
	}


	/**
	 * @return the company
	 */
	public Company getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * @return the language
	 */
	public Integer getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(Integer language) {
		this.language = language;
	}

	/**
	 * @return the notificationCode
	 */
	public Integer getNotificationCode() {
		return notificationCode;
	}

	/**
	 * @param notificationCode the notificationCode to set
	 */
	public void setNotificationCode(Integer notificationCode) {
		this.notificationCode = notificationCode;
	}

	/**
	 * @return the pocketList
	 */
	public List<Pocket> getPocketList() {
		return pocketList;
	}

	/**
	 * @param pocketList the pocketList to set
	 */
	public void setPocketList(List<Pocket> pocketList) {
		this.pocketList = pocketList;
	}

	/**
	 * @param pocket
	 */
	public void addPocket(Pocket pocket) {
		this.pocketList.add(pocket);
	}

	/**
	 * @return the sourceMessage
	 */
	public CMBase getSourceMessage() {
		return sourceMessage;
	}

	/**
	 * @param sourceMessage the sourceMessage to set
	 */
	public void setSourceMessage(CMBase sourceMessage) {
		this.sourceMessage = sourceMessage;
	}
	
	protected String formatDate(Date date){
    	DateFormat df = new SimpleDateFormat(ConfigurationUtil.getTransactionDateTimeFormat());
    	// Making timezone as a configurable property
        TimeZone zone = ConfigurationUtil.getLocalTimeZone();
        df.setTimeZone(zone);
        if(date==null)
        {
        	date = new Timestamp();
        }
        return df.format(date);
    }
	
	protected String formatDateForTransaction(Date date){
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	// Making timezone as a configurable property
        TimeZone zone = ConfigurationUtil.getLocalTimeZone();
        df.setTimeZone(zone);
        
        if(date==null) {
        	
        	date = new Timestamp();
        }
        
        return df.format(date);
    }

	/**
	 * @return the multixResponse
	 */
	public CFIXMsg getMultixResponse() {
		return multixResponse;
	}

	/**
	 * @param multixResponse the multixResponse to set
	 */
	public void setMultixResponse(CFIXMsg multixResponse) {
		this.multixResponse = multixResponse;
	}

	/**
	 * @return the lastBankTrxnList
	 */
	public List<CMGetLastTransactionsFromBank.CGEntries> getLastBankTrxnList() {
		return lastBankTrxnList;
	}

	/**
	 * @param lastBankTrxnList the lastBankTrxnList to set
	 */
	public void setLastBankTrxnList(
			List<CMGetLastTransactionsFromBank.CGEntries> lastBankTrxnList) {
		this.lastBankTrxnList = lastBankTrxnList;
	}


	public void setDetailsOfPresentTransaction(CommodityTransfer detailsOfPresentTransaction)
        {
	    DetailsOfPresentTransaction = detailsOfPresentTransaction;
        }


	public CommodityTransfer getDetailsOfPresentTransaction()
        {
	    return DetailsOfPresentTransaction;
        }

	public void setBillerList(List<Biller> billers) {
		this.billerList = billers;
	}

	public List<Biller> getBillerList() {
		return billerList;
	}

	public void setDenominations(List<Denomination> denominations) {
		this.denominations = denominations;
	}

	public List<Denomination> getDenominations() {
		return denominations;
	}
	
	public void setActivityStatus(Boolean activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Boolean getActivityStatus() {
		return activityStatus;
	}
	
	public void setBankResponseCode(String bankResponseCode) {
		this.bankResponseCode = bankResponseCode;
	}

	public String getBankResponseCode() {
		return bankResponseCode;
	}
	
	public void setDestinationMDN(String destMDN)
	{
		this.destinationMDN = destMDN;
	}
	
	public String getDestinationMDN()
	{
		return destinationMDN;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getReceiverAccountName() {
		return receiverAccountName;
	}

	public void setReceiverAccountName(String receiverAccountName) {
		this.receiverAccountName = receiverAccountName;
	}

	public List<SCTLSettlementMap> getPendingSettlements() {
		return pendingSettlements;
	}

	public void setPendingSettlements(List<SCTLSettlementMap> pendingSettlements) {
		this.pendingSettlements = pendingSettlements;
	}
	
	public ZTEDataPush getZteDataPush() {
		return zteDataPush;
	}

	public void setZteDataPush(ZTEDataPush zteDataPush) {
		this.zteDataPush = zteDataPush;
	}
	
	public String getMfaMode() {
		return mfaMode;
	}

	public void setMfaMode(String mfaMode) {
		this.mfaMode = mfaMode;
	}

	public List<CMBalanceInquiryFromBank.CGEntries> getNfcCardBalances() {
		return nfcCardBalances;
	}

	public void setNfcCardBalances(List<CMBalanceInquiryFromBank.CGEntries> nfcCardBalances) {
		this.nfcCardBalances = nfcCardBalances;
	}

	public List<CMGetLastTransactionsFromBank.CGEntries> getNfcTransactionHistory() {
		return nfcTransactionHistory;
	}

	public void setNfcTransactionHistory(List<CMGetLastTransactionsFromBank.CGEntries> nfcTransactionHistory) {
		this.nfcTransactionHistory = nfcTransactionHistory;
	}
}
