package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.Pocket;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAdjustmentsPocket;
import com.mfino.service.SystemParametersService;
import com.mfino.uicore.fix.processor.AdjustmentsPocketProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 * @author Srikanth
 *
 */
@Service("AdjustmentsPocketProcessorImpl")
public class AdjustmentsPocketProcessorImpl extends BaseFixProcessor implements AdjustmentsPocketProcessor {
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	private void updateMessage(Pocket pocket, CmFinoFIX.CMJSAdjustmentsPocket.CGEntries entry) {
		entry.setPocketID(pocket.getID());
		if(pocket.getPocketTemplate() != null) {
			entry.setPocketTemplateID(pocket.getPocketTemplate().getID());
			entry.setPocketTemplateDescription(pocket.getPocketTemplate().getDescription() + "(ID:" + pocket.getID() + ")");
		}		
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSAdjustmentsPocket realMsg = (CMJSAdjustmentsPocket) msg;
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();	
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			if(realMsg.getSctlId() != null) {
				Long globalAccountPocketId = systemParametersService.getLong(SystemParameterKeys.GLOBAL_ACCOUNT_POCKET_ID_KEY);
				List<Pocket> results = pocketDao.getPocketsForCommodityTransferAdjustments(realMsg.getSctlId(), globalAccountPocketId);			
				if(results != null) {
					realMsg.allocateEntries(results.size());
		            for (int i = 0; i < results.size(); i++) {
		                Pocket p = results.get(i);
		                CMJSAdjustmentsPocket.CGEntries entry = new CMJSAdjustmentsPocket.CGEntries();
		                updateMessage(p, entry);
		                realMsg.getEntries()[i] = entry;
		            }
		            realMsg.setsuccess(CmFinoFIX.Boolean_True);
		            realMsg.settotal(results.size());
				}
			}						
		}
		return realMsg;
	}
}
