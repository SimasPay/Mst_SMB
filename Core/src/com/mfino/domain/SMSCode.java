/**
 * 
 */
package com.mfino.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CmFinoFIX.CRSMSCode;

/**
 * @author Deva
 *
 */
public class SMSCode extends CRSMSCode {

	private List<String> allowedShortCodeList = null;
	
	public List<String> getAllowedShortCodes() {
		if (allowedShortCodeList == null && StringUtils.isNotBlank(getShortCodes())) {
			allowedShortCodeList = new ArrayList<String>();
			Scanner scanner = new Scanner(getShortCodes());
			scanner.useDelimiter(",");
			while(scanner.hasNext()) {
				allowedShortCodeList.add(scanner.next());
			}
		}
		return allowedShortCodeList;
	}
}
