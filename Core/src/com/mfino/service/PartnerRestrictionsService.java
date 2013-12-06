package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.PartnerRestrictions;

/**
 * 
 * @author Sasi
 *
 */
public interface PartnerRestrictionsService {
	
	public List<PartnerRestrictions> getPartnerRestrictions(PartnerRestrictionsQuery query);
	
}
