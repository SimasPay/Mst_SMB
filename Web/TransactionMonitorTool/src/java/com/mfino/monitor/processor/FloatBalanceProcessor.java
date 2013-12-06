package com.mfino.monitor.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.Pocket;
import com.mfino.monitor.model.FloatBalanceResult;
import com.mfino.service.SystemParametersService;

/**
 * @author Srikanth
 * 
 */

public class FloatBalanceProcessor extends BaseProcessor {
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	public FloatBalanceResult process() {
		// Get the global SVA pocket balance
		Pocket globalPocket = pocketDAO.getById(systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY));		
		FloatBalanceResult result = new FloatBalanceResult();
		if(globalPocket != null){
			result.setCurrentBalance(globalPocket.getCurrentBalance());
		}		
		return result;
	}
}
