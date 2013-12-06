/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.SCTLSettlementMapQuery;
import com.mfino.domain.SCTLSettlementMap;

/**
 * @author Shashank
 *
 */
public interface SCTLSettlementMapService {

	public List<SCTLSettlementMap> get(SCTLSettlementMapQuery query);
	public void save(SCTLSettlementMap sCTLSettlementMap);
}
