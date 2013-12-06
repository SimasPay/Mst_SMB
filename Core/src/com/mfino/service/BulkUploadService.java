/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.BulkUploadQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.exceptions.MfinoRuntimeException;

/**
 * @author Sreenath
 *
 */
public interface BulkUploadService {

	/**
	 * Saves the BulkUpload record to database 
	 * @param bulkUpload
	 * @throws MfinoRuntimeException
	 */
	public void save(BulkUpload bulkUpload) throws MfinoRuntimeException;
	public List<BulkUpload> getByQuery(BulkUploadQuery bulkUploadQuery) throws MfinoRuntimeException;
	
	public BulkUpload getByReverseSCTLId(long sctlId);
	public BulkUpload getBySCTLId(long sctlId);
	public BulkUpload getById(long bulkTransferId);
}
