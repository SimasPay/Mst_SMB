/**
 * 
 */
package com.mfino.transactionapi.handlers.interswitch;

import com.mfino.domain.ChannelCode;
import com.mfino.fix.CFIXMsg;
import com.mfino.transactionapi.handlers.interswitch.impl.TransactionDataContainerImpl;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;

/**
 * @author Shashank
 *
 */
public interface IntegrationCashinInquiryHandler {


	/**
	 * To be called first in this handler before communicate or postprocess
	 * @return
	 */
	public TransferInquiryXMLResult preprocess(TransactionDataContainerImpl cashinDataConatiner, ChannelCode channel);
	
	/**
	 * To be called after preprocess only
	 * @return
	 */
	public CFIXMsg communicate(TransactionDataContainerImpl cashinDataConatiner,ChannelCode channel);
	
	/**
	 * To be called after communicate only
	 * @param response
	 * @param result
	 * @return
	 */
	public TransferInquiryXMLResult postprocess(TransactionDataContainerImpl cashinDataConatiner,ChannelCode channel, CFIXMsg response, TransferInquiryXMLResult result);



}
