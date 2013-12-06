/**
 * StartSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package com.ucitech.neptune;

import java.util.ArrayList;
import java.util.List;

/**
 * StartSkeleton java skeleton for the axisService
 */
public class StartSkeleton {

	/**
	 * Auto generated method signature
	 * 
	 * @param addCustomerProductsByMerchant
	 * @return addCustomerProductsByMerchantResponse
	 */

	public com.ucitech.neptune.AddCustomerProductsByMerchantResponseE addCustomerProductsByMerchant(
	        com.ucitech.neptune.AddCustomerProductsByMerchantE addCustomerProductsByMerchant) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#addCustomerProductsByMerchant");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param delCustomerProductByMerchant
	 * @return delCustomerProductByMerchantResponse
	 */

	public com.ucitech.neptune.DelCustomerProductByMerchantResponseE delCustomerProductByMerchant(
	        com.ucitech.neptune.DelCustomerProductByMerchantE delCustomerProductByMerchant) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#delCustomerProductByMerchant");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param queryCustomerByMerchant
	 * @return queryCustomerByMerchantResponse
	 */

	public com.ucitech.neptune.QueryCustomerByMerchantResponseE queryCustomerByMerchant(
	        com.ucitech.neptune.QueryCustomerByMerchantE queryCustomerByMerchant) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#queryCustomerByMerchant");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param delCustomerProductsByMerchant
	 * @return delCustomerProductsByMerchantResponse
	 */

	public com.ucitech.neptune.DelCustomerProductsByMerchantResponseE delCustomerProductsByMerchant(
	        com.ucitech.neptune.DelCustomerProductsByMerchantE delCustomerProductsByMerchant) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#delCustomerProductsByMerchant");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param login
	 * @return loginResponse
	 */

	public com.ucitech.neptune.LoginResponseE login(com.ucitech.neptune.LoginE login) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#login");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param addCustomerProductByMerchant
	 * @return addCustomerProductByMerchantResponse
	 */

	public com.ucitech.neptune.AddCustomerProductByMerchantResponseE addCustomerProductByMerchant(
	        com.ucitech.neptune.AddCustomerProductByMerchantE addCustomerProductByMerchant) {

		AddCustomerProductByMerchantResponseE responseE = new AddCustomerProductByMerchantResponseE();
		AddCustomerProductByMerchantResponse response = new AddCustomerProductByMerchantResponse();

		ProxyResponse pr = new ProxyResponse();
		pr.setDesc("OK");
		pr.setRetn(1000);

		response.set_return(pr);
		responseE.setAddCustomerProductByMerchantResponse(response);

		return responseE;

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param queryMerchants
	 * @return queryMerchantsResponse
	 */

	public com.ucitech.neptune.QueryMerchantsResponseE queryMerchants(com.ucitech.neptune.QueryMerchantsE queryMerchants) {

		QueryMerchantsResponseE responseE = new QueryMerchantsResponseE();
		QueryMerchantsResponse response = new QueryMerchantsResponse();

		List<Object> record = new ArrayList<Object>();
		record.add("<merchantId>visafone</merchantId>" + "<name>Visafone communications limited</name>");
		// record.add("sadfsdsf");

		ProxyResponse pr = new ProxyResponse();
		pr.setDesc("OK");
		pr.setRetn(1000);
		pr.setRecord(record.toArray());

		response.set_return(pr);
		responseE.setQueryMerchantsResponse(response);

		return responseE;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param queryMerchantProducts
	 * @return queryMerchantProductsResponse
	 */

	public com.ucitech.neptune.QueryMerchantProductsResponseE queryMerchantProducts(com.ucitech.neptune.QueryMerchantProductsE queryMerchantProducts) {

		QueryMerchantProductsResponseE responseE = new QueryMerchantProductsResponseE();
		QueryMerchantProductsResponse response = new QueryMerchantProductsResponse();

		List<Object> record = new ArrayList<Object>();
		record.add("<productId>12122</productId>" + "<name>ryhgt</name>" + "<price>123</price");

		ProxyResponse pr = new ProxyResponse();
		pr.setDesc("OK");
		pr.setRetn(1000);
		pr.setRecord(record.toArray());

		response.set_return(pr);
		responseE.setQueryMerchantProductsResponse(response);

		return responseE;

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param queryMerchantTransaction
	 * @return queryMerchantTransactionResponse
	 */

	public com.ucitech.neptune.QueryMerchantTransactionResponseE queryMerchantTransaction(
	        com.ucitech.neptune.QueryMerchantTransactionE queryMerchantTransaction) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#queryMerchantTransaction");
	}

}
