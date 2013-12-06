package com.mfino.gt.nibss.util;

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
				if("code".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setResponseCode(nodeList.item(i).getTextContent());
				}
				if("accountname".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setBenName(nodeList.item(i).getTextContent());
				}
				if("sessionid".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setSessionId(nodeList.item(i).getTextContent());
				}
				if("error".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setError(nodeList.item(i).getTextContent());
				}
				if("message".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setMessage(nodeList.item(i).getTextContent());
				}
			}
		}
		catch(Exception e){
			log.error("NIBSSCashOutResponseParser :: getNIBSSCashOutResponse() - Error parsing XML", e);
		}
		
		return response;
	}
}
