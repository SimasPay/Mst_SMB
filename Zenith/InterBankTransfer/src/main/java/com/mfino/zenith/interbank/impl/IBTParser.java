package com.mfino.zenith.interbank.impl;

import static com.mfino.zenith.interbank.impl.IBTConstants.FUNDS_TRANSFER_WS_METHOD_NAME;
import static com.mfino.zenith.interbank.impl.IBTConstants.TRASACTION_STATUS_WS_METHOD_NAME;

import java.io.StringBufferInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Sasi
 *
 */
public class IBTParser {
	// *FindbugsChange*
	// Previous -- public static Log log = LogFactory.getLog(IBTParser.class);
	private static Log log = LogFactory.getLog(IBTParser.class);
	
	public static String getXML(IBTWSData ibtWSData, String operationName){
		
		StringBuffer xmlBuffer = new StringBuffer();
		
		if(FUNDS_TRANSFER_WS_METHOD_NAME.equals(operationName)){
			xmlBuffer.append("<FTSingleCreditRequest>");
			xmlBuffer.append("<SessionID>" + ibtWSData.getSessionId() + "</SessionID>");
			xmlBuffer.append("<DestinationBankCode>" + ibtWSData.getDestinationBankCode() + "</DestinationBankCode>");
			xmlBuffer.append("<ChannelCode>" + ibtWSData.getChannelCode() + "</ChannelCode>");
			xmlBuffer.append("<AccountName>" + ibtWSData.getAccountName()+ "</AccountName>");
			xmlBuffer.append("<AccountNumber>" + ibtWSData.getAccountNumber() + "</AccountNumber>");
			xmlBuffer.append("<OriginatorName>" + ibtWSData.getOriginatorName() + "</OriginatorName>");
			xmlBuffer.append("<Narration>" + ibtWSData.getNarration() + "</Narration>");
			xmlBuffer.append("<PaymentReference>" + ibtWSData.getPaymentReference() + "</PaymentReference>");
			xmlBuffer.append("<Amount>" + ibtWSData.getAmount() + "</Amount>");
			xmlBuffer.append("</FTSingleCreditRequest>");
		}
		else if(TRASACTION_STATUS_WS_METHOD_NAME.equals(operationName)){
			xmlBuffer.append("<TSQuerySingleRequest>");
			xmlBuffer.append("<DestinationBankCode>" + ibtWSData.getDestinationBankCode() + "</DestinationBankCode>");
			xmlBuffer.append("<ChannelCode>" + ibtWSData.getChannelCode() + "</ChannelCode>");
			xmlBuffer.append("<PaymentReference>" + ibtWSData.getPaymentReference() + "</PaymentReference>");
			xmlBuffer.append("</TSQuerySingleRequest>");
		}
		
		return xmlBuffer.toString();
	}
	
	public static IBTWSData getIBTWSResponseData(String responseXML){
		
		IBTWSData ibtWSData = new IBTWSData();
		
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new StringBufferInputStream(responseXML));
			Element rootElement = dom.getDocumentElement();
			
			NodeList nodeList = rootElement.getChildNodes();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				
				if("SessionID".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setSessionId(nodeList.item(i).getTextContent());
				}
				if("DestinationBankCode".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setDestinationBankCode(nodeList.item(i).getTextContent());
				}
				if("ChannelCode".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setChannelCode(nodeList.item(i).getTextContent());
				}
				if("AccountName".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setAccountName(nodeList.item(i).getTextContent());
				}
				if("AccountNumber".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setAccountNumber(nodeList.item(i).getTextContent());
				}
				if("OriginatorName".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setOriginatorName(nodeList.item(i).getTextContent());
				}
				if("Narration".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setNarration(nodeList.item(i).getTextContent());
				}
				if("PaymentReference".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setPaymentReference(nodeList.item(i).getTextContent());
				}
				if("Amount".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setAmount(nodeList.item(i).getTextContent());
				}
				if("ResponseCode".equals(nodeList.item(i).getNodeName())){
					ibtWSData.setResponseCode(nodeList.item(i).getTextContent());
				}
			}
		}
		catch(Exception e){
			log.error("IBTParser :: getIBTWSResponseData() - Error parsing XML", e);
		}
		
		return ibtWSData;
	}
}
