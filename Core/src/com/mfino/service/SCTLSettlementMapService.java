/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.SCTLSettlementMapQuery;
import com.mfino.domain.SctlSettlementMap;

/**
 * @author Shashank
 *
 */
public interface SCTLSettlementMapService {

	public List<SctlSettlementMap> get(SCTLSettlementMapQuery query);
	public void save(SctlSettlementMap sCTLSettlementMap);
}
