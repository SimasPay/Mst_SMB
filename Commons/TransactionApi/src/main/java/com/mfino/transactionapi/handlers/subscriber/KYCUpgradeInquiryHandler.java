/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sreenath
 *
 */
public interface KYCUpgradeInquiryHandler {
	
	Result handle(TransactionDetails transactionDetails);


}
