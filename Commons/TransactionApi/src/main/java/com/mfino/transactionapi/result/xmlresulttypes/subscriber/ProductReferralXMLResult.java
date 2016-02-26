/**
 * 
 */
package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

/**
 * @author Harihara
 *
 */
public class ProductReferralXMLResult extends XMLResult {

	private String agentMDN;

	private String fullName;

	/**
	 * @return the agentMDN
	 */
	public String getAgentMDN() {
		return agentMDN;
	}

	/**
	 * @param agentMDN
	 *            the agentMDN to set
	 */
	public void setAgentMDN(String agentMDN) {
		this.agentMDN = agentMDN;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the subscriberMDN
	 */
	public String getSubscriberMDN() {
		return subscriberMDN;
	}

	/**
	 * @param subscriberMDN
	 *            the subscriberMDN to set
	 */
	public void setSubscriberMDN(String subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the productDesired
	 */
	public String getProductDesired() {
		return productDesired;
	}

	/**
	 * @param productDesired
	 *            the productDesired to set
	 */
	public void setProductDesired(String productDesired) {
		this.productDesired = productDesired;
	}

	/**
	 * @return the others
	 */
	public String getOthers() {
		return others;
	}

	/**
	 * @param others
	 *            the others to set
	 */
	public void setOthers(String others) {
		this.others = others;
	}

	private String subscriberMDN;
	private String email;
	private String productDesired;
	private String others;

	public ProductReferralXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();
		if (StringUtils.isNotBlank(getTransID())) {
			getXmlWriter().writeStartElement("transID");
			getXmlWriter().writeCharacters(getTransID(), false);
			getXmlWriter().writeEndElement();
		}
		writeEndOfDocument();

	}

}
