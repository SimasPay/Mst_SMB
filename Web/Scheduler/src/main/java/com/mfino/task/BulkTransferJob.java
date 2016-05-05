/**
 * 
 */
package com.mfino.task;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.i18n.MessageText;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.BulkUploadService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.PocketService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.service.BulkTransferService;

/**
 * @author Bala Sunku
 *
 */
public class BulkTransferJob {

	private Logger log = LoggerFactory.getLogger(BulkTransferJob.class);
	private PocketService pocketService;
	private ChannelCodeService channelCodeService;
	private BulkUploadEntryService bulkUploadEntryService;
	private BulkTransferService btService;
	private BulkUploadService bulkuploadService;
	private SystemParametersService systemParametersService; 
	
	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}

	public void setSystemParametersService(SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	public BulkTransferService getBtService() {
		return btService;
	}

	public void setBtService(BulkTransferService btService) {
		this.btService = btService;
	}

	public BulkUploadService getBulkuploadService() {
		return bulkuploadService;
	}

	public void setBulkuploadService(BulkUploadService bulkuploadService) {
		this.bulkuploadService = bulkuploadService;
	}

	public ChannelCodeService getChannelCodeService() {
		return channelCodeService;
	}

	public void setChannelCodeService(ChannelCodeService channelCodeService) {
		this.channelCodeService = channelCodeService;
	}

	public BulkUploadEntryService getBulkUploadEntryService() {
		return bulkUploadEntryService;
	}

	public void setBulkUploadEntryService(BulkUploadEntryService bulkUploadEntryService) {
		this.bulkUploadEntryService = bulkUploadEntryService;
	}	

	public void doBulkTransfer(BulkUpload bulkUpload) {
		// change the Bulk upload file status to processing.
		bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Processing);
		bulkUpload.setDeliveryDate(new Timestamp());
		bulkuploadService.save(bulkUpload);

		try {
			bulkUpload = processBulkTransferData(bulkUpload);
		} catch (IOException e) {
			log.error("Could not able to process the uploaded Tranfer file for Bulk Transfer: " + bulkUpload.getID() + "." + e.getMessage(), e);
			String failureReason = e.getMessage().length() > 255 ? e.getMessage().substring(0, 255) : e.getMessage(); 
			btService.failTheBulkTransfer(bulkUpload, failureReason);
			return;
		}
		bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Complete);
		bulkUpload.setDeliveryDate(new Timestamp());
		bulkuploadService.save(bulkUpload);
		btService.sendEmailBulkUploadSummary(bulkUpload);	
	}

	private BulkUpload processBulkTransferData(BulkUpload bulkUpload) throws IOException {
		int successCount = 0;
		int i = 0;
		BigDecimal successAmount = BigDecimal.ZERO;

        long srcPocketId = systemParametersService.getLong(SystemParameterKeys.INTEREST_COMMISSION_FUNDING_POCKET_ID);
        Pocket srcPocket = pocketService.getById(srcPocketId);
		
		ChannelCode channelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		
		List<BulkUploadEntry> bulkUploadEntries = bulkUploadEntryService.getBulkUploadEntriesForBulkUpload(bulkUpload.getID());

		for (BulkUploadEntry bue:bulkUploadEntries) {
			try {
				i++;
				boolean flag = btService.processEntry(bue, bulkUpload, srcPocket, channelCode, i);
				if (flag) {
					successCount ++;
					successAmount = successAmount.add(bue.getAmount());
				}
			} catch (Exception e) {
				log.error("Error: While processing the line number " + i + " for Bulk Transfer --> " + bulkUpload.getID(), e);
			}
		}

		bulkUpload.setSuccessAmount(successAmount);
		int failedCount = bulkUpload.getTransactionsCount().intValue() - successCount;
		bulkUpload.setFailedTransactionsCount(failedCount);

		return bulkUpload;
	}
}
