/**
 * 
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.ChargePricingDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ChargePricingQuery;
import com.mfino.domain.ChargePricing;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSChargePricing;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ChargePricingProcessor;

/**
 * @author Bala Sunku
 *
 */
@Service("ChargePricingProcessorImpl")
public class ChargePricingProcessorImpl extends BaseFixProcessor implements ChargePricingProcessor{
	
	private void updateEntity(ChargePricing cp, CMJSChargePricing.CGEntries e) {
		ChargeDefinitionDAO cpDAO = DAOFactory.getInstance().getChargeDefinitionDAO();
		
		if (e.getChargeDefinitionID() != null) {
			cp.setChargeDefinition(cpDAO.getById(e.getChargeDefinitionID()));
		}
		
		if (e.getIsDefault() != null) {
			cp.setIsdefault((short) (e.getIsDefault() ? 1 : 0));
		}
		
		if (e.getIsDefault() != null && e.getIsDefault().booleanValue()) {
			cp.setMinamount(null);
			cp.setMaxamount(null);
		} else {
			if (e.getMinAmount() != null) {
				cp.setMinamount(e.getMinAmount());
			}
			if (e.getMaxAmount() != null) {
				cp.setMaxamount(e.getMaxAmount());
			}
		}
		
		
		if (e.getCharge() != null) {
			cp.setCharge(e.getCharge());
		}
		if (e.getMinCharge() != null) {
			cp.setMincharge(e.getMinCharge());
		}
		if (e.getMaxCharge() != null) {
			cp.setMaxcharge(e.getMaxCharge());
		}
	}
	
	private void updateMessage(ChargePricing cp, CMJSChargePricing.CGEntries e) {
		e.setID(cp.getId().longValue());
		e.setMSPID(cp.getMfinoServiceProvider().getId().longValue());
		e.setChargeDefinitionID(cp.getChargeDefinition().getId().longValue());
		e.setIsDefault(cp.getIsdefault() != 0);
		e.setMinAmount(cp.getMinamount());
		e.setMaxAmount(cp.getMaxamount());
		
		e.setCharge(cp.getCharge());
		e.setMaxCharge(cp.getMaxcharge());
		e.setMinCharge(cp.getMincharge());
		e.setRecordVersion(((Long) cp.getVersion()).intValue());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSChargePricing realMsg = (CMJSChargePricing) msg;
		ChargePricingDAO dao = DAOFactory.getInstance().getChargePricingDAO();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			ChargePricingQuery query = new ChargePricingQuery();
			int i=0;
			
			if (realMsg.getChargeDefinitionID() != null) {
				query.setChargeDefinitionId(realMsg.getChargeDefinitionID());
			}
			
			List<ChargePricing> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (ChargePricing cp: lst){
					CMJSChargePricing.CGEntries e = new CMJSChargePricing.CGEntries();
					updateMessage(cp, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSChargePricing.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSChargePricing.CGEntries e: entries) {
				ChargePricing cp = new ChargePricing();
				updateEntity(cp, e);
				dao.save(cp);
				updateMessage(cp, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSChargePricing.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSChargePricing.CGEntries e: entries) {
				ChargePricing cp = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(cp.getVersion()))) {
        			handleStaleDataException();
        		}
        		
				     		
        		
        		updateEntity(cp, e);
				dao.save(cp);
        		updateMessage(cp, e);
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSChargePricing.CGEntries[] entries = realMsg.getEntries();
			for (CMJSChargePricing.CGEntries e: entries) {
				dao.deleteById(e.getID());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);			
		}
		return realMsg;
	}
}
