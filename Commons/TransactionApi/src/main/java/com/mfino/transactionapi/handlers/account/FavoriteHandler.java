/**
 * 
 */
package com.mfino.transactionapi.handlers.account;

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Srikanth
 *
 */
public interface FavoriteHandler {

	XMLResult handle(TransactionDetails transactionDetails);

}
