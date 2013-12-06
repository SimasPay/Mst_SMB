package com.mfino.service;

import java.util.List;

import com.mfino.domain.BulkUploadFile;

public interface BulkUploadFileService {
	public List<BulkUploadFile> getPendingFiles();
	
	public void save(BulkUploadFile bulkUploadFile);

}
