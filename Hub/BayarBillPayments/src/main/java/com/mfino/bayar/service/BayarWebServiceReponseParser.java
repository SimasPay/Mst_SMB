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
		log.info("BayarWebServiceReponseParser getBayarWebServiceResponse responseString="+responseString);
		BayarWebServiceResponse response = new BayarWebServiceResponse();

		//JSONObject jsonResponse = null;
		try {
			JSONObject jsonResponse = new JSONObject(responseString);
			if(jsonResponse.has("status")){
				response.setStatus(jsonResponse.getInt("status"));
			}
			if(jsonResponse.has("message")){
				response.setMessage(jsonResponse.getString("message"));
			}

			if(jsonResponse.has("data"))
			{
				JSONObject data = jsonResponse.getJSONObject("data");

				if(data != null){
					if(data.has("customer_data")){

						JSONObject customerData = data.getJSONObject("customer_data");
						if(customerData != null){
							if(customerData.has("bill_number")){
								response.setBillNo(customerData.getString("bill_number"));
							}
							if(customerData.has("bill_name")){
								response.setBillName(customerData.getString("bill_name"));
							}
						}
					}

					if(data.has("bill_data")){

						JSONObject billData = data.getJSONObject("bill_data");
						if(billData != null){
							if(billData.has("bill_reference")){
								response.setBillReference(billData.getString("bill_reference"));
							}
							if(billData.has("total_amount")){
								response.setTotalAmount(billData.getInt("total_amount"));
							}
							if(billData.has("fee")){
								response.setFee(billData.getInt("fee"));
								if(response.getFee() == 0)
									response.setFee(new Integer(billData.getString("fee")));
							}
							if(billData.has("late_fee")){
								response.setLateFee(billData.getInt("late_fee"));
							}
							if(billData.has("grand_total")){
								response.setGrandTotal(billData.getInt("grand_total"));	
							}
							
							if(billData.has("total_bill")){
								response.setTotalBill(billData.getInt("total_bill"));
							}
							if(billData.has("bills")){
								JSONObject bills = billData.getJSONArray("bills").getJSONObject(response.getTotalBill() - 1);
								if(bills != null){
									if(bills.has("bill_info")){
										response.setBillInfo(bills.getString("bill_info"));
									}
								}
							}
						}
					}

					if(data.has("reference_id")){
						response.setReferenceId(data.getString("reference_id"));
					}
					if(data.has("payment_code")){
						response.setPaymentCode(data.getString("payment_code"));
					}
					if(data.has("product_code")){
						response.setProductCode(data.getString("product_code"));
					}
					if(data.has("message")){
						response.setDataMesssage(data.getString("message"));
					}
					if(data.has("voucher_token")){
						response.setVoucherToken(data.getString("voucher_token"));
					}
					if(data.has("voucher_number")){
						response.setVoucherNo(data.getString("voucher_number"));
					}
					if(data.has("voucher_denomination")){
						response.setVoucherDenomination(data.getInt("voucher_denomination"));
					}
					if(data.has("balance_deducted")){
						response.setBalanceDeducted(data.getInt("balance_deducted"));	
					}
					if(data.has("current_balance")){
						response.setCurrentBalance(data.getInt("current_balance"));	
					}
					if(data.has("remaining_credit_limit")){
						response.setRemainingCreditLimit(data.getInt("remaining_credit_limit"));
					}
					if(data.has("transaction_id")){
						response.setTransactionId(data.getInt("transaction_id"));	
					}
					if(data.has("transaction_status")){
						response.setTrxnStatus(data.getString("transaction_status"));
					}
					if(data.has("transaction_date")){
						response.setTrxnDate(data.getString("transaction_date"));
					}
				}
			}

		} catch (JSONException e) {
			log.error("BayarWebServiceReponseParser :: getBayarWebServiceResponse() - Error json", e);
			//e.printStackTrace();
		}

		return response;
	}

}
