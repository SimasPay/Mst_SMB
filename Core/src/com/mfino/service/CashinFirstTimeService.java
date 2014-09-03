package com.mfino.service;

import com.mfino.domain.CashinFirstTime;

public interface CashinFirstTimeService {

	public CashinFirstTime getByMDN(String MDN);
	
	public void saveCashinFirstTime(CashinFirstTime cft);
	
}
