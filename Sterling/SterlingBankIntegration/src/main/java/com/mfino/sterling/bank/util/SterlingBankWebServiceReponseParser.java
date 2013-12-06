package com.mfino.sterling.bank.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Amar
 *
 */
public class SterlingBankWebServiceReponseParser {

	private static Log log = LogFactory.getLog(SterlingBankWebServiceReponseParser.class);

	public static SterlingBankWebServiceResponse getSterlingBankWebServiceResponse(String xml){
		log.info("SterlingBankWebServiceReponseParser getSterlingBankWebServiceResponse xml="+xml);
		SterlingBankWebServiceResponse response = new SterlingBankWebServiceResponse();

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));
			
			//Element rootElement = dom.getDocumentElement();
			//NodeList childNodes = rootElement.getChildNodes();
			
			NodeList nodeList = dom.getElementsByTagName("IBSResponse");
			NodeList childNodes = nodeList.item(0).getChildNodes();
			
			for (int i = 0; i < childNodes.getLength(); i++) {
				if("ReferenceID".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setReferenceID(childNodes.item(i).getTextContent());
				}
				else if("RequestType".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setRequestType(childNodes.item(i).getTextContent());
				}
				else if("ResponseCode".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setResponseCode(childNodes.item(i).getTextContent());
				}
				else if("ResponseText".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setResponseText(childNodes.item(i).getTextContent());
				}
				else if("SessionID".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setSessionID(childNodes.item(i).getTextContent());
				}
				else if("Account".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setAccount(childNodes.item(i).getTextContent());
				}
				else if("Available".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setAvailableBalance(childNodes.item(i).getTextContent());
				}
				else if("Book".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					response.setBook(childNodes.item(i).getTextContent());
				}
				else if("Records".equalsIgnoreCase(childNodes.item(i).getNodeName())){
					NodeList recordNodes = childNodes.item(i).getChildNodes();
					List<SterlingBankWebServiceResponse.Record> records = new ArrayList<SterlingBankWebServiceResponse.Record>();
					for (int recordIndex = 0; recordIndex < recordNodes.getLength(); recordIndex++) 
					{
						if("Rec".equalsIgnoreCase(recordNodes.item(recordIndex).getNodeName()))
						{
							SterlingBankWebServiceResponse.Record record = response. new Record();
							NodeList recordDetails = recordNodes.item(recordIndex).getChildNodes();
							for (int recordDetailsIndex = 0; recordDetailsIndex < recordDetails.getLength(); recordDetailsIndex++) 
							{
								if("Date".equalsIgnoreCase(recordDetails.item(recordDetailsIndex).getNodeName()))
								{
									record.setDate(recordDetails.item(recordDetailsIndex).getTextContent());
								}
								else if("DC".equalsIgnoreCase(recordDetails.item(recordDetailsIndex).getNodeName()))
								{
									String dc = recordDetails.item(recordDetailsIndex).getTextContent();
									if("C".equalsIgnoreCase(dc) || "2".equalsIgnoreCase(dc))
									{
										record.setIsCredit(true);
									}
									else if("D".equalsIgnoreCase(dc) || "1".equalsIgnoreCase(dc))
									{
										record.setIsCredit(false);
									}
								}
								else if("Amount".equalsIgnoreCase(recordDetails.item(recordDetailsIndex).getNodeName()))
								{
									record.setAmount(recordDetails.item(recordDetailsIndex).getTextContent());
								}
							}
							records.add(record);
						}
					}
					response.setRecords(records);
				}

			}
		}
		catch(Exception e){
			log.error("SterlingBankWebServiceReponseParser :: getSterlingBankWebServiceResponse() - Error parsing XML", e);
		}

		return response;
	}

}
