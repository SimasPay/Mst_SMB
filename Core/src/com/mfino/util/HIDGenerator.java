package com.mfino.util;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CommodityTransferNextIDDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.CommodityTransferNextId;

public class HIDGenerator implements IdentifierGenerator {

	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {
		
		CommodityTransferNextIDDAO commodityTransferNextIDDAO = DAOFactory.getInstance().getCommodityTransferNextIDDAO();
		CommodityTransferNextId commodityTransferNextID = commodityTransferNextIDDAO.getNextIDWithLock();
		long nextID = commodityTransferNextID.getNextrecordid();
		commodityTransferNextID.setNextRecordId(commodityTransferNextID.getNextrecordid()+1);
		commodityTransferNextIDDAO.save(commodityTransferNextID);
		
		return nextID;
	}
}