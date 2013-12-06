package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.service.PendingCommodityTransferService;

@Service("PendingCommodityTransferServiceImpl")
public class PendingCommodityTransferServiceImpl implements PendingCommodityTransferService{
	
	PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer getById(long pctId) throws MfinoRuntimeException
	{
		return pctDao.getById(pctId);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<PendingCommodityTransfer> getByQuery(CommodityTransferQuery commodityTransferQuery) throws Exception
	{
		return pctDao.get(commodityTransferQuery);
	}
}
