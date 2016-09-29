/**
 * 
 */
package com.mfino.validators;

import java.util.ArrayList;
import java.util.List;

import com.mfino.fix.CmFinoFIX;

/**
 * @author Deva
 *
 */

public class Validator {

	private List<IValidator> validatorList = new ArrayList<IValidator>();
	
	public Integer validateAll() {
		for (IValidator validator : validatorList) {
			Integer validationResult = validator.validate();
			if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
				return validationResult;
			}
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
	public boolean addValidator(IValidator validator) {
		this.validatorList.add(validator);
		return true;
	}
	
	public boolean addValidators(List<IValidator> validators) {
		this.validatorList.addAll(validators);
		return true;
	}
}
