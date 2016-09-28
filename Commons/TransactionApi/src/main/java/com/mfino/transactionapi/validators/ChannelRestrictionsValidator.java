package com.mfino.transactionapi.validators;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
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
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.transactionapi.validators.ActorChannelMappingUtil;

/**
 * 
 * @author Hemanth
 *
 */
public class ChannelRestrictionsValidator{
	public boolean validate(TransactionDetails transactionDetails){
		boolean allow=true;

		//setting default as true.
		 
		// Setting allow as true for login transactions
		if(ApiConstants.TRANSACTION_LOGIN.equalsIgnoreCase(transactionDetails.getTransactionName()))
		{
			//do nothing
		}
		
		
		else
		{
			ActorChannelMappingQuery acmQuery= new ActorChannelMappingQuery();
			if(null!=transactionDetails.getChannelCode())
			{
			acmQuery.setChannelCodeID(Long.parseLong(transactionDetails.getChannelCode()));
			}
			ServiceDAO serviceDAO=DAOFactory.getInstance().getServiceDAO();
			if(null!=serviceDAO.getServiceByName(transactionDetails.getServiceName()))
			{
				acmQuery.setServiceID(serviceDAO.getServiceByName(transactionDetails.getServiceName()).getId().longValue());
			}
			// handling getting transactionTypeID from transactionDetails separately
			ActorChannelMappingUtil acmUtil=new ActorChannelMappingUtil();
			if(null!=acmUtil.getTransactionID(transactionDetails)){
			acmQuery.setTransactionTypeID(acmUtil.getTransactionID(transactionDetails));
			}
			SubscriberMDNDAO subMDNDAO=DAOFactory.getInstance().getSubscriberMdnDAO();
			Subscriber sub = null;
			SubscriberMdn subMDN=null;
			PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
			if(null!= subMDNDAO.getByMDN(transactionDetails.getSourceMDN());
			{	
				subMDN= subMDNDAO.getByMDN(transactionDetails.getSourceMDN());
			}
			if(null!=subMDN)
			{
			 sub= subMDN.getSubscriber();
			}
			if(null!=sub)
			{
			acmQuery.setSubscriberType((int)sub.getType());
			//kyc level is set in query only when the subscriber is of type subscriber
			//When the subscriber is of partner or agent type the kyclevel is null in actor mapping table
			if(CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())) {
			acmQuery.setKycLevel(sub.getKycLevel().getKyclevel().longValue());
					}
			if(null!=partnerDAO.getPartnerBySubscriber(sub))
			{
				acmQuery.setPartnerType(partnerDAO.getPartnerBySubscriber(sub).getBusinesspartnertype().intValue());
			}
			
			/*
			Set<SubscriberDAO> subscriberGroup=sub.getSubscriberGroupFromSubscriberID();
			Iterator<SubscriberGroup> iterator=subscriberGroup.iterator();
			if(iterator.hasNext())
			{
				SubscriberGroup group=iterator.next();
				acmQuery.setGroup(group.getGroup().getID());
			}
			 */
			//There is no foreign key reference on subscriber_group table hence modifying code
			
			
			SubscriberGroupDao subGroupDAO=DAOFactory.getInstance().getSubscriberGroupDao();
			long subID=sub.getId().longValue();
			if(null!=subGroupDAO.getBySubscriberID(subID)){
			acmQuery.setGroup(subGroupDAO.getBySubscriberID(subID).getGroup().getID());
			}else{
			acmQuery.setGroup(1L); //default group is ANY with group ID=1
			}
			
			ActorChannelMappingDAO acmDAO=DAOFactory.getInstance().getActorChannelMappingDao();
			List<ActorChannelMapping> list=acmDAO.get(acmQuery);
			//Setting false only when the isAllowed is explicitly set as false in the ActorChannelMapping table so that the existing transactions are not affected
			if(CollectionUtils.isNotEmpty(list))
			{
				allow=list.get(0).getIsallowed()==0?false:true;
			}else
			{	
			//do nothing
			}
		}
		}
		return allow;
}
}
