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
			cd.setChargeTypeByChargetypeid(ctDAO.getById(e.getChargeTypeID()));
		}
		if (e.isRemoteModifiedIsChargeFromCustomer() || e.getIsChargeFromCustomer() != null) {
			cd.setIschargefromcustomer((short) (e.getIsChargeFromCustomer() ? 1 : 0));
			if (e.getIsChargeFromCustomer().booleanValue()) {
				if (e.getDependantChargeTypeID() != null) {
					cd.setChargeTypeByDependantchargetypeid(ctDAO.getById(e.getDependantChargeTypeID()));
				}
				cd.setPartner(null);
				cd.setPocket(null);
			} else {
				if (e.getFundingPartnerID() != null) {
					cd.setPartner(partnerDAO.getById(e.getFundingPartnerID()));
				}
				if (e.getPocketID() != null) {
					cd.setPocket(pocketDAO.getById(e.getPocketID()));
				}
				cd.setChargeTypeByDependantchargetypeid(null);
			}
		}
		if (e.isRemoteModifiedDependantChargeTypeID())  {
			if (e.getDependantChargeTypeID() != null) {
				cd.setChargeTypeByDependantchargetypeid(ctDAO.getById(e.getDependantChargeTypeID()));
			} else {
				cd.setChargeTypeByDependantchargetypeid(null);
			}
		}
		if (e.isRemoteModifiedIsTaxable() || e.getIsTaxable() != null) {
			cd.setIstaxable((short) (e.getIsTaxable() ? 1: 0));
		}
	}
	
	private void updateMessage(ChargeDefinition cd, CMJSChargeDefinition.CGEntries e) {
		e.setID(cd.getId().longValue());
		e.setMSPID(cd.getMfinoServiceProvider().getId().longValue());
		e.setName(cd.getName());
		e.setDescription(cd.getDescription());
/*		if (cd.getCalculationType() != null) {
			e.setCalculationType(cd.getCalculationType());
			e.setCalculationTypeText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CalculationType, null, e.getCalculationType()));
		}*/
		if (cd.getChargeTypeByChargetypeid() != null) {
			e.setChargeTypeID(cd.getChargeTypeByChargetypeid().getId().longValue());
			e.setChargeTypeName(cd.getChargeTypeByChargetypeid().getName());
		}
		if ((Short)cd.getIschargefromcustomer() != null) {
			e.setIsChargeFromCustomer(cd.getIschargefromcustomer() != 0);
		}
		if (cd.getChargeTypeByDependantchargetypeid() != null) {
			e.setDependantChargeTypeID(cd.getChargeTypeByDependantchargetypeid().getId().longValue());
			e.setDependantChargeTypeName(cd.getChargeTypeByDependantchargetypeid().getName());
		}
		if (cd.getPartner() != null) {
			e.setFundingPartnerID(cd.getPartner().getId().longValue());
			e.setTradeName(cd.getPartner().getTradename());
		}
		if (cd.getPocket() != null) {
			e.setPocketID(cd.getPocket().getId().longValue());
			e.setPocketDispText(MfinoUtil.getPocketDisplayText(cd.getPocket()));
		}
		if ( (Short) cd.getIstaxable() != null) {
			e.setIsTaxable(cd.getIstaxable() != 0);
		}
		e.setRecordVersion(((Long)cd.getVersion()).intValue());
		e.setCreatedBy(cd.getCreatedby());
		e.setCreateTime(cd.getCreatetime());
		e.setUpdatedBy(cd.getUpdatedby());
		e.setLastUpdateTime(cd.getLastupdatetime());
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
        			e.setIsChargeFromCustomer(ct.getIschargefromcustomer() != 0);
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
