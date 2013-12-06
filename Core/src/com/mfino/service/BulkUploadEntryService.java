/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.domain.BulkUploadEntry;

/**
 * @author Sreenath
 *
 */
public interface BulkUploadEntryService {
	/**
	 * 
	 * @param sctlID
	 * @return
	 */
	public BulkUploadEntry getBulkUploadEntryBySctlID(Long sctlID);

	/**
	 * Saves the BulkUploadEntry to the database
	 * @param bulkUploadEntry
	 */
	public void saveBulkUploadEntry(BulkUploadEntry bulkUploadEntry);
	
	/**
	 * Get Bulk upload Entries for Bulk upload
	 * @param bulkUploadEntry
	 */
	public List<BulkUploadEntry> getBulkUploadEntriesForBulkUpload(Long bulkUploadEntryId);
}
