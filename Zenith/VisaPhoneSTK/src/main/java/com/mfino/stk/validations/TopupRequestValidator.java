package com.mfino.stk.validations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.stk.BrandValidator;
import com.mfino.transactionapi.vo.TransactionDetails;

public class TopupRequestValidator implements RestrictionsValidator {

	private static Logger	               log	= LoggerFactory.getLogger(TopupRequestValidator.class);

	private BrandValidator	brandValidator;

	@Override
	public ValidationResult validator(TransactionDetails details, ValidationResult result) {

		result.setResult(true);
		String destMDn = details.getDestMDN();
		if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(details.getTransactionName())) {
			log.info("valiating mdn for airtime purchase");
			boolean res = brandValidator.validate(details.getCompanyID(), destMDn);
			if (!res) {
				log.warn("this mdn=" + destMDn + " is not permitted to do airtime purchase");
				result.getXmlResult().setMessage("Service not available.Please try again later");
				result.setResult(false);
				return result;
			}
			log.info("airitme purcahse permitted for this mdn=" + destMDn);
		}
		return result;

	}

	public void setBrandValidator(BrandValidator brandValidator) {
		this.brandValidator = brandValidator;
	}

}