package com.mfino.mce.backend;

import java.util.List;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 * Takes care of any failed auto reversals
 * Checks where Auto Reversal failed in the below steps and continues from there.
 * charges -> Transit
 * src -> Transit
 * Transit -> Src
 */
public interface AutoReversalFailuresMonitoringService {
	
	public List<MCEMessage> handleAutoReversalFailures();
	
}
