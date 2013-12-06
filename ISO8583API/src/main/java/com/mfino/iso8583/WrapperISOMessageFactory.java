package com.mfino.iso8583;

import java.io.IOException;

import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class WrapperISOMessageFactory {

	private WrapperISOMessageFactory() {

	}

	private static MessageFactory	MsgFactory;

	//FIXME have to make this configurable
	private static void parseConfigFile() throws IOException {
		MsgFactory = ConfigParser.createFromClasspathConfig("config.xml");
	}

	public static WrapperISOMessage newWrapperISOMessage(int type, String bankInterface) throws IOException {

		if (MsgFactory == null) {
			parseConfigFile();
		}

		IsoMessage msg = MsgFactory.newMessage(type);
		if (CmFinoFIX.ISO8583_Variant_Sinarmas_Bank_Interface.equals(bankInterface))
			return new SinarmasISOMessage(msg);
		else if(CmFinoFIX.ISO8583_Variant_Mobile_Operator_Gateway_Interface.equals(bankInterface))
			return new MobileOperatorISOMessage(msg);
		else if(CmFinoFIX.ISO8583_Variant_Bank_BillPayments_Gateway_Interface.equals(bankInterface))
			return new UMGH2HISOMessage(msg);
		else if (CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface.equals(bankInterface))
			return new ZenithBankISOMessage(msg);	
		return null;

	}
	
	/**
	 * default bank interface is used.Default=CmFinoFIX.ISO8583_Variant_Sinarmas_Bank_Interface
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public static WrapperISOMessage newWrapperISOMessage(int type) throws IOException{
		
		return newWrapperISOMessage(type,CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface);
		
	}
	
	public static void setMessageFactory(MessageFactory messageFactory)
	{
		MsgFactory = messageFactory;
	}
	
	
}
