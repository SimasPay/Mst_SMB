package com.mfino.iso8583.processor.bankchannel.isotofix;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankChannelPaymentRequest;
import com.mfino.iso8583.processor.bankchannel.IArtajasaRequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IMobile8RequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IXLinkReqeustProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class Payment extends BankChannelRequestProcessor implements IArtajasaRequestProcessor, IMobile8RequestProcessor, IXLinkReqeustProcessor {

	private static Logger log = LoggerFactory.getLogger(Payment.class);

	@Override
	public CFIXMsg process(ArtajasaISOMessage isoMsg) throws Exception {
		CMBankChannelPaymentRequest request = new CMBankChannelPaymentRequest();
		request.setSourceMDN(null);//FIXME default source mdn for postpaid
		request.setBillReferenceNumber(Long.parseLong(isoMsg.getBillingProvidertData().substring(17, 28)));
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));

		if (request.getAmount().longValue() > 0) {
			return forwardArtajasaBankChannelRequest(isoMsg, request);
		}
		else
			//FIXME forward response isoMsg 13

			return null;
	}

	
	@Override
	public CFIXMsg process(Mobile8ISOMessage isoMsg) throws Exception {
		CMBankChannelPaymentRequest request = new CMBankChannelPaymentRequest();
		request.setSourceMDN(null);//FIXME default source mdn for postpaid

		request.setBillReferenceNumber(Long.parseLong(isoMsg.getBillingProvidertData().substring(14, 30)));
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		//if Mobile-8 read the amount from element - 48
		String amount = isoMsg.getBillingProvidertData().substring(60, 72);
		request.setAmount(new BigDecimal(amount));

		if (request.getAmount().compareTo(BigDecimal.ZERO) > 1) {
			return forwardMobile8BankChannelRequest(isoMsg, request);
		}

		return null;
	}

	
	@Override
	public CFIXMsg process(XLinkISOMessage isoMsg) throws Exception {
		CMBankChannelPaymentRequest request = new CMBankChannelPaymentRequest();
		request.setSourceMDN(null);//FIXME default source mdn for postpaid

		if (isoMsg.getXLinkPrivatRequestData().length() == 292) {//total size of element 61
			String details = isoMsg.getXLinkPrivatRequestData();
			String bill4RefNo = details.substring(190, 206);//16+16+1+1+3(16+12+12+12)+(16+12+12+12)+40+8+2
			String bill4Amount = details.substring(206, 218);
			request.setBillReferenceNumber(Long.parseLong(bill4RefNo.trim()));
			request.setAmount(new BigDecimal(bill4Amount.trim()));

			if (!request.getAmount().toString().equals(isoMsg.getTransactionAmount())) {
				//FIXME forward response 30
				return null;
			}
			
			if(request.getAmount().compareTo(BigDecimal.ZERO)>1)
				return forwardXLinkBankChannelRequest(isoMsg, request);
			else
				return null;//FIXME forwardresoinse 13
		}

		return null;
	}

	
	
	//C++ definition of DE61 for XLINK
	//defined here just for info
	private class XLinkPaymentDetails {

		private XLinkPaymentDetails() {
			String.format(null, null);
		}
		char[]	msisdn		= new char[16];
		char[]	customerID	= new char[16];
		char	noOfBills;
		char	noOfBillPaid;
		private class Bill {
			char[]	referenceNo	= new char[16];
			char[]	amount		= new char[12];
			char[]	tax			= new char[12];
			char[]	fine		= new char[12];

			private Bill() {

			}
		}
		char[]	nameOfCustomer	= new char[40];
		char[]	windowPeriod	= new char[8];
		char[]	regionCode		= new char[2];
	}
}
