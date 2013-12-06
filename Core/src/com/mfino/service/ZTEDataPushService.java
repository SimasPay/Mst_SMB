package com.mfino.service;

import com.mfino.domain.ZTEDataPush;

public interface ZTEDataPushService {

	/**
	 * queries ZTEDataPushDAO with Msisdn and returns ztedatapush
	 * @param sourceMDN
	 * @return 
	 */
	ZTEDataPush getByMDN(String sourceMDN);

}
