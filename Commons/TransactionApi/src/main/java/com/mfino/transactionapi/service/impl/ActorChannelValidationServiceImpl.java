package com.mfino.transactionapi.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ActorChannelMappingDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.ActorChannelMappingQuery;
import com.mfino.domain.ActorChannelMapping;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.service.ActorChannelValidationService;
import com.mfino.transactionapi.validators.ActorChannelMappingUtil;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("ActorChannelValidationServiceImpl")
public class ActorChannelValidationServiceImpl implements ActorChannelValidationService{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
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
			SubscriberMDN subMDN=null;
			
			if(null!=transactionDetails.getChannelCode()){
				acmQuery.setChannelCodeID(Long.parseLong(transactionDetails.getChannelCode()));
			}

			if(null!=serviceDAO.getServiceByName(transactionDetails.getServiceName())){
				acmQuery.setServiceID(serviceDAO.getServiceByName(transactionDetails.getServiceName()).getID());
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
				acmQuery.setSubscriberType(sub.getType());
			
				if(CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
					acmQuery.setKycLevel(sub.getKYCLevelByKYCLevel().getKYCLevel());		//kyc level is set in query only when the subscriber is of type subscriber
					//When the subscriber is of partner or agent type the kyclevel is null in actor mapping table
				}
				if(null!=partnerDAO.getPartnerBySubscriber(sub)){
					acmQuery.setPartnerType(partnerDAO.getPartnerBySubscriber(sub).getBusinessPartnerType());
				}
				long subID=sub.getID();
				if(null!=subGroupDAO.getBySubscriberID(subID)){
					acmQuery.setGroup(subGroupDAO.getBySubscriberID(subID).getGroup().getID());
				}else{
					acmQuery.setGroup(1L); //default group is ANY with group ID=1
				}
				List<ActorChannelMapping> list=acmDAO.get(acmQuery);
			
					//Setting false only when the isAllowed is explicitly set as false in the ActorChannelMapping table so that the existing transactions are not affected
				if(CollectionUtils.isNotEmpty(list)){
					allow=list.get(0).getIsAllowed();
				}else{	
					//do nothing
				}
			}
		}
		return allow;
	}

}
