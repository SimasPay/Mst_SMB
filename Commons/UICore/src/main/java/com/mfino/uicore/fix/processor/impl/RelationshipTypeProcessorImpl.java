package com.mfino.uicore.fix.processor.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DCTRestrictionsDao;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerRestrictionsDao;
import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.DCTRestrictions;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerRestrictions;
import com.mfino.domain.RelationshipType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSRelationshipType;
import com.mfino.service.EnumTextService;
import com.mfino.service.RelationshipService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.RelationshipTypeProcessor;

/**
 * @author Sasi
 *
 */
@Service("RelationshipTypeProcessorImpl")
public class RelationshipTypeProcessorImpl extends BaseFixProcessor implements RelationshipTypeProcessor {
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("RelationshipServiceImpl")
	private RelationshipService relationshipService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSRelationshipType realMsg = (CMJSRelationshipType) msg;
		log.info("RelationshipTypeProcessor :: process BEGIN "+realMsg.getaction());

		DistributionChainTemplateDAO dctDao = DAOFactory.getInstance().getDistributionChainTemplateDAO();
		DCTRestrictionsDao dctRestrictionsDao = DAOFactory.getInstance().getDctRestrictionsDao();
		PartnerRestrictionsDao partnerRestrictionsDao = DAOFactory.getInstance().getPartnerRestrictionsDao();
		
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
		int size = 0;
		Long dctId = realMsg.getDCTID();
		Long partnerId = realMsg.getPartnerID();
		
		DistributionChainTemplate dct = dctDao.getById(dctId);
		Partner partner = partnerDao.getById(partnerId);
		
		int index = 0;
		if((null != dct) && (null != partner)){
			Integer level = relationshipService.getLevel(partner,dct);
			
			DCTRestrictionsQuery query = new DCTRestrictionsQuery();
			query.setDctId(dctId);
			query.setLevel(level);
			
			List<DCTRestrictions> dctRestrictions = dctRestrictionsDao.get(query);
			
			PartnerRestrictionsQuery prQuery = new PartnerRestrictionsQuery();
			prQuery.setDctId(dctId);
			prQuery.setPartnerId(partner.getID());
			
			List<PartnerRestrictions> partnerRestrictions = partnerRestrictionsDao.get(prQuery);
			
			Set<RelationshipType> relationshipTypes = new HashSet<RelationshipType>();
			
			for(DCTRestrictions dctRestriction : dctRestrictions){
				RelationshipType relationshipType = new RelationshipType();
				relationshipType.setID(dctRestriction.getRelationShipType());
				relationshipType.setDescription(enumTextService.getEnumTextValue(CmFinoFIX.TagID_RelationShipType, CmFinoFIX.Language_English, dctRestriction.getRelationShipType()));
				relationshipTypes.add(relationshipType);
			}
			
			for(PartnerRestrictions partnerRestriction : partnerRestrictions){
				RelationshipType relationshipType = new RelationshipType();
				relationshipType.setID(partnerRestriction.getRelationShipType());
				relationshipType.setDescription(enumTextService.getEnumTextValue(CmFinoFIX.TagID_RelationShipType, CmFinoFIX.Language_English, partnerRestriction.getRelationShipType()));
				relationshipTypes.add(relationshipType);
			}
			
				
    		size = relationshipTypes.size();
    		realMsg.allocateEntries(size);

    		for(RelationshipType relationshipType : relationshipTypes){
    			CMJSRelationshipType.CGEntries entry = new CMJSRelationshipType.CGEntries();
    			entry.setID(relationshipType.getID());
    			entry.setDescription(relationshipType.getDescription());
    			realMsg.getEntries()[index] = entry;
    			index++;
			}
		}

    	realMsg.setsuccess(CmFinoFIX.Boolean_True);
    	realMsg.settotal(size);
		
		return realMsg;
	}
}
