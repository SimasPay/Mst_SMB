/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadEntryDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.service.BulkUploadEntryService;

/**
 * @author Sreenath
 *
 */
@Service("BulkUploadEntryServiceImpl")
public class BulkUploadEntryServiceImpl implements BulkUploadEntryService{
	private static Logger log = LoggerFactory.getLogger(BulkUploadEntryServiceImpl.class);
	
	/**
	 * Gets the BulkUploadEntry by the sctlID
	 * @param sctlID
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public BulkUploadEntry getBulkUploadEntryBySctlID(Long sctlID){
		BulkUploadEntry bulkUploadEntry = null;
		if(sctlID!=null){
			log.info("Getting the BulkUploadEntry record with the sctlID: "+sctlID);
			BulkUploadEntryDAO bulkUploadEntryDAO = DAOFactory.getInstance().getBulkUploadEntryDAO();
			bulkUploadEntry = bulkUploadEntryDAO.getBySCTLId(sctlID);
		}
		return bulkUploadEntry;		
	}
	
	/**
	 * Saves the BulkUploadEntry to the database
	 * @param bulkUploadEntry
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void saveBulkUploadEntry(BulkUploadEntry bulkUploadEntry){
		BulkUploadEntryDAO bulkUploadEntryDAO = DAOFactory.getInstance().getBulkUploadEntryDAO();
		bulkUploadEntryDAO.save(bulkUploadEntry);
	}
	/**
	 * Gets the BulkUploadEntry by the sctlID
	 * @param sctlID
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<BulkUploadEntry> getBulkUploadEntriesForBulkUpload(Long bulkUploadEntryId){
		List<BulkUploadEntry> bulkUploadEntries = null;
		if(bulkUploadEntryId!=null){
			log.info("Getting the BulkUploadEntry record with the Id: "+bulkUploadEntryId);
			BulkUploadEntryDAO bulkUploadEntryDAO = DAOFactory.getInstance().getBulkUploadEntryDAO();
			bulkUploadEntries = bulkUploadEntryDAO.getBulkUploadEntriesForBulkUpload(bulkUploadEntryId);
		}
		return bulkUploadEntries;		
	}

}
