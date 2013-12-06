/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.result.Result;
import com.mfino.result.SMSResult;

/**
 * @author Shashank
 *
 */
public interface ResultService {

	public Result returnResult(CMBase msg, Integer nc, SubscriberMDN smdn, Result result, boolean sms);
	/**
	 * Sends an SMS to the number that is returned by base.getSourceMDN() <br>
	 * @param base
	 *            base messages used in handlers
	 * @param result
	 *            result returned by createResult()
	 */

	public void sendSMS(CMBase base, Result result);
	
	/**
	 * Copies the result as an SMSResult. Sets all the inherited fields <br>
	 * of SMSResult object using the fields of Result object. <br>
	 * 
	 * @author Gurram Karthik
	 * 
	 * @param result
	 *            The result that is to be cloned as SMSResult
	 * 
	 * @return SMSResult the new SMSResult created after cloning has been done
	 */
	public SMSResult cloneResultAsSMSResult(Result result);


}
