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
import com.mfino.fix.CmFinoFIX.CMJSTransactionTypeForPartnerRestrictions;
import com.mfino.service.RelationshipService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionTypePartnerRestrictionsProcessor;

@Service("TransactionTypePartnerRestrictionsProcessorImpl")
public class TransactionTypePartnerRestrictionsProcessorImpl extends BaseFixProcessor implements TransactionTypePartnerRestrictionsProcessor{

	@Autowired
	@Qualifier("RelationshipServiceImpl")
	private RelationshipService relationshipService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSTransactionTypeForPartnerRestrictions realMsg = (CMJSTransactionTypeForPartnerRestrictions) msg;
		log.info("TransactionTypePartnerRestrictionsProcessor :: process BEGIN "+realMsg.getaction());

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
			prQuery.setPartnerId(partner.getId().longValue());
			
			List<PartnerRestrictions> partnerRestrictions = partnerRestrictionsDao.get(prQuery);
			
			
			Set<TransactionType> transactionTypes = new HashSet<TransactionType>();
			
			TransactionTypeDAO transactionTypeDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			for(DCTRestrictions dctRestriction : dctRestrictions){
				TransactionType transactionType = transactionTypeDAO.getById(dctRestriction.getTransactiontypeid());
				transactionTypes.add(transactionType);
			}
			
			for(PartnerRestrictions partnerRestriction : partnerRestrictions){
				TransactionType transactionType = transactionTypeDAO.getById(partnerRestriction.getTransactiontypeid());
				transactionTypes.add(transactionType);
			}
				
    		size = transactionTypes.size();
    		realMsg.allocateEntries(size);

    		for(TransactionType transactionType : transactionTypes){
    			CMJSTransactionTypeForPartnerRestrictions.CGEntries entry = new CMJSTransactionTypeForPartnerRestrictions.CGEntries();
    			entry.setID(transactionType.getId().longValue());
    			entry.setTransactionName(transactionType.getTransactionname());
    			realMsg.getEntries()[index] = entry;
    			index++;
			}
		}

    	realMsg.setsuccess(CmFinoFIX.Boolean_True);
    	realMsg.settotal(size);
		
		return realMsg;
	}
}
