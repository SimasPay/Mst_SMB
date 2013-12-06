package com.mfino.billpayments.communicator;

import static com.mfino.billpayments.BillPayConstants.TERMINAL_ID_KEY;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interswitchng.services.quicktellerservice.QuickTellerService;

/**
 * 
 * @author Sasi
 *
 */
public class QTGetBillersCommunicator {

	private Map<String, String> params;
	protected Log log = LogFactory.getLog(this.getClass());
	
	private QuickTellerService quickTellerService;
	
	@SuppressWarnings("unchecked")
	public String getBillers() throws Exception {
		log.info("QTGetBillersCommunicator :: process BEGIN");
		
		String getBillersXml = "<billers></billers>";
		try
		{	
			getBillersXml = quickTellerService.getBillers(getRequestXml());
			log.info("Response getBillersXml="+getBillersXml);
		}
		catch(Exception e)
		{
			log.error("Exception during call to web service",e);
		}
		
		log.info("QTGetBillersCommunicator :: process END");
		return getBillersXml;
	}
	
	public String getRequestXml() {
		
		String requestXml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " +
				"<SearchCriteria>" +
				"<TerminalId>"+ params.get(TERMINAL_ID_KEY) +"</TerminalId>" +
				"</SearchCriteria>";
		
		log.info("QTGetBillersCommunicator :: getParameterList requestXml="+requestXml);
		return requestXml;
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public QuickTellerService getQuickTellerService() {
		return quickTellerService;
	}

	public void setQuickTellerService(QuickTellerService quickTellerService) {
		this.quickTellerService = quickTellerService;
	}
	
	
}
