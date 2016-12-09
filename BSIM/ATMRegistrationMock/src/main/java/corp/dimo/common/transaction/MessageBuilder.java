package corp.dimo.common.transaction;

import iso8583.jPos.common.utils.PackagerBuilder;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

import corp.dimo.common.utils.ConfigConstan;
import corp.dimo.common.utils.HelperUtils;
import corp.dimo.common.utils.MessageConstant;

public class MessageBuilder extends MessageConstant{

	public static ISOMsg atmMBankingRegistration(String sourceAcctNo, String encryptPin, String mdn){
		ISOMsg isoMsg = new ISOMsg();
		ISOPackager packager = PackagerBuilder.customPacakager(ConfigConstan.packager_iso87ascii);
		
		isoMsg.setPackager(packager);
		
		try {
			
			isoMsg.setMTI(mti_transaction);
			isoMsg.set(3, processingCode_atm_mBankingRegistration);
			isoMsg.set(7, HelperUtils.commonCurrentDateString("MMddhhmmss"));
			isoMsg.set(11, HelperUtils.padleft(Integer.toString(HelperUtils.generateUniqueInteger()), 6, '0'));
			isoMsg.set(12, HelperUtils.commonCurrentDateString("hhmmss"));
			isoMsg.set(13, HelperUtils.commonCurrentDateString("MMdd"));
			isoMsg.set(18, merchantType_atm);
			isoMsg.set(24, networkInternationIdentifier_channelActivation);
			isoMsg.set(27, authorizationIdentificationResponseLength);
			isoMsg.set(32, acquiringInstitutionCode);
			isoMsg.set(33, forwardingInstitutionCode);
			isoMsg.set(35, track2Data);
			isoMsg.set(37, HelperUtils.padleft(Integer.toString(HelperUtils.generateUniqueInteger()), 12, '0'));
			isoMsg.set(41, cardAcceptorTermID_atm);
			isoMsg.set(42, cardAcceptorID_atm);
			isoMsg.set(43, cardAcceptorNameLoc_atm);
			isoMsg.set(48, additionalData+mdn);
			isoMsg.set(52, HelperUtils.padleft(encryptPin, 16, '0'));
			isoMsg.set(61, mdn);
			isoMsg.set(102, HelperUtils.padleft(sourceAcctNo, 28, ' '));
			isoMsg.set(121, languange_idn);
			
		} catch (ISOException e) {
			e.printStackTrace();
		}
		
		return isoMsg;
	}
}
