package com.mfino.fep.validators;

import com.mfino.fep.FEPConstants;
import com.mfino.fep.ValidatorNotFoundException;

public class ISORequestValidatorFactory {
	
	public static ISORequestValidator getValidator(String mti,String processingCode) throws ValidatorNotFoundException{
		ISORequestValidator validator = null;
		if(FEPConstants.REQUEST_MSG_TYPE.equals(mti)){
			if(FEPConstants.WITHDRAW_REQUEST.equals(processingCode))
				validator = new CashoutRequestValidator();
			else
				throw new ValidatorNotFoundException();
		}else if(FEPConstants.REVERSAL_REQUEST_MSG_TYPE.equals(mti)){
			if(FEPConstants.WITHDRAW_REQUEST.equals(processingCode))
				validator = new CashoutReversalRequestValidator();
			else
				throw new ValidatorNotFoundException();
		}
		else 
			throw new ValidatorNotFoundException();
		
		return validator;
	}
	

}
