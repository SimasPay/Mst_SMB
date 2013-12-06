package com.mfino.stk.validations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.transactionapi.vo.TransactionDetails;

public class RechargePinRequestValidator implements RestrictionsValidator {

	private static Logger	log	= LoggerFactory.getLogger(RechargePinRequestValidator.class);

	@Override
	public ValidationResult validator(TransactionDetails details, ValidationResult result) {

		log.info("validating whether the given request is rechargepin(BS03) for mdn=" + details.getSourceMDN());
		result.setResult(true);
		if ("BS03".equalsIgnoreCase(details.getTransactionCode())) {
			log.warn("request is rechargepin(BS03).Rejecting the request as Service not available for mdn=" + details.getSourceMDN());
			result.getXmlResult().setMessage("Service not available.Please try again later");
			result.setResult(false);
		}
		return result;

	}

}
