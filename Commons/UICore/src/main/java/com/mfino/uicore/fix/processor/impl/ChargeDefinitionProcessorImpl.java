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

import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.query.ChargeDefinitionQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSChargeDefinition;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ChargeDefinitionProcessor;
import com.mfino.util.MfinoUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("ChargeDefinitionProcessorImpl")
public class ChargeDefinitionProcessorImpl extends BaseFixProcessor implements ChargeDefinitionProcessor{
	
	private void updateEntity(ChargeDefinition cd, CMJSChargeDefinition.CGEntries e) {
		ChargeTypeDAO ctDAO = DAOFactory.getInstance().getChargeTypeDAO();
		PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		if (StringUtils.isNotBlank(e.getName())) {
			cd.setName(e.getName());
		}
		
		if (e.isRemoteModifiedDescription() || StringUtils.isNotBlank(e.getDescription())) {
			cd.setDescription(e.getDescription());
		}
		
/*		if (e.getCalculationType() != null) {
			cd.setCalculationType(e.getCalculationType());
		}
*/		
		if (e.getChargeTypeID() != null) {
			cd.setChargeType(ctDAO.getById(e.getChargeTypeID()));
		}
		if (e.isRemoteModifiedIsChargeFromCustomer() || e.getIsChargeFromCustomer() != null) {
			cd.setIsChargeFromCustomer(e.getIsChargeFromCustomer());
			if (e.getIsChargeFromCustomer().booleanValue()) {
				if (e.getDependantChargeTypeID() != null) {
					cd.setChargeTypeByDependantChargeTypeID(ctDAO.getById(e.getDependantChargeTypeID()));
				}
				cd.setPartnerByFundingPartnerID(null);
				cd.setPocket(null);
			} else {
				if (e.getFundingPartnerID() != null) {
					cd.setPartnerByFundingPartnerID(partnerDAO.getById(e.getFundingPartnerID()));
				}
				if (e.getPocketID() != null) {
					cd.setPocket(pocketDAO.getById(e.getPocketID()));
				}
				cd.setChargeTypeByDependantChargeTypeID(null);
			}
		}
		if (e.isRemoteModifiedDependantChargeTypeID())  {
			if (e.getDependantChargeTypeID() != null) {
				cd.setChargeTypeByDependantChargeTypeID(ctDAO.getById(e.getDependantChargeTypeID()));
			} else {
				cd.setChargeTypeByDependantChargeTypeID(null);
			}
		}
		if (e.isRemoteModifiedIsTaxable() || e.getIsTaxable() != null) {
			cd.setIsTaxable(e.getIsTaxable());
		}
	}
	
	private void updateMessage(ChargeDefinition cd, CMJSChargeDefinition.CGEntries e) {
		e.setID(cd.getID());
		e.setMSPID(cd.getmFinoServiceProviderByMSPID().getID());
		e.setName(cd.getName());
		e.setDescription(cd.getDescription());
/*		if (cd.getCalculationType() != null) {
			e.setCalculationType(cd.getCalculationType());
			e.setCalculationTypeText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CalculationType, null, e.getCalculationType()));
		}*/
		if (cd.getChargeType() != null) {
			e.setChargeTypeID(cd.getChargeType().getID());
			e.setChargeTypeName(cd.getChargeType().getName());
		}
		if (cd.getIsChargeFromCustomer() != null) {
			e.setIsChargeFromCustomer(cd.getIsChargeFromCustomer());
		}
		if (cd.getChargeTypeByDependantChargeTypeID() != null) {
			e.setDependantChargeTypeID(cd.getChargeTypeByDependantChargeTypeID().getID());
			e.setDependantChargeTypeName(cd.getChargeTypeByDependantChargeTypeID().getName());
		}
		if (cd.getPartnerByFundingPartnerID() != null) {
			e.setFundingPartnerID(cd.getPartnerByFundingPartnerID().getID());
			e.setTradeName(cd.getPartnerByFundingPartnerID().getTradeName());
		}
		if (cd.getPocket() != null) {
			e.setPocketID(cd.getPocket().getID());
			e.setPocketDispText(MfinoUtil.getPocketDisplayText(cd.getPocket()));
		}
		if (cd.getIsTaxable() != null) {
			e.setIsTaxable(cd.getIsTaxable());
		}
		e.setRecordVersion(cd.getVersion());
		e.setCreatedBy(cd.getCreatedBy());
		e.setCreateTime(cd.getCreateTime());
		e.setUpdatedBy(cd.getUpdatedBy());
		e.setLastUpdateTime(cd.getLastUpdateTime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSChargeDefinition realMsg = (CMJSChargeDefinition) msg;
		ChargeDefinitionDAO dao = DAOFactory.getInstance().getChargeDefinitionDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			ChargeDefinitionQuery query = new ChargeDefinitionQuery();
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
			if (StringUtils.isNotBlank(realMsg.getChargeTypeSearch())) {
				query.setChargeTypeId(new Long(realMsg.getChargeTypeSearch()));
			}
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}
			
			List<ChargeDefinition> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (ChargeDefinition cd: lst){
					CMJSChargeDefinition.CGEntries e = new CMJSChargeDefinition.CGEntries();
					updateMessage(cd, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSChargeDefinition.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSChargeDefinition.CGEntries e: entries) {
				ChargeDefinition ct = new ChargeDefinition();
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
			CMJSChargeDefinition.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSChargeDefinition.CGEntries e: entries) {
				ChargeDefinition ct = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(ct.getVersion()))) {
        			handleStaleDataException();
        		}
        		if (e.getIsChargeFromCustomer() == null) {
        			e.setIsChargeFromCustomer(ct.getIsChargeFromCustomer());
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
		String message = MessageText._("Charge Definition Name already exists in DB, please enter different name.");
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorName(CmFinoFIX.CMJSChargeDefinition.FieldName_NameSearch);
		newEntries[0].setErrorDescription(message);
		log.warn(message, cve);
		return errorMsg;
	}
}
