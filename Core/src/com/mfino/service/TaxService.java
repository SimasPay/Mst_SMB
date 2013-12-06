/**
 * 
 */
package com.mfino.service;

import java.math.BigDecimal;

import com.mfino.domain.ServiceCharge;

/**
 * @author Bala Sunku
 *
 */
public interface TaxService {
	
	public BigDecimal calculateTax(ServiceCharge serviceCharge);
}
