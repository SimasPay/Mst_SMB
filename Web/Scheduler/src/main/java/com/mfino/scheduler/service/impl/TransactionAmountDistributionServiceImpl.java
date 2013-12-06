/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.TransactionAmountDistributionQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionAmountDistributionLog;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;
import com.mfino.hibernate.Timestamp;
import com.mfino.scheduler.service.TransactionAmountDistributionService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionAmountDistributionLogService;
import com.mfino.service.TransactionChargingService;
import com.mfino.transactionapi.handlers.money.ChargeDistributionHandler;

/**
 * @author Bala Sunku
 *
 */
@Service("TransactionAmountDistributionServiceImpl")
public class TransactionAmountDistributionServiceImpl  implements TransactionAmountDistributionService {
	
	private static Logger log = LoggerFactory.getLogger(TransactionAmountDistributionServiceImpl.class);

	@Autowired
	@Qualifier("ChargeDistributionHandlerImpl")
	private ChargeDistributionHandler chargeDistributionHandler;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("TransactionAmountDistributionLogServiceImpl")
	private TransactionAmountDistributionLogService transactionAmountDistributionLogService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	
	public void distributeTransactionAmount() {
		log.info("BEGIN distribute Transaction amount");
			
			Timestamp currentTime = new Timestamp();			
			Integer[] status = new Integer[]{
						CmFinoFIX.SCTLStatus_Confirmed,
						CmFinoFIX.SCTLStatus_Reverse_Requested,
						CmFinoFIX.SCTLStatus_Reverse_Approved,
						CmFinoFIX.SCTLStatus_Reverse_Rejected,
						CmFinoFIX.SCTLStatus_Reversed,
						CmFinoFIX.SCTLStatus_Reverse_Failed,
						CmFinoFIX.SCTLStatus_Reverse_Success
					};
			
			List<ServiceChargeTransactionLog> lst = serviceChargeTransactionLogService.getByStatus(status);
			if (CollectionUtils.isNotEmpty(lst)) {
				for (ServiceChargeTransactionLog sctl : lst) {
					if ((sctl.getIsChargeDistributed()!= null ) && (!sctl.getIsChargeDistributed().booleanValue()) && 
							((currentTime.getTime() - sctl.getLastUpdateTime().getTime()) > 300000) ) {
						log.info("Charge Distribution of SCTL ID --> " + sctl.getID());
						distribute(sctl.getID());
					}
					// Changing the Confirmed SCTL status based on the IsChargeDistributed value to Distribution_Completed.
					else if((sctl.getIsChargeDistributed()!= null ) && (sctl.getIsChargeDistributed().booleanValue()) && 
							CmFinoFIX.SCTLStatus_Confirmed.equals(sctl.getStatus())) {
						sctl.setStatus(CmFinoFIX.SCTLStatus_Distribution_Completed);
						serviceChargeTransactionLogService.save(sctl);
					}
				} 
			}
		
		log.info("END distribute Transaction amount");
	}

	
	private void distribute(Long sctlID){

		transactionChargingService.distributeTransactionAmount(sctlID);
	}

	@Override
	
	public void updateCollectorPockets() {
		log.info("BEGIN update Collector Pockets");

			TransactionAmountDistributionQuery query = new TransactionAmountDistributionQuery();
			query.setStatus(CmFinoFIX.TADLStatus_Initialized); 

			List<TransactionAmountDistributionLog> lstTADL =  transactionAmountDistributionLogService.get(query);
			if (CollectionUtils.isNotEmpty(lstTADL)) {
				for (TransactionAmountDistributionLog tadl: lstTADL) {
					updatePocketForDistribution(tadl);
				}
			}
		log.info("END update collector pockets");
	}
	
	
	private void updatePocketForDistribution(TransactionAmountDistributionLog tadl) {
		if(tadl==null){
			return;
		}
		log.info("Update the pocket for SCTL --> " + tadl.getServiceChargeTransactionLogID() + " , Transaction Id --> " + 
				tadl.getTransactionID() + ", Transaction Charge Id --> " + tadl.getTransactionCharge().getID());
		CMChargeDistribution chargeDistribution;
		tadl.setStatus(CmFinoFIX.TADLStatus_Processing);
		transactionAmountDistributionLogService.save(tadl);

		String sourceMDN = systemParametersService.getString(SystemParameterKeys.PLATFORM_DUMMY_MDN_KEY);
		Long sourcePocketId = systemParametersService.getLong(SystemParameterKeys.CHARGES_POCKET_ID_KEY);
		
		TransactionCharge tc = tadl.getTransactionCharge();
		
		if (tc != null && !(tc.getChargeDefinition().getIsChargeFromCustomer().booleanValue())) {
			sourceMDN = getMDNForPartner(tc.getChargeDefinition().getPartnerByFundingPartnerID());
			sourcePocketId = tc.getChargeDefinition().getPocket().getID();
		}
		
		String destMDN = null;
		if(tadl.getPartner() != null)
		{
			destMDN = getMDNForPartner(tadl.getPartner());
		}
		else if(tadl.getSubscriber() != null)
		{
			destMDN = tadl.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next().getMDN();
		}
		
		
		chargeDistribution = new CMChargeDistribution();
		chargeDistribution.setParentTransactionID(tadl.getTransactionID());
		chargeDistribution.setSourceMDN(sourceMDN);
		chargeDistribution.setSourcePocketID(sourcePocketId);
		chargeDistribution.setDestMDN(destMDN);
		chargeDistribution.setDestPocketID((tadl.getPocket() != null) ? tadl.getPocket().getID() : null);
		chargeDistribution.setAmount(tadl.getShareAmount());
		chargeDistribution.setTaxAmount(tadl.getTaxAmount());
		chargeDistribution.setTransactionChargeID(tadl.getTransactionCharge().getID());
		chargeDistribution.setIsPartOfSharedUpChain(tadl.getIsPartOfSharedUpChain());
		chargeDistribution.setChargeTypeName(tadl.getTransactionCharge().getChargeType().getName());
		chargeDistribution.setServiceChargeTransactionLogID(tadl.getServiceChargeTransactionLogID());
		if(tadl.getPartner() != null)
		{
			chargeDistribution.setPartnerID(tadl.getPartner().getID());
		}
		
		//REFACTOR:: This is a workaround need to set the data based on original Txn information
		chargeDistribution.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		chargeDistribution.setSourceApplication(CmFinoFIX.SourceApplication_WebAPI);
		chargeDistribution.setMessageType(CmFinoFIX.MessageType_ChargeDistribution);
		
		log.info("Sending the request to backend for processing.");

		
		CFIXMsg response = chargeDistributionHandler.process(chargeDistribution);
		TransactionResponse transactionResponse = chargeDistributionHandler.checkBackEndResponse(response);

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			String failureReason = transactionResponse.getMessage();
			if (failureReason.length() > 255) {
				failureReason = failureReason.substring(0, 255);
			}
			if (transactionResponse.isResult()) {
				if (CmFinoFIX.NotificationCode_ChargeDistributionCompleted.equals(Integer.parseInt(transactionResponse.getCode()))) {
					log.info("Pocket Updation completed.");
					tadl.setStatus(CmFinoFIX.TADLStatus_Completed);
					tadl.setFailureReason("");
					transactionAmountDistributionLogService.save(tadl);
				} else {
					log.info("pocket updation is failed and put for Retry");
					tadl.setFailureReason(failureReason);
					tadl.setStatus(CmFinoFIX.TADLStatus_ReTry);
					transactionAmountDistributionLogService.save(tadl);
				}
			} else {
				log.info("Pocket updation failed.");
				tadl.setFailureReason(failureReason);
				tadl.setStatus(CmFinoFIX.TADLStatus_Failed);
				transactionAmountDistributionLogService.save(tadl);
			}			
		}
	}

	@Override
	
	public void retryCollectorPockets() {
		log.info("BEGIN Retry Collector Pockets");

			TransactionAmountDistributionQuery query = new TransactionAmountDistributionQuery();
			query.setStatus(CmFinoFIX.TADLStatus_ReTry);

			List<TransactionAmountDistributionLog> lstTADL =  transactionAmountDistributionLogService.get(query);
			if (CollectionUtils.isNotEmpty(lstTADL)) {
				for (TransactionAmountDistributionLog tadl: lstTADL) {
					updatePocketForDistribution(tadl);
				}
			}
		log.info("END Retry Collector Pockets");
	}
	
	@Override
	
	public void retryFailedCollectorPockets() {
		log.info("BEGIN retry Failed Collector Pockets");

			TransactionAmountDistributionQuery query = new TransactionAmountDistributionQuery();
			query.setStatus(CmFinoFIX.TADLStatus_Failed); 

			List<TransactionAmountDistributionLog> lstTADL =  transactionAmountDistributionLogService.get(query);
			if (CollectionUtils.isNotEmpty(lstTADL)) {
				for (TransactionAmountDistributionLog tadl: lstTADL) {
					if (tadl.getShareAmount().compareTo(BigDecimal.ZERO) > 0) {
						updatePocketForDistribution(tadl);
					}
				}
			}
		
		log.info("END retry Failed collector pockets");
	}	

	/**
	 * Returns the MDN for the given Partner
	 * @param partner
	 * @return
	 */
	private String getMDNForPartner(Partner partner) {
		String MDN = "";
		if (partner != null && partner.getSubscriber() != null) {
			Set<SubscriberMDN> setMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID();
			if (CollectionUtils.isNotEmpty(setMDN)) {
				for (SubscriberMDN smdn: setMDN) {
					MDN = smdn.getMDN(); 
				}
			}
		}
		return MDN;
	}
}
