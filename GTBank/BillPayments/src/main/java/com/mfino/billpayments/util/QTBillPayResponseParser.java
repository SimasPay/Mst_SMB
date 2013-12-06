package com.mfino.billpayments.util;

import java.io.StringBufferInputStream;
import java.math.BigDecimal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mfino.billpayments.beans.QTBillPayAdviceResponse;
import com.mfino.billpayments.beans.QTBillPaymentInquiryResponse;
import com.mfino.billpayments.beans.QTQueryTransactionResponse;

/**
 * @author Sasi
 *
 */
public class QTBillPayResponseParser {
	
	private static Log log = LogFactory.getLog(QTBillPayResponseParser.class);
	
	public static QTBillPaymentInquiryResponse getBillPaymentInquiryResponse(String xml){
		QTBillPaymentInquiryResponse qtBillPayInquiryResponse = new QTBillPaymentInquiryResponse();
		
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new StringBufferInputStream(xml));
			Element rootElement = dom.getDocumentElement();
			
			NodeList nodeList = rootElement.getChildNodes();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				if("TransactionRef".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					qtBillPayInquiryResponse.setTransactionReference(nodeList.item(i).getTextContent());
				}
				if("ResponseCode".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					qtBillPayInquiryResponse.setResponseCode(nodeList.item(i).getTextContent());
				}
				if("Biller".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					qtBillPayInquiryResponse.setBiller(nodeList.item(i).getTextContent());
				}
				if("CustomerName".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					qtBillPayInquiryResponse.setCustomerName(nodeList.item(i).getTextContent());
				}
				if("Amount".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					qtBillPayInquiryResponse.setAmount(BigDecimal.valueOf(Double.valueOf(nodeList.item(i).getTextContent())));
				}
				if("CollectionsAccountNumber".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					qtBillPayInquiryResponse.setCollectionsAccountNumber(nodeList.item(i).getTextContent());
				}
				if("CollectionsAccountType".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					qtBillPayInquiryResponse.setCollectionsAccountType(nodeList.item(i).getTextContent());
				}
			}
		}
		catch(Exception e){
			log.error("QTBillPayResponseParser :: getBillPaymentInquiryResponse() - Error parsing XML", e);
		}
		
		return qtBillPayInquiryResponse;
	}
	
	public static QTBillPayAdviceResponse getBillPayAdviceResponse(String xml){
		QTBillPayAdviceResponse response = new QTBillPayAdviceResponse();
		
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new StringBufferInputStream(xml));
			Element rootElement = dom.getDocumentElement();
			
			NodeList nodeList = rootElement.getChildNodes();
			
			for(int i = 0; i < nodeList.getLength(); i++) {
				if("ResponseCode".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setResponseCode(nodeList.item(i).getTextContent());
				}
				if("TransactionRef".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setTransactionReference(nodeList.item(i).getTextContent());
				}
				if("RechargePIN".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setRechargePin(nodeList.item(i).getTextContent());
				}
			}
		}
		catch(Exception e){
			log.error("QTBillPayResponseParser :: getQTBillPayAdviceResponse() - Error parsing XML", e);
		}
		
		return response;
	}
	
	public static QTQueryTransactionResponse getQueryTransactionResponse(String xml){
		QTQueryTransactionResponse response = new QTQueryTransactionResponse();
		
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new StringBufferInputStream(xml));
			Element rootElement = dom.getDocumentElement();
			
			NodeList nodeList = rootElement.getChildNodes();
			
			for(int i = 0; i < nodeList.getLength(); i++) {
				if("ResponseCode".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setResponseCode(nodeList.item(i).getTextContent());
				}
				if("TransactionSet".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setTransactionSet(nodeList.item(i).getTextContent());
				}
				if("TransactionResponseCode".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setTransactionResponseCode(nodeList.item(i).getTextContent());
				}
				if("Status".equalsIgnoreCase(nodeList.item(i).getNodeName())){
					response.setStatus(nodeList.item(i).getTextContent());
				}
			}
		}
		catch(Exception e){
			log.error("QTTransactionResponseParser :: getQueryTransactionResponse() - Error parsing XML", e);
		}
		
		return response;
	}
}
