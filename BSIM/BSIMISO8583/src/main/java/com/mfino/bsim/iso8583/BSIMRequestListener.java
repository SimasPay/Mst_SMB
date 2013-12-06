package com.mfino.bsim.iso8583;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.iso.packager.XMLPackager;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import com.mfino.bsim.iso8583.BSIMRequestListener;
import com.mfino.bsim.iso8583.nm.NMRequestHandler;
import com.mfino.bsim.iso8583.handlers.TransactionHandler;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;


/**
 * @author Sasi
 *
 */
public class BSIMRequestListener implements ISORequestListener{

	private static Log	    log	= LogFactory.getLog(BSIMRequestListener.class);
	private ExecutorService	ntMgmtService;
	private ExecutorService	transactionPool;
	
	public BSIMRequestListener() {
		this.transactionPool = Executors.newFixedThreadPool(10);
		this.ntMgmtService = Executors.newSingleThreadExecutor();
	}

	@Override
	public boolean process(ISOSource source, ISOMsg m) {
		log.info("BSIMRequestListener :: process() BEGIN");
		try {
			String mti = m.getMTI();
			log.info("BSIMRequestListener :: mti=" + mti + ", m=" + m.toString());
			if (mti.equals("0800")) {
				this.ntMgmtService.execute(new NMRequestHandler(m, source));
			}else if (mti.equals("0200")) {
				log.info("mti is"+ mti+" , so this is a trnasaction request");
				transactionPool.execute(new TransactionHandler(m, source));
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
