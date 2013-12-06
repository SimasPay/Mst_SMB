package com.mfino.mce.iso.jpos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.q2.Q2;

/**
 * 
 * @author Sasi
 *
 */
public class Q2MUXInitializer {

	private Q2 q2;
	Log log = LogFactory.getLog(Q2MUXInitializer.class);
	
	private String deployFolder;
	
	public void init(){
		q2 = new Q2(deployFolder);
		q2.start();
		
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e) 
		{
			log.warn("exception waiting for Q2 to start, check if signon observer is doing good ",e);
		}
	}
	
	public void stop(){
		q2.shutdown();
	}
	
	public String getDeployFolder() {
		return deployFolder;
	}

	public void setDeployFolder(String deployFolder) {
		this.deployFolder = deployFolder;
	}
}
