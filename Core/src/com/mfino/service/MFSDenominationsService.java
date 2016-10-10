package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MfsDenominations;

public interface MFSDenominationsService {
	
	public List<MfsDenominations> get(MFSDenominationsQuery mdquery);
}
