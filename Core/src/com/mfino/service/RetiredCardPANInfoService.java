package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.RetiredCardPANInfoQuery;
import com.mfino.domain.RetiredCardPANInfo;

public interface RetiredCardPANInfoService {
	public List<RetiredCardPANInfo> get(RetiredCardPANInfoQuery query);
	public void  save(RetiredCardPANInfo retiredCardPANInfo);
}
