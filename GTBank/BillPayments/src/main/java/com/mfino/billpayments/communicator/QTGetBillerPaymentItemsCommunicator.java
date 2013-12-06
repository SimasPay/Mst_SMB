package com.mfino.billpayments.communicator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interswitchng.services.quicktellerservice.QuickTellerService;

/**
 * @author Sasi
 *
 */
public class QTGetBillerPaymentItemsCommunicator {

	private Map<String, String> params;
	protected Log log = LogFactory.getLog(this.getClass());
	
	private QuickTellerService quickTellerService;
	
	@SuppressWarnings("unchecked")
	public String getBillerPaymentItems(String billerId) throws Exception {
		log.info("QTGetBillerPaymentItemsCommunicator :: process BEGIN billerId="+billerId);
		//billerId = billerId.replaceAll("<.*?>", "");
		
		String getBillerPaymentItemsXml = "<billers></billers>";
		try
		{	
			getBillerPaymentItemsXml = quickTellerService.getBillerPaymentItems(getRequestXml(billerId));
			log.info("Response getBillerPaymentItemsXml="+getBillerPaymentItemsXml);
			
			writeToFile(billerId,getBillerPaymentItemsXml);
		}
		catch(Exception e)
		{
			log.warn("Exception during call to web service",e);
		}
		
		log.info("QTGetBillerPaymentItemsCommunicator :: process END ");
		return getBillerPaymentItemsXml;
	}
	
	private void writeToFile(String billerId, String paymentItemsXml){
		log.info("QTBillerPaymentItemsCommunicator BEGIN billerId="+billerId);
		String dirPath = "";
		try{
			dirPath = params.get("dirPath");
			
			if((null != dirPath) && !("".equals(dirPath))){
				FileWriter fstream = new FileWriter(dirPath + File.separator + billerId.replaceAll("<.*?>", "") +"_paymentItems.xml");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(paymentItemsXml);
				out.close();
			}
		}
		catch (Exception e) {
			log.error("QTGetBillerPaymentItems-Exception ",e);
		}
		
		log.info("QTBillerPaymentItemsCommunicator END");
	}
	
	public String getRequestXml(String billerId) {
		
		String requestXml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " +
				"<SearchCriteria>" +
				"<BillerId>"+billerId+"</BillerId>" +
				"</SearchCriteria> ";
		
		log.info("QTGetBillerPaymentItemsCommunicator :: getParameterList requestXml="+requestXml);
		return requestXml;
	}

	public QuickTellerService getQuickTellerService() {
		return quickTellerService;
	}

	public void setQuickTellerService(QuickTellerService quickTellerService) {
		this.quickTellerService = quickTellerService;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}
