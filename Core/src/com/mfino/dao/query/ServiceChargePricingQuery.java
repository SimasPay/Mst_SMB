/**
 * 
 */
package com.mfino.dao.query;


/**
 * @author Bala Sunku
 *
 */
public class ServiceChargePricingQuery extends BaseQuery {

	private Long serviceChargeTemplateId;

	public Long getServiceChargeTemplateId() {
		return serviceChargeTemplateId;
	}

	public void setServiceChargeTemplateId(Long serviceChargeTemplateId) {
		this.serviceChargeTemplateId = serviceChargeTemplateId;
	}
}
