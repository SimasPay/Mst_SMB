/**
 * 
 */
package com.mfino.handlers;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Deva
 *
 */
public class ResponseXMLValidator {
	
	private int notificationCode;
	
	private String notificationText;
	
	private String responseXML;
	
	private long transferId;  
	
	private Logger log = LoggerFactory.getLogger(getClass());
	public ResponseXMLValidator(int notificationCode, String notificationText, String responseXML) {
		this.notificationCode = notificationCode;
		this.notificationText = notificationText;
		this.responseXML = responseXML;
	}
	
	/*
	 * This will take care of parsing the output XML 
	 * and compare the notification code with the passed notification code 
	 */
	
	public boolean isValidOutput() {
		/*this.responseXML = "<?xml version=\"1.0\"?>" +
							"<response>" +
							"	<message code=\"7\">Maaf, transaksi pada 01/12/10 19:17 gagal. Layanan M-Commerce anda tidak aktif. Untuk mengaktifkan layanan M-Commerce, pilih menu Aktifkan M-Commerce. Info, hub 882. REF: </message>" +
							"</response>";*/
		int rtNotificationCode = 0;
		String rtMessage = null;
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(responseXML));
			while(reader.hasNext()) {
				if(reader.getEventType() == XMLStreamReader.START_ELEMENT) {
					if("message".equals(reader.getLocalName())) {
						rtNotificationCode = Integer.parseInt(reader.getAttributeValue(0));
						reader.next();
						rtMessage = new String(reader.getText());
					}else if("transferID".equals(reader.getLocalName())) {
						reader.next();
						transferId = Long.parseLong(reader.getText());
					}
				}
				reader.next();
			}
			if(this.notificationCode == rtNotificationCode) {
				log.info(rtMessage);
				return true;
			}
		} catch (XMLStreamException xmlError) {
			log.error("Error while validating response xml: ", xmlError);
		}
		return false;
	}

	/**
	 * @return the notificationCode
	 */
	public int getNotificationCode() {
		return notificationCode;
	}

	/**
	 * @param notificationCode the notificationCode to set
	 */
	public void setNotificationCode(int notificationCode) {
		this.notificationCode = notificationCode;
	}

	/**
	 * @return the notificationText
	 */
	public String getNotificationText() {
		return notificationText;
	}

	/**
	 * @param notificationText the notificationText to set
	 */
	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	/**
	 * @return the responseXML
	 */
	public String getResponseXML() {
		return responseXML;
	}

	/**
	 * @param responseXML the responseXML to set
	 */
	public void setResponseXML(String responseXML) {
		this.responseXML = responseXML;
	}
	public static void main(String[] args) {
		ResponseXMLValidator validator = new ResponseXMLValidator(7, null, null);
		System.out.println(validator.isValidOutput());
	}

	/**
	 * @return the transferId
	 */
	public long getTransferId() {
		return transferId;
	}

	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(long transferId) {
		this.transferId = transferId;
	}
}
