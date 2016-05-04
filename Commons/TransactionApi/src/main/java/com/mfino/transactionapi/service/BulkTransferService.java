/**
 * 
 */
package com.mfino.transactionapi.service;

import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;

/**
 * @author Shashank
 *
 */
public interface BulkTransferService {

	public boolean processEntry(BulkUploadEntry bue, BulkUpload bulkUpload, Pocket srcPocket, ChannelCode channelCode, int i);

	void failTheBulkTransfer(BulkUpload bulkUpload, String string);

	void sendEmailBulkUploadSummary(BulkUpload bulkUpload);

	void sendNotification(BulkUpload bulkUpload, Integer notificationcodeBulktransfercompletedtopartner);	

}
