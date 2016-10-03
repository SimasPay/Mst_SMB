package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.BulkUploadEntryDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.BulkUploadEntryQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSVerifyNonRegisteredBulkTransfer;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.VerifyNonRegisteredBulkTransferProcessor;
import com.mfino.util.MfinoUtil;

@Service("VerifyNonRegisteredBulkTransferProcessorImpl")
public class VerifyNonRegisteredBulkTransferProcessorImpl extends BaseFixProcessor implements VerifyNonRegisteredBulkTransferProcessor{
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	  @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	  public CFIXMsg process(CFIXMsg msg) {

		    CMJSVerifyNonRegisteredBulkTransfer realMsg = (CMJSVerifyNonRegisteredBulkTransfer) msg;
		    CMJSError err = new CMJSError();

		    User loggedInUser = userService.getCurrentUser();

		    if (loggedInUser != null && realMsg.getBulkUploadID() != null && StringUtils.isNotBlank(realMsg.getNonRegisteredIdsStr())) {
		    	BulkUploadDAO buDao = DAOFactory.getInstance().getBulkUploadDAO();
		    	BulkUpload bulkUpload = buDao.getById(realMsg.getBulkUploadID());
		    	// *FindbugsChange*
		    	// Previous -- if (bulkUpload != null && bulkUpload.getUser().getID() != loggedInUser.getID()) {
		    	if (bulkUpload != null && !(bulkUpload.getMfinoUser().getId().equals(loggedInUser.getId()))) {
			        err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			        err.setErrorDescription(MessageText._("You are not authorized to Verify the Bulk Transfer."));
					return err;
		    	}

		    	BulkUploadEntryQuery query = new BulkUploadEntryQuery();
		    	query.setId(realMsg.getBulkUploadID());

		    	String nonRegisteredIdsStr = realMsg.getNonRegisteredIdsStr();
		    	String data[] = nonRegisteredIdsStr.split(",");
		    	Integer ids[] = new Integer[data.length];
		    	for (int i=0; i<data.length; i++) {
		    		ids[i] = Integer.parseInt(data[i]);
		    	}
		    	query.setUploadLineNumbers(ids);
		    	BulkUploadEntryDAO bueDAO = DAOFactory.getInstance().getBulkUploadEntryDAO();

		    	try {
					List<BulkUploadEntry> lstBulkUploadEntries = bueDAO.get(query);
					if (CollectionUtils.isNotEmpty(lstBulkUploadEntries)) {
						for (BulkUploadEntry bue: lstBulkUploadEntries) {
							generateFundAccessCode(bue);
						}
					}
				} catch (Exception e) {
			        err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			        err.setErrorDescription(MessageText._("There is a problem in generating the Fund Access codes for Non Registered Susbscribers. Please try after some time."));
					log.error("Exception in Resending the Fund Access code " + e.getMessage() , e);
					return err;
				}

		        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		        err.setErrorDescription(MessageText._("Successfully Send the Fund Access codes for the selected Non Registered Subscribers."));
		    }

		    return err;
		  }

		  /**
		   * Generate and send New Fund Access codes for non registered subscribers
		   * @param bulkUploadEntry
		   */
		  private void generateFundAccessCode(BulkUploadEntry bulkUploadEntry) {
			  if (bulkUploadEntry != null && Boolean.valueOf(bulkUploadEntry.getIsunregistered().toString())) {

				  //Get the UnRegistered Transaction info using SCTL ID
				  UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
				  query.setTransferSctlId(bulkUploadEntry.getServicechargetransactionlogid().longValue());

				  UnRegisteredTxnInfoDAO urtDao = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
				  List<UnregisteredTxnInfo> lstunUnRegisteredTxnInfo= urtDao.get(query);
				  if (CollectionUtils.isNotEmpty(lstunUnRegisteredTxnInfo)) {
					  UnregisteredTxnInfo unRegTxnInfo = lstunUnRegisteredTxnInfo.get(0);
					  SubscriberMdn destSubscriberMDN = unRegTxnInfo.getSubscriberMdn();
					  Subscriber destSubscriber = destSubscriberMDN.getSubscriber();

					  if (destSubscriberMDN.getMdn().equals(bulkUploadEntry.getDestmdn())) {
							Integer OTPLength = systemParametersService.getOTPLength();
							String code = MfinoUtil.generateOTP(OTPLength);
							String digestedCode = MfinoUtil.calculateDigestPin(destSubscriberMDN.getMdn(), code);
							unRegTxnInfo.setDigestedpin(digestedCode);
							urtDao.save(unRegTxnInfo);

							// Sending the new Fund access code to Destination subscriber
							NotificationWrapper notification = new NotificationWrapper();
							
							Long tempLanguageL = destSubscriber.getLanguage();
							Integer tempLanguageLI = tempLanguageL.intValue();
							
							notification.setLanguage(tempLanguageLI);
							notification.setFirstName(destSubscriber.getFirstname());
					    	notification.setLastName(destSubscriber.getLastname());
							notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
							notification.setCode(CmFinoFIX.NotificationCode_SendFundAccessCodeForNonRegisteredBulkTransfer);
							notification.setOneTimePin(code);
							notification.setSctlID(bulkUploadEntry.getServicechargetransactionlogid().longValue());
							String message = notificationMessageParserService.buildMessage(notification,true);
							smsService.setDestinationMDN(destSubscriberMDN.getMdn());
							smsService.setMessage(message);
							smsService.setNotificationCode(notification.getCode());
							smsService.asyncSendSMS();
					  }
				  }
			  }
		  }
}
