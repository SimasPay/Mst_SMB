package com.mfino.billpayments.service.impl;

import static com.mfino.billpayments.BillPayConstants.SOURCE_TO_DESTINATION;
import static com.mfino.billpayments.BillPayConstants.SOURCE_TO_SUSPENSE;
import static com.mfino.billpayments.BillPayConstants.SUSPENSE_TO_DESTINATION;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.billpayments.service.BillPayMoneyTransferService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerDAO;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.dao.query.MFSBillerQuery;
import com.mfino.dao.query.PartnerServicesQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.MfsBiller;
import com.mfino.domain.MfsbillerPartnerMap;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.BillerService;
import com.mfino.service.PocketService;
/**
 * @author Sasi
 *
 */
public class BillPayMoneyTransferServiceImpl extends BillPaymentsBaseServiceImpl implements BillPayMoneyTransferService{
	
	public Log log = LogFactory.getLog(this.getClass());
	protected BankService bankService;
	protected BillPaymentsService billPaymentsService;
	protected BillerService billerService;
	private HibernateTransactionManager htm;
	private SessionFactory sessionFactory;
	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}

	public BillerService getBillerService() {
		return billerService;
	}

	public void setBillerService(BillerService billerService) {
		this.billerService = billerService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	protected PocketService pocketService;

	protected String reversalResponseQueue = "jms:suspenseAndChargesRRQueue?disableReplyTo=true";
	
	protected String sourceToSuspenseBankResponseQueue = "jms:bpSourceToSuspenseBRQueue?disableReplyTo=true";
	
	protected String suspenseToDestBankResponseQueue = "jms:bpSuspenseToDestBRQueue?disableReplyTo=true";
	
	public BillPayMoneyTransferServiceImpl(){
		
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferInquiry(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquiry mceMessage="+mceMessage);
			
		CMBillPayInquiry billPayInquiry = (CMBillPayInquiry)mceMessage.getRequest();
		
		MCEMessage message =  billPayMoneyTransferInquiry(mceMessage, SOURCE_TO_DESTINATION);
		
		CFIXMsg response = message.getResponse();
		
		if(response instanceof CMTransferInquiryToBank){
			billPaymentsService.updateBillPayStatus(billPayInquiry.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_INQ_PENDING);
			message.setDestinationQueue("jms:bpSourceToDestBRQueue?disableReplyTo=true"); //FIXME use suspenseToDestBankResponseQueue instead of hard code.
		}
		else if(response instanceof BackendResponse){
			if(((BackendResponse)response).getResult()==CmFinoFIX.ResponseCode_Success){
				billPaymentsService.updateBillPayStatus(billPayInquiry.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_INQ_COMPLETED);
			}
			else{
				billPaymentsService.updateBillPayStatus(billPayInquiry.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_INQ_FAILED);
			}
		}
		else{
			throw new RuntimeException("BillPayMoneyTransferServiceImpl ::billPayMoneyTransferInquiry:: Invalid Response");
		}
		
		return message;
	}
	
	@Override
	public MCEMessage billPayMoneyTransferInquirySourceToSuspense(MCEMessage mceMessage) {
		  sessionFactory = htm.getSessionFactory();
		  Session session = sessionFactory.getCurrentSession();
		  TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquirySourceToSuspense mceMessage="+mceMessage);
		
		CMBillPayInquiry billPayInquiry = (CMBillPayInquiry)mceMessage.getRequest();
		MCEMessage message =  billPayMoneyTransferInquiry(mceMessage, SOURCE_TO_SUSPENSE);
		
		CFIXMsg response = message.getResponse();
		
		if(response instanceof CMTransferInquiryToBank){
			billPaymentsService.updateBillPayStatus(billPayInquiry.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_INQ_PENDING);
			((CMBase)message.getRequest()).setServiceChargeTransactionLogID(billPayInquiry.getServiceChargeTransactionLogID());
			((CMBase)message.getResponse()).setServiceChargeTransactionLogID(billPayInquiry.getServiceChargeTransactionLogID());
			message.setDestinationQueue(getSourceToSuspenseBankResponseQueue());
		}
		else if(response instanceof BackendResponse){
			if(((BackendResponse)response).getResult()==CmFinoFIX.ResponseCode_Success){
				billPaymentsService.updateBillPayStatus(billPayInquiry.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_INQ_COMPLETED);
			}
			else{
				billPaymentsService.updateBillPayStatus(billPayInquiry.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_INQ_FAILED);
			}
		}
		else{
			throw new RuntimeException("BillPayMoneyTransferServiceImpl ::billPayMoneyTransferInquirySourceToSuspense:: Invalid Response");
		}
		  SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
		  SessionFactoryUtils.closeSession(sessionHolder.getSession());
		return message;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferInquirySuspenseToDestination(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquirySuspenseToDestination mceMessage="+mceMessage);

		CMBase requestFix = (CMBase)mceMessage.getRequest();
		MCEMessage message =  billPayMoneyTransferInquiry(mceMessage, SUSPENSE_TO_DESTINATION);
		
		CFIXMsg response = message.getResponse();
		
		if(response instanceof CMTransferInquiryToBank){
			billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_INQ_PENDING);
			((CMBase)message.getRequest()).setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			((CMBase)message.getResponse()).setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			message.setDestinationQueue(getSuspenseToDestBankResponseQueue());
		}
		else if(response instanceof BackendResponse){
			if(((BackendResponse)response).getResult()==CmFinoFIX.ResponseCode_Success){
				billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_INQ_COMPLETED);
			}
			else{
				billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_INQ_FAILED);
			}
		}
		else{
			throw new RuntimeException("BillPayMoneyTransferServiceImpl ::billPayMoneyTransferInquirySuspenseToDestination:: Invalid Response");
		}

		return message;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferInquiry(MCEMessage mceMessage, String transferType){
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquiry :: mceMessage="+mceMessage+", transferType="+transferType);
		
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		ChannelCodeDAO channelCodeDAO = DAOFactory.getInstance().getChannelCodeDao();
		ChannelCode cc = channelCodeDAO.getByChannelSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMdn = subscriberMdnDao.getByMDN(sctl.getSourcemdn());
		
		String emailAddress = subscriberMdn.getSubscriber().getEmail();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		String billerCode = "";
		
		CMBillPayInquiry billPayInquiry = null;
		
		if(SUSPENSE_TO_DESTINATION.equals(transferType)){
			billerCode = billPayments.getBillercode();
			billPayInquiry = new CMBillPayInquiry();
			
			ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
			Service service = serviceDAO.getById(sctl.getServiceid().longValue());
			
			TransactionTypeDAO transactionTypeDAO= DAOFactory.getInstance().getTransactionTypeDAO();
			TransactionType transactionType = transactionTypeDAO.getById(sctl.getTransactiontypeid().longValue()); 
			
			billPayInquiry.setSourceMDN(sctl.getSourcemdn());
			billPayInquiry.setDestMDN(sctl.getDestmdn());
			billPayInquiry.setBillerCode(billPayments.getBillercode());
			billPayInquiry.setIntegrationCode(billPayments.getIntegrationcode());
			billPayInquiry.setPartnerBillerCode(billPayments.getPartnerbillercode());
			billPayInquiry.setAmount(billPayments.getAmount()); // Bala: Changed as it is confusing the Charges part
			log.info("Amount:BillPayMoneyTransferServiceImpl"+billPayments.getAmount());
			
			billPayInquiry.setCharges(BigDecimal.ZERO);
			billPayInquiry.setEmail(emailAddress);
			billPayInquiry.setIsSystemIntiatedTransaction(Boolean.TRUE);
			billPayInquiry.setMessageType(billPayInquiry.header().getMsgType());
			billPayInquiry.setSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
			billPayInquiry.setChannelCode(cc.getChannelcode());
			billPayInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			billPayInquiry.setServiceChargeTransactionLogID(sctlId);
			billPayInquiry.setServiceName(service.getServicename());
			billPayInquiry.setParentTransactionID(0L);
			if(transactionType != null){
				billPayInquiry.setSourceMessage(transactionType.getDisplayname());
			}
			TransactionLog tLog = saveTransactionsLog(CmFinoFIX.MessageType_BillPayInquiry, billPayInquiry.DumpFields());
			
			billPayInquiry.setTransactionID(tLog.getId().longValue());
		}
		else{
			billPayInquiry = (CMBillPayInquiry)mceMessage.getRequest();
			billerCode = billPayInquiry.getBillerCode();
			billPayInquiry.setMessageType(billPayInquiry.header().getMsgType());
			if(null==billPaymentsService.getBillPaymentsRecord(billPayInquiry.getServiceChargeTransactionLogID())){
			billPaymentsService.createBillPayments(billPayInquiry);
			}
		}
		
		Partner partner = getPartner(billerCode);
		Pocket suspencePocket = pocketService.getSuspencePocket(partner);
		
		if(SOURCE_TO_SUSPENSE.equals(transferType)){
			billPayInquiry.setDestPocketID(suspencePocket.getId().longValue());
		}
		else if(SUSPENSE_TO_DESTINATION.equals(transferType)){
			billPayInquiry.setSourcePocketID(suspencePocket.getId().longValue());
			billPayInquiry.setSourceMDN(suspencePocket.getSubscriberMdn().getMdn());
			billPayInquiry.setDestPocketID(getDestPocketByService(partner,sctl).getId().longValue());
		}
		CFIXMsg inquiryResponse =createResponseObject();
		try {
		inquiryResponse = bankService.onTransferInquiryToBank(billPayInquiry);
		} catch(Exception e){
			if(inquiryResponse instanceof BackendResponse){
			((BackendResponse) inquiryResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			((BackendResponse) inquiryResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			}
			mceMessage.setRequest(billPayInquiry);
			mceMessage.setResponse(inquiryResponse);
			log.error(e.getMessage());
			return mceMessage;
		}
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquiry inquiryResponse="+inquiryResponse);
		
		mceMessage.setRequest(billPayInquiry);
		mceMessage.setResponse(inquiryResponse);
		
		return mceMessage;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferInquiryCompleted(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquiryCompleted mceMessage="+mceMessage);

		MCEMessage message = billPayMoneyTransferInquiryCompleted(mceMessage, SOURCE_TO_DESTINATION);
		
		BackendResponse response = (BackendResponse)message.getResponse();
		
		if(((BackendResponse)response).getResult()==CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_INQ_COMPLETED);
		}
		else{
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_INQ_FAILED);
		}

		return message;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferInquirySourceToSuspenseCompleted(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquirySourceToSuspenseCompleted mceMessage="+mceMessage);

		MCEMessage message = billPayMoneyTransferInquiryCompleted(mceMessage, SOURCE_TO_SUSPENSE);
		
		BackendResponse response = (BackendResponse)message.getResponse();
		
		if(((BackendResponse)response).getResult()==CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_INQ_COMPLETED);
		}
		else{
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_INQ_FAILED);
		}
		

		return message;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferInquirySuspenceToDestinationCompleted(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquirySuspenceToDestinationCompleted mceMessage="+mceMessage);

		MCEMessage message = billPayMoneyTransferInquiryCompleted(mceMessage, SUSPENSE_TO_DESTINATION);
		
		BackendResponse response = (BackendResponse)message.getResponse();
		
		if(((BackendResponse)response).getResult()==CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_INQ_COMPLETED);
		}
		else{
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_INQ_FAILED);
		}
		

		return message;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferInquiryCompleted(MCEMessage mceMessage, String transferType) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferInquiryCompleted :: mceMessage="+mceMessage+", mtHeader="+transferType);

		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank)mceMessage.getRequest();
		CMTransferInquiryFromBank fromBank = (CMTransferInquiryFromBank)mceMessage.getResponse();
		CFIXMsg response=createResponseObject();
		try {
		response = bankService.onTransferInquiryFromBank(toBank, fromBank);
		} catch(Exception e){
			if(response instanceof BackendResponse){
			((BackendResponse) response).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			((BackendResponse) response).setResult(CmFinoFIX.ResponseCode_Failure);
			}
			mceMessage.setResponse(response);
			log.error(e.getMessage());
			return mceMessage;
		}
		mceMessage.setResponse(response);

		return mceMessage;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmation(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmation mceMessage="+mceMessage);

		CMBillPay billPay = (CMBillPay)mceMessage.getRequest();
		
		MCEMessage message = billPayMoneyTransferConfirmation(mceMessage, SOURCE_TO_DESTINATION); 
		CFIXMsg response = message.getResponse();
		
		if(response instanceof CMMoneyTransferToBank){
			billPaymentsService.updateBillPayStatus(billPay.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_PENDING);
			message.setDestinationQueue("jms:bpSourceToDestBRQueue?disableReplyTo=true"); //FIXME dont use hard coded value.
		}
		else if(response instanceof BackendResponse){
			if(((BackendResponse)response).getResult()==CmFinoFIX.ResponseCode_Success){
				billPaymentsService.updateBillPayStatus(billPay.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_COMPLETED);
			}
			else{
				billPaymentsService.updateBillPayStatus(billPay.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_FAILED);
			}
		}
		else{
			throw new RuntimeException("Invalid Response :: billPayMoneyTransferConfirmation");
		}

		return message;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmationSourceToSuspense(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmationSourceToSuspense mceMessage="+mceMessage);

		CMBase requestFix = (CMBase)mceMessage.getRequest();
		
		MCEMessage message = billPayMoneyTransferConfirmation(mceMessage, SOURCE_TO_SUSPENSE); 
		CFIXMsg response = message.getResponse();

		((CMBase)message.getRequest()).setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		((CMBase)message.getResponse()).setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		
		if(response instanceof CMMoneyTransferToBank){
			billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_PENDING);
			message.setDestinationQueue(getSourceToSuspenseBankResponseQueue());
		}
		else if(response instanceof BackendResponse){
			if(((BackendResponse)response).getResult() == CmFinoFIX.ResponseCode_Success){
				billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_COMPLETED);
			}
			else{
				billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_FAILED);
			}
		}
		else{
			throw new RuntimeException("Invalid Response :: billPayMoneyTransferConfirmationSourceToSuspense");
		}
		
		return message;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmationSuspenseToDestination(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmationSuspenseToDestination mceMessage="+mceMessage);

		CMBase requestFix = (CMBase)mceMessage.getRequest();
		
		MCEMessage message = billPayMoneyTransferConfirmation(mceMessage, SUSPENSE_TO_DESTINATION); 
		
		((CMBase)message.getRequest()).setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		((CMBase)message.getResponse()).setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());

		CFIXMsg response = message.getResponse();
		
		if(response instanceof CMMoneyTransferToBank){
			billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_PENDING);
			message.setDestinationQueue(getSuspenseToDestBankResponseQueue());
		}
		else if(response instanceof BackendResponse){
			if(((BackendResponse)response).getResult() == CmFinoFIX.ResponseCode_Success){
				billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_COMPLETED);
			}
			else{
				billPaymentsService.updateBillPayStatus(requestFix.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_FAILED);
			}
		}
		else{
			throw new RuntimeException("Invalid Response :: billPayMoneyTransferConfirmationSuspenseToDestination");
		}

		return message;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmation(MCEMessage mceMessage, String transferType) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmation :: mceMessage="+mceMessage+", mtHeader="+transferType);

		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		ChannelCodeDAO channelCodeDAO = DAOFactory.getInstance().getChannelCodeDao();
		ChannelCode cc = channelCodeDAO.getByChannelSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMdn = subscriberMdnDao.getByMDN(sctl.getSourcemdn());
		
		String emailAddress = subscriberMdn.getSubscriber().getEmail();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		String billerCode = billPayments.getBillercode();
		
		CMBillPay billPay = null;
		if(SUSPENSE_TO_DESTINATION.equals(transferType)){
			billPay = new CMBillPay();
			
			BackendResponse inquiryResponse = (BackendResponse)mceMessage.getResponse();
			
			billPay.setSourceMDN(sctl.getSourcemdn());
			billPay.setDestMDN(sctl.getDestmdn());
	        billPay.setBillerCode(billPayments.getBillercode());
			billPay.setIntegrationCode(billPayments.getIntegrationcode());
			billPay.setPartnerBillerCode(billPayments.getPartnerbillercode());
			billPay.setEmail(emailAddress);
			billPay.setIsSystemIntiatedTransaction(Boolean.TRUE);
			billPay.setMessageType(billPay.header().getMsgType());
			billPay.setSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
			billPay.setChannelCode(cc.getChannelcode());
			billPay.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			billPay.setServiceChargeTransactionLogID(sctlId);
			billPay.setServiceName(ServiceAndTransactionConstants.SERVICE_BUY);
			billPay.setParentTransactionID(inquiryResponse.getParentTransactionID());
			billPay.setTransferID(inquiryResponse.getTransferID());

			TransactionLog tLog = saveTransactionsLog(CmFinoFIX.MessageType_BillPayInquiry, billPay.DumpFields(), inquiryResponse.getParentTransactionID());

			billPay.setTransactionID(tLog.getId().longValue());
			billPay.setConfirmed(Boolean.TRUE);
		}
		else{
			billPay = (CMBillPay)mceMessage.getRequest();
		}
		
		Partner partner = billerService.getPartner(billerCode);
		billPay.setInvoiceNumber(billPayments.getInvoicenumber());


		Pocket suspencePocket = pocketService.getSuspencePocket(partner);
		
		if(SOURCE_TO_SUSPENSE.equals(transferType)){
			billPay.setDestPocketID(suspencePocket.getId().longValue());
		}		
		else if(SUSPENSE_TO_DESTINATION.equals(transferType)){
			billPay.setSourcePocketID(suspencePocket.getId().longValue());
			billPay.setSourceMDN(suspencePocket.getSubscriberMdn().getMdn());
			billPay.setDestPocketID(getDestPocketByService(partner,sctl).getId().longValue());
		}
		CFIXMsg confirmationResponse=createResponseObject();
		try {
		confirmationResponse = bankService.onTransferConfirmationToBank(billPay);
		} catch(Exception e){
			if(confirmationResponse instanceof BackendResponse){
			((BackendResponse) confirmationResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			((BackendResponse) confirmationResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			}
			log.error(e.getMessage());
		}
		//DE#62
		if(StringUtils.isNotBlank(billPayments.getInfo1())){
		    ((CMBase)confirmationResponse).setPaymentInquiryDetails(billPayments.getInfo1());
		}
		
		log.info("Amount:>>>>>>>>>>>>>>>"+billPayments.getAmount());
				if(confirmationResponse instanceof BackendResponse){
				((BackendResponse)confirmationResponse).setAmount(billPayments.getAmount());
				}
		
		if(confirmationResponse instanceof CMMoneyTransferToBank){
			((CMMoneyTransferToBank)confirmationResponse).setINTxnId(billPayments.getIntxnid());
			((CMMoneyTransferToBank)confirmationResponse).setComments(billPayments.getInfo2());
		}
		
		mceMessage.setResponse(confirmationResponse);

		return mceMessage;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmationCompleted(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmationCompleted mceMessage="+mceMessage);

		MCEMessage message = billPayMoneyTransferConfirmationCompleted(mceMessage, SOURCE_TO_DESTINATION);
		
		BackendResponse response = (BackendResponse)message.getResponse();
		
		if(((BackendResponse)response).getResult() == CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_COMPLETED);
		}
		else{
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_DEST_FAILED);
		}

		return message;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmationSourceToSuspenseCompleted(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmationSourceToSuspenseCompleted mceMessage="+mceMessage);

		MCEMessage message = billPayMoneyTransferConfirmationCompleted(mceMessage, SOURCE_TO_SUSPENSE);
		
		BackendResponse response = (BackendResponse)message.getResponse();
		CMBase requestFix = (CMBase)mceMessage.getRequest();
				Long sctlId = requestFix.getServiceChargeTransactionLogID();
		 		
				ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
				ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
				BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
				response.setAmount(billPayments.getAmount());
		
		if(((BackendResponse)response).getResult() == CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_COMPLETED);
		}
		else{
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_FAILED);
		}

		return message;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmationSuspenseToDestinationCompleted(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmationSuspenseToDestinationCompleted mceMessage="+mceMessage);

		MCEMessage message = billPayMoneyTransferConfirmationCompleted(mceMessage, SUSPENSE_TO_DESTINATION);
		
		BackendResponse response = (BackendResponse)message.getResponse();
		
		if(((BackendResponse)response).getResult() == CmFinoFIX.ResponseCode_Success){
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_COMPLETED);
		}
		else{
			billPaymentsService.updateBillPayStatus(response.getServiceChargeTransactionLogID(), CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_FAILED);
		}

		return message;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyTransferConfirmationCompleted(MCEMessage mceMessage, String transferType) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyTransferConfirmationCompleted :: mceMessage="+mceMessage+", mtHeader="+transferType);
		
		CMMoneyTransferToBank toBank = (CMMoneyTransferToBank)mceMessage.getRequest();
		CMMoneyTransferFromBank fromBank = (CMMoneyTransferFromBank)mceMessage.getResponse();
		CFIXMsg response=createResponseObject();
		try {
		response = bankService.onTransferConfirmationFromBank(toBank, fromBank);
		} catch(Exception e){
			if(response instanceof BackendResponse){
			((BackendResponse) response).copy(toBank);
			((BackendResponse) response).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			((BackendResponse) response).setResult(CmFinoFIX.ResponseCode_Failure);
			}
			log.error(e.getMessage());
		}
		//DE#62
		if(StringUtils.isNotBlank(toBank.getPaymentInquiryDetails()))
		((CMBase)response).setPaymentInquiryDetails(toBank.getPaymentInquiryDetails());
		
		mceMessage.setResponse(response);

		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage billPayMoneyBillerInquiryFail(MCEMessage mceMessage) {
		log.info("BillPayMoneyTransferServiceImpl :: billPayMoneyBillerInquiryFail :: mceMessage="+mceMessage);
		Long sctlId = ((CMBase) mceMessage.getRequest()).getServiceChargeTransactionLogID();
		BackendResponse response=createResponseObject();

		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);

		response.setSenderMDN(sctl.getSourcemdn());
		response.setSourceMDN(sctl.getSourcemdn());
 		response.setReceiverMDN(sctl.getDestmdn());
 		response.setTransferID(response.getTransferID());
		response.setParentTransactionID(response.getParentTransactionID());
		response.setServiceChargeTransactionLogID(sctlId);
	
		if(response instanceof BackendResponse){
			
 			((BackendResponse) response).setInternalErrorCode(NotificationCodes.AirtimePurchaseFailed.getInternalErrorCode());
			((BackendResponse) response).setResult(CmFinoFIX.ResponseCode_Failure);
		
		}
		mceMessage.setResponse(response);
		return mceMessage;
	}
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage suspenseAndChargesToSourceReversal(MCEMessage mceMessage){
		log.info("BillPayMoneyTransferServiceImpl :: suspenseAndChargesToSourceReversal BEGIN mceMessage="+mceMessage);

		MCEMessage reversalMce = new MCEMessage();
		CMAutoReversal autoReversal = new CMAutoReversal();
		
		Long sctlId = ((CMBase)mceMessage.getRequest()).getServiceChargeTransactionLogID();
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);

		Partner partner = getPartner(billPayments.getBillercode());

		Pocket suspencePocket = pocketService.getSuspencePocket(partner);
		
		Long ctId = sctl.getCommoditytransferid().longValue();
		
		//for some old airtime transactions ctid is not recorded in sctl.
		//get the ctid from the map chargetxn_transfer_map table.Least ct id value
		//save it to sctl
		if(ctId == null){
			
			ctId = Long.MAX_VALUE;

			ChargeTxnCommodityTransferMapDAO ctctmdao = DAOFactory.getInstance().getTxnTransferMap();
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctlId);
			List<ChargetxnTransferMap> list= ctctmdao.get(query);
			
			for(ChargetxnTransferMap map : list){
				if(ctId>map.getCommoditytransferid().longValue())
					ctId = map.getCommoditytransferid().longValue();
			}
			
			sctl.setCommoditytransferid(ctId);
			sctlDao.save(sctl);
		}
		
		CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransfer ct = ctDao.getById(ctId);
		
		autoReversal.setSourcePocketID(ct.getPocket().getId().longValue());
		autoReversal.setDestPocketID(suspencePocket.getId().longValue());
		autoReversal.setServiceChargeTransactionLogID(sctlId);
		autoReversal.setAmount(ct.getAmount());
		autoReversal.setCharges(ct.getCharges());
		
		reversalMce.setRequest(autoReversal);
		
		reversalMce.setDestinationQueue(getReversalResponseQueue());
		
		billPayments.setBillpaystatus(CmFinoFIX.BillPayStatus_BILLPAY_FAILED);
	    billPaymentsService.saveBillPayment(billPayments);
		

		return reversalMce;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	private Pocket getDestPocketByService(Partner partner, ServiceChargeTxnLog sctl){
		Pocket destPocket = null;
		
//		ServiceDAO serviceDao = DAOFactory.getInstance().getServiceDAO();
//		Service service = serviceDao.getById(sctl.getServiceID());
		
		PartnerServicesDAO partnerServicesDao = DAOFactory.getInstance().getPartnerServicesDAO();
		PartnerServicesQuery query = new PartnerServicesQuery();
		query.setPartnerId(partner.getId().longValue());
		query.setServiceId(sctl.getServiceid().longValue());
		
		List<PartnerServices> partnerServiceList = partnerServicesDao.get(query);
		
		if(partnerServiceList.size() > 0){
			PartnerServices partnerService = partnerServiceList.get(0);
			destPocket = partnerService.getPocketByDestpocketid();
		}
		
		return destPocket;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	private Partner getPartner(String billerCode){
		Partner partner = null;
		MFSBillerDAO billerDAO = DAOFactory.getInstance().getMFSBillerDAO();
		MFSBillerQuery billerQuery = new MFSBillerQuery();
		billerQuery.setBillerCode(billerCode);
		List<MfsBiller> billersList = billerDAO.get(billerQuery);
		MfsBiller biller = null;
		if(billersList.size()==1){
			biller = billersList.get(0);
		}
		if(biller!=null){
			MFSBillerPartnerDAO billerPartnerDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
			MFSBillerPartnerQuery billerPartnerQuery = new MFSBillerPartnerQuery();
			billerPartnerQuery.setMfsBillerId(biller.getId().longValue());
			List<MfsbillerPartnerMap> billerPartners = billerPartnerDAO.get(billerPartnerQuery);
			if(billerPartners.size()==1){
				MfsbillerPartnerMap billerPartner = billerPartners.get(0);
				partner = billerPartner.getPartner();
			}
		}
		
		return partner;
	}
	
	protected TransactionLog saveTransactionsLog(Integer messageCode, String data) {
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(messageCode);
		transactionsLog.setMessagedata(data);
        MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
        MfinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
	
	protected TransactionLog saveTransactionsLog(Integer messageCode, String data,Long parentTxnID)
	{
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(messageCode);
		transactionsLog.setMessagedata(data);
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		MfinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		if(parentTxnID!=null)
			transactionsLog.setParenttransactionid(new BigDecimal(parentTxnID));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
	
	public BankService getBankService() {
		return bankService;
	}

	@Override
	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	@Override
	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}


	public String getReversalResponseQueue() {
	    return reversalResponseQueue;
    }

	public String getSourceToSuspenseBankResponseQueue() {
	    return sourceToSuspenseBankResponseQueue;
    }

	@Override
	public void setReversalResponseQueue(String reversalResponseQueue) {
		this.reversalResponseQueue = reversalResponseQueue;
	}
	@Override
	public void setSourceToSuspenseBankResponseQueue(String sourceToSuspenseBankResponseQueue) {
	    this.sourceToSuspenseBankResponseQueue = sourceToSuspenseBankResponseQueue;
    }
	@Override
	public void setSuspenseToDestBankResponseQueue(String suspenseToDestBankResponseQueue) {
		this.suspenseToDestBankResponseQueue = suspenseToDestBankResponseQueue;
	}

	public String getSuspenseToDestBankResponseQueue() {
	    return suspenseToDestBankResponseQueue;
    }

	
}
