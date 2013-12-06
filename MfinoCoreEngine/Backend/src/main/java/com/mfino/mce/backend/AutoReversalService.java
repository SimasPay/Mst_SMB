package com.mfino.mce.backend;

import java.util.Collection;

import com.mfino.domain.AutoReversals;
import com.mfino.mce.backend.exception.DuplicateAutoReversalException;
import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public interface AutoReversalService {

	public MCEMessage doReversal(MCEMessage mceMessage) throws DuplicateAutoReversalException;
	
	public MCEMessage chargesToTransitInquiry(MCEMessage mceMessage) throws DuplicateAutoReversalException;
	
	public MCEMessage chargesToTransitConfirmation(MCEMessage mceMessage) throws DuplicateAutoReversalException;
	
	public MCEMessage destinationToTransitInquiry(MCEMessage mceMessage) throws DuplicateAutoReversalException;
	
	public MCEMessage destinationToTransitConfirmation(MCEMessage mceMessage) throws DuplicateAutoReversalException;
	
	public MCEMessage transitToSourceInquiry(MCEMessage mceMessage) throws DuplicateAutoReversalException;
	
	public MCEMessage transitToSourceConfirmation(MCEMessage mceMessage) throws DuplicateAutoReversalException;
	
	public MCEMessage destinationToTransitInquiryFromBank(MCEMessage mceMessage);
	
	public MCEMessage destinationToTransitConfirmationFromBank(MCEMessage mceMessage);
	
	public MCEMessage transitToSourceInquiryFromBank(MCEMessage mceMessage);
	
	public MCEMessage transitToSourceConfirmationFromBank(MCEMessage mceMessage);
	
	public Collection<AutoReversals> getAutoReversalsWithStatus(Collection<Integer> statuses);
	
	public AutoReversals getAutoReversalBySctlId(Long sctlId);
	
	public void updateAutoReversalStatus(Long sctlId, Integer status);
}
