package com.mfino.mce.backend.impl;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CommodityTransferNextIDDAO;
import com.mfino.domain.CommodityTransferNextID;
import com.mfino.mce.backend.CommodityTransferSequenceGenerator;
import org.springframework.aop.SpringProxy;
import org.hibernate.jdbc.ConnectionWrapper;

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
		CommodityTransferNextID commodityTransferNextID = commodityTransferNextIDDAO.getNextIDWithLock();
		long nextID = commodityTransferNextID.getNextRecordID();
		commodityTransferNextID.setNextRecordID(commodityTransferNextID.getNextRecordID()+1);
		commodityTransferNextIDDAO.save(commodityTransferNextID);
		log.info("CommodityTransferSequenceGeneratorImpl :: getNextTransferID() END");
		return nextID;
	}
}
