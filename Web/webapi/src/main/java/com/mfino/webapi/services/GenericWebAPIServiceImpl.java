package com.mfino.webapi.services;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionIdentifierService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
/**
 * 
 * @author Shashank
 *
 */
@Service("GenericWebAPIServiceImpl")
public class GenericWebAPIServiceImpl implements GenericWebAPIService{
	public Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

 	@Autowired
 	@Qualifier("TransactionIdentifierServiceImpl")
 	private TransactionIdentifierService transactionIdentifierService;
 	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
 	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
 	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService ;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public String getDestinationMDNFromAccountNumber(String accountNo){

		PocketQuery query = new PocketQuery();
		query.setCardPan(accountNo);
		List<Pocket> pockets = pocketService.get(query);
		if(CollectionUtils.isNotEmpty(pockets)){
			Pocket pocket = pockets.get(0);
			SubscriberMdn subscriberMDN = pocket.getSubscriberMdn();

 			Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(subscriberMDN);
		    if((CmFinoFIX.ResponseCode_Success.equals(validationResult))&& (CmFinoFIX.PocketStatus_Active.equals(pocket.getStatus()))){
			    return  subscriberMDN.getMdn() ;
				 
			}else{
			    return systemParametersService.getString(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN) ;
			}
		}
		else{
			return systemParametersService.getString(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public XMLResult updateSubscriberDetails(String subscriberMDN, XMLResult xmlResult){
		
		SubscriberMdn smdn = subscriberMdnService.getByMDN(subscriberMDN);

		if(smdn != null)
		{
			xmlResult.setLanguage((int)smdn.getSubscriber().getLanguage());
			xmlResult.setFirstName(smdn.getSubscriber().getFirstname());
			xmlResult.setLastName(smdn.getSubscriber().getLastname());
		}	
		
		return xmlResult;
		
	}
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public String generateTransactionIdentifier(HttpServletRequest request){
		String uniqueIdMDN = request.getParameter(ApiConstants.PARAMETER_SOURCE_MDN);
//		uniqueIdMDN = subscriberService.normalizeMDN(uniqueIdMDN);
		if(uniqueIdMDN==null || "".equals(uniqueIdMDN)){
			uniqueIdMDN = request.getParameter(ApiConstants.PARAMETER_SUB_MDN);
		}
		String trxnIdentifier = transactionIdentifierService.generateTransactionIdentifier(uniqueIdMDN);
		return trxnIdentifier;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public void activateInactiveSubscriber(TransactionDetails transactionDetails){
		String sourceMDN = transactionDetails.getSourceMDN();
		String destMDN = transactionDetails.getDestMDN();
		if(sourceMDN != null && !sourceMDN.equalsIgnoreCase("")){
			activateInactiveSubscriber(sourceMDN);
		}
		if(destMDN != null && !destMDN.equalsIgnoreCase("")){
			activateInactiveSubscriber(destMDN);
		}
	}
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public void activateInactiveSubscriber(String mdn){

		Timestamp now = new Timestamp();
		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(mdn);
		
		Subscriber subscriber = null;
		if(subscriberMDN != null && subscriberMDN.getStatus() == CmFinoFIX.SubscriberStatus_InActive.intValue()){
			if(subscriberMDN.getRestrictions() == CmFinoFIX.SubscriberRestrictions_None.intValue()){
				subscriber = subscriberMDN.getSubscriber();
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
				subscriberMDN.setStatustime(now);		
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
				subscriber.setStatustime(now);
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
				subscriberMdnService.saveSubscriberMDN(subscriberMDN);
				subscriberService.saveSubscriber(subscriber);
				Partner partner = getPartnerForSubscriber(subscriber);
				if (partner != null) {
					partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Active);
					partnerService.savePartner(partner);
				}
				log.info("Activated the Inactive Subscriber because of Activity and his Id is--> " + subscriber.getId());			
			}
		}
	}
	
	private Partner getPartnerForSubscriber(Subscriber subscriber) {
		Partner partner = null;
		if (subscriber != null) {
			Set<Partner> partners = subscriber.getPartnerFromSubscriberID();
			if ((partners != null) && (partners.size()!=0)) { 
				partner = partners.iterator().next();
			}
		}
		return partner;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public ChannelCode getChannelCode(String channelCode) throws InvalidDataException{
		ChannelCode cc = channelCodeService.getChannelCodeByChannelCode(channelCode);
		if(cc==null){
			throw new InvalidDataException("Invalid ChannelID", CmFinoFIX.NotificationCode_InvalidData, 
					ApiConstants.PARAMETER_CHANNEL_ID);
		}
		return cc;
	}


}
