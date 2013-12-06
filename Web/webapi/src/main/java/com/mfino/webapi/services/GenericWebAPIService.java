package com.mfino.webapi.services;

import javax.servlet.http.HttpServletRequest;

import com.mfino.domain.ChannelCode;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

public interface GenericWebAPIService {

	String getDestinationMDNFromAccountNumber(String accountNo);

	XMLResult updateSubscriberDetails(String sourceMDN, XMLResult xmlResult);

	String generateTransactionIdentifier(HttpServletRequest request);

	void activateInactiveSubscriber(TransactionDetails transactionDetails);

	ChannelCode getChannelCode(String channelCode) throws InvalidDataException;


}

