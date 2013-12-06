package com.mfino.gt.iso8583;

import java.io.IOException;

import com.mfino.fix.CmFinoFIX;

public class WrapperISOMessageFactory {

	private WrapperISOMessageFactory() {

	}


	//FIXME have to make this configurable
	private static void parseConfigFile() throws IOException {
	}

	public static WrapperISOMessage newWrapperISOMessage(int type, String bankInterface) throws IOException {
		return null;
	}
	
	/**
	 * default bank interface is used.Default=CmFinoFIX.ISO8583_Variant_Sinarmas_Bank_Interface
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public static WrapperISOMessage newWrapperISOMessage(int type) throws IOException{
		
		return newWrapperISOMessage(type,CmFinoFIX.ISO8583_Variant_GT_Bank_Interface);
	}
	
}
