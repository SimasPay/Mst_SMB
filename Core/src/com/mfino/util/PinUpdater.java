package com.mfino.util;

import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMdn;

public class PinUpdater {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		SubscriberMDNDAO dao = DAOFactory.getInstance().getSubscriberMdnDAO();
		List<SubscriberMdn> subList = dao.getAll();
		for(SubscriberMdn mdn:subList) {
			
			String newPin = MfinoUtil.calculateDigestPin(mdn.getMdn(), "1234");
			mdn.setDigestedpin(newPin);
			
			
		}
		dao.save(subList);
		
	}
}
