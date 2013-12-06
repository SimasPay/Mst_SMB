package com.mfino.mce.fix;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class FixActivator implements BundleActivator 
{
	private Log log = LogFactory.getLog(getClass());
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		log.info(">> mFino Router started");
		MDC.put("app.name","mFino Router");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		log.info(">> mFino Router Stopped");
	}

}
