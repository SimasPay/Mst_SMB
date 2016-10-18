/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.scheduler.upload.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.dao.query.GroupQuery;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.Address;
import com.mfino.domain.AuthPersonDetails;
import com.mfino.domain.BulkUploadFile;
import com.mfino.domain.BulkUploadFileEntry;
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.scheduler.upload.service.SubscriberBulkUploadService;
import com.mfino.scheduler.util.BulkUploadUtil;
import com.mfino.service.AddressService;
import com.mfino.service.AuthorizingPersonService;
import com.mfino.service.BulkUploadFileEntryService;
import com.mfino.service.GroupService;
import com.mfino.service.KYCLevelService;
import com.mfino.service.MailService;
import com.mfino.service.PocketService;
import com.mfino.service.PocketTemplateConfigService;
import com.mfino.service.PocketTemplateService;
import com.mfino.service.SubscriberGroupService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SubscribersAdditionalFieldsService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.SubscriberSyncErrors;

/**
 * 
 * @author Maruthi
 */
@Service("SubscriberBulkUploadServiceImpl")
public class SubscriberBulkUploadServiceImpl  implements SubscriberBulkUploadService {
		
	private static Logger log = LoggerFactory.getLogger(SubscriberBulkUploadServiceImpl.class);
	
	private static final int UNBANKED_MINFIELD_COUNT = 15;
	private static final int SEMIBANKED_MINFIELD_COUNT = 18;
	private static final int FULLYBANKED_MINFIELD_COUNT = 21;
	private static final int TOTAL_FIELD_COUNT = 22;
	private static final int MDN_MAX_LENGTH = 13;
	private static final int MDN_MIN_LENGTH = 10;
	//private int cardPanLength=10;
    private List<Integer> cardPanLength = new  ArrayList<Integer>();
	private String uploadedBy="";
	private static final String dateFormat = "ddMMyyyy";
	private PocketTemplate eMoneyPocketTemplate;
	private Map<String, Groups> mapGroup = null;
	private Map<String, PocketTemplate> mapGroupPocketTemp = null;
	private Groups defaultGroup = null;
	private int notificationMethod = CmFinoFIX.NotificationMethod_Web;
	private String emailSubject = "OneTimePassword";
	private boolean sendEmailNotification = false;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketTemplateServiceImpl")
	private PocketTemplateService pocketTemplateService;
	
	@Autowired
	@Qualifier("KYCLevelServiceImpl")
	private KYCLevelService kycLevelService;
	
	@Autowired
	@Qualifier("AddressServiceImpl")
	private AddressService addressService;
	
	@Autowired
	@Qualifier("SubscribersAdditionalFieldsServiceImpl")
	private SubscribersAdditionalFieldsService subscribersAdditionalFieldsService;
	
	@Autowired
	@Qualifier("AuthorizingPersonServiceImpl")
	private AuthorizingPersonService authorizingPersonService;
	
	@Autowired
	@Qualifier("PocketTemplateConfigServiceImpl")
	private PocketTemplateConfigService pocketTemplateConfigService;
	
	@Autowired
	@Qualifier("GroupServiceImpl")
	private GroupService groupService;
	
	@Autowired
	@Qualifier("SubscriberGroupServiceImpl")
	private SubscriberGroupService subscriberGroupService;
	
	@Autowired
	@Qualifier("BulkUploadFileEntryServiceImpl")
	private BulkUploadFileEntryService bulkUploadFileEntryService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	
	public BulkUploadFile ProcessBulkUploadFile(BulkUploadFile bulkUploadFile) throws IOException {
		
		mapGroup = getAllGroups();
		mapGroupPocketTemp = getSVAPocketTemplatesForAllGroupsOfSubScriber();
		notificationMethod = ConfigurationUtil.getBulkUploadSubscriberNotificationMethod();
		emailSubject = ConfigurationUtil.getBulkUploadSubscriberEmailSubject();
		sendEmailNotification = ConfigurationUtil.getBulkUploadSubscriberSendEmail();
		
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
					lineData = bulkUploadFileEntry.getLinedata().getSubString(0, ((Long)bulkUploadFileEntry.getLinedata().length()).intValue());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				log.info("Processing record at line number: " + linenumber);
				//set the bulk upload file entry record status as Processing
				bulkUploadFileEntry.setBulkuploadfileentrystatus(CmFinoFIX.BulkUploadFileEntryStatus_Processing);
				bulkUploadFileEntryService.save(bulkUploadFileEntry);
				
				if (bulkUploadFile.getRecordtype().equals(CmFinoFIX.RecordType_SubscriberFullyBanked)
						||bulkUploadFile.getRecordtype().equals(CmFinoFIX.RecordType_SubscriberSemiBanked)
						||bulkUploadFile.getRecordtype().equals(CmFinoFIX.RecordType_SubscriberUnBanked)) {
					
					CMJSError response = createSubscriber(lineData, bulkUploadFile.getRecordtype().intValue(),bulkUploadFile.getCreatedby());
					if (response.getErrorCode().equals(SubscriberSyncErrors.Success)) {
						log.info("Successfully created the Subscriber for record at line number: " + linenumber);
						bulkUploadFileEntry.setBulkuploadfileentrystatus(CmFinoFIX.BulkUploadFileEntryStatus_Completed);							
					} else {
						log.info("Error while creating the Subscriber for record at line number: " + linenumber + " :"+ response.getErrorDescription());
						bulkUploadFileEntry.setBulkuploadfileentrystatus(CmFinoFIX.BulkUploadFileEntryStatus_Failed);
						bulkUploadFileEntry.setFailurereason(response.getErrorDescription());					
						errorLineCount++;
					}
					bulkUploadFileEntryService.save(bulkUploadFileEntry);
				}
				else if(bulkUploadFile.getRecordtype().equals(CmFinoFIX.RecordType_Upgrade_N_Approve)) {				
					CMJSError response = upgradeNApproveSubscriber(lineData, bulkUploadFile.getCreatedby());
					if (response.getErrorCode().equals(SubscriberSyncErrors.Success)) {
						bulkUploadFileEntry.setBulkuploadfileentrystatus(CmFinoFIX.BulkUploadFileEntryStatus_Completed);					
					} else {
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
	
	private CMJSError upgradeNApproveSubscriber(String line, String userName) {
		log.info("Upgrade and approve the line : " + line);
		String strTokens[] = line.split(GeneralConstants.FIELD_SEPARATOR);
		if(strTokens.length==1)
        {
        	strTokens = line.split(GeneralConstants.FIELD_SEPARATOR_COMMA);
        }
		CMJSError response = new CMJSError();
		response.setErrorCode(SubscriberSyncErrors.Success);
		
		if (strTokens.length < SEMIBANKED_MINFIELD_COUNT) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_Field_Count);
			response.setErrorDescription("Insufficient field count :"+ strTokens.length);
			log.error(String.format("Bad line format: %s", line));
			return response;
		}
		else {
			String mdn = subscriberService.normalizeMDN(strTokens[2]);
			if (!StringUtils.isNumeric(mdn) || mdn.length() > MDN_MAX_LENGTH || mdn.length() < MDN_MIN_LENGTH) {
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("MDN length should be between " +MDN_MIN_LENGTH+" and "+MDN_MAX_LENGTH);
				log.error("Invalid MDN: " + mdn);
				return response;
			}
			
			long pocketTempletId = 0l;
			try {
				pocketTempletId = Long.valueOf(strTokens[3]);
			} catch (Exception e) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_PocketTemplate);
				response.setErrorDescription("Invalid Bank Pocket TemplateID: "+ strTokens[3]);
				log.error("Invalid Bank Pocket TemplateID: " + strTokens[3], e);
				return response;
			}
			PocketTemplate pocketTemplate = pocketTemplateService.getById(pocketTempletId);
			if(pocketTemplate == null || !CmFinoFIX.PocketType_BankAccount.equals(pocketTemplate.getType())){
				response.setErrorCode(SubscriberSyncErrors.Invalid_PocketTemplate);
				response.setErrorDescription("Invalid Bank Pocket TemplateID: "+ strTokens[3] );
				log.error("Invalid BankAccount TemplateID: " + strTokens[3]);
				return response;
			}
			
			String cardPan = strTokens[4];
			if(StringUtils.isBlank(cardPan) ||!getCardPanLength().contains(cardPan.length())|| !StringUtils.isNumeric(cardPan)) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_CardPan);
				response.setErrorDescription("Invalid CardPan: "+ cardPan);
				log.error("Invalid CardPan: "+ cardPan);
				return response;
			}
			
			response = BulkUploadUtil.checkCardPan(cardPan);
			if(!response.getErrorCode().equals(SubscriberSyncErrors.Success)){
				response.setErrorCode(SubscriberSyncErrors.CardPan_Already_Exist);
				response.setErrorDescription("CardPan already exists: "+ cardPan);
				log.error("CardPan already exists: "+ cardPan);
				return response;
			}
			
			String streetAddress = strTokens[6];
			String idType = strTokens[9];
			String idNumber = strTokens[10];
			String idExpiry = strTokens[11];
			String proof = strTokens[15];
			String nextKinName = strTokens[16];
			String nextKinNumber = strTokens[17];
			Date idExpirationDate = null;
			if (StringUtils.isBlank(streetAddress) || StringUtils.isBlank(idType) || StringUtils.isBlank(idNumber) || StringUtils.isBlank(idExpiry) ||
				StringUtils.isBlank(proof) || StringUtils.isBlank(nextKinName) || StringUtils.isBlank(nextKinNumber)) {
				response.setErrorCode(SubscriberSyncErrors.Mandatory_Field_Missing);
				response.setErrorDescription("Some of the Mandatory Fields are blank: "+ line);
				log.error("Some of the Mandatory Fields are blank: "+ line);
				return response;
			}
			try {
				SimpleDateFormat sd = new SimpleDateFormat(dateFormat);
				idExpirationDate = sd.parse(idExpiry);
				if (idExpirationDate.before(new Date())) {
					response.setErrorCode(SubscriberSyncErrors.Invalid_Field);
					response.setErrorDescription("Invalid Id Expiration date: "+ idExpiry);
					log.error("Invalid Id Expiration date: "+ idExpiry);
					return response;
				}
			} catch (ParseException e) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
				response.setErrorDescription("Invalid DateFormat for IDExpirationTime: "+ idExpiry);
				log.error("Invalid DateFormat for IDExpirationTime: "+ idExpiry, e);
				return response;
			}
			if (!StringUtils.isNumeric(nextKinNumber)) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_Field);
				response.setErrorDescription("Invalid Next of Kin number: "+ nextKinNumber);
				log.error("Invalid Next of Kin number: "+ nextKinNumber);
				return response;
			}
			
			
			SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(mdn);
			if (subscriberMDN == null) {
				response.setErrorCode(SubscriberSyncErrors.Notregistered_MDN);
				response.setErrorDescription("MDN not Registered: " + mdn);
				log.error("MDN Not Registered to Upgrade or Approve: " + line);
				return response;
			}
			Subscriber subscriber = subscriberMDN.getSubscriber();
			if (subscriber == null) {
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Subscriber Entry not found for MDN: " + mdn);
				log.error("Subscriber Entry not found for MDN: " + mdn);
				return response;
			}
			if (! CmFinoFIX.UpgradeState_none.equals(subscriber.getUpgradestate()) ) {
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Subscriber has already upgraded to different KYClevel");
				log.error("Subscriber has already upgraded to different KYClevel");
				return response;
			}
			if (subscriber.getUpgradablekyclevel() != null && 
				!(subscriber.getUpgradablekyclevel().equals(ConfigurationUtil.getBulkUploadSubscriberKYClevel()))) {
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Subscriber has applied to upgraded for different KYClevel");
				log.error("Subscriber has applied to upgraded for different KYClevel");
				return response;
			}
			Address address = subscriber.getAddressBySubscriberaddressid();
			SubscriberAddiInfo additionalFields = null;
			if (CollectionUtils.isNotEmpty(subscriber.getSubscriberAddiInfos())) {
				additionalFields = subscriber.getSubscriberAddiInfos().iterator().next();
			} 
			else {
				additionalFields = new SubscriberAddiInfo();
				additionalFields.setSubscriber(subscriber);
			}
			
			KycLevel kycLevel = kycLevelService.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
			
			//Upgrade Emoney pocket Template to Fully Banked
			// Set Group Id to Default Group Id '1' so that if any subscriber exists with out group then 
			//the default group will be used in calculating of pocket templates.
			Long groupID = defaultGroup.getId().longValue(); 
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
			
			if(subscriberGroups != null && !subscriberGroups.isEmpty())
			{
				SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroupid();
			}
			
			eMoneyPocketTemplate = mapGroupPocketTemp.get(subscriber.getKycLevel().getKyclevel().toString()+groupID.toString());
			PocketTemplate upgradeTemplate = mapGroupPocketTemp.get(kycLevel.getKyclevel().toString()+groupID.toString());
            
			if (upgradeTemplate == null) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_PocketTemplate);
				response.setErrorDescription("Invalid Pocket Template for Kyclevel to Upgrade");
				log.error("Invalid Pocket Template for Kyclevel to Upgrade");
				return response;
			}
			Pocket emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(),eMoneyPocketTemplate.getId().longValue());
			if (emoneyPocket == null || 
				!(emoneyPocket.getStatus()==(CmFinoFIX.PocketStatus_Initialized)|| emoneyPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
				response.setErrorCode(SubscriberSyncErrors.EmoneyPocketNotFound);
				response.setErrorDescription("Emoney Pocket not Found");
				log.error("Emoney Pocket not Found");
				return response;
			}
			
			if (emoneyPocket.getPocketTemplateByPockettemplateid().equals(upgradeTemplate)) {
				response.setErrorCode(SubscriberSyncErrors.PocketAlreadyUpgraded);
				response.setErrorDescription("Pocket already upgraded");
				log.error("Pocket already upgraded");
				return response;
			}
			emoneyPocket.setPocketTemplateByOldpockettemplateid(emoneyPocket.getPocketTemplateByPockettemplateid());
			emoneyPocket.setPocketTemplateByPockettemplateid(upgradeTemplate);
			emoneyPocket.setPockettemplatechangedby(userName);
			emoneyPocket.setPockettemplatechangetime(new Timestamp());
			
			// Check is already bank pocket avialable for subscriber. If so throw Exception else Create Bank pocket
			Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
			if (bankPocket == null) {
				bankPocket = pocketService.createPocket(pocketTempletId, subscriberMDN, CmFinoFIX.PocketStatus_Initialized,true, cardPan);
				if (subscriber.getStatus()==(CmFinoFIX.SubscriberStatus_Active)) {
					bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
					bankPocket.setActivationtime(new Timestamp());
					bankPocket.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
				}
			}
			else {
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Bank pocket already added to MDN: " + mdn);
				log.info("Bank pocket already added to MDN: " + mdn);
				return response;
			}
			
			if (address == null) {
				address = new Address();
				subscriber.setAddressBySubscriberaddressid(address);
			}
			address.setLine2(streetAddress);
			subscriberMDN.setIdtype(idType);
			subscriberMDN.setIdnumber(idNumber);
			subscriber.setIdexiparetiontime(new Timestamp(idExpirationDate));
			subscriber.setKycLevel(kycLevel);
			subscriber.setUpgradablekyclevel(null);
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved);
			subscriber.setApproveorrejectcomment(MessageText._("Upgrade N Approved by System"));
			subscriber.setApprovedorrejectedby(userName);
			subscriber.setApproveorrejecttime(new Timestamp());
			
 			additionalFields.setProofofaddress(proof);
			additionalFields.setKinname(nextKinName);
			additionalFields.setKinmdn(nextKinNumber);
			
			addressService.save(address);
			pocketService.save(emoneyPocket);
			pocketService.save(bankPocket);
			subscriberService.saveSubscriber(subscriber);
			subscriberMdnService.saveSubscriberMDN(subscriberMDN);
			subscribersAdditionalFieldsService.save(additionalFields);
			
			log.info("Subscriber with MDN " + mdn + " Upgraded and Approved Successfully");
			return response;
		}
	} 

	/**
	 * Get all the groups from the system and generate Map with group name as key.
	 * @return
	 */
	private Map<String, Groups> getAllGroups() {
		Map<String, Groups> result = new Hashtable<String, Groups>();		
		GroupQuery groupQuery = new GroupQuery();
		groupQuery.setIncludeSystemGroups(true);
		List<Groups> lstGroup = groupService.get(groupQuery);
		if (CollectionUtils.isNotEmpty(lstGroup)) {
			for (Groups group:lstGroup) {
				result.put(group.getGroupname().toLowerCase(), group);
				if (group.getSystemgroup() != null && group.getSystemgroup() != 0) {
					defaultGroup = group;
				}
			}
		}
		if(defaultGroup == null)
			defaultGroup = groupService.getById(1l);
		return result;
	}
	
	/**
	 * Get all the Allowed SVA pocket templates for the subscriber and generate the map with Group as key. 
	 * @return
	 */
	private Map<String, PocketTemplate> getSVAPocketTemplatesForAllGroupsOfSubScriber() {
		Map<String, PocketTemplate> result = new Hashtable<String, PocketTemplate>();
		
		PocketTemplateConfigQuery ptcQuery = new PocketTemplateConfigQuery();
		ptcQuery.set_subscriberType(CmFinoFIX.SubscriberType_Subscriber);
		ptcQuery.set_commodity(CmFinoFIX.Commodity_Money);
		ptcQuery.set_pocketType(CmFinoFIX.PocketType_SVA);
		ptcQuery.set_isSuspensePocket(false);
		ptcQuery.set_isCollectorPocket(false);
		ptcQuery.set_isDefault(true);
		
		List<PocketTemplateConfig> lstPtc = pocketTemplateConfigService.get(ptcQuery);
		if (CollectionUtils.isNotEmpty(lstPtc)) {
			for (PocketTemplateConfig ptc: lstPtc) {
				result.put(generateStringKey(ptc), ptc.getPocketTemplate());
			}
		}
		return result;
	}
	
	/**
	 * generates a string from pocket template config object as key.
	 * @param ptc
	 * @return
	 */
	private String generateStringKey(PocketTemplateConfig ptc) {
		StringBuilder sb = new StringBuilder();
		sb.append(ptc.getKycLevel().getKyclevel());
		if (CollectionUtils.isNotEmpty(ptc.getPtcGroupMappings())) {
			sb.append(ptc.getPtcGroupMappings().iterator().next().getGroups().getId());
		}
		return sb.toString();
	}

	private CMJSError createSubscriber(String strLine,Integer subscriberType,String uploadUserName) {
		uploadedBy = uploadUserName;
		String strTokens[] = strLine.split(GeneralConstants.FIELD_SEPARATOR);
		if(strTokens.length==1)
        {
        	strTokens = strLine.split(GeneralConstants.FIELD_SEPARATOR_COMMA);
        }
		CMJSError response = new CMJSError();
		
		if (strTokens.length < UNBANKED_MINFIELD_COUNT) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_Field_Count);
			response.setErrorDescription("Insufficient field count :"+ strTokens.length + " Min should be 15");
			log.info(String.format("Bad line format: %s", strLine));
			return response;
		}else if (CmFinoFIX.RecordType_SubscriberSemiBanked.equals(subscriberType) && strTokens.length < SEMIBANKED_MINFIELD_COUNT) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_Field_Count);
			response.setErrorDescription("Insufficient field count :"+ strTokens.length + " Min should be 18");
			log.info(String.format("Bad line format: %s", strLine));
			return response;
		} else if (CmFinoFIX.RecordType_SubscriberFullyBanked.equals(subscriberType) && strTokens.length < FULLYBANKED_MINFIELD_COUNT) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_Field_Count);
			response.setErrorDescription("Insufficient field count :"+ strTokens.length + " Min should be 21");
			log.info(String.format("Bad line format: %s", strLine));
			return response;
		}
			SubscriberSyncRecord subscriberSyncRecord = new SubscriberSyncRecord();
			response = createSubscriberSyncRecord(subscriberSyncRecord,strTokens,subscriberType);
			subscriberSyncRecord.setAccountType(subscriberType);
			if (!SubscriberSyncErrors.Success.equals(response.getErrorCode())) {
				return response;
			}
			
			response=BulkUploadUtil.checkMDN(subscriberSyncRecord);
			if (subscriberSyncRecord.getId() != null && response.getErrorCode().equals(SubscriberSyncErrors.MDN_Already_Exist)) {
				response.setErrorCode(SubscriberSyncErrors.MDN_Already_Exist);
				response.setErrorDescription("Subscriber already exist with MDN:"+ subscriberSyncRecord.getMdn());
				return response;
			}
			try {
				response = createOrUpdateSubscriber(subscriberSyncRecord);
			} catch (Exception exp) {
				log.error("Exception in create subscriber from upload date", exp);
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Subscriber record creation  failed");
			}
		return response;
	}

	/**
	 * @param subscriberSyncRecord
	 * @return
	 */
	private CMJSError createOrUpdateSubscriber(SubscriberSyncRecord syncRecord)throws Exception {
		CMJSError result = new CMJSError();
		result.setErrorCode(SubscriberSyncErrors.Success);
		Subscriber subscriber = new Subscriber();
		SubscriberMdn subscriberMDN = new SubscriberMdn();
		AuthPersonDetails authorizingPerson = null;
		SubscriberGroups sg = null;
		boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
		if(syncRecord.getId()!=null){
			subscriberMDN = subscriberMdnService.getById(syncRecord.getId(), LockMode.UPGRADE);
			subscriber = subscriberMDN.getSubscriber();
			authorizingPerson = subscriber.getAuthPersonDetails();
		}else{
			subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_BulkUpload);
			subscriber.setCreatedby(uploadedBy);
			subscriberMDN.setCreatedby(uploadedBy);
		}
		
		if (StringUtils.isNotBlank(syncRecord.getAuthorizID())
				|| StringUtils.isNotBlank(syncRecord.getAuthorizFirstName())
				|| StringUtils.isNotBlank(syncRecord.getAuthorizLastName())
				|| StringUtils.isNotBlank(syncRecord.getAuthorizIDDesc())) {
			if(authorizingPerson==null){
			authorizingPerson = new AuthPersonDetails();
			authorizingPerson.setCreatedby(uploadedBy);
			}			
			updateAuthorizingPerson(authorizingPerson, subscriber, syncRecord);
		}
		
		if (!CmFinoFIX.RecordType_SubscriberUnBanked.equals(syncRecord.getAccountType())) {
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable);
			subscriber.setUpgradablekyclevel(BigDecimal.valueOf(syncRecord.getAccountType().longValue()));
			subscriber.setAppliedby(uploadedBy);
		}
		Long groupID = null;
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroupid().longValue();
		}
		else {
			if (mapGroup.get(syncRecord.getGroupName().toLowerCase()) != null) {
				groupID = mapGroup.get(syncRecord.getGroupName().toLowerCase()).getId().longValue();
			}
			else {
				groupID = defaultGroup.getId().longValue(); // Setting the default groupId in order to get the Pocket template.
			}
		}
		
		
		eMoneyPocketTemplate = mapGroupPocketTemp.get(syncRecord.getAccountType().toString()+groupID.toString());
		if(eMoneyPocketTemplate == null){
			//get default group template
			eMoneyPocketTemplate = mapGroupPocketTemp.get(syncRecord.getAccountType().toString()+defaultGroup.getId().toString());
		}
		if(isEMoneyPocketRequired == true){
			if (eMoneyPocketTemplate == null) {
				result.setErrorCode(SubscriberSyncErrors.Failure);
				result.setErrorDescription("Default Emoney Pocket Template not exist");
				return result;
			}
		}
		
		int responseCode = subscriberServiceExtended.createNewSubscriber(syncRecord,subscriber, subscriberMDN,uploadedBy);
	
		if (SubscriberSyncErrors.Success.equals(responseCode)) {
			SubscriberAddiInfo subscribersAdditionalFields = new SubscriberAddiInfo();
			subscribersAdditionalFields.setSubscriber(subscriber);
			subscribersAdditionalFields.setCreatedby(uploadedBy);
			updateAdditionalInfo(subscribersAdditionalFields, syncRecord);	
			
			String cardPan = pocketService.generateSVAEMoney16DigitCardPAN(syncRecord.getMdn());			
			if (authorizingPerson != null) {
				authorizingPersonService.save(authorizingPerson);
				subscriber.setAuthPersonDetails(authorizingPerson);
			}
		 	subscriberService.saveSubscriber(subscriber);
		 	if(syncRecord.getEmail() != null) { //send Email verification mail
		 		mailService.generateEmailVerificationMail(subscriber, syncRecord.getEmail());
			}
			subscriberMDN.setSubscriber(subscriber);
			subscriberMdnService.saveSubscriberMDN(subscriberMDN);
			if (subscribersAdditionalFields != null) {
				subscribersAdditionalFields.setSubscriber(subscriber);
				subscribersAdditionalFieldsService.save(subscribersAdditionalFields);
			}
			// Adding Group to new subscriber
			if(syncRecord.getId() == null) {
				sg = new SubscriberGroups();
				sg.setSubscriberid(subscriber.getId().longValue());
				sg.setGroupid((mapGroup.get(syncRecord.getGroupName().toLowerCase()) != null) ?
						mapGroup.get(syncRecord.getGroupName().toLowerCase()).getId().longValue() : defaultGroup.getId().longValue());
				subscriberGroupService.save(sg);
			}
			if (StringUtils.isNotBlank(syncRecord.getCardPan())&&CmFinoFIX.RecordType_SubscriberFullyBanked.equals(syncRecord.getAccountType())) {
				pocketService.createPocket(syncRecord.getPocketTemplateID(),subscriberMDN, CmFinoFIX.PocketStatus_Initialized,true, syncRecord.getCardPan());
			while(syncRecord.getCardPan().equals(cardPan)){
				 cardPan = pocketService.generateSVAEMoney16DigitCardPAN(syncRecord.getMdn());
			}
			}
			
			if(isEMoneyPocketRequired == true){
				if(syncRecord.getId()!=null){
					Long unregPocketTempId = systemParametersService.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED);
					if(unregPocketTempId>0){
						Pocket emoney = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), unregPocketTempId);
						if(emoney!=null&&emoney.getStatus()==(CmFinoFIX.PocketStatus_OneTimeActive)){
							emoney.setPocketTemplateByOldpockettemplateid(emoney.getPocketTemplateByPockettemplateid());
							emoney.setPocketTemplateByPockettemplateid(eMoneyPocketTemplate);
							emoney.setPockettemplatechangedby(uploadedBy);
							emoney.setStatus(CmFinoFIX.PocketStatus_Initialized);
							emoney.setStatustime(new Timestamp());
							pocketService.save(emoney);
						}else{
							pocketService.createPocket(eMoneyPocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true, cardPan);
						}
					}else{
						//return error
					}
				}else{
					pocketService.createPocket(eMoneyPocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true, cardPan);
				}
			// send otp
			NotificationWrapper wrapper = subscriberServiceExtended.generateOTPMessage(syncRecord.getOneTimePin(), notificationMethod);
			wrapper.setLanguage((int)subscriber.getLanguage());
			wrapper.setFirstName(subscriber.getFirstname());
			wrapper.setLastName(subscriber.getLastname());
			BulkUploadUtil.sendSMS(syncRecord.getMdn(), wrapper);
			if (sendEmailNotification && StringUtils.isNotBlank(subscriber.getEmail())) {
				String email = subscriber.getEmail();
				String name = subscriber.getFirstname();
				BulkUploadUtil.sendMail(email, name, emailSubject, wrapper);
			}
			if(syncRecord.getId()!=null){
				subscriberServiceExtended.updateUnRegisteredTxnInfoToActivated(subscriberMDN);
			}

			}

		} else {
			result.setErrorCode(SubscriberSyncErrors.Failure);
			result.setErrorDescription("Subscriber record creation failed");
		}
		return result;

	}

	private void updateAuthorizingPerson(AuthPersonDetails authorizingPerson,
										Subscriber subscriber, SubscriberSyncRecord syncRecord) {

//		authorizingPerson = subscriber.getAuthorizingPerson();
		if (StringUtils.isNotBlank(syncRecord.getAuthorizFirstName())) {
			authorizingPerson.setFirstname(syncRecord.getAuthorizFirstName());
		}
		if (StringUtils.isNotBlank(syncRecord.getAuthorizLastName())) {
			authorizingPerson.setLastname(syncRecord.getAuthorizLastName());
		}
		if (StringUtils.isNotBlank(syncRecord.getAuthorizID())) {
			authorizingPerson.setIdnumber(syncRecord.getAuthorizID());
		}
		if (StringUtils.isNotBlank(syncRecord.getAuthorizIDDesc())) {
			authorizingPerson.setIddesc(syncRecord.getAuthorizIDDesc());
		}
		if (syncRecord.getAuthorizDOB() != null) {
			authorizingPerson.setDateofbirth(new Timestamp(syncRecord.getDateOfBirth()));
		}
	}

	private void updateAdditionalInfo(SubscriberAddiInfo subscribersAdditionalFields,
					SubscriberSyncRecord syncRecord) {

		if (StringUtils.isNotBlank(syncRecord.getCertificateofIncorporation())) {
			subscribersAdditionalFields.setCertofincorporation(syncRecord
					.getCertificateofIncorporation());
		}
		if (syncRecord.getControlReference() != null) {
			subscribersAdditionalFields.setControllreference(Long.valueOf(syncRecord
					.getControlReference()));
		}
		if (StringUtils.isNotBlank(syncRecord.getCreditCheck())) {
			subscribersAdditionalFields.setCreditcheck(syncRecord
					.getCreditCheck());
		}
		if (StringUtils.isNotBlank(syncRecord.getNextKinNumber())) {
			subscribersAdditionalFields
					.setKinmdn(syncRecord.getNextKinNumber());
		}
		if (StringUtils.isNotBlank(syncRecord.getNextKinName())) {
			subscribersAdditionalFields.setKinname(syncRecord.getNextKinName());
		}
		if (StringUtils.isNotBlank(syncRecord.getNationality())) {
			subscribersAdditionalFields.setNationality(syncRecord
					.getNationality());
		}
		if (StringUtils.isNotBlank(syncRecord.getProofofaddress())) {
			subscribersAdditionalFields.setProofofaddress(syncRecord
					.getProofofaddress());
		}
		if (StringUtils.isNotBlank(syncRecord.getCompanyName())) {
			subscribersAdditionalFields.setSubscompanyname(syncRecord
					.getCompanyName());
		}
		if (StringUtils.isNotBlank(syncRecord.getMobileCompanyName())) {
			subscribersAdditionalFields.setSubscribermobilecompany(syncRecord
					.getMobileCompanyName());
		}
		if (StringUtils.isNotBlank(syncRecord.getMobileCompanyName())) {
			subscribersAdditionalFields.setSubscribermobilecompany(syncRecord
					.getMobileCompanyName());
		}
		if (StringUtils.isNotBlank(syncRecord.getMisc1())) {
			subscribersAdditionalFields.setMisc1(syncRecord.getMisc1());
		}
		if (StringUtils.isNotBlank(syncRecord.getMisc2())) {
			subscribersAdditionalFields.setMisc2(syncRecord.getMisc2());
		}
	}
	
	/**
	 * Verify if the pocket template id (set in subscriberSyncRecord) is defined in ptc-group mapping for the subscriber type.
	 * 
	 * @param subscriberSyncRecord
	 * @param subscriberType
	 * @return true if pocket id is defined in ptc group mapping else returns false.
	 */
	public boolean isValidPocketTemplateId(SubscriberSyncRecord subscriberSyncRecord, Integer subscriberType) {
		PocketTemplateConfigQuery ptcQuery = new PocketTemplateConfigQuery();
		ptcQuery.set_subscriberType(CmFinoFIX.SubscriberType_Subscriber);
		ptcQuery.set_commodity(CmFinoFIX.Commodity_Money);
		ptcQuery.set_pocketType(CmFinoFIX.PocketType_BankAccount);
		Groups group = mapGroup.get(subscriberSyncRecord.getGroupName().toLowerCase());
		ptcQuery.set_GroupID(group.getId().longValue());
		ptcQuery.set_KYCLevel(subscriberType.longValue());
		List <PocketTemplateConfig> results = pocketTemplateConfigService.get(ptcQuery);
		if(results.size() == 0) {
			return false;
		} else {
			for(PocketTemplateConfig ptc: results) {
				if(ptc.getPocketTemplate().getId().equals(subscriberSyncRecord.getPocketTemplateID())) {
					return true;
				}
			}
		}
		return false;
	}

	public CMJSError checkMandatoryFields(SubscriberSyncRecord syncRecord) {
		CMJSError response = new CMJSError();
		response.setErrorCode(SubscriberSyncErrors.Mandatory_Field_Missing);
		if (StringUtils.isBlank(syncRecord.getApplicationId())) {
			response.setErrorDescription("ApplicationID missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getFirstName())) {
			response.setErrorDescription("FirstName missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getLastName())) {
			response.setErrorDescription("LastName missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getMdn())) {
			response.setErrorDescription("Phone number missing");
			return response;
		}
		if (syncRecord.getLanguage() == null) {
			response.setErrorDescription("Language missing");
			return response;
		}

		// if (StringUtils.isBlank(syncRecord.getCurrency())) {
		// response.setErrorDescription("Currency missing");
		// return response;
		// }
		if (syncRecord.getDateOfBirth() == null) {
			response.setErrorDescription("DateofBirth missing");
			return response;
		}
		if (syncRecord.getPocketTemplateID() == null) {
			response
					.setErrorDescription("bank pocket pockettemplateID missing");
			return response;
		}
		if (syncRecord.getServiceType() == null) {
			response.setErrorDescription("subscriber type missing");
			return response;
		}

		if (syncRecord.getAccountStatus() == null) {
			response.setErrorDescription("account status missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getCity())) {
			response.setErrorDescription("city missing");
			return response;
		}
		if (syncRecord.getReferenceACNumber() == null) {
			response.setErrorDescription("reference account Number missing");
			return response;
		}

		// for now upload only fully banked
		// *FindbugsChange*
    	// Previous -- if (!ConfigurationUtil.getBulkUploadSubscriberKYClevel().equals(
		// conversion to long
		if (!ConfigurationUtil.getBulkUploadSubscriberKYClevel().equals(Long.valueOf(
				syncRecord.getAccountType()))) {
			response.setErrorDescription("invalid Account Type");
			return response;
		}

		// for now only fully banked are uploaded so other account types are
		// invalid
		// if(!CmFinoFIX.SubscriberAccountType_Unbanked.equals(accountType)){

		if (StringUtils.isBlank(syncRecord.getAddress())) {
			response.setErrorDescription("AddressLine1 missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getAddressline2())) {
			response.setErrorDescription("AddressLine2 missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getIdType())) {
			response.setErrorDescription("IDType missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getIdNumber())) {
			response.setErrorDescription("IDNumber missing");
			return response;
		}
		if (syncRecord.getIdExpireDate() == null) {
			response.setErrorDescription("IDExpiration date missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getProofofaddress())) {
			response.setErrorDescription("Proof of address missing");
			return response;
		}
		// add dob check
		// }

		// if(accountType.equals(3)){
		if (syncRecord.getBankAcType() == null) {
			response.setErrorDescription("bank account missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getCreditCheck())) {
			response.setErrorDescription("creditCheck missing");
			return response;
		}
		if (StringUtils.isBlank(syncRecord.getCardPan())) {
			response.setErrorDescription("cardPan missing");
			return response;
		} else {
			PocketTemplate pocketTemplate = pocketTemplateService.getById(syncRecord
					.getPocketTemplateID());
			if (!(pocketTemplate != null
					&& CmFinoFIX.PocketType_BankAccount.equals(pocketTemplate
							.getType()) && syncRecord.getBankAcType().equals(
					pocketTemplate.getBankaccountcardtype()))) {
				response.setErrorDescription("Invalid Pocket Template");
				return response;
			}
		}
		response = BulkUploadUtil.checkCardPan(syncRecord.getCardPan());
		// }

		if (StringUtils.isBlank(response.getErrorDescription())) {
			response.setErrorCode(SubscriberSyncErrors.Success);
		}
		return response;

	}

	private CMJSError createSubscriberSyncRecord(
			SubscriberSyncRecord subscriberSyncRecord, String[] result, Integer subscriberType) {
		CMJSError response = new CMJSError();
		response.setErrorCode(SubscriberSyncErrors.Success);
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		dateFormat.setLenient(false);
		int index = 0;
		subscriberSyncRecord.setFirstName(result[index++]);
		if(StringUtils.isBlank(subscriberSyncRecord.getFirstName())){
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("FirstName is empty ");
			log.info("FirstName is empty " + subscriberSyncRecord.getFirstName());
			return response;
		}
		subscriberSyncRecord.setLastName(result[index++]);
		if(StringUtils.isBlank(subscriberSyncRecord.getLastName())){
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("LastName is empty ");
			log.info("LastName is empty " + subscriberSyncRecord.getLastName());
			return response;
		}
		subscriberSyncRecord.setMdn(subscriberService.normalizeMDN(result[index++]));
		if(subscriberSyncRecord.getMdn().length()>MDN_MAX_LENGTH||subscriberSyncRecord.getMdn().length()<MDN_MIN_LENGTH){
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("MDN length should be between "
					+MDN_MIN_LENGTH+" and "+MDN_MAX_LENGTH);
			log.info("Invalid MDN" + subscriberSyncRecord.getMdn());
			return response;
		}
		if(!StringUtils.isNumeric(subscriberSyncRecord.getMdn())||subscriberSyncRecord.getMdn().length()>MDN_MAX_LENGTH){
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid MDN "
					+subscriberSyncRecord.getMdn());
			log.info("Invalid MDN" + subscriberSyncRecord.getMdn());
			return response;
		}
		
		/*String accountType = result[index++];		
		try {
			subscriberSyncRecord.setAccountType(Integer.valueOf(accountType));
		} catch (Exception e) {
			response.setErrorCode(SubscriberSyncErrors.InvalidAccountType);
			response.setErrorDescription("Invalid Account Type:"+ accountType);
			log.info("Invalid Account Type:" + accountType, e);
			return response;
		}*/
		String bankAcType = result[index++];
		String cardPan = result[index++];
		if(CmFinoFIX.RecordType_SubscriberFullyBanked.equals(subscriberType)){
		try {
			subscriberSyncRecord.setPocketTemplateID(Long.valueOf(bankAcType));
		} catch (Exception e) {
			response.setErrorCode(SubscriberSyncErrors.InvalidBankAccountType);
			response.setErrorDescription("Invalid BankAccount TemplateID:"+ bankAcType);
			log.error("Invalid BankAccount TemplateID:" + bankAcType);
			return response;
		}
		PocketTemplate template=pocketTemplateService.getById(subscriberSyncRecord.getPocketTemplateID());
		if(template==null||!CmFinoFIX.PocketType_BankAccount.equals(template.getType())){
			response.setErrorCode(SubscriberSyncErrors.InvalidBankAccountType);
			response.setErrorDescription("Invalid BankAccount TemplateID:"+ bankAcType);
			log.error("Invalid BankAccount TemplateID:" + bankAcType);
			return response;
		}
		if(StringUtils.isBlank(cardPan)||!getCardPanLength().contains(cardPan.length()) |!StringUtils.isNumeric(cardPan)){
			response.setErrorCode(SubscriberSyncErrors.InvalidBankAccountType);
			response.setErrorDescription("Invalid CardPan:"+ cardPan);
			log.error("Invalid CardPan:"+ cardPan);
			return response;
		}
		response = BulkUploadUtil.checkCardPan(cardPan);
		if(!response.getErrorCode().equals(SubscriberSyncErrors.Success)){
			return response;
		}
		subscriberSyncRecord.setCardPan(cardPan);
		}		
		subscriberSyncRecord.setAddress(result[index++]);
		subscriberSyncRecord.setAddressline2(result[index++]);
		if(!CmFinoFIX.RecordType_SubscriberUnBanked.equals(subscriberType)&&StringUtils.isBlank(subscriberSyncRecord.getAddressline2())){			
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Invalid Address Line2:"+ subscriberSyncRecord.getAddressline2());
				log.info("Invalid Address Line2:"+ subscriberSyncRecord.getAddressline2());
				return response;
			}
		subscriberSyncRecord.setCity(result[index++]);
		if(StringUtils.isBlank(subscriberSyncRecord.getCity())){			
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid Address city:"+ subscriberSyncRecord.getCity());
			log.info("Invalid Address City:"+ subscriberSyncRecord.getCity());
			return response;
		}
		subscriberSyncRecord.setEmail(result[index++]);
		if(StringUtils.isNotBlank(subscriberSyncRecord.getEmail())&&!mailService.isValidEmailAddress(subscriberSyncRecord.getEmail())){
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid EmailId"+ subscriberSyncRecord.getEmail());
			log.info("Invalid EmailId"+ subscriberSyncRecord.getEmail());
			return response;
		}
		subscriberSyncRecord.setIdType(result[index++]);
		if(!CmFinoFIX.RecordType_SubscriberUnBanked.equals(subscriberType)&&StringUtils.isBlank(subscriberSyncRecord.getIdType())){			
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid IDType:"+ subscriberSyncRecord.getIdType());
			log.info("Invalid IDType:"+ subscriberSyncRecord.getIdType());
			return response;
		}
		subscriberSyncRecord.setIdNumber(result[index++]);
		if(!CmFinoFIX.RecordType_SubscriberUnBanked.equals(subscriberType)&&StringUtils.isBlank(subscriberSyncRecord.getIdNumber()))
		{			
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid IdNumber:"+ subscriberSyncRecord.getIdNumber());
			log.info("Invalid IdNumber:"+ subscriberSyncRecord.getIdNumber());
			return response;
		}
		String idexp = result[index++];
		if(!CmFinoFIX.RecordType_SubscriberUnBanked.equals(subscriberType)){				
			
			if (idexp.length() != 8) 
			{
				response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
				response.setErrorDescription("Invalid length for IDExpirationTime:"	+ idexp);
				log.info("invalid date format");
				return response;
			}
			Date idExpirationDate = null;
			try {
				idExpirationDate = dateFormat.parse(idexp);
				subscriberSyncRecord.setIdExpireDate(idExpirationDate);
			} catch (ParseException e) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
				response.setErrorDescription("Invalid DateFormat for IDExpirationTime:"+ idexp);
				log.error("parsing error", e);
				return response;
			}
			Date today = DateUtils.truncate(new Date(), Calendar.DATE);
					if(idExpirationDate==null || today.after(idExpirationDate))
					{
						response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
						response.setErrorDescription("Invalid IDExpirationTime:"	+ idexp);
						log.info("Date of Expiry should be after Today's date ");
						return response;
					}
		}		
		String dob = result[index++];
		if (dob.length() != 8) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
			response.setErrorDescription("Invalid length for DateofBirth:"+ dob);
			log.info("invalid date format");
			return response;
		}
		Date dateOfBirth = null;
		try {
			dateOfBirth = dateFormat.parse(dob);
			subscriberSyncRecord.setDateOfBirth(dateOfBirth);
		} catch (ParseException e) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
			response.setErrorDescription("Invalid DateFormat for DateofBirth:"+ dob);
			log.error("parsing error", e);
			return response;
		}
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		if(dateOfBirth==null || today.before(dateOfBirth))
		{
			response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
			response.setErrorDescription("Invalid DateofBirth:"	+ dateOfBirth);
			log.info("Date of Birth should be before Today's date ");
			return response;
		}
		subscriberSyncRecord.setGroupName(result[index++]);
		if (StringUtils.isNotBlank(subscriberSyncRecord.getGroupName()) && (mapGroup.get(subscriberSyncRecord.getGroupName().toLowerCase()) == null) ) {
			response.setErrorCode(SubscriberSyncErrors.Invalid_GroupName);
			response.setErrorDescription("Group Name does not exist: "	+ subscriberSyncRecord.getGroupName());
			log.info("Group Name does not exist: "	+ subscriberSyncRecord.getGroupName());
			return response;
		}
		String branchCode = result[index++];
		if (!StringUtils.isNumeric(branchCode)) {
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid branchcode "+ branchCode);
			log.info("invalid branch code "+branchCode);
			return response;
		}
		subscriberSyncRecord.setApplicationId(branchCode);
		if(subscriberSyncRecord.getDateOfBirth().after(DateUtil.addYears(new Date(), -18))){
			if(result.length!=TOTAL_FIELD_COUNT){
				response.setErrorCode(SubscriberSyncErrors.Invalid_Field_Count);
				response.setErrorDescription("Authorization details required as age is under 18 ");
				log.info(String.format("Authorization details required as age is under 18 "));
				return response;	
			}
		}
		try{
		subscriberSyncRecord.setProofofaddress(result[index++]);
		if(!CmFinoFIX.RecordType_SubscriberUnBanked.equals(subscriberType)&&StringUtils.isBlank(subscriberSyncRecord.getProofofaddress())){			
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid ProofOfAddress:"+ subscriberSyncRecord.getProofofaddress());
			log.info("Invalid ProofOfAddress:"+ subscriberSyncRecord.getProofofaddress());
			return response;
		}
		
		subscriberSyncRecord.setNextKinName(result[index++]);
		subscriberSyncRecord.setNextKinNumber(result[index++]);
		if(!CmFinoFIX.RecordType_SubscriberUnBanked.equals(subscriberType)){
			if(StringUtils.isBlank(subscriberSyncRecord.getNextKinName())){
			response.setErrorCode(SubscriberSyncErrors.Failure);
			response.setErrorDescription("Invalid Next of Kin Name:"+ subscriberSyncRecord.getNextKinName());
			log.info("Invalid Next of Kin Name:"+ subscriberSyncRecord.getNextKinName());
			return response;
			}
			if(StringUtils.isBlank(subscriberSyncRecord.getNextKinNumber())){
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Invalid Next of Kin Number:"+ subscriberSyncRecord.getNextKinNumber());
				log.info("Invalid Next of Kin Number:"+ subscriberSyncRecord.getNextKinNumber());
				return response;
				}
		}
		if(StringUtils.isNotBlank(subscriberSyncRecord.getNextKinNumber())){
			if(!StringUtils.isNumeric(subscriberSyncRecord.getNextKinNumber())){
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Invalid Next of Kin Number:"+ subscriberSyncRecord.getNextKinNumber());
				log.info("Invalid Next of Kin Number:"+ subscriberSyncRecord.getNextKinNumber());
				return response;
			}
		}
		if(CmFinoFIX.RecordType_SubscriberFullyBanked.equals(subscriberType)){
			// Check(only for fully banked) if the pocket template is valid as per the PTC group mapping defined
			if(!isValidPocketTemplateId(subscriberSyncRecord, subscriberType)) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_PocketTemplate);
				response.setErrorDescription("Invalid pocket template as per the ptc group mapping:"+ subscriberSyncRecord.getPocketTemplateID());
				log.info("Invalid pocket template as per the ptc group mapping:"+ subscriberSyncRecord.getPocketTemplateID());
				return response;
			}
		}		
		subscriberSyncRecord.setAuthorizFirstName(result[index++]);
		subscriberSyncRecord.setAuthorizLastName(result[index++]);
		subscriberSyncRecord.setAuthorizID(result[index++]);
		if(subscriberSyncRecord.getDateOfBirth().after(DateUtil.addYears(new Date(), -18))){
			if(StringUtils.isBlank(subscriberSyncRecord.getAuthorizLastName())){
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Invalid Authorizing last name:"+ subscriberSyncRecord.getAuthorizLastName());
				log.info("Invalid Authorizing last name:"+ subscriberSyncRecord.getAuthorizLastName());
				return response;
				}
			if(StringUtils.isBlank(subscriberSyncRecord.getAuthorizFirstName())){
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription("Invalid Authorizing first name:"+ subscriberSyncRecord.getAuthorizFirstName());
				log.info("Invalid Authorizing first name:"+ subscriberSyncRecord.getAuthorizFirstName());
				return response;
				}
			dob = result[index++];
			if (dob.length() != 8) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
				response.setErrorDescription("Invalid length for AuthorizingPersonDateofBirth:"+ dob);
				log.info("invalid date format");
				return response;
			}
	        dateOfBirth = null;
			try {
	            dateOfBirth = dateFormat.parse(dob);
				subscriberSyncRecord.setAuthorizDOB(dateOfBirth);
			} catch (ParseException e) {
				response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
				response.setErrorDescription("Invalid DateFormat for AuthorizingPersonDateofBirth:"+ dob);
				log.error("parsing error", e);
				return response;
			}
			 today = DateUtils.truncate(new Date(), Calendar.DATE);
			if(dateOfBirth==null ||today.after(dateOfBirth))
			{
				response.setErrorCode(SubscriberSyncErrors.Invalid_DateFormat);
				response.setErrorDescription("Invalid DateofBirth:"	+ idexp);
				log.info("Date of Birth of AuthorizingPerson should be before Today's date ");
				return response;
			}
			
			if(subscriberSyncRecord.getAuthorizDOB().after(DateUtil.addYears(new Date(), -18))){
				response.setErrorCode(SubscriberSyncErrors.Failure);
				response.setErrorDescription(" AuthorizingPerson age is less than 18years:"+ dob);
				log.info("AuthorizingPerson age is less than 18years:"+ dob);
				return response;
			}
			}
			}catch (Exception e) {
				log.info("Exception sync data",e);
			}
		
		subscriberSyncRecord.setServiceType(CmFinoFIX.SubscriberType_Subscriber);
		return response;
	}

	public List<Integer> getCardPanLength() {
		return cardPanLength;
	}

	public void setCardPanLength(List<Integer> cardPanLength) {
		this.cardPanLength = cardPanLength;
	}
}
