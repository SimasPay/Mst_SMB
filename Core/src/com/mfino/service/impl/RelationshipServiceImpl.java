package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.DistributionChainTemp;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.RelationshipService;
import com.mfino.service.TransactionChargingService;

/**
 * @author Sasi
 *
 */
@org.springframework.stereotype.Service("RelationshipServiceImpl")
public class RelationshipServiceImpl implements RelationshipService {
	
	private static  Logger log = LoggerFactory.getLogger(RelationshipServiceImpl.class);
	
	private Collection<Integer> relationshipTypes = new HashSet<Integer>();
	private PartnerServicesDAO partnerServicesDao = DAOFactory.getInstance().getPartnerServicesDAO();
	//private DistributionChainTemplate dct;
	//private Service service;
	private PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Collection<Integer> getRelationshipTypes(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		log.info("HierarchyServiceImpl :: getRelationshipTypes() BEGIN");

		PartnerServices sourcePartnerService = getPartnerService(sourcePartner,dct);
		PartnerServices destPartnerService = getPartnerService(destinationPartner,dct);
		//this.service = dct.getService();
		
		if(((null != dct) &&
				(null != sourcePartnerService) && (null != destPartnerService)) && 
				((null != sourcePartnerService.getDistributionChainTemp()) && (null != destPartnerService.getDistributionChainTemp())) &&
				((sourcePartnerService.getDistributionChainTemp().equals(dct)) && (destPartnerService.getDistributionChainTemp().equals(dct))))
		{
			if(isSibling(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_SIBLING);
			}
	
			if(isChild(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_CHILD);
			}
			
			if(isParent(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_PARENT);
			}
			
			if(isDescendent(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_DESCENDENT);
			}
			
			if(isAncestor(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_ANCESTOR);
			}
			
			if(isSameLevel(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_SAME_LEVEL);
			}
			
			if(isLowerLevel(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_LOWER_LEVEL);
			}
			
			if(isUpperLevel(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_UPPER_LEVEL);
			}
			
			if(isBelongsToTree(sourcePartner, destinationPartner,dct)){
				relationshipTypes.add(CmFinoFIX.RelationShipType_BELONGS_TO_TREE);
			}
		}
		
		log.info("HierarchyServiceImpl :: getRelationshipTypes() END");
		return relationshipTypes;
	}
	
	/**
	 * 
	 * @param sourcePartner
	 * @param destinationPartner
	 * @param sourcePartnerService
	 * @param destPartnerService
	 * @return
	 */
	private Boolean isSibling(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;
		
		PartnerServices sourcePartnerService = getPartnerService(sourcePartner,dct);
		PartnerServices destPartnerService = getPartnerService(destinationPartner,dct);
		
		if(null != sourcePartnerService && null !=destPartnerService) {
			Partner sourceParent = sourcePartnerService.getPartnerByParentid(); 
			Partner destParent = destPartnerService.getPartnerByParentid();
			
			if((null != sourceParent) && (null != destParent)){
				if(sourceParent.equals(destParent)){
					flag = Boolean.TRUE;
				}
			}
		}		
		return flag;
	}
	
	/**
	 * is sourcePartner child of destinationPartner
	 * @param sourcePartner
	 * @param destinationPartner
	 * @param sourcePartnerService
	 * @param destPartnerService
	 * @return
	 */
	private Boolean isChild(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;		
		
		PartnerServices destPartnerService = getPartnerService(destinationPartner,dct);
		if(null != destPartnerService){
			Partner destParent = destPartnerService.getPartnerByParentid();
			if((null != destParent) && (destParent.equals(sourcePartner))){
				flag = Boolean.TRUE;
			}
		}		
		return flag;
	}

	/**
	 * is sourcePartner parent of destinationPartner
	 * @param sourcePartner
	 * @param destinationPartner
	 * @param sourcePartnerService
	 * @param destPartnerService
	 * @return
	 */
	private Boolean isParent(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;
		
		PartnerServices sourcePartnerService = getPartnerService(sourcePartner,dct);	
		if(null != sourcePartnerService) {
			Partner sourceParent = sourcePartnerService.getPartnerByParentid();
			if((null != sourceParent) && (destinationPartner.equals(sourceParent))){
				flag = Boolean.TRUE;
			}
		}		
		return flag;
	}
	
	/**
	 * is sourcePartner descendent of destinationPartner
	 * @param sourcePartner
	 * @param destinationPartner
	 * @param sourcePartnerService
	 * @param destPartnerService
	 * @return
	 */
	private Boolean isDescendent(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		log.info("RelationshipServiceImpl :: sourcePartner="+sourcePartner+", destinationPartner="+destinationPartner);
		Boolean flag = Boolean.FALSE;
		
		PartnerServices destPartnerService = getPartnerService(destinationPartner,dct);

		if(null != destPartnerService){
			Partner destParent = destPartnerService.getPartnerByParentid();
			
			while(destParent != null){
				log.info("RelationshipServiceImpl :: destParent="+destParent);
				if(destParent.equals(sourcePartner)){
					flag = Boolean.TRUE;
					break;
				}
				
				PartnerServices partnerService = getPartnerService(destParent,dct);
				log.info("RelationshipServiceImpl :: sourcePartnerService = "+partnerService);
				if(partnerService != null){
					destParent = partnerService.getPartnerByParentid();
				}
				else{
					break;
				}
			}
		}
		
		return flag;
	}
	
	/**
	 * is sourcePartner ascendent of destinationPartner
	 * @param sourcePartner
	 * @param destinationPartner
	 * @param sourcePartnerService
	 * @param destPartnerService
	 * @return
	 */
	private Boolean isAncestor(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;
		
		flag = isDescendent(destinationPartner, sourcePartner,dct);
		
		return flag;
	}
	
	private Boolean isSameLevel(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;
		
		flag = isSibling(sourcePartner, destinationPartner,dct);
		if(!flag){
			if(getLevel(sourcePartner,dct).equals(getLevel(destinationPartner,dct))){
				flag = Boolean.TRUE;
			}
		}
		
		return flag;
	}
	
	private Boolean isLowerLevel(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;
		
		if((getLevel(sourcePartner,dct).compareTo(getLevel(destinationPartner,dct))) < 0){
			flag = Boolean.TRUE;
		}
		
		return flag;
	}
	
	private Boolean isUpperLevel(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;
		
		if((getLevel(sourcePartner,dct).compareTo(getLevel(destinationPartner,dct))) > 0){
			flag = Boolean.TRUE;
		}
		
		return flag;
	}
	
	private Boolean isBelongsToTree(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct){
		Boolean flag = Boolean.FALSE;
		
		PartnerServices sourcePartnerService = getPartnerService(sourcePartner,dct);
		PartnerServices destPartnerService = getPartnerService(destinationPartner,dct);
		
		if( null !=sourcePartnerService && null != destPartnerService){
			if(sourcePartnerService.getDistributionChainTemp().equals(destPartnerService.getDistributionChainTemp())){
				flag = Boolean.TRUE;
			}			
		}		
		return flag;
	}
	
	private PartnerServices getPartnerService(Partner partner, DistributionChainTemp dct){
		PartnerServices partnerService = null;
		
		if(partner != null){
			

			Long serviceProviderId = null;
			
			try{
				serviceProviderId = transactionChargingService.getServiceProviderId(null);
			}
			catch(Exception e){
				log.error("ERROR in RelationshipServiceImpl ", e);
			}
			
			List<PartnerServices> partnerServices = partnerServicesDao.getPartnerServices(partner.getId().longValue(), serviceProviderId, dct.getServiceid());
			
			
			if((null != partnerServices) && (partnerServices.size() > 0)){
				return partnerServices.get(0);
			}
		}
		
		return partnerService;
	}
	
	public Integer getLevel(Partner partner, DistributionChainTemp dct){
		Integer level = 1;
		
		PartnerServices partnerService = getPartnerService(partner,dct);
		Partner parent = (partnerService != null) ? partnerService.getPartnerByParentid() : null;
		while(parent != null){
			partnerService = getPartnerService(parent,dct);
//			parent = partnerService.getPartnerByParentID();
			parent = (partnerService != null) ? partnerService.getPartnerByParentid() : null;
			level++;
		}
		
		return level;
	}
	
	/**
	 * Returns all descendents for this partner in this DCT.
	 * @param partner
	 * @return
	 */
	public List<Partner> getDescendents(Partner partner, DistributionChainTemp dct){
		List<Partner> partnersList = new ArrayList<Partner>();
		PartnerQuery partnerQuery = new PartnerQuery();
		partnerQuery.setDistributionChainTemplateId(dct.getId().longValue());
		partnersList = partnerDao.get(partnerQuery);
		
		List<Partner> descendentList = new ArrayList<Partner>();
		if((null != partnersList) && (partnersList.size() > 0)){
			for(Partner  pPartner : partnersList){
				if(isDescendent(pPartner, partner,dct)){
					descendentList.add(pPartner);
				}
			}
		}
		
		return descendentList;
	}
}
