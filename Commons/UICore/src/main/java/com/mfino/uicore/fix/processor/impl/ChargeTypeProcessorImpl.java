/**
 * 
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ChargeTypeQuery;
import com.mfino.domain.ChargeType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSChargeType;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ChargeTypeProcessor;

/**
 * @author Bala Sunku
 *
 */
@Service("ChargeTypeProcessorImpl")
public class ChargeTypeProcessorImpl extends BaseFixProcessor implements ChargeTypeProcessor{
	
	private void updateEntity(ChargeType ct, CMJSChargeType.CGEntries e) {
		if (StringUtils.isNotBlank(e.getName())) {
			ct.setName(e.getName());
		}
		
		if (e.isRemoteModifiedDescription() || StringUtils.isNotBlank(e.getDescription())) {
			ct.setDescription(e.getDescription());
		}
	}
	
	private void updateMessage(ChargeType ct, CMJSChargeType.CGEntries e) {
		e.setID(ct.getId().longValue());
		e.setMSPID(ct.getMfinoServiceProvider().getId().longValue());
		e.setName(ct.getName());
		e.setDescription(ct.getDescription());
		e.setRecordVersion(((Long)ct.getVersion()).intValue());
		e.setCreatedBy(ct.getCreatedby());
		e.setCreateTime(ct.getCreatetime());
		e.setUpdatedBy(ct.getUpdatedby());
		e.setLastUpdateTime(ct.getLastupdatetime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSChargeType realMsg = (CMJSChargeType) msg;
		ChargeTypeDAO dao = DAOFactory.getInstance().getChargeTypeDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			ChargeTypeQuery query = new ChargeTypeQuery();
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
			if (realMsg.getNotEqualID() != null) {
				query.setNotEqualId(realMsg.getNotEqualID());
			}
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}
			
			List<ChargeType> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (ChargeType ct: lst){
					CMJSChargeType.CGEntries e = new CMJSChargeType.CGEntries();
					updateMessage(ct, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSChargeType.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSChargeType.CGEntries e: entries) {
				ChargeType ct = new ChargeType();
				updateEntity(ct, e);
        		try {
					dao.save(ct);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
				updateMessage(ct, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSChargeType.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSChargeType.CGEntries e: entries) {
				ChargeType ct = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(ct.getVersion()))) {
        			handleStaleDataException();
        		}
        		
        		updateEntity(ct, e);
        		try {
					dao.save(ct);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
        		updateMessage(ct, e);
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
	private CFIXMsg generateError(ConstraintViolationException cve) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		String message = MessageText._("Charge Type Name already exists in DB, please enter different name.");
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorName(CmFinoFIX.CMJSChargeType.FieldName_NameSearch);
		newEntries[0].setErrorDescription(message);
		log.warn(message, cve);
		return errorMsg;
	}
}
