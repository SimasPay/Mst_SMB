package com.mfino.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.DataException;

import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.DistributionChainLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SharePartner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionAmountDistributionLog;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionRule;
import com.mfino.domain.TransactionType;
import com.mfino.exceptions.DistributionException;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.service.impl.TransactionChargingServiceImpl.TransactionChargeShareHolder;

public interface TransactionChargingService {
	/**
	 * Returns the ServiceProvider by name
	 * @param serviceProviderName
	 * @return
	 */
	public Partner getServiceProvider(String serviceProviderName);
	/**
	 * Return serviceProviderId by getting serviceprovider name and then ID
	 * @param serviceProviderName
	 * @return
	 * @throws InvalidServiceException
	 */
	public Long getServiceProviderId(String serviceProviderName) throws InvalidServiceException;
	
	/**
	 * Returns the Service by name
	 * @param serviceName
	 * @return
	 */
	public Service getService(String serviceName);
	
	public Long getServiceId(String serviceName) throws InvalidServiceException;
	
	/**
	 * Returns the TransactionType by name
	 * @param transactionName
	 * @return
	 */
	public TransactionType getTransactionType(String transactionName);
	
	public Long getTransactionTypeId(String transactionName) throws InvalidServiceException;
	
	/**
	 * Returns the Map object with MDN type, KYC levelId and Partner Id details for the given MDN
	 * @param MDN
	 * @return
	 */
	Map<String, Long> getMDNDetails(String MDN);
	
	/**
	 * Calculates the Total Transaction charge from user, Transaction amount and any other charges (Commission) defined for the given details.
	 * @param sc
	 * @return
	 * @throws InvalidServiceException, DataException
	 * @throws InvalidChargeDefinitionException 
	 */

	public Transaction getCharge(ServiceCharge sc) throws InvalidServiceException, DataException, InvalidChargeDefinitionException;

	/**
	 * Gets the Transaction rule for the given details
	 * @param serviceProviderId
	 * @param serviceId
	 * @param transactionTypeId
	 * @param channelCodeId
	 * @param sourceType
	 * @param sourceKYC
	 * @param destType
	 * @param destKYC
	 * @return
	 */
	public TransactionRule getTransactionRule(Long serviceProviderId, Long serviceId, Long transactionTypeId, Long channelCodeId,
			Integer sourceType, Long sourceKYC, Integer destType, Long destKYC);
	
	public TransactionRule getTransactionRule(Long serviceProviderId, Long serviceId, Long transactionTypeId, Long channelCodeId,
			Long sourceGroup, Long destinationGroup);
	
	/**
	 * Calculate the individual charges for each charge type. 
	 * @param tr
	 * @param amount
	 * @return
	 * @throws InvalidChargeDefinitionException 
	 */
	public HashMap<TransactionCharge, BigDecimal> getTransactionCharges(TransactionRule tr, BigDecimal amount) throws InvalidChargeDefinitionException;
	
	/**
	 * Calculates the total of all charges.
	 * @param chargeMap
	 * @return
	 */
	public BigDecimal calculateTotalCharge(HashMap<TransactionCharge, BigDecimal> chargeMap);
	
	/**
	 * Calculate the Charge for the given charge Definition.
	 * @param cd
	 * @param amount
	 * @return
	 * @throws InvalidChargeDefinitionException 
	 */	
	BigDecimal calculateCharge(ChargeDefinition cd, BigDecimal amount) throws InvalidChargeDefinitionException;
	
	/**
	 * Saves the Service Transaction details to the log table.
	 * @param sctl
	 * @return
	 */
	public Long saveServiceTransactionLog(ServiceChargeTransactionLog sctl);
	/**
	 * Returns the Service charge Transaction Log entry by Transaction Log Id (Parent Transaction ID)
	 * @param transactionLogId
	 * @return
	 */
	
	public ServiceChargeTransactionLog getServiceChargeTransactionLog(long transactionLogId);
	
	/**
	 * Adds the sctlID to the transactionIdentifier table and returns the  
	 * Service charge Transaction Log entry by Transaction Log Id (Parent Transaction ID)
	 * @param transactionLogId
	 * @param trxnIdentifier
	 * @return
	 */

	public ServiceChargeTransactionLog getServiceChargeTransactionLog(long transactionLogId,String trxnIdentifier);
	/**
	 * Updates the SCTL status to Confirmed
	 * @param sctl
	 * @param commodityTransaferId
	 */
	public void confirmTheTransaction(ServiceChargeTransactionLog sctl, long commodityTransaferId);
	
	/**
	 * Updates the SCTL status to Confirmed
	 * @param sctl
	 * @param commodityTransaferId
	 */
	public void confirmTheTransaction(ServiceChargeTransactionLog sctl);
	
	/**
	 * Updates the SCTL status to Pending
	 * @param commodityTransaferId
	 */
	public void setPendingStatus(long commodityTransaferId);
	
	/**
	 * Updates the SCTL CommodityTransferID
	 * @param sctl
	 * @param commodityTransaferId
	 */
	public void addTransferID(ServiceChargeTransactionLog sctl, long commodityTransaferId);
	
	/**
	 * Updates the SCTL status to Distribution completed
	 * @param sctl
	 */
	public void completeTheTransaction(ServiceChargeTransactionLog sctl);
	
	/**
	 * Updates the SCTL status to Failure
	 * @param sctl
	 * @param failureReason
	 */
	public void failTheTransaction(ServiceChargeTransactionLog sctl, String failureReason);
	
	/**
	 * Update the SCTL status to Pending for Agent for Confirmation
	 * @param sctl
	 */
	public void chnageStatusToProcessing(ServiceChargeTransactionLog sctl);
	
	/**
	 * Update the SCTL status to Pending for Agent for Confirmation
	 * @param sctl
	 */
	public void changeStatusToPending(ServiceChargeTransactionLog sctl);
	
	public void changeStatusToPendingResolved(ServiceChargeTransactionLog sctl);
	
	public void changeStatusToPendingResolvedProcessing(ServiceChargeTransactionLog sctl);
	
	/**
	 * Stores the Individual Charge amounts to the log table. 
	 * @param sctlId
	 * @param tc
	 * @param charge
	 */
	public void saveTransactionChargeLog(long sctlId, TransactionCharge tc, BigDecimal charge );
	
	/**
	 * Checks whether the given Agent / Partner has active service or not
	 * @param sc
	 * @return
	 * @throws InvalidServiceException
	 */
	public boolean checksPartnerService(ServiceCharge sc) throws InvalidServiceException;
	
	/**
	 * Returns the Partner Service Object for the given Partner and Service details
	 * @param sc
	 * @return
	 * @throws InvalidServiceException
	 */
	public PartnerServices getPartnerService(ServiceCharge sc) throws InvalidServiceException;
	
	/**
	 * Returns the Partner Service for the given Partner and Service
	 * @param partnerId
	 * @param serviceProviderId
	 * @param serviceId
	 * @return
	 */
	public PartnerServices getPartnerService(long partnerId, long serviceProviderId, long serviceId);
	
	/**
	 * Distribute the Transaction amount among the Partners involved in the Transaction.
	 * @param sctlId
	 */
	public void distributeTransactionAmount(Long sctlId);
	
	/**
	 * Validate if aggregate share calculated from individual shares(in shareMap) exceeded the calculatedCharge
	 * @param shareMap
	 * @param calculatedCharge
	 * @throws DistributionException
	 */
	void validateAggregateShare(HashMap<TransactionChargeShareHolder, BigDecimal> shareMap, BigDecimal calculatedCharge)  throws DistributionException;
	
	/**
	 * Calculate charge share based on minShareCharge and maxShargeCharge values that are obtained from sharePartner object
	 * @param sharePartner
	 * @param calculatedCharge
	 * @return
	 * @throws DistributionException 
	 */
	BigDecimal calculateChargeShare(SharePartner sharePartner, BigDecimal calculatedCharge) throws DistributionException;
	
	/**
	 * Add the given Partner Id with percentage details to the Share Map
	 * @param shareMap
	 * @param partnerId
	 * @param percentage
	 * @return
	 */
	HashMap<TransactionChargeShareHolder, BigDecimal> updateShareMap(HashMap<TransactionChargeShareHolder, BigDecimal> shareMap, TransactionChargeShareHolder tcShareHolder, BigDecimal percentage);
	
	/**
	 * Generates the Transaction amount Distribution log for the given SCTL and Partner
	 * @param sctl
	 * @param tc
	 * @param shareAmount
	 * @param lstTADL
	 * @param partnerId
	 * @return
	 * @throws DistributionException
	 */
	List<TransactionAmountDistributionLog> generateTADL(ServiceChargeTransactionLog sctl, TransactionCharge tc, BigDecimal shareAmount,
			List<TransactionAmountDistributionLog> lstTADL, Long partnerId, boolean isPartner) throws DistributionException;
	
	Long getRegisteringPartner(String mdn);
	
	/**
	 * Logs the Distribution of Transaction amount among the Shared up chain partners.
	 * @param sctlId
	 * @param tcId
	 * @param ps
	 * @param lstParentPS
	 * @param amount
	 * @param mapDCL
	 * @param lstTADL
	 * @return
	 */
	List<TransactionAmountDistributionLog> distributeChargeAmongSharedUpChainPartners(long sctlId, long transactionId, 
			TransactionCharge tc, PartnerServices ps, List<PartnerServices> lstParentPS, BigDecimal amount, Map<Integer, 
			DistributionChainLevel> mapDCL, List<TransactionAmountDistributionLog> lstTADL);
	
	void failTheDistribution(ServiceChargeTransactionLog sctl, String failureReason);
	
	/**
	 * Gets the Share percentages for each level in the Distribution chain for the given PartnerServices. 
	 * @param ps
	 */
	Map<Integer, DistributionChainLevel> getDistributionChainLevelShares(PartnerServices ps);

	/**
	 * Returns the List of Parents for the given Partner Service.
	 * @param ps
	 * @param lstParentPS
	 * @return
	 */
	List<PartnerServices> getParentList(PartnerServices ps, List<PartnerServices> lstParentPS);
	
	TransactionAmountDistributionLog getTADL(Long sctlID, Long transactionId, TransactionCharge tc, Partner partner, Subscriber subscriber,  Pocket pocket, 
			BigDecimal amount, boolean isPartOfCharge, boolean isActualAmt, boolean isPartOfSharedUpChain, boolean isSelf);
	
	/**
	 * Creates the Log for the Transaction amount distribution
	 * @param sctlID
	 * @param tcID
	 * @param partner
	 * @param pocket
	 * @param amount
	 * @param isPartOfCharge
	 * @param isActualAmt
	 * @param isPartOfSharedUpChain
	 * @param settlementType
	 */
	public void saveTADL(Long sctlID, Long transactionId, TransactionCharge tc, Partner partner, Subscriber subscriber, Pocket pocket, BigDecimal amount, boolean isPartOfCharge, boolean isActualAmt, 
			boolean isPartOfSharedUpChain);
	
	/**
	 * Updates the Audit information based on the Service and Source type
	 * @param serviceProviderId
	 * @param serviceId
	 * @param sourceType
	 * @param sourceId
	 * @param KYCLevelId
	 * @param amount
	 */
	public void doServiceAudit(Long serviceProviderId, Long serviceId, Integer sourceType, Long sourceId, Long KYCLevelId, BigDecimal amount);
	
	public void updateTransactionStatus(TransactionResponse tr, ServiceChargeTransactionLog sctl);
	
	public boolean isTransactionSuccessful(TransactionResponse transactionResponse);
	public Transaction getChargeDetails(ServiceCharge sc)  throws DataException, InvalidServiceException, InvalidChargeDefinitionException;

	/**
	 * Updates the Pending SCTL status to Fail
	 * @param commodityTransaferId
	 * @param failureReason
	 */
	public void setAsFailed(long commodityTransaferId, String failureReason);
}
