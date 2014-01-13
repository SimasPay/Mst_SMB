/**
 * 
 */
package com.mfino.bsim.sms.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NotificationLogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSNotificationService;

/**
 * @author Amar
 *
 */
public class BSIMSMSServiceImpl implements SMSNotificationService  
{	
	public static final String KEY_PARTNER_ID = "partnerID";
	public static final String KEY_API_TOKEN = "apiToken";
	public static final String KEY_SHORT_CODE = "shortcode";
	public static final String KEY_TO_ADDRESS = "to";
	public static final String KEY_MESSAGE = "message";
	public static final String RESPONSE_CODE_SUCCESS = "202";
	
	private Log log = LogFactory.getLog(BSIMSMSServiceImpl.class);

	private String url;
	private String partnerID;
	private String apiToken;
	private String shortcode;
	private String toAddress;
	private String message;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED) 
	public void process(Exchange httpExchange) {
		
		log.debug("BSIMSMSServiceImpl :: process() BEGIN");
		SMSNotification smsNotification = httpExchange.getIn().getBody(SMSNotification.class);
		message = smsNotification.getContent();
		toAddress = smsNotification.getMdn();
		
		Long notificationLogDetailsID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NotificationLogDetails notificationLogDetails = null;
		if(notificationLogDetailsID != null)
		{
			notificationLogDetails = notificationLogDetailsDao.getById(notificationLogDetailsID);
		}		
		
		Object obj = null;
		String responseCode;
		
		String url = "http://sms.ssdindia.in/API/WebSMS/Http/v1.0a/index.php" + "?";
		CamelContext camelContext = httpExchange.getContext();
		
		try{
			String queryString = getQueryString();
			log.info("BSIMSMSServiceImpl :: process() url="+url+", Exchange.HTTP_QUERY="+queryString);
			httpExchange.getIn().setHeader(Exchange.HTTP_QUERY, queryString);
			ProducerTemplate template = camelContext.createProducerTemplate();
			template.start();
			obj = template.sendBodyAndHeader(url, ExchangePattern.InOut,"",Exchange.HTTP_QUERY,queryString);
			template.stop();

			log.info("BSIMSMSServiceImpl DEBUG obj="+obj+", obj.getclass="+obj.getClass());
			StringWriter writer = new StringWriter();
			InputStream inputStream = (InputStream)obj;
			IOUtils.copy(inputStream, writer, "UTF-8");
			String content = writer.toString();
			log.info("Response from web call = "+content);
			responseCode = getResponseCode(content);

		}
		catch (Exception e) {
			log.error("BSIMSMSServiceImpl catch block, Error communicating with BSIM SMS Service. Error message:" + e.getMessage());
			responseCode = MCEUtil.SERVICE_UNAVAILABLE;
		}		
		
		
		if((null != responseCode) && responseCode.equals(RESPONSE_CODE_SUCCESS))
		{
			if(notificationLogDetails != null )
			{
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Success);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("SMS with NotificationLogDetailsID " + notificationLogDetailsID + " was successfully sent");
			}
		}
		else
		{
			if(notificationLogDetails != null )
			{
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("Failed to send sms with notificationLogDetailsID " + notificationLogDetailsID);
			}
		}
		log.debug("BSIMSMSServiceImpl :: process() END");	
	}

	private String getResponseCode(String response) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(response));
        Document xmlDocument = builder.parse(is);
        NodeList root = xmlDocument.getElementsByTagName("responseCode");
    	Node message = root.item(0).getFirstChild();
    	return message.getTextContent();
	}
	
	private String getQueryString() throws UnsupportedEncodingException {
		String queryString = "username=lokkum&password=rand260&sender=demo&to={1}&message={2}&reqid=1&format=json";
		String mdn = getToAddress();
		String message = URLEncoder.encode( getMessage(), "UTF-8" );
		queryString = queryString.replace("{1}", mdn);
		queryString = queryString.replace("{2}", message);
		return queryString;
	}

	public String getPartnerID() {
		return partnerID;
	}

	public void setPartnerID(String partnerID) {
		this.partnerID = partnerID;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public String getToAddress() {
		return toAddress;
	}

	public String getMessage() {
		return message;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
