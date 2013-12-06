package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.ChargeDefinitionQuery;
import com.mfino.domain.ChargeDefinition;

public interface ChargeDefinitionService {
	public List<ChargeDefinition> get(ChargeDefinitionQuery query);
}
