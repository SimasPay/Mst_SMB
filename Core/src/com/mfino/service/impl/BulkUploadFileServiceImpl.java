package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadFileDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUploadFile;
import com.mfino.service.BulkUploadFileService;

@Service("BulkUploadFileServiceImpl")
public class BulkUploadFileServiceImpl implements BulkUploadFileService {
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<BulkUploadFile> getPendingFiles(){
		BulkUploadFileDAO bulkUploadFileDAO = DAOFactory.getInstance().getBulkUploadFileDAO();
		return bulkUploadFileDAO.getPendingFiles();
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(BulkUploadFile bulkUploadFile){
		BulkUploadFileDAO bulkUploadFileDAO = DAOFactory.getInstance().getBulkUploadFileDAO();
		bulkUploadFileDAO.save(bulkUploadFile);
	}
}
