package com.mfino.commons.hierarchyservice.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.DCTRestrictions;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerRestrictions;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Service;
import com.mfino.domain.Subscriber;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.DCTRestrictionsService;
import com.mfino.service.MfinoService;
import com.mfino.service.PartnerRestrictionsService;
import com.mfino.service.PartnerService;
import com.mfino.service.RelationshipService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionTypeService;

/**
 * @author Sasi
 *
 */
@org.springframework.stereotype.Service("HierarchyServiceImpl")
public class HierarchyServiceImpl implements HierarchyService {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("DCTRestrictionsServiceImpl")
	private DCTRestrictionsService dctRestrictionsService;
	
	@Autowired
	@Qualifier("PartnerRestrictionsServiceImpl")
	private PartnerRestrictionsService partnerRestrictionsService;
	
	@Autowired
	@Qualifier("RelationshipServiceImpl")
	private RelationshipService relationshipService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("MfinoServiceImpl")
	private MfinoService mfinoService;
	
	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService ;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;

	private Integer validateRestrictions(Subscriber sourceSubscriber, Subscriber destSubscriber, TransactionType transactionType,DistributionChainTemplate dct){
		log.info("HierarchyServiceImpl :: validateRestrictions() BEGIN ");
		Integer result = CmFinoFIX.ResponseCode_Success;
		Boolean flag = Boolean.TRUE;
		
		if(dct != null && !sourceSubscriber.getID().equals(destSubscriber.getID()))
		{
			flag = validateDctRestrictions(sourceSubscriber, destSubscriber, transactionType,dct);
			
			if(flag){
				flag = validatePartnerRestrictions(sourceSubscriber, destSubscriber, transactionType,dct);
				if(!flag){
					result = CmFinoFIX.NotificationCode_PartnerRestriction; 
				}
			}else{
				result = CmFinoFIX.NotificationCode_DCTRestriction;
			}
		}
		
		log.info("HierarchyServiceImpl :: validateRestrictions() END flag = "+flag);
		return result;
	}
	
	private boolean validateDctRestrictions(Subscriber sourceSubscriber, Subscriber destSubscriber, TransactionType transactionType,DistributionChainTemplate dct){
		log.info("HierarchyServiceImpl :: validateDctRestrictions BEGIN");
		Boolean flag = Boolean.FALSE;
		
		Partner sourcePartner = null;
		Partner destPartner = null;
		
		Set<Partner> partners = sourceSubscriber.getPartnerFromSubscriberID();
		if((null != partners) && (partners.size() > 0)){
			sourcePartner = partners.iterator().next();
		}
		
		partners = (destSubscriber != null) ? destSubscriber.getPartnerFromSubscriberID() : null;
		if((null != partners) && (partners.size() > 0)){
			destPartner = partners.iterator().next();
		}
		
		log.info("HierarchyServiceImpl  validateDctRestrictions :: sourcePartner="+sourcePartner+", destPartner="+destPartner);
		
		if(sourcePartner != null){
			DCTRestrictionsQuery dctRestrictionsQuery = new DCTRestrictionsQuery();
			dctRestrictionsQuery.setLevel(relationshipService.getLevel(sourcePartner,dct));
			dctRestrictionsQuery.setTransactionTypeId(transactionType.getID());
			dctRestrictionsQuery.setDctId(dct.getID());
			dctRestrictionsQuery.setIsAllowed(Boolean.TRUE);
			
			List<DCTRestrictions> dctRestrictions = dctRestrictionsService.getDctRestrictions(dctRestrictionsQuery);
			Collection<Integer> relationshipTypes = relationshipService.getRelationshipTypes(sourcePartner, destPartner, dct);
			
			if((null != dctRestrictions) && (dctRestrictions.size() > 0)){
				for(DCTRestrictions dctRestriction : dctRestrictions){
					if(((dctRestriction.getRelationShipType().equals(CmFinoFIX.RelationShipType_SUBSCRIBER)) && (destPartner == null)) || 
							(relationshipTypes.contains(dctRestriction.getRelationShipType()))){
						flag = Boolean.TRUE;
						break;
					}
				}
			}
		}
		
		log.info("HierarchyServiceImpl :: validateDctRestrictions END flag = "+flag);
		return flag;
	}
	
	private boolean validatePartnerRestrictions(Subscriber sourceSubscriber, Subscriber destSubscriber, TransactionType transactionType,DistributionChainTemplate dct){
		log.info("HierarchyServiceImpl :: validatePartnerRestrictions BEGIN");
		Boolean flag = Boolean.TRUE;
		
		Partner sourcePartner = null;
		Partner destPartner = null;
		
		Set<Partner> partners = sourceSubscriber.getPartnerFromSubscriberID();
		if((null != partners) && (partners.size() > 0)){
			sourcePartner = partners.iterator().next();
		}
		
		partners = (destSubscriber != null) ? destSubscriber.getPartnerFromSubscriberID() : null;
		if((null != partners) && (partners.size() > 0)){
			destPartner = partners.iterator().next();
		}
		
		log.info("HierarchyServiceImpl validatePartnerRestrictions :: sourcePartner="+sourcePartner+", destPartner="+destPartner);
		
		if(sourcePartner != null){
			PartnerRestrictionsQuery query = new PartnerRestrictionsQuery();
			query.setTransactionTypeId(transactionType.getID());
			query.setDctId(dct.getID());
			query.setIsAllowed(Boolean.TRUE);
			query.setPartnerId(sourcePartner.getID());
			
			List<PartnerRestrictions> partnerRestrictions = partnerRestrictionsService.getPartnerRestrictions(query);
			Collection<Integer> relationshipTypes = relationshipService.getRelationshipTypes(sourcePartner, destPartner, dct);
			
			if((null != partnerRestrictions) && (partnerRestrictions.size() > 0)){
				for(PartnerRestrictions partnerRestriction : partnerRestrictions){
					if(((partnerRestriction.getRelationShipType().equals(CmFinoFIX.RelationShipType_SUBSCRIBER)) && (destPartner == null)) || 
							(relationshipTypes.contains(partnerRestriction.getRelationShipType()))){
						flag = Boolean.FALSE;
						break;
					}
				}
			}
		}
		
		log.info("HierarchyServiceImpl :: validatePartnerRestrictions END flag = "+flag);
		return flag;
	}

	public Integer validate(Subscriber sourceSubscriber, Subscriber destSubscriber, String serviceName, String transactionTypeName) {

		Service service = mfinoService.getServiceByName(serviceName);
		TransactionType transactionType = transactionTypeService.getTransactionTypeByName(transactionTypeName);

		DistributionChainTemplate dct = null;
		Set<Partner> partners = sourceSubscriber.getPartnerFromSubscriberID();
		
		if((null != partners) && (partners.size() > 0)){
			Partner partner = partners.iterator().next();

			Long serviceProviderId = null;
			try{
				serviceProviderId = transactionChargingService.getServiceProviderId(null);
			}
			catch(Exception e){
				log.error("HierarchyServiceImpl :: Exception in constructor ", e);
			}
			List<PartnerServices> partnerServices = partnerService.getPartnerServices(partner.getID(), serviceProviderId, service.getID());
			if((null != partnerServices) && (partnerServices.size() > 0)){
				dct = partnerServices.iterator().next().getDistributionChainTemplate();
			}
		}
		if((sourceSubscriber == null) || (transactionType == null)){
			throw new RuntimeException("HierarchyService not properly initiated.");
		}
		
		return validateRestrictions(sourceSubscriber, destSubscriber, transactionType,dct);
	}
}
