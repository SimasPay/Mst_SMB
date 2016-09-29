package com.mfino.validators;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Maruthi
 *
 */

public class DestPocketLimitsValidator implements IValidator {
	private static BigDecimal ZERO = new BigDecimal(0);
	private BigDecimal amount;
	private Pocket pocket;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public DestPocketLimitsValidator(BigDecimal amount,Pocket pocket){
		this.amount=amount;
		this.pocket= pocket;
				
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Integer validate() {
		
		try{
            PocketTemplate pocketTemplate = pocket.getPocketTemplate();
            
			
//		if(amount<pocketTemplate.getMinAmountPerTransaction()){
            BigDecimal currBalance = new BigDecimal(pocket.getCurrentbalance());
        if(amount.add(currBalance).compareTo(pocketTemplate.getMaximumstoredvalue())> 0){            
		return CmFinoFIX.NotificationCode_BalanceTooHigh;
		}
		
		}catch (Exception e) {
			log.error("Pocket Validation failed", e);
			return CmFinoFIX.ResponseCode_Failure;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
//	public static void main(String[] args) {
//		PocketLimitsValidator pocketLimitsValidator = new PocketLimitsValidator(1000L,11L);
//    	Integer validationResult = pocketLimitsValidator.validate();
//    	System.out.println("Validation Result " + validationResult);
//	}    

}
