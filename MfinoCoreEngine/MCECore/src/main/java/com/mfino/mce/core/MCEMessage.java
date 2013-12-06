package com.mfino.mce.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.mfino.fix.CFIXMsg;
/*
 * @author pochadri
 * 
 */

public class MCEMessage implements Serializable
{
	CFIXMsg request;
	CFIXMsg response;
	boolean isTimeout;
	
	private Stack<String> destinationQueue = new Stack<String>();
	
	private Map<String, Object> integrationResponseDataHolder=new HashMap<String, Object>();
	public Map<String, Object> getIntegrationDataHolder() {
		return integrationResponseDataHolder;
	}
	public boolean isTimeout() {
		return isTimeout;
	}
	public void setTimeout(boolean isTimeout) {
		this.isTimeout = isTimeout;
	}
	public CFIXMsg getRequest() {
		return request;
	}
	public void setRequest(CFIXMsg request) {
		this.request = request;
	}
	public CFIXMsg getResponse() {
		return response;
	}
	public void setResponse(CFIXMsg response) {
		this.response = response;
	}
	
	public String getDestinationQueue() {
		String destQueue = null;
		if(!destinationQueue.isEmpty()){
			destQueue = destinationQueue.peek();
		}
		
		return destQueue;
	}
	
	public String getNextQueue(){
		return destinationQueue.pop();
	}
	
	public void setDestinationQueue(String destinationQueue) {
		this.destinationQueue.push(destinationQueue);
	}
	
	public void setDestinationQueues(Stack<String> destinationQueues){
		this.destinationQueue = destinationQueues;
	}
	
	public Stack<String> getDestinationQueues(){
		return destinationQueue;
	}
	
	@Override
	public String toString() {
		return "request=("+request+")" + ", response=("+response+")";
	}
}
