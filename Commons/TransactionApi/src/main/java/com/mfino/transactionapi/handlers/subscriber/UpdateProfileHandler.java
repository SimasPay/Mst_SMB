package com.mfino.transactionapi.handlers.subscriber;
/**
 * 
 */

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

public interface UpdateProfileHandler {

	XMLResult handle(TransactionDetails transactionDetails);

}
