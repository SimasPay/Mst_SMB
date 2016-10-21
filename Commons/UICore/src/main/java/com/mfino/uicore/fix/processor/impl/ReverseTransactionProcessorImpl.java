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
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
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
				 List<ServiceChargeTxnLog> results = sctlDao.get(query);
				 
				 if (CollectionUtils.isNotEmpty(results)) {
		                realMsg.allocateEntries(results.size());
		                for (int i = 0; i <results.size(); i++) {
		                	ServiceChargeTxnLog sctl = results.get(i);
	                        CMJSReverseTransaction.CGEntries entry = new CMJSReverseTransaction.CGEntries();
	                        
	                        ServiceChargeTxnLog reverseAmountSctl = getReverseTransaction(sctl);
	                        ServiceChargeTxnLog reverseChargeSctl = getChargeReverseTransaction(sctl);
	                        
	                        if((reverseAmountSctl != null) && (reverseChargeSctl != null)) {
		                        log.info("ReverseTransactionProcessor : reverseAmountSctl.id="+reverseAmountSctl.getId() + ", reverseChargeSctl.id="+reverseChargeSctl.getId());
	                            updateMessage(reverseAmountSctl, sctl,entry);
	                            realMsg.getEntries()[i] = entry;
	                            
	                            entry.setID(reverseAmountSctl.getId().longValue());
	                            entry.setReverseTxnAmount(reverseAmountSctl.getTransactionamount());
	                            entry.setChargeOnReverseTxnAmount(reverseAmountSctl.getCalculatedcharge());
	                            entry.setReverseChargeAmount(reverseChargeSctl.getTransactionamount());
	                            entry.setChargeOnReverseChargeAmount(reverseChargeSctl.getCalculatedcharge());
	                            entry.setAmountReversalSCTLID(reverseAmountSctl.getId().longValue());
	                            entry.setChargeReversalSCTLID(reverseChargeSctl.getId().longValue());
	                        } 
	                        else if(reverseAmountSctl != null){
		                        log.info("ReverseTransactionProcessor : reverseAmountSctl.id="+reverseAmountSctl.getId());
	                            updateMessage(reverseAmountSctl, sctl,entry);
	                            realMsg.getEntries()[i] = entry;
	                            entry.setID(reverseAmountSctl.getId().longValue());
	                            entry.setReverseTxnAmount(reverseAmountSctl.getTransactionamount());
	                            entry.setChargeOnReverseTxnAmount(reverseAmountSctl.getCalculatedcharge());
	                            entry.setAmountReversalSCTLID(reverseAmountSctl.getId().longValue());
	                        }
	                        else if(reverseChargeSctl != null){
	                        	log.info("ReverseTransactionProcessor : reverseChargeSctl.id="+reverseChargeSctl.getId());
	                            updateMessage(reverseChargeSctl, sctl,entry);
	                            realMsg.getEntries()[i] = entry;
	                            entry.setID(reverseChargeSctl.getId().longValue());
	                            entry.setChargeReversalSCTLID(reverseChargeSctl.getId().longValue());
	                            entry.setReverseChargeAmount(reverseChargeSctl.getTransactionamount());
	                            entry.setChargeOnReverseChargeAmount(reverseChargeSctl.getCalculatedcharge());
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
	
	private ServiceChargeTxnLog getReverseTransaction(ServiceChargeTxnLog sctl) {
		log.info("Generating the Reverse Transaction ...");
		//Check the SCTL status 
		if (((sctl.getParentsctlid() == null) && (sctl.getTransactionamount().compareTo(ZERO) > 0)) && 
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
			ChannelCode cc = channelcodeDao.getByChannelSourceApplication(CmFinoFIX.SourceApplication_Web);
			sc.setChannelCodeId(cc.getId().longValue());
			sc.setServiceName(serviceName);
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REVERSE_TRANSACTION);
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

	private ServiceChargeTxnLog getChargeReverseTransaction(ServiceChargeTxnLog sctl) {
		log.info("Generate SCTL for charge reverse transaction");
		//Check the SCTL status 
		ServiceChargeTxnLog chargeReversalSctl = null;
		if (((sctl.getParentsctlid() == null) && (sctl.getCalculatedcharge().compareTo(ZERO) > 0)) && 
				((CmFinoFIX.SCTLStatus_Confirmed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Started.equals(sctl.getStatus()) ||
				 CmFinoFIX.SCTLStatus_Distribution_Completed.equals(sctl.getStatus()) || CmFinoFIX.SCTLStatus_Distribution_Failed.equals(sctl.getStatus())))
				 && ((null == sctl.getChrgrevstatus()) || (CmFinoFIX.SCTLStatus_Reverse_Failed.equals(sctl.getChrgrevstatus())))
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
			
			sc.setSourceMDN(reversalFundingPocket.getSubscriberMdn().getMdn()); //Service partner or Funding Partner MDN.
			sc.setDestMDN(sctl.getSourcemdn());
			ChannelCode cc = channelcodeDao.getByChannelSourceApplication(CmFinoFIX.SourceApplication_Web);
			sc.setChannelCodeId(cc.getId().longValue());
			// As Charge is reversed from Reverse funding pocket, the Service here is always 'Wallet' service.
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET); 
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REVERSE_CHARGE);
			sc.setTransactionAmount(sctl.getCalculatedcharge()); // This is charge reversal, so transaction amount is service charge.
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
				chargeReversalSctl = transaction.getServiceChargeTransactionLog();
			} 
		} 
		
		return chargeReversalSctl;
	}
	
	private void updateMessage(ServiceChargeTxnLog newSctl, ServiceChargeTxnLog sctl, CGEntries entry) {

		if(newSctl.getChannelcodeid()!=null){
			ChannelCode cc= channelcodeDao.getById(newSctl.getChannelcodeid().longValue());
		entry.setAccessMethodText(cc!=null?cc.getChannelname():"");
		}
		if(newSctl.getCommoditytransferid()!=null){
		entry.setCommodityTransferID(newSctl.getCommoditytransferid().longValue());
		}
		if(sctl.getSourcemdn() != null){
			entry.setDestMDN(sctl.getSourcemdn());
		}
		if(newSctl.getDestpartnerid()!=null){
		entry.setDestPartnerID(newSctl.getDestpartnerid().longValue());
		entry.setDestPartnerTradeName(partnerDao.getById(newSctl.getDestpartnerid().longValue()).getTradename());
		}
		if(newSctl.getFailurereason()!=null){
		entry.setFailureReason(newSctl.getFailurereason());
		}
		if(newSctl.getInvoiceno()!=null){
		entry.setInvoiceNo(newSctl.getInvoiceno());
		}
		if(newSctl.getMfsbillercode()!=null){
		entry.setMFSBillerCode(newSctl.getMfsbillercode());
		}
		if(newSctl.getOnbehalfofmdn()!=null){
		entry.setOnBeHalfOfMDN(newSctl.getOnbehalfofmdn());
		}
		if(newSctl.getTransactiontypeid()!=null){
		entry.setTransactionTypeID(newSctl.getTransactiontypeid().longValue());
		
		entry.setTransactionName(ttDao.getById(newSctl.getTransactiontypeid().longValue()).getDisplayname());
		}
		if(newSctl.getServiceid()!=null){
			entry.setServiceID(newSctl.getServiceid().longValue());
			entry.setServiceName(serviceDao.getById(newSctl.getServiceid().longValue()).getDisplayname());
		}
		if(newSctl.getServiceproviderid()!=null){
		entry.setServiceProviderID(newSctl.getServiceproviderid().longValue());
		}
		if(sctl.getDestmdn() != null){
		entry.setSourceMDN(sctl.getDestmdn());
		}
		if(newSctl.getDestpartnerid()!=null){
		entry.setSourcePartnerID(newSctl.getDestpartnerid().longValue());
		entry.setSourcePartnerTradeName(partnerDao.getById(newSctl.getDestpartnerid().longValue()).getTradename());
		}
		if(newSctl.getStatus()!=0){
			entry.setStatus(newSctl.getStatus());
			entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SCTLStatus, 
					CmFinoFIX.Language_English, newSctl.getStatus()));
		}
		if(newSctl.getTransactionid()!=null){
		entry.setTransactionID(newSctl.getTransactionid().longValue());
		}
		if(newSctl.getTransactionruleid()!=null){
		entry.setTransactionRuleID(newSctl.getTransactionruleid().longValue());
		}
		entry.setTransactionTime(newSctl.getCreatetime());
		entry.setID(newSctl.getId().longValue());
		
		entry.setOriginalTransactionAmount(sctl.getTransactionamount());
		entry.setOriginalCharge(sctl.getCalculatedcharge());
		entry.setOriginalReferenceID(sctl.getCommoditytransferid().longValue());
		entry.setParentSCTLID(newSctl.getParentsctlid().longValue());

	}

}
