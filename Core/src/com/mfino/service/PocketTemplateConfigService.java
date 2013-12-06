package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.PocketTemplateConfig;

public interface PocketTemplateConfigService {
	public List<PocketTemplateConfig> get(PocketTemplateConfigQuery query);
}
