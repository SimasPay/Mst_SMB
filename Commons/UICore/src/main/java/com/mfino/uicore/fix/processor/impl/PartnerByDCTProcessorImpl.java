package com.mfino.uicore.fix.processor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPartnerByDCT;
import com.mfino.service.RelationshipService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PartnerByDCTProcessor;
@Service("PartnerByDCTProcessorImpl")
public class PartnerByDCTProcessorImpl extends BaseFixProcessor implements PartnerByDCTProcessor{

	@Autowired
	@Qualifier("RelationshipServiceImpl")
	private RelationshipService relationshipService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSPartnerByDCT realMsg = (CMJSPartnerByDCT) msg;
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
		DistributionChainTemplateDAO dctDao = DAOFactory.getInstance().getDistributionChainTemplateDAO();
		
    	int size = 0;
    	int i = 0;
    	Set<Partner> lst = null;
    	
    	if (realMsg.getServiceProviderIDSearch() != null && realMsg.getServiceIDSearch() != null && realMsg.getDCTIDSearch() != null) {
    		lst = psDAO.getPartnersByDCT(realMsg.getServiceProviderIDSearch(), realMsg.getServiceIDSearch(), 
    				realMsg.getDCTIDSearch(), realMsg.getForPartnerID());
    	}
		
    	Partner forPartner = null; //partnerDao.getById(realMsg.getForPartnerID());
    	
    	PartnerQuery query = new PartnerQuery();
    	query.setPartnerId(realMsg.getForPartnerID());
    	query.setStart(realMsg.getstart());
    	query.setLimit(realMsg.getlimit());
    	
    	List<Partner> partnerList = partnerDao.get(query);
    	if((null != partnerList) && (partnerList.size() > 0)){
    		forPartner = partnerList.get(0);
    	}
    	
    	log.info("ForPartner forPartner.tradeName="+forPartner.getTradeName() + ", ID="+forPartner.getID());
    	
    	DistributionChainTemplate dct = null;
    	
    	if(realMsg.getDCTIDSearch() != null){
    		dct = dctDao.getById(realMsg.getDCTIDSearch());
    	}
    	
    	List<Partner> eligiblePartners = new ArrayList<Partner>();
    	
    	if(dct != null){
    		Integer levelCount = (null != dct.getDistributionChainLevelFromTemplateID() ? dct.getDistributionChainLevelFromTemplateID().size() : 0);
    		List<Partner> descendentsList = relationshipService.getDescendents(forPartner, dct);

    		Integer forPartnerLevel = relationshipService.getLevel(forPartner, dct);
    		
/*    		for(Partner partner: lst){
    			Boolean isDescendent = relationshipService.isDescendent(partner, forPartner);
    			if(isDescendent){
    				descendentsList.add(partner);
    			}
    		}*/
    		
    		Integer maxDescendentLevel = forPartnerLevel;
    		for(Partner descendent : descendentsList){
    			Integer level = relationshipService.getLevel(descendent, dct);
    			maxDescendentLevel = (level > maxDescendentLevel) ? level : maxDescendentLevel;
    		}
    		
    		if((null != lst) && (lst.size() > 0)){
	    		for(Partner partner : lst){
	    			if(!(descendentsList.contains(partner))){
	    				Integer partnerLevel = relationshipService.getLevel(partner, dct);
	    				if((maxDescendentLevel - forPartnerLevel + partnerLevel + 1) <= levelCount){
	    					eligiblePartners.add(partner);
	    				}
	    			}
	    		}
    		}
    	}
    	
    	if (CollectionUtils.isNotEmpty(eligiblePartners)) {
    		size = eligiblePartners.size();
    		realMsg.allocateEntries(size);
    		for(Partner p: eligiblePartners) {
    			CMJSPartnerByDCT.CGEntries e = new CMJSPartnerByDCT.CGEntries();
    			e.setID(p.getID());
    			e.setTradeName(p.getTradeName());
    			realMsg.getEntries()[i] = e;
    			i++;
    		}
    	}
    	
    	realMsg.setsuccess(CmFinoFIX.Boolean_True);
    	realMsg.settotal(size);
		
		return realMsg;
	}
}
