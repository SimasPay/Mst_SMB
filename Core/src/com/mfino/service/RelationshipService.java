package com.mfino.service;

import java.util.Collection;
import java.util.List;

import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;

/**
 * @author Sasi
 *
 */
public interface RelationshipService {

	public Collection<Integer> getRelationshipTypes(Partner sourcePartner, Partner destinationPartner, DistributionChainTemplate dct);
	
	public Integer getLevel(Partner partner, DistributionChainTemplate dct);
	
	public List<Partner> getDescendents(Partner partner, DistributionChainTemplate dct);
}
