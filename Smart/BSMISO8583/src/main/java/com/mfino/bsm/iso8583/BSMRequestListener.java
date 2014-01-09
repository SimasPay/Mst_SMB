package com.mfino.bsm.iso8583;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import com.mfino.bsm.iso8583.nm.NMRequestHandler;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;

/**
 * @author Sasi
 * 
 */
public class BSMRequestListener implements ISORequestListener {

	private static Log	    log	= LogFactory.getLog(BSMRequestListener.class);
	private ExecutorService	ntMgmtService;

	public BSMRequestListener() {
		this.ntMgmtService = Executors.newSingleThreadExecutor();
	}

	@Override
	public boolean process(ISOSource source, ISOMsg m) {
		log.info("BSMRequestListener :: process() BEGIN");
		try {
			String mti = m.getMTI();
			log.info("BSMRequestListener :: mti=" + mti + ", m=" + m.toString());
			if (mti.equals("0800")) {
				this.ntMgmtService.execute(new NMRequestHandler(m, source));
			}
			else {
				log.info("BSMRequestListener iso mti="+mti+" de-39="+m.getString(39)+",de-11="+m.getString(11)+",de-70"+m.getString(70)+" is not for BSMRequestListener - Ignoring the message");
				log.info("echo status "+StatusRegistrar.getEchoStatus("bsmmux"));
				//Space sp = SpaceFactory.getSpace();
				//sp.out ("bsmreceive", m); 
		        //return true;
			}
		}
		catch (Exception ex) {
			log.error("Exception occured", ex);
		}
		return false;
	}
}
