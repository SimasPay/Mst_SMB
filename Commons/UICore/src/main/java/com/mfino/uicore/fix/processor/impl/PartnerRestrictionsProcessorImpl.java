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
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.DCTRestrictions;
import com.mfino.domain.DistributionChainTemplate;
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
		
		partnerRestrictionEntry.setID(partnerRestrictions.getID());
		
		if(null != partnerRestrictions.getDistributionChainTemplateByDCTID()){
			partnerRestrictionEntry.setDCTID(partnerRestrictions.getDistributionChainTemplateByDCTID().getID());
		}
		
		if(null != partnerRestrictions.getTransactionType()){
			partnerRestrictionEntry.setTransactionTypeID(partnerRestrictions.getTransactionType().getID());
			partnerRestrictionEntry.setServiceID(partnerRestrictions.getDistributionChainTemplateByDCTID().getService().getID());
		}
		
		if(null != partnerRestrictions.getPartner()){
			partnerRestrictionEntry.setPartnerID(partnerRestrictions.getPartner().getID());
		}
		
		if(null != partnerRestrictions.getIsAllowed()){
			partnerRestrictionEntry.setIsAllowed(partnerRestrictions.getIsAllowed());
		}
		
		if((null != partnerRestrictions.getDistributionChainTemplateByDCTID()) && (null != partnerRestrictions.getPartner())){
			DCTRestrictionsServiceImpl dctRestrictionsService = new DCTRestrictionsServiceImpl();
			Integer level = relationshipService.getLevel(partnerRestrictions.getPartner(),partnerRestrictions.getDistributionChainTemplateByDCTID());
			
			DCTRestrictionsQuery query = new DCTRestrictionsQuery();
			query.setDctId(partnerRestrictions.getDistributionChainTemplateByDCTID().getID());
			query.setLevel(level);
			query.setTransactionTypeId(partnerRestrictions.getTransactionType().getID());
			query.setRelationshipType(partnerRestrictions.getRelationShipType());
			
			List<DCTRestrictions> dctRestrictions = dctRestrictionsService.getDctRestrictions(query);
			
			if(!((null != dctRestrictions) && (dctRestrictions.size() > 0))){
				partnerRestrictionEntry.setIsValid(Boolean.FALSE);
			}
		}
		
		if(null != partnerRestrictions.getRelationShipType()){
			partnerRestrictionEntry.setRelationShipType(partnerRestrictions.getRelationShipType());
		}

        if(null != partnerRestrictions.getLastUpdateTime()){
        	partnerRestrictionEntry.setLastUpdateTime(partnerRestrictions.getLastUpdateTime());
        }
        if(null != partnerRestrictions.getUpdatedBy()){
        	partnerRestrictionEntry.setUpdatedBy(partnerRestrictions.getUpdatedBy());
        }
        if(null != partnerRestrictions.getCreateTime()){
        	partnerRestrictionEntry.setCreateTime(partnerRestrictions.getCreateTime());
        }
        if(null != partnerRestrictions.getCreatedBy()){
        	partnerRestrictionEntry.setCreatedBy(partnerRestrictions.getCreatedBy());
        }
        
		log.info("PartnerRestrictionsProcessor :: updateMessage END");
	}
	
    private void updateEntity(PartnerRestrictions partnerRestrictions, CMJSPartnerRestrictions.CGEntries partnerRestrictionsEntry) {
    	log.info("partnerRestrictionsProcessor :: updateEntity BEGIN");
    	
    	DistributionChainTemplateDAO dctDao = DAOFactory.getInstance().getDistributionChainTemplateDAO();
    	TransactionTypeDAO transactionTypeDao = DAOFactory.getInstance().getTransactionTypeDAO();
    	PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
    	
        if (null != partnerRestrictionsEntry.getDCTID()) {
        	DistributionChainTemplate dct = dctDao.getById(partnerRestrictionsEntry.getDCTID());
        	partnerRestrictions.setDistributionChainTemplateByDCTID(dct);
        }
        
        if(null != partnerRestrictionsEntry.getTransactionTypeID()){
        	TransactionType transactionType = transactionTypeDao.getById(partnerRestrictionsEntry.getTransactionTypeID());
        	partnerRestrictions.setTransactionType(transactionType);
        }
        
        if(null != partnerRestrictionsEntry.getPartnerID()){
        	Partner partner = partnerDao.getById(partnerRestrictionsEntry.getPartnerID());
        	partnerRestrictions.setPartner(partner);
        }
        
        if(null != partnerRestrictionsEntry.getIsAllowed()){
        	partnerRestrictions.setIsAllowed(partnerRestrictionsEntry.getIsAllowed());
        }
        
        if(null != partnerRestrictionsEntry.getRelationShipType()){
        	partnerRestrictions.setRelationShipType(partnerRestrictionsEntry.getRelationShipType());
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
