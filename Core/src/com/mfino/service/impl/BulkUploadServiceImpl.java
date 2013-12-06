package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.BulkUploadEntryDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkUploadQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.service.BulkUploadService;

@Service("BulkUploadServiceImpl")
public class BulkUploadServiceImpl implements BulkUploadService{
	
	/**
	 * Saves the BulkUpload record to database 
	 * @param bulkUpload
	 * @throws MfinoRuntimeException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void save(BulkUpload bulkUpload) throws MfinoRuntimeException
	{
		BulkUploadDAO bulkUploadDao = DAOFactory.getInstance().getBulkUploadDAO();
		bulkUploadDao.save(bulkUpload);
	}
	/**
	 * Saves the BulkUpload record to database 
	 * @param bulkUpload
	 * @throws MfinoRuntimeException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<BulkUpload> getByQuery(BulkUploadQuery bulkUploadQuery) throws MfinoRuntimeException
	{
		BulkUploadDAO bulkUploadDao = DAOFactory.getInstance().getBulkUploadDAO();
		return bulkUploadDao.get(bulkUploadQuery);
	}
	/**
	 * Saves the BulkUpload record to database 
	 * @param bulkUpload
	 * @throws MfinoRuntimeException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public BulkUpload getByReverseSCTLId(long sctlId)
	{
		BulkUploadDAO bulkUploadDao = DAOFactory.getInstance().getBulkUploadDAO();
		return bulkUploadDao.getByReverseSCTLId(sctlId);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public BulkUpload getBySCTLId(long sctlId){
		BulkUploadDAO bulkUploadDao = DAOFactory.getInstance().getBulkUploadDAO();
		return bulkUploadDao.getBySCTLId(sctlId);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public BulkUpload getById(long bulkTransferId) {
		 BulkUploadDAO bulkUploadDao = DAOFactory.getInstance().getBulkUploadDAO();
		return bulkUploadDao.getById(bulkTransferId);
	}
}
