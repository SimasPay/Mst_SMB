package com.mfino.zenith.billpay.mfinows.communicators;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tempuri.ProcessRequestResponse;

import com.mfino.zenith.billpay.mfinows.MfinoWSConstants;
/**
 * @author Satya
 * 
 */
public class MfinoWSResponseParser {

	private static Logger	log	= LoggerFactory.getLogger(MfinoWSResponseParser.class);

	public static MfinoWSResponse parse(List<Object> wsResponse) throws Exception {
		MfinoWSResponse response = new MfinoWSResponse();

		try {

			if (wsResponse.get(0) instanceof ProcessRequestResponse) {
				ProcessRequestResponse pr = (ProcessRequestResponse) wsResponse.get(0);
				response.retn = pr.getProcessRequestResult();
				if(response.retn.contains(MfinoWSConstants.OPERATION_SUCCESS)){
					response.retn=MfinoWSConstants.OPERATION_SUCCESS;
					response.desc=MfinoWSConstants.OPERATION_SUCCESS_DESCRIPTION;
				}else{
					response.retn=MfinoWSConstants.OPERATION_FAILURE;
					response.desc=MfinoWSConstants.OPERATION_FAILURE_DESCRIPTION;
				}
				
				log.info("reponse from the mfino webservice --->");
				
				log.info("mfinoWS response retn -->"+pr.getProcessRequestResult());
			}
			else if(wsResponse.get(0) instanceof String){
				response.retn = (String) wsResponse.get(0);
				if(response.retn.contains(MfinoWSConstants.OPERATION_SUCCESS)){
					response.retn=MfinoWSConstants.OPERATION_SUCCESS;
					response.desc=MfinoWSConstants.OPERATION_SUCCESS_DESCRIPTION;
				}else{
					response.retn=MfinoWSConstants.OPERATION_FAILURE;
					response.desc=MfinoWSConstants.OPERATION_FAILURE_DESCRIPTION;
				}
				log.info("reponse from the mfino webservice --->");
				
				log.info("mfinoWS response retn -->"+(String) wsResponse.get(0));
			}
			else {
				log.error("response is not an instance of ProcessRequestResponse");
				throw new Exception("response is not an instance of ProcessRequestResponse");
			}

		}
		catch (Exception e) {
			log.error("MfinoWSResponseParser.parse --> Error parsing XML", e);
			e.fillInStackTrace();
			throw e;
		}

		return response;

	}

}
