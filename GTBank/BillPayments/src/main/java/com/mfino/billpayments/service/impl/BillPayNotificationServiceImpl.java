package com.mfino.billpayments.service.impl;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.billpayments.service.BillPayNotificationService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSMSNotification;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.BillerService;
import com.mfino.service.NotificationMessageParserService;


/**
 * @author Sasi
 *
 */
public class BillPayNotificationServiceImpl extends BillPaymentsBaseServiceImpl implements BillPayNotificationService{

	private Log log = LogFactory.getLog(this.getClass());
	
	protected BillerService billerService;
	protected NotificationMessageParserService notificationMessageParserService ;


	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}

	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}

	public BillerService getBillerService() {
		return billerService;
	}

	public void setBillerService(BillerService billerService) {
		this.billerService = billerService;
	}

	@Override
	public MCEMessage notificationToDestination(MCEMessage mceMessage) {
		log.info("BillPayNotificationServiceImpl : process notification to Destination mceMessage="+mceMessage);

		MCEMessage smsMessage = new MCEMessage();
		if(mceMessage.getResponse() instanceof BackendResponse){
			BackendResponse backendResponse = (BackendResponse)mceMessage.getResponse();
			Partner partner = billerService.getPartner(backendResponse.getBillerCode());
			if(partner!=null){
				Set<MFSBillerPartner> billerPartners = partner.getMfsbillerPartnerMaps();
				MFSBillerPartner billPartner = null;
				for(MFSBillerPartner billerPartner: billerPartners){
					if(backendResponse.getBillerCode().equals(billerPartner.getMfsBiller().getMfsbillercode())){
						billPartner = billerPartner;
						break;
					}
					
				}
				if(billPartner!=null){
					Integer billerPartnerType = billPartner.getBillerpartnertype().intValue();
					
					CMSMSNotification smsNotification = new CMSMSNotification();
					NotificationWrapper notificationWrapper = new NotificationWrapper();
					
					notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
					
/*					if(CmFinoFIX.BillerPartnerType_Payment_Full.equals(billerPartnerType)
							||CmFinoFIX.BillerPartnerType_Payment_Partial.equals(billerPartnerType))
						notificationWrapper.setCode(CmFinoFIX.NotificationCode_BillPayCompletedToReceiver);
					else*/
					notificationWrapper.setCode(CmFinoFIX.NotificationCode_BillPayTopupCompletedToReceiver);
					
					log.info("BillPayNotificationServiceImpl : notificationcode="+notificationWrapper.getCode() + ", destinationMdn="+backendResponse.getReceiverMDN());
					notificationWrapper.setSourceMDN(backendResponse.getSenderMDN());
					notificationWrapper.setReceiverMDN(backendResponse.getReceiverMDN());
					notificationWrapper.setAmount(backendResponse.getAmount());
					notificationWrapper.setSctlID(backendResponse.getServiceChargeTransactionLogID());
					notificationWrapper.setServiceCharge(backendResponse.getCharges());
					notificationWrapper.setInvoiceNumber(backendResponse.getInvoiceNumber());
					
					String sms = notificationMessageParserService.buildMessage(notificationWrapper,true);
						if(StringUtils.isNotBlank(sms)){
							smsNotification.setText(sms);
							smsNotification.setTo(backendResponse.getReceiverMDN());
							smsNotification.setServiceChargeTransactionLogID(backendResponse.getServiceChargeTransactionLogID());
							smsMessage.setRequest(smsNotification);
						}else{
							log.info("BillPayNotificationServiceImpl : notification not found for code:"+notificationWrapper.getCode());
						}						
				}else{
					log.info("BillPayNotificationServiceImpl : MFSBillerPartner not exist with billerCode:"+backendResponse.getBillerCode());
				}
			}else{
				log.info("BillPayNotificationServiceImpl : Partner not exist with billerCode:"+backendResponse.getBillerCode());
			}
		}
		smsMessage.setResponse(null);

		return smsMessage;
	}

	@Override
	public MCEMessage notificationToBiller(MCEMessage mceMessage) {
		// TODO Auto-generated method stub
		return null;
	}

}
