package com.mfino.iframework.builders;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mfino.iframework.domain.*;
import com.mfino.iframework.exceptions.ResponseParseException;

public class ResponseParser {

	private static Logger	log	= LoggerFactory.getLogger(ResponseParser.class);

	Integration	          integrationObject;

	public Integration getIntegrationObject() {
		return integrationObject;
	}

	public void setIntegrationObject(Integration integrationObject) {
		this.integrationObject = integrationObject;
	}

	/**
	 * Parses the xml response and adds response values to the map
	 * 
	 * @param xmlResponse
	 * @return
	 */
	public Map<String, String> parseResponse(String xmlResponse, String requestType) throws ResponseParseException{

		StringReader sr = new StringReader(xmlResponse);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Map<String, String> variantToResponseMap = new HashMap<String, String>();
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression exp = xpath.compile("//*[not(child::*)]");

			NodeList leafNodeList = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);
			List<Message> msgList = integrationObject.getMessage();
			Message presentMsg = null;
			for (int i = 0; i < msgList.size(); i++) {
				if (msgList.get(i).getType().equalsIgnoreCase(requestType)) {
					presentMsg = msgList.get(i);
					break;
				}
			}

			MessageType msgType = presentMsg.getResponseIn();
			List<Field> fieldList = msgType.getField();

			List<Parameter> paramList = integrationObject.getParameter();
			for (int i = 0; i < leafNodeList.getLength(); i++) {
				Node node = leafNodeList.item(i);
				String nodeName = node.getNodeName();
				Field correspondingField = null;
				for (int j = 0; j < fieldList.size(); j++) {
					Field curField = fieldList.get(j);
					if (curField.getIdentifier().equalsIgnoreCase(nodeName)) {
						correspondingField = curField;
						break;
					}
				}
				for (int j = 0; j < paramList.size(); j++) {
					Parameter p = paramList.get(j);
					if (correspondingField.getParam().equalsIgnoreCase(p.getName())) {
						String location = paramList.get(j).getValue().trim().toLowerCase();
						if (isDerivedParam(location)) {
							String fieldName = location.substring(1, location.length() - 1);
							if (isFromPreviousResponse(presentMsg, location)) {
								String result = node.getTextContent().trim();
								if(StringUtils.isBlank(result)){
									throw new Exception("field "+fieldName+" does not have a valid value");
								}
								variantToResponseMap.put(fieldName, node.getTextContent());
							}
						}
						else {
						}
						break;
					}
				}
			}

		}
		catch (Exception ex) {
			log.error(requestType+" failed.May need to try for reversal",ex);
			ResponseParseException rpe = new ResponseParseException(ex.getMessage());
			rpe.initCause(ex);
			throw rpe;
		}

		return variantToResponseMap;

	}

	// public void persistResponse(Map<String, String> map, FlowStep flowStep) {
	//
	// String location = "";
	//
	// RequestTypeDAO rtdao = DAOFactory.getInstance().getRequestTypeDAO();
	// int dotIndex = location.indexOf('.');
	// String requestTypeStr = location.substring(1, dotIndex);
	// RequestTypeQuery query = new RequestTypeQuery();
	// query.setName(requestTypeStr);
	// List<RequestType> rtList = rtdao.get(query);
	// RequestType requestType = rtList.get(0);
	//
	// Long sctlId = getSctlID(map);
	//
	// InteractionSummaryDAO isdao =
	// DAOFactory.getInstance().getInteractionSummaryDAO();
	// InteractionSummaryQuery isq = new InteractionSummaryQuery();
	// isq.setSctlID(sctlId);
	// isq.setFlowStepID(flowStep.getID());
	// isq.setRequestTypeID(requestType.getID());
	//
	// List<InteractionSummary> isList = isdao.get(isq);
	// InteractionSummary isummary = isList.get(0);
	//
	// for (Entry<String, String> entry : map.entrySet()) {
	// InteractionAdditionalData iad = new InteractionAdditionalData();
	// iad.setInteractionSummary(isummary);
	// String loc = entry.getKey();
	// iad.setMappedKey(loc.substring(1, loc.length() - 1));
	// iad.setMappedValue(entry.getValue());
	// }
	//
	// }
	//
	// private RequestType getRequestType(Map<String, String> map) {
	//
	// for (Entry<String, String> entry : map.entrySet()) {
	// String location = entry.getKey();
	// RequestTypeDAO rtdao = DAOFactory.getInstance().getRequestTypeDAO();
	// int dotIndex = location.indexOf('.');
	// String requestTypeStr = location.substring(1, dotIndex);
	// RequestTypeQuery query = new RequestTypeQuery();
	// query.setName(requestTypeStr);
	// List<RequestType> rtList = rtdao.get(query);
	// if (rtList != null && rtList.size() > 0)
	// return rtList.get(0);
	// }
	// return null;
	//
	// }

	private Long getSctlID(Map<String, String> map) {
		for (Entry<String, String> entry : map.entrySet()) {
			String location = entry.getKey();
			location = location.toLowerCase();
			if (entry.getKey().contains("transactionid") || entry.getKey().contains("sctlid")) {
				Long l = Long.valueOf(entry.getValue());
				return l;
			}
		}
		return -1l;

	}

	private boolean isFromPreviousResponse(Message msg, String location) {
		int dotIndex = location.indexOf('.');
		String belongsTo = location.substring(1, dotIndex);
		return msg.getType().equalsIgnoreCase(belongsTo);

	}

	boolean isStandardIntegrationData(String location) {
		return location.startsWith("{integration");
	}

	boolean isDerivedParam(String location) {
		return location.startsWith("{") && location.endsWith("}");
	}

	boolean isFromTxnData(String location) {
		return location.startsWith("{txn");
	}

	boolean isDynamic(String location) {
		return isDerivedParam(location) && location.startsWith("dynamic", 1);
	}

}