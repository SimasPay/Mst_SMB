package com.mfino.billpay.startimes.communicator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpay.startimes.util.StarTimesWSConstants;
import com.star.sms.haiwai.service.CustomerPay2Response;
import com.star.sms.haiwai.service.QueryBalanceResponse;
import com.star.sms.model.haiwai.CustomerPayResult2;
import com.star.sms.service.model.BalanceInfo;

public class StarTimesResponseParser {
	private static Logger	log	= LoggerFactory.getLogger(StarTimesResponseParser.class);

	public static StarTimesResponse parse(List<Object> wsResponse) throws Exception{
		StarTimesResponse response = new StarTimesResponse();
		try{
			if(wsResponse.get(0) instanceof CustomerPayResult2) {
				CustomerPayResult2 resp = (CustomerPayResult2) wsResponse.get(0);
				//implement the response processing logic
				if(resp.getReturnCode().getValue().equals(StarTimesWSConstants.OPERATION_SUCCESS)){
					response.retn = StarTimesWSConstants.OPERATION_SUCCESS;
					response.desc = StarTimesWSConstants.PAYMENT_OPERATION_SUCCESS_DESCRIPTION;
					response.orderCode = resp.getOrderCode().getValue();
				}
				else{
					response.retn=StarTimesWSConstants.OPERATION_FAILURE;
					response.desc=StarTimesWSConstants.OPERATION_FAILURE_DESCRIPTION;
					response.orderCode = resp.getOrderCode().getValue();
				}
				
				if(resp != null){
					log.info("StarTimesWS response retn Code --> "+resp.getReturnCode().getValue());
					log.info("StarTimesWS response retn Message --> "+resp.getReturnMsg().getValue());
				}
			}
			else if(wsResponse.get(0) instanceof BalanceInfo){
				BalanceInfo resp = (BalanceInfo) wsResponse.get(0);
				if(resp.getReturnCode().getValue().equals(StarTimesWSConstants.OPERATION_SUCCESS)){
					response.retn = StarTimesWSConstants.OPERATION_SUCCESS;
					response.desc = StarTimesWSConstants.QUERY_OPERATION_SUCCESS_DESCRIPTION;
					response.balance = resp.getBalance().getValue().toString();
					response.billAmount = resp.getBillAmount().getValue().toString();
				}
				else{
					response.retn=StarTimesWSConstants.OPERATION_FAILURE;
					response.desc=StarTimesWSConstants.OPERATION_FAILURE_DESCRIPTION;
				}

				log.info("reponse from the StarTimes webservice() --->");

				if(resp != null){
					log.info("StarTimesWS response retn Code --> "+resp.getReturnCode().getValue());
					log.info("StarTimesWS response retn Message --> "+resp.getReturnMsg().getValue());					
				}
			}
			else {
				log.error("response is neither instance of CustomerPay2Response nor queryBalanceResponse");
				throw new Exception("response is not an instance of CustomerPay2Response or queryBalanceResponse");
			}
		}
		catch (Exception e) {
			log.error("StarTimesResponseParser.parse --> Error parsing XML", e);
			e.fillInStackTrace();
			throw e;
		}
		return response;
	}
}
