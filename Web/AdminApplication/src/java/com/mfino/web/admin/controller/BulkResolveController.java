/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialClob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.View;

import com.mfino.domain.PendingTxnsFile;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;
import com.mfino.service.PendingTransactionsService;
import com.mfino.service.UserService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author Raju
 */
@Controller
public class BulkResolveController {

    private Logger log = LoggerFactory.getLogger(BulkResolveController.class);
    private final int one = 1;
    private final int two = 2;
    
	@Autowired
	@Qualifier("PendingTransactionsServiceImpl")
	private PendingTransactionsService pendingTransactionsService;


	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    @RequestMapping("/bulkresolve.htm")
    protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("success", false);
        if (request instanceof MultipartHttpServletRequest) {
            int maxFileSizeMB = Integer.parseInt(ConfigurationUtil.getUploadFileSizeLimit());
            long maxFileSize = maxFileSizeMB * 1024L * 1024L;
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            @SuppressWarnings("unchecked")
			Iterator<String> fileNames = multipartRequest.getFileNames();
            try {
                while (fileNames.hasNext()) {
                    String fileName = (String) fileNames.next();
                    MultipartFile file = multipartRequest.getFile(fileName);
                    if (file == null || file.getSize() == 0) {
                        responseMap.put("Error", "File is Empty");
                    } else if (file.getSize() > maxFileSize) {
                        responseMap.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), maxFileSizeMB));
                    } else {
                        String description = multipartRequest.getParameter("Description");
                        String status = multipartRequest.getParameter("markAs");
                        String noOfTransactionsStr = multipartRequest.getParameter("filenooftransactions");
                        int noOfTransactions = Integer.parseInt(noOfTransactionsStr);
                        //String descriptionStr = multipartRequest.getParameter("Description");
                        //check the line count before we move to add the subscribers or merchants.
                        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
                        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
                        int linecount = 0;
                        String strLine;
                        String filedescription = null;
                        String fileStatus = null;
                        int fileStatusint;
                        while ((strLine = br.readLine()) != null) {
                            linecount++;
                            if (strLine.trim().length() == 0) {
                                responseMap.put("success", false);
                                responseMap.put("Error", String.format(MessageText._("Sorry, File contain Empty Lines")));
                                return new JSONView(responseMap);
                            }
                            String[] result = strLine.split(",");
                            if (linecount == one && result.length == two) {
                                filedescription = result[0].trim();
                                fileStatus = result[1].trim();
                            } else if (linecount == one && result.length != two) {
                                responseMap.put("Error", String.format(MessageText._("Sorry, File contents are invalid")));
                                responseMap.put("success", false);
                                return new JSONView(responseMap);
                            }
                            if(!filedescription.equalsIgnoreCase("Resolve")){
                                responseMap.put("Error", String.format(MessageText._("Sorry, File contents are invalid")));
                                responseMap.put("success", false);
                                return new JSONView(responseMap);
                            }
                        }
                        if (!fileStatus.equalsIgnoreCase(status)) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, file status doesnot match with entered file status")));
                            responseMap.put("success", false);
                            return new JSONView(responseMap);
                        }
                        if (linecount-1 != noOfTransactions) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, file count doesnot match with entered file count")));
                            responseMap.put("success", false);
                            return new JSONView(responseMap);
                        }
                        if(fileStatus.equalsIgnoreCase("successful")){
                            fileStatusint = CmFinoFIX.ResolveAs_success;
                        } else if (fileStatus.equalsIgnoreCase("fail")){
                            fileStatusint = CmFinoFIX.ResolveAs_failed;
                        } else {
                        	responseMap.put("Error", String.format(MessageText._("Sorry, Invalid file status has been provided. Please check and try again")));
                            responseMap.put("success", false);
                            return new JSONView(responseMap);
                        }
                        String fileData = new String(file.getBytes());
                        PendingTxnsFile fileToSave = new PendingTxnsFile();
                        fileToSave.setDescription(filedescription);
                        Clob clob = new SerialClob(fileData.toCharArray());
                        clob.setString(1, fileData);
                        fileToSave.setFiledata(clob);
                        fileToSave.setFilename(file.getOriginalFilename());
                       // fileToSave.setRecordType(CmFinoFIX.RecordType_Bulkresolve);
                        fileToSave.setRecordcount(Long.valueOf(linecount-1));
                        fileToSave.setResolveas(Long.valueOf(fileStatusint));
                        fileToSave.setUploadfilestatus(CmFinoFIX.UploadFileStatus_Uploaded);
                        fileToSave.setCompany(userService.getUserCompany());
                        
                        pendingTransactionsService.savePendingTransactions(fileToSave);
                        
                        responseMap.put("success", true);
                        responseMap.put("file", file.getOriginalFilename());
                    }
                }
            } catch (Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
                responseMap.put("Error", "Sorry," + throwable.toString());
            } 
        }
        return new JSONView(responseMap);
    }
}
