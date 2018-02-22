package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.BranchCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubsUpgradeBalanceLogDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.SubscribersAdditionalFieldsQuery;
import com.mfino.domain.Address;
import com.mfino.domain.BranchCodes;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.KycLevel;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SMSValues;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeBalanceLog;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberUpgradeKyc;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscribersAdditionalFieldsService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SubscriberUpgradeKycProcessor;

@Service("SubscriberUpgradeKycProcessorImpl")
public class SubscriberUpgradeKycProcessorImpl extends BaseFixProcessor implements
		SubscriberUpgradeKycProcessor {

	private SubsUpgradeBalanceLogDAO subsUpgradeBalanceLogDAO = DAOFactory.getInstance().getSubsUpgradeBalanceLogDAO();
	private SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
	private SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private BranchCodeDAO branchCodeDao = DAOFactory.getInstance().getBranchCodeDAO(); 
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	private KYCLevelDAO kycLevelDao = DAOFactory.getInstance().getKycLevelDAO();
	private SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
	private ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
	
	private static final String DEFAULT_BRANCH = "000";
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscribersAdditionalFieldsServiceImpl")
	private SubscribersAdditionalFieldsService subscriberAdditionalFieldsService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In SubscriberUpgradeKycProcessImpl Process method");
		
		CMJSSubscriberUpgradeKyc realMsg = (CMJSSubscriberUpgradeKyc) msg;
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		TransactionLog transactionsLog = null;
		
		SubscriberMdn subscriberMDN = subMdndao.getById(realMsg.getID());
        
		if(subscriberMDN == null){
        	error.setErrorDescription(MessageText._("Invalid MDN ID"));
        	return error;
        }
        
        if(subscriberMDN.getUpgradeacctstatus() != null && 
			subscriberMDN.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeStatus_Approve &&
			subscriberMDN.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeStatus_Reject ){
        	
    		error.setErrorDescription(MessageText._("Subscriber Upgrade Not Allowed "));
        	return error;
    	}
        
        Subscriber subscriber = subscriberMDN.getSubscriber();
        Integer subscriberStatus = subscriber.getStatus();
        
        if( !subscriberStatus.equals(CmFinoFIX.SubscriberStatus_Active) ){
        	
        	error.setErrorDescription(MessageText._("Subscriber Should be Active! "));
        	return error;
        }
        
        Long groupID = null;
		List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(subscriber.getId());
		if(subscriberGroups != null && !subscriberGroups.isEmpty()){
			SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroupid();
		}
        
        KycLevel nonKycLevel = kycLevelDao.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_NoKyc.longValue());
        PocketTemplate eMoneyNonKycTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(
        		nonKycLevel.getKyclevel(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
        
        if (eMoneyNonKycTemplate == null) {
        	error.setErrorDescription(MessageText._("Emoney - Non Kyc Not Available."));
        	return error;
        }
        
        Pocket nonKycPocket = getNonKycPocket(subscriberMDN, eMoneyNonKycTemplate);
		if (nonKycPocket == null){
			error.setErrorDescription(MessageText._("Subscriber Not Have Emoney-Non KYC Pocket."));
        	return error;
		}

        String actionString = realMsg.getaction();
        log.info("Action is :"+actionString);
        if (StringUtils.equals(actionString, "default")){
        	SubscriberUpgradeData subscriberUpgradeData = subscriberUpgradeDataDAO.getSubmitedRequestData(subscriberMDN.getId(), CmFinoFIX.SubscriberActivity_Upgrade_From_NonKyc_To_KYC);
    		realMsg.settotal(0);
        	if(subscriberUpgradeData != null){
        		realMsg.allocateEntries(1);
        		String idTypeValue = enumTextService.getEnumTextValue(CmFinoFIX.TagID_IDTypeForKycUpgrade, null, 
        				subscriberUpgradeData.getIdType());
        		CMJSSubscriberUpgradeKyc.CGEntries entry = new CMJSSubscriberUpgradeKyc.CGEntries();
        		entry.setBirthPlace(subscriberUpgradeData.getBirthPlace());
        		entry.setDateOfBirth(subscriberUpgradeData.getBirthDate());
        		entry.setEmail(subscriberUpgradeData.getEmail());
        		entry.setFirstName(subscriberUpgradeData.getFullName());
        		entry.setIDType(subscriberUpgradeData.getIdType());
        		entry.setIDNumber(subscriberUpgradeData.getIdNumber());
        		entry.setKTPDocumentPath(subscriberUpgradeData.getIdCardScanPath());
        		entry.setMothersMaidenName(subscriberUpgradeData.getMotherMaidenName());
        		entry.setID(subscriberMDN.getId());
        		entry.setIDTypeText(idTypeValue);
        		entry.setNationality(subscriberUpgradeData.getNationality());
        		entry.setWork(subscriberUpgradeData.getJob());
        		entry.setWorkText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_JobList, null, subscriberUpgradeData.getJob()));
        		entry.setGender(subscriberUpgradeData.getGender());
        		entry.setGenderText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Gender, null, subscriberUpgradeData.getGender()));
        		entry.setMaritalStatus(subscriberUpgradeData.getMaritalStatus());
        		entry.setMaritalStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_MaritalStatusList, null, subscriberUpgradeData.getMaritalStatus()));
        		entry.setSourceOfFund(subscriberUpgradeData.getSourceOfFund());
        		entry.setIncome(subscriberUpgradeData.getAvgMonthlyIncome());
        		entry.setIncomeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_AvgIncomeList, null, subscriberUpgradeData.getAvgMonthlyIncome()));
        		entry.setGoalOfAcctOpening(subscriberUpgradeData.getEmoneyOpeningPurpose());
        		entry.setOtherWork(subscriberUpgradeData.getOtherJob());
        		Address address = subscriberUpgradeData.getAddress();
        		if(address != null){
	        		entry.setCity(address.getCity());
	        		entry.setRegionName(address.getRegionname());
	        		entry.setState(address.getState());
	        		entry.setSubState(address.getSubstate());
	        		entry.setStreetAddress(address.getLine2());
        		}
        		realMsg.getEntries()[0] = entry;
        		realMsg.settotal(1);
        	} 
        	realMsg.setsuccess(Boolean.TRUE);
        	return realMsg;
        } 
        
		if(subscriberMDN.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeStatus_Initialized) {
			
			String makerUsername = subscriberMDN.getUpgradeacctrequestby();
			MfinoUser makerUser = userService.getByUserName(makerUsername);
			
			Long checkerUserBranchId = userService.getCurrentUser().getBranchcodeid();
			BranchCodes branchCodes = branchCodeDao.getById(checkerUserBranchId);
			
			if((makerUser != null && makerUser.getBranchcodeid() == checkerUserBranchId) || 
					(branchCodes != null && StringUtils.equals(branchCodes.getBranchcode(), DEFAULT_BRANCH))){
				
				 KycLevel unBankedLevel = kycLevelDao.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_UnBanked.longValue());
			     PocketTemplate eMoneyUnBankedTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(unBankedLevel.getKyclevel(), 
			    		 true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
				
            	if (eMoneyUnBankedTemplate == null){
    				error.setErrorDescription(MessageText._("Emoney - UnBanked Not Available."));
                	return error;
    			}
    			
    			Integer notificationCode = updateStatus(realMsg, error, subscriberMDN, subscriber, 
    					eMoneyUnBankedTemplate, nonKycPocket, unBankedLevel);
        		
        		error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
        		sendSMS(subscriberMDN,notificationCode);
        		
			} else{
				error.setErrorDescription(MessageText._("Admin's Branch is not available or different"));
            	return error;
			}
			
		} else{
    		error.setErrorDescription(MessageText._("Subscriber Upgrade Not Allowed "));
        	return error;
    	}
		
		
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSSubscriberUpgradeKyc, realMsg.DumpFields());
		
		ChannelCode channelCode   =	channelCodeService.getChannelCodeByChannelCode("2");

		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(null);
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(channelCode.getId());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.SUBSCRIBER_UPGRADE_KYC);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getId());

		try{
			Transaction charge = transactionChargingService.getCharge(serviceCharge);
			ServiceChargeTxnLog serviceChargeTransactionLog = charge.getServiceChargeTransactionLog();
			serviceChargeTransactionLog.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
			sctlDAO.save(serviceChargeTransactionLog);
			
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			error.setCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			error.setErrorDescription(MessageText._("ServiceNotAvailable"));
        	return error;
		
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			error.setCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			error.setErrorDescription(MessageText._("ServiceNotAvailable"));
        	return error;
		}
		
		return error;
	}

	private Integer updateStatus(CMJSSubscriberUpgradeKyc realMsg,
			CMJSError error, SubscriberMdn subscriberMDN,
			Subscriber subscriber, PocketTemplate eMoneyUnBankedTemplate,
			Pocket nonKycPocket, KycLevel unBankedLevel) {
		
		Integer upgradeStatus = realMsg.getSubscriberUpgradeStatus();
		subscriberMDN.setUpgradeacctstatus(upgradeStatus);
		subscriberMDN.setUpgradeacctcomments(realMsg.getUpgradeAcctComments());
		subscriberMDN.setUpgradeacctapprovedby(userService.getCurrentUser().getUsername());
		subscriberMDN.setUpgradeaccttime(new Timestamp());
		
		
		SubscriberUpgradeData upgradeData = subscriberUpgradeDataDAO.getSubmitedRequestData(subscriberMDN.getId(), CmFinoFIX.SubscriberActivity_Upgrade_From_NonKyc_To_KYC);
		Integer notificationCode = null;
		if(upgradeData != null){
			if (upgradeStatus == CmFinoFIX.SubscriberUpgradeStatus_Approve){
				nonKycPocket.setPocketTemplateByOldpockettemplateid(nonKycPocket.getPocketTemplateByPockettemplateid());			
				nonKycPocket.setPocketTemplateByPockettemplateid(eMoneyUnBankedTemplate);
				nonKycPocket.setPockettemplatechangedby(userService.getCurrentUser().getUsername());
				nonKycPocket.setPockettemplatechangetime(new Timestamp());
				pocketDAO.save(nonKycPocket);

				SubscriberUpgradeBalanceLog subUpgradeBalanceLog = new SubscriberUpgradeBalanceLog();
				subUpgradeBalanceLog.setSubscriberId(subscriber.getId());
				subUpgradeBalanceLog.setPockatBalance(nonKycPocket.getCurrentbalance());
				subUpgradeBalanceLog.setTxnDate(new Timestamp());
				subUpgradeBalanceLog.setCreatedby(getLoggedUserName());
				subUpgradeBalanceLog.setCreatetime(new Timestamp());
				subUpgradeBalanceLog.setUpdatedby(getLoggedUserName());
				subUpgradeBalanceLog.setLastupdatetime(new Timestamp());
				subsUpgradeBalanceLogDAO.save(subUpgradeBalanceLog);
    			
				
				subscriber.setGender(upgradeData.getGender());
				subscriber.setAddressBySubscriberaddressid(upgradeData.getAddress());
				subscriber.setEmail(upgradeData.getEmail());
				subscriber.setBirthplace(upgradeData.getBirthPlace());
				subscriber.setDateofbirth(upgradeData.getBirthDate());
				subscriber.setFirstname(upgradeData.getFullName());
				subscriber.setMothersmaidenname(upgradeData.getMotherMaidenName());
				subscriber.setKycLevel(unBankedLevel);
				subscriberDao.save(subscriber);

				SubscribersAdditionalFieldsDAO subAddFieldDao = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
				SubscribersAdditionalFieldsQuery query = new SubscribersAdditionalFieldsQuery();
				query.set_SubscriberID(subscriber.getId().intValue());
				List<SubscriberAddiInfo> addInfo = subAddFieldDao.get(query);
				if(addInfo != null && !addInfo.isEmpty()) {
					for (SubscriberAddiInfo subscriberAddInfo : addInfo) {
						subscriberAddInfo.setNationality(upgradeData.getNationality());
						subscriberAddInfo.setWork(upgradeData.getJob());
						if(upgradeData.getJob().equals(CmFinoFIX.WorkList_Lainnya.toString())) {
							subscriberAddInfo.setOtherwork(upgradeData.getOtherJob());
						}else {
							subscriberAddInfo.setOtherwork(null);
						}
						subscriberAddInfo.setOtherwork(upgradeData.getOtherJob());
						subscriberAddInfo.setIncome(upgradeData.getAvgMonthlyIncome());
						subscriberAddInfo.setSourceoffund(upgradeData.getSourceOfFund());
						subscriberAddInfo.setGoalofacctopening(upgradeData.getEmoneyOpeningPurpose());
						subscriberAddInfo.setMaritalStatus(upgradeData.getMaritalStatus());
						subscriberAddInfo.setSubscriber(subscriber);
						subAddFieldDao.save(subscriberAddInfo);
						log.info("updated subscriber additional info: "+ subscriberAddInfo.getId());
					}
				}else {
					SubscriberAddiInfo subscriberAddInfo = new SubscriberAddiInfo();
					subscriberAddInfo.setNationality(upgradeData.getNationality());
					subscriberAddInfo.setWork(upgradeData.getJob());
					subscriberAddInfo.setOtherwork(upgradeData.getOtherJob());
					subscriberAddInfo.setIncome(upgradeData.getAvgMonthlyIncome());
					subscriberAddInfo.setSourceoffund(upgradeData.getSourceOfFund());
					subscriberAddInfo.setGoalofacctopening(upgradeData.getEmoneyOpeningPurpose());
					subscriberAddInfo.setMaritalStatus(upgradeData.getMaritalStatus());
					subscriberAddInfo.setSubscriber(subscriber);
					subAddFieldDao.save(subscriberAddInfo);
					log.info("updated subscriber additional info: "+ subscriberAddInfo.getId());
				}
				
				subscriberMDN.setIdtype(upgradeData.getIdType());
				subscriberMDN.setIdnumber(upgradeData.getIdNumber());
				subscriberMDN.setKtpdocumentpath(upgradeData.getIdCardScanPath());
				
				error.setErrorDescription(MessageText._("Request for Subscriber Upgraded is Approved successfully"));
				notificationCode = CmFinoFIX.NotificationCode_SubscriberUpgradeRequestApproved;
				log.info("Request for Subscriber Upgraded Approved successfully");
				upgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				
			} else if(upgradeStatus == CmFinoFIX.SubscriberUpgradeKycStatus_Revision){
				error.setErrorDescription(MessageText._("Request for Subscriber Need Revision"));
				notificationCode = CmFinoFIX.NotificationCode_SubscriberUpgradeRequestRevision;
				log.info("Request for Subscriber Need Revision");
				upgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
				
			} else {
				subscriberMDN.setUpgradeacctstatus(CmFinoFIX.SubscriberUpgradeKycStatus_Reject);
				error.setErrorDescription(MessageText._("Request for Subscriber Upgraded is Rejected successfully"));
				notificationCode = CmFinoFIX.NotificationCode_SubscriberUpgradeRequestRejected;
				log.info("Request for Subscriber Upgraded Rejected successfully");
				upgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
			}
		}
		
		upgradeData.setSubsActivityApprovedBY(userService.getCurrentUser().getUsername());
		upgradeData.setSubsActivityAprvTime(new Timestamp());
		subscriberUpgradeDataDAO.save(upgradeData);
		
		subMdndao.save(subscriberMDN);
		return notificationCode;
	}

	private Pocket getNonKycPocket(SubscriberMdn subscriberMDN,
			PocketTemplate eMoneyNonKycTemplate) {
		PocketQuery pocketQuery= new PocketQuery();
		pocketQuery.setPocketTemplateID(eMoneyNonKycTemplate.getId());
		pocketQuery.setMdnIDSearch(subscriberMDN.getId());
		List<Pocket> pocketList = pocketDAO.get(pocketQuery);
		if(pocketList != null && pocketList.size() > 0)
			return pocketList.get(0);
		else
			return null;
	}
	
	private void sendSMS (SubscriberMdn subscriberMDN , Integer notificationCode) {
		try{
			Subscriber subscriber = subscriberMDN.getSubscriber();
			String mdn2 = subscriberMDN.getMdn();
			
			NotificationWrapper smsNotificationWrapper = new NotificationWrapper();
			smsNotificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsNotificationWrapper.setCode(notificationCode);
			smsNotificationWrapper.setDestMDN(mdn2);
			smsNotificationWrapper.setLanguage(subscriber.getLanguage());
			smsNotificationWrapper.setFirstName(subscriber.getFirstname());
	    	smsNotificationWrapper.setLastName(subscriber.getLastname());
			
	    	String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper, true);
			SMSValues smsValues= new SMSValues();
			smsValues.setDestinationMDN(mdn2);
			smsValues.setMessage(smsMessage);
			smsValues.setNotificationCode(smsNotificationWrapper.getCode());
			
			smsService.asyncSendSMS(smsValues);
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error in Sending SMS "+e.getMessage(),e);
		}
	}
}
