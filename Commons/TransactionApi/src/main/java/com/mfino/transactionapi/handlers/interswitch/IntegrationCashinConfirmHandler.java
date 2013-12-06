/**
 * 
 */
package com.mfino.transactionapi.handlers.interswitch;

import com.mfino.domain.ChannelCode;
import com.mfino.fix.CFIXMsg;
import com.mfino.transactionapi.handlers.interswitch.impl.TransactionDataContainerImpl;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;

/**
 * @author Shashank
 *
 */
public interface IntegrationCashinConfirmHandler {
	

	
	/**
	 * To be called after communicate only
	 * @param response
	 * @param result
	 * @return
	 */
	public WalletConfirmXMLResult postprocess(TransactionDataContainerImpl transactionDetailsContainer, ChannelCode channel, CFIXMsg response,WalletConfirmXMLResult result);
	
	/**
	 * TO be called after preprocess only
	 * @return
	 */
	public CFIXMsg communicate(TransactionDataContainerImpl cashinDataConatiner, ChannelCode channel);
	
	/**
	 * To be called first in this handler before communicate or postprocess
	 * @return
	 */
	public WalletConfirmXMLResult preprocess(TransactionDataContainerImpl details, ChannelCode channel,String transactionIdentifier);
}
