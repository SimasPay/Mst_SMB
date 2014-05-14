package com.mfino.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterbankTransfer;
import com.mfino.service.IBTService;

@Service("IBTServiceImpl")
public class IBTServiceImpl implements IBTService {
	
	/**
	 * Gets the Inter bank transfer entry for the given SCTL ID
	 * @param sctlID
	 * @return
	 */
	public InterbankTransfer getBySctlId(Long sctlID) {
		InterbankTransfer result = null;
		
		if(sctlID == null) return null;
		
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		InterBankTransfersQuery query = new InterBankTransfersQuery();
		query.setSctlId(sctlID);
		List<InterbankTransfer> ibtList = interBankTransferDao.get(query);
		
		if(CollectionUtils.isNotEmpty(ibtList))
		{
			//Only there should be one record for a given sctld
			result = ibtList.get(0);
		}
		return result;
	}
}
