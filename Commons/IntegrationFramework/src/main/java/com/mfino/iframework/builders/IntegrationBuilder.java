package com.mfino.iframework.builders;

import java.io.StringReader;
import java.net.Authenticator.RequestorType;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mfino.iframework.MyFileReader;
import com.mfino.iframework.domain.Field;
import com.mfino.iframework.domain.Integration;
import com.mfino.iframework.domain.Message;
import com.mfino.iframework.domain.MessageType;
import com.mfino.iframework.domain.Parameter;

public class IntegrationBuilder {

	private static Integration	integration;

	public Integration getIntegration() {
		if (integration == null)
			integration = buildIntegration(null);
		return integration;
	}

	public static Integration buildIntegration(String variantFilepath) {

		integration = new Integration();

		MyFileReader fReader = new MyFileReader();
		fReader.setFilePath(variantFilepath);

		StringReader sr = new StringReader(fReader.read());

		try {

			InputSource is = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression parameterXPath = xpath.compile("integration/parameter");

			NodeList list = (NodeList) parameterXPath.evaluate(doc, XPathConstants.NODESET);

			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				Parameter p = new Parameter();

				String name = n.getAttributes().getNamedItem("name").getNodeValue();
				p.setName(name);
				NodeList nl = n.getChildNodes();
				for (int j = 0; j < nl.getLength(); j++) {
					Node childNode = nl.item(j);
					if (childNode.getNodeName().contains("value")) {
						p.setValue(childNode.getTextContent());
						break;
					}
				}
				String isPers = n.getAttributes().getNamedItem("isPersistent").getNodeValue();
				p.setIsPersistent(Boolean.parseBoolean(isPers));

				String isSecure = n.getAttributes().getNamedItem("isSecure").getNodeValue();
				p.setIsSecure(Boolean.parseBoolean(isSecure));

				integration.getParameter().add(p);
			}

			XPathExpression msgXPath = xpath.compile("integration/message");
			NodeList xmlMsgList = (NodeList) msgXPath.evaluate(doc, XPathConstants.NODESET);
			List<Message> msgList = new ArrayList<Message>(xmlMsgList.getLength());
			for (int i = 0; i < xmlMsgList.getLength(); i++) {
				Node n = xmlMsgList.item(i);
				Message msg = new Message();
				String type = n.getAttributes().getNamedItem("type").getNodeValue();
				msg.setType(type);

				NodeList rrList = n.getChildNodes();
				for (int j = 0; j < rrList.getLength(); j++) {
					Node rrItem = rrList.item(j);

					if (!rrItem.getNodeName().contains("r"))
						continue;

					MessageType rrMsg = new MessageType();
					rrMsg.setName(rrItem.getNodeName());
					NodeList fieldsList = rrItem.getChildNodes();
					rrMsg.getField().addAll(getFields(fieldsList));
					if (rrMsg.getName().contains("request"))
						msg.setRequestOut(rrMsg);
					else
						msg.setResponseIn(rrMsg);
				}

				msgList.add(msg);

			}
			integration.getMessage().addAll(msgList);

		}
		catch (Exception ex) {
		}
		finally {
			sr.close();
		}

		return integration;

	}

	private static List<Field> getFields(NodeList nodeList) {

		List<Field> list = new ArrayList<Field>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node prst = nodeList.item(i);
			if (!prst.getNodeName().contains("f"))
				continue;

			Field field = new Field();
			String identifier = prst.getAttributes().getNamedItem("identifier").getNodeValue();
			field.setIdentifier(identifier);
			String param = prst.getAttributes().getNamedItem("param").getNodeValue();
			field.setParam(param.substring(1, param.length() - 1));

			list.add(field);
		}

		return list;
	}

	public static void main(String[] args) {

		String filePath = "A:\\servicemix70297\\mfino_conf\\Variant.xml";

		Integration integration = buildIntegration(filePath);
		int i = 10;

	}

}