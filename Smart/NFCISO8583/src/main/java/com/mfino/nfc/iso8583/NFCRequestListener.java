package com.mfino.nfc.iso8583;

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

import com.mfino.nfc.iso8583.NFCRequestListener;
import com.mfino.nfc.iso8583.nm.NMRequestHandler;


/**
 * @author Sasi
 *
 */
public class NFCRequestListener implements ISORequestListener{

	private static Log	    log	= LogFactory.getLog(NFCRequestListener.class);
	private ExecutorService	ntMgmtService;

	public NFCRequestListener() {
		this.ntMgmtService = Executors.newSingleThreadExecutor();
	}

	@Override
	public boolean process(ISOSource source, ISOMsg m) {
		log.info("NFCRequestListener :: process() BEGIN");
		try {
			String mti = m.getMTI();
			log.info("NFCRequestListener :: mti=" + mti + ", m=" + m.toString());
			if (mti.equals("0800")) {
				this.ntMgmtService.execute(new NMRequestHandler(m, source));
			}
			else {
				log.info("NFCRequestListener iso mti="+mti+" is not for NFCRequestListener - Redirecting message to NFCmux-NFCreceive");
				
				Space sp = SpaceFactory.getSpace();
				sp.out ("NFCreceive", m); 
		        return true; 
			}

		}
		catch (Exception ex) {
			log.error("Exception occured", ex);
		}
		return false;
	}
}
