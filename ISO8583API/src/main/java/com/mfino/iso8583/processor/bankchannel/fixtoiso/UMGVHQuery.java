package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.UMGVHISOMessage;
import com.mfino.util.StringUtilities;

public class UMGVHQuery implements IFIXtoISOProcessor {

	public static ArrayList<String> m_MerchantPrefixData;
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		CMBankChannelResponse response = (CMBankChannelResponse) fixmsg;

		UMGVHISOMessage isoMsg = getIsoMessage();

		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			return isoMsg;

		String providerData = null;
		int merchantPCode = Integer.parseInt(isoMsg.getAccountIdentification2().substring(0, 4).trim());
		String toAccountNumber = isoMsg.getAccountIdentification2().substring(4, 28);
		int configuredEMoneyCode=0;
		String configData=null,code=null,serviceName=null;
		for(String str:m_MerchantPrefixData)
		{
			configData = str;
			String[] s = StringUtilities.tokenizeString(configData, ',');
			code=s[0];
			if(configData.length()>0)
				serviceName = configData;
			if(CmFinoFIX.VAServiceName_EMONEY_CASHIN.equals(serviceName))
				configuredEMoneyCode = Integer.parseInt(code);
		}
		if(merchantPCode!=configuredEMoneyCode)
		{
			providerData = String.format("%-30s%-16s%-30s%-30s%s",response.getPayerName().substring(0, 30),
					isoMsg.getAccountIdentification2().substring(0, 4)+StringUtilities.trimBeginningChars(toAccountNumber, '0'),
					" ","SMART Telecom Branch","10");
		}
		else if(merchantPCode == configuredEMoneyCode && 
				!CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_VirtualAccount_Transfer_Credit.equals(isoMsg.getProcessingCode().toString().substring(0, 2)))
		{
			providerData = String.format("%-30s%-16s%-30s%-30s%s", response.getPayerName().substring(0, 30),
					isoMsg.getAccountIdentification2().substring(0, 4)+StringUtilities.trimBeginningChars(toAccountNumber, '0'),
					" ","SMART Telecom Branch","10");
		}

		providerData = providerData.toUpperCase();
		isoMsg.setBillingProvidertData(providerData);
		//FIXME ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
		return isoMsg;
	}

	public UMGVHISOMessage getIsoMessage() {
		return null;
	}
}
