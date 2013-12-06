package com.mfino.zenith.airtime.visafone.communicators;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucitech.neptune.ProxyResponse;

public class VAResponseParser {

	private static Logger	log	= LoggerFactory.getLogger(VAResponseParser.class);

	public static VAResponse parse(List<Object> wsResponse) throws Exception {
		VAResponse response = new VAResponse();

		try {

			if (wsResponse.get(0) instanceof ProxyResponse) {
				ProxyResponse pr = (ProxyResponse) wsResponse.get(0);
				response.desc = pr.getDesc();
				response.retn = pr.getRetn().toString();
				
				log.info("reponse form the vtu webservice --->");
				
				log.info("vtu response desc -->"+pr.getDesc());
				log.info("vtu response retn -->"+pr.getRetn().toString());
			}
			else {
				log.error("response is not an instance of ProxyResponse");
				throw new Exception("response is not an instance of ProxyResponse");
			}

		}
		catch (Exception e) {
			log.error("VAResponseParser.parse --> Error parsing XML", e);
			e.fillInStackTrace();
			throw e;
		}

		return response;

	}

}
