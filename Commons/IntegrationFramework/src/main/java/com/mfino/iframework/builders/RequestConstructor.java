package com.mfino.iframework.builders;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mfino.hibernate.Timestamp;
import com.mfino.iframework.DateTimeFormatter;
import com.mfino.iframework.MyFileReader;
import com.mfino.iframework.domain.Field;
import com.mfino.iframework.domain.Integration;
import com.mfino.iframework.domain.Message;
import com.mfino.iframework.domain.MessageType;
import com.mfino.iframework.domain.Parameter;

public class RequestConstructor {

	public static final String	MESSAGE_INQUIRY	= "Inquiry";
	private static Logger	   logger	            = LoggerFactory.getLogger(RequestConstructor.class);

	private Integration	       integrationObject;

	public Integration getIntegrationObject() {
		return integrationObject;
	}

	public void setIntegrationObject(Integration integrationObject) {
		this.integrationObject = integrationObject;
	}

	private Map<String, String> requestTypeTorequestFormatMap;

	public void setRequestTypeTorequestFormatMap(Map<String, String> requestTypeTorequestFormatMap) {
		this.requestTypeTorequestFormatMap = requestTypeTorequestFormatMap;
	}

	/**
	 * Takes the values from the map, and fills up the request xml file. Xpath
	 * is used to get all the leaf nodes , and the values of leaf nodes are
	 * filled with corresponding value
	 * 
	 * More logging and error cases handling needed
	 * 
	 * @param dataHolder
	 * @return
	 */
	public String constructRequest(Map<String, Object> dataHolder, String requestType) {

		try {

			logger.info("constructing request for "+requestType);
			
			String requestFormatFilepath = requestTypeTorequestFormatMap.get(requestType);
			
			MyFileReader fileReader = new MyFileReader();
			fileReader.setFilePath(requestFormatFilepath);
			String requestFormat = fileReader.read();

			StringReader sr = new StringReader(requestFormat);
			InputSource is = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression exp = xpath.compile("//*[not(child::*)]");

			NodeList list = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);
			List<Message> msgList = integrationObject.getMessage();
			Message presentMsg = null;
			for (int i = 0; i < msgList.size(); i++) {
				if (msgList.get(i).getType().equalsIgnoreCase(requestType)) {
					presentMsg = msgList.get(i);
					break;
				}
			}

			MessageType msgType = presentMsg.getRequestOut();
			List<Field> fieldList = msgType.getField();

			List<Parameter> paramList = integrationObject.getParameter();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
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
							String fieldStr = location.substring(1, location.length() - 1);
							if (isFromTxnData(location) || isFromPreviousResponse(location)) {
								node.setTextContent(dataHolder.get(fieldStr).toString());
							}
							else if (isDynamic(location)) {
								node.setTextContent(getDynamicField(fieldStr, dataHolder));
							}
						}
						else {
							node.setTextContent(p.getValue());
						}
						break;
					}
				}
			}

			StringWriter sw = new StringWriter();
			TransformerFactory tfact = TransformerFactory.newInstance();
			Transformer trans = tfact.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(sw));
			String resultXML = sw.toString();

			sr.close();
			sw.close();
			return resultXML;

		}
		catch (Exception ex) {
			logger.error("Exception occured", ex);
			return null;
		}
		finally {

		}

	}

	String getDynamicField(String fieldName, Map<String, Object> dataHolder) {
		String fn = fieldName.toLowerCase();
		if (fn.contains("transactiontime")) {
			fn = DateTimeFormatter.getYYYYMMDDHHMMSS((Timestamp) dataHolder.get("dynamic."+"transactiontime"));
		}
		return fn;

	}

	// public void persistField(String location, TransactionDataHolder holder,
	// Field field, FlowStep flowStep, Long ipmID) {
	//
	// RequestTypeDAO rtdao = DAOFactory.getInstance().getRequestTypeDAO();
	// int dotIndex = location.indexOf('.');
	// String requestTypeStr = location.substring(1, dotIndex);
	// RequestTypeQuery query = new RequestTypeQuery();
	// query.setName(requestTypeStr);
	// List<RequestType> rtList = rtdao.get(query);
	// RequestType requestType = rtList.get(0);
	//
	// InteractionSummary is = new InteractionSummary();
	// is.setFlowStep(flowStep);
	// is.setRequestType(requestType);
	//
	// ServiceChargeTransactionLogDAO sctldao =
	// DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
	// ServiceChargeTransactionLog sctl = sctldao.getById(holder.getSctlID());
	//
	// is.setServiceChargeTransactionLogBySctlId(sctl);
	// is.setIntegrationPartnerMappingByIntegrationID(null);
	//
	// IntegrationPartnerMappingDAO ipmdao =
	// DAOFactory.getInstance().getIntegrationPartnerMappingDAO();
	// IntegrationPartnerMapping ipm = ipmdao.getById(ipmID);
	//
	// is.setIntegrationPartnerMappingByIntegrationID(ipm);
	//
	// InteractionSummaryDAO isdao =
	// DAOFactory.getInstance().getInteractionSummaryDAO();
	// isdao.save(is);
	//
	// }

	private boolean isFromPreviousResponse(String location) {
		if (isDerivedParam(location) && !isFromTxnData(location) && !isDynamic(location))
			return true;
		return false;

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