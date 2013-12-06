package com.mfino.mce.frontend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class FrontendActivator implements BundleActivator 
{
	private Log log = LogFactory.getLog(getClass());
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		log.info(">> MCE Frontend started");
		MDC.put("app.name","MCE Frontend");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		log.info(">> MCE Frontend Stopped");
	}

}
