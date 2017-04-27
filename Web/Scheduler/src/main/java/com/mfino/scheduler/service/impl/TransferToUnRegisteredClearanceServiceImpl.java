/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.EnumTextService;
import com.mfino.service.SCTLService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionTypeService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.transactionapi.handlers.ReverseTransactionToUnregistered;
import com.mfino.transactionapi.handlers.money.AutoReverseHandler;

/**
 * @author Bala Sunku
 *
 */
@Service("TransferToUnRegisteredClearanceServiceImpl")
public class TransferToUnRegisteredClearanceServiceImpl  {
	
	public static final int TRANSFER_TO_UNREGISTERED_EXPIRY_TIME = 2; // Default 2 days
	private static Logger log = LoggerFactory.getLogger(TransferToUnRegisteredClearanceServiceImpl.class);
	
	@Autowired
	@Qualifier("AutoReverseHandlerImpl")
	private AutoReverseHandler autoReverseHandler;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("UnRegisteredTxnInfoServiceImpl")
	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
	
	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService  serviceChargeTransactionLogService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private BulkUploadEntryService  bulkUploadEntryService;
	
	private DAOFactory daoFactory = DAOFactory.getInstance();
	private ChannelCodeDAO channelcodeDao = daoFactory.getChannelCodeDao();
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("ReverseTransactionToUnregisteredImpl")
	private ReverseTransactionToUnregistered reverseTransactionToUnregistered;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	public void checkExpirationOfTransferToUnRegistered() {
		log.info("TransferToUnRegisteredClearanceServiceImpl :: checkExpirationOfTransferToUnRegistered :: BEGIN");
		long expiryDays = systemParametersService.getLong(SystemParameterKeys.TRANSFER_TO_UNREGISTERED_EXPIRY_TIME);
		if (expiryDays == -1) {
			expiryDays = TRANSFER_TO_UNREGISTERED_EXPIRY_TIME;
		}
		long expiryTime = expiryDays * 24 * 60 * 60 *1000;
		Timestamp cuurentTime = new Timestamp();
		
		String reverseCharge = systemParametersService.getString(SystemParameterKeys.REVERSE_CHARGE_FOR_EXPIRED_TRANSFER_TO_UNREGISTERED);
		boolean isChargeRevese = false;
		if ("true".equalsIgnoreCase(reverseCharge)) {
			isChargeRevese = true;
		}
		
		Long chargeRevFundPocket = systemParametersService.getLong(SystemParameterKeys.CHARGE_REVERSAL_FUNDING_POCKET);
				
		UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
		Integer[] status = new Integer[2];
		status[0] = CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED;
		status[1] = CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED;
		urtiQuery.setMultiStatus(status);
		
		List<UnregisteredTxnInfo> unRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
		
		if (CollectionUtils.isNotEmpty(unRegisteredTxnInfos)) {
			for (UnregisteredTxnInfo urti: unRegisteredTxnInfos) {
				if ( !(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(urti.getTransactionname())) ) {
					long diffTime = cuurentTime.getTime() - urti.getCreatetime().getTime();
					if (diffTime > expiryTime) {
						revertTransfer(urti, isChargeRevese, chargeRevFundPocket);
					} 
				}
			}
		}
		log.info("TransferToUnRegisteredClearanceServiceImpl :: checkExpirationOfTransferToUnRegistered :: END");
	}

	private void revertTransfer(UnregisteredTxnInfo urti, boolean isChargeRevese, Long chargeRevFundPocket) {
		log.info("TransferToUnRegisteredClearanceServiceImpl :: revertTransfer :: START");
		String failureReason="Time for Unregistered Subscriber is over";
		
		ServiceChargeTxnLog sctl = urti.getServiceChargeTxnLog();
		ServiceChargeTxnLog sctltrx=getReverseTransaction(sctl);
        
		ServiceChargeTxnLog parentSCTL = sctlService.getBySCTLID(sctltrx.getParentsctlid().longValue());

		sctltrx.setFailurereason(failureReason);
		sctltrx.setStatus(CmFinoFIX.SCTLStatus_Reverse_Start);

		parentSCTL.setAmtrevstatus(CmFinoFIX.SCTLStatus_Reverse_Approved.longValue());

		sctlService.saveSCTL(parentSCTL);
		sctlService.saveSCTL(sctltrx);

		// Send the  Reverse request to Backend for processing
		log.info("TransferToUnRegisteredClearanceServiceImpl :: Send the  Reverse request to Backend for processing :: PROCESSING");
		reverseTransactionToUnregistered.processReverseRequest(sctltrx, parentSCTL);
		
		log.info("TransferToUnRegisteredClearanceServiceImpl :: revertTransfer :: END");
        
	}
	
	private ServiceChargeTxnLog getReverseTransaction(ServiceChargeTxnLog sctl) {
		//Check the SCTL status 
		if (((sctl.getParentsctlid() == null)) && 
				((CmFinoFIX.SCTLStatus_Confirmed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Started.equals(sctl.getStatus()) ||
				 CmFinoFIX.SCTLStatus_Distribution_Completed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Failed.equals(sctl.getStatus())))
				 && ((null == sctl.getAmtrevstatus()) || (CmFinoFIX.SCTLStatus_Reverse_Failed.equals(sctl.getAmtrevstatus())))
				 ) {
			
			// Get the Max CT Record for the Transaction.
			long sourcePocketId = 0l;
			CommodityTransfer maxCT = null;
			CommodityTransferDAO ctDao = daoFactory.getCommodityTransferDAO();
			ChargeTxnCommodityTransferMapDAO txnCommodityTransferMapDAO = daoFactory.getTxnTransferMap();
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getId().longValue());
			List<ChargetxnTransferMap> lstTxnCommodityTransferMaps = txnCommodityTransferMapDAO.get(query);
			if (CollectionUtils.isNotEmpty(lstTxnCommodityTransferMaps)) {
				for (ChargetxnTransferMap ctmap: lstTxnCommodityTransferMaps) {
					CommodityTransfer ct = ctDao.getById(ctmap.getCommoditytransferid().longValue());
					if (! CmFinoFIX.TransactionUICategory_Charge_Distribution.equals(ct.getUicategory())) {
						if (maxCT == null) {
							maxCT = ct;
						}
						else if (ct.getId().longValue() > maxCT.getId().longValue()) {
							maxCT = ct;
						}
					}

				}
			}
			
			// Get the Source pocket for the Reverse transaction based on the transaction type.		 
			TransactionTypeDAO transactionTypeDao = daoFactory.getTransactionTypeDAO();
			TransactionType transactionType = transactionTypeDao.getById(sctl.getTransactiontypeid().longValue());
			if (transactionType != null && 
					((ServiceAndTransactionConstants.TRANSACTION_PURCHASE.equalsIgnoreCase(transactionType.getTransactionname())) || 
					(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY.equalsIgnoreCase(transactionType.getTransactionname()))) ) {
				PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
				List<PartnerServices> lst = psDAO.getPartnerServices(sctl.getDestpartnerid().longValue(), sctl.getServiceproviderid().longValue(), sctl.getServiceid().longValue());
				if (CollectionUtils.isNotEmpty(lst)) {
					PartnerServices ps = lst.get(0);
					sourcePocketId = ps.getPocketBySourcepocket().getId().longValue();
				}
			} 
			else {
				sourcePocketId = maxCT.getDestpocketid().longValue();
			}
			
			// Get the Service Name for the Reverse Transaction.
			String serviceName = ServiceAndTransactionConstants.SERVICE_WALLET;
			PocketDAO pocketDao = daoFactory.getPocketDAO();
			Pocket sourcePocket = pocketDao.getById(sourcePocketId);
			if (sourcePocket != null && CmFinoFIX.PocketType_BankAccount.equals(sourcePocket.getPocketTemplateByPockettemplateid().getType())) {
				serviceName = ServiceAndTransactionConstants.SERVICE_BANK;
			}
			
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(sctl.getDestmdn());
			sc.setDestMDN(sctl.getSourcemdn());
//			ChannelCode cc = channelcodeDao.getByChannelSourceApplication(CmFinoFIX.SourceApplication_Web);
			ChannelCode cc = channelcodeDao.getByChannelSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
			sc.setChannelCodeId(cc.getId().longValue());
			sc.setServiceName(serviceName);
//			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION);
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_AUTOREVERSE_TRANSFER_TO_UNREGISTERED);
			sc.setTransactionAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
			sc.setReverseTransaction(true);
			sc.setParentSctlId(sctl.getId().longValue());
			Transaction transaction = null;
			try {
				transaction = transactionChargingService.getCharge(sc);
			} catch (InvalidServiceException e) {
				CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
				error.setErrorDescription(MessageText._("Invalid Service"));
				return null;
			} catch (InvalidChargeDefinitionException e) {
				CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
				error.setErrorDescription(MessageText._("Invalid Charge Definition"));				
				return null;
			} 
			if (transaction != null) {
				sctl = transaction.getServiceChargeTransactionLog();
			} else {
				sctl = null;
			}
		} else {
			sctl = null;
		}
		return sctl;
	}
}
