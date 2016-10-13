/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.scheduler.upload.service.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.domain.BulkUploadFile;
import com.mfino.domain.BulkUploadFileEntry;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.Timestamp;
import com.mfino.scheduler.upload.service.AgentBulkUploadService;
import com.mfino.service.BulkUploadFileEntryService;
import com.mfino.util.SubscriberSyncErrors;

/**
 * 
 * @author Maruthi
 */
@Service("AgentBulkUploadServiceImpl")
public class AgentBulkUploadServiceImpl 
		implements AgentBulkUploadService {

	private static Logger log = LoggerFactory.getLogger(AgentBulkUploadServiceImpl.class);
	private static final int FIELD_COUNT = 21;
	private String uploadedBy="";
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Autowired
	@Qualifier("BulkUploadFileEntryServiceImpl")
	private BulkUploadFileEntryService bulkUploadFileEntryService;
	
	public BulkUploadFile ProcessBulkUploadFile(BulkUploadFile bulkUploadFile) throws IOException {
		
		BulkUploadFileEntryQuery query = new BulkUploadFileEntryQuery();
		query.setUploadFileID(bulkUploadFile.getId().longValue());
		List<BulkUploadFileEntry> fileEntries = bulkUploadFileEntryService.get(query);
		Iterator<BulkUploadFileEntry> iterator = fileEntries.iterator();
		int processedCount = 0;
		int errorLineCount = 0;
		while(iterator.hasNext()) {
			BulkUploadFileEntry bulkUploadFileEntry = iterator.next();
			//process only the file records whose status is left initialized
			if(CmFinoFIX.BulkUploadFileEntryStatus_Initialized.equals(bulkUploadFileEntry.getBulkuploadfileentrystatus())) {
				processedCount++;
				Integer linenumber =(int) bulkUploadFileEntry.getLinenumber();
				String lineData = "";
				try {
					lineData = bulkUploadFileEntry.getLinedata().getSubString(0,((Long) bulkUploadFileEntry.getLinedata().length()).intValue());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				log.info("Processing record at line number: " + linenumber);
				//set the bulk upload file entry record status as Processing
				bulkUploadFileEntry.setBulkuploadfileentrystatus(CmFinoFIX.BulkUploadFileEntryStatus_Processing);
				bulkUploadFileEntryService.save(bulkUploadFileEntry);
				
				if(bulkUploadFile.getRecordtype().equals(CmFinoFIX.RecordType_Agent)){
					CMJSError response = createAgent(lineData, bulkUploadFile.getCreatedby());
					if (response.getErrorCode().equals(SubscriberSyncErrors.Success)) {
						log.info("Successfully created the Agent for record at line number: " + linenumber);
						bulkUploadFileEntry.setBulkuploadfileentrystatus(CmFinoFIX.BulkUploadFileEntryStatus_Completed);
					} else {
						log.info("Error while creating the Agent for record at line number: " + linenumber + " :"+ response.getErrorDescription());
						bulkUploadFileEntry.setBulkuploadfileentrystatus(CmFinoFIX.BulkUploadFileEntryStatus_Failed);
						bulkUploadFileEntry.setFailurereason(response.getErrorDescription());
						errorLineCount++;
					}
					bulkUploadFileEntryService.save(bulkUploadFileEntry);
				}
			}			
//			if (processedCount % 20 == 0) {
//				getHibernateSessionHolder().getSession().flush();
//				getHibernateSessionHolder().getSession().clear();
//			}
		}
		//update the processing info and set the status to Processed
		bulkUploadFile.setFileprocesseddate(new Timestamp());		
		bulkUploadFile.setErrorlinecount(Long.valueOf(errorLineCount));
		bulkUploadFile.setUploadfilestatus(CmFinoFIX.UploadFileStatus_Processed);		
		return bulkUploadFile;
	}

	private CMJSError createAgent(String strLine,String uploadedBy) {
		this.uploadedBy = uploadedBy;
		String strTokens[] = strLine.split(GeneralConstants.FIELD_SEPARATOR);
		if(strTokens.length==1)
        {
        	strTokens = strLine.split(GeneralConstants.FIELD_SEPARATOR_COMMA);
        }
		CMJSError response = new CMJSError();
		if (strTokens.length < FIELD_COUNT) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_Field_Count);
			response.setErrorDescription("Insufficient field count :"+ strTokens.length);
			log.error(String.format("Bad line format: %s", strLine));
			return response;
		} else {
			response.setErrorCode(SubscriberSyncErrors.Success);
		}
		return response;
	}

	
}
