package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerRestrictionsDao;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.DCTRestrictions;
import com.mfino.domain.DistributionChainTemp;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerRestrictions;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPartnerRestrictions;
import com.mfino.service.RelationshipService;
import com.mfino.service.impl.DCTRestrictionsServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PartnerRestrictionsProcessor;

/**
 * @author Sasi
 *
 */
@Service("PartnerRestrictionsProcessorImpl")
public class PartnerRestrictionsProcessorImpl extends BaseFixProcessor implements PartnerRestrictionsProcessor{

	@Autowired
	@Qualifier("RelationshipServiceImpl")
	private RelationshipService relationshipService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSPartnerRestrictions realMsg = (CMJSPartnerRestrictions) msg;
		log.info("PartnerRestrictionsProcessor :: process BEGIN "+realMsg.getaction());
		
		PartnerRestrictionsDao partnerRestrictionsDao = DAOFactory.getInstance().getPartnerRestrictionsDao();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            PartnerRestrictionsQuery query = new PartnerRestrictionsQuery();
            
            query.setPartnerRestrictionsId(realMsg.getIDSearch());
            query.setDctId(realMsg.getDCTIDSearch());
            query.setPartnerId(Long.valueOf(realMsg.getPartnerIDSearch()));
            query.setTransactionTypeId(realMsg.getTransactionTypeIDSearch());
            
            List<PartnerRestrictions> results = partnerRestrictionsDao.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                PartnerRestrictions s = results.get(i);
                CMJSPartnerRestrictions.CGEntries entry = new CMJSPartnerRestrictions.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
		}
		
		if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSPartnerRestrictions.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPartnerRestrictions.CGEntries e : entries) {
            	PartnerRestrictions s = new PartnerRestrictions();
                updateEntity(s, e);
                try{
                	partnerRestrictionsDao.save(s);
                }catch (ConstraintViolationException cve) {
                	return generateError(cve, CmFinoFIX.CMJSPartnerRestrictions.FieldName_IDSearch, "Duplicate Partner Restrictions are not allowed.");
				}
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
		}
		if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSPartnerRestrictions.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPartnerRestrictions.CGEntries e : entries) {
            	PartnerRestrictions s = partnerRestrictionsDao.getById(e.getID());

/*                // Check for Stale Data
                if (!e.getRecordVersion().equals(s.getVersion())) {
                    handleStaleDataException();
                }*/

                updateEntity(s, e);
                partnerRestrictionsDao.save(s);
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);			
		}
		if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSPartnerRestrictions.CGEntries[] entries = realMsg.getEntries();

			for (CMJSPartnerRestrictions.CGEntries e : entries) {
                partnerRestrictionsDao.deleteById(e.getID());
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
		}
		
		log.info("PartnerRestrictionsProcessor :: process END");
		return realMsg;
	}

	private void updateMessage(PartnerRestrictions partnerRestrictions, CMJSPartnerRestrictions.CGEntries partnerRestrictionEntry) {
		log.info("PartnerRestrictionsProcessor :: updateMessage BEGIN");
		DistributionChainTemplateDAO distributionChainTemplateDAO = DAOFactory.getInstance().getDistributionChainTemplateDAO();
		DistributionChainTemp distributionChainTemplate = distributionChainTemplateDAO.getById(partnerRestrictions.getDctid());
		
		partnerRestrictionEntry.setID(partnerRestrictions.getId().longValue());
		
		if(null != (Long)partnerRestrictions.getDctid()){
			partnerRestrictionEntry.setDCTID(partnerRestrictions.getDctid());
		}
		
		if(null != partnerRestrictions.getTransactiontypeid()){
			partnerRestrictionEntry.setTransactionTypeID(partnerRestrictions.getTransactiontypeid());
			partnerRestrictionEntry.setServiceID(distributionChainTemplate.getServiceid());
		}
		
		if(null != (Long)partnerRestrictions.getPartnerid()){
			partnerRestrictionEntry.setPartnerID(partnerRestrictions.getPartnerid());
		}
		
		if(null != partnerRestrictions.getIsallowed()){
			partnerRestrictionEntry.setIsAllowed(partnerRestrictions.getIsallowed() != 0);
		}
		
		if((null != (Long)partnerRestrictions.getDctid()) && (null != (Long)partnerRestrictions.getPartnerid())){
			DCTRestrictionsServiceImpl dctRestrictionsService = new DCTRestrictionsServiceImpl();
			PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
			Partner partner = partnerDAO.getById(partnerRestrictions.getPartnerid());
			Integer level = relationshipService.getLevel(partner, distributionChainTemplate);
			
			DCTRestrictionsQuery query = new DCTRestrictionsQuery();
			query.setDctId(partnerRestrictions.getDctid());
			query.setLevel(level);
			query.setTransactionTypeId(partnerRestrictions.getTransactiontypeid());
			query.setRelationshipType(partnerRestrictions.getRelationshiptype().intValue());
			
			List<DCTRestrictions> dctRestrictions = dctRestrictionsService.getDctRestrictions(query);
			
			if(!((null != dctRestrictions) && (dctRestrictions.size() > 0))){
				partnerRestrictionEntry.setIsValid(Boolean.FALSE);
			}
		}
		
		if(null != partnerRestrictions.getRelationshiptype()){
			partnerRestrictionEntry.setRelationShipType(partnerRestrictions.getRelationshiptype().intValue());
		}

        if(null != partnerRestrictions.getLastupdatetime()){
        	partnerRestrictionEntry.setLastUpdateTime(partnerRestrictions.getLastupdatetime());
        }
        if(null != partnerRestrictions.getUpdatedby()){
        	partnerRestrictionEntry.setUpdatedBy(partnerRestrictions.getUpdatedby());
        }
        if(null != partnerRestrictions.getCreatetime()){
        	partnerRestrictionEntry.setCreateTime(partnerRestrictions.getCreatetime());
        }
        if(null != partnerRestrictions.getCreatedby()){
        	partnerRestrictionEntry.setCreatedBy(partnerRestrictions.getCreatedby());
        }
        
		log.info("PartnerRestrictionsProcessor :: updateMessage END");
	}
	
    private void updateEntity(PartnerRestrictions partnerRestrictions, CMJSPartnerRestrictions.CGEntries partnerRestrictionsEntry) {
    	log.info("partnerRestrictionsProcessor :: updateEntity BEGIN");
    	
    	DistributionChainTemplateDAO dctDao = DAOFactory.getInstance().getDistributionChainTemplateDAO();
    	TransactionTypeDAO transactionTypeDao = DAOFactory.getInstance().getTransactionTypeDAO();
    	PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
    	
        if (null != partnerRestrictionsEntry.getDCTID()) {
        	partnerRestrictions.setDctid(partnerRestrictionsEntry.getDCTID());
        }
        
        if(null != partnerRestrictionsEntry.getTransactionTypeID()){
        	partnerRestrictions.setTransactiontypeid(partnerRestrictionsEntry.getTransactionTypeID());
        }
        
        if(null != partnerRestrictionsEntry.getPartnerID()){
        	partnerRestrictions.setPartnerid(partnerRestrictionsEntry.getPartnerID());
        }
        
        if(null != partnerRestrictionsEntry.getIsAllowed()){
        	partnerRestrictions.setIsallowed((short) (partnerRestrictionsEntry.getIsAllowed() ? 1:0));
        }
        
        if(null != partnerRestrictionsEntry.getRelationShipType()){
        	partnerRestrictions.setRelationshiptype(partnerRestrictionsEntry.getRelationShipType().longValue());
        }
        
    	log.info("partnerRestrictionsProcessor :: updateEntity END");
    }
    
	private CFIXMsg generateError(ConstraintViolationException cvError, String errorName, String message) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorName(errorName);
		newEntries[0].setErrorDescription(message);
		
		if(cvError==null){
			log.error(message);
		}else{
			log.error(message, cvError);
		}
		
		return errorMsg;
	}
}
