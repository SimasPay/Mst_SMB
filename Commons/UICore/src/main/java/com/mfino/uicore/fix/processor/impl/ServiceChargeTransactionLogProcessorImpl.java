package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.ServiceTransactionDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.Adjustments;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.InterbankTransfers;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSServiceChargeTransactions;
import com.mfino.fix.CmFinoFIX.CMJSServiceChargeTransactions.CGEntries;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.EnumTextService;
import com.mfino.service.IBTService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServiceChargeTransactionLogProcessor;

@org.springframework.stereotype.Service("ServiceChargeTransactionLogProcessorImpl")
public class ServiceChargeTransactionLogProcessorImpl extends BaseFixProcessor implements ServiceChargeTransactionLogProcessor{
	
	private DAOFactory daoFactory = DAOFactory.getInstance();
	private ServiceChargeTransactionLogDAO sctlDao =daoFactory.getServiceChargeTransactionLogDAO(); 
	private ChannelCodeDAO channelcodeDao = daoFactory.getChannelCodeDao();
	private PartnerDAO partnerDao = daoFactory.getPartnerDAO();
	private TransactionTypeDAO ttDao = daoFactory.getTransactionTypeDAO();
	private ServiceDAO serviceDao = daoFactory.getServiceDAO();
	private PendingCommodityTransferDAO pctDao = daoFactory.getPendingCommodityTransferDAO();
	private CommodityTransferDAO ctDao = daoFactory.getCommodityTransferDAO();
	private IntegrationSummaryDao integrationSummaryDao = daoFactory.getIntegrationSummaryDao();
	private ChargeTxnCommodityTransferMapDAO ctmapDao = daoFactory.getTxnTransferMap();
	private BillPaymentsDAO billPaymentsDao = daoFactory.getBillPaymentDAO();
	private final Integer BACTH_SIZE = 10000;


	//private int maxNoOfDaysToReverseTxn;

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("IBTServiceImpl")
	private IBTService ibtService;	

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSServiceChargeTransactions realMsg = (CMJSServiceChargeTransactions) msg;

		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
			Partner partner = userService.getPartner();
			if(partner!=null){
				query.setSourceDestPartnerID(partner.getId().longValue());
			}
			if(StringUtils.isNotBlank(realMsg.getBankRetrievalReferenceNumber())){
				Long Id = getSCTLID(realMsg.getBankRetrievalReferenceNumber()) ;
				if(Id!=null){
					query.setId(Id);
				}else{
					realMsg.setsuccess(CmFinoFIX.Boolean_True);
					realMsg.settotal(0);
					return realMsg;
				}
			}else{
				if(realMsg.getIDSearch()!=null){
					query.setId(realMsg.getIDSearch());
				}
				if(realMsg.getSourceApplicationSearch()!=null){
					query.setSourceChannelApplication(realMsg.getSourceApplicationSearch());				 
				}
				if(realMsg.getTransactionIdSearch()!=null){
					query.setTransationID(realMsg.getTransactionIdSearch());
				}
				if(realMsg.getTransferID()!=null){
					query.setTransferID(realMsg.getTransferID());
				}
				if(realMsg.getTransactionTypeID() != null){

					query.setTransactionTypeID(realMsg.getTransactionTypeID());
				}
				
				if(realMsg.getServiceID() != null){

					query.setServiceID(realMsg.getServiceID());
				}
				
				
			
				
				if(StringUtils.isNotBlank(realMsg.getSourcePartnerCode())){
					Partner sourcePartner = partnerDao.getPartnerByPartnerCode(realMsg.getSourcePartnerCode());
					if(sourcePartner==null){
						realMsg.setsuccess(CmFinoFIX.Boolean_True);
						realMsg.settotal(0);
						return realMsg;
					}
					query.setSourcePartnerID(sourcePartner.getId().longValue());
				}
				if(StringUtils.isNotBlank(realMsg.getDestPartnerCode())){
					Partner destPartner = partnerDao.getPartnerByPartnerCode(realMsg.getDestPartnerCode());
					if(destPartner==null){
						realMsg.setsuccess(CmFinoFIX.Boolean_True);
						realMsg.settotal(0);
						return realMsg;
					}
					query.setDestPartnerID(destPartner.getId().longValue());
				}
				if(StringUtils.isNotBlank(realMsg.getMFSBillerCode())){
					query.setBillerCode(realMsg.getMFSBillerCode());
				}
				if(realMsg.getSourceMDN()!=null){
					query.setSourceMdn(realMsg.getSourceMDN());
				}
				if(realMsg.getDestMDN()!=null){
					query.setDestMdn(realMsg.getDestMDN());
				}
				if(realMsg.getStatus()!=null){
					query.setStatus(realMsg.getStatus());
				}
				if(realMsg.getStartDateSearch()!=null){
					query.setCreateTimeGE(realMsg.getStartDateSearch());
				}
				if(realMsg.getEndDateSearch()!=null){
					query.setCreateTimeLT(realMsg.getEndDateSearch());
				}
				if(realMsg.getParentSCTLID()!=null) {
					query.setParentSCTLID(realMsg.getParentSCTLID());
				}
				if(realMsg.getAdjustmentStatus()!=null) {
					query.setAdjustmentStatus(realMsg.getAdjustmentStatus());
				}
			}
			query.setStart(realMsg.getstart());
			query.setLimit(realMsg.getlimit());
			query.setIDOrdered(true); 
			
			List<ServiceChargeTxnLog> results = sctlDao.get(query);
			List<Long> sctlList = getSctlList(results);

			List<IntegrationSummary> integrationSummaryLst ;//= integrationSummaryDao.getBySctlList(sctlList);
			Map<Long,IntegrationSummary> sctlIsMap = null ;//= getSctlIsMap(integrationSummaryLst);

			List<BillPayments> billPaymentsLst ;//= billPaymentsDao.getBySctlList(sctlList);
			Map<Long,BillPayments> sctlBpMap = null ;//= getSctlBpMap(billPaymentsLst);
			
			int maxNoOfDaysToReverseTxn = systemParametersService.getInteger(SystemParameterKeys.MAX_NO_OF_DAYS_TO_REVERSE_TXN);
			int startIndex = 0;

			if (results != null) {
				realMsg.allocateEntries(results.size());
				for (int i = 0; i <results.size(); i++) {
					if( i%BACTH_SIZE == 0 ) {
						int endIndex = startIndex+BACTH_SIZE < results.size() ? startIndex+BACTH_SIZE : results.size();
						integrationSummaryLst = integrationSummaryDao.getBySctlList(sctlList.subList(startIndex, endIndex ));
						sctlIsMap = getSctlIsMap(integrationSummaryLst);

						billPaymentsLst = billPaymentsDao.getBySctlList(sctlList.subList(startIndex, endIndex ));
						sctlBpMap = getSctlBpMap(billPaymentsLst);
						startIndex = startIndex + BACTH_SIZE;
					}
					ServiceChargeTxnLog sctl = results.get(i);
					CMJSServiceChargeTransactions.CGEntries entry = new CMJSServiceChargeTransactions.CGEntries();
					updateMessage(sctl,entry,realMsg, maxNoOfDaysToReverseTxn,sctlIsMap,sctlBpMap);
					realMsg.getEntries()[i] = entry;
				}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());

		}

		return realMsg;
	}

	private Map<Long, BillPayments> getSctlBpMap(List<BillPayments> billPaymentsLst) {
		Map<Long, BillPayments> sctlBpMap = new HashMap<Long, BillPayments>();
		for(BillPayments bp : billPaymentsLst) {
			sctlBpMap.put(bp.getId().longValue(), bp);
		}
		return sctlBpMap;
	}

	private Map<Long, IntegrationSummary> getSctlIsMap(List<IntegrationSummary> integrationSummaryLst) {
		Map<Long, IntegrationSummary> sctlIsMap = new HashMap<Long, IntegrationSummary>();
		for(IntegrationSummary is : integrationSummaryLst) {
			sctlIsMap.put(is.getId().longValue(), is);
		}
		return sctlIsMap;
	}

	private List<Long> getSctlList(List<ServiceChargeTxnLog> results) {
		List<Long> sctlList = new ArrayList<Long>();
		for(ServiceChargeTxnLog sctl : results) {
			sctlList.add(sctl.getId().longValue());
		}
		return sctlList;
	}

	private Long getSCTLID(String bankRetrievalReferenceNumber) {
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setBankRRN(bankRetrievalReferenceNumber);
		DAOFactory daoFactory =  DAOFactory.getInstance();
		CommodityTransferDAO ctDao =daoFactory.getCommodityTransferDAO();
		List<CommodityTransfer> ct=null;
		List<PendingCommodityTransfer> pct = null;
		Long ctId = null;
		try {
			ct=ctDao.get(query);
		} catch (Exception e) {
			log.error("Exception",e);
		}
		if(ct==null||ct.isEmpty()){
			PendingCommodityTransferDAO pctDao = daoFactory.getPendingCommodityTransferDAO();
			try {
				pct= pctDao.get(query);
			} catch (Exception e) {
				log.error("Exception",e);
			}
			if(pct!=null&&!pct.isEmpty()){
				ctId = pct.get(0).getId().longValue();
			}
		}else{
			ctId = ct.get(0).getId().longValue();
		}
		if(ctId!=null){
			ChargeTxnCommodityTransferMapDAO cTxnCommodityTransferMapDAO = daoFactory.getTxnTransferMap();
			ChargeTxnCommodityTransferMapQuery query2 = new ChargeTxnCommodityTransferMapQuery();
			query2.setCommodityTransferID(ctId);
			List<ChargetxnTransferMap> ctxnMap = cTxnCommodityTransferMapDAO.get(query2);
			if(ctxnMap!=null&&!ctxnMap.isEmpty()){
				return ctxnMap.get(0).getId().longValue();
			}
		}
		return null;
	}

	private void updateMessage(ServiceChargeTxnLog sctl, CGEntries entry, CMJSServiceChargeTransactions realMsg, int maxNoOfDaysToReverseTxn, Map<Long, IntegrationSummary> sctlIsMap, Map<Long, BillPayments> sctlBpMap) {
		TransactionType transactionType=null;
		Service service=null;
		if(sctl.getCalculatedcharge()!=null){
			entry.setCalculatedCharge(sctl.getCalculatedcharge());
		}
		if(sctl.getChannelcodeid()!=null){
			ChannelCode cc= channelcodeDao.getById(sctl.getChannelcodeid().longValue());
			entry.setAccessMethodText(cc!=null?cc.getChannelname():"");
		}
		long ctid = 0;
		CommodityTransfer ct = null;
		if(sctl.getCommoditytransferid() == null){
			List<Long> lstCTIds = ctmapDao.geTransferIdsBySCTLId(sctl.getId().longValue());
			if (CollectionUtils.isNotEmpty(lstCTIds)) {
				ctid = lstCTIds.get(0);
			}
		}
		else {
			ctid = sctl.getCommoditytransferid().longValue();
		}
		entry.setCommodityTransferID(ctid);
		PendingCommodityTransfer pct = pctDao.getById(ctid);
	    if(pct != null)
	    {
	    	if(pct.getOperatorresponsecode() != null){
	    		entry.setOperatorResponseCode(pct.getOperatorresponsecode().intValue());
	    	}else{
	    		if(pct.getBankrejectreason() != null){
	    			entry.setOperatorResponseCode(Integer.valueOf(pct.getBankrejectreason()));	
	    		}
	    	}
	    	entry.setSourceAccountNumber(pct.getSourcecardpan());
	    }
	    else
	    {		    	
	    	ct = ctDao.getById(ctid);
	    	if(ct != null)
		    {
		    	if(ct.getOperatorresponsecode() != null){
		    		entry.setOperatorResponseCode(ct.getOperatorresponsecode().intValue());
		    	}else{
		    		if(ct.getBankrejectreason() != null){
		    			entry.setOperatorResponseCode(Integer.valueOf(ct.getBankrejectreason()));	
		    		}
		    	}
		    	entry.setSourceAccountNumber(ct.getSourcecardpan());
		    }
	    }
		if(sctl.getDestmdn()!=null){
			entry.setDestMDN(sctl.getDestmdn());
		}
		if(sctl.getDestpartnerid()!=null){
			entry.setDestPartnerID(sctl.getDestpartnerid().longValue());
			entry.setDestPartnerCode(partnerDao.getById(sctl.getDestpartnerid().longValue()).getPartnercode());
		}
		if(sctl.getFailurereason()!=null){
			entry.setFailureReason(sctl.getFailurereason());
		}
		if(sctl.getInvoiceno()!=null){
			entry.setInvoiceNo(sctl.getInvoiceno());
		}
		if(sctl.getMfsbillercode()!=null){
			entry.setMFSBillerCode(sctl.getMfsbillercode());
		}
		if(sctl.getOnbehalfofmdn()!=null){
			entry.setOnBeHalfOfMDN(sctl.getOnbehalfofmdn());
		}
		if(sctl.getTransactiontypeid()!=null){
			entry.setTransactionTypeID(sctl.getTransactiontypeid().longValue());
			transactionType = ttDao.getById(sctl.getTransactiontypeid().longValue());
			entry.setTransactionName(transactionType.getDisplayname());
			
			/*if(sctl.getServiceTypeID()!=null){
				entry.setServiceTypeID(sctl.getServiceTypeID());
				service = serviceDao.getById(sctl.getServiceTypeID());
				entry.setServiceName(service.getDisplayName());	}
			*/

			if("InterBankTransfer".equals(transactionType.getTransactionname()))
			{
				InterbankTransfers ibt = ibtService.getBySctlId(sctl.getId().longValue());
				if (ibt != null) {
					entry.setDestBankCode(ibt.getDestbankcode());
				}
			}			
		}
		if(sctl.getServiceid()!=null){
			entry.setServiceID(sctl.getServiceid().longValue());
			service = serviceDao.getById(sctl.getServiceid().longValue());
			entry.setServiceName(service.getDisplayname());
		}
		if(sctl.getServiceproviderid()!=null){
			entry.setServiceProviderID(sctl.getServiceproviderid().longValue());
		}
		if(sctl.getSourcemdn()!=null){
			entry.setSourceMDN(sctl.getSourcemdn());
		}
		if(sctl.getSourcepartnerid()!=null){
			entry.setSourcePartnerID(sctl.getSourcepartnerid().longValue());
			entry.setSourcePartnerCode(partnerDao.getById(sctl.getSourcepartnerid().longValue()).getPartnercode());
		}
		if(sctl.getStatus()!=0){
			entry.setStatus(Integer.valueOf(Long.valueOf(sctl.getStatus()).intValue()));
			entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English, sctl.getStatus()));
		}
		if(sctl.getAdjustmentses()!= null){
			Set<Adjustments> adjustments = sctl.getAdjustmentses();
			Iterator<Adjustments> iterator = adjustments.iterator();
			Adjustments lastAdjustment = null;
			while(iterator.hasNext()) {
				Adjustments adjustment = iterator.next();
				if(lastAdjustment == null) {
					lastAdjustment = adjustment;
				} else if(Long.valueOf(adjustment.getAdjustmentstatus()).compareTo(lastAdjustment.getAdjustmentstatus()) < 1) {
					lastAdjustment = adjustment;
				}
			}
			if(lastAdjustment != null) {
				entry.setAdjustmentsLogID(lastAdjustment.getId().longValue());
				entry.setAdjustmentStatus(Integer.valueOf(Long.valueOf(lastAdjustment.getAdjustmentstatus()).intValue()));
				entry.setAdjustmentStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_AdjustmentStatus, CmFinoFIX.Language_English, lastAdjustment.getAdjustmentstatus()));
			}			
		}
		if(sctl.getChargemode()!=null){
			entry.setChargeMode(sctl.getChargemode().intValue());
			entry.setChargeModeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ChargeMode, CmFinoFIX.Language_English, sctl.getChargemode()));
		}
		if(sctl.getTransactionamount()!=null){
			entry.setTransactionAmount(sctl.getTransactionamount());
		}
		if(sctl.getTransactionid()!=null){
			entry.setTransactionID(sctl.getTransactionid().longValue());
		}
		if(sctl.getServiceid()!=null){
			entry.setServiceID(sctl.getServiceid().longValue());
		}
		
		if(sctl.getTransactionruleid()!=null){
			entry.setTransactionRuleID(sctl.getTransactionruleid().longValue());
		}
		entry.setTransactionTime(sctl.getCreatetime());
		entry.setID(sctl.getId().longValue());
		if (sctl.getParentsctlid() != null) {
			entry.setParentSCTLID(sctl.getParentsctlid().longValue());
		}
		if (StringUtils.isNotBlank(sctl.getReversalreason())) {
			entry.setReversalReason(sctl.getReversalreason());
		}
		entry.setIsChargeDistributed(Boolean.valueOf(Short.toString(sctl.getIschargedistributed())));
		if (sctl.getIstransactionreversed() != null) {
			entry.setIsTransactionReversed(Boolean.valueOf(Short.toString(sctl.getIstransactionreversed())));
		} else {
			entry.setIsTransactionReversed(CmFinoFIX.Boolean_False);
		}
		/*if(StringUtils.isNotBlank(realMsg.getBankRetrievalReferenceNumber()))
	    	entry.setBankRetrievalReferenceNumber(realMsg.getBankRetrievalReferenceNumber());
	    else*/
		if(sctl.getIntegrationtransactionid()!=null)
			entry.setBankRetrievalReferenceNumber(String.valueOf(sctl.getIntegrationtransactionid()));

		entry.setAmtRevStatus(sctl.getAmtrevstatus().intValue());
		entry.setChrgRevStatus(sctl.getChrgrevstatus().intValue());

		if(sctl.getAmtrevstatus() != null){
			entry.setAmtRevStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English, sctl.getAmtrevstatus()));	
		}
		if(sctl.getInfo1()!=null){
			entry.setInfo1(sctl.getInfo1());
		}

		if(sctl.getChrgrevstatus() != null){
			entry.setChrgRevStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English, sctl.getChrgrevstatus()));
		}
		if(sctl.getDescription() != null){
			entry.setDescription(sctl.getDescription());
		}
		

//	    if(sctl.getCommodityTransferID() != null){
//	    	CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
//	    	CommodityTransfer ct = ctDao.getById(sctl.getCommodityTransferID());
//	    	if(ct != null){
//	    		entry.setSourceAccountNumber(ct.getSourceCardPAN());
//	    	}
//	    }		
		
		setAdditionanInfo(entry,sctl,transactionType,service, sctlBpMap, ct);
		setIntegrationSummaryInfo(entry,sctl, transactionType, sctlIsMap);
		entry.setIsReverseAllowed(checkIsTxnReverseAllowed(sctl, transactionType, maxNoOfDaysToReverseTxn));
	}

	/**
	 * Check whether the given SCTL is allowed for Reverse or not based on the Service and Transaction Type.
	 * @param sctl
	 * @return
	 */
	private boolean checkServiceTxnIsReverseAllowed(ServiceChargeTxnLog sctl) {
		ServiceTransactionDAO stDAO = daoFactory.getServiceTransactionDAO();
		return stDAO.isReverseAllowed(sctl.getServiceid().longValue(), sctl.getTransactiontypeid().longValue());
	}

	// Check whether the Reverse of transaction is allowed or not.
	private boolean checkIsTxnReverseAllowed(ServiceChargeTxnLog sctl, TransactionType transactionType, int maxNoOfDaysToReverseTxn) {
		boolean isReverseAllowed = false;

		Timestamp txnTime = sctl.getCreatetime();
		Timestamp currentTime = new Timestamp();

		long days_old = (currentTime.getTime() - txnTime.getTime()) / (24 * 60  *60 * 1000);
		if ( (days_old <= maxNoOfDaysToReverseTxn) && 
				((sctl.getParentsctlid() == null) &&
						(sctl.getTransactionamount().compareTo(BigDecimal.ZERO) > 0) && 
						(CmFinoFIX.SCTLStatus_Confirmed.intValue() == Long.valueOf(sctl.getStatus()).intValue() ||
						CmFinoFIX.SCTLStatus_Distribution_Started.intValue() == Long.valueOf(sctl.getStatus()).intValue() ||
						CmFinoFIX.SCTLStatus_Distribution_Completed.intValue() == Long.valueOf(sctl.getStatus()).intValue() ||
						CmFinoFIX.SCTLStatus_Distribution_Failed.intValue() == Long.valueOf(sctl.getStatus()).intValue())
						) && (checkServiceTxnIsReverseAllowed(sctl)) &&
						((sctl.getAmtrevstatus() == null || CmFinoFIX.SCTLStatus_Reverse_Failed.equals(sctl.getAmtrevstatus())) ||
								((sctl.getCalculatedcharge().compareTo(BigDecimal.ZERO) > 0) && 
										(sctl.getChrgrevstatus() == null || CmFinoFIX.SCTLStatus_Reverse_Failed.equals(sctl.getChrgrevstatus()))
										)		 
								)
				) {
			isReverseAllowed = true;
		}

		// Check if the UnRegisteredTranfser, The cashout is completed or not.
		if (isReverseAllowed &&
				(ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER.equalsIgnoreCase(transactionType.getTransactionname()) ||
						ServiceAndTransactionConstants.TRANSACTION_TRANSFER_UNREGISTERED.equalsIgnoreCase(transactionType.getTransactionname()))) {
			UnRegisteredTxnInfoDAO urtiDAO = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
			UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
			urtiQuery.setTransferSctlId(sctl.getId().longValue());
			List<UnregisteredTxnInfo> urtiList = urtiDAO.get(urtiQuery);
			if (CollectionUtils.isNotEmpty(urtiList)) {
				UnregisteredTxnInfo urti = urtiList.get(0);
				if (CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED.equals(urti.getUnregisteredtxnstatus()) || 
						CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(urti.getUnregisteredtxnstatus())) {
					isReverseAllowed = true;
				}
				else {
					isReverseAllowed = false;
				}
			}
		}

		return isReverseAllowed;
	}

	private void setAdditionanInfo(CGEntries entry, ServiceChargeTxnLog sctl, TransactionType transactionType, Service service, 
			Map<Long, BillPayments> sctlBpMap, CommodityTransfer ct) {
		BillPayments billPayment = sctlBpMap.get(sctl.getId());
		entry.setAdditionalInfo("");
		if(billPayment!=null) {
			String invoiceNumber = billPayment.getInvoicenumber();
			String inRespCode = billPayment.getInresponsecode();
			entry.setAdditionalInfo(invoiceNumber);

			if (CmFinoFIX.SCTLStatus_Failed.equals(sctl.getStatus()) && 
					ServiceAndTransactionConstants.TRANSACTION_INTER_EMONEY_TRANSFER.equals(transactionType.getTransactionname())) {
				entry.setFailureReason(inRespCode);//
			}
		}
		else if(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION.equals(transactionType.getTransactionname())){
			entry.setAdditionalInfo(sctl.getOnbehalfofmdn());
		}
		else {
			if(ct!=null&&CmFinoFIX.PocketType_BankAccount.equals(ct.getDestpockettype())&&ct.getDestcardpan()!=null){
				entry.setAdditionalInfo(ct.getDestcardpan());
			}
			if (ct != null && StringUtils.isBlank(entry.getBankRetrievalReferenceNumber()) ){
				entry.setBankRetrievalReferenceNumber(ct.getBankretrievalreferencenumber());
			}

		}

	}

//	private boolean isBillpaymentTxn(TransactionType transactionType) {
//		String txnName = transactionType.getTransactionName();
//		if(txnName.equals(ServiceAndTransactionConstants.TRANSACTION_QR_PAYMENT) || txnName.equals(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY) || 
//				txnName.equals(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE)) {
//			return true;
//		}
//		return false;
//	}
//	
//	private boolean isDataExistInIntegrationSummary(TransactionType transactionType) {
//		String txnName = transactionType.getTransactionName();
//		if(txnName.equals(ServiceAndTransactionConstants.TRANSACTION_QR_PAYMENT) || txnName.equals(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY) || 
//				txnName.equals(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE) || txnName.equals(ServiceAndTransactionConstants.TRANSACTION_CHARGE_SETTLEMENT)) {
//			return true;
//		}
//		return false;
//	}

	private void setIntegrationSummaryInfo(CGEntries entry, ServiceChargeTxnLog sctl, TransactionType transactionType, Map<Long, IntegrationSummary> sctlIsMap){
		IntegrationSummary integrationSummary = sctlIsMap.get(sctl.getId());
		if(integrationSummary!=null){
			entry.setIntegrationType(integrationSummary.getIntegrationtype());
			entry.setReconcilationID1(integrationSummary.getReconcilationid1());
			entry.setReconcilationID2(integrationSummary.getReconcilationid2());
			entry.setReconcilationID3(integrationSummary.getReconcilationid3());			
		}		
	}

	private boolean isTransfer(TransactionType transactionType) {
		// TODO Auto-generated method stub
		return transactionType.getTransactionname().equals(ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER)
				||transactionType.getTransactionname().equals(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
	}
}
