package com.mfino.webapi.services;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.mfino.integrations.vo.IntegrationDetails;
import com.mfino.transactionapi.vo.TransactionDetails;

public interface RequestValidationService {

	/**
	 * 
	 * @param integrationDetails
	 * @param writer
	 * @return
	 */
	boolean validateIntegration(IntegrationDetails integrationDetails, ServletOutputStream writer);

	/**
	 * 
	 * @param request
	 * @param writer
	 * @return
	 */
	boolean validateRequest(HttpServletRequest request,ServletOutputStream writer);
	/**
	 * 
	 * @param integrationDetails
	 * @return
	 */
	boolean isLoginEnabledForIntegration(IntegrationDetails integrationDetails);

	/**
	 * Validates channel and actor(subscriber, agent, partner) details and allows transaction if and only if, for a given channel that specific actor has permissions to do so.
	 * @param transactionDetails
	 * @return
	 */
	boolean validateTransaction(TransactionDetails transactionDetails);

}
