package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.mfino.dao.query.UserQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.RolePermission;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.BulkUploadService;
import com.mfino.service.MailService;
import com.mfino.service.PermissionService;
import com.mfino.service.SubscriberService;
import com.mfino.service.UserService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Bala Sunku
 * 
 */
@Controller
public class BulkTransferConfirmController {
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private BulkUploadEntryService bulkUploadEntryService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
    @Autowired
    @Qualifier("PermissionServiceImpl")
    private PermissionService permissionService;
    
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService; 
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, Object> responseMap = new HashMap<String, Object>();;
    private String DELIMITOR = ",";
    
    @RequestMapping("/bulkTransferConfirm.htm")
    protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
		boolean isAuthorized = true; //Authorization.isAuthorized(CmFinoFIX.Permission_Bulk_Upload_Add)
		if (isAuthorized) { 
			return doUpload(request);
		} else {
            responseMap.put("success", false);
            responseMap.put("Error", MessageText._("You are not authorized to perform this operation."));
    		return new JSONView(responseMap);
		}
    }
    
    protected View doUpload(HttpServletRequest request) {

        responseMap.put("success", false);

        try {
        	boolean confirmStatus = Boolean.parseBoolean(request.getParameter("confirmStatus"));
        	long bulkUploadId = Long.parseLong(request.getParameter("ID"));
        	log.info("Bulk transfer request: " + bulkUploadId + " is confirmed: "+ confirmStatus);
        	BulkUpload bulkUpload = bulkUploadService.getById(bulkUploadId);
        	if (confirmStatus) {
        		bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Uploaded);
        		createEntries(bulkUpload);
        		
                // Send mail to Approvers to approve the bulk transfer request.
        		String message = ConfigurationUtil.getBulkTranferApproverMessage();
        		String subject = ConfigurationUtil.getBulkTransferApproverSubject();
        		UserQuery userQuery = new UserQuery();
        		List<MfinoUser> lstApprover = null;
        		List<RolePermission> rolePermissions = permissionService.getByPermission(CmFinoFIX.Permission_BulkTransfer_Approve);
        		List<Integer> roles = new ArrayList<Integer>();
        		Iterator<RolePermission> iterator = rolePermissions.iterator();
        		while(iterator.hasNext()) {
        			roles.add((int)iterator.next().getRole());
        		}
        		if (!roles.isEmpty()) {                    	
        			log.info("Bulk Transfer approve mail is sent to users with Role IDs:" + roles);
        			userQuery.setRoles(roles);
        			lstApprover = userService.get(userQuery);
        		}
        		if (CollectionUtils.isNotEmpty(lstApprover)) {
        			for (MfinoUser approver: lstApprover) {
        				mailService.asyncSendEmail(approver.getEmail(), approver.getFirstname() + " " + approver.getLastname(), subject, message);
        			}
        		}
        	}
        	else {
        		bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Terminated);
        		bulkUpload.setFailurereason("Cancelled by the user during confirmation level");
        	}
        	bulkUploadService.save(bulkUpload);
        	
        	responseMap.put("success", true);
        	responseMap.put("id", bulkUpload.getId());
        	responseMap.put("disPalyId", leftPadWithCharacter(bulkUpload.getId()+"", 8, "0"));
        	
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseMap.put("Error", "Sorry," + e.toString());
        } 
        
        return new JSONView(responseMap);
    }
    
    private void createEntries(BulkUpload bulkUpload) throws IOException {
    	log.info("Creating the entries for bulk transfer id: "+ bulkUpload.getId());
    	BufferedReader bufferedReader = new BufferedReader(new StringReader(bulkUpload.getInfiledata()));
	
		String line = null;
		BulkUploadEntry bue = null;
		int i=1;
		for (i=1; (line = bufferedReader.readLine()) != null; i++) {
			Integer transferStatus = CmFinoFIX.TransactionsTransferStatus_Initialized;
			String lineData[] = line.split(DELIMITOR);
			String destMDN = subscriberService.normalizeMDN(lineData[0]);
			BigDecimal amount = new BigDecimal(lineData[1]);

			// Setting the Bulk upload entry
			bue = new BulkUploadEntry();
			bue.setUploadid(bulkUpload.getId());
			bue.setLinenumber(i);
			bue.setStatus(transferStatus);
			bue.setAmount(amount);
			bue.setDestmdn(destMDN);
			
			bulkUploadEntryService.saveBulkUploadEntry(bue);
		}
		log.info("Successfully created " + i + " entries" );
    }
    
	private String leftPadWithCharacter(String str, int totalLength, String padCharacter) {
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
