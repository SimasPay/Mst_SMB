package com.mfino.mce.backend.impl;

import java.math.BigDecimal;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CommodityTransferNextIDDAO;
import com.mfino.domain.CommodityTransferNextId;
import com.mfino.mce.backend.CommodityTransferSequenceGenerator;

/**
 * @author Sasi
 *
 */
public class CommodityTransferSequenceGeneratorImpl extends BaseServiceImpl implements CommodityTransferSequenceGenerator{

	/**
	 * Method to get the next id from the nextID table
	 * record is locked before reading and then updating it.
	 * @return
	 */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	public Long getNextTransferID() {
		log.info("CommodityTransferSequenceGeneratorImpl :: getNextTransferID() BEGIN");
		CommodityTransferNextIDDAO commodityTransferNextIDDAO = coreDataWrapper.getCommodityTransferNextIDDAO();
		CommodityTransferNextId commodityTransferNextID = commodityTransferNextIDDAO.getNextIDWithLock();
		long nextID = commodityTransferNextID.getNextrecordid().longValue();
		commodityTransferNextID.setNextrecordid(commodityTransferNextID.getNextrecordid()+1);
		commodityTransferNextIDDAO.save(commodityTransferNextID);
		log.info("CommodityTransferSequenceGeneratorImpl :: getNextTransferID() END");
		return nextID;
	}
}
