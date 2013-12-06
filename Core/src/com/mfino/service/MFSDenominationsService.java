package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MFSDenominations;

public interface MFSDenominationsService {
	
	public List<MFSDenominations> get(MFSDenominationsQuery mdquery);
}
