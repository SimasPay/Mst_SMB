package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionChargeDAO;
import com.mfino.dao.TransactionRuleDAO;
import com.mfino.dao.query.TransactionChargeQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.TransactionCharge;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSTransactionCharge;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionChargeProcessor;

@Service("TransactionChargeProcessorImpl")
public class TransactionChargeProcessorImpl extends BaseFixProcessor implements TransactionChargeProcessor{

	private TransactionChargeDAO dao = DAOFactory.getInstance().getTransactionChargeDAO();
	
	private void updateEntity(TransactionCharge tc, CMJSTransactionCharge.CGEntries e) {
		ChargeTypeDAO ctDAO = DAOFactory.getInstance().getChargeTypeDAO();
		TransactionRuleDAO trDAO = DAOFactory.getInstance().getTransactionRuleDAO();
		ChargeDefinitionDAO cdDAO = DAOFactory.getInstance().getChargeDefinitionDAO();
		
		if (e.getTransactionRuleID() != null) {
			tc.setTransactionRule(trDAO.getById(e.getTransactionRuleID()));
		}
		
		if (e.getChargeTypeID() != null) {
			tc.setChargeType(ctDAO.getById(e.getChargeTypeID()));
		}
		
		if (e.getChargeDefinitionID() != null) {
			tc.setChargeDefinition(cdDAO.getById(e.getChargeDefinitionID()));
		}
		if (e.isRemoteModifiedIsActive() || e.getIsActive() != null) {
			tc.setIsActive(e.getIsActive());
		}
		if (e.isRemoteModifiedIsChrgDstrbApplicableToSrcSub() || e.getIsChrgDstrbApplicableToSrcSub() != null) {
			tc.setIsChrgDstrbApplicableToSrcSub(e.getIsChrgDstrbApplicableToSrcSub());
		}
		if (e.isRemoteModifiedIsChrgDstrbApplicableToDestSub() || e.getIsChrgDstrbApplicableToDestSub() != null) {
			tc.setIsChrgDstrbApplicableToDestSub(e.getIsChrgDstrbApplicableToDestSub());
		}
	}
	
	private void updateMessage(TransactionCharge tc, CMJSTransactionCharge.CGEntries e) {
		e.setID(tc.getID());
		e.setMSPID(tc.getmFinoServiceProviderByMSPID().getID());
		if (tc.getTransactionRule() != null) {
			e.setTransactionRuleID(tc.getTransactionRule().getID());
			e.setTransactionRuleName(tc.getTransactionRule().getName());
		}
		if (tc.getChargeType() != null) {
			e.setChargeTypeID(tc.getChargeType().getID());
			e.setChargeTypeName(tc.getChargeType().getName());
		}
		if (tc.getChargeDefinition() != null) {
			e.setChargeDefinitionID(tc.getChargeDefinition().getID());
			e.setChargeDefinitionName(tc.getChargeDefinition().getName());
		}
		e.setRecordVersion(tc.getVersion());
		e.setCreatedBy(tc.getCreatedBy());
		e.setCreateTime(tc.getCreateTime());
		e.setUpdatedBy(tc.getUpdatedBy());
		e.setLastUpdateTime(tc.getLastUpdateTime());
		e.setIsActive(tc.getIsActive());
		e.setIsChrgDstrbApplicableToSrcSub(tc.getIsChrgDstrbApplicableToSrcSub());
		e.setIsChrgDstrbApplicableToDestSub(tc.getIsChrgDstrbApplicableToDestSub());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSTransactionCharge realMsg = (CMJSTransactionCharge) msg;
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			TransactionChargeQuery query = new TransactionChargeQuery();;
			int i=0;
			
			if (realMsg.getStartDateSearch() != null) {
				query.setStartDate(realMsg.getStartDateSearch());
			}
			if (realMsg.getEndDateSearch() != null) {
				query.setEndDate(realMsg.getEndDateSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getTransactionRuleSearch())) {
				query.setTransactionRuleId(new Long(realMsg.getTransactionRuleSearch()));
			}
			if (StringUtils.isNotBlank(realMsg.getChargeTypeSearch())) {
				query.setChargeTypeId(new Long(realMsg.getChargeTypeSearch()));
			}
			if (StringUtils.isNotBlank(realMsg.getChargeDefinitionSearch())) {
				query.setChargeDefinitionId(new Long(realMsg.getChargeDefinitionSearch()));
			}
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}
			
			List<TransactionCharge> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (TransactionCharge tc: lst){
					CMJSTransactionCharge.CGEntries e = new CMJSTransactionCharge.CGEntries();
					updateMessage(tc, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSTransactionCharge.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSTransactionCharge.CGEntries e: entries) {
				TransactionCharge ct = new TransactionCharge();
				if (!checkDependantChargeType(null,e.getTransactionRuleID(), e.getChargeDefinitionID())) {
					return generateError(2, null);
				}
				updateEntity(ct, e);
        		try {
					dao.save(ct);
				} catch (ConstraintViolationException ce) {
					return generateError(1, ce);
				}
				updateMessage(ct, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSTransactionCharge.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSTransactionCharge.CGEntries e: entries) {
				TransactionCharge tc = dao.getById(e.getID());
				Long transactionRuleId = null;
				Long chargeDefinitionId = null;
        		if (!(e.getRecordVersion().equals(tc.getVersion()))) {
        			handleStaleDataException();
        		}
        		transactionRuleId = (e.getTransactionRuleID() != null) ? e.getTransactionRuleID() : tc.getTransactionRule().getID();
        		chargeDefinitionId = (e.getChargeDefinitionID() != null) ? e.getChargeDefinitionID() : tc.getChargeDefinition().getID();
        		if (!checkDependantChargeType(tc.getID(), transactionRuleId, chargeDefinitionId)) {
					return generateError(2, null);
				}
        		updateEntity(tc, e);
        		try {
					dao.save(tc);
				} catch (ConstraintViolationException ce) {
					return generateError(1, ce);
				}
        		updateMessage(tc, e);
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			
		}
		return realMsg;
	}
	
	/**
	 * @return
	 */
	private CFIXMsg generateError(int errornum, ConstraintViolationException cvError) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		String message = "";
		if (errornum == 1) {
			message = MessageText._("For the given Transaction Rule and Charge Type only one Charge Definition can be assigned.");
			errorMsg.setErrorDescription(message);
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			newEntries[0].setErrorName(CmFinoFIX.CMJSTransactionCharge.FieldName_TransactionRuleSearch);
			newEntries[0].setErrorDescription(message);
		} else if (errornum == 2) {
			message = MessageText._("Please define the Transaction charge for dependant Charge Type.");
			errorMsg.setErrorDescription(message);
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			newEntries[0].setErrorName(CmFinoFIX.CMJSTransactionCharge.FieldName_TransactionRuleSearch);
			newEntries[0].setErrorDescription(message);
		}
		if(cvError==null){
			log.warn(message);
		}else{
			log.warn(message, cvError);
		}
		return errorMsg;
	}

	/**
	 * checks whether the Transaction charge for dependant charge type is defined or not.
	 * @param e
	 * @return
	 */
	private boolean checkDependantChargeType(Long transactionChargeId, Long transactionRuleId, Long chargeDefinitionId) {
		boolean exists = false;
		ChargeDefinitionDAO cdDAO = DAOFactory.getInstance().getChargeDefinitionDAO();
		ChargeDefinition cd = cdDAO.getById(chargeDefinitionId);
		TransactionCharge tc = null;
		if (cd != null) {
			if (cd.getChargeTypeByDependantChargeTypeID() != null) {
				tc = dao.getTransactionCharge(transactionRuleId, cd.getChargeTypeByDependantChargeTypeID().getID());
				exists = (tc != null && !(tc.getID().equals(transactionChargeId))) ? true : false;
			} else {
				exists = true;
			}
		}
		return exists;
	}
}
