package com.mfino.mce.backend;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public interface AutoReversalPendingResolveService {
	
	public MCEMessage resolvePendingTransaction(MCEMessage mceMessage);
	
}
