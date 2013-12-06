/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SCTLSettlementMapDAO;
import com.mfino.dao.query.SCTLSettlementMapQuery;
import com.mfino.domain.SCTLSettlementMap;
import com.mfino.service.SCTLSettlementMapService;

/**
 * @author Shashank
 *
 */
@Service("SCTLSettlementMapServiceImpl")
public class SCTLSettlementMapServiceImpl implements SCTLSettlementMapService{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<SCTLSettlementMap> get(SCTLSettlementMapQuery query){
		
		SCTLSettlementMapDAO sctlSettlementMapDAO = DAOFactory.getInstance().getSCTLSettlementMapDao();
		
		List<SCTLSettlementMap> pendingSettlements = sctlSettlementMapDAO.get(query);
		
		return pendingSettlements;		

	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(SCTLSettlementMap sCTLSettlementMap){
		SCTLSettlementMapDAO sCTLSettlementMapDao = DAOFactory.getInstance().getSCTLSettlementMapDao();
		sCTLSettlementMapDao.save(sCTLSettlementMap);
	}

}
