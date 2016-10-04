package com.mfino.monitor.processor;

import java.text.NumberFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.Pocket;
import com.mfino.monitor.model.FloatBalanceResult;
import com.mfino.monitor.processor.Interface.FloatBalanceProcessorI;
import com.mfino.service.SystemParametersService;
import com.mfino.util.MfinoUtil;

/**
 * @author Srikanth
 * 
 */
@Service("FloatBalanceProcessor")
public class FloatBalanceProcessor extends BaseProcessor implements FloatBalanceProcessorI{
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	public FloatBalanceResult process() {
		// Get the global SVA pocket balance
		NumberFormat numberFormat = MfinoUtil.getNumberFormat();
		Pocket globalPocket = pocketDAO.getById(systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY));		
		FloatBalanceResult result = new FloatBalanceResult();
		if(globalPocket != null){
			result.setCurrentBalance(numberFormat.format(globalPocket.getCurrentbalance()));
		}		
		return result;
	}
}
