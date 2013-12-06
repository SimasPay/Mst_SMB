package com.mfino.mce.backend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class BackendActivator implements BundleActivator 
{
	private Log log = LogFactory.getLog(getClass());
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		log.info(">> MCE Backend started");
		MDC.put("app.name","MCE Backend");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		log.info(">> MCE Backend Stopped");
	}

}
