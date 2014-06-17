/**
 * 
 */
package com.mfino.hub.sms.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpRequest;
import org.eclipse.jetty.util.UrlEncoded;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;
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


public class HubSMSServiceImpl implements SMSNotificationService  
{	
	public static final String RESPONSE_CODE_SUCCESS = "200";

	private Log log = LogFactory.getLog(HubSMSServiceImpl.class);

	private String url;
	private String uid;
	private String pwd;
	private String dest;
	private String toAddress;
	private String intPrefixCode;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED) 
	public void process(Exchange httpExchange) {

		log.debug("HubSMSServiceImpl :: process() BEGIN");
		SMSNotification smsNotification = httpExchange.getIn().getBody(SMSNotification.class);
		String message = smsNotification.getContent();
		if (smsNotification.getMdn().startsWith("62") || smsNotification.getMdn().startsWith("0")) {
			toAddress = smsNotification.getMdn();
		} else {
			toAddress = StringUtils.isNotBlank(getIntPrefixCode()) ? getIntPrefixCode() +  smsNotification.getMdn() : smsNotification.getMdn();
		}

		Long notificationLogDetailsID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NotificationLogDetails notificationLogDetails = null;
		if(notificationLogDetailsID != null)
		{
			notificationLogDetails = notificationLogDetailsDao.getById(notificationLogDetailsID);
		}		

		//Object obj = null;
		String responseCode;

		String url = getUrl() ;
		//CamelContext camelContext = httpExchange.getContext();

		try{
			log.info("HubSMSServiceImpl :: encoding message "+message);
			String encodedMessage = encode(message);
			String request = createXML(toAddress,encodedMessage,notificationLogDetailsID);
			log.info("HubSMSServiceImpl :: process() url="+url+", Exchange.HTTP_QUERY="+request);
			HttpURLConnection connection = createHttpConnection();
			connection.setRequestProperty("Content-Length", "" + Integer.toString(request.getBytes().length));
			sendRequest(request,connection);
			//Get Response	
			String response = getResponse(connection);
			responseCode = getResponseCode(connection);
			log.info("HubSMSServiceImpl :: process() response="+response+", response Code "+responseCode);
		}
		catch (Exception e) {
			log.error("HubSMSServiceImpl catch block, Error communicating with Hub SMS Service. Error message:" + e.getMessage());
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
		log.debug("HubSMSServiceImpl :: process() END");	
	}

	private String encode(String message) throws UnsupportedEncodingException {
		String encodedMessage = message.replace('<','[');
		encodedMessage = encodedMessage.replace('>', ']');
		encodedMessage = encodedMessage.replace('@', '|');
		encodedMessage = encodedMessage.replace(".",URLEncoder.encode(".","UTF-8"));
		encodedMessage = encodedMessage.replace(",", URLEncoder.encode(",","UTF-8"));
		return encodedMessage;
	}

	private String getResponseCode(HttpURLConnection connection) throws IOException {
		// TODO Auto-generated method stub
		return Integer.toString(connection.getResponseCode());
	}

	private String getResponse(HttpURLConnection connection) throws IOException {
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer(); 
		while((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		return response.toString();
	}

	private void sendRequest(String request, HttpURLConnection connection) throws IOException {
		DataOutputStream wr = new DataOutputStream (
				connection.getOutputStream ());
		wr.writeBytes (request);
		wr.flush ();
		wr.close ();
	}

	private HttpURLConnection createHttpConnection() throws IOException {
		URL uri = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", 
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Language", "en-US");  
		connection.setUseCaches (false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	private String createXML(String toAddress, String message, Long notificationLogDetailsID) throws ParserConfigurationException, TransformerException  {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("smartfren-h2h");
		doc.appendChild(rootElement);

		// staff elements
		Element staff = doc.createElement("sms");
		rootElement.appendChild(staff);

		// set attribute to staff element
		Attr idAttr = doc.createAttribute("id");
		idAttr.setValue(notificationLogDetailsID.toString());
		staff.setAttributeNode(idAttr);

		Attr uidAttr = doc.createAttribute("uid");
		uidAttr.setValue(getUid());
		staff.setAttributeNode(uidAttr);

		Attr pwdAttr = doc.createAttribute("pwd");
		pwdAttr.setValue(getPwd());
		staff.setAttributeNode(pwdAttr);

		Attr srcAttr = doc.createAttribute("src");
		srcAttr.setValue(toAddress);
		staff.setAttributeNode(srcAttr);

		Attr destAttr = doc.createAttribute("dest");
		destAttr.setValue(getDest());
		staff.setAttributeNode(destAttr);
		
		staff.appendChild(doc.createTextNode(message));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		transformer.transform(source, new StreamResult(writer));
		String output = writer.getBuffer().toString();
		return output;
	}


	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getToAddress() {
		return toAddress;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getIntPrefixCode() {
		return intPrefixCode;
	}

	public void setIntPrefixCode(String intPrefixCode) {
		this.intPrefixCode = intPrefixCode;
	}
	public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("smartfren-h2h");
		doc.appendChild(rootElement);

		// staff elements
		Element staff = doc.createElement("sms");
		rootElement.appendChild(staff);

		// set attribute to staff element
		Attr idAttr = doc.createAttribute("id");
		idAttr.setValue("1234567890");
		staff.setAttributeNode(idAttr);

		Attr uidAttr = doc.createAttribute("uid");
		uidAttr.setValue("test");
		staff.setAttributeNode(uidAttr);

		Attr pwdAttr = doc.createAttribute("pwd");
		pwdAttr.setValue("test");
		staff.setAttributeNode(pwdAttr);

		Attr srcAttr = doc.createAttribute("src");
		srcAttr.setValue("");
		staff.setAttributeNode(srcAttr);

		Attr destAttr = doc.createAttribute("dest");
		destAttr.setValue("3020008");
		staff.setAttributeNode(destAttr);
		String message="Hello <[ ]> ..,,..";
		String encodedmessage = message.replace('<','[');
		encodedmessage = encodedmessage.replace('>', ']');
		encodedmessage = encodedmessage.replace('@', '|');
		encodedmessage = encodedmessage.replace(".",URLEncoder.encode(".","UTF-8"));
		encodedmessage = encodedmessage.replace(",", URLEncoder.encode(",","UTF-8"));
		staff.appendChild(doc.createTextNode(message));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StringWriter writer = new StringWriter();

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, new StreamResult(writer));
		String output = writer.getBuffer().toString();
		System.out.println(output);
		URL url = new URL("https://uangku.smartfren.com/h2h/smartfren-h2h.php");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", 
				"application/x-www-form-urlencoded");

		connection.setRequestProperty("Content-Length", "" + 
				Integer.toString(output.getBytes().length));
		connection.setRequestProperty("Content-Language", "en-US");  

		connection.setUseCaches (false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		//Send request
		DataOutputStream wr = new DataOutputStream (
				connection.getOutputStream ());
		wr.writeBytes (output);
		wr.flush ();
		wr.close ();

		//Get Response	
		
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer(); 
		while((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		System.out.println("response>>> "+response.toString());
		System.out.println("Responscode>>"+connection.getResponseCode());

	}
}
