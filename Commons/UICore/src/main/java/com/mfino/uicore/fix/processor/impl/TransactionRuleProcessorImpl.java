package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.TransactionRuleDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.TransactionRuleQuery;
import com.mfino.domain.Groups;
import com.mfino.domain.TransactionRule;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSTransactionRule;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionRuleProcessor;

@Service("TransactionRuleProcessorImpl")
public class TransactionRuleProcessorImpl extends BaseFixProcessor implements TransactionRuleProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	private void updateEntity(TransactionRule tr, CMJSTransactionRule.CGEntries e) {
		PartnerDAO pDAO = DAOFactory.getInstance().getPartnerDAO();
		ServiceDAO sDAO = DAOFactory.getInstance().getServiceDAO();
		TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
		ChannelCodeDAO ccDAO = DAOFactory.getInstance().getChannelCodeDao();
		KYCLevelDAO kycDAO = DAOFactory.getInstance().getKycLevelDAO();
		GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
		
		if (StringUtils.isNotBlank(e.getName())) {
			tr.setName(e.getName());
		}
		if (e.getServiceProviderID() != null) {
			tr.setPartnerByServiceProviderID(pDAO.getById(e.getServiceProviderID()));
		}
		if (e.getServiceID() != null) {
			tr.setService(sDAO.getById(e.getServiceID()));
		}
		if (e.getTransactionTypeID() != null) {
			tr.setTransactionType(ttDAO.getById(e.getTransactionTypeID()));
		}
		if (e.getChannelCodeID() != null) {
			tr.setChannelCode(ccDAO.getById(e.getChannelCodeID()));
		}
		if (e.getChargeMode() != null) {
			tr.setChargemode(e.getChargeMode());
		}
		if (e.getSourceType() != null) {
			tr.setSourcetype(e.getSourceType().longValue());
		}
		if (e.getSourceKYC() != null) {
			tr.setKycLevelBySourcekyc(kycDAO.getById(e.getSourceKYC()));
		}
		if (e.getDestType() != null) {
			tr.setDesttype(e.getDestType().longValue());
		}
		if (e.getDestKYC() != null) {
			tr.setKycLevelByDestkyc(kycDAO.getById(e.getDestKYC()));
		}
		if (e.getSourceGroup() != null) {
			Groups sourceGroup = groupDao.getById(e.getSourceGroup());
			tr.setGroupBySourceGroup(sourceGroup);
		}
		if (e.getDestinationGroup() != null) {
			Groups destinationGroup = groupDao.getById(e.getDestinationGroup());
			tr.setGroupByDestinationGroup(destinationGroup);
		}
	}
	
	private void updateMessage(TransactionRule tr, CMJSTransactionRule.CGEntries e) {
		e.setID(tr.getId().longValue());
		e.setMSPID(tr.getMfinoServiceProvider().getId().longValue());
		e.setName(tr.getName());
		if (tr.getPartnerByServiceProviderID() != null) {
			e.setServiceProviderID(tr.getPartnerByServiceProviderID().getID());
			e.setServiceProviderName(tr.getPartnerByServiceProviderID().getTradeName());
		}
		if (tr.getService() != null) {
			e.setServiceID(tr.getService().getId().longValue());
			e.setServiceName(tr.getService().getDisplayname());
		}
		if (tr.getTransactionType() != null) {
			e.setTransactionTypeID(tr.getTransactionType().getId().longValue());
			e.setTransactionName(tr.getTransactionType().getDisplayname());
		}
		if (tr.getChannelCode() != null) {
			e.setChannelCodeID(tr.getChannelCode().getId().longValue());
			e.setChannelName(tr.getChannelCode().getChannelname());
		}
		if (tr.getChargemode() != 0) {
			e.setChargeMode(Integer.valueOf(Long.valueOf(tr.getChargemode()).intValue()));
			e.setChargeModeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ChargeMode, null, e.getChargeMode()));
		}
		if (tr.getSourcetype() != null) {
			e.setSourceType(tr.getSourcetype().intValue());
			e.setSourceTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberType, null, tr.getSourcetype()));
		}
		if (tr.getKycLevelBySourcekyc() != null) {
			e.setSourceKYC(tr.getKycLevelBySourcekyc().getId().longValue());
			e.setSourceKYCText(tr.getKycLevelBySourcekyc().getKyclevelname());
		}
		if (tr.getDesttype() != null) {
			e.setDestType(tr.getDesttype().intValue());
			e.setDestTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberType, null, tr.getDesttype()));
		}
		if (tr.getKycLevelByDestkyc() != null) {
			e.setDestKYC(tr.getKycLevelByDestkyc().getId().longValue());
			e.setDestKYCText(tr.getKycLevelByDestkyc().getKyclevelname());
		}
		if (tr.getGroupBySourceGroup() != null) {
			e.setSourceGroup(tr.getGroupBySourceGroup().getID());
			e.setSourceGroupName(tr.getGroupBySourceGroup().getGroupName());
		}
		if (tr.getGroupByDestinationGroup() != null) {
			e.setDestinationGroup(tr.getGroupByDestinationGroup().getID());
			e.setDestinationGroupName(tr.getGroupByDestinationGroup().getGroupName());
		}
		if(tr.getTransactionRuleAddnInfoFromTransactionRuleID() != null) {
			if(!tr.getTransactionRuleAddnInfoFromTransactionRuleID().isEmpty()) {
				e.setAdditionalInfo("Yes");
			} else {
				e.setAdditionalInfo("No");
			}
		}
		e.setRecordVersion(Integer.valueOf(Long.valueOf(tr.getVersion()).intValue()));
		e.setCreatedBy(tr.getCreatedby());
		e.setCreateTime(tr.getCreatetime());
		e.setUpdatedBy(tr.getUpdatedby());
		e.setLastUpdateTime(tr.getLastupdatetime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSTransactionRule realMsg = (CMJSTransactionRule) msg;
		TransactionRuleDAO dao = DAOFactory.getInstance().getTransactionRuleDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			TransactionRuleQuery query = new TransactionRuleQuery();
			int i=0;
			
			if (StringUtils.isNotBlank(realMsg.getNameSearch())) {
				query.setName(realMsg.getNameSearch());
			}
			if (realMsg.getStartDateSearch() != null) {
				query.setStartDate(realMsg.getStartDateSearch());
			}
			if (realMsg.getEndDateSearch() != null) {
				query.setEndDate(realMsg.getEndDateSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getServiceTypeNameSearch())) {
				query.setServiceId(new Long(realMsg.getServiceTypeNameSearch()));
			}
			if (StringUtils.isNotBlank(realMsg.getTransactionTypeNameSearch())) {
				query.setTransactionTypeId(new Long(realMsg.getTransactionTypeNameSearch()));
			}
			if (StringUtils.isNotBlank(realMsg.getAccessChannelSearch())) {
				query.setChannelCodeId(new Long(realMsg.getAccessChannelSearch()));
			}
			if (StringUtils.isNotBlank(realMsg.getChargeModeSearch())) {
				query.setChargeMode(new Integer(realMsg.getChargeModeSearch()));
			}
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}
			
			List<TransactionRule> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (TransactionRule tr: lst){
					CMJSTransactionRule.CGEntries e = new CMJSTransactionRule.CGEntries();
					updateMessage(tr, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSTransactionRule.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSTransactionRule.CGEntries e: entries) {
				/*if (checkTransactionRule(e)) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Transaction Rule is already defined for given details."));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
				}*/
				TransactionRule tr = new TransactionRule();
				updateEntity(tr, e);
        		try {
					dao.save(tr);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
				updateMessage(tr, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSTransactionRule.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSTransactionRule.CGEntries e: entries) {				
				TransactionRule tr = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(tr.getVersion()))) {
        			handleStaleDataException();
        		}
        		log.info("Charge Mode for the transaction rule - " + tr.getName()  +" is updated to "+ e.getChargeMode() + " from "+ tr.getChargemode() + " by user:" + getLoggedUserNameWithIP());
        		updateEntity(tr, e);        		
        		try {
					dao.save(tr);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
        		updateMessage(tr, e);
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
	private CFIXMsg generateError(ConstraintViolationException cvError) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		String message = MessageText._("Transaction Rule Name already exists in DB, please enter different name.");
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorName(CmFinoFIX.CMJSTransactionRule.FieldName_NameSearch);
		newEntries[0].setErrorDescription(message);
		log.warn(message, cvError);
		return errorMsg;
	}
	
	/**
	 * Checks Whether the Transaction rule is already defined for the given details or not.
	 * @param e
	 * @return
	 */
	private boolean checkTransactionRule(CMJSTransactionRule.CGEntries e) {
    	boolean duplicate = false;
		TransactionRuleDAO dao = DAOFactory.getInstance().getTransactionRuleDAO();
		TransactionRuleQuery query = new TransactionRuleQuery();
		query.setServiceProviderId(e.getServiceProviderID());
		query.setServiceId(e.getServiceID());
		query.setTransactionTypeId(e.getTransactionTypeID());
		query.setChannelCodeId(e.getChannelCodeID());
		query.setChargeMode(e.getChargeMode());
		query.setSourceGroup(e.getSourceGroup());
		query.setDestinationGroup(e.getDestinationGroup());
		query.setExactMatch(true);
		
		List<TransactionRule> lst = dao.get(query);
		
		if (CollectionUtils.isNotEmpty(lst) && lst.size() > 0 ) {
			duplicate = true;
		}
		
		return duplicate;
	}
}
