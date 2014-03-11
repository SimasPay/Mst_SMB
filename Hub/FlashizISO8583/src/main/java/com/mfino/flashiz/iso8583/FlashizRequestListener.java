package com.mfino.flashiz.iso8583;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import com.mfino.flashiz.iso8583.FlashizRequestListener;
import com.mfino.flashiz.iso8583.nm.NMRequestHandler;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;


/**
 * @author Sasi
 *
 */
public class FlashizRequestListener implements ISORequestListener{

	private static Log	    log	= LogFactory.getLog(FlashizRequestListener.class);
	private ExecutorService	ntMgmtService;
	private ExecutorService	transactionPool;
	
	public FlashizRequestListener() {
		this.transactionPool = Executors.newFixedThreadPool(10);
		this.ntMgmtService = Executors.newSingleThreadExecutor();
	}

	@Override
	public boolean process(ISOSource source, ISOMsg m) {
		log.info("FlashizRequestListener :: process() BEGIN");
		try {
			String mti = m.getMTI();
			log.info("FlashizRequestListener :: mti=" + mti + ", m=" + m.toString());
			if (mti.equals("0800")) {
				log.info("FlashizRequestListener iso msg received with "+",de-11="+m.getString(11)+",de-70="+m.getString(70)+",de11="+m.getString(11));
				NMRequestHandler nmRequestHandler = NMRequestHandler.getInstance();
				nmRequestHandler.setMsg(m);
				nmRequestHandler.setSource(source);
				this.ntMgmtService.execute(nmRequestHandler);
			}
			else {
				log.info("FlashizRequestListener iso mti="+mti+" de-39="+m.getString(39)+",de-11="+m.getString(11)+",de-70"+m.getString(70)+" is not for FlashizRequestListener - Ignoring the message");
				log.info("echo status "+StatusRegistrar.getEchoStatus("flashizmux"));
			}

		}catch(RuntimeException e){
			log.error("ISO Message received in Flashiz Request Listener before the creation of instance");
		}catch (Exception ex) {
			log.error("Exception occured", ex);
		}
		return false;
	}
}
