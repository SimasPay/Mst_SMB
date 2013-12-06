package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.domain.BulkUploadFileEntry;

public interface BulkUploadFileEntryService {
	public List<BulkUploadFileEntry> get(BulkUploadFileEntryQuery query);
	public void save(BulkUploadFileEntry bulkUploadFileEntry);
}
