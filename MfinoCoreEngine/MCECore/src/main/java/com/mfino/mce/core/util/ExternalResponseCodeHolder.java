package com.mfino.mce.core.util;

import java.io.File;
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

	public static void loadExternalCodeMappings(String filePath) {
		log.info("Begin ExternalResponseCodeHolder:loadExternalCodeMappings method");
		ApplicationContext ctx = new FileSystemXmlApplicationContext();
		Resource res = ctx.getResource(filePath);
		log.info("External response code mappings list:");
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			File file = res.getFile();
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
						log.info(externalResponsecode);
					}
				} else {
					log.info("No external response codes loaded");
				}
			}

		} catch (Exception e) {
			log.error(e);
		}
	}

	public static String getNotificationText(String key) {
		ExternalResponsecode externalResponsecode = (ExternalResponsecode) codeMap
				.get(key);
		if (externalResponsecode == null) {
			return "";
		} else {
			return externalResponsecode.getNotificationText();
		}
	}

}
