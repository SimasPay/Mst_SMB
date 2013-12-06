package com.mfino.dao;

import java.util.List;

import com.mfino.domain.VisafoneTxnGenerator;

/**
 * 
 * @author Sasi
 *
 */
public class VisafoneTxnGeneratorDao extends BaseDAO<VisafoneTxnGenerator> {

	public VisafoneTxnGenerator getVisafoneTxnGenerator(){
		VisafoneTxnGenerator vTxnGanerator = null;
		
		List<VisafoneTxnGenerator> vTxnList = super.getAll();
		if((null != vTxnList) && (vTxnList.size() > 0)) return vTxnList.get(0);
		
		return vTxnGanerator;
	}
	 
}
