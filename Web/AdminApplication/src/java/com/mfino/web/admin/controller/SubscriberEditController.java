package com.mfino.web.admin.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
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

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.domain.Address;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.AddressService;
import com.mfino.service.BranchCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.service.KYCLevelService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.UserService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;

@Controller
public class SubscriberEditController {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("AddressServiceImpl")
	private AddressService addressService;

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("BranchCodeServiceImpl")
	private BranchCodeService branchCodeService;


	@Autowired
	@Qualifier("KYCLevelServiceImpl")
	private KYCLevelService kycLevelService;
	
	@RequestMapping("/subscribereditmaker.htm")
    protected View process(HttpServletRequest request, HttpServletResponse response) {
		log.info("Upgrade Subscriber KYC Level");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("success", false);
		try {
			String idType = request.getParameter("IDType");
			String mdnId = request.getParameter("MDNID");
			String language = request.getParameter("Language");
			int activeProcess =  subscriberUpgradeDataDAO.getCountByMdnId(Long.valueOf(mdnId));
			
			if(activeProcess == 0){
				
				if(StringUtils.isNumeric(idType)){
					idType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_IDTypeForKycUpgrade, null, idType);
				}
				
				SubscriberMdn subscriberMdn = subscriberMdnService.getById(Long.valueOf(mdnId));
				if(subscriberMdn == null){
		        	responseMap.put("Error", MessageText._("Invalid MDN ID"));
		        	return new JSONView(responseMap);
		        }
				
				Subscriber subscriber = subscriberMdn.getSubscriber();

	        	String subscriberStatusReq = request.getParameter("SubscriberStatus");
	        	Integer subscriberStatusInt = Integer.valueOf(subscriberStatusReq);
	        	String restrictions = request.getParameter("MDNRestrictions");
	        	Integer restrictionsInt = 0;
	        	if(StringUtils.isNotBlank(restrictions) && StringUtils.isNumeric(restrictions))
	        		restrictionsInt = Integer.valueOf(restrictions);
	        	
				if(CmFinoFIX.SubscriberStatus_Active.equals(subscriberStatusInt) && 
						!isActivationAllowed(subscriber, subscriberStatusInt, restrictionsInt)){
					responseMap.put("Error", MessageText._("Subscriber Activation not allowed."));
					return new JSONView(responseMap);
				}
		        
				if(CmFinoFIX.SubscriberStatus_InActive.equals(subscriberStatusInt)){
					if(!CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(restrictionsInt)){
						responseMap.put("Error", MessageText._("Subscriber InActivation not allowed."));
						return new JSONView(responseMap);
					}
				}
		        
				if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberStatusInt)){
					responseMap.put("Error", MessageText._("Changing Subscriber status to 'Not Registered' is not allowed."));
					return new JSONView(responseMap);
				}
				
				if(CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberStatusInt) && 
						!( CmFinoFIX.SubscriberStatus_Initialized.equals(subscriber.getStatus()) 
								|| CmFinoFIX.SubscriberStatus_Suspend.equals(subscriber.getStatus()) 
								|| CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus())
								||CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus())) 
								){
					responseMap.put("Error", MessageText._("Intializing subscriber not allowed."));
					return new JSONView(responseMap);
				}
				
				if(CmFinoFIX.SubscriberStatus_Suspend.equals(subscriberStatusInt) && 
						!CmFinoFIX.SubscriberStatus_Suspend.equals(subscriber.getStatus())){
					responseMap.put("Error", MessageText._("Suspending of subscriber not allowed."));
					return new JSONView(responseMap);
				}
				
				if(subscriberStatusInt != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberStatusInt) &&
						CmFinoFIX.SubscriberStatus_Suspend.equals(subscriber.getStatus())){
					responseMap.put("Error", MessageText._("Suspended subscriber can be moved to Intialized status only."));
					return new JSONView(responseMap);
				}
				
				if(subscriberStatusInt != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberStatusInt) &&
						CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus()) && !isActivationAllowed(subscriber, subscriberStatusInt, restrictionsInt)){
					responseMap.put("Error", MessageText._("Inactive subscriber can be moved to Intialized status only."));
					return new JSONView(responseMap);
				}

				String idCardpath = subscriberMdn.getKtpdocumentpath();
				
		        if (request instanceof MultipartHttpServletRequest) {
					String path = storeFile(request, responseMap, idType, mdnId);
					
					if(responseMap.get("Error") != null)
						return new JSONView(responseMap);
					
					if(StringUtils.isNotBlank(path)){
						idCardpath = path;
					} 
				}
		        
	        	SubscriberUpgradeData upgradeData = subscriberUpgradeDataDAO.getSubmitedRequestData(subscriberMdn.getId(), CmFinoFIX.SubscriberActivity_Edit_Subscriber_Details);

	        	Address addressBaseOnIdCard = new Address();
	        	if(upgradeData == null){
	        		upgradeData = new SubscriberUpgradeData();
	        	}
	        	
	        	addressBaseOnIdCard.setRegionname(request.getParameter("RegionName"));
	        	addressBaseOnIdCard.setState(request.getParameter("State"));
	        	addressBaseOnIdCard.setSubstate(request.getParameter("SubState"));
	        	addressBaseOnIdCard.setCity(request.getParameter("City"));
	        	addressBaseOnIdCard.setLine2(request.getParameter("StreetAddress"));
	        	addressService.save(addressBaseOnIdCard);
	        	
	        	upgradeData.setAddress(addressBaseOnIdCard);
	        	upgradeData.setFullName(request.getParameter("FirstName"));
	        	upgradeData.setEmail(request.getParameter("Email"));
	        	upgradeData.setMdnId(subscriberMdn.getId());
	        	upgradeData.setIdType(idType);
	        	upgradeData.setIdNumber(request.getParameter("IDNumber"));
	        	upgradeData.setBankAccountNumber(request.getParameter("AccountNumber"));
	        	upgradeData.setLanguage(StringUtils.isBlank(language) ? 0 : Integer.valueOf(language));
	        	String notifMethod = request.getParameter("NotificationMethod");
	        	if(StringUtils.isNumeric(notifMethod))
	        		upgradeData.setNotificationMethod(Integer.valueOf(notifMethod));
	        	if(StringUtils.isNumeric(restrictions))
	        		upgradeData.setSubscriberRestriction(Integer.valueOf(restrictions));
	        	upgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Edit_Subscriber_Details);
	        	upgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
	        	upgradeData.setCreatedby(userService.getCurrentUser().getUsername());
	        	upgradeData.setCreatetime(new Timestamp(System.currentTimeMillis()));
	        	upgradeData.setSubscriberStatus(StringUtils.isBlank(subscriberStatusReq) ? subscriber.getStatus() : subscriberStatusInt);
	        	
	        	if(StringUtils.isNotBlank(idCardpath))
	        		upgradeData.setIdCardScanPath(idCardpath);
	        	
	        	subscriberUpgradeDataDAO.save(upgradeData);
	        	
	        	subscriberMdn.setUpgradeacctstatus(CmFinoFIX.SubscriberUpgradeKycStatus_Initialized);
	        	subscriberMdn.setUpgradeacctrequestby(userService.getCurrentUser().getUsername());
	    		subscriberMdnService.save(subscriberMdn);
	        	
				responseMap.put("success", true);
			} else{
				responseMap.put("Error", MessageText._(ConfigurationUtil.getSubscriberActivityActiveMessage()));
				return new JSONView(responseMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONView(responseMap);
	}
	
	private boolean isActivationAllowed(Subscriber subscriber,
			Integer subscriberStatusInt, Integer subscriberRestrictionInt) {

		if(CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())){
			return true;
		}
		if(CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus())){
			if(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriber.getRestrictions()) && (
					(subscriberStatusInt == null) || 
					(CmFinoFIX.SubscriberRestrictions_None.equals(subscriberRestrictionInt)
							))){
				return true;
			}
		}
		return false;
	}

	private String storeFile(HttpServletRequest request,
			Map<String, Object> responseMap, String idType, String mdnId){
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		int maxFileSizeMB = Integer.parseInt(ConfigurationUtil.getUploadFileSizeLimit());
		long maxFileSize = maxFileSizeMB * 1024L * 1024L;
		
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		Set<Entry<String,MultipartFile>> entrySet = fileMap.entrySet();
		
		for (Entry<String, MultipartFile> entry : entrySet) {
			
			MultipartFile file = entry.getValue();
			
			if(file != null && file.getSize() > 0 ){
				if(!isValidFileFormat(file)){
					responseMap.put("Error", MessageText._("Please upload a file with valid format"));
				} else if (file.getSize() > maxFileSize) {
					responseMap.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), maxFileSizeMB));
				} else {
					String imagePath = ConfigurationUtil.getKTPImagePath();
					String fileName = file.getOriginalFilename();
					String extension = FilenameUtils.getExtension(fileName);
					String[] completeFileName = {idType, "_", mdnId, String.valueOf(System.currentTimeMillis()), FilenameUtils.EXTENSION_SEPARATOR_STR, extension};
					String newFileName = imagePath+StringUtils.join(completeFileName);
					log.info("KTP Scan uploaded to: "+newFileName);
					
					File destFile = new File(newFileName);
					try {
						file.transferTo(destFile);
					} catch (Exception e) {
						log.error("Error When Uploading file", e);
						responseMap.put("Error", MessageText._("Failed to store the file"));
					}

					String url = ConfigurationUtil.getKTPImageUrl();
					return url+StringUtils.join(completeFileName);
				}
			}
		}
		return null;
	}
	
	private boolean isValidFileFormat(MultipartFile file){
		String fileName = file.getOriginalFilename().toLowerCase();
		if(fileName.endsWith(".png") || fileName.endsWith(".jpg") || 
				fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || 
				fileName.endsWith(".tif"))
			return true;
		return false;
	}
}
