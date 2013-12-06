/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections.CollectionUtils;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.BulkUploadFileDAO;
import com.mfino.dao.BulkUploadFileEntryDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.domain.BulkUploadFile;
import com.mfino.domain.BulkUploadFileEntry;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkUploadFileEntry;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.BulkUploadFileEntryProcessor;

/**
 *
 * @author Raju
 */
@Service("BulkUploadFileEntryProcessorImpl")
public class BulkUploadFileEntryProcessorImpl extends BaseFixProcessor implements BulkUploadFileEntryProcessor{

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
    
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
        CmFinoFIX.CMJSBulkUploadFileEntry realMsg = (CmFinoFIX.CMJSBulkUploadFileEntry) msg;        
        BulkUploadFileEntryDAO bulkUploadFileEntryDAO = DAOFactory.getInstance().getBulkUploadFileEntryDAO();
        BulkUploadFileDAO bulkUploadFileDAO = DAOFactory.getInstance().getBulkUploadFileDAO();
        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            BulkUploadFileEntryQuery query = new BulkUploadFileEntryQuery();
            int i=0;
            if (realMsg.getIDSearch() != null) {
                query.setUploadFileID(realMsg.getIDSearch());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            List<BulkUploadFileEntry> lst = bulkUploadFileEntryDAO.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				// For processing files that are uploaded after Bulk upload refactoring.
				log.info("BulkUploadFileEntry lst size is " + lst.size() + ", Processing upload file entries from BulkUploadFileEntry table");
				realMsg.allocateEntries(lst.size());
				for (BulkUploadFileEntry fileEntry: lst){					
					CMJSBulkUploadFileEntry.CGEntries e = new CMJSBulkUploadFileEntry.CGEntries();
					updateMessage(fileEntry, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
				realMsg.setsuccess(CmFinoFIX.Boolean_True);
	        	realMsg.settotal(query.getTotal());
        	} else {
        		// For processing files that are uploaded before Bulk upload refactoring.
        		log.info("BulkUploadFileEntry lst is empty, hence processing upload file entries from BulkUploadFile table");        		
        		BulkUploadFile s = bulkUploadFileDAO.getById(realMsg.getIDSearch());
        		int total = 0;        		
        		if (s != null) {        			 
                     String filedata = s.getUploadReport();
                     //if filedata is null means it's not processed so  we simply return the realmsg.
                     if (filedata == null) {
                         return realMsg;
                     }
                     total = s.getTotalLineCount();
                     log.info("totalLineCount as per BulkUploadFile filedata is " + total);
                     realMsg.allocateEntries(s.getTotalLineCount());
                     BufferedReader bufferedReader = new BufferedReader(new StringReader(filedata));
                     String strLine = null;
                     try {
                    	 for (i = 0; i < s.getTotalLineCount() && (strLine = bufferedReader.readLine()) != null; i++) {
                             CmFinoFIX.CMJSBulkUploadFileEntry.CGEntries entry =
                                     new CmFinoFIX.CMJSBulkUploadFileEntry.CGEntries();
                             String input[] = strLine.split("\\|"); // Pipe is a special character.
                             if(input.length==1)
                             {
                             	input = strLine.split(GeneralConstants.COMMA_STRING);
                             }                             
                             String sbyte[] = strLine.split(GeneralConstants.RESPONSE_SEPARATOR);
                             if(sbyte.length==1)
                             {
                             	sbyte = strLine.split(GeneralConstants.COMMA_STRING);
                             }                             
                             entry.setID((long) i + 1);
                             entry.setLineNumber(i + 1);
                             // entry.setRecordData(strLine);
                             if (input.length > 2) {
                                 if (CmFinoFIX.RecordType_Agent.equals(s.getRecordType())) {
                                     entry.setMDN(input[0]);
                                 } else {
                                     entry.setMDN(input[2]);
                                 }                       
                             }
                             String failureReason = sbyte[sbyte.length - 1];
                             entry.setRecordMessage(failureReason!= null && failureReason.equals("Success") ? null : failureReason); 
                             String statusText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_SynchError, null, sbyte[sbyte.length - 2]);
                             Integer fileEntryStatus = null;
                             if(statusText != null && statusText.equals("Success")) {
                            	 fileEntryStatus = CmFinoFIX.BulkUploadFileEntryStatus_Completed;
                             } else {
                            	 fileEntryStatus = CmFinoFIX.BulkUploadFileEntryStatus_Failed;                            	 
                             }
                             entry.setRecordStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadFileEntryStatus, null, fileEntryStatus));
                             realMsg.getEntries()[i] = entry;
                             
                             //Updating the above file entries in BulkUploadFileEntry table so that we can neglect 
                             //filedata column value going further 
                             BulkUploadFileEntry bulkUploadFileEntry = new BulkUploadFileEntry();
                             bulkUploadFileEntry.setBulkUploadFile(s);
                             bulkUploadFileEntry.setLineData(sbyte[0]);
                             bulkUploadFileEntry.setLineNumber(i+1);                             
                             bulkUploadFileEntry.setFailureReason(sbyte[sbyte.length - 1]);
                             bulkUploadFileEntry.setBulkUploadFileEntryStatus(fileEntryStatus);
                             bulkUploadFileEntryDAO.save(bulkUploadFileEntry);
                         }
                     } catch (IOException e) {
						log.error("Error while processing file data from BulkUploadFile table" + e);
					}
                     
        		}
        		realMsg.setsuccess(CmFinoFIX.Boolean_True);
	        	realMsg.settotal(total);
        	}
        }
        return realMsg;
    }
    
    private void updateMessage(BulkUploadFileEntry fileEntry, CMJSBulkUploadFileEntry.CGEntries e) {
    	e.setID(fileEntry.getID());
    	String strLine = fileEntry.getLineData();
    	e.setLineNumber(fileEntry.getLineNumber());
    	if(strLine != null) {
    		String input[] = strLine.split("\\|"); // Pipe is a special character.
            if(input.length==1)
            {
            	input = strLine.split(GeneralConstants.COMMA_STRING);
            }
            if (input.length > 2) {
                if (CmFinoFIX.RecordType_Agent.equals(fileEntry.getBulkUploadFile().getRecordType())) {
                    e.setMDN(input[0]);
                } else {
                    e.setMDN(input[2]);
                }                       
            }
    	}
    	e.setRecordMessage(fileEntry.getFailureReason()); 
    	e.setRecordStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadFileEntryStatus, null, fileEntry.getBulkUploadFileEntryStatus()));
    }
}
