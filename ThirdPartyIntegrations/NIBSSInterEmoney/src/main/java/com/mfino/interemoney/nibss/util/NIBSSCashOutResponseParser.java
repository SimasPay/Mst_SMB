package com.mfino.interemoney.nibss.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Sasi
 *
 */
public class NIBSSCashOutResponseParser {

	private static Log log = LogFactory.getLog(NIBSSCashOutResponseParser.class);
	
	public static NIBSSCashOutResponse getNIBSSCashOutResponse(String xml){
		log.info("NIBSSCashOutResponseParser getNIBSSCashOutResponse xml="+xml);
		NIBSSCashOutResponse response = new NIBSSCashOutResponse();
		
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));
			Element rootElement = dom.getDocumentElement();
			
			NodeList nodeList = rootElement.getChildNodes();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				if("responseCode".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setResponseCode(nodeList.item(i).getTextContent());
				}
				if("accountIdentificationName".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setBenName(nodeList.item(i).getTextContent());
				}
				if("accountIdentificationNumber".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setBenAccountNumber(nodeList.item(i).getTextContent());
				}
				if("destinationCode".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setDestCode(nodeList.item(i).getTextContent());
				}
				if("transactionNumber".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setTransactionNumber(nodeList.item(i).getTextContent());
				}
				if("originatorName".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setOriginatorName(nodeList.item(i).getTextContent());
				}
				if("narration".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setNarration(nodeList.item(i).getTextContent());
				}
				if("paymentReference".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setPaymentReference(nodeList.item(i).getTextContent());
				}
				if("amount".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setAmount(nodeList.item(i).getTextContent());
				}
			}
		}
		catch(Exception e){
			log.error("NIBSSCashOutResponseParser :: getNIBSSCashOutResponse() - Error parsing XML", e);
		}
		
		return response;
	}
}
