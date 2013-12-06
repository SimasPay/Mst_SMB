package com.mfino.stk.validations;

import com.mfino.transactionapi.vo.TransactionDetails;

public interface RestrictionsValidator {

	public ValidationResult validator(TransactionDetails details,ValidationResult result);

}
