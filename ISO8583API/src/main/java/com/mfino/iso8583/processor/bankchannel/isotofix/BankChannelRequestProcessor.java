package com.mfino.iso8583.processor.bankchannel.isotofix;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelEMoneyInquiry;
import com.mfino.fix.CmFinoFIX.CMBankChannelRequest;
import com.mfino.fix.CmFinoFIX.CMH2HBankChannelRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.UMGVHISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;
import com.mfino.util.StringUtilities;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public abstract class BankChannelRequestProcessor {

	private static Log	log	= LogFactory.getLog(BankChannelRequestProcessor.class);

	protected CFIXMsg forwardEmoneyTransferBankChannelRequest(WrapperISOMessage isoMsg, CMBankChannelRequest request) {
		if (!StringUtils.isBlank(isoMsg.getAcquiringInstitutionIdentificationCode()))
			request.setISO8583_AcquiringInstIdCode(Integer.parseInt(isoMsg.getAcquiringInstitutionIdentificationCode()));//Use this in BackEnd Server to get Merchant Source MDN (DE-32) AcquiringInstitutionIdentificationCode which corresponds to MerchantID

		//		Request.SetSourceMDNByRef(SourceMDNForPostpaid());			
		request.setSourceMDN(null);//FIXME get this from somewhere, mostly tpmconfig.xml
		//
		if (isoMsg.getLocalTransactionTime() != null) {
			IsoValue<Timestamp> ts = new IsoValue<Timestamp>(IsoType.TIME, isoMsg.getLocalTransactionTime());
			request.setISO8583_LocalTxnTimeHhmmss(ts.toString());
		}

		if (isoMsg.getMerchantType() != null) {
			request.setISO8583_MerchantType(isoMsg.getMerchantType().toString());
		}

		if (!StringUtils.isBlank(isoMsg.getPAN()))
			request.setISO8583_PrimaryAccountNumber(isoMsg.getPAN());

		if (isoMsg.getSTAN() != null)
			request.setISO8583_SystemTraceAuditNumber(isoMsg.getSTAN().toString());

		if (!StringUtils.isBlank(isoMsg.getRRN()))
			request.setISO8583_RetrievalReferenceNum(isoMsg.getRRN());
		//		

		request.setISO8583_MessageData(isoMsg.toString());
		//
		request.setISO8583_Variant(isoMsg.getISOVariant());
		//
		request.setISO8583_ProcessingCode(isoMsg.getProcessingCode().toString());

		if (!StringUtils.isBlank(isoMsg.getCardAcceptorTerminalIdentification()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorTerminalIdentification());//Corresponds to ref SOURCE MDN of Merchant
		//
		if (!StringUtils.isBlank(isoMsg.getAccountIdentification2().substring(4, 32))) {
			try {
				String destMDN = isoMsg.getAccountIdentification2().substring(4, 32);
				destMDN = StringUtilities.trimBeginningChars(destMDN, '0');
				request.setDestMDN(destMDN);
			}
			catch (IndexOutOfBoundsException ex) {
				//FIXME
				log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n", ex);
				//FIXME what to do here
				return null;
			}
		}
		return request;
	}

	protected CFIXMsg forwardEmoneyQueryBankChannelRequest(UMGVHISOMessage isoMsg, CMBankChannelEMoneyInquiry request) {
		if (!StringUtils.isBlank(isoMsg.getAcquiringInstitutionIdentificationCode()))
			request.setISO8583_AcquiringInstIdCode(Integer.parseInt(isoMsg.getAcquiringInstitutionIdentificationCode()));//Use this in BackEnd Server to get Merchant Source MDN (DE-32) AcquiringInstitutionIdentificationCode which corresponds to MerchantID

		request.setSourceMDN(null);//FIXME get this from somewhere, mostly tpmconfig.xml
		if (isoMsg.getLocalTransactionTime() != null) {
			IsoValue<Timestamp> ts = new IsoValue<Timestamp>(IsoType.TIME, isoMsg.getLocalTransactionTime());
			request.setISO8583_LocalTxnTimeHhmmss(ts.toString());
		}

		if (isoMsg.getMerchantType() != null) {
			request.setISO8583_MerchantType(isoMsg.getMerchantType().toString());
		}

		if (!StringUtils.isBlank(isoMsg.getPAN()))
			request.setISO8583_PrimaryAccountNumber(isoMsg.getPAN());

		if (isoMsg.getSTAN() != null)
			request.setISO8583_SystemTraceAuditNumber(isoMsg.getSTAN().toString());

		if (!StringUtils.isBlank(isoMsg.getRRN()))
			request.setISO8583_RetrievalReferenceNum(isoMsg.getRRN());

		request.setISO8583_MessageData(isoMsg.toString());
		request.setISO8583_Variant(isoMsg.getISOVariant());
		request.setISO8583_ProcessingCode(isoMsg.getProcessingCode().toString());

		if (!StringUtils.isBlank(isoMsg.getCardAcceptorTerminalIdentification()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorTerminalIdentification());//Corresponds to ref SOURCE MDN of Merchant
		if (!StringUtils.isBlank(isoMsg.getAccountIdentification2().substring(4, 32))) {
			try {
				String destMDN = isoMsg.getAccountIdentification2().substring(4, 32);
				destMDN = StringUtilities.trimBeginningChars(destMDN, '0');
				request.setDestMDN(destMDN);
			}
			catch (IndexOutOfBoundsException ex) {
				//throw a new checked exception in this case in all the requests processors
				//FIXME
				log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n", ex);
				//FIXME what to do here
				return null;
			}
		}
		return request;
	}

	protected CFIXMsg forwardMerchantBankChannelRequest(Mobile8ISOMessage isoMsg, CMH2HBankChannelRequest request) {
		if (!StringUtils.isBlank(isoMsg.getAcquiringInstitutionIdentificationCode()))
			request.setISO8583_AcquiringInstIdCode(Integer.parseInt(isoMsg.getAcquiringInstitutionIdentificationCode()));//Use this in BackEnd Server to get Merchant Source MDN (DE-32) AcquiringInstitutionIdentificationCode which corresponds to MerchantID

		//		Request.SetSourceMDNByRef(SourceMDNForPostpaid());			
		request.setSourceMDN(null);//FIXME get this from somewhere, mostly tpmconfig.xml
		//
		if (isoMsg.getLocalTransactionTime() != null) {
			IsoValue<Timestamp> ts = new IsoValue<Timestamp>(IsoType.TIME, isoMsg.getLocalTransactionTime());
			request.setISO8583_LocalTxnTimeHhmmss(ts.toString());
		}

		if (isoMsg.getMerchantType() != null) {
			request.setISO8583_MerchantType(isoMsg.getMerchantType().toString());
		}

		if (!StringUtils.isBlank(isoMsg.getPAN()))
			request.setISO8583_PrimaryAccountNumber(isoMsg.getPAN());

		if (isoMsg.getSTAN() != null)
			request.setISO8583_SystemTraceAuditNumber(isoMsg.getSTAN().toString());

		if (!StringUtils.isBlank(isoMsg.getRRN()))
			request.setISO8583_RetrievalReferenceNum(isoMsg.getRRN());
		//		

		request.setISO8583_MessageData(isoMsg.toString());
		//
		request.setISO8583_Variant(isoMsg.getISOVariant());
		//
		request.setISO8583_ProcessingCode(isoMsg.getProcessingCode().toString());

		if (!StringUtils.isBlank(isoMsg.getCardAcceptorTerminalIdentification()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorTerminalIdentification());//Corresponds to ref SOURCE MDN of Merchant

		request.setPin("dummy");
		request.setBucketType(CmFinoFIX.BucketType_Recharge_Regular);
		request.setEscapePINCheck(CmFinoFIX.EscapePINCheck_Skip);

		if (!StringUtils.isBlank(isoMsg.getBillingProvidertData())) {
			try {
				String mdn = isoMsg.getBillingProvidertData().substring(0, 14);
				request.setDestMDN(mdn);
			}
			catch (IndexOutOfBoundsException ex) {
				//FIXME
				log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n", ex);
				//FIXME what to do here
				return null;
			}
		}
		return request;
	}

	protected CFIXMsg forwardArtajasaBankChannelRequest(ArtajasaISOMessage isoMsg, CMBankChannelRequest request) {

		processCommonElements(isoMsg, request);

		if (!StringUtils.isBlank(isoMsg.getBillingProvidertData())) {
			String pId = isoMsg.getBillingProvidertData().substring(0, 4);
			request.setProductIndicatorCode(pId);
			try {
				String destMDN = isoMsg.getBillingProvidertData().substring(4, 17);
				request.setDestMDN(destMDN);
			}
			catch (Exception ex) {
				//FIXME
				log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n", ex);
				//FIXME what to do here
				return null;
			}
		}
		return request;
	}

	private void processCommonElements(WrapperISOMessage isoMsg, CMBankChannelRequest request) {
		if (!StringUtils.isBlank(isoMsg.getAcquiringInstitutionIdentificationCode()))
			request.setISO8583_AcquiringInstIdCode(Integer.parseInt(isoMsg.getAcquiringInstitutionIdentificationCode()));//Use this in BackEnd Server to get Merchant Source MDN (DE-32) AcquiringInstitutionIdentificationCode which corresponds to MerchantID
		if (!StringUtils.isBlank(isoMsg.getCardAcceptorIdentificationCode()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorIdentificationCode());//Corresponds to ref SOURCE MDN of Merchant
		if (isoMsg.getLocalTransactionTime() != null) {
			IsoValue<Timestamp> ts = new IsoValue<Timestamp>(IsoType.TIME, isoMsg.getLocalTransactionTime());
			request.setISO8583_LocalTxnTimeHhmmss(ts.toString());
		}
		if (isoMsg.getMerchantType() != null)
			request.setISO8583_MerchantType(isoMsg.getMerchantType().toString());
		if (!StringUtils.isBlank(isoMsg.getPAN()))
			request.setISO8583_PrimaryAccountNumber(isoMsg.getPAN());
		if (isoMsg.getSTAN() != null)
			request.setISO8583_SystemTraceAuditNumber(isoMsg.getSTAN().toString());
		if (!StringUtils.isBlank(isoMsg.getRRN()))
			request.setISO8583_RetrievalReferenceNum(isoMsg.getRRN());
		request.setISO8583_MessageData(isoMsg.toString());
		request.setISO8583_Variant(isoMsg.getISOVariant());
		request.setISO8583_ProcessingCode(isoMsg.getProcessingCode().toString());
	}

	protected CFIXMsg forwardMobile8BankChannelRequest(Mobile8ISOMessage isoMsg, CMBankChannelRequest request) {

		processCommonElements(isoMsg, request);

		if (!StringUtils.isBlank(isoMsg.getCardAcceptorTerminalIdentification()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorTerminalIdentification());
		if (!StringUtils.isBlank(isoMsg.getBillingProvidertData())) {
			request.setProductIndicatorCode(CmFinoFIX.ISO8583_Artajasa_ProductIndicator_Mobile8MDN);
			try {
				String destMDN = isoMsg.getBillingProvidertData().substring(4, 17);
				request.setDestMDN(destMDN);
			}
			catch (Exception ex) {
				//FIXME
				log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n", ex);
				//FIXME what to do here
				return null;
			}
		}
		return request;
	}

	protected CFIXMsg forwardXLinkBankChannelRequest(XLinkISOMessage isoMsg, CMBankChannelRequest request) {

		processCommonElements(isoMsg, request);

		if (!StringUtils.isBlank(isoMsg.getBillingProvidertData())) {
			try {
				String destMDN = isoMsg.getBillingProvidertData().substring(0, 14);
				request.setDestMDN(destMDN);
			}
			catch (Exception ex) {
				//FIXME
				log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n", ex);
				//FIXME what to do here
				return null;
			}
		}
		return request;
	}

	protected CFIXMsg forwardUMGVHBankChannelRequest(UMGVHISOMessage isoMsg, CMBankChannelRequest request) {

		processCommonElements(isoMsg, request);

		if (!StringUtils.isBlank(isoMsg.getCardAcceptorTerminalIdentification()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorTerminalIdentification());

		if (!StringUtils.isBlank(isoMsg.getAccountIdentification2())) {
			try {
				String destMDN = isoMsg.getAccountIdentification2().substring(4, 32);
				destMDN = StringUtilities.trimBeginningChars(destMDN, '0');
				request.setDestMDN(destMDN);
			}
			catch (Exception ex) {
				//FIXME
				log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n", ex);
				//FIXME what to do here
				return null;
			}
		}
		return request;
	}

}
