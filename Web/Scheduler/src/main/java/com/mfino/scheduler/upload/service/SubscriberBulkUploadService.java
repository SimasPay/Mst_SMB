/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.scheduler.upload.service;

import java.io.IOException;

import com.mfino.domain.BulkUploadFile;
import com.mfino.scheduler.service.BaseService;


/**
 * 
 * @author Maruthi
 */
public interface SubscriberBulkUploadService extends BaseService{

	public BulkUploadFile ProcessBulkUploadFile(BulkUploadFile bulkUploadFile) throws IOException;
	
}
