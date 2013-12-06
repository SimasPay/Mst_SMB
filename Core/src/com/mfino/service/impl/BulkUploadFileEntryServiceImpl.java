package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadFileEntryDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.domain.BulkUploadFileEntry;
import com.mfino.service.BulkUploadFileEntryService;

@Service("BulkUploadFileEntryServiceImpl")
public class BulkUploadFileEntryServiceImpl implements
		BulkUploadFileEntryService {

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<BulkUploadFileEntry> get(BulkUploadFileEntryQuery query) {
		BulkUploadFileEntryDAO bulkUploadFileEntryDAO = DAOFactory.getInstance().getBulkUploadFileEntryDAO();
		return bulkUploadFileEntryDAO.get(query);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void save(BulkUploadFileEntry bulkUploadFileEntry) {
		BulkUploadFileEntryDAO bulkUploadFileEntryDAO = DAOFactory.getInstance().getBulkUploadFileEntryDAO();
		 bulkUploadFileEntryDAO.save(bulkUploadFileEntry);
	}

}
