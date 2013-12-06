/**
 * 
 */
package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;


public interface KYCUpgradeHandler {

 	Result handle(TransactionDetails transactionDetails) ;

}
