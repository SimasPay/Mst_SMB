/**
 * 
 */
package com.mfino.scheduler.service;

/**
 * Bulk Upload service to upload data.
 * 
 * @author Chaitanya
 *
 */
public interface BulkUploadSchedulerService extends BaseService {
	public void uploadData() throws Throwable;
}
