package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialClob;

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

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.BulkUploadService;
import com.mfino.service.MfinoServiceProviderService;
import com.mfino.service.PocketService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.UserService;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author Bala Sunku
 * 
 */
@Controller
public class BulkTransferInquiryController {
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("MfinoServiceProviderServiceImpl")
	private MfinoServiceProviderService mfinoServiceProviderService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, Object> responseMap = new HashMap<String, Object>();;
    private String DELIMITOR = ",";
    
    @RequestMapping("/bulkTransferInquiry.htm")
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
    	log.info("Processing the new bulk transfer inquiry request...........");
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
                	log.info("File size is empty.");
                    responseMap.put("Error", "Empty File");
                } 
                else if (file.getSize() > maxFileSize) {
                	log.info("File size exceeds the max limit "+ maxFileSize);
                    responseMap.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), maxFileSizeMB));
                }
                else {
                	long srcPocketId = systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY);
                	if (srcPocketId == -1) {
                    	log.info("Source Global SVA pocket id not configured");
                        responseMap.put("Error", String.format(MessageText._("Sorry, Source Global SVA pocket id not configured.")));
                        return new JSONView(responseMap);
                	}
                	
                    Pocket srcPocket = pocketService.getById(srcPocketId);
            		Integer validationResult = transactionApiValidationService.validateDestinationPocket(srcPocket);
            		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
            			log.info("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
                        responseMap.put("Error", String.format(MessageText._("Sorry, Source pocket has failed validations.")));
                        return new JSONView(responseMap);
            		}                	

                    long destPocketId = systemParametersService.getLong(SystemParameterKeys.INTEREST_COMMISSION_FUNDING_POCKET_ID);
                    if (destPocketId == -1) {
                    	log.info("Interest / commission funding pocket id not configured");
                        responseMap.put("Error", String.format(MessageText._("Sorry, Interest / commission funding pocket id not configured.")));
                        return new JSONView(responseMap);
                    }
                    Pocket destPocket = pocketService.getById(destPocketId);
            		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
            		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
            			log.info("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
                        responseMap.put("Error", String.format(MessageText._("Sorry, Destination pocket has failed validations.")));
                        return new JSONView(responseMap);
            		}
            		
                    long ntPocketId = systemParametersService.getLong(SystemParameterKeys.NATIONAL_TREASURY_POCKET);
                    if (ntPocketId == -1) {
                    	log.info("National Treasury pocket id not configured");
                        responseMap.put("Error", String.format(MessageText._("Sorry, Suspense account pocket id is not configured.")));
                        return new JSONView(responseMap);
                    }
                    Pocket ntPocket = pocketService.getById(ntPocketId);
            		validationResult = transactionApiValidationService.validateDestinationPocket(ntPocket);
            		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
            			log.info("Suspense Account pocket with id "+(ntPocket!=null? ntPocket.getId():null)+" has failed validations");
                        responseMap.put("Error", String.format(MessageText._("Sorry, Suspense Account pocket has failed validations.")));
                        return new JSONView(responseMap);
            		}            		
                    //Parse each line of the file and check the line count
                    ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
                    BufferedReader br = new BufferedReader(new InputStreamReader(bis));
                    String strLine;
                    int linecount = 0;
                    while ((strLine = br.readLine()) != null) {
                        if (strLine.trim().length() == 0) {
                        	log.info("File contain Empty lines.");
                            responseMap.put("Error", String.format(MessageText._("Sorry, File contain Empty Lines")));
                            return new JSONView(responseMap);
                        }
                        linecount++;
                        String[] line = strLine.split(DELIMITOR);
                        if (line.length < 2) {
                        	log.info("Invalid data at line number: "+ linecount);
                            responseMap.put("Error", String.format(MessageText._("Sorry, Invalid data at line number: " + linecount)));
                            return new JSONView(responseMap);
                        }
                        if (StringUtils.isBlank(line[0]) || (line[0].length() < 10 || line[0].length() > 14) || !(line[0].matches("((-|\\+)?[0-9])+")) ) {
                        	log.info("Invalid MDN at line number: "+ linecount);
                            responseMap.put("Error", String.format(MessageText._("Sorry, Invalid MDN at line number: " + linecount)));
                            return new JSONView(responseMap);
                        } 
                        if (StringUtils.isBlank(line[1]) || !(line[1].matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) ) {
                        	log.info("Invalid Amount at line number: "+ linecount);
                            responseMap.put("Error", String.format(MessageText._("Sorry, Invalid Amount at line number: " + linecount)));
                            return new JSONView(responseMap);
                        }                        
                        totalAmount = totalAmount.add(new BigDecimal(line[1]));
                    }
                    count = linecount;

                    BulkUpload bulkUpload = new BulkUpload();
                    
                    String paymentDateStr = multipartRequest.getParameter("PaymentDate");
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date paymentDate = sdf.parse(paymentDateStr);
                    MfinoUser loggedInUser = userService.getCurrentUser();
                    Timestamp currentTime = new Timestamp();
                    bulkUpload.setName(multipartRequest.getParameter("Name"));
                    bulkUpload.setCompany(loggedInUser.getCompany());
                    bulkUpload.setDescription(multipartRequest.getParameter("Description"));
                    bulkUpload.setMfinoUser(loggedInUser);
                    bulkUpload.setUsername(loggedInUser.getUsername());
                    
                    SubscriberMdn srcSubscriberMDN = srcPocket.getSubscriberMdn();
                    bulkUpload.setMdn(srcSubscriberMDN.getMdn());
                    bulkUpload.setMdnid(srcSubscriberMDN.getId());
                    bulkUpload.setInfilename(file.getOriginalFilename());
                    bulkUpload.setInfiledata(new String(file.getBytes()));
                    bulkUpload.setInfilecreatedate(currentTime.toString());
                    bulkUpload.setFiletype(fileType);
                    bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Initialized);
                    bulkUpload.setTransactionscount(count);
                    bulkUpload.setTotalamount(totalAmount);
                    bulkUpload.setPaymentdate(new Timestamp(paymentDate));
                    
                    bulkUpload.setPocket(srcPocket);
                    bulkUploadService.save(bulkUpload);
                    
                    // Send mail to Approvers to approve the bulk transfer request.
//                    String message = ConfigurationUtil.getBulkTranferApproverMessage();
//                    String subject = ConfigurationUtil.getBulkTransferApproverSubject();
//                    UserQuery userQuery = new UserQuery();
//                    List<User> lstApprover = null;
//                    List<RolePermission> rolePermissions = permissionService.getByPermission(CmFinoFIX.Permission_BulkTransfer_Approve);
//                    List<Integer> roles = new ArrayList<Integer>();
//                    Iterator<RolePermission> iterator = rolePermissions.iterator();
//                    while(iterator.hasNext()) {
//                    	roles.add(iterator.next().getRole());
//                    }
//                    if(!roles.isEmpty()) {                    	
//                    	log.info("Bulk Transfer approve mail is sent to users with Role IDs:" + roles);
//                    	userQuery.setRoles(roles);
//                    	lstApprover = userService.get(userQuery);
//                    }
//                    if (CollectionUtils.isNotEmpty(lstApprover)) {
//                    	for (User approver: lstApprover) {
//                    		mailService.asyncSendEmail(String.valueOf(approver.getmFinoServiceProviderByMSPID().getID()), approver.getEmail(), approver.getFirstName() + " " + approver.getLastName(), subject, message);
//                    	}
//                    }

                    responseMap.put("success", true);
                    responseMap.put("file", file.getOriginalFilename());
                    responseMap.put("id", bulkUpload.getId());
                    responseMap.put("disPalyId", leftPadWithCharacter(bulkUpload.getId()+"", 8, "0"));
                    responseMap.put("paymentDate", convertDate(bulkUpload.getPaymentdate()));
                    responseMap.put("count", count + " MDNs");
                    responseMap.put("totalAmount", "RP "+ MfinoUtil.getNumberFormat().format(totalAmount));
                    responseMap.put("name", bulkUpload.getName());
                    responseMap.put("description", bulkUpload.getDescription());
                }
            }

        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            responseMap.put("Error", "Sorry," + throwable.toString());
        } 
        log.info("End new bulk transfer inquiry request...........");
        return new JSONView(responseMap);
    }
    
    private String convertDate(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    	return sdf.format(date);
    }
    
	private String leftPadWithCharacter(String str, int totalLength, String padCharacter){
		if((str == null) || ("".equals(str))) return str;
		
		if(str.length() < totalLength){
			int strLen = str.length();
			for(int i = 0;i < (totalLength - strLen);i++ ){
				str = padCharacter + str;
			}
		}
		return str;
	}    
}
