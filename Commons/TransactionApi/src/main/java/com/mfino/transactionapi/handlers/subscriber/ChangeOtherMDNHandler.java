/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber;

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
public interface ChangeOtherMDNHandler {

	XMLResult handle(TransactionDetails transactionDetails);

}
