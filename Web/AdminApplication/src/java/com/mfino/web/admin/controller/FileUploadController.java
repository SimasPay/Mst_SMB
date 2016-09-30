package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.View;

import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMTransfersBulkUpload;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;

@Controller
public class FileUploadController extends MultixCommunicationHandler {
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final int one = 1;
    private final int two = 2;
    private final int three = 3;
    private final int four = 4;
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
    @RequestMapping("/upload.htm")
    protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
    	return doUpload(request);
    }

    public CFIXMsg process(CFIXMsg msg) {
        // We dont do anything
        return msg;
    }

    private View doUpload(HttpServletRequest request) throws HibernateException {
        String file_name = null;
        String content = null;
        int linecount = 0;
        String mdn = null;
        String strLine = null;
        HashMap<String, Object> map = new HashMap<String, Object>();
        long totalLineCount = 0;
        String fileType = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        long totalHashTotalInFile = 0;
        BigDecimal totalAmountInFile = BigDecimal.ZERO;
        long totalHashTotal = 0;
        String dateInFile = null;
        try {
            
            mdn = request.getParameter("mdn");
            SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(mdn);
            if (subscriberMDN == null) {
                log.error("BulkUpload error: cannot find MDN record for id " + mdn);
                map.put("Error", String.format(MessageText._("Cannot find MDN record for the given ID") + (" %s"), mdn));
                map.put("success", false);
                return new JSONView(map);
            }
            if (subscriberMDN.getSubscriber() == null || subscriberMDN.getSubscriber().getMfinoUserByUserid() == null) {
                log.error("BulkUpload error: data inconsistant with MDN record for id " + mdn);
                map.put("Error", String.format(MessageText._("Data inconsistant with MDN record for the given ID") + "%s." + MessageText._(" Please note down this ID and contact system administrator."), mdn));
                map.put("success", false);
                return new JSONView(map);
            }

            if (request instanceof DefaultMultipartHttpServletRequest) {
                // this is what we need
                DefaultMultipartHttpServletRequest realRequest = (DefaultMultipartHttpServletRequest) request;
                @SuppressWarnings("unchecked")
				Iterator<String> filenames = realRequest.getFileNames();
                while (filenames.hasNext()) {
                    String filename = filenames.next();
                    MultipartFile file = realRequest.getFile(filename);
                    int fileSzMB = Integer.parseInt(ConfigurationUtil.getUploadFileSizeLimit());
                    long maxFileSizeAllowed = fileSzMB * 1048576;
                    if (file.getSize() > maxFileSizeAllowed) {
                        map.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), fileSzMB));
                        map.put("success", false);
                        return new JSONView(map);
                    }
                    file_name = file.getOriginalFilename();
                    content = new String(file.getBytes());
                    // Parsing the file for validations
                    ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
                    BufferedReader br = new BufferedReader(new InputStreamReader(bis));
                    while ((strLine = br.readLine()) != null) {
                        if (strLine.trim().length() == 0) {
                            map.put("Error", String.format(MessageText._("Sorry, File contain Empty Lines")));
                            map.put("success", false);
                            return new JSONView(map);
                        }
                        linecount++;
                        String[] result = strLine.split(",");
                        if (linecount == one && result.length == three) {
                            dateInFile = result[0];
                            totalLineCount = (long) Long.parseLong(result[1]);
                            fileType = result[2];
                        }
                        if (linecount == one && result.length == four) {
                            dateInFile = result[0];
                            totalLineCount = (long) Long.parseLong(result[1]);
                            fileType = result[2];
                        }
                        if (linecount == two && result.length == four) {
                            totalAmountInFile = new BigDecimal(result[1]);
                            totalHashTotalInFile = (long) Long.parseLong(result[2]);
                        }
                        if (linecount > two && result.length == four) {
//                            totalAmount += (long) Long.parseLong(result[1]);
                        	totalAmount = totalAmount.add(new BigDecimal(result[1]));                        	
                            totalHashTotal += (long) Long.parseLong(result[2]);
                        }
                        if ((linecount == one && result.length < three) || (linecount == one && result.length > four)) {
                            map.put("Error", String.format(MessageText._("Sorry, File contents are invalid")));
                            map.put("success", false);
                            return new JSONView(map);
                        }
                        if (linecount > one && (result.length != four)) {
                            map.put("Error", String.format(MessageText._("Sorry, File contents are invalid")));
                            map.put("success", false);
                            return new JSONView(map);
                        }
                    }
                }
                int fileTypeInt = -1;
                if (fileType != null) {
                    if (fileType.trim().equals("Transfer")) {
                        fileTypeInt = CmFinoFIX.BulkUploadFileType_AirtimeTransfer;
                    } else if (fileType.trim().equals("Remittance")) {
                        fileTypeInt = CmFinoFIX.BulkUploadFileType_BankAccountTransfer;
                    } else if (fileType.trim().equals("Topup")) {
                        fileTypeInt = CmFinoFIX.BulkUploadFileType_Topup;
                    } else {
                        fileTypeInt = CmFinoFIX.BulkUploadFileType_eMoneyTransfer;
                    }
                } else {
                    map.put("Error", String.format(MessageText._("Sorry, File Type is not Specified in the Uploaded File")));
                    map.put("success", false);
                    return new JSONView(map);
                }
                int enteredLineCount = Integer.parseInt(request.getParameter("filenooftransactions"));
                BigDecimal enteredTotalAmount = new BigDecimal(request.getParameter("filetotalamount"));
                int enteredFileType = Integer.parseInt(request.getParameter("bulkfiletype"));
                String enterFileTypeText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_BulkUploadFileType, null, enteredFileType);
                String monthcheck;
                String datecheck;
                if(dateInFile.length() != 8) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry upload failed. Invalid Date format")));
                    return new JSONView(map);
                }
                try {
                    monthcheck =dateInFile.substring(4, 6);
                    datecheck =dateInFile.substring(6, 8);
                } catch(StringIndexOutOfBoundsException indexOutofBoundsExp) {
                	String message =  String.format(MessageText._("Sorry upload failed. Invalid Date format"));
                	log.error(message, indexOutofBoundsExp);
                    map.put("success", false);
                    map.put("Error",message);
                    return new JSONView(map);
                }
                int monthNumber;
                int dateNumber;
                try{
                    monthNumber = Integer.parseInt(monthcheck);
                    dateNumber = Integer.parseInt(datecheck);
                }catch(NumberFormatException numberFormatExp){
                    String message = String.format(MessageText._("Sorry, Invalid Date: Expecting Date in Number Format"));
                    log.error(message, numberFormatExp);
                	map.put("success", false);
                    map.put("Error", message);
                    return new JSONView(map);
                }

                if (totalLineCount != (linecount - 2)) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Total LineCount does not match. Expecting ") + (linecount - 2) + MessageText._(" but got ") + totalLineCount));
                    return new JSONView(map);
                } else if (totalLineCount != enteredLineCount) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Entered LineCount does not match. Expecting ") + (linecount - 2) + MessageText._(" but got ") + enteredLineCount));
                    return new JSONView(map);
//                } else if (totalAmountInFile != totalAmount) {
                } else if (totalAmountInFile.compareTo(totalAmount) != 0) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Total Amount does not match. Expecting ") + totalAmount + MessageText._(" but got ") + totalAmountInFile));
                    return new JSONView(map);
//                } else if (totalAmount != enteredTotalAmount) {
                } else if (totalAmount.compareTo(enteredTotalAmount) != 0) {                    
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Entered Amount does not match. Expecting ") + totalAmountInFile + MessageText._(" but got ") + enteredTotalAmount));
                    return new JSONView(map);
                } else if (totalHashTotalInFile != totalHashTotal) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: HashTotal does not match. Expecting ") + totalHashTotal + MessageText._(" but got ") + totalHashTotalInFile));
                    return new JSONView(map);
                } else if (enteredFileType != fileTypeInt) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Entered filetype does not match. Expecting") + "( " + fileType + " ) " + MessageText._("but got ") + " (" + enterFileTypeText + " )"));
                    return new JSONView(map);
                } else if (dateInFile.length() != 8) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Entered date is not a valid format. Format is YYYYMMDD")));
                    return new JSONView(map);
                }else if (dateInFile.length() == 8 && monthNumber >12 ) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Entered date is not a valid format. Format is YYYYMMDD")));
                    return new JSONView(map);
                } else if (dateInFile.length() == 8 && dateNumber >31 ) {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry, Invalid file: Entered date is not a valid format. Format is YYYYMMDD")));
                    return new JSONView(map);
                }else {
                    CMTransfersBulkUpload toBeSentMsg = new CmFinoFIX.CMTransfersBulkUpload();
                    toBeSentMsg.setDescription(request.getParameter("filedescription"));
                    toBeSentMsg.setUserID(subscriberMDN.getSubscriber().getMfinoUserByUserid().getId().longValue());
                    toBeSentMsg.setSourceMDN(mdn);
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    String userName = (auth != null) ? auth.getName() : " ";
                    toBeSentMsg.setUserName(userName);
                    toBeSentMsg.setFileName(file_name);
                    toBeSentMsg.setFileData(content);
                    toBeSentMsg.setFileType(enteredFileType);
                    toBeSentMsg.setDeliveryDate(Timestamp.fromString(request.getParameter("filedeliverydate")));
                    toBeSentMsg.setTransactionsCount((int) totalLineCount);
                    toBeSentMsg.setTotalAmount(totalAmount);
                    toBeSentMsg.setVerificationChecksum(totalHashTotal);
                    toBeSentMsg.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);
                    toBeSentMsg.setSourceApplication(CmFinoFIX.SourceApplication_Web);
                    toBeSentMsg.setMSPID(1L); // TODO: Define a constat or find one if
                    // already exists for Smart
                    if (fileTypeInt == CmFinoFIX.BulkUploadFileType_BankAccountTransfer) {
                        if (request.getParameter("checkNumber") != null && request.getParameter("bankBranchCode") != null) {
                            toBeSentMsg.setCheckNum(request.getParameter("checkNumber"));
                            toBeSentMsg.setBankBranchName(request.getParameter("bankBranchCode"));
                        }
                    }
                    CmFinoFIX.CMJSError errorMsg = (CMJSError) handleRequestResponse(toBeSentMsg);
                    if (errorMsg.getErrorCode() == CmFinoFIX.ErrorCode_NoError) {
                        map.put("success", true);
                        map.put("file", file_name);
                        return new JSONView(map);
                    } else {
                        map.put("Error", errorMsg.getErrorDescription());
                        map.put("success", false);
                        return new JSONView(map);
                    }
                }
            }
        } catch (Throwable throwable) {

            log.error("Error in FileUploadController", throwable);
            map.put("success", false);
            map.put("Error", String.format(MessageText._("Sorry," + throwable.toString())));
        } 
        
        return new JSONView(map);
    }
}
