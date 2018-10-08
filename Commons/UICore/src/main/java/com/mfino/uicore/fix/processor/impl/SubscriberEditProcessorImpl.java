package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.KtpDetailsDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubsUpgradeBalanceLogDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.EnumTextQuery;
import com.mfino.dao.query.KtpDetailsQuery;
import com.mfino.dao.query.SubscribersAdditionalFieldsQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.Address;
import com.mfino.domain.EnumText;
import com.mfino.domain.KtpDetails;
import com.mfino.domain.KycLevel;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SMSValues;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeBalanceLog;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.errorcodes.Codes;
import com.mfino.exceptions.SubscriberRetiredException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSForwardNotificationRequest;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberEdit;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SubscribersAdditionalFieldsService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ForwardNotificationRequestProcessor;
import com.mfino.uicore.fix.processor.SubscriberEditProcessor;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

@Service("SubscriberEditProcessorImpl")
public class SubscriberEditProcessorImpl extends BaseFixProcessor implements SubscriberEditProcessor {

	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	private SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private KYCLevelDAO kyclevelDao = DAOFactory.getInstance().getKycLevelDAO();
	private  boolean sendOTPOnIntialized;

	private KtpDetailsDAO ktpDetailsDAO= DAOFactory.getInstance().getKtpDetailsDAO();
	private KtpDetailsQuery ktpDetailsQuery = new KtpDetailsQuery();
	
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
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
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
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("ForwardNotificationRequestProcessorImpl")
	private ForwardNotificationRequestProcessor forwardNotificationRequestProcessor;

	@Autowired
	@Qualifier("SubscribersAdditionalFieldsServiceImpl")
	private SubscribersAdditionalFieldsService subscribersAdditionalFieldsService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In SubscriberEditProcessorImpl Process method");
		CMJSSubscriberEdit realMsg = (CMJSSubscriberEdit) msg;
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		SubscriberMdn subscriberMDN = subMdndao.getById(realMsg.getMDNID());
		if(subscriberMDN == null){
        	error.setErrorDescription(MessageText._("Invalid MDN ID"));
        	return error;
        }
    	Subscriber subscriber = subscriberMDN.getSubscriber();
		String actionString = realMsg.getaction();
		log.info("Action is :"+actionString);
		SubscriberUpgradeData subscriberUpgradeData = subscriberUpgradeDataDAO.getSubmitedRequestData(
				subscriberMDN.getId(), CmFinoFIX.SubscriberActivity_Edit_Subscriber_Details);
		
        if (StringUtils.equals(actionString, "default")){
    		realMsg.settotal(0);
    		CMJSSubscriberEdit.CGEntries entry = new CMJSSubscriberEdit.CGEntries();
    		
    		if(subscriberUpgradeData != null){
        		realMsg.allocateEntries(2);
        		displaySubmitedRequestData(subscriberMDN, subscriberUpgradeData, entry);
        		
        		CMJSSubscriberEdit.CGEntries entryOldValue = new CMJSSubscriberEdit.CGEntries();
        		displayExistingSubscriberData(subscriberMDN, subscriber, entryOldValue);
        		
        		realMsg.getEntries()[0] = entry;
        		realMsg.getEntries()[1] = entryOldValue;
        		realMsg.settotal(2);
    		} else{
        		realMsg.allocateEntries(1);
        		displayExistingSubscriberData(subscriberMDN, subscriber, entry);
        		realMsg.getEntries()[0] = entry;
        		realMsg.settotal(1);
    		}
    		
        	realMsg.setsuccess(Boolean.TRUE);
        	return realMsg;
        	
        } else{
        	if(subscriberUpgradeData == null){
        		error.setErrorDescription(MessageText._("Subscriber Edit data is not available"));
        		return error;
        	}
        		
        	if(realMsg.getSubscriberUpgradeStatus() == CmFinoFIX.SubscriberUpgradeStatus_Approve){
	        	//approveSubscriberEdit(realMsg, subscriberMDN, subscriber, subscriberUpgradeData);

        		Integer mdnRestrictions = subscriberUpgradeData.getSubscriberRestriction();
        		boolean isLakuapandiaSubscriber = false;
				Set<Pocket> subPockets = subscriberMDN.getPockets();
				for (Iterator<Pocket> iterator = subPockets.iterator(); iterator.hasNext();) {
					Pocket pocket = (Pocket) iterator.next();
					if(pocket.getPocketTemplateByPockettemplateid().getType().equals(CmFinoFIX.PocketType_LakuPandai)) {
						isLakuapandiaSubscriber = true;
					}
				}
				
				Integer oldRestrictions = subscriberMDN.getRestrictions();
				
	        	if(CmFinoFIX.SubscriberStatus_Active.equals(subscriberUpgradeData.getSubscriberStatus()) && !isActivationAllowed(subscriber, subscriberUpgradeData)){
					error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Subscriber Activation not allowed."));
					log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Subscriber Activation not allowed for user:"+ getLoggedUserNameWithIP());
					return error;
				}
	        	
	        	if(CmFinoFIX.SubscriberStatus_InActive.equals(subscriberUpgradeData.getSubscriberStatus())){
					if(!CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(subscriberUpgradeData.getSubscriberRestriction())){
						error = new CmFinoFIX.CMJSError();
						error.setErrorDescription(MessageText._("Subscriber InActivation not allowed."));
						log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Subscriber InActivation not allowed for user:"+ getLoggedUserNameWithIP());
						return error;
					}
				}
	        	
	        	if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberUpgradeData.getSubscriberStatus())){
					error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Changing Subscriber status to 'Not Registered' is not allowed."));
					log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Changing Subscriber status to 'Not Registered' is not allowed for user:"+ getLoggedUserNameWithIP());
					return error;
				}
	        	
	        	if(CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberUpgradeData.getSubscriberStatus()) && 
						!( CmFinoFIX.SubscriberStatus_Initialized.equals(subscriber.getStatus()) 
								|| CmFinoFIX.SubscriberStatus_Suspend.equals(subscriber.getStatus()) 
								|| CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus())
								||CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus())) 
								){
					error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Intializing subscriber not allowed."));
					log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Initializing subscriber not allowed for "+ getLoggedUserNameWithIP());
					return error;
				}
	        	
	        	if(CmFinoFIX.SubscriberStatus_Suspend.equals(subscriberUpgradeData.getSubscriberStatus()) && !CmFinoFIX.SubscriberStatus_Suspend.equals(subscriber.getStatus())){
					error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspending of subscriber not allowed."));
					log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Suspending of subscriber not allowed for "+ getLoggedUserNameWithIP());
					return error;
				}
				
				if(subscriberUpgradeData.getSubscriberStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberUpgradeData.getSubscriberStatus()) &&
						CmFinoFIX.SubscriberStatus_Suspend.equals(subscriber.getStatus())){
					error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspended subscriber can be moved to Intialized status only"));
					log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Suspended subscriber can be moved to Intialized status only for "+ getLoggedUserNameWithIP());
					return error;
				}
				
				if(subscriberUpgradeData.getSubscriberStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberUpgradeData.getSubscriberStatus()) &&
						CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus()) && !isActivationAllowed(subscriber, subscriberUpgradeData)){
					error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Inactive subscriber can be moved to Intialized status only"));
					log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Suspended subscriber can be moved to Intialized status only for "+ getLoggedUserNameWithIP());
					return error;
				}
	        	
				// Dont allow to change status when subscriber is PendingRetired or
				// Retired.
				if (subscriberUpgradeData.getSubscriberStatus() != null) {
					if (subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired) || subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement)) {
						if (subscriberUpgradeData.getSubscriberStatus() != CmFinoFIX.SubscriberStatus_Retired && subscriberUpgradeData.getSubscriberStatus() != CmFinoFIX.SubscriberStatus_PendingRetirement) {
							handleRetiredSubscriber();
						}
						else
							log.warn("SubscriberMDN:"+subscriberMDN.getId() + "Subscriber status is eigther retired or pending retirement for "+ getLoggedUserNameWithIP());
					}
				}

				if (subscriberUpgradeData.getSubscriberStatus() != null && (subscriberUpgradeData.getSubscriberStatus().equals(CmFinoFIX.SubscriberStatus_Retired) || 
						subscriberUpgradeData.getSubscriberStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement))) {
					
					subscriberUpgradeData.setSubscriberStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					subscriberUpgradeData.setSubscriberRestriction(CmFinoFIX.SubscriberRestrictions_None);
					
					CommodityTransferQuery ctQuery = new CommodityTransferQuery();
					ctQuery.setSourceDestnMDN(subscriberMDN.getMdn());
					
					PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					List<PendingCommodityTransfer> lstPCT = pctDAO.get(ctQuery);
					
					if (!CollectionUtils.isEmpty(lstPCT)) {
						error = new CmFinoFIX.CMJSError();
						error.setErrorDescription(MessageText._("There are "+lstPCT.size()+" pending transactions to be resolved"));
						return error;
					}
					
					int code = subscriberService.retireSubscriber(subscriberMDN);
					if (code == Codes.OPERATION_NOT_ALLOWED) {
						error = new CmFinoFIX.CMJSError();
						error.setErrorDescription(MessageText._("Subscriber retirement failed. This subscriber is registered as an active merchant. MDN will be suspended to prevent merchant performing transactions."));
						log.warn("SubscriberMDN:"+subscriberMDN.getId() + " Merchant MDN suspended to prevent transactions for user "+ getLoggedUserNameWithIP());
						return error;
					}
				}
				
				boolean isNonRegisterActivation = false;
				if(subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_NotRegistered) 
						&& subscriberUpgradeData.getSubscriberStatus().equals(CmFinoFIX.SubscriberStatus_Initialized)){
					
					updateUnregisteredSubsPocket(subscriberMDN);
					
					KycLevel fullyBankedLevel = kyclevelDao.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_FullyBanked.longValue());
					subscriber.setKycLevel(fullyBankedLevel);
					
					UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
					query.setSubscriberMDNID(subscriber.getId());
					UnRegisteredTxnInfoDAO unregisteredDao = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
					List<UnregisteredTxnInfo> unregisteredSubscriber = unregisteredDao.get(query);
					if(unregisteredSubscriber != null && unregisteredSubscriber.size() > 0) {
						for (UnregisteredTxnInfo txnInfo : unregisteredSubscriber) {
							txnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_SUBSCRIBER_ACTIVE);					
							unregisteredDao.save(txnInfo);
						}
					}
					isNonRegisterActivation = true;
					subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable);
				}
	        	
				updateEntity(subscriberMDN, subscriberUpgradeData);
	        	
				//Generate OTP for the subscriber if the status is changed from Suspend to Initialise or Inactive to Initialise.
				if(subscriberUpgradeData.getSubscriberStatus() != null && CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberUpgradeData.getSubscriberStatus()) && !isNonRegisterActivation){
					sendOTPOnIntialized = ConfigurationUtil.getSendOTPOnIntialized();
					if(sendOTPOnIntialized){
						generateAndSendOTP(subMdndao, subscriberMDN, CmFinoFIX.NotificationCode_New_OTP_Success);
						log.info("new OTP is generated for the subscriber" + subscriberMDN.getId() + "as the status is changed from Suspend to Initialise or Inactive to Initialise");
					}
				}

				subscriber.setAddressBySubscriberaddressid(subscriberUpgradeData.getAddress());
				
				SubscribersAdditionalFieldsDAO subAddFieldDao = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
				SubscribersAdditionalFieldsQuery query = new SubscribersAdditionalFieldsQuery();
				query.set_SubscriberID(subscriber.getId().intValue());
				List<SubscriberAddiInfo> addInfo = subAddFieldDao.get(query);
				log.info("get subscriber additional info: ");
				log.info("@Martin: job isNull["+subscriberUpgradeData.getJob()+"]");
				if(addInfo != null && !addInfo.isEmpty()) {
					for (SubscriberAddiInfo subscriberAddInfo : addInfo) {
						subscriberAddInfo.setNationality(subscriberUpgradeData.getNationality());
						subscriberAddInfo.setWork(subscriberUpgradeData.getJob());
						if(subscriberUpgradeData.getJob()!=null && subscriberUpgradeData.getJob().equals(CmFinoFIX.WorkList_Lainnya.toString())) {
							subscriberAddInfo.setOtherwork(subscriberUpgradeData.getOtherJob());
						}else {
							subscriberAddInfo.setOtherwork(null);
						}
						subscriberAddInfo.setIncome(subscriberUpgradeData.getAvgMonthlyIncome());
						subscriberAddInfo.setSourceoffund(subscriberUpgradeData.getSourceOfFund());
						subscriberAddInfo.setGoalofacctopening(subscriberUpgradeData.getEmoneyOpeningPurpose());
						subscriberAddInfo.setMaritalStatus(subscriberUpgradeData.getMaritalStatus());
						subscriberAddInfo.setSubscriber(subscriber);
						subAddFieldDao.save(subscriberAddInfo);
						log.info("updated subscriber additional info: "+ subscriberAddInfo.getId());
					}
				}else {
					SubscriberAddiInfo subscriberAddInfo = new SubscriberAddiInfo();
					subscriberAddInfo.setNationality(subscriberUpgradeData.getNationality());
					subscriberAddInfo.setWork(subscriberUpgradeData.getJob());
			
					if(subscriberUpgradeData.getJob()!=null && subscriberUpgradeData.getJob().equals(CmFinoFIX.WorkList_Lainnya.toString())) {
						subscriberAddInfo.setOtherwork(subscriberUpgradeData.getOtherJob());
					}
					subscriberAddInfo.setIncome(subscriberUpgradeData.getAvgMonthlyIncome());
					subscriberAddInfo.setSourceoffund(subscriberUpgradeData.getSourceOfFund());
					subscriberAddInfo.setGoalofacctopening(subscriberUpgradeData.getEmoneyOpeningPurpose());
					subscriberAddInfo.setMaritalStatus(subscriberUpgradeData.getMaritalStatus());
					subscriberAddInfo.setSubscriber(subscriber);
					subAddFieldDao.save(subscriberAddInfo);
					log.info("updated subscriber additional info: "+ subscriberAddInfo.getId());
				}
				
				subscriber.setGender(subscriberUpgradeData.getGender());
				subscriberDao.save(subscriber);
				log.info("updated subscriber: " + subscriber.getId());
				if(subscriberUpgradeData.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
					mailService.generateEmailVerificationMail(subscriber, subscriberUpgradeData.getEmail());
				}
				subMdndao.save(subscriberMDN);
	        	
				ktpDetailsQuery.setMdn(subscriberMDN.getMdn());
				ktpDetailsQuery.setOrder("desc");
				
				List<KtpDetails> ktpDetailsList = ktpDetailsDAO.getByMDN(ktpDetailsQuery);
				KtpDetails ktpDetails = null;
				
				if(ktpDetailsList != null && ktpDetailsList.size() > 0){
					 ktpDetails = ktpDetailsList.get(0);
				}
				
				if(isLakuapandiaSubscriber) {
					ktpDetailsDAO.save(ktpDetails);
				}
				log.info("@Martin: mdnRestrictions isNull["+mdnRestrictions+"]");    	
				if (mdnRestrictions != null) {
					CMJSForwardNotificationRequest forwardMsg = new CMJSForwardNotificationRequest();
					updateForwardMessage(forwardMsg, subscriberUpgradeData, oldRestrictions, subscriberMDN);
				}
				
				if(StringUtils.isNotBlank(subscriberUpgradeData.getBankAccountNumber()))
				{
					Pocket existingBankPocket = subscriberService.getDefaultPocket(subscriberMDN.getMdn(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
					PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
					if(existingBankPocket != null)
					{
						log.info("Bank pocket already exists for subscriber with mdn " + subscriberMDN.getMdn());
						String existingAccountNo = existingBankPocket.getCardpan();
						if(!existingAccountNo.equals(subscriberUpgradeData.getBankAccountNumber()))
						{
							log.info("Updating the old bank a/c no " + existingAccountNo + " with the new a/c no "+subscriberUpgradeData.getBankAccountNumber() + "for subscriber with mdn "+ subscriberMDN.getMdn());
							existingBankPocket.setCardpan(subscriberUpgradeData.getBankAccountNumber());
							pocketDao.save(existingBankPocket);								 
						}
					}
				}	
				
	        	error.setErrorDescription(MessageText._("Request for Subscriber Edit Data is Approved successfully"));
	        	if(CmFinoFIX.MDNStatus_Retired.equals(subscriberMDN.getStatus()) || CmFinoFIX.MDNStatus_PendingRetirement.equals(subscriberMDN.getStatus())){
	        		error.setErrorDescription(MessageText._("Request for Subscriber Archival is Approved successfully"));
	        	}
	        	
				error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				log.info("Request for Subscriber Upgraded Approved successfully");
				
        	} else {
				error.setErrorDescription(MessageText._("Request for Subscriber Edit Data is Rejected successfully"));
				error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				log.info("Request for Subscriber Upgraded Rejected successfully");
			}

    		error.setsuccess(true);
        	subscriberUpgradeData.setSubsActivityComments(realMsg.getUpgradeAcctComments());
			subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
			subscriberUpgradeData.setSubsActivityApprovedBY(userService.getCurrentUser().getUsername());
			subscriberUpgradeData.setSubsActivityAprvTime(new Timestamp());
			subscriberUpgradeDataDAO.save(subscriberUpgradeData);
        }
        
		return error;
	}

	private void updateForwardMessage(
			CMJSForwardNotificationRequest newMsg,
			SubscriberUpgradeData subscriberUpgradeData, Integer oldRestrictions, SubscriberMdn subscriberMdn) throws Exception {
		log.info("@Martin>>: Event Subscriber update ForwardMessage");
		newMsg.setDestMDN(subscriberMdn.getMdn());
		newMsg.setFormatOnly(Boolean.FALSE);
		newMsg.setMSPID(1L);
		newMsg.setSourceMDN(subscriberMdn.getMdn());
		
		Integer newRestrictions = subscriberUpgradeData.getSubscriberRestriction();
		Boolean isNewAbsolutLocked = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		Boolean isNewRestrictionsNone = ((newRestrictions | CmFinoFIX.SubscriberRestrictions_None) == CmFinoFIX.SubscriberRestrictions_None);
		Boolean isNewSecurityLocked = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_SecurityLocked) == CmFinoFIX.SubscriberRestrictions_SecurityLocked);
		Boolean isNewSelfSuspended = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_SelfSuspended) == CmFinoFIX.SubscriberRestrictions_SelfSuspended);
		Boolean isNewSuspended = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_Suspended) == CmFinoFIX.SubscriberRestrictions_Suspended);

		Boolean isOldAbsolutLocked = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		Boolean isOldRestrictionsNone = ((oldRestrictions | CmFinoFIX.SubscriberRestrictions_None) == CmFinoFIX.SubscriberRestrictions_None);
		Boolean isOldSecurityLocked = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_SecurityLocked) == CmFinoFIX.SubscriberRestrictions_SecurityLocked);
		Boolean isOldSelfSuspended = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_SelfSuspended) == CmFinoFIX.SubscriberRestrictions_SelfSuspended);
		Boolean isOldSuspended = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_Suspended) == CmFinoFIX.SubscriberRestrictions_Suspended);

		Boolean isAbsolutLockedChanged = (isOldAbsolutLocked ^ isNewAbsolutLocked);
		Boolean isRestrictionsNoneChanged = !isOldRestrictionsNone;
		Boolean isSecurityLockedChanged = (isNewSecurityLocked ^ isOldSecurityLocked);
		Boolean isSelfSuspendedChanged = (isOldSelfSuspended ^ isNewSelfSuspended);
		Boolean isSuspendedChanged = (isOldSuspended ^ isNewSuspended);

		if ((isAbsolutLockedChanged && isNewAbsolutLocked) || (isSecurityLockedChanged && isNewSecurityLocked) || (isSelfSuspendedChanged && isNewSelfSuspended) || (isSuspendedChanged && isNewSuspended)) {
			newMsg.setCode(CmFinoFIX.NotificationCode_MDNAccountSuspendNotification);
			forwardNotificationRequestProcessor.process(newMsg);
			//@Martin : send SMS upon restriction
			log.info("@Martin>>: Event to send MDNRestriction sms, due to Admin trigger.. ");
			sendSMS(subscriberMdn, CmFinoFIX.NotificationCode_MDNAccountSuspendNotification);
		} else if (isRestrictionsNoneChanged && isNewRestrictionsNone) {
			newMsg.setCode(CmFinoFIX.NotificationCode_MDNReleaseSuspension);
			forwardNotificationRequestProcessor.process(newMsg);
			//@Martin : send SMS upon Release restriction
			log.info("@Martin>>: Event to send MDNReleaseRestriction sms");
			sendSMS(subscriberMdn, CmFinoFIX.NotificationCode_MDNReleaseSuspension);
		}
	
	}

	/**
	 * Generates new OTP and Send the same as SMS and Email if exists.
	 * @param mdnDao
	 * @param mdn
	 * @param notificationCode
	 */
	private void generateAndSendOTP(SubscriberMDNDAO mdnDao, SubscriberMdn mdn, Integer notificationCode) {
		Subscriber sub = mdn.getSubscriber();
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(mdn.getMdn(), oneTimePin);
		mdn.setOtp(digestPin1);
		mdn.setDigestedpin(null);
		mdn.setAuthorizationtoken(null);
		mdn.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));

		NotificationWrapper notification = new NotificationWrapper();
		notification.setLanguage(Integer.valueOf(Long.valueOf(sub.getLanguage()).intValue()));
		notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notification.setCode(notificationCode);
		notification.setOneTimePin(oneTimePin);
		notification.setFirstName(sub.getFirstname());
    	notification.setLastName(sub.getLastname());
		String message = notificationMessageParserService.buildMessage(notification,true);
		String mdn2 = mdn.getMdn();
		smsService.setDestinationMDN(mdn2);
		smsService.setMessage(message);
		smsService.setNotificationCode(notification.getCode());
		smsService.asyncSendSMS();
		if(((sub.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0) && sub.getEmail() != null)
		{
			notification.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			String emailMessage = notificationMessageParserService.buildMessage(notification,true);
			String to=sub.getEmail();
			String name=sub.getFirstname();
			String subject = ConfigurationUtil.getOTPMailSubsject();
			mailService.asyncSendEmail(to, name, subject, emailMessage);
		}
	}
	
	
	private void updateEntity(SubscriberMdn subscriberMDN, SubscriberUpgradeData subscriberUpgradeData) {

		String ID = String.valueOf(subscriberMDN.getId());

		if (subscriberUpgradeData.getSubscriberRestriction() != null) {
			if(!subscriberUpgradeData.getSubscriberRestriction().equals(subscriberMDN.getRestrictions())){
        		log.info("Subscriber MDN:"+ID+" MDN Restrictions updated to "+subscriberUpgradeData.getSubscriberRestriction()+" by user:"+getLoggedUserNameWithIP());
        		log.info("Subscriber :"+subscriberMDN.getSubscriber().getId()+" Restrictions updated to "+subscriberUpgradeData.getSubscriberRestriction()+" by user:"+getLoggedUserNameWithIP());
        	}
			subscriberMDN.setRestrictions(subscriberUpgradeData.getSubscriberRestriction());
			subscriberMDN.getSubscriber().setRestrictions(subscriberUpgradeData.getSubscriberRestriction());
		}
		if (subscriberUpgradeData.getSubscriberStatus() != null) {
			if(!subscriberUpgradeData.getSubscriberStatus().equals(subscriberMDN.getStatus())){
        		log.info("Subscriber MDN:"+ID+" Status updated to "+subscriberUpgradeData.getSubscriberStatus()+" by user:"+getLoggedUserNameWithIP());
        		log.info("Subscriber :"+subscriberMDN.getSubscriber().getId()+" Status updated to "+subscriberUpgradeData.getSubscriberStatus()+" by user:"+getLoggedUserNameWithIP());
        	}
			// *FindbugsChange*
        	// Previous -- if (subscriberUpgradeData.getSubscriberStatus() != subscriberMDN.getStatus()) {
			if (!(subscriberUpgradeData.getSubscriberStatus().equals(subscriberMDN.getStatus()))) {
				if (CmFinoFIX.MDNStatus_Retired.equals(subscriberUpgradeData.getSubscriberStatus()) ||
						CmFinoFIX.MDNStatus_PendingRetirement.equals(subscriberUpgradeData.getSubscriberStatus())) {
					subscriberMDN.setIsforcecloserequested(true);
				}
				subscriberMDN.setStatus(subscriberUpgradeData.getSubscriberStatus());
				subscriberMDN.setStatustime(new Timestamp());
				subscriberMDN.getSubscriber().setStatus(subscriberUpgradeData.getSubscriberStatus());
				subscriberMDN.getSubscriber().setStatustime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriberMDN.getSubscriber(), true);
			}
		}
		if (subscriberMDN.getStatustime() == null) {
			log.info("Subscriber MDN:"+ID+" Status Time updated by user:"+getLoggedUserNameWithIP());
			subscriberMDN.setStatustime(new Timestamp());
		}

		if (subscriberMDN.getSubscriber().getStatustime() == null) {
			log.info("Subscriber :"+subscriberMDN.getSubscriber().getId()+" Status Time updated by user:"+getLoggedUserNameWithIP());
			subscriberMDN.getSubscriber().setStatustime(new Timestamp());
		}

		ID = String.valueOf(subscriberMDN.getSubscriber().getId());
		
		// subscriber related fields
		if (subscriberUpgradeData.getFullName() != null) {
			if(!subscriberUpgradeData.getFullName().equals(subscriberMDN.getSubscriber().getFirstname())){
        		log.info("Subscriber:"+ID+" First name updated to "+subscriberUpgradeData.getFullName()+" by user:"+getLoggedUserNameWithIP());
        	}
			subscriberMDN.getSubscriber().setFirstname(subscriberUpgradeData.getFullName());
		}
		if (subscriberUpgradeData.getEmail() != null) {
			if(!subscriberUpgradeData.getEmail().equals(subscriberMDN.getSubscriber().getEmail())){
        		log.info("Subscriber:"+ID+" Email updated to "+subscriberUpgradeData.getEmail()+" by user:"+getLoggedUserNameWithIP());
        	}
			subscriberMDN.getSubscriber().setEmail(subscriberUpgradeData.getEmail());
			subscriberMDN.getSubscriber().setIsemailverified(CmFinoFIX.Boolean_True);
		}		
		if (subscriberUpgradeData.getLanguage() != null) {
			if(!subscriberUpgradeData.getLanguage().equals(subscriberMDN.getSubscriber().getLanguage())){
        		log.info("Subscriber:"+ID+" Language updated to "+subscriberUpgradeData.getLanguage()+" by user:"+getLoggedUserNameWithIP());
        	}
			subscriberMDN.getSubscriber().setLanguage(subscriberUpgradeData.getLanguage());
		}
		else
		{
			subscriberMDN.getSubscriber().setLanguage(systemParametersService.getSubscribersDefaultLanguage());
		}

		if (subscriberUpgradeData.getNotificationMethod() != null) {
			if(!subscriberUpgradeData.getNotificationMethod().equals(subscriberMDN.getSubscriber().getNotificationmethod())){
        		log.info("Subscriber:"+ID+" Notification method updated to "+subscriberUpgradeData.getNotificationMethod()+" by user:"+getLoggedUserNameWithIP());
        	}
			subscriberMDN.getSubscriber().setNotificationmethod(subscriberUpgradeData.getNotificationMethod());
		}
		if(subscriberUpgradeData.getIdType() != null){
			if(!subscriberUpgradeData.getIdType().equals(subscriberMDN.getIdtype())){
        		log.info("Subscriber:"+ID+" ID Type updated to "+subscriberUpgradeData.getIdType()+" by user:"+getLoggedUserNameWithIP());
        	}
			subscriberMDN.setIdtype(subscriberUpgradeData.getIdType());
		}
		if(subscriberUpgradeData.getIdNumber() != null){
			if(!subscriberUpgradeData.getIdNumber().equals(subscriberMDN.getIdnumber())){
        		log.info("Subscriber:"+ID+" ID Number updated to "+subscriberUpgradeData.getIdNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			subscriberMDN.setIdnumber(subscriberUpgradeData.getIdNumber());
		}
		
		subscriberMDN.setKtpdocumentpath(subscriberUpgradeData.getIdCardScanPath());
		
	}

	private boolean isActivationAllowed(Subscriber subscriber,
			SubscriberUpgradeData subscriberUpgradeData) {
		if(CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())){
			return true;
		}
		if(CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus())){
			if(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriber.getRestrictions()) && (
					(subscriberUpgradeData.getSubscriberRestriction()==null) || 
					(CmFinoFIX.SubscriberRestrictions_None.equals(subscriberUpgradeData.getSubscriberRestriction())
							))){
				return true;
			}
		}
		return false;
	}

	private void handleRetiredSubscriber() throws SubscriberRetiredException {
		CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		error.setErrorDescription(MessageText._("Can't Change Subscriber Status"));
		WebContextError.addError(error);
		throw new SubscriberRetiredException(MessageText._(" Can't Change Status"));
	}
	
	private void updateUnregisteredSubsPocket(SubscriberMdn subscriberMDN) {
		Pocket unregisteredPocket = subscriberMDN.getPockets().iterator().next();
		Subscriber subscriber = subscriberMDN.getSubscriber();
		
		KycLevel unBankedLevel = kyclevelDao.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_UnBanked.longValue());
	    PocketTemplate eMoneyUnBankedTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(unBankedLevel.getKyclevel(), 
	    		 true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, null);
	    
	    unregisteredPocket.setPocketTemplateByOldpockettemplateid(unregisteredPocket.getPocketTemplateByPockettemplateid());
		unregisteredPocket.setPocketTemplateByPockettemplateid(eMoneyUnBankedTemplate);
		unregisteredPocket.setStatus(CmFinoFIX.PocketStatus_Active);
		pocketService.save(unregisteredPocket);

		SubsUpgradeBalanceLogDAO subsUpgradeBalanceLogDAO = DAOFactory.getInstance().getSubsUpgradeBalanceLogDAO();
		SubscriberUpgradeBalanceLog subUpgradeBalanceLog = new SubscriberUpgradeBalanceLog();
		subUpgradeBalanceLog.setSubscriberId(subscriber.getId());
		subUpgradeBalanceLog.setPockatBalance(unregisteredPocket.getCurrentbalance());
		subUpgradeBalanceLog.setTxnDate(new Timestamp());
		subUpgradeBalanceLog.setCreatedby(getLoggedUserName());
		subUpgradeBalanceLog.setCreatetime(new Timestamp());
		subUpgradeBalanceLog.setUpdatedby(getLoggedUserName());
		subUpgradeBalanceLog.setLastupdatetime(new Timestamp());
		subsUpgradeBalanceLogDAO.save(subUpgradeBalanceLog);
	}
	
	

	private void displayExistingSubscriberData(SubscriberMdn subscriberMDN,
			Subscriber subscriber, CMJSSubscriberEdit.CGEntries entry) {
		String idTypeValue = getIdTypeCode(subscriberMDN.getIdtype());
		entry.setEmail(subscriber.getEmail());
		entry.setFirstName(subscriber.getFirstname());
		entry.setIDType(idTypeValue);
		entry.setIDNumber(subscriberMDN.getIdnumber());
		entry.setKTPDocumentPath(subscriberMDN.getKtpdocumentpath());
		entry.setMDNID(subscriberMDN.getId());
		entry.setMDN(subscriberMDN.getMdn());
		Pocket existingBankPocket = subscriberService.getDefaultPocket(subscriberMDN.getMdn(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		if(existingBankPocket != null){
			entry.setAccountNumber(existingBankPocket.getCardpan());
		}else{
			entry.setAccountNumber("#");
		}
		Address address = subscriber.getAddressBySubscriberaddressid();
		
		SubscribersAdditionalFieldsDAO subAddFieldDao = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
		SubscribersAdditionalFieldsQuery query = new SubscribersAdditionalFieldsQuery();
		query.set_SubscriberID(subscriber.getId().intValue());
		List<SubscriberAddiInfo> addInfo = subAddFieldDao.get(query);
		
		if(addInfo != null && !addInfo.isEmpty() && addInfo.get(0) != null) {
			SubscriberAddiInfo subscriberAddiInfo = addInfo.get(0);
			entry.setNationality(subscriberAddiInfo.getNationality());
			entry.setWork(subscriberAddiInfo.getWork());
			entry.setWorkText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_JobList, null, subscriberAddiInfo.getWork()));
			entry.setGender(subscriber.getGender());
			entry.setGenderText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Gender, null, subscriber.getGender()));
			entry.setMaritalStatus(subscriberAddiInfo.getMaritalStatus());
			entry.setMaritalStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_MaritalStatusList, null, subscriberAddiInfo.getMaritalStatus()));
			entry.setSourceOfFund(subscriberAddiInfo.getSourceoffund());
			entry.setIncome(subscriberAddiInfo.getIncome());
			entry.setIncomeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_AvgIncomeList, null, subscriberAddiInfo.getIncome()));
			entry.setGoalOfAcctOpening(subscriberAddiInfo.getGoalofacctopening());
			entry.setOtherWork(subscriberAddiInfo.getOtherwork());
		}
		
		
		if(address != null){
			entry.setCity(address.getCity());
			entry.setRegionName(address.getRegionname());
			entry.setState(address.getState());
			entry.setSubState(address.getSubstate());
			entry.setStreetAddress(address.getLine2());
		}
		entry.setNotificationMethod(subscriber.getNotificationmethod());
		entry.setMDNRestrictions(subscriberMDN.getRestrictions());
		entry.setLanguage(subscriber.getLanguage());
		entry.setSubscriberStatus(subscriber.getStatus());
	}

	private void displaySubmitedRequestData(SubscriberMdn subscriberMDN,
			SubscriberUpgradeData subscriberUpgradeData,
			CMJSSubscriberEdit.CGEntries entry) {
		String idTypeValue = getIdTypeCode(subscriberUpgradeData.getIdType());
		entry.setEmail(subscriberUpgradeData.getEmail());
		entry.setFirstName(subscriberUpgradeData.getFullName());
		entry.setIDType(idTypeValue);
		entry.setIDNumber(subscriberUpgradeData.getIdNumber());
		entry.setKTPDocumentPath(subscriberUpgradeData.getIdCardScanPath());
		entry.setMDNID(subscriberUpgradeData.getId());
		entry.setMDN(subscriberMDN.getMdn());
		Pocket existingBankPocket = subscriberService.getDefaultPocket(subscriberMDN.getMdn(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		if(existingBankPocket != null){
			entry.setAccountNumber(subscriberUpgradeData.getBankAccountNumber());
		}else{
			entry.setAccountNumber("#");
		}
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
		entry.setNotificationMethod(subscriberUpgradeData.getNotificationMethod());
		entry.setMDNRestrictions(subscriberUpgradeData.getSubscriberRestriction());
		entry.setLanguage(subscriberUpgradeData.getLanguage());
		entry.setSubscriberStatus(subscriberUpgradeData.getSubscriberStatus());
	}

	private String getIdTypeCode(String idTypeValue) {
		if(!StringUtils.isNumeric(idTypeValue)){
			EnumTextQuery enumTextQuery = new EnumTextQuery();
			enumTextQuery.setTagId(CmFinoFIX.TagID_IDTypeForKycUpgrade);
			enumTextQuery.setDisplayText(idTypeValue);
			List<EnumText> enumTexts = enumTextService.getEnumText(enumTextQuery);
			if(enumTexts != null && enumTexts.size() > 0){
				EnumText enumText = enumTexts.get(0);
				idTypeValue = enumText.getEnumcode();
				return idTypeValue;
			}
		}
		return idTypeValue;
	}
	
	
	//@@martin
	private void sendSMS (SubscriberMdn subscriberMDN , Integer notificationCode) {
		try{
			Subscriber subscriber = subscriberMDN.getSubscriber();
			String mdn2 = subscriberMDN.getMdn();
			log.info("@Martin>>: sendSMS to ["+mdn2+"] start ");
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
			log.info("@Martin>>: sendSMS to ["+mdn2+"] message=["+smsMessage+"] ");
			
			smsService.asyncSendSMS(smsValues);
			log.info("@Martin>>: sendSMS to ["+mdn2+"] DONE !");
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error in Sending SMS "+e.getMessage(),e);
		}
	}

}
