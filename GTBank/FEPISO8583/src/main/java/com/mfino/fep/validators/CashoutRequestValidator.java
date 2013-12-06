package com.mfino.fep.validators;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fep.FEPConstants;

public class CashoutRequestValidator extends ISORequestValidator {
	private static Logger	         log	= LoggerFactory.getLogger(CashoutRequestValidator.class);

	private String customerMDN;
	private String fac;
	private String institutionID;

	@Override
	public boolean isValid(ISOMsg msg) {
			String subfield22 = msg.getString("127.22");
			if(StringUtils.isBlank(subfield22)){
				log.info("Field 127.022 is Empty");
				return false;
			}
			
			customerMDN = getCharacters(subfield22,FEPConstants.MDNTAG);
			if(StringUtils.isBlank(customerMDN)){
				log.info("customerMDN is empty");
				return false;
			}
			customerMDN = customerMDN.trim();
			
			fac = getCharacters(subfield22,FEPConstants.FACTAG);
			if(StringUtils.isBlank(fac)){
				log.info("fac is empty");
				return false;
			}
			fac = fac.trim();
		
		return true;
	}

	public String getCustomerMDN() {
		return customerMDN;
	}

	public String getFAC() {
		return fac;
	}

	public String getInstitutionID() {
		return institutionID;
	}

}
