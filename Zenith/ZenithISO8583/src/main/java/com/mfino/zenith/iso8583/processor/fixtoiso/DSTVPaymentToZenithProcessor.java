package com.mfino.zenith.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.zenith.iso8583.GetConstantCodes;

/**
 *  TODO: need to refactor all the ISO code to use the inheritance properly 
 *  and never check for instance of as it is dangerous
 * @author POCHADRI
 *
 */
public class DSTVPaymentToZenithProcessor extends MoneyTransferToZenithProcessor {
	// *FindbugsChange*
	// Previous -- public static String MTI="0200";
	public static final String MTI="0200";
	public DSTVPaymentToZenithProcessor(){
		try {
	        isoMsg.setMTI (MTI);
        }
        catch (ISOException ex) {
	        ex.printStackTrace();
        }
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException 
	{
        // Everything is same as Money transfer except a few changes, those are done below	 
		isoMsg = super.process(fixmsg);
		
		/**
		 * DSTV specific feilds changed here
		 */
		CMDSTVMoneyTransferToBank msg =(CMDSTVMoneyTransferToBank)fixmsg;
		try{
		if(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf.equals(msg.getUICategory())){
			isoMsg.set(41, GetConstantCodes.ZENITH_DE41_SVA_DSTV_BILLPAY); //41
			isoMsg.set(42, GetConstantCodes.ZENITH_DE42_SVA_DSTV_BILLPAY); //42
			isoMsg.set(98,GetConstantCodes.ZENITH_DE98_SVA_DSTV_BILLPAY);
		}else{
			isoMsg.set(41, GetConstantCodes.ZENITH_DE41_BANK_DSTV_BILLPAY); //41
			isoMsg.set(42, GetConstantCodes.ZENITH_DE42_BANK_DSTV_BILLPAY); //42
			isoMsg.set(98,GetConstantCodes.ZENITH_DE98_BANK_DSTV_BILLPAY);
		}
		
		String preString = "Zenith";
		String seperator = "-";
		String postString ="eaZymoneyLANG";
		String invoiceNumber = msg.getInvoiceNumber();
		int spaceLength = 40 - (invoiceNumber.length()+preString.length()+2*seperator.length()+postString.length());
		String spaceChars=null;
        try {
	        spaceChars = MoneyTransferReversalToZenithProcessor.padOnRight("", ' ', spaceLength);
        }
        catch (Exception ex) {
	        // TODO Auto-generated catch block
	        ex.printStackTrace();
        }
		//isoMsg.set(43,""++"-                eaZymoneyLANG");
		isoMsg.set(43,preString+seperator+invoiceNumber+seperator+spaceChars+postString);
		}
		catch(ISOException ex){
			
		}
		return isoMsg;
	}
}

