package com.mfino.mce.fix.conf;

import java.util.List;

import com.mfino.mce.core.conf.MCEConf;
import com.mfino.mce.core.conf.MCELinkConf;


public class FixConf extends MCEConf
{
	private String listenerServiceName;
	private String listenerServiceMethodName;
	private String fixServiceQueueName;
	private String fixServiceName;
	private String fixServiceMethodName;
	private Integer timeout;
	private Integer minThreads;
	private Integer maxThreads;

	public FixConf(List<MCELinkConf> linkList)
	{
		setLinkList(linkList);
		setListenerServiceName("fixListenerService");
		setListenerServiceMethodName("processMesg");
		setFixServiceQueueName("jms:queue:fixServiceQueue");
		setFixServiceName("fixService");
		setFixServiceMethodName("processMessage");
	}
	public void setListenerServiceName(String listenerServiceName) {
		this.listenerServiceName = listenerServiceName;
	}
	public String getListenerServiceName() {
		return listenerServiceName;
	}
	public void setListenerServiceMethodName(String serviceMethodName) {
		this.listenerServiceMethodName = serviceMethodName;
	}
	public String getListenerServiceMethodName() {
		return listenerServiceMethodName;
	}
	public void setFixServiceQueueName(String fixServiceQueueName) {
		this.fixServiceQueueName = fixServiceQueueName;
	}
	public String getFixServiceQueueName() {
		return fixServiceQueueName;
	}
	public void setFixServiceName(String fixServiceName) {
		this.fixServiceName = fixServiceName;
	}
	public String getFixServiceName() {
		return fixServiceName;
	}
	public void setFixServiceMethodName(String fixServiceMethodName) {
		this.fixServiceMethodName = fixServiceMethodName;
	}
	public String getFixServiceMethodName() {
		return fixServiceMethodName;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public Integer getMinThreads() {
		return minThreads;
	}
	public void setMinThreads(Integer minThreads) {
		this.minThreads = minThreads;
	}
	public Integer getMaxThreads() {
		return maxThreads;
	}
	public void setMaxThreads(Integer maxThreads) {
		this.maxThreads = maxThreads;
	}
}
