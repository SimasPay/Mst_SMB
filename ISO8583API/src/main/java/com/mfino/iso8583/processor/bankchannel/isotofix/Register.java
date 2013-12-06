package com.mfino.iso8583.processor.bankchannel.isotofix;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMRegister;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.IXLinkReqeustProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class Register implements IXLinkReqeustProcessor {
	private static Logger log = LoggerFactory.getLogger(Register.class);

	@Override
	public CFIXMsg process(XLinkISOMessage isoMsg) throws Exception {
		CMRegister request = new CMRegister();
		request.setSourceMDN(null);//SourceMDNForPrepaid()

		if (!StringUtils.isBlank(isoMsg.getBillingProvidertData()))
			return forwardXLinkBankChannelRegisterRequest(isoMsg, request);
		else
			return null;//ForwardResponse(ISOMsg,"13");

	}

	private CFIXMsg forwardXLinkBankChannelRegisterRequest(XLinkISOMessage isoMsg, CMRegister request) {

		processCommonElements(isoMsg, request);
		if (!StringUtils.isBlank(isoMsg.getBillingProvidertData())) {
			String mdn = isoMsg.getBillingProvidertData().trim();
			request.setDestMDN(mdn);
			request.setSourceMDN(mdn);
			if (!StringUtils.isBlank(isoMsg.getInstitutionCode()))
				request.setCompanyCode(Integer.parseInt(isoMsg.getInstitutionCode()));
			if (CmFinoFIX.ISO8583_ProcessingCode_XLink_Register.equals(isoMsg.getProcessingCode())) {
				request.setLowBalNotifRegistered(1);
				request.setLowBalNotifType(0);
			}
			else if (CmFinoFIX.ISO8583_ProcessingCode_XLink_UnRegister.equals(isoMsg.getProcessingCode().toString())) {
				request.setLowBalNotifRegistered(0);
				request.setLowBalNotifType(0);
			}
		}
		if (!StringUtils.isBlank(request.getSourceMDN())) {
			return request;
		}
		else {
			//FIXME
			log.error("No Dest MDN Specified - Connection " + "Link name here" + " !!!\nRequest Data:\n" + isoMsg.toString() + "\n");
			//FIXME what to do here
			return null;
		}
	}

	private void processCommonElements(WrapperISOMessage isoMsg, CMRegister request) {
		if (!StringUtils.isBlank(isoMsg.getPAN()))
			request.setISO8583_PrimaryAccountNumber(isoMsg.getPAN());
		if (isoMsg.getLocalTransactionTime() != null) {
			IsoValue<Timestamp> ts = new IsoValue<Timestamp>(IsoType.TIME, isoMsg.getLocalTransactionTime());
			request.setISO8583_LocalTxnTimeHhmmss(ts.toString());
		}
		if (isoMsg.getMerchantType() != null)
			request.setISO8583_MerchantType(isoMsg.getMerchantType().toString());
		if (!StringUtils.isBlank(isoMsg.getAcquiringInstitutionIdentificationCode()))
			request.setISO8583_AcquiringInstIdCode(Integer.parseInt(isoMsg.getAcquiringInstitutionIdentificationCode()));//Use this in BackEnd Server to get Merchant Source MDN (DE-32) AcquiringInstitutionIdentificationCode which corresponds to MerchantID
		if (!StringUtils.isBlank(isoMsg.getCardAcceptorTerminalIdentification()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorTerminalIdentification());//Corresponds to ref SOURCE MDN of Merchant
		if (isoMsg.getSTAN() != null)
			request.setISO8583_SystemTraceAuditNumber(isoMsg.getSTAN().toString());
		if (!StringUtils.isBlank(isoMsg.getRRN()))
			request.setISO8583_RetrievalReferenceNum(isoMsg.getRRN());
		request.setISO8583_MessageData(isoMsg.toString());
		request.setISO8583_Variant(isoMsg.getISOVariant());
		request.setISO8583_ProcessingCode(isoMsg.getProcessingCode().toString());
	}

}
