/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.web.TextView;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.HibernateUtil;

/**
 * 
 * @author Venkata Krishna Teja D
 */
@Controller
public class TreeController {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@RequestMapping("/tree.htm")
	public View treeView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Transaction transaction = null;
		try {

			WebContextError.clear();
			transaction = HibernateUtil.getCurrentSession().beginTransaction();
			BufferedReader reader = request.getReader();
			String line = reader.readLine();

			StringBuffer sb = new StringBuffer();
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
			String completePostString = sb.toString();

			if (null == completePostString || 0 == completePostString.length()) {
				log.error("Failed to decode fix message");
				CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
				// TODO : choose more meaningful error and get the description from enum
				// text table
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				errorMsg.setErrorDescription(MessageText._("Bad message received by server."));
				return new TextView(errorMsg.getErrorDescription());
			}

			String merchantId = null;
			String search = null;
			String node = null;
			String[] requestParameters = completePostString.split("&");
			for (int i = 0; i < requestParameters.length; i++) {
				String eachElement = requestParameters[i];
				int indexOfEquals = eachElement.indexOf("=");
				int elementLength = eachElement.length();

				String key = eachElement.substring(0, indexOfEquals);
				String value = eachElement.substring(indexOfEquals + 1, elementLength);
				if (key.equals("search")) {
					search = value;
				} else if (key.equalsIgnoreCase("merchantid")) {
					merchantId = value;
				} else if (key.equalsIgnoreCase("node")) {
					node = value;
				}
			}

			// Added all the Null Checks for the Got Input String from the Post
			// message.
			if (null == merchantId || 0 == merchantId.length()) {
				// Send an Error Msg back.
				String errorMsg = "Error : Invalid Data: The Merchant Id sent from tree is null or empty.";
				return new TextView(errorMsg);
			}

			if (null == search || 0 == search.length()) {
				// Send an Error Msg back.
				String errorMsg = "Error : Invalid Data: The Search Criteria sent from tree is null or empty.";
				return new TextView(errorMsg);
			}

			if (null == node || 0 == node.length()) {
				// Send an Error Msg Back.
				String errorMsg = "Error : Invalid Data: The Node Id sent from tree is null or empty.";
				return new TextView(errorMsg);
			}

			Long merchantIdLong = null;
			Integer searchInt = null;
			Long nodeId = null;
			try {
				merchantIdLong = new Long(Long.valueOf(merchantId));
				searchInt = new Integer(Integer.valueOf(search));
				nodeId = new Long(Long.valueOf(node));
			} catch (NumberFormatException numFormatExp) {
				// If we reach here then we got some Invalid data.
				// Send an Error Msg Back.
				String errorMsg = "Error : Invalid Data sent from tree.";
				log.error(errorMsg, numFormatExp);
				return new TextView(errorMsg);
			}

			CmFinoFIX.CMJSMerchantTree message = new CmFinoFIX.CMJSMerchantTree();
			message.setTreeSearch(searchInt);
			message.setIDSearch(merchantIdLong);
			message.setNodeIDSearch(nodeId);

			/*MerchantTreeProcessor merchantTreeProcessor = new MerchantTreeProcessor();
			CmFinoFIX.CMJSMerchantTree returnMsg = (CmFinoFIX.CMJSMerchantTree) merchantTreeProcessor.process(message);*/
			CmFinoFIX.CMJSMerchantTree returnMsg = null;
			CmFinoFIX.CMJSMerchantTree.CGEntries[] entries = returnMsg.getEntries();

			if (null == entries || 0 == entries.length) {
				return new TextView("INFO : No Records Found for this parent.");
			}

			JSONObject parentSearchObj = new JSONObject();
			JSONArray childSearchArray = new JSONArray();
			for (int i = 0; i < entries.length; i++) {
				CmFinoFIX.CMJSMerchantTree.CGEntries eachEntry = entries[i];

				if (null == eachEntry.getID()) {
					log.error("Found a merchant with Null ID <" + eachEntry.toString() + ">");
					continue;
				}

				if (StringUtils.isBlank(eachEntry.getText())) {
					log.error("Found a merchant with NO DISPLAY NAME <" + eachEntry.toString() + ">");
					continue;
				}

				if (0 == message.getTreeSearch()) {
					JSONObject thisNode = new JSONObject();
					thisNode.put("id", String.valueOf(eachEntry.getID().longValue()));
					thisNode.put("text", eachEntry.getText());

					if ((eachEntry.getSubscriberStatus().intValue() == CmFinoFIX.MDNStatus_Active) && (eachEntry.getSubscriberRestrictions().intValue() == CmFinoFIX.SubscriberRestrictions_None)) {
						thisNode.put("icon", "resources/images/customer_green.png");
					} else if ((eachEntry.getSubscriberStatus().intValue() == CmFinoFIX.MDNStatus_Active) && (eachEntry.getSubscriberRestrictions().intValue() > CmFinoFIX.SubscriberRestrictions_None)) {
						thisNode.put("icon", "resources/images/customer_orange.png");
					} else if (eachEntry.getSubscriberStatus().intValue() > CmFinoFIX.MDNStatus_Active) {
						thisNode.put("icon", "resources/images/customer_red.png");
					} else if (CmFinoFIX.MDNStatus_Active == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_green.png");
					} else if (CmFinoFIX.MDNStatus_Initialized == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_green.png");
					} else if (CmFinoFIX.MDNStatus_PendingRetirement == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_orange.png");
					} else if (CmFinoFIX.MDNStatus_Retired == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_red.png");
					} else {
						thisNode.put("icon", "resources/images/customer.png");
					}

					if (0 == i) {
						parentSearchObj = thisNode;
						continue;
					}

					thisNode.put("expanded", true);
					String children = "[" + parentSearchObj.toString() + "]";
					thisNode.put("children", children);
					parentSearchObj = thisNode;
				} else {
					JSONObject thisNode = new JSONObject();
					thisNode.put("id", eachEntry.getID());
					thisNode.put("text", eachEntry.getText());
					if ((eachEntry.getSubscriberStatus().intValue() == CmFinoFIX.MDNStatus_Active) && (eachEntry.getSubscriberRestrictions().intValue() == CmFinoFIX.SubscriberRestrictions_None)) {
						thisNode.put("icon", "resources/images/customer_green.png");
					} else if ((eachEntry.getSubscriberStatus().intValue() == CmFinoFIX.MDNStatus_Active) && (eachEntry.getSubscriberRestrictions().intValue() > CmFinoFIX.SubscriberRestrictions_None)) {
						thisNode.put("icon", "resources/images/customer_orange.png");
					} else if (eachEntry.getSubscriberStatus().intValue() > CmFinoFIX.MDNStatus_Active) {
						thisNode.put("icon", "resources/images/customer_red.png");
					} else if (CmFinoFIX.MDNStatus_Active == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_green.png");
					} else if (CmFinoFIX.MDNStatus_Initialized == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_green.png");
					} else if (CmFinoFIX.MDNStatus_PendingRetirement == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_orange.png");
					} else if (CmFinoFIX.MDNStatus_Retired == eachEntry.getSubscriberStatus().intValue()) {
						thisNode.put("icon", "resources/images/customer_red.png");
					} else {
						thisNode.put("icon", "resources/images/customer.png");
					}
					//childSearchArray.add(thisNode);
				}
			}
			transaction.rollback();

			if (0 == message.getTreeSearch()) {
				JSONArray array = new JSONArray();
				//array.add(parentSearchObj.toString());
				return new TextView(array.toString());
			} else {
				return new TextView(childSearchArray.toString());
			}
		} catch (Throwable throwable) {
			if(transaction!=null && transaction.isActive()){
				transaction.rollback();
			}

			if (WebContextError.isEmpty()) {
				log.error("Error in Tree controller", throwable);
				CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				errorMsg.setErrorDescription(MessageText._("Server error: ") + throwable.toString());
				return new TextView("ERROR : " + throwable.toString());
			} else {
				return new TextView("ERROR : " + WebContextError.getError().getErrorDescription());
			}
		} finally{
			if(transaction!=null && transaction.isActive()){
				transaction.rollback();
			}
		}
	}
}
