package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ZTEDataPushDAO;
import com.mfino.domain.ZTEDataPush;
import com.mfino.service.ZTEDataPushService;

@Service("ZTEDataPushServiceImpl")
public class ZTEDataPushServiceImpl implements ZTEDataPushService{
	private static Logger log = LoggerFactory.getLogger(ZTEDataPushServiceImpl.class);
	
	/**
	 * queries ZTEDataPushDAO with Msisdn and returns ztedatapush
	 * @param sourceMDN
	 * @return 
	 */

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public ZTEDataPush getByMDN(String MDN) {
		ZTEDataPushDAO mdnDAO = DAOFactory.getInstance().getZTEDataPushDAO();
		return mdnDAO.getByMsisdn(MDN);
	}

}
