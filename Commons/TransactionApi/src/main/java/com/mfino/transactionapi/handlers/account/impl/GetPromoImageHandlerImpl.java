package com.mfino.transactionapi.handlers.account.impl;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.transactionapi.handlers.account.GetPromoImageHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.PromoImageXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;

/**
 * 
 * @author Amar
 *
 */
@Service("GetPromoImageHandlerImpl") 
public class GetPromoImageHandlerImpl extends FIXMessageHandler implements GetPromoImageHandler  {

	private static Logger log = LoggerFactory.getLogger(GetPromoImageHandlerImpl.class);
	
	public Result handle(TransactionDetails transactionDetails) {
		PromoImageXMLResult result = new PromoImageXMLResult();
		
		String promoImagepath = ConfigurationUtil.getPromoImagepath();
		
		File catalinaBase = new File( System.getProperty( "catalina.base" ) ).getAbsoluteFile();
		File file = new File( catalinaBase, "webapps/" +  promoImagepath);
		
		if(!file.exists())
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_PromoImageNotFound);
			log.error("Promo image path doesn't exist");
			return result;
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_GetPromoImageSuccessful);
		result.setPromoImageURL(promoImagepath);
		log.info("Get Promo Image Successful");
		return result;
	
	}

}
