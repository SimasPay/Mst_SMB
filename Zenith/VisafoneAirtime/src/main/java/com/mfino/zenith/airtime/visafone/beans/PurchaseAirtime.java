package com.mfino.zenith.airtime.visafone.beans;

import java.math.BigDecimal;

public class PurchaseAirtime {
	private String	appId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMerchandID() {
		return MerchandID;
	}

	public void setMerchandID(String merchandID) {
		MerchandID = merchandID;
	}

	public String getAdditionalInfo() {
		return AdditionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		AdditionalInfo = additionalInfo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getProductId() {
		return ProductId;
	}

	public void setProductId(String productId) {
		ProductId = productId;
	}

	public String getQuantity() {
		return Quantity;
	}

	public void setQuantity(String quantity) {
		Quantity = quantity;
	}

	private String	   MerchandID;
	private String	   AdditionalInfo;
	private String	   customerId;
	private String	   Id;
	private String	   ProductId;
	private String	Quantity;

	public String toXML() {
		StringBuffer xmlStringBuffer = new StringBuffer();

		xmlStringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		xmlStringBuffer.append("<nep:addCustomerProductByMerchant>");

		xmlStringBuffer.append("<appid>");
		xmlStringBuffer.append(getAppId());
		xmlStringBuffer.append("<appid>");

		xmlStringBuffer.append("<merchantId>");
		xmlStringBuffer.append(getMerchandID());
		xmlStringBuffer.append("<merchantId>");

		xmlStringBuffer.append("<order>");

		xmlStringBuffer.append("<additionalInfo>");
		xmlStringBuffer.append(getAdditionalInfo());
		xmlStringBuffer.append("<additionalInfo>");

		xmlStringBuffer.append("<customerId>");
		xmlStringBuffer.append(getCustomerId());
		xmlStringBuffer.append("<customerId>");

		xmlStringBuffer.append("<id>");
		xmlStringBuffer.append(getId());
		xmlStringBuffer.append("<id>");

		xmlStringBuffer.append("<item>");

		xmlStringBuffer.append("<productId>");
		xmlStringBuffer.append(getProductId());
		xmlStringBuffer.append("<productId>");

		xmlStringBuffer.append("<quantity>");
		xmlStringBuffer.append(getQuantity());
		xmlStringBuffer.append("<quantity>");

		xmlStringBuffer.append("</item>");

		xmlStringBuffer.append("</order>");

		xmlStringBuffer.append("</nep:addCustomerProductByMerchant>");

		return xmlStringBuffer.toString();
	}

}