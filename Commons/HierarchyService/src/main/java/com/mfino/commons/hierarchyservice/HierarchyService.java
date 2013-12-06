package com.mfino.commons.hierarchyservice;

import com.mfino.domain.Subscriber;

/**
 * @author Sasi
 *
 */
public interface HierarchyService {
	
	/**
	 * 
	 * @param sourceSubscriber
	 * @param destSubscriber
	 * @param serviceName
	 * @param transactionTypeName
	 * @return
	 */
	public Integer validate(Subscriber sourceSubscriber, Subscriber destSubscriber, String serviceName, String transactionTypeName);

}
