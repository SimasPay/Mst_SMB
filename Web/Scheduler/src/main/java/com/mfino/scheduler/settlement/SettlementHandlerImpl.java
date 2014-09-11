package com.mfino.scheduler.settlement;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.query.SCTLSettlementMapQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.SCTLSettlementMap;
import com.mfino.domain.ScheduleTemplate;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.ServiceSettlementConfig;
import com.mfino.domain.SettlementTransactionLogs;
import com.mfino.domain.SettlementTransactionSCTLMap;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.TransferStatus;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;
import com.mfino.handlers.SettlementFixCommunicationHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.MfinoServiceProviderService;
import com.mfino.service.PartnerServicesService;
import com.mfino.service.SCTLSettlementMapService;
import com.mfino.service.ScheduleTemplateService;
import com.mfino.service.SettlementTransactionLogsService;
import com.mfino.service.SettlementTransactionSCTLMapService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionsLogCoreService;


/**
 * @author sasidhar
 * Functionality to do settlement for a given partner service.
 */
@Service("SettlementHandlerImpl")
public class SettlementHandlerImpl implements SettlementHandler{
	private static BigDecimal ZERO = new BigDecimal(0);
	// *FindbugsChange*
	// Previous -- protected static Logger log = LoggerFactory.getLogger(SettlementHandler.class);
	private static final Logger log = LoggerFactory.getLogger(SettlementHandlerImpl.class);
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SettlementTransactionLogsServiceImpl")
	private SettlementTransactionLogsService settlementTransactionLogsService ;

	@Autowired
	@Qualifier("PartnerServicesServiceImpl")
	private PartnerServicesService partnerServicesService ;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService ;
	
	@Autowired
	@Qualifier("SCTLSettlementMapServiceImpl")
	private SCTLSettlementMapService sCTLSettlementMapService ;
	
	@Autowired
	@Qualifier("MfinoServiceProviderServiceImpl")
	private MfinoServiceProviderService mfinoServiceProviderService ;
	
	@Autowired
	@Qualifier("SettlementTransactionSCTLMapServiceImpl")
	private SettlementTransactionSCTLMapService settlementTransactionSCTLMapService ;
	
	@Autowired
	@Qualifier("TransactionsLogCoreServiceImpl")
	private TransactionsLogCoreService transactionsLogCoreService ;
	
	@Autowired
	@Qualifier("ScheduleTemplateServiceImpl")
	private ScheduleTemplateService scheduleTemplateService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	private HibernateSessionHolder hibernateSessionHolder = null;
	
	private SessionFactory sessionFactory;
	
	private static SettlementHandlerImpl settlementHandlerFactory;
	
	public static SettlementHandlerImpl createInstance(){
		  if(settlementHandlerFactory==null){
		   settlementHandlerFactory = new SettlementHandlerImpl();
		  }
		  
		  return settlementHandlerFactory;
		 }
		 
	public static SettlementHandlerImpl getInstance(){
		  if(settlementHandlerFactory==null){
		   throw new RuntimeException("Instance is not already created");
		  }
		  return settlementHandlerFactory;
		 }
	
	//TODO replace below with autowire, possible?
	private SettlementFixCommunicationHandler settlementFixCommunicationHandler = new SettlementFixCommunicationHandler();

	
	public void doSettlement(Long partnerServiceId) throws MfinoRuntimeException{
		log.info("PartnerSettlement :: doSettlement called for partner service id "+partnerServiceId);
		PartnerServices partnerService = partnerServicesService.getById(partnerServiceId);
		doSettlement(partnerService);
	}
	
	private void doSettlement(PartnerServices partnerService) throws MfinoRuntimeException{
		
		log.info("PartnerSettlementService :: doSettlement() BEGIN partnerService.getID()="+partnerService.getID());
		/*
		 * Initilizations and Validations.
		 */
		Partner partner = partnerService.getPartner();
		SettlementTransactionLogs settlementTransactionLog = new SettlementTransactionLogs();
		settlementTransactionLog.setMSPID(1L);
		settlementTransactionLog.setTransferStatus(TransferStatus.INITIALIZED.getTransferStatus());
		settlementTransactionLog.setPartnerServicesID(partnerService.getID());
		settlementTransactionLog.setAmount(ZERO);
		settlementTransactionLog.setDescription("");
		
		settlementTransactionLogsService.save(settlementTransactionLog);
		Long stlID = null;
		if(settlementTransactionLog != null)
			stlID = settlementTransactionLog.getID();
		
		if(!(CmFinoFIX.PartnerServiceStatus_Active.equals(partnerService.getStatus()))){
			log.info("PartnerSettlementService :: doSettlement() PartnerService is not active");
			settlementTransactionLog.setResponse("Partner Service Not Active");
			settlementTransactionLog.setTransferStatus(TransferStatus.PARTNER_SERVICE_NOT_ACTIVE.getTransferStatus());
			settlementTransactionLogsService.save(settlementTransactionLog);
			return;
		}
		
		Pocket collectorPocket = partnerService.getPocketByCollectorPocket();
		Pocket settlementPocket = null;
		Set<ServiceSettlementConfig> settlementConfigs = partnerService.getServiceSettlementConfigFromPartnerServiceID();
		ServiceSettlementConfig settlementConfig = null;
		
		if((settlementConfigs != null) && (settlementConfigs.size() > 0)){
			/*
			 * Expectation is currently we are not considering date effectivity and there will be only one settlement configuration.
			 * This part needs to be modified when date effectivity comes into picture.
			 */
			for(ServiceSettlementConfig sc : settlementConfigs){
				if((sc.getIsDefault() != null) && (sc.getIsDefault())){
					settlementConfig = sc;
					break;
				}
			}
		}
		
		if(settlementConfig == null){
			log.info("PartnerSettlementService :: doSettlement() Settlement Config Not Defined");
			settlementTransactionLog.setResponse("Settlement Config Not Defined");
			settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
			settlementTransactionLogsService.save(settlementTransactionLog);
			return;
		} 
		
		log.info("PartnerSettlementService :: doSettlement() Settlement Config ID="+settlementConfig.getID());
		
		settlementTransactionLog.setServiceSettlementConfigID(settlementConfig.getID());
		settlementPocket = settlementConfig.getSettlementTemplate().getPocketBySettlementPocket();
		
		//For CutoffTime
		Long stID = null;
		if(settlementConfig.getSettlementTemplate().getCutoffTime() != null)
			stID = settlementConfig.getSettlementTemplate().getScheduleTemplate().getID();
		
		if(collectorPocket == null){
			log.info("PartnerSettlementService :: doSettlement() Collector Pocket is null");
			settlementTransactionLog.setResponse("Collector Pocket is null");
			settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
			settlementTransactionLogsService.save(settlementTransactionLog);
			return;
		} else{
			if(!collectorPocket.getPocketTemplate().getCommodity().equals(CmFinoFIX.Commodity_Money)){
				log.info("PartnerSettlementService :: doSettlement() Collector pocket commodity type should be money");
				settlementTransactionLog.setResponse("Collector pocket commodity type should be money");
				settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
				settlementTransactionLogsService.save(settlementTransactionLog);
				return;
			}
			if(!collectorPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_SVA)){
				log.info("PartnerSettlementService :: doSettlement() Collector pocket type should be SVA");				
				settlementTransactionLog.setResponse("Collector pocket type should be SVA");
				settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
				settlementTransactionLogsService.save(settlementTransactionLog);
				return;
			}
		}
		
		if(settlementPocket == null){
			log.info("PartnerSettlementService :: doSettlement() Source Pocket is null");
			settlementTransactionLog.setResponse("Source Pocket is null");
			settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
			settlementTransactionLogsService.save(settlementTransactionLog);
			return;
		} else{
			if(!settlementPocket.getPocketTemplate().getCommodity().equals(CmFinoFIX.Commodity_Money)){
				log.info("PartnerSettlementService :: doSettlement() Settlement pocket commodity type should be money");
				settlementTransactionLog.setResponse("Settlement pocket commodity type should be money");
				settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
				settlementTransactionLogsService.save(settlementTransactionLog);
				return;
			}
			//Settlement Pocket could be either an Emoney or a Bank Account			
			if(!settlementPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)&&
					!(settlementPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_SVA))){
				log.info("PartnerSettlementService :: doSettlement() Settlement pocket type should be Bank or SVA Account");
				settlementTransactionLog.setResponse("Settlement pocket type should be Bank or SVA Account");
				settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
				settlementTransactionLogsService.save(settlementTransactionLog);
				return;
			}
		}
		
//		Long minimumBalance = collectorPocket.getPocketTemplate().getMinimumStoredValue() == null ? 0 : collectorPocket.getPocketTemplate().getMinimumStoredValue();
//		Long settlementAmount = 0L; 
		BigDecimal minimumBalance = collectorPocket.getPocketTemplate().getMinimumStoredValue() == null ? ZERO : collectorPocket.getPocketTemplate().getMinimumStoredValue();

		//BigDecimal pendingAmount = getPendingAmount(partner.getID(), collectorPocket);
		BigDecimal pendingAmount = getPendingAmount(partner.getID(), collectorPocket, stlID, stID);
		minimumBalance = minimumBalance.add(pendingAmount); 
		
		BigDecimal settlementAmount = ZERO; 
		
//		if((collectorPocket.getCurrentBalance() != null) && (collectorPocket.getCurrentBalance() > minimumBalance)){
//			settlementAmount = collectorPocket.getCurrentBalance() - minimumBalance;
		BigDecimal currentBalance = collectorPocket.getCurrentBalance();
		if((currentBalance != null) && (currentBalance.compareTo(minimumBalance) > 0)){
			settlementAmount = collectorPocket.getCurrentBalance().subtract(minimumBalance);
			log.info("PartnerSettlementService :: doSettlement() settlement amount calculated="+settlementAmount);
		}
		else{
			if(currentBalance.compareTo(BigDecimal.ZERO) == 0) {
				log.info("PartnerSettlementService :: doSettlement() collector pocket balance is 0");
				settlementTransactionLog.setResponse("collector pocket balance is 0");
				settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
				settlementTransactionLogsService.save(settlementTransactionLog);
				return;				
			}else{
				log.info("PartnerSettlementService :: doSettlement() collector pocket amount less than min amount to do settlement, Amount in Pending "+pendingAmount);
				settlementTransactionLog.setResponse("collector pocket amount less than min amount to do settlement");
				settlementTransactionLog.setTransferStatus(TransferStatus.VALIDATION_FAILED.getTransferStatus());
				settlementTransactionLogsService.save(settlementTransactionLog);
				return;
			}
		}
		
		log.info("settlement amount is "+settlementAmount);
		
		/*
		 * Actual transfer code begins
		 */
		Subscriber subscriber = subscriberService.getSubscriberbySubscriberId(partner.getSubscriber().getID());
		SubscriberMDN subscriberMDN = subscriber.getSubscriberMDNFromSubscriberID().iterator().next();
		
		CMSettlementOfCharge settlementFixMessage = new CMSettlementOfCharge();
		
		settlementFixMessage.setSourceMDN(subscriberMDN.getMDN());
		settlementFixMessage.setAmount(settlementAmount);
		settlementFixMessage.setSourcePocketID(collectorPocket.getID());
		settlementFixMessage.setDestPocketID(settlementPocket.getID());
		settlementFixMessage.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		settlementFixMessage.setSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		settlementFixMessage.setServiceName(ServiceAndTransactionConstants.SERVICE_SYSTEM);
		
		TransactionsLog transactionsLog = saveTransactionsLog(CmFinoFIX.MessageType_SettlementOfCharge, " ");
		settlementFixMessage.setTransactionID(transactionsLog.getID());
		
		// Generating the SCTL Entry 

		ChannelCode cc = getChannelCode(CmFinoFIX.SourceApplication_BackEnd);
		ServiceChargeTransactionLog sctl = new ServiceChargeTransactionLog();
		
		try{
			sctl.setCalculatedCharge(BigDecimal.ZERO);
			sctl.setChannelCodeID(cc.getID());
			sctl.setSourceMDN(settlementFixMessage.getSourceMDN());
			sctl.setDestMDN(settlementFixMessage.getSourceMDN());
			sctl.setServiceID(transactionChargingService.getServiceId(settlementFixMessage.getServiceName()));
			sctl.setServiceProviderID(transactionChargingService.getServiceProviderId(null));
			sctl.setStatus(CmFinoFIX.SCTLStatus_Processing);
			sctl.setTransactionAmount(settlementFixMessage.getAmount());
			sctl.setTransactionTypeID(transactionChargingService.getTransactionTypeId(ServiceAndTransactionConstants.TRANSACTION_CHARGE_SETTLEMENT));
			sctl.setTransactionID(transactionsLog.getID());
			sctl.setDestPartnerID(partner.getID());
		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			return;
		}
		long sctlId = transactionChargingService.saveServiceTransactionLog(sctl);
		settlementFixMessage.setServiceChargeTransactionLogID(sctlId);
		
		CFIXMsg response = settlementFixCommunicationHandler.process(settlementFixMessage);
		TransactionResponse transferResponse = settlementFixCommunicationHandler.checkBackEndResponse(response);
		log.info("Transfer Response = "+transferResponse.getMessage());
		if(transferResponse.getTransactionId()!=null){
		sctl.setTransactionID(transferResponse.getTransactionId());
		}
		if(transferResponse.getTransferId()!=null){
		sctl.setCommodityTransferID(transferResponse.getTransferId());
		}
		//Settlement Enhancement
		SettlementTransactionSCTLMap stsm = new SettlementTransactionSCTLMap();
		stsm.setSctlId(sctl.getID());
		stsm.setStlID(settlementTransactionLog.getID());
        mFinoServiceProvider msp = mfinoServiceProviderService.getMFSPbyID(1);
        stsm.setmFinoServiceProviderByMSPID(msp);
		
		if(settlementAmount != null){
			settlementTransactionLog.setAmount(settlementAmount);
			settlementTransactionLog.setDescription("PartnerSettlementService for SettlementAmount: "+settlementAmount);
		}
		if (transferResponse.isResult())
		{
			log.info("PartnerSettlementService :: doSettlement() Transfer Success Response="+transferResponse);
			settlementTransactionLog.setResponse("Transfer Success Notification ("+transferResponse+")");
			settlementTransactionLog.setTransferStatus(TransferStatus.COMPLETED.getTransferStatus());
			if(transferResponse.getTransferId()!=null){
			settlementTransactionLog.setCommodityTransferID(transferResponse.getTransferId());
			}
			settlementTransactionLogsService.save(settlementTransactionLog);
			transactionChargingService.completeTheTransaction(sctl);
			
			stsm.setStatus(settlementTransactionLog.getTransferStatus());
			settlementTransactionSCTLMapService.save(stsm);
			return;
		}else{
			log.info("PartnerSettlementService :: doSettlement() Transfer Failed Response="+transferResponse.getMessage());
			settlementTransactionLog.setResponse("Transfer Failed Notification ("+transferResponse.getCode()+")");
			settlementTransactionLog.setTransferStatus(TransferStatus.TRANSFER_FAILED.getTransferStatus());
			settlementTransactionLogsService.save(settlementTransactionLog);
			String errorMsg = transferResponse.getMessage();
			// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
			if (errorMsg.length() > 255) {
				errorMsg = errorMsg.substring(0, 255);
			}
			transactionChargingService.failTheTransaction(sctl, errorMsg);
			
			stsm.setStatus(settlementTransactionLog.getTransferStatus());
			settlementTransactionSCTLMapService.save(stsm);
			return;
		}
	
	}

	private TransactionsLog saveTransactionsLog(Integer messageCode, String data) {
		
		mFinoServiceProvider msp = mfinoServiceProviderService.getMFSPbyID(1);
		
		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(messageCode);
		transactionsLog.setMessageData(data);
		transactionsLog.setmFinoServiceProviderByMSPID(msp);
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		transactionsLogCoreService.save(transactionsLog);
		return transactionsLog;
	}
	
	private String msgToData(CMBase base) {
		CMultiXBuffer buffer = new CMultiXBuffer();
		try{
			base.toFIX(buffer);
			String data =new String(buffer.DataPtr());
			return data;
		}catch (Exception error) {
			log.error("error in converting msg to data", error);
			return base.DumpFields();
		}
	}
	
	/**
	 * @param channelIdStr
	 * @return
	 */
	private ChannelCode getChannelCode(Integer sourceApplication) {
		return channelCodeService.getChannelCodebySourceApplication(sourceApplication);
	}
	
	public static void main(String[] args) {
		log.info("PartnerSettlementService Test");
		
		try {
			
			SettlementHandlerImpl pss = new SettlementHandlerImpl();
			PartnerServicesDAO partnerServicesDao = DAOFactory.getInstance().getPartnerServicesDAO();
			List<PartnerServices> partnerServices = partnerServicesDao.getPartnerServices(18L, 1L, 1L);
			log.info("Partner Services Object "+partnerServices);
			
			pss.doSettlement(partnerServices.get(0));
			
			
		} catch (Exception e) {
			throw new MfinoRuntimeException(e);
		}
		
	}
	
	/*private BigDecimal getPendingAmount(Long partnerId, Pocket collectorPocket){
		log.debug("SettlementHandler :: getPendingAmount() BEGIN");
		BigDecimal pendingAmount = ZERO;
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		
		sctlQuery.setStatus(CmFinoFIX.SCTLStatus_Pending);
		sctlQuery.setDestPartnerID(partnerId);
		
		List<ServiceChargeTransactionLog> sctlList = sctlDao.get(sctlQuery);
		
		ChargeTxnCommodityTransferMapDAO chargeTxnCTDao = DAOFactory.getInstance().getTxnTransferMap();
		List<Long> pctIdList = new ArrayList<Long>();
		for(ServiceChargeTransactionLog sctl : sctlList){
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getID());
			
			List<ChargeTxnCommodityTransferMap> chargeTxnCTMap = chargeTxnCTDao.get(query);
			for(ChargeTxnCommodityTransferMap ctMap : chargeTxnCTMap){
				pctIdList.add(ctMap.getCommodityTransferID());
			}
		}
		
		PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		for(Long pctId : pctIdList){
			PendingCommodityTransfer pct = null;
			
			try{
				pct = pctDao.getById(pctId);
			}
			catch(Exception e){
				log.error("SettlementHandler :: Error Fetching PCT with id="+pctId);
			}
			
			if(pct != null){
				pendingAmount = pendingAmount.add(pct.getAmount());
			}
		}
		
		log.debug("SettlementHandler :: getPendingAmount() END");
		return pendingAmount;
	}*/
	private BigDecimal getPendingAmount(Long partnerId, Pocket collectorPocket, Long stlID, Long stID){
		log.debug("SettlementHandler :: getPendingAmount() BEGIN");
		BigDecimal pendingAmount = ZERO;
		
		SCTLSettlementMapQuery sctlSMQuery = new SCTLSettlementMapQuery();
		
		ScheduleTemplate st = new ScheduleTemplate();
		
		Date cutoff = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(cutoff);
		
		try{
			if(stID != null){
				st = scheduleTemplateService.getScheduleTemplateById(stID);
				if(StringUtils.isNotBlank(st.getTimerValueHH()))
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(st.getTimerValueHH()));
				if(StringUtils.isNotBlank(st.getTimerValueMM()))
					cal.set(Calendar.MINUTE, Integer.valueOf(st.getTimerValueMM()));
				cutoff = cal.getTime();
			}
			
			sctlSMQuery.setSettlementStatus(CmFinoFIX.SettlementStatus_Completed);
			sctlSMQuery.setPartnerID(partnerId);
			sctlSMQuery.setLastUpdateTimeLT(cutoff);
			
			List<SCTLSettlementMap> sctlSMList = sCTLSettlementMapService.get(sctlSMQuery);
	
			log.info("CutoffTime : "+cutoff+"PartnerID : "+partnerId);
			
			for(SCTLSettlementMap sctl : sctlSMList){
//				pendingAmount = pendingAmount.add(sctl.getAmount());
				sctl.setStatus(CmFinoFIX.SettlementStatus_Settled);
				sctl.setStlID(stlID);
				sCTLSettlementMapService.save(sctl);
			}
		
		} catch (Exception ex) {
			log.error("Exception occured in getting pendingAmount",ex);
			return pendingAmount;
		}
		
		log.debug("SettlementHandler :: getPendingAmount() END");
		return pendingAmount;
	}
}
