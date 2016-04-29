/**
 * 
 */
package com.mfino.dao.query;

/**
 * @author Sunil
 *
 */
public class KtpDetailsQuery extends BaseQuery {
	
	private String mdn;
	private String order;

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	
	

}
