/**
 * 
 */
package com.mfino.stk;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.stk.vo.STKRequest;

/**
 * Encryption and Decryption class for visafone request.
 * 
 * @author Bala Sunku
 * 
 */
public class VisafonePlainTextProcessor extends VisafoneEncryptionDecryption {

	private static Logger	log	= LoggerFactory.getLogger(VisafonePlainTextProcessor.class);

	/**
	 * Process the received encrypted request message to get the plain text.
	 * 
	 * @param stkRequest
	 * @return
	 * @throws Exception
	 */
	@Override
	public STKRequest process(STKRequest stkRequest) throws Exception {
		if (stkRequest != null && !StringUtils.isBlank(stkRequest.getRequestMsg())) {
			stkRequest.setDecryptedRequestMsg(stkRequest.getRequestMsg());
		}
		return stkRequest;
	}

}
