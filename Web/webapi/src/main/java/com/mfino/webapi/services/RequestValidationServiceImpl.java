package com.mfino.webapi.services;

import static com.mfino.fix.CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing;

import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ActorChannelMappingDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.ActorChannelMappingQuery;
import com.mfino.domain.ActorChannelMapping;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.integrations.service.IntegrationService;
import com.mfino.integrations.vo.IntegrationDetails;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.validators.ActorChannelMappingUtil;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.webapi.utilities.IntegrationDetailsExtractor;

@Service("RequestValidationServiceImpl")
public class RequestValidationServiceImpl implements RequestValidationService{

	@Autowired
	@Qualifier("WebAPIUtilsServiceImpl")
	private WebAPIUtilsService webAPIUtilsService;

	public boolean validateRequest(HttpServletRequest request, ServletOutputStream writer){

		String sourceMDN = request.getParameter(ApiConstants.PARAMETER_SOURCE_MDN);
		String serviceName = request.getParameter(ApiConstants.PARAMETER_SERVICE_NAME);
		String transactionName = request.getParameter(ApiConstants.PARAMETER_TRANSACTIONNAME);
 		String institutionID =  request.getParameter(ApiConstants.PARAMETER_INSTITUTION_ID);
 		
		if(StringUtils.isNotBlank(institutionID)){
			IntegrationDetails integrationDetails = IntegrationDetailsExtractor.getIntegrationDetails(request);
			if(!validateIntegration(integrationDetails, writer)){
				return false;
			}
		}
		if (StringUtils.isBlank(serviceName)) {
			webAPIUtilsService.sendError(NotificationCode_InvalidWebAPIRequest_ParameterMissing, writer, sourceMDN, ApiConstants.PARAMETER_SERVICE_NAME);
			return false;
		}
		if (StringUtils.isBlank(transactionName)) {
			webAPIUtilsService.sendError(NotificationCode_InvalidWebAPIRequest_ParameterMissing, writer, sourceMDN, ApiConstants.PARAMETER_TRANSACTIONNAME);
			return false;
		}
		
		if (StringUtils.isBlank(sourceMDN) && 
				!((serviceName.equals(ServiceAndTransactionConstants.SERVICE_ACCOUNT) && (transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBER_REGISTRATION_THROUGH_WEB)||
						transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_PARTNER_REGISTRATION_THROUGH_API) || transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_GET_PUBLIC_KEY)
						|| transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_GET_PROMO_IMAGE) ))||
						(serviceName.equals(ServiceAndTransactionConstants.SERVICE_PAYMENT) && transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_GET_THIRD_PARTY_DATA)) ||
						(serviceName.equals(ServiceAndTransactionConstants.SERVICE_PAYMENT) && transactionName.equals(ServiceAndTransactionConstants.TRANSACTION_GET_THIRD_PARTY_LOCATION))	)) {
			webAPIUtilsService.sendError(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, writer, sourceMDN, ApiConstants.PARAMETER_SOURCE_MDN);
			return false;
		}
		return true;
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
 	public boolean validateIntegration(IntegrationDetails integrationDetails, ServletOutputStream writer){
		
 		XMLResult xmlResult = IntegrationService.validateIntegration(integrationDetails);
 		if(!CmFinoFIX.NotificationCode_Integration_Validation_Successful.equals(xmlResult.getNotificationCode()))
		{
			xmlResult.setInstitutionID(integrationDetails.getInstitutionID());
			xmlResult.setIntegrationName(integrationDetails.getIntegrationName());
			xmlResult.setIPAddress(integrationDetails.getIPAddress());
			webAPIUtilsService.sendIntegrationValidationError(xmlResult, writer);
			return false;
		}
	
		return true;
 	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean isLoginEnabledForIntegration(IntegrationDetails integrationDetails){
		
		return IntegrationService.isLoginEnabledForIntegration(integrationDetails);
		
	}
	
	/**
	 * Validates channel and actor(subscriber, agent, partner) details and allows transaction if and only if, for a given channel that specific actor has permissions to do so.
	 * @param transactionDetails
	 * @return
	 */	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean validateTransaction(TransactionDetails transactionDetails){
		boolean allow=true;
		//setting default as true.
		// Setting allow as true for login transactions
		if(ApiConstants.TRANSACTION_LOGIN.equalsIgnoreCase(transactionDetails.getTransactionName())){
			
		}
		else{

			PartnerDAO partnerDAO 		= 	DAOFactory.getInstance().getPartnerDAO();
			ServiceDAO serviceDAO		=	DAOFactory.getInstance().getServiceDAO();
			SubscriberMDNDAO subMDNDAO	=	DAOFactory.getInstance().getSubscriberMdnDAO();
			ActorChannelMappingDAO acmDAO=  DAOFactory.getInstance().getActorChannelMappingDao();
			SubscriberGroupDao subGroupDAO= DAOFactory.getInstance().getSubscriberGroupDao();
			
			ActorChannelMappingQuery acmQuery= new ActorChannelMappingQuery();
			ActorChannelMappingUtil acmUtil=new ActorChannelMappingUtil();
			Subscriber sub = null;
			SubscriberMdn subMDN=null;
			
			if(null!=transactionDetails.getChannelCode()){
				acmQuery.setChannelCodeID(Long.parseLong(transactionDetails.getChannelCode()));
			}

			if(null!=serviceDAO.getServiceByName(transactionDetails.getServiceName())){
				acmQuery.setServiceID(serviceDAO.getServiceByName(transactionDetails.getServiceName()).getId().longValue());
			}
			
			// handling getting transactionTypeID from transactionDetails separately

			if(null!=acmUtil.getTransactionID(transactionDetails)){
				acmQuery.setTransactionTypeID(acmUtil.getTransactionID(transactionDetails));
			}
			if(null!= subMDNDAO.getByMDN(transactionDetails.getSourceMDN())){	
				subMDN= subMDNDAO.getByMDN(transactionDetails.getSourceMDN());
			}
			if(null!=subMDN){
				sub= subMDN.getSubscriber();
			}
			if(null!=sub){
				acmQuery.setSubscriberType((int)sub.getType());
			
				if(CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
					acmQuery.setKycLevel(sub.getKycLevel().getKyclevel().longValue());		//kyc level is set in query only when the subscriber is of type subscriber
					//When the subscriber is of partner or agent type the kyclevel is null in actor mapping table
				}
				if(null!=partnerDAO.getPartnerBySubscriber(sub)){
					acmQuery.setPartnerType(partnerDAO.getPartnerBySubscriber(sub).getBusinesspartnertype().intValue());
				}
				long subID=sub.getId().longValue();
				if(null!=subGroupDAO.getBySubscriberID(subID)){
					acmQuery.setGroup(subGroupDAO.getBySubscriberID(subID).getGroupid());
				}else{
					acmQuery.setGroup(1L); //default group is ANY with group ID=1
				}
				List<ActorChannelMapping> list=acmDAO.get(acmQuery);
			
					//Setting false only when the isAllowed is explicitly set as false in the ActorChannelMapping table so that the existing transactions are not affected
				if(CollectionUtils.isNotEmpty(list)){
					allow=list.get(0).getIsallowed()==0?false:true;
				}else{	
					//do nothing
				}
			}
		}
		return allow;
	}
}
