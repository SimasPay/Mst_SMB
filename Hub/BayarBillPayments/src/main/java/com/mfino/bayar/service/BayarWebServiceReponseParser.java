package com.mfino.bayar.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Amar
 *
 */
public class BayarWebServiceReponseParser {

	private static Log log = LogFactory.getLog(BayarWebServiceReponseParser.class);

	public static BayarWebServiceResponse getBayarWebServiceResponse(String responseString){
		log.info("BayarWebServiceReponseParser getBayarWebServiceResponse xml="+responseString);
		BayarWebServiceResponse response = new BayarWebServiceResponse();

		//JSONObject jsonResponse = null;
		try {
			JSONObject jsonResponse = new JSONObject(responseString);
			response.setStatus(jsonResponse.getInt("status"));
			response.setMessage(jsonResponse.getString("message"));

			JSONObject data = jsonResponse.getJSONObject("data");

			if(data != null){

				JSONObject customerData = data.getJSONObject("customer_data");
				if(customerData != null){
					response.setBillNo(customerData.getString("bill_number"));
					response.setBillName(customerData.getString("bill_name"));
				}
				
				JSONObject billData = data.getJSONObject("bill_data");
				if(billData != null){
					response.setBillReference(billData.getString("bill_reference"));
					response.setTotalAmount(billData.getInt("total_amount"));
					response.setFee(billData.getInt("fee"));
					response.setLateFee(billData.getInt("late_fee"));
					response.setGrandTotal(billData.getInt("grand_total"));	
				}

				response.setReferenceId(data.getString("reference_id"));
				response.setPaymentCode(data.getString("payment_code"));
				response.setProductCode(data.getString("product_code"));
				response.setDataMesssage(data.getString("message"));
				response.setVoucherToken(data.getString("voucher_token"));
				response.setVoucherNo(data.getString("voucher_number"));
				response.setVoucherDenomination(data.getInt("voucher_denomination"));
				response.setBalanceDeducted(data.getInt("balance_deducted"));	
				response.setCurrentBalance(data.getInt("current_balance"));	
				response.setRemainingCreditLimit(data.getInt("remaining_credit_limit"));	
				response.setTransactionId(data.getInt("transaction_id"));	
			}

		} catch (JSONException e) {
			log.error("BayarWebServiceReponseParser :: getBayarWebServiceResponse() - Error json", e);
			//e.printStackTrace();
		}

		return response;
	}

}
