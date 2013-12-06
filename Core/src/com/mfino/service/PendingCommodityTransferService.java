package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.exceptions.MfinoRuntimeException;


public interface PendingCommodityTransferService {

	public PendingCommodityTransfer getById(long pctId) throws MfinoRuntimeException;
	public List<PendingCommodityTransfer> getByQuery(CommodityTransferQuery commodityTransferQuery) throws Exception;
	
}
