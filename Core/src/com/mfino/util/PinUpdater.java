package com.mfino.util;

import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMDN;

public class PinUpdater {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		SubscriberMDNDAO dao = DAOFactory.getInstance().getSubscriberMdnDAO();
		List<SubscriberMDN> subList = dao.getAll();
		for(SubscriberMDN mdn:subList) {
			
			String newPin = MfinoUtil.calculateDigestPin(mdn.getMDN(), "1234");
			mdn.setDigestedPIN(newPin);
			
			
		}
		dao.save(subList);
		
	}
}
