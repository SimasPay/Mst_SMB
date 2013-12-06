/**
 * 
 */
package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.View;

import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BulkUploadFileProcessor;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Deva
 * 
 */
@Controller
public class BulkUploadFileController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    @Qualifier("BulkUploadFileProcessorImpl")
    private BulkUploadFileProcessor bulkUploadFileProcessor;
    
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

    /*
     * Handle the bulk upload of subscriber and merchant records.
     * This will support only one file per request.
     *
     */
    @RequestMapping("/uploadsubscribers.htm")
    protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Subscriber Bulk upload started at " + new Date());

        int recordType = -1;
        int count = -1;

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("success", false);
        //
        if (request instanceof MultipartHttpServletRequest) {
            int maxFileSizeMB = Integer.parseInt(ConfigurationUtil.getUploadFileSizeLimit());
            long maxFileSize = maxFileSizeMB * 1024L * 1024L;
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            @SuppressWarnings("unchecked")
            Iterator<String> fileNames = multipartRequest.getFileNames();
            try {
                if (authorizationService.isAuthorized(CmFinoFIX.Permission_Bulk_Upload_Add)) {
                    while (fileNames.hasNext()) {
                        String fileName = (String) fileNames.next();
                        MultipartFile file = multipartRequest.getFile(fileName);
                        if (file == null || file.getSize() == 0) {
                            responseMap.put("Error", "");
                        }else if(!file.getOriginalFilename().endsWith(".csv")){
                        	responseMap.put("Error", MessageText._("Please upload CSV file"));
                        } else if (file.getSize() > maxFileSize) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), maxFileSizeMB));
                        } else {
                            String recordTypeStr = multipartRequest.getParameter("RecordType");
                            recordType = Integer.parseInt(recordTypeStr);
//                            count = Integer.parseInt(multipartRequest.getParameter("RecordCount"));
                            //check the line count before we move to add the subscribers or merchants.
                            ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
                            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
                            String strLine;
                            int linecount = 0;
                            while ((strLine = br.readLine()) != null) {
                                if (strLine.trim().length() == 0) {
                                    responseMap.put("success", false);
                                    responseMap.put("Error", String.format(MessageText._("Sorry, File contain Empty Lines")));
                                    return new JSONView(responseMap);
                                }
                                linecount++;
                            }
                            if (linecount <=0) {
                                responseMap.put("success", false);
                                responseMap.put("Error", String.format(MessageText._("Sorry, Invalid file: Total LineCount is 0.")));
                                return new JSONView(responseMap);
                            }

                            String desc = multipartRequest.getParameter("Description");
                            bulkUploadFileProcessor.processFileData(file,recordType, linecount, desc);
                            responseMap.put("success", true);
                            responseMap.put("file", file.getOriginalFilename());
                            responseMap.put("recordType", enumTextService.getEnumTextValue(CmFinoFIX.TagID_RecordType, CmFinoFIX.Language_English, recordType));
                            responseMap.put("totalCount", linecount);
                        }
                    }
                } else {
                    responseMap.put("success", false);
                    responseMap.put("Error", String.format(MessageText._("You are not authorized to perform this operation.")));
                    return new JSONView(responseMap);
                }
            } catch (Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
               
                responseMap.put("Error", "Sorry," + throwable.toString());
            } 
        }
        return new JSONView(responseMap);
    }
}
