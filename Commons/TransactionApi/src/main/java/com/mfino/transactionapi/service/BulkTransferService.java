/**
 * 
 */
package com.mfino.transactionapi.service;

import java.io.IOException;
import java.math.BigDecimal;

import com.mfino.dao.BulkUploadDAO;
import com.mfino.domain.BulkUpload;

/**
 * @author Shashank
 *
 */
public interface BulkTransferService {

	void failTheBulkTransfer(BulkUpload bulkUpload,
			String string);

	BulkUpload processBulkTransferData(BulkUpload bulkUpload) throws IOException;

	void sendEmailBulkUploadSummary(BulkUpload bulkUpload,
			BigDecimal moneyAvailbleBeforeTheJob,
			BigDecimal moneyAvailbleAfterTheJob);

	void sendNotification(BulkUpload bulkUpload,
			Integer notificationcodeBulktransfercompletedtopartner);

}
