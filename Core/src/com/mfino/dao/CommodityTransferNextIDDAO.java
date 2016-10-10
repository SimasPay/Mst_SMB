/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import org.hibernate.Criteria;
import org.hibernate.LockMode;

import com.mfino.domain.CommodityTransferNextId;


public class CommodityTransferNextIDDAO extends BaseDAO<CommodityTransferNextId> 
{
	/**
	 * lock the record we read,it is same as select for update
	 * once the transactions commits it will release the lock
	 * @return
	 */
	public CommodityTransferNextId getNextIDWithLock()
	{
		Criteria criteria = createCriteria();
		criteria.setLockMode(LockMode.UPGRADE);
		return (CommodityTransferNextId)criteria.list().get(0);
	}
}
