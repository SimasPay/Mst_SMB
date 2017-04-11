package com.mfino.web.admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Address;
import com.mfino.domain.BranchCodes;
import com.mfino.domain.KycLevel;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.AddressService;
import com.mfino.service.BranchCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.service.KYCLevelService;
import com.mfino.service.PocketService;
import com.mfino.service.PocketTemplateService;
import com.mfino.service.SubscriberGroupService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.UserService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

@Controller
public class SubscriberUpgradeKycController {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String DEFAULT_BRANCH = "000";
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("SubscriberGroupServiceImpl")
	private SubscriberGroupService subscriberGroupService;
	
	@Autowired
	@Qualifier("PocketTemplateServiceImpl")
	private PocketTemplateService pocketTemplateService;

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
	
	@RequestMapping("/upgradesubscriberkyc.htm")
    protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
		log.info("Upgrade Subscriber KYC Level");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("success", false);
		try {
			
			String idType = request.getParameter("IDType");
			String mdnId = request.getParameter("ID");

			int activeProcess =  subscriberUpgradeDataDAO.getCountByMdnId(Long.valueOf(mdnId));
			
			SubscriberMdn subscriberMdn = subscriberMdnService.getById(Long.valueOf(mdnId));
			if(subscriberMdn == null){
	        	responseMap.put("Error", MessageText._("Invalid MDN ID"));
	        	return new JSONView(responseMap);
	        }
			
			if(activeProcess == 0 || CmFinoFIX.SubscriberUpgradeKycStatus_Revision.equals(subscriberMdn.getUpgradeacctstatus())){
				if(subscriberMdn.getUpgradeacctstatus() != null){
					if(subscriberMdn.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeKycStatus_Approve){
			        	responseMap.put("Error", MessageText._("Subscriber Upgrade Not Allowed"));
			        	return new JSONView(responseMap);
			        	
			    	}else if(subscriberMdn.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeKycStatus_Revision){
			    		String prevMakerUsername = subscriberMdn.getUpgradeacctrequestby();
						MfinoUser prevMakerUser = userService.getByUserName(prevMakerUsername);
						
						Long currentMakerUserBranchId = userService.getCurrentUser().getBranchcodeid();
						BranchCodes currentMakerBranchCodes = branchCodeService.getById(currentMakerUserBranchId);
						
						if(prevMakerUser == null){
							responseMap.put("Error", MessageText._("Unknow upgrade request branch"));
			        		return new JSONView(responseMap);
						}
						
						if (!(prevMakerUser.getBranchcodeid() == currentMakerUserBranchId ||
								StringUtils.equals(currentMakerBranchCodes.getBranchcode(), DEFAULT_BRANCH)) ) {
							responseMap.put("Error", MessageText._("User Admin doesn't have authority for Resubmit form"));
			        		return new JSONView(responseMap);
						}
			    	}
				}
				
				Subscriber subscriber = subscriberMdn.getSubscriber();
		        Integer subscriberStatus = subscriber.getStatus();
		        
		        if(!(subscriberStatus.equals(CmFinoFIX.SubscriberStatus_Active)
		        		|| subscriberStatus.equals(CmFinoFIX.SubscriberStatus_Active))){
		        	responseMap.put("Error", MessageText._("Subscriber Should be Active!"));
		        	return new JSONView(responseMap);
		        }

				String idCardpath = subscriberMdn.getKtpdocumentpath();
				
		        if (request instanceof MultipartHttpServletRequest) {
					String path = storeFile(request, responseMap, idType, mdnId);
					
					if(responseMap.get("Error") != null)
						return new JSONView(responseMap);
					
					if(StringUtils.isNotBlank(path))
						idCardpath = path;
				}
		        
		        Long groupID = null;
				SubscriberGroups subscriberGroup = subscriberGroupService.getBySubscriberID(subscriber.getId());
				if(subscriberGroup != null){
					groupID = subscriberGroup.getGroupid();
				}
		        
		        KycLevel nonKycLevel = kycLevelService.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_NoKyc.longValue());
		        PocketTemplate eMoneyNonKycTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(
		        		nonKycLevel.getKyclevel(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		        
		        if (eMoneyNonKycTemplate != null) {
					
					Pocket nonKycPocket = getNonKycPocket(subscriberMdn, eMoneyNonKycTemplate);	
	            	if (nonKycPocket == null) {
	                	responseMap.put("Error", MessageText._("Subscriber Not Have Emoney-Non KYC Pocket."));
	    	        	return new JSONView(responseMap);
	            	}
	            	
	            	String dateOfBirthStr = request.getParameter("DateOfBirth");
	            	Date dateOfBirth = DateUtil.getDate(dateOfBirthStr, "dd-MM-yyyy");
	            	SubscriberUpgradeData upgradeData = subscriberUpgradeDataDAO.getSubmitedRequestData(subscriberMdn.getId(), CmFinoFIX.SubscriberActivity_Upgrade_From_NonKyc_To_KYC);
	
	            	Address addressBaseOnIdCard = new Address();
	            	if(upgradeData == null){
	            		upgradeData = new SubscriberUpgradeData();
	            	}else{
	            		addressBaseOnIdCard = upgradeData.getAddress();
	            	}
	            	
	            	addressBaseOnIdCard.setRegionname(request.getParameter("RegionName"));
	            	addressBaseOnIdCard.setState(request.getParameter("State"));
	            	addressBaseOnIdCard.setSubstate(request.getParameter("SubState"));
	            	addressBaseOnIdCard.setCity(request.getParameter("City"));
	            	addressBaseOnIdCard.setLine2(request.getParameter("StreetAddress"));
	            	addressService.save(addressBaseOnIdCard);
	            	
	            	upgradeData.setAddress(addressBaseOnIdCard);
	            	upgradeData.setBirthPlace(request.getParameter("BirthPlace"));
	            	upgradeData.setBirthDate(new Timestamp(dateOfBirth));
	            	upgradeData.setFullName(request.getParameter("FirstName"));
	            	upgradeData.setMotherMaidenName(request.getParameter("MothersMaidenName"));
	            	upgradeData.setEmail(request.getParameter("Email"));
	            	upgradeData.setMdnId(subscriberMdn.getId());
	            	upgradeData.setIdType(idType);
	            	upgradeData.setIdNumber(request.getParameter("IDNumber"));
	            	upgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Upgrade_From_NonKyc_To_KYC);
	            	upgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
	            	upgradeData.setCreatedby(userService.getCurrentUser().getUsername());
	            	upgradeData.setCreatetime(new Timestamp(System.currentTimeMillis()));
	            	
	            	if(StringUtils.isNotBlank(idCardpath))
	            		upgradeData.setIdCardScanPath(idCardpath);
	            	
	            	subscriberUpgradeDataDAO.save(upgradeData);
	            	
	            	subscriberMdn.setUpgradeacctstatus(CmFinoFIX.SubscriberUpgradeKycStatus_Initialized);
	            	subscriberMdn.setUpgradeacctrequestby(userService.getCurrentUser().getUsername());
	        		subscriberMdnService.save(subscriberMdn);
	            	
					responseMap.put("success", true);
					
				} else {
	            	responseMap.put("Error", MessageText._("Emoney-UnBanked Not Available."));
		        	return new JSONView(responseMap);
	            }
			} else{
				responseMap.put("Error", MessageText._(ConfigurationUtil.getSubscriberActivityActiveMessage()));
				return new JSONView(responseMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONView(responseMap);
	}

	private String storeFile(HttpServletRequest request,
			Map<String, Object> responseMap, String idType, String mdnId)
			throws IOException {
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
					file.transferTo(destFile);

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
	
	private Pocket getNonKycPocket(SubscriberMdn subscriberMDN,
			PocketTemplate eMoneyNonKycTemplate) {
		PocketQuery pocketQuery= new PocketQuery();
		pocketQuery.setPocketTemplateID(eMoneyNonKycTemplate.getId());
		pocketQuery.setMdnIDSearch(subscriberMDN.getId());
		List<Pocket> pocketList = pocketService.get(pocketQuery);
		if(pocketList != null && pocketList.size() > 0)
			return pocketList.get(0);
		else
			return null;
	}
}
