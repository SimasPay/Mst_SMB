package com.mfino.monitor.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Loads external code,description values into codeMap from properties file
 * "externalcodedescriptions.xml". Bean for the class is defined in
 * mcecore_context.xml
 * 
 * @author Srikanth
 * 
 */
public class ExternalResponseCodeHolder {

	static Log log = LogFactory.getLog(ExternalResponseCodeHolder.class);

	private static HashMap<String, ExternalResponsecode> codeMap = new HashMap<String, ExternalResponsecode>();
	private static ExternalResponseCodeHolder externalResponseCodeHolder = null;

	private ExternalResponseCodeHolder() {}
	
	public static ExternalResponseCodeHolder getInstance() {
		
		if(externalResponseCodeHolder == null) {
			
			externalResponseCodeHolder = new ExternalResponseCodeHolder();
			externalResponseCodeHolder.loadExternalCodeMappings("");
		}
		
		return externalResponseCodeHolder;
	}
	
	private void loadExternalCodeMappings(String filePath) {
		ApplicationContext ctx = new FileSystemXmlApplicationContext();
		//Resource res = ctx.getResource(filePath);
		//Resource res = ctx.getResource("D:/simobi/simservers/tomcat-6.0.37/apache-tomcat-6.0.37/mfino_conf/externalcodedescriptions.xml");
		//Resource res = ctx.getResource("/TransactionMonitorTool/src/java/com/mfino/monitor/util/externalcodedescriptions.xml");	
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    URL url = loader.getResource("..\\externalcodedescriptions.xml");

		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			//File file = res.getFile();
			File file = new File(url.toURI());

			if (file.exists()) {
				Document doc = db.parse(file);
				Element docEle = doc.getDocumentElement();
				NodeList responseCodeList = docEle
						.getElementsByTagName("response-code");
				if (responseCodeList != null
						&& responseCodeList.getLength() > 0) {
					for (int i = 0; i < responseCodeList.getLength(); i++) {

						Node node = responseCodeList.item(i);
						ExternalResponsecode externalResponsecode = new ExternalResponsecode();
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) node;
							NodeList nodeList = e.getElementsByTagName("code");
							externalResponsecode.setCode(nodeList.item(0)
									.getChildNodes().item(0).getNodeValue());
							nodeList = e.getElementsByTagName("description");
							externalResponsecode.setDescription(nodeList
									.item(0).getChildNodes().item(0)
									.getNodeValue());
							nodeList = e
									.getElementsByTagName("notification-text");
							externalResponsecode.setNotificationText(nodeList
									.item(0).getChildNodes().item(0)
									.getNodeValue());
						}
						codeMap.put(externalResponsecode.getCode(),
								externalResponsecode);
					}
				} else {
				}
			}

		} catch (Exception e) {
			e.fillInStackTrace();
		}
	}

	public String getNotificationText(String key) {
		ExternalResponsecode externalResponsecode = (ExternalResponsecode) codeMap.get(key);
		if (externalResponsecode == null) {
			return "No Description Available";
		} else {
			
			return externalResponsecode.getNotificationText();
		}
	}
	
	public String getDescription(String key) {
		ExternalResponsecode externalResponsecode = (ExternalResponsecode) codeMap.get(key);
		if (externalResponsecode == null) {
			return "No Description Available";
		} else {
			
			return externalResponsecode.getDescription();
		}
	}

}
