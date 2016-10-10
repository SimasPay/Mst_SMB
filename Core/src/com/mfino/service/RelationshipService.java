package com.mfino.service;

import java.util.Collection;
import java.util.List;

import com.mfino.domain.DistributionChainTemp;
import com.mfino.domain.Partner;

/**
 * @author Sasi
 *
 */
public interface RelationshipService {

	public Collection<Integer> getRelationshipTypes(Partner sourcePartner, Partner destinationPartner, DistributionChainTemp dct);
	
	public Integer getLevel(Partner partner, DistributionChainTemp dct);
	
	public List<Partner> getDescendents(Partner partner, DistributionChainTemp dct);
}
