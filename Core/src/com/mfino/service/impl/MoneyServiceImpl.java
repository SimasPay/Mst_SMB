/**
 * 
 */
package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.service.MoneyService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.impl.TransactionChargingServiceImpl.TransactionChargeShareHolder;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * @author Bala Sunku
 *
 */
@Service("MoneyServiceImpl")
public class MoneyServiceImpl implements MoneyService{
	
	private static Logger log = LoggerFactory.getLogger(MoneyServiceImpl.class);
	protected int ROUND_MODE = BigDecimal.ROUND_HALF_EVEN;
	protected int DECIMALS = 2;
	
//	@Autowired
//	@Qualifier("SystemParametersServiceImpl")
//	private SystemParametersService systemParametersService ;
	
	/**
	 * Returns the rounded BigDecimal to the decimals.
	 * @param bigDecimal
	 * @return
	 */
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public BigDecimal round(BigDecimal bigDecimal) {
//		DECIMALS = systemParametersService.getInteger(SystemParameterKeys.NO_OF_DECIMALS);
		return bigDecimal.setScale(DECIMALS, ROUND_MODE);
	}
	
	/**
	 * Subtracts the value from the given BigDecimal.
	 * @param from
	 * @param value
	 * @return
	 */
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public BigDecimal subtract(BigDecimal from, BigDecimal value) {
		return round(from.subtract(value));
	}
	
	/**
	 * Adds the given two BigDecimals.
	 * @param bigDecimal1
	 * @param bigDecimal2
	 * @return
	 */
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public BigDecimal add(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
		return round(bigDecimal1.add(bigDecimal2));
	}
	
	/**
	 * Returns the Percentage value of the given amount as per the rounded decimals
	 * @param amount
	 * @param sharePercentage
	 * @return
	 */
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public BigDecimal calculateShareAmount(BigDecimal amount, BigDecimal sharePercentage) {
		return round(amount.multiply(sharePercentage).divide(new BigDecimal("100")));
	}
	
	/**
	 * Returns the resultant charge calculated from the expression
	 * @param amount
	 * @param regExpr
	 * @return
	 * @throws UnparsableExpressionException 
	 * @throws UnknownFunctionException 
	 */
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public BigDecimal calculateChargeFromExpr(BigDecimal amount, String regExpr)
			throws UnknownFunctionException, UnparsableExpressionException {
		if (regExpr.contains("%") && !regExpr.contains("amount")) {			
			regExpr = regExpr.replaceAll("%", "*amount/100");
		}
		else{		
			regExpr = regExpr.replaceAll("%", "/100");
		}
		Calculable calc = new ExpressionBuilder(regExpr).withVariable("amount",
				amount.doubleValue()).build();
		return BigDecimal.valueOf(calc.calculate());
	}
	
	/**
	 * Rounds off the share amount for each share holder and return the map with the resultant share amount.
	 * @param map
	 * @return
	 */
	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public HashMap<TransactionChargeShareHolder, BigDecimal> roundOffTheShareMoney(HashMap<TransactionChargeShareHolder, BigDecimal> map) {
		log.info("RoundOffTheShareMoney method begin");
		int size = 0;
		int i = 0;
		BigDecimal diff = BigDecimal.ZERO;		
		if (map != null) {
			size = map.size();
			Iterator<TransactionChargeShareHolder> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				TransactionChargeShareHolder key = iterator.next();
				BigDecimal shareAmount = map.get(key);
				if (i < size-1) { 
					diff = diff.add(shareAmount.subtract(round(shareAmount)));					
				} else {
					log.info("Share amount "+ shareAmount + " adjusted to ");
					shareAmount = shareAmount.add(diff);
					log.info(shareAmount + " to avoid inconsistency in total collected charge and sumup value of shared charges");
				}
				i++;
				map.put(key, round(shareAmount));
			}
		}
		return map;
	}
}
