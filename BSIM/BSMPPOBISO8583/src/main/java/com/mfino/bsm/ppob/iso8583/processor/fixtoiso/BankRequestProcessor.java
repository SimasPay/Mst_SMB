package com.mfino.bsm.ppob.iso8583.processor.fixtoiso;

import static com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter.getHHMMSS;
import static com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter.getMMDD;
import static com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter.getMMDDHHMMSS;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.util.DateTimeUtil;

public abstract class BankRequestProcessor implements IFixToIsoProcessor {
	
	public Log log = LogFactory.getLog(this.getClass());
	
	protected ISOMsg	          isoMsg	= new ISOMsg();

	protected Map<String, String>	constantFieldsMap;
	
	public int	TPM_UseBankNewCodes;
	public String	sinarmasTransferCode;
	public String	sinarmasTransferReversalCode;

	public void setConstantFieldsMap(Map<String, String> map) {
		this.constantFieldsMap = map;
		TPM_UseBankNewCodes = Integer.parseInt(constantFieldsMap.get("TPM_UseBankNewCodes"));
		sinarmasTransferCode = constantFieldsMap.get("Sinarmas_Transfer");
		sinarmasTransferReversalCode = constantFieldsMap.get("Sinarmas_Transfer_Reversal");
	}

	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {

		CMBankRequest request = (CMBankRequest) fixmsg;

		try {
			isoMsg.set(2, constantFieldsMap.get("2"));// 2
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.set(7, getMMDDHHMMSS(ts)); // 7
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, getHHMMSS(ts)); // 12
			isoMsg.set(13, getMMDD(ts)); // 13
			// isoMsg.setMerchantType(Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone));//18
			// isoMsg.setAuthorizingIdentificationResponseLength(CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas);//27
			isoMsg.set(32, CmFinoFIX.ISO8583_AcquiringInstIdCode_mFino_to_Bank.toString()); // 32
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_mFino_to_Bank.toString()); // 33
			// isoMsg.setTrack2Data(request.getSourceCardPAN()); //35
			isoMsg.set(37, request.getTransactionID().toString()); // 37
			isoMsg.set(42, request.getSourceMDN()); // 42 FIXME merchantcode?
			isoMsg.set(43, constructDE43(request)); // 43
			                                        // FIXME
			                                        // merchant
			                                        // name
			                                        // location
			
			isoMsg.set(123, request.getPOSDataCode()); // 123
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}

	protected String buildProcessingCode(CMBankRequest request) {
		return null;
	}

	protected String constructDE43(CMBankRequest request) {

		final String spaces = "                                                                          ";

		return "GT    -"
		        + request.getServiceChargeTransactionLogID()
		        + ((request.getSourceCardPAN() == null) ? " " : "-" + request.getSourceCardPAN())
		        + spaces.substring(0, 30 - request.getServiceChargeTransactionLogID().toString().length()
		                - (request.getSourceCardPAN() == null ? 0 : request.getSourceCardPAN().length())) + "NG";

	}

	public static void main(String[] args) throws Exception {
		CMBankRequest request = new CMBankRequest();
		BankRequestProcessor processor = new BankRequestProcessor() {
		};

		request.setServiceChargeTransactionLogID(33333333l);

		request.setSourceCardPAN("1234567890");
		System.out.println(processor.constructDE43(request));

		request.setSourceCardPAN(null);
		System.out.println(processor.constructDE43(request));

	}

}
