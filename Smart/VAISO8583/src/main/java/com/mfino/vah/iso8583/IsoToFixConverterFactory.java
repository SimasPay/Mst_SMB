package com.mfino.vah.iso8583;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.vah.converters.IsoToCashin;
import com.mfino.vah.converters.IsoToCashinReversal;
import com.mfino.vah.converters.IsoToNativeTransformer;

public class IsoToFixConverterFactory  {

	private static final IsoToFixConverterFactory factory = new IsoToFixConverterFactory();
	 private static Logger log = LoggerFactory.getLogger(IsoToFixConverterFactory.class);
	 
	public IsoToNativeTransformer getTransformer(String mti,String processingCode) throws ProcessorNotAvailableException {

		IsoToNativeTransformer transformer = null;
		log.info("IsoToFixConverterFactory ProcessingCode:"+processingCode);
		if(mti.equals("0200")){
			if(processingCode.startsWith("47"))
					transformer = new IsoToCashin();
			else
				throw new ProcessorNotAvailableException();
		}else{
			if(processingCode.startsWith("47"))
					transformer = new IsoToCashinReversal();
			else
				throw new ProcessorNotAvailableException();
		}
		return transformer;
	}
	
	public static IsoToFixConverterFactory getInstance(){
		return factory;
	}

}
