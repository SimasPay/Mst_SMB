package com.mfino.vah.handlers;

import java.io.IOException;

import javax.jms.JMSException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.packager.XMLPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.service.SCTLService;
import com.mfino.service.TransactionChargingService;
import com.mfino.vah.converters.IsoToNativeTransformer;
import com.mfino.vah.converters.TransformationException;
import com.mfino.vah.handlers.inqury.InquiryHandler;
import com.mfino.vah.handlers.inqury.InquiryRequestException;
import com.mfino.vah.handlers.inqury.InvalidRequestException;
import com.mfino.vah.iso8583.IsoToFixConverterFactory;
import com.mfino.vah.iso8583.ResponseCode;
import com.mfino.vah.messaging.ChannelCommunicationException;
import com.mfino.vah.messaging.QueueChannel;

public class TransactionHandler implements Runnable {

	private static Logger	         log	= LoggerFactory.getLogger(TransactionHandler.class);

	private ISOMsg	                 msg;
	private ISOSource	             source;
	private QueueChannel	         channel;
	private IsoToFixConverterFactory	isoToFixConverterFactory;
	private SCTLService sctlService;
	
	public SCTLService getSctlService() {
		return sctlService;
	}

	public void setSctlService(SCTLService sctlService) {
		this.sctlService = sctlService;
	}

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	private TransactionChargingService transactionChargingService;
	
	public TransactionHandler(ISOMsg msg, ISOSource source) throws JMSException {
		this.msg = msg;
		this.source = source;
		this.channel = new QueueChannel("CIQueue", "smartCashinOutQueue");
		this.isoToFixConverterFactory = IsoToFixConverterFactory.getInstance();
	}
	
	@Override
	public void run() {
		String element39 = ResponseCode.VAH_ERROR;
		String response = null;
		try {

			try {
				log.info("isomsg received in transactionhandler" + msg);
				XMLPackager packager = new XMLPackager();
				log.info("received isomsg-->" + new String(packager.pack(msg)));

				// signon not done
				msg.set(38, "123456");
				if (!VAHEnum.Signon.getStatus()) {
					log.warn("VAH ISO Request received before signon, so rejected");
					element39 = ResponseCode.VAH_ERROR;
				}
				else {
					String processingCode = msg.getString(3);
					String mti = msg.getMTI();

					log.info("mti=" + mti + " processingCode=" + processingCode);

					if (mti.equals("0200") && processingCode.startsWith("37"))
						throw new InquiryRequestException();

					IsoToNativeTransformer transfomrer = isoToFixConverterFactory.getTransformer(mti, processingCode);
					String request = transfomrer.transform(msg);
					log.info("sending request=" + request + " to QueueChannel for processing ");
					response = channel.requestAndReceive(request);
					log.info("received reponse=" + response + " from QueueChannel");
					
					element39 = getResponseCode(response);
				}
			}
			catch (ProcessorNotAvailableException e) {
				log.warn("received a request that is not supported yet.");
				element39 = ResponseCode.VAH_ERROR;
			}
			catch (TransformationException ex) {
				log.error("could not transform the iso msg to cashin request string,invalid xml string", ex);
				element39 = ResponseCode.VAH_ERROR;
			}
			catch (InquiryRequestException ex) {
				log.info("setting inquiry successful response");

				//InquiryHandler handler = new InquiryHandler(msg);
				try {
					response = "success";
					String de48 = InquiryHandler.getInstance().getInquiryResponseElement48(msg);
					msg.set(48, de48);
					element39 = ResponseCode.APPROVED;
				}
				catch (InvalidRequestException ex1) {
					log.warn("requests other than 8881 are not supported", ex1);
					element39 = ResponseCode.VAH_ERROR;
				}
				catch (Exception ex2) {
					log.warn("exceptin occured.", ex2);
					element39 = ResponseCode.VAH_ERROR;
				}

			}
			catch (ChannelCommunicationException ex) {
				log.error("couldnot communicate with the Queue channel", ex);
				element39 = ResponseCode.VAH_ERROR;
			}
			finally {
				if(null != response){
					msg.set(39, element39);
					msg.setResponseMTI();
					XMLPackager packager = new XMLPackager();
					log.info("response isomsg-->" + new String(packager.pack(msg)));
					source.send(msg);
				}else{
					// response will be null in timeout cases where CashIn, fix or other modules did not reply in-time. We will send generic error in such cases
					log.info("response is null. Failing the transaction and sending generic error as response");
					/*String paymentLogid = msg.getValue(11).toString();
					ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
					sctlQuery.setIntegrationTxnID(Long.parseLong(paymentLogid));
					List<ServiceChargeTxnLog> sctlList = sctlService.getByQuery(sctlQuery);
					
					ServiceChargeTxnLog sctl = null;
					if(!sctlList.isEmpty()){
						sctl = sctlList.get(0); // Only one match would be there as we do not allow duplicate entry
						transactionChargingService.failTheTransaction(sctl, "Transaction Timed Out");
					}*/
								
					msg.set(39, ResponseCode.VAH_ERROR);
					msg.setResponseMTI();
					XMLPackager packager = new XMLPackager();
					log.info("response isomsg-->" + new String(packager.pack(msg)));
					source.send(msg);
				}
				
				try {
					log.info("Closing JMS connection for this quueue channel **");
					channel.close();
				} catch (Exception e) {
					log.error("TransactionHandler, Error while closing the channel ", e);
					e.printStackTrace();
				}
			}
		}
		catch (ISOException ex) {
			log.error("Exception occured while handling the request ", ex);
		}
		catch (IOException ex) {
			log.error("Exception occured while handling the request ", ex);
		}

	}
	
	/*public String getInquiryResponseElement48() throws InvalidRequestException {

		String result = null;

		try {

			String oMdn = msg.getValue(103).toString();
			if (!oMdn.startsWith("8881")){
				log.warn("received a request other than 8881");
				throw new InvalidRequestException();
			}
			
			
			String mdn = subscriberService.normalizeMDN(oMdn.substring(4));
			log.info("normalized mdn="+mdn);
			SubscriberMDNDAO dao = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMDN subMdn = dao.getByMDN(mdn);
			Subscriber subscriber = subMdn.getSubscriber();

			String firstName = subscriber.getFirstName();
			String lastName = subscriber.getLastName();

			result = String.format("%-30s%-16s%-30s%-30s%s", "SMEM QQ " + firstName + " " + lastName, oMdn, "", "SMART E-MONEY", "10");
			
			log.info("Inquiry response DE-48="+result);
			
		}
		catch (ISOException ex) {

		}finally{
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}

		return result;
	}*/

	
	private String getResponseCode(String response) {
		if(null == response) return null;
		
		if (response.contains(">100<")) {
			log.info("response containts the string >100<.So successful");
			return "00";
		}
		else if (response.contains(">13<")) {
			log.info("response contains the string >13<. So failure because of amount limits");
			return ResponseCode.AMOUNT_OFF_LIMITS;
		}
		else if (response.contains(">112<")) {
			log.info("response contains the string >112<. So failure");
			return ResponseCode.VAH_ERROR;
		}
		else {
			log.info("response doesn't contain the string >100<. So failure");
			return ResponseCode.VAH_ERROR;
		}
	}

	public static void main(String[] args) throws ISOException, InvalidRequestException {

		ISOMsg msg = new ISOMsg();
		msg.set(103, "80019876543210");
		//InquiryHandler p = new InquiryHandler(msg);

		//System.out.println(p.getInquiryResponseElement48());

	}

}
