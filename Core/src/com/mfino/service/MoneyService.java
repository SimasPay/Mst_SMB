/**
 * 
 */
package com.mfino.service;

import java.math.BigDecimal;
import java.util.HashMap;

import com.mfino.service.impl.TransactionChargingServiceImpl.TransactionChargeShareHolder;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * @author Bala Sunku
 *
 */
public interface MoneyService {
	
	/**
	 * Returns the rounded BigDecimal to the decimals.
	 * @param bigDecimal
	 * @return
	 */
	public BigDecimal round(BigDecimal bigDecimal);
	
	/**
	 * Returns the Percentage value of the given amount as per the rounded decimals
	 * @param amount
	 * @param sharePercentage
	 * @return
	 */
	public BigDecimal calculateShareAmount(BigDecimal amount, BigDecimal sharePercentage);
	
	/**
	 * Returns the resultant charge calculated from the expression
	 * @param amount
	 * @param regExpr
	 * @return
	 * @throws UnparsableExpressionException 
	 * @throws UnknownFunctionException 
	 */
	public BigDecimal calculateChargeFromExpr(BigDecimal amount, String regExpr) throws UnknownFunctionException, UnparsableExpressionException;
	
	/**
	 * Subtracts the value from the given BigDecimal.
	 * @param from
	 * @param value
	 * @return
	 */
	public BigDecimal subtract(BigDecimal from, BigDecimal value);
	
	/**
	 * Adds the given two BigDecimals.
	 * @param bigDecimal1
	 * @param bigDecimal2
	 * @return
	 */
	public BigDecimal add(BigDecimal bigDecimal1, BigDecimal bigDecimal2);
	
	/**
	 * Rounds off the share amount for each share holder and return the map with the resultant share amount.
	 * @param map
	 * @return
	 */
	public HashMap<TransactionChargeShareHolder, BigDecimal> roundOffTheShareMoney(HashMap<TransactionChargeShareHolder, BigDecimal> map);
}
