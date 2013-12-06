/**
 * 
 */
package com.mfino.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ServiceCharge;
import com.mfino.service.MoneyService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TaxService;

/**
 * @author Bala Sunku
 *
 */
@Service("TaxServiceImpl")
public class TaxServiceImpl implements TaxService {
	private static Logger log = LoggerFactory.getLogger(TaxServiceImpl.class);
	public BigDecimal TAX_PERCENTAGE = BigDecimal.ZERO;
	
	@Autowired
	@Qualifier("MoneyServiceImpl")
	public MoneyService moneyService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Override
	public BigDecimal calculateTax(ServiceCharge serviceCharge) {
		BigDecimal tax = BigDecimal.ZERO;
		TAX_PERCENTAGE = systemParametersService.getBigDecimal(SystemParameterKeys.TAX_PERCENTAGE);
		tax = moneyService.calculateShareAmount(serviceCharge.getTransactionAmount(), TAX_PERCENTAGE);
		log.info("Calculated Tax with percentage = " + TAX_PERCENTAGE + " is --> " + tax);
		return tax;
	}
}
