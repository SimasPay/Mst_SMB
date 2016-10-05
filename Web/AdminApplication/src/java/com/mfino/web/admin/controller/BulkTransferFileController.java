package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.View;

import com.mfino.dao.query.UserQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.RolePermission;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.BulkUploadService;
import com.mfino.service.MailService;
import com.mfino.service.PermissionService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberService;
import com.mfino.service.UserService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.EncryptionUtil;

/**
 * @author Bala Sunku
 * 
 */
@Controller
public class BulkTransferFileController {
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;


	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;


	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, Object> responseMap = new HashMap<String, Object>();;
    private String DELIMITOR = ",";
    
    @Autowired
    @Qualifier("PermissionServiceImpl")
    private PermissionService permissionService;
    
    @RequestMapping("/bulkTransfer.htm")
    protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
    	if (request instanceof MultipartHttpServletRequest) {
    		boolean isAuthorized = true; //Authorization.isAuthorized(CmFinoFIX.Permission_Bulk_Upload_Add)
    		if (isAuthorized) { 
    			return doUpload(request);
    		} else {
                responseMap.put("success", false);
                responseMap.put("Error", MessageText._("You are not authorized to perform this operation."));
        		return new JSONView(responseMap);
    		}
    	} else {
            responseMap.put("success", false);
            responseMap.put("Error", MessageText._("Invalid request"));
    		return new JSONView(responseMap);
    	}
    	
    }
    
    protected View doUpload(HttpServletRequest request) {

        int fileType = CmFinoFIX.BulkUploadFileType_eMoneyTransfer;
        int count = -1;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        responseMap.put("success", false);

        int maxFileSizeMB = Integer.parseInt(ConfigurationUtil.getUploadFileSizeLimit());
        long maxFileSize = maxFileSizeMB * 1024L * 1024L;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartRequest.getFileNames();
        try {
            while (fileNames.hasNext()) {
                String fileName = fileNames.next();
                MultipartFile file = multipartRequest.getFile(fileName);
                if (file == null || file.getSize() == 0) {
                    responseMap.put("Error", "Empty File");
                } 
                else if (file.getSize() > maxFileSize) {
                    responseMap.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), maxFileSizeMB));
                }
                else {
                    count = Integer.parseInt(multipartRequest.getParameter("TransactionsCount"));
                    
                    //Parse each line of the file and check the line count
                    ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
                    BufferedReader br = new BufferedReader(new InputStreamReader(bis));
                    String strLine;
                    int linecount = 0;
                    while ((strLine = br.readLine()) != null) {
                        if (strLine.trim().length() == 0) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, File contain Empty Lines")));
                            return new JSONView(responseMap);
                        }
                        linecount++;
                        String[] line = strLine.split(DELIMITOR);
                        if (line.length != 4) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, Invalid data at line number: " + linecount)));
                            return new JSONView(responseMap);
                        }
                        if (StringUtils.isBlank(line[0]) ) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, First Name is blank at line number: " + linecount)));
                            return new JSONView(responseMap);
                        }
                        if (StringUtils.isBlank(line[1]) ) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, Last Name is blank at line number: " + linecount)));
                            return new JSONView(responseMap);
                        }
                        if (StringUtils.isBlank(line[2]) || !(line[2].length() == 10 || line[2].length() == 13) || !(line[2].matches("((-|\\+)?[0-9])+")) ) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, Invalid MDN at line number: " + linecount)));
                            return new JSONView(responseMap);
                        }
                        if (StringUtils.isBlank(line[3]) || !(line[3].matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) ) {
                            responseMap.put("Error", String.format(MessageText._("Sorry, Invalid Amount at line number: " + linecount)));
                            return new JSONView(responseMap);
                        }

                        totalAmount = totalAmount.add(new BigDecimal(line[3]));
                    }
                    if (linecount != count) {
                        responseMap.put("Error", String.format(MessageText._("Sorry, Invalid file, Total Line Count does not match.")));
                        return new JSONView(responseMap);
                    }

                    BulkUpload bulkUpload = new BulkUpload();
                    
                    String sourcePocket = multipartRequest.getParameter("SourcePocket");
                    String pin = multipartRequest.getParameter("Pin");
                    String encryptedPin = EncryptionUtil.getEncryptedString(pin);
                    String paymentDateStr = multipartRequest.getParameter("PaymentDate");
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date paymentDate = sdf.parse(paymentDateStr);
                    User loggedInUser = userService.getCurrentUser();
                    Timestamp currentTime = new Timestamp();
                    
                    bulkUpload.setCompany(loggedInUser.getCompany());
                    bulkUpload.setDescription(multipartRequest.getParameter("Description"));
                    bulkUpload.setMfinoUser(loggedInUser);
                    bulkUpload.setUsername(loggedInUser.getUsername());
                    bulkUpload.setMdn(subscriberService.normalizeMDN(multipartRequest.getParameter("MDN")));
                    bulkUpload.setInfilename(file.getOriginalFilename());
                    Clob clob = new SerialClob(new String(file.getBytes()).toCharArray());
                    clob.setString(1, new String(file.getBytes()));
                    bulkUpload.setInfiledata(clob);
                    bulkUpload.setInfilecreatedate(currentTime.toString());
                    bulkUpload.setFiletype(fileType);
                    bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Uploaded);
                    bulkUpload.setDeliverydate(currentTime);
                    bulkUpload.setTransactionscount(count);
                    bulkUpload.setTotalamount(totalAmount);
                    bulkUpload.setPin(encryptedPin);
                    bulkUpload.setPaymentdate(new Timestamp(paymentDate));
                    bulkUpload.setPocket(pocketService.getById(new Long(sourcePocket)));
                    bulkUploadService.save(bulkUpload);
                    
                    // Send mail to Approvers to approve the bulk transfer request.
                    String message = ConfigurationUtil.getBulkTranferApproverMessage();
                    String subject = ConfigurationUtil.getBulkTransferApproverSubject();
                    UserQuery userQuery = new UserQuery();
                    List<User> lstApprover = null;
                    List<RolePermission> rolePermissions = permissionService.getByPermission(CmFinoFIX.Permission_BulkTransfer_Approve);
                    List<Integer> roles = new ArrayList<Integer>();
                    Iterator<RolePermission> iterator = rolePermissions.iterator();
                    while(iterator.hasNext()) {
                    	roles.add((int)iterator.next().getRole());
                    }
                    if(!roles.isEmpty()) {                    	
                    	log.info("Bulk Transfer approve mail is sent to users with Role IDs:" + roles);
                    	userQuery.setRoles(roles);
                    	lstApprover = userService.get(userQuery);
                    }
                    if (CollectionUtils.isNotEmpty(lstApprover)) {
                    	for (User approver: lstApprover) {
                    		mailService.asyncSendEmail(approver.getEmail(), approver.getFirstname() + " " + approver.getLastname(), subject, message);
                    	}
                    }

                    responseMap.put("success", true);
                    responseMap.put("file", file.getOriginalFilename());
                    responseMap.put("id", bulkUpload.getId());
                    responseMap.put("paymentDate", bulkUpload.getPaymentdate());
                }
            }

        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            responseMap.put("Error", "Sorry," + throwable.toString());
        } 
        
        return new JSONView(responseMap);
    }
}
