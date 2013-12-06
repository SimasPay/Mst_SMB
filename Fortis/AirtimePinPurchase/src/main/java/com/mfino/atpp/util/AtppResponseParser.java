package com.mfino.atpp.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Satya
 *
 */
public class AtppResponseParser {

	private static Log log = LogFactory.getLog(AtppResponseParser.class);
	
	public static AtppResponse getAtppResponse(String xml){
		log.info("AtppResponseParser getAtppResponse xml="+xml);
		AtppResponse response = new AtppResponse();
		
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
				if("requestReference".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setRequestReference(nodeList.item(i).getTextContent());
				}
				if("transactionReference".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setTransactionReference(nodeList.item(i).getTextContent());
				}
				if("rechargePin".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setRechargePin(nodeList.item(i).getTextContent());
				}
				if("pinValue".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setPinValue(nodeList.item(i).getTextContent());
				}
				if("amountCharged".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setAmountCharged(nodeList.item(i).getTextContent());
				}
				
			}
		}
		catch(Exception e){
			log.error("AtppResponseParser :: getAtppResponse() - Error parsing XML", e);
		}
		
		return response;
	}
}
