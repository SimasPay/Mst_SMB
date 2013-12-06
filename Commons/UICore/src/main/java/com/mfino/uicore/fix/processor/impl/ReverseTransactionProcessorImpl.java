package com.mfino.uicore.fix.processor.impl;

import static com.mfino.constants.SystemParameterKeys.CHARGE_REVERSAL_FUNDING_POCKET;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionType;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSReverseTransaction;
import com.mfino.fix.CmFinoFIX.CMJSReverseTransaction.CGEntries;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ReverseTransactionProcessor;

@Service("ReverseTransactionProcessorImpl")
public class ReverseTransactionProcessorImpl extends BaseFixProcessor  implements ReverseTransactionProcessor{
	
	private DAOFactory daoFactory = DAOFactory.getInstance();
	private ServiceChargeTransactionLogDAO sctlDao =daoFactory.getServiceChargeTransactionLogDAO(); 
	private ChannelCodeDAO channelcodeDao = daoFactory.getChannelCodeDao();
	private PartnerDAO partnerDao = daoFactory.getPartnerDAO();
	private TransactionTypeDAO ttDao = daoFactory.getTransactionTypeDAO();
	private ServiceDAO serviceDao = daoFactory.getServiceDAO();
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSReverseTransaction realMsg = (CMJSReverseTransaction) msg;
		
		 if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			 ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
			 log.info("ReverseTransaction request for SCTL --> " + realMsg.getServiceChargeTransactionLogID() + " recieved");
			 if(realMsg.getServiceChargeTransactionLogID()!=null) {
				 query.setId(realMsg.getServiceChargeTransactionLogID());
				 List<ServiceChargeTransactionLog> results = sctlDao.get(query);
				 
				 if (CollectionUtils.isNotEmpty(results)) {
		                realMsg.allocateEntries(results.size());
		                for (int i = 0; i <results.size(); i++) {
	                        ServiceChargeTransactionLog sctl = results.get(i);
	                        CMJSReverseTransaction.CGEntries entry = new CMJSReverseTransaction.CGEntries();
	                        
	                        ServiceChargeTransactionLog reverseAmountSctl = getReverseTransaction(sctl);
	                        ServiceChargeTransactionLog reverseChargeSctl = getChargeReverseTransaction(sctl);
	                        
	                        if((reverseAmountSctl != null) && (reverseChargeSctl != null)) {
		                        log.info("ReverseTransactionProcessor : reverseAmountSctl.id="+reverseAmountSctl.getID() + ", reverseChargeSctl.id="+reverseChargeSctl.getID());
	                            updateMessage(reverseAmountSctl, sctl,entry);
	                            realMsg.getEntries()[i] = entry;
	                            entry.setID(reverseAmountSctl.getID());
	                            entry.setReverseTxnAmount(reverseAmountSctl.getTransactionAmount());
	                            entry.setChargeOnReverseTxnAmount(reverseAmountSctl.getCalculatedCharge());
	                            entry.setReverseChargeAmount(reverseChargeSctl.getTransactionAmount());
	                            entry.setChargeOnReverseChargeAmount(reverseChargeSctl.getCalculatedCharge());
	                            entry.setAmountReversalSCTLID(reverseAmountSctl.getID());
	                            entry.setChargeReversalSCTLID(reverseChargeSctl.getID());
	                        } 
	                        else if(reverseAmountSctl != null){
		                        log.info("ReverseTransactionProcessor : reverseAmountSctl.id="+reverseAmountSctl.getID());
	                            updateMessage(reverseAmountSctl, sctl,entry);
	                            realMsg.getEntries()[i] = entry;
	                            entry.setID(reverseAmountSctl.getID());
	                            entry.setReverseTxnAmount(reverseAmountSctl.getTransactionAmount());
	                            entry.setChargeOnReverseTxnAmount(reverseAmountSctl.getCalculatedCharge());
	                            entry.setAmountReversalSCTLID(reverseAmountSctl.getID());
	                        }
	                        else if(reverseChargeSctl != null){
	                        	log.info("ReverseTransactionProcessor : reverseChargeSctl.id="+reverseChargeSctl.getID());
	                            updateMessage(reverseChargeSctl, sctl,entry);
	                            realMsg.getEntries()[i] = entry;
	                            entry.setID(reverseChargeSctl.getID());
	                            entry.setChargeReversalSCTLID(reverseChargeSctl.getID());
	                            entry.setReverseChargeAmount(reverseChargeSctl.getTransactionAmount());
	                            entry.setChargeOnReverseChargeAmount(reverseChargeSctl.getCalculatedCharge());
	                        }
	                        else {
	                        	log.info("ReverseTransaction request for SCTL --> " + realMsg.getServiceChargeTransactionLogID() + " has some problem in Charge calculation");
	        					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
	        					error.setErrorDescription(MessageText._("The Selected Transaction having some problem. Please try after some time."));
	        					return error;
	                        }
	                    }
		            }
				 	realMsg.setsuccess(CmFinoFIX.Boolean_True);
		            realMsg.settotal(query.getTotal());
			 } else {
				 log.info("Transaction failed because of null SCTLID.");
				 CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
				 error.setErrorDescription(MessageText._("The Selected Transaction having some problem. Please try after some time."));
				 return error;
			 }
		 }
		return realMsg;
	}
	
	private ServiceChargeTransactionLog getReverseTransaction(ServiceChargeTransactionLog sctl) {
		log.info("Generating the Reverse Transaction ...");
		//Check the SCTL status 
		if (((sctl.getParentSCTLID() == null) && (sctl.getTransactionAmount().compareTo(ZERO) > 0)) && 
				((CmFinoFIX.SCTLStatus_Confirmed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Started.equals(sctl.getStatus()) ||
				 CmFinoFIX.SCTLStatus_Distribution_Completed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Failed.equals(sctl.getStatus())))
				 && ((null == sctl.getAmtRevStatus()) || (CmFinoFIX.SCTLStatus_Reverse_Failed.equals(sctl.getAmtRevStatus())))
				 ) {
			
			// Get the Max CT Record for the Transaction.
			long sourcePocketId = 0l;
			CommodityTransfer maxCT = null;
			CommodityTransferDAO ctDao = daoFactory.getCommodityTransferDAO();
			ChargeTxnCommodityTransferMapDAO txnCommodityTransferMapDAO = daoFactory.getTxnTransferMap();
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getID());
			List<ChargeTxnCommodityTransferMap> lstTxnCommodityTransferMaps = txnCommodityTransferMapDAO.get(query);
			if (CollectionUtils.isNotEmpty(lstTxnCommodityTransferMaps)) {
				for (ChargeTxnCommodityTransferMap ctmap: lstTxnCommodityTransferMaps) {
					CommodityTransfer ct = ctDao.getById(ctmap.getCommodityTransferID());
					if (! CmFinoFIX.TransactionUICategory_Charge_Distribution.equals(ct.getUICategory())) {
						if (maxCT == null) {
							maxCT = ct;
						}
						else if (ct.getID().longValue() > maxCT.getID().longValue()) {
							maxCT = ct;
						}
					}

				}
			}
			
			// Get the Source pocket for the Reverse transaction based on the transaction type.		 
			TransactionTypeDAO transactionTypeDao = daoFactory.getTransactionTypeDAO();
			TransactionType transactionType = transactionTypeDao.getById(sctl.getTransactionTypeID());
			if (transactionType != null && 
					((ServiceAndTransactionConstants.TRANSACTION_PURCHASE.equalsIgnoreCase(transactionType.getTransactionName())) || 
					(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY.equalsIgnoreCase(transactionType.getTransactionName()))) ) {
				PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
				List<PartnerServices> lst = psDAO.getPartnerServices(sctl.getDestPartnerID(), sctl.getServiceProviderID(), sctl.getServiceID());
				if (CollectionUtils.isNotEmpty(lst)) {
					PartnerServices ps = lst.get(0);
					sourcePocketId = ps.getPocketBySourcePocket().getID();
				}
			} 
			else {
				sourcePocketId = maxCT.getDestPocketID();
			}
			
			// Get the Service Name for the Reverse Transaction.
			String serviceName = ServiceAndTransactionConstants.SERVICE_WALLET;
			PocketDAO pocketDao = daoFactory.getPocketDAO();
			Pocket sourcePocket = pocketDao.getById(sourcePocketId);
			if (sourcePocket != null && CmFinoFIX.PocketType_BankAccount.equals(sourcePocket.getPocketTemplate().getType())) {
				serviceName = ServiceAndTransactionConstants.SERVICE_BANK;
			}
			

			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(sctl.getDestMDN());
			sc.setDestMDN(sctl.getSourceMDN());
			ChannelCode cc = channelcodeDao.getByChannelSourceApplication(CmFinoFIX.SourceApplication_Web);
			sc.setChannelCodeId(cc.getID());
			sc.setServiceName(serviceName);
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION);
			sc.setTransactionAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
			sc.setReverseTransaction(true);
			sc.setParentSctlId(sctl.getID());
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

	private ServiceChargeTransactionLog getChargeReverseTransaction(ServiceChargeTransactionLog sctl) {
		log.info("Generate SCTL for charge reverse transaction");
		//Check the SCTL status 
		ServiceChargeTransactionLog chargeReversalSctl = null;
		if (((sctl.getParentSCTLID() == null) && (sctl.getCalculatedCharge().compareTo(ZERO) > 0)) && 
				((CmFinoFIX.SCTLStatus_Confirmed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Started.equals(sctl.getStatus()) ||
				 CmFinoFIX.SCTLStatus_Distribution_Completed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Failed.equals(sctl.getStatus())))
				 && ((null == sctl.getChrgRevStatus()) || (CmFinoFIX.SCTLStatus_Reverse_Failed.equals(sctl.getChrgRevStatus())))
				 ) {
			
			PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
			Long chrgRevFundingPocketId = systemParametersService.getLong(CHARGE_REVERSAL_FUNDING_POCKET);
			
			if(chrgRevFundingPocketId == -1){
				log.error("ReverseTransactionProcessor : CHARGE_REVERSAL_FUNDING_POCKET is null, probably this value is not set in SYSTEM_PARAMETERS");
				return null;
			}
			
			Pocket reversalFundingPocket = pocketDao.getById(chrgRevFundingPocketId);
			
			if(reversalFundingPocket == null){
				log.error("ReverseTransactionProcessor : CHARGE_REVERSAL_FUNDING_POCKET is null, No pocket found with id="+chrgRevFundingPocketId);
				return null;
			}
			

			ServiceCharge sc = new ServiceCharge();
			
			sc.setSourceMDN(reversalFundingPocket.getSubscriberMDNByMDNID().getMDN()); //Service partner or Funding Partner MDN.
			sc.setDestMDN(sctl.getSourceMDN());
			ChannelCode cc = channelcodeDao.getByChannelSourceApplication(CmFinoFIX.SourceApplication_Web);
			sc.setChannelCodeId(cc.getID());
			// As Charge is reversed from Reverse funding pocket, the Service here is always 'Wallet' service.
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET); 
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REVERSE_CHARGE);
			sc.setTransactionAmount(sctl.getCalculatedCharge()); // This is charge reversal, so transaction amount is service charge.
			sc.setReverseTransaction(true);
			sc.setParentSctlId(sctl.getID());
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
				chargeReversalSctl = transaction.getServiceChargeTransactionLog();
			} 
		} 
		
		return chargeReversalSctl;
	}
	
	private void updateMessage(ServiceChargeTransactionLog newSctl, ServiceChargeTransactionLog sctl, CGEntries entry) {

		if(newSctl.getChannelCodeID()!=null){
			ChannelCode cc= channelcodeDao.getById(newSctl.getChannelCodeID());
		entry.setAccessMethodText(cc!=null?cc.getChannelName():"");
		}
		if(newSctl.getCommodityTransferID()!=null){
		entry.setCommodityTransferID(newSctl.getCommodityTransferID());
		}
		if(sctl.getSourceMDN() != null){
			entry.setDestMDN(sctl.getSourceMDN());
		}
		if(newSctl.getDestPartnerID()!=null){
		entry.setDestPartnerID(newSctl.getDestPartnerID());
		entry.setDestPartnerTradeName(partnerDao.getById(newSctl.getDestPartnerID()).getTradeName());
		}
		if(newSctl.getFailureReason()!=null){
		entry.setFailureReason(newSctl.getFailureReason());
		}
		if(newSctl.getInvoiceNo()!=null){
		entry.setInvoiceNo(newSctl.getInvoiceNo());
		}
		if(newSctl.getMFSBillerCode()!=null){
		entry.setMFSBillerCode(newSctl.getMFSBillerCode());
		}
		if(newSctl.getOnBeHalfOfMDN()!=null){
		entry.setOnBeHalfOfMDN(newSctl.getOnBeHalfOfMDN());
		}
		if(newSctl.getTransactionTypeID()!=null){
		entry.setTransactionTypeID(newSctl.getTransactionTypeID());
		entry.setTransactionName(ttDao.getById(newSctl.getTransactionTypeID()).getDisplayName());
		}
		if(newSctl.getServiceID()!=null){
			entry.setServiceID(newSctl.getServiceID());
			entry.setServiceName(serviceDao.getById(newSctl.getServiceID()).getDisplayName());
		}
		if(newSctl.getServiceProviderID()!=null){
		entry.setServiceProviderID(newSctl.getServiceProviderID());
		}
		if(sctl.getDestMDN() != null){
		entry.setSourceMDN(sctl.getDestMDN());
		}
		if(newSctl.getSourcePartnerID()!=null){
		entry.setSourcePartnerID(newSctl.getSourcePartnerID());
		entry.setSourcePartnerTradeName(partnerDao.getById(newSctl.getSourcePartnerID()).getTradeName());
		}
		if(newSctl.getStatus()!=null){
		entry.setStatus(newSctl.getStatus());
		entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English, newSctl.getStatus()));
		}
		if(newSctl.getTransactionID()!=null){
		entry.setTransactionID(newSctl.getTransactionID());
		}
		if(newSctl.getTransactionRuleID()!=null){
		entry.setTransactionRuleID(newSctl.getTransactionRuleID());
		}
		entry.setTransactionTime(newSctl.getCreateTime());
		entry.setID(newSctl.getID());
		
		entry.setOriginalTransactionAmount(sctl.getTransactionAmount());
		entry.setOriginalCharge(sctl.getCalculatedCharge());
		entry.setOriginalReferenceID(sctl.getCommodityTransferID());
		entry.setParentSCTLID(newSctl.getParentSCTLID());

	}

}
