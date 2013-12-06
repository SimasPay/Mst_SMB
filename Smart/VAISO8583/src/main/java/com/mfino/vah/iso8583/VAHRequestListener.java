package com.mfino.vah.iso8583;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

import com.mfino.vah.handlers.NMRequestHandler;
import com.mfino.vah.handlers.TransactionHandler;

/**
 * @author Sasi
 * 
 */
public class VAHRequestListener implements ISORequestListener {
	private static Log	    log	= LogFactory.getLog(VAHRequestListener.class);

	private ExecutorService	transactionPool;
	private ExecutorService	NMpool;

	public VAHRequestListener() {
		this.transactionPool = Executors.newFixedThreadPool(10);
		this.NMpool = Executors.newSingleThreadExecutor();
	}

	@Override
	public boolean process(ISOSource source, ISOMsg m) {

		log.info("received a vah request");
		
		try {
			String mti = m.getMTI();
			if (mti.equals("0800")) {
				log.info("mti is 0800, so this is a network management request");
				this.NMpool.execute(new NMRequestHandler(m, source));
			}
			else if (mti.equals("0200")||mti.startsWith("042")) {
				log.info("mti is"+ mti+" , so this is a trnasaction request");
				transactionPool.execute(new TransactionHandler(m, source));
			}
			else {
				log.error("iso mti="+mti+" is not supported.");
				m.set(39, "40");
				source.send(m);
			}
		}
		catch (IOException ex) {
			log.error("Exception occured while sending iso response",ex);
		}
		catch (Exception ex) {
			log.error("Exception occured",ex);
		}
		return false;
	}
	
	public void finalize(){
		
	}

}