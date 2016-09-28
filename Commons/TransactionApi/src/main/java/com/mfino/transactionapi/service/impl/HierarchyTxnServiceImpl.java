/**
 * 
 */
package com.mfino.transactionapi.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.DistributionChainTemplateService;
import com.mfino.service.PartnerServicesService;
import com.mfino.service.PocketService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.transactionapi.handlers.wallet.AgentToAgentTransferConfirmHandler;
import com.mfino.transactionapi.handlers.wallet.AgentToAgentTransferInquiryHandler;
import com.mfino.transactionapi.service.HierarchyTxnService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 *  This service provides handles transactions made by Parent to Child entities in the Hierarchy Services
 *  Transactions supported are Transfer to multiple children.
 * 
 * @author Chaitanya
 *
 */
@org.springframework.stereotype.Service("HierarchyTxnServiceImpl")
public class HierarchyTxnServiceImpl implements HierarchyTxnService{
	
	private Logger log = LoggerFactory.getLogger(HierarchyTxnServiceImpl.class);
	
	private long refid = Calendar.getInstance().getTimeInMillis();
	
	@Autowired
	@Qualifier("AgentToAgentTransferInquiryHandlerImpl")
	private AgentToAgentTransferInquiryHandler agentToAgentTransferInquiryHandler;
	
	@Autowired
	@Qualifier("AgentToAgentTransferConfirmHandlerImpl")
	private AgentToAgentTransferConfirmHandler agentToAgentTransferConfirmHandler;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("DistributionChainTemplateServiceImpl")
	private DistributionChainTemplateService distributionChainTemplateService;
	
	@Autowired
	@Qualifier("PartnerServicesServiceImpl")
	private PartnerServicesService partnerServicesService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	/**
	 * Transfers amount to children as defined in the map, as part of this the follow steps are followed:
	 * 
	 * 1. Validate if the total amount can be transferred from Parent's Wallet Service Outgoing pocket
	 * 
	 * 2. Debit Parent's Wallet Service Outgoing pocket and Credit Parent's Emoney Suspense Pocket with the total amount + Service Charge
	 * 
	 * 2.1 On successful transfer for step 2, loops through the childSubscriberVsAmt Map
	 * 2.1.1 Checks if the Parent has permissions to transfer amount to child subscriber, if yes follows step 2.1.2, else provides a failure notification
	 * 2.1.2 Debit Parent's Emoney Suspense Pocket and credit child's Wallet Service incoming pocket with the amount specified as value in the map
	 * 
	 * 2.2 If transfer failed for Step 2, aborts processing further and returns the failure notification against parent's subscriber in the return map
	 * 
	 * 2.3 If transfer is pending state for Step 2, need to handle this as a separate job
	 * 
	 * 2.4 Return the failed amount back to Parent's source pocket
	 * 
	 * @param parent
	 * @param childSubscriberVsAmt
	 * @param sourcePin
	 * 
	 * @return empty map if the parameters are not valid
	 */
	public Map<Subscriber, CMJSError> transferToChildren(Subscriber parent, Map<Subscriber, BigDecimal> childSubscriberVsAmt, String sourcePin, BigDecimal totalAmt, Long dctId)
	{
		
		log.info("BEGIN:: Request to transfer to children from Subscriber: "+parent.getId()+" refId: "+refid);
		Map<Subscriber, CMJSError> resultMap = new HashMap<Subscriber, CMJSError>();
		Partner partner = getPartner(parent);
		BigDecimal failedAmt = BigDecimal.ZERO;
		int errorCode = CmFinoFIX.ErrorCode_NoError;
		String description = "";
		if(partner!=null)
		{
			DistributionChainTemplate dct = distributionChainTemplateService.getDistributionChainTemplateById(dctId);
			
			if(dct==null)
			{
				errorCode = CmFinoFIX.ErrorCode_RequiredParametersMissing;
				description = "Distribution Chain Template not available with id: "+dctId;
				CMJSError error = getError(errorCode, description);
				resultMap.put(parent, error);
				return resultMap;
			}
			Pocket sourcePocket = getPocket(partner, true, dct);
			
			Pocket suspensePocket = pocketService.getSuspencePocket(partner);
			
			if(sourcePocket==null)
			{
				errorCode = CmFinoFIX.ErrorCode_MoneySVAPocketNotFound;
				description = "Source Pocket not found for Partner: "+partner+" DCT: "+dctId;
				CMJSError error = getError(errorCode, description);
				resultMap.put(parent, error);
				return resultMap;
			}
			
			if(suspensePocket==null)
			{				
				log.info("Creating default suspense pocket for partner: "+parent.getId());
				long suspensePocketTemplateId = -1;
                try {
                	suspensePocketTemplateId = systemParametersService.getLong(SystemParameterKeys.SUSPENCE_POCKET_TEMPLATE_ID_KEY);
                	SubscriberMdn subscriberMdn = parent.getSubscriberMdns().iterator().next();
					String suspensePocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getId().toPlainString());
					suspensePocket = pocketService.createPocket(suspensePocketTemplateId, subscriberMdn, CmFinoFIX.PocketStatus_Active, 
							true, suspensePocketCardPan);
				} catch (Exception e1) {
					log.error("Default Suspense Pocket creation failed for partner: "+parent.getId());					
				}
                if(suspensePocket==null){
                	errorCode = CmFinoFIX.ErrorCode_MoneySVAPocketNotFound;
					description = "Suspense Pocket not found for Partner: "+partner;
					CMJSError error = getError(errorCode, description);
					resultMap.put(parent, error);
					return resultMap;
                }
			}
			
			BigDecimal totalAmount = totalAmt;
			ChannelCode cc = channelCodeService.getChannelCodeByChannelCode("2"); //Channel Code for web
			
			if(sourcePocket!=null && suspensePocket!=null)
			{
				log.info("Source to Suspense pocket Transfer requested for partner: "+partner.getId()+" amount: "+totalAmount+" refId: "+refid);
				
				//Step 2.1 Move amount from source to suspense
				XMLResult result = processTransfer(cc, partner, partner, sourcePin, totalAmount, sourcePocket, suspensePocket, dct);
				
				log.info("Source to Suspense pocket Transfer completed for partner: "+partner.getId()+" amount: "+totalAmount+" notification message: "+result.getMessage()+" refId: "+refid);
				if(CmFinoFIX.NotificationCode_AgentToAgentTransferCompletedToSender.toString().equals(result.getCode()))
				{
					//Step 2.1.2
					Set<Entry<Subscriber, BigDecimal>> entrySet = childSubscriberVsAmt.entrySet();
					Iterator<Entry<Subscriber, BigDecimal>> iterator = entrySet.iterator();
					while(iterator.hasNext())
					{
						Entry<Subscriber, BigDecimal> entry = iterator.next();
						Partner childPartner = getPartner(entry.getKey());
						if(childPartner==null)
						{
							errorCode = CmFinoFIX.ErrorCode_DestMDNNotFound;
							description = "Partner not found for child subscriber: "+entry.getKey();
							CMJSError error = getError(errorCode, description);
							resultMap.put(entry.getKey(), error);
							failedAmt = failedAmt.add(entry.getValue());
						}
						else
						{
							Pocket destPocket = getPocket(childPartner, false, dct);
							log.info("Suspense to Child pocket Transfer requested for child partner: "+childPartner.getId()+" amount: "+entry.getValue()+" refId: "+refid);
							
							XMLResult childResult = processTransfer(cc, partner, childPartner, sourcePin, entry.getValue(), suspensePocket, destPocket, dct);
							
							log.info("Suspense to Child pocket Transfer completed for child partner: "+childPartner.getId()+" amount: "+entry.getValue()+" notification message: "+result.getMessage()+" refId: "+refid);
							
							if(CmFinoFIX.NotificationCode_AgentToAgentTransferCompletedToSender.toString().equals(childResult.getCode()))
							{
								//Successfully transferred
								
							}
							else if(!"Your request is queued. Please check after sometime.".equals(childResult.getMessage()))
							{
								errorCode = CmFinoFIX.ErrorCode_TransferAbortedAndUnresovled;
								description = "Message: "+childResult.getMessage()+ "Notification code: "+childResult.getCode()+ " for child subscriber: "+entry.getKey();
								CMJSError error = getError(errorCode, description);
								resultMap.put(entry.getKey(), error);
								failedAmt = failedAmt.add(entry.getValue());
							}
							else
							{
								//Transaction pending
								errorCode = CmFinoFIX.ErrorCode_TransferAbortedAndUnresovled;
								description = "Message: "+childResult.getMessage()+ " for child subscriber: "+entry.getKey();
								CMJSError error = getError(errorCode, description);
								resultMap.put(entry.getKey(), error);
							}
						}
						
					}
					if(failedAmt!=BigDecimal.ZERO)
					{
						log.info("Suspense to Source pocket Transfer requested for partner: "+partner.getId()+" amount: "+failedAmt+" refId: "+refid);
						//2.4 reverse failed amount
						XMLResult reverseResult = processTransfer(cc, partner, partner, sourcePin, failedAmt, suspensePocket, sourcePocket, dct);
						
						log.info("Suspense to Child pocket Transfer completed for child partner: "+partner.getId()+" amount: "+failedAmt+" notification message: "+reverseResult.getMessage()+" refId: "+refid);
					}
				}
				else
				{
					//Add notification
					errorCode = CmFinoFIX.ErrorCode_TransferAbortedAndUnresovled;
					if(result.getCode()!=null)
					{
						description = "Message: "+result.getMessage()+ "Notification code: "+result.getCode()+ " for subscriber: "+parent.getId();
					}
					else
					{
						description = "Message: "+result.getMessage()+ " for subscriber: "+parent.getId();
					}
					CMJSError error = getError(errorCode, description);
					resultMap.put(parent, error);
				}
				
			}
		}
		
		log.info("END:: Request to transfer to children from Subscriber: "+parent.getId());
		
		return resultMap;
	}
	
	
	private XMLResult processTransfer(ChannelCode cc, Partner sourcePartner, Partner destPartner, String sourcePin, BigDecimal amount, 
			Pocket sourcePocket, Pocket destPocket, DistributionChainTemplate dct)
	{
		String sourceMessage = ServiceAndTransactionConstants.MESSAGE_AGENT_AGENT_TRANSFER;
		Set<SubscriberMdn> mdn = sourcePartner.getSubscriber().getSubscriberMdns();

		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(mdn.iterator().next().getMdn());
		transactionDetails.setPartnerCode(destPartner.getPartnercode());
		transactionDetails.setSourcePIN(sourcePin);
		transactionDetails.setAmount(amount);
		transactionDetails.setCc(cc);
		transactionDetails.setChannelCode(cc.getChannelcode());
		transactionDetails.setSourceMessage(sourceMessage);
		transactionDetails.setServiceName(dct.getService().getServicename());
		transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
		transactionDetails.setSrcPocketId(sourcePocket.getId().longValue());
		transactionDetails.setDestinationPocketId(destPocket.getId().longValue());

		XMLResult result = (XMLResult)agentToAgentTransferInquiryHandler.handle(transactionDetails);
		if (result != null) {
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(result.getCode())) {

				transactionDetails.setTransferId(result.getTransferID());
				transactionDetails.setParentTxnId(result.getParentTransactionID());
				transactionDetails.setConfirmString(Boolean.TRUE.toString());
				XMLResult confirmResult = (XMLResult)agentToAgentTransferConfirmHandler.handle(transactionDetails);
				return confirmResult;
			} 
		}
		return result;
	}
	
	private Partner getPartner(Subscriber subscriber)
	{
		Set<Partner> partners = subscriber.getPartners();
		if(!partners.isEmpty())
		{
			return partners.iterator().next();
		}
		return null;
	}
	
	private Pocket getPocket(Partner partner, boolean isOutgoingPocket, DistributionChainTemplate dct)
	{
		Pocket pocket = null;
		
		Service service = dct.getService();
		

		Long serviceProviderId = null;
		try{
			serviceProviderId = transactionChargingService.getServiceProviderId(null);
		}
		catch(Exception e){
			log.error("HierarchyTxnService :: Exception in constructor ", e);
		}
		
		List<PartnerServices> partnerServices = partnerServicesService.getPartnerServicesList(partner.getId().longValue(), serviceProviderId, service.getId().longValue());
		if((null != partnerServices) && (partnerServices.size() > 0)){
			PartnerServices partnerService = partnerServices.iterator().next();
			if(isOutgoingPocket)
			{
				pocket = partnerService.getPocketBySourcepocket();
			}
			else
			{
				pocket = partnerService.getPocketByDestpocketid();
			}
		}
		return pocket;
	}
	
	private CMJSError getError(int errorCode, String description)
	{
		CMJSError error = new CMJSError();
		error.setErrorCode(errorCode);
		error.setErrorDescription(MessageText._(description));
		if(errorCode==CmFinoFIX.ErrorCode_NoError)
		{
			log.info(description+" refId: "+refid);
		}
		else
		{
			log.error("ErrorCode: "+errorCode+" message: "+description+" refId: "+refid);
		}
		return error;
	}
}
