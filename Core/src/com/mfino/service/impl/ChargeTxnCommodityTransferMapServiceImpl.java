/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.service.ChargeTxnCommodityTransferMapService;

/**
 * @author Sreenath
 *
 */
@Service("ChargeTxnCommodityTransferMapServiceImpl")
public class ChargeTxnCommodityTransferMapServiceImpl implements ChargeTxnCommodityTransferMapService{
	
	private static Logger log	= LoggerFactory.getLogger(ChargeTxnCommodityTransferMapServiceImpl.class);
	
	/**
	 * Returns the ChargeTxnCommodityTransferMap records matching the query given
	 * @param query
	 * @return
	 */
	public List<ChargetxnTransferMap> getChargeTxnCommodityTransferMapByQuery
	(ChargeTxnCommodityTransferMapQuery query){
		List<ChargetxnTransferMap> lstTxnCommodityTransferMaps = null;
		if(query!=null){
			log.info("Getting ChargeTxnCommodityTransferMap records matching query: "+query);
			ChargeTxnCommodityTransferMapDAO txnCommodityTransferMapDAO = DAOFactory.getInstance().getTxnTransferMap();
			lstTxnCommodityTransferMaps = txnCommodityTransferMapDAO.get(query);
		}
		return lstTxnCommodityTransferMaps;
	}

}
