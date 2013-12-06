package com.mfino.fep.handlers;

import javax.jms.JMSException;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fep.FEPConfiguration;
import com.mfino.fep.FEPConstants;
import com.mfino.fep.ProcessorNotAvailableException;
import com.mfino.fep.ValidatorNotFoundException;
import com.mfino.fep.messaging.ChannelCommunicationException;
import com.mfino.fep.processor.ISORequestProcessor;
import com.mfino.fep.processor.ISORequestProcessorFactory;
import com.mfino.fep.validators.ISORequestValidator;
import com.mfino.fep.validators.ISORequestValidatorFactory;

public class TransactionHandler implements Runnable {

	private static Logger	         log	= LoggerFactory.getLogger(TransactionHandler.class);

	private ISOMsg	                 msg;
	private ISOSource	             source;
	
	public TransactionHandler(ISOMsg msg, ISOSource source) {
		this.msg = msg;
		this.source = source;
	}

	@Override
	public void run() {
		log.info("isomsg received in transactionhandler" + msg);
		
		String mti = null ;
		String processingCode = null; 
		String stan ="";
		String field39 = null;
			try {
				field39 = FEPConfiguration.getISOResponseCode(FEPConstants.ISORESPONSE_SYSTEM_MALFUNCTION);
				processingCode= msg.getString(3);
				mti = msg.getMTI();
				stan = msg.getString(11);
				
				log.info("recieved request with MTI=" + mti + " processingCode=" + processingCode+" STAN="+stan);
				ISORequestValidator validator = ISORequestValidatorFactory.getValidator(mti, processingCode);
				if(!validator.isValid(msg)){
					field39 = FEPConfiguration.getISOResponseCode(FEPConstants.ISORESPONSE_INVALID_MSG);
				}else{
					ISORequestProcessor processor = ISORequestProcessorFactory.getProcessor(mti, processingCode);
					processor.setValidator(validator);					
					processor.process(msg);
				}					
			}catch (ISOException e) {
				log.error("Error in TransactionHandler:",e);
			} catch (ValidatorNotFoundException e) {
				log.info("Validator not found for message with MTI:"+mti+" processingcode:"+processingCode);
			} catch (ProcessorNotAvailableException e) {
				log.info("processor not found for message with MTI:"+mti+" processingcode:"+processingCode);
			} catch (JMSException e) {
				log.info("JMSException in TransactionHandler:",e);
			} catch (ChannelCommunicationException e) {
				log.info("ChannelCommunicationException in TransactionHandler",e);
			}catch (Exception e) {
				log.info("Error in TransactionHandler:",e);
			}finally {
					try{
						if(StringUtils.isBlank(msg.getString(39)))
							msg.set(39, field39);
						msg.setResponseMTI();
						log.info("response -->STAN:"+ msg.getString(11)+" field39:"+msg.getString(39));
						source.send(msg);
					}catch (Exception e) {
						log.error("Error in replying request with MTI=" + mti + " processingCode=" + processingCode+" STAN="+stan,e);
					}
			}
			
	}
	
}
