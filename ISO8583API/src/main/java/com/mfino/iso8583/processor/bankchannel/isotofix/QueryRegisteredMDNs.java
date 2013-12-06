package com.mfino.iso8583.processor.bankchannel.isotofix;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMQueryRegisteredMDNs;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.processor.bankchannel.IXLinkReqeustProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class QueryRegisteredMDNs implements IXLinkReqeustProcessor {

	@Override
	public CFIXMsg process(XLinkISOMessage isoMsg) throws Exception {
		CMQueryRegisteredMDNs request = new CMQueryRegisteredMDNs();
		request.setSourceMDN(null);//FIXME SourceMDNForPrepaid()
		request.setDestMDN(null);//FIXME SourceMDNForPrepaid()

		if(StringUtils.isBlank(isoMsg.getInstitutionCode()))
			request.setCompanyCode(Integer.parseInt(isoMsg.getInstitutionCode()));
		
		if (!StringUtils.isBlank(isoMsg.getPAN()))
			request.setISO8583_PrimaryAccountNumber(isoMsg.getPAN());
		if (isoMsg.getLocalTransactionTime() != null) {
			IsoValue<Timestamp> ts = new IsoValue<Timestamp>(IsoType.TIME, isoMsg.getLocalTransactionTime());
			request.setISO8583_LocalTxnTimeHhmmss(ts.toString());
		}
		if (isoMsg.getMerchantType() != null)
			request.setISO8583_MerchantType(isoMsg.getMerchantType().toString());
		if (!StringUtils.isBlank(isoMsg.getAcquiringInstitutionIdentificationCode()))
			request.setISO8583_AcquiringInstIdCode(Integer.parseInt(isoMsg.getAcquiringInstitutionIdentificationCode()));
		if (isoMsg.getSTAN() != null)
			request.setISO8583_SystemTraceAuditNumber(isoMsg.getSTAN().toString());
		if (!StringUtils.isBlank(isoMsg.getRRN()))
			request.setISO8583_RetrievalReferenceNum(isoMsg.getRRN());
		if (!StringUtils.isBlank(isoMsg.getCardAcceptorTerminalIdentification()))
			request.setISO8583_CardAcceptorIdCode(isoMsg.getCardAcceptorTerminalIdentification());
		request.setISO8583_MessageData(isoMsg.toString());
		request.setISO8583_Variant(isoMsg.getISOVariant());
		request.setISO8583_ProcessingCode(isoMsg.getProcessingCode().toString());

		return request;
	}

}
