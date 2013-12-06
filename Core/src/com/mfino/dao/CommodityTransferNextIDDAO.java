/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import org.hibernate.Criteria;
import org.hibernate.LockMode;

import com.mfino.domain.CommodityTransferNextID;


public class CommodityTransferNextIDDAO extends BaseDAO<CommodityTransferNextID> 
{
	/**
	 * lock the record we read,it is same as select for update
	 * once the transactions commits it will release the lock
	 * @return
	 */
	public CommodityTransferNextID getNextIDWithLock()
	{
		Criteria criteria = createCriteria();
		criteria.setLockMode(LockMode.UPGRADE);
		return (CommodityTransferNextID)criteria.list().get(0);
	}
}
