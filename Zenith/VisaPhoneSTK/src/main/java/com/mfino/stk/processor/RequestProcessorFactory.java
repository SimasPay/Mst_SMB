/**
 * 
 */
package com.mfino.stk.processor;

import com.mfino.result.XMLResult;
import com.mfino.stk.vo.STKRequest;

/**
 * Factory class which classifies the processor to be used to process STKRequest
 * 
 * @author Chaitanya
 *
 */
public class RequestProcessorFactory {

	private RequestProcessor encryptedProcessor;
	
	private RequestProcessor plainTextProcessor;

	private static final String plainTextMsgIdentifier = "Reg";
	private static final String plainTextMsgIdentifierCO = "CO";
	/**
	 * Processes the request by identifying the processor to be used based on request attributes.
	 * 
	 * 
	 * @param request
	 * @return
	 */
	public XMLResult processRequest(STKRequest request){
		XMLResult result = new XMLResult();
		RequestProcessor processor = getProcessor(request);
		if(processor!=null)
		{
			result = processor.processRequest(request);
		}
		else
		{
			result.setMessage("Request Not supported: "+request.getSourceMDN());
		}
		return result;
	}
	
	/**
	 * Gets RequestProcessor based on request attributes, such as EncryptedRequestProcessor is returned
	 * if the request message is not supported plain text formats, otherwise returns PlainTextRequestProcessor 
	 *  
	 * @param request
	 * @return
	 */
	public RequestProcessor getProcessor(STKRequest request)
	{
		RequestProcessor requestProcessor = null;
		//The processor is identified based on Request Message, if it starts with Reg then plain text
		//otherwise Encrypted
		
		//This could also be made based on Short code used, if client supports the different short codes 
		//for different formats
		if(request!=null && request.getRequestMsg()!=null)
		{
			if(request.getRequestMsg().trim().startsWith(plainTextMsgIdentifier) || request.getRequestMsg().trim().toUpperCase().startsWith(plainTextMsgIdentifierCO))
			{
				requestProcessor = getPlainTextRequestProcessor();
			}
			else
			{
				requestProcessor = getEncryptedRequestProcessor();
			}
		}
		
		return requestProcessor;
	}
	
	public void setEncryptedRequestProcessor(RequestProcessor requestProcessor)
	{
		this.encryptedProcessor = requestProcessor;
	}
	
	private RequestProcessor getEncryptedRequestProcessor()
	{
		return this.encryptedProcessor;
	}
	
	public void setPlainTextRequestProcessor(RequestProcessor requestProcessor)
	{
		this.plainTextProcessor = requestProcessor;
	}
	
	private RequestProcessor getPlainTextRequestProcessor()
	{
		return this.plainTextProcessor;
	}
}
