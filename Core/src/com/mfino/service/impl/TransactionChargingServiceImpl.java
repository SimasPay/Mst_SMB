/**
 * 
 */
package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.RuleKeyDAO;
import com.mfino.dao.ServiceAuditDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.TransactionAmountDistributionLogDAO;
import com.mfino.dao.TransactionChargeDAO;
import com.mfino.dao.TransactionChargeLogDAO;
import com.mfino.dao.TransactionRuleAddnInfoDAO;
import com.mfino.dao.TransactionRuleDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.RuleKeyQuery;
import com.mfino.dao.query.TransactionChargeQuery;
import com.mfino.dao.query.TransactionRuleAddnInfoQuery;
import com.mfino.dao.query.TransactionRuleQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.ChargePricing;
import com.mfino.domain.ChargeType;
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.DistributionChainLvl;
import com.mfino.domain.DistributionChainTemp;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.RuleKey;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceAudit;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SharePartner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionRule;
import com.mfino.domain.TransactionType;
import com.mfino.domain.TxnAmountDstrbLog;
import com.mfino.domain.TxnRuleAddnInfo;
import com.mfino.exceptions.DistributionException;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.MoneyService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TaxService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionIdentifierService;
import com.mfino.util.ConfigurationUtil;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * @author Bala Sunku
 *
 */

@org.springframework.stereotype.Service("TransactionChargingServiceImpl")
public class TransactionChargingServiceImpl implements TransactionChargingService{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String TYPE = "Type";
	private static final String KYC_LEVEL = "KYCLevel";
	private static final String PARTNER_ID = "PartnerId";
	private static final String GROUP = "GROUP";
	private static Map<Long,Object> ruleWeight = new HashMap<Long,Object>();

	@Autowired
	@Qualifier("MoneyServiceImpl")
	private MoneyService moneyService;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	public SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("TaxServiceImpl")
	public TaxService taxService;

	@Autowired
	@Qualifier("TransactionIdentifierServiceImpl")
	private TransactionIdentifierService transactionIdentifierService;
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	/*
	 * Auxiliary class used by distributeTransactionAmount() method to uniquely identify a subscriber or partner.
	 * PartnerID can be equivalent to a SubscriberID yet they should be handled differently.
	 * Here id represents PartnerID or SubscriberID and isPartner says if he is a partner or subscriber.
	 * Two TCShareHolders are same if and only if their id is same and if both of them are partners or both of them are subscribers
	 */
	public static class TransactionChargeShareHolder
	{
		private Long id;
		private boolean isPartner;
				
		public TransactionChargeShareHolder(Long transactionChargeShareHolderId, boolean isPartner)
		{
			this.id = transactionChargeShareHolderId;
			this.isPartner = isPartner;
		}
		public TransactionChargeShareHolder(Long transactionChargeShareHolderId)
		{
			this.id = transactionChargeShareHolderId;
			this.isPartner = true;
		}
		
		public Long getTransactionChargeShareHolderId()
		{
			return id;
		}
		
		public void setTransacionChargeShareHolderId(Long transactionChargeShareHolderId)
		{
			this.id = transactionChargeShareHolderId;
		}
		
		public boolean getIsPartner()
		{
			return isPartner;
		}
		
		public void setIsPartner( boolean isPartner)
		{
			this.isPartner = isPartner;
		}
		
		public boolean equals(Object o)
		{
			if(o instanceof TransactionChargeShareHolder && ((TransactionChargeShareHolder) o).getTransactionChargeShareHolderId().equals(this.id) && ((TransactionChargeShareHolder) o).getIsPartner() == this.isPartner)
			{
				return true;
			}
			return false;
		}
	}

	
	public TransactionChargingServiceImpl() {
	}
	/**
	 * Return the Business Partner for the given Biller code.
	 * @param billerCode
	 * @return
	 */
	public Partner getPartnerByBillerCode(String billerCode) {
		MFSBillerPartnerDAO dao = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		Partner p = null;
		List<Partner> lst = dao.getPartnersByBillerCode(billerCode);
		
		//TODO Need to write the logic to get the Best partner
		
		if (CollectionUtils.isNotEmpty(lst)) {
			p = lst.get(0);
		}
		
		return p;
	}
	
	/**
	 * Returns the ServiceProvider by name
	 * @param serviceProviderName
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)	
	public Partner getServiceProvider(String serviceProviderName) {
		PartnerDAO spDAO = DAOFactory.getInstance().getPartnerDAO();
		if (StringUtils.isNotBlank(serviceProviderName)) {
			return spDAO.getPartnerByTradeName(serviceProviderName);
		} else {
			return spDAO.getServiceProvider();
		}
	}
	/**
	 * Return serviceProviderId by getting serviceprovider name and then ID
	 * @param serviceProviderName
	 * @return
	 * @throws InvalidServiceException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)	
	public Long getServiceProviderId(String serviceProviderName) throws InvalidServiceException {
		Long result = null;
		Partner partner = getServiceProvider(serviceProviderName);
		if (partner != null) {
			result = partner.getId().longValue();
		} else {
			log.info("-----------------------> Service Provider Not found");
			throw new InvalidServiceException(MessageText._("Service Provider Not found"));
		}
		return result; 
	}
	
	/**
	 * Returns the Service by name
	 * @param serviceName
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Service getService(String serviceName) {
		ServiceDAO sDAO = DAOFactory.getInstance().getServiceDAO();
		return sDAO.getServiceByName(serviceName);
	}
	
	public Long getServiceId(String serviceName) throws InvalidServiceException {
		Long result = null;
		Service service = getService(serviceName);
		if (service != null) {
			result = service.getId().longValue();
		} else {
			log.info("---------------------------> Service Not found--"+serviceName);
			throw new InvalidServiceException(MessageText._("Service Not found --"+serviceName));
		}
		return result;
	}
	
	/**
	 * Returns the TransactionType by name
	 * @param transactionName
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public TransactionType getTransactionType(String transactionName) {
		TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
		return ttDAO.getTransactionTypeByName(transactionName);
	}
	
	public Long getTransactionTypeId(String transactionName) throws InvalidServiceException {
		Long result = null;
		TransactionType tt = getTransactionType(transactionName);
		if (tt != null) {
			result = tt.getId().longValue();
		} else {
			log.info("-----------------------> Transaction Type Not Found");
			throw new InvalidServiceException(MessageText._("Transaction Type Not Found"));
		}
		return result;
	}
	
	/**
	 * Returns the Map object with MDN type, KYC levelId and Partner Id details for the given MDN
	 * @param MDN
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Map<String, Long> getMDNDetails(String MDN) {
		Map<String, Long> result = new HashMap<String, Long>();
		
		if (StringUtils.isNotBlank(MDN)) {
			   SubscriberMDNDAO sMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			   SubscriberMdn smdn = sMDNDAO.getByMDN(MDN);
			   if (smdn != null) {
			    Subscriber subscriber = smdn.getSubscriber();
			    result.put(TYPE, subscriber.getType().longValue());
			    result.put(KYC_LEVEL, subscriber.getKycLevel().getKyclevel());
			    SubscriberGroupDao sgDao = DAOFactory.getInstance().getSubscriberGroupDao();
			    SubscriberGroups subscriberGroup = sgDao.getBySubscriberID(subscriber.getId());
			    if(null != subscriberGroup) {
			     	result.put(GROUP, subscriberGroup.getGroupid());
			    }
			    
			    if (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType())) {
			      Set<Partner> setPartner = subscriber.getPartners();
			      for (Partner p : setPartner) {
			    	  result.put(PARTNER_ID, p.getId());
			     }
			   }
			}
		}
		return result;
	}
		
	public Transaction getChargeDetails(ServiceCharge sc) throws DataException, InvalidServiceException, InvalidChargeDefinitionException{
		Transaction transaction = getCharge(sc);
		return transaction;
		
	}
	/**
	 * Calculates the Total Transaction charge from user, Transaction amount and any other charges (Commission) defined for the given details.
	 * @param sc
	 * @return
	 * @throws InvalidServiceException, DataException
	 * @throws InvalidChargeDefinitionException 
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Transaction getCharge(ServiceCharge sc) throws InvalidServiceException, DataException, InvalidChargeDefinitionException {
		log.info("Getting the Charges for --> " + sc.getTransactionTypeName());
		
		
		HashMap<TransactionCharge, BigDecimal> chargeMap = null;

		BigDecimal originalAmount = sc.getTransactionAmount();
		BigDecimal amountToDebit = moneyService.round(originalAmount);
		BigDecimal amountToCredit = moneyService.round(originalAmount);
		BigDecimal charges = BigDecimal.ZERO;
				
		long serviceProviderId = getServiceProviderId(sc.getServiceProviderName());
		long serviceId = getServiceId(sc.getServiceName());
		long transactionTypeId = getTransactionTypeId(sc.getTransactionTypeName());
		
		log.info("checking for source partner service details");
		Map<String, Long> sourceMap = getMDNDetails(sc.getSourceMDN());
		if (sourceMap.get(PARTNER_ID) != null && !ServiceAndTransactionConstants.TRANSACTION_AGENTACTIVATION.equals(sc.getTransactionTypeName())
				&&!ServiceAndTransactionConstants.TRANSACTION_ACTIVATION.equals(sc.getTransactionTypeName())
				&& !ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_SYSTEM.equals(sc.getTransactionTypeName())
				&& !ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY.equals(sc.getTransactionTypeName())
				&& getPartnerService(sourceMap.get(PARTNER_ID), serviceProviderId, serviceId) == null) {
			log.info("--------------------->Source Partner did not opt for the given Service");
			throw new InvalidServiceException(MessageText._("Source Partner did not opt for the given Service"));
		}
		log.info("checking for destination partner service details DestPartnerMDN="+sc.getDestMDN());
		Map<String, Long> destMap = getMDNDetails(sc.getDestMDN());
		if (sourceMap.get(PARTNER_ID) == null && destMap.get(PARTNER_ID) != null && getPartnerService(destMap.get(PARTNER_ID), serviceProviderId, serviceId) == null) {
	//	if (destMap.get(PARTNER_ID) != null && getPartnerService(destMap.get(PARTNER_ID), serviceProviderId, serviceId) == null) {
			log.info("--------------------->Destination Partner did not opt for the given Service");
			throw new InvalidServiceException(MessageText._("Destination Partner did not opt for the given Service"));
		}

/*		TransactionRule tr = getTransactionRule(serviceProviderId, serviceId, transactionTypeId, sc.getChannelCodeId(), 
				sourceMap.get(TYPE)!=null ? sourceMap.get(TYPE).intValue() : null, sourceMap.get(KYC_LEVEL), 
				destMap.get(TYPE)!=null ? destMap.get(TYPE).intValue() : null, destMap.get(KYC_LEVEL));*/
		
		TransactionRule tr = getNewTransactionRule(serviceProviderId, serviceId, transactionTypeId, sc, 
				sourceMap.get(GROUP)!=null ? sourceMap.get(GROUP).longValue() : null,  
				destMap.get(GROUP)!=null ? destMap.get(GROUP).longValue() : null);
		
		if (tr != null) {
			log.info("Transaction rule applied for the transaction is --> " + tr.getName());
			chargeMap = getTransactionCharges(tr, sc.getTransactionAmount());
			charges = calculateTotalCharge(chargeMap);
			log.info("Total Charge for the transaction -->" + charges.toPlainString());
			if (CmFinoFIX.ChargeMode_Exclusive.equals(tr.getChargemode())) {
				amountToDebit = moneyService.add(originalAmount, charges);//originalAmount.add(charges);
			} else {
				amountToCredit = moneyService.subtract(originalAmount, charges);//originalAmount.subtract(charges);
			}
		}
		
		long sctlId = 0l;
		ServiceChargeTxnLog sctl = null;
		if (sc.getSctlId() != null) {
			sctlId = sc.getSctlId();
			log.info("Calculating the Charge for the SCTL Id -->" + sctlId);
			if(sc.getTransactionIdentifier()!=null && !("".equals(sc.getTransactionIdentifier()))){
				log.info("TransactionIdentifer is not null.table record is created");

				transactionIdentifierService.createTrxnIdentifierDbEntry(sc.getTransactionIdentifier(), sctlId);
			}
		} else {
			sctl = new ServiceChargeTxnLog();
			
			sctl.setTransactionid(sc.getTransactionLogId());
			sctl.setSourcepartnerid(sourceMap.get(PARTNER_ID));
			
			sctl.setDestpartnerid(null!=destMap.get(PARTNER_ID)?destMap.get(PARTNER_ID):null);
			sctl.setMfsbillercode(sc.getMfsBillerCode());
			sctl.setSourcemdn(sc.getSourceMDN());
			sctl.setDestmdn(sc.getDestMDN());
			sctl.setOnbehalfofmdn(sc.getOnBeHalfOfMDN());
			sctl.setServiceproviderid(serviceProviderId);
			sctl.setServiceid(serviceId);
			sctl.setTransactiontypeid(transactionTypeId);
			sctl.setChannelcodeid(sc.getChannelCodeId());
			sctl.setInvoiceno(sc.getInvoiceNo());
			sctl.setTransactionruleid((tr != null) ? tr.getId() : null);
			sctl.setTransactionamount(amountToDebit);
			sctl.setCalculatedcharge(charges);
			if (sc.isReverseTransaction()) {
				sctl.setParentsctlid(sc.getParentSctlId());
			}
			sctl.setStatus(CmFinoFIX.SCTLStatus_Inquiry);
			if (ServiceAndTransactionConstants.TRANSACTION_TRANSFER_UNREGISTERED.equals(sc.getTransactionTypeName()) || 
					ServiceAndTransactionConstants.TRANSACTION_E2ETRANSFER.equals(sc.getTransactionTypeName())) {
				sctl.setIschargedistributed(CmFinoFIX.Boolean_False);	
			} else {
				// treating the transactions which involve bank as charge distribution completed as the bank will take the charge amount.
				sctl.setIschargedistributed(CmFinoFIX.Boolean_True);				
			}
			sctl.setIntegrationtransactionid(null!=sc.getIntegrationTxnID()?sc.getIntegrationTxnID():null);
			sctl.setChargemode((tr != null) ? tr.getChargemode() : null);
			sctl.setDescription(sc.getDescription());
			sctl.setInfo1(sc.getInfo1());
			sctl.setInfo2(sc.getInfo2());
			sctl.setInfo3(sc.getInfo3());
			sctl.setInfo4(sc.getInfo4());
			
			sctlId = saveServiceTransactionLog(sctl);
			log.info("Service Charge Transaction Log created with Id --> " + sctlId);
			if(sc.getTransactionIdentifier()!=null && !("".equals(sc.getTransactionIdentifier()))){
				log.info("TransactionIdentifer is not null.table record is created");

				transactionIdentifierService.createTrxnIdentifierDbEntry(sc.getTransactionIdentifier(), sctlId);
			}
			
		}

		if (chargeMap != null) {
			Set<TransactionCharge> setTC = chargeMap.keySet();
			for (TransactionCharge trc: setTC) {
				saveTransactionChargeLog(sctlId, trc, chargeMap.get(trc));
			}
		}
		// Make Service Audit count for each service
		// Removing Service Audit as no more keeping track of count
//		doServiceAudit(serviceProviderId, serviceId, sourceMap.get(TYPE)!=null ? sourceMap.get(TYPE).intValue() : null, 
//				new Long(sc.getSourceMDN()), sourceMap.get(KYC_LEVEL), originalAmount);
		
		Transaction transactionDetails = new Transaction();
		transactionDetails.setAmountToDebit(amountToDebit);
		transactionDetails.setAmountToCharge(charges);
		transactionDetails.setAmountToCredit(amountToCredit);
		transactionDetails.setServiceChargeTransactionLog(sctl);
		return transactionDetails;
	}

	private TransactionRule getNewTransactionRule(Long serviceProviderId, Long serviceId, Long transactionTypeId, ServiceCharge sc,
			Long sourceGroup, Long destinationGroup) {
		TransactionRule tr = null;
		TransactionRuleQuery query = new TransactionRuleQuery();
		TransactionRuleDAO trDAO = DAOFactory.getInstance().getTransactionRuleDAO();
		
		//Get All transaction rules for a given transactionid
		query.setServiceId((serviceId != null) ? serviceId : 0);
		query.setTransactionTypeId((transactionTypeId != null) ? transactionTypeId : 0);
		List<TransactionRule> lst = trDAO.get(query);
		
		TransactionChargeQuery tcQuery = new TransactionChargeQuery();
		TransactionChargeDAO tcDAO = DAOFactory.getInstance().getTransactionChargeDAO();
		TransactionCharge tc = null;
		
		//
		if (CollectionUtils.isNotEmpty(lst)) {
			for(Iterator<TransactionRule> it = lst.iterator();it.hasNext();){
				TransactionRule tRule = it.next();
				tcQuery.setTransactionRuleId(tRule.getId().longValue());
				List<TransactionCharge> tcLst = tcDAO.get(tcQuery);
				if(CollectionUtils.isNotEmpty(tcLst)){
					boolean isActive = false;
					for(Iterator<TransactionCharge> itTcLst = tcLst.iterator();itTcLst.hasNext();){
						TransactionCharge txnChg = itTcLst.next();
						if(txnChg.getIsactive()){
							isActive = true;
							break;
						}
					}
					if(isActive == false){
						it.remove();
					}
				}
			}			
		}

		if (CollectionUtils.isNotEmpty(lst)) {
			RuleKey rk = null;
			RuleKeyQuery rkQuery= new RuleKeyQuery();
			RuleKeyDAO rkDAO = DAOFactory.getInstance().getRuleKeyDao();
			rkQuery.setServiceID(serviceId);
			rkQuery.setTransactionTypeID(transactionTypeId);
			rkQuery.setSortByPriority(true);
			List <RuleKey> rkLst = rkDAO.get(rkQuery);

			//Map<String,Object> weight = new HashMap<String, Object>();
			
			if(CollectionUtils.isNotEmpty(rkLst)){
				for(int i=0; i<rkLst.size(); i++){
					//weight.put(rkLst.get(i).getTxnRuleKey(), new Integer(1<<((rkLst.size()-1)-i)));
					int weight = 1<<((rkLst.size()-1)-i);
					lst = filterRules(rkLst.get(i).getTxnrulekey(),lst,rkLst.get(i).getTxnrulekeytype(),
							serviceId,transactionTypeId,sc,sourceGroup,destinationGroup,weight);
				}
			}
		}
		
		TransactionRule finalRule = null;
		
		if (CollectionUtils.isNotEmpty(lst)) {
			int maxWeight = 0;			
			for(Iterator<TransactionRule> it = lst.iterator();it.hasNext();){
				TransactionRule tRule = it.next();
				if(ruleWeight.get(tRule.getId()) != null && (Integer)ruleWeight.get(tRule.getId()) > maxWeight){
					maxWeight = (Integer)ruleWeight.get(tRule.getId());
					finalRule = tRule;
				}
			}
			// Filters the rules based on the source group and destination group as there is no weightage given to any rule.
			if (finalRule == null && maxWeight == 0) {
				finalRule = filterRules(lst, sourceGroup, destinationGroup);
			}
		}		
		
		return finalRule;
	}
	
	
	private List<TransactionRule> filterRules(String txnRuleKey,
			List<TransactionRule> lst, String txnRuleKeyType, Long serviceId,
			Long transactionTypeId, ServiceCharge sc, Long sourceGroup,
			Long destinationGroup, int weight) {
		TxnRuleAddnInfo txnAddlInfo = null;
		TransactionRuleAddnInfoQuery traiQuery = new TransactionRuleAddnInfoQuery();
		TransactionRuleAddnInfoDAO traiDAO = DAOFactory.getInstance().getTransactionRuleAddnInfoDao();
		List <TransactionRule> newLst = new ArrayList<TransactionRule>();
		
		for(Iterator<TransactionRule> it = lst.iterator();it.hasNext();){
			TransactionRule tRule = it.next();
			if(txnRuleKeyType.equalsIgnoreCase("Additional")){				
				traiQuery.setTransactionRuleID(tRule.getId().longValue());
				List <TxnRuleAddnInfo> traiLst = traiDAO.get(traiQuery);
				for(TxnRuleAddnInfo trai : traiLst){
					if(txnRuleKey.equals(trai.getTxnrulekey())){
						if(txnRuleKey.equalsIgnoreCase("BillerCode")){
							if(trai.getTxnrulevalue().equals(sc.getMfsBillerCode())){
								newLst.add(tRule);
								Integer curWeight = (Integer)ruleWeight.get(tRule.getId());
								int newWeight = (curWeight == null ? weight : curWeight+weight);
								ruleWeight.put(tRule.getId().longValue(), newWeight);								
							}
							else{
								it.remove();
							}
						}
					}
				}

			}
			else if(txnRuleKeyType.equalsIgnoreCase("Standard")){
				if(txnRuleKey.equalsIgnoreCase("SourceGroup")){
					if(tRule.getSourcegroup().equals(sourceGroup) &&
							!(tRule.getSourcegroup().equals(ConfigurationUtil.ANY_GROUP_ID))){
						newLst.add(tRule);
						Integer curWeight = (Integer)ruleWeight.get(tRule.getId());
						int newWeight = (curWeight == null ? weight : curWeight+weight);
						ruleWeight.put(tRule.getId().longValue(), newWeight);								
					}
					if(tRule.getSourcegroup().equals(ConfigurationUtil.ANY_GROUP_ID)){
						newLst.add(tRule);
					}
				}
				
				if(txnRuleKey.equalsIgnoreCase("DestGroup")){
					if(tRule.getDestinationgroup().equals(destinationGroup) &&
							!(tRule.getDestinationgroup().equals(ConfigurationUtil.ANY_GROUP_ID))){
						newLst.add(tRule);
						Integer curWeight = (Integer)ruleWeight.get(tRule.getId());
						int newWeight = (curWeight == null ? weight : curWeight+weight);
						ruleWeight.put(tRule.getId().longValue(), newWeight);								
					}
					if(tRule.getDestinationgroup().equals(ConfigurationUtil.ANY_GROUP_ID)){
						newLst.add(tRule);
					}
				}
				
				if(txnRuleKey.equalsIgnoreCase("Channel")){
					if(tRule.getChannelCode().getId().equals(sc.getChannelCodeId()) &&
							!(tRule.getChannelCode().getId().equals(ConfigurationUtil.DEFAULT_CHANNEL_ID))){
						newLst.add(tRule);
						Integer curWeight = (Integer)ruleWeight.get(tRule.getId());
						int newWeight = (curWeight == null ? weight : curWeight+weight);
						ruleWeight.put(tRule.getId().longValue(), newWeight);								
					}
					if(tRule.getChannelCode().getId().equals(ConfigurationUtil.DEFAULT_CHANNEL_ID)){
						newLst.add(tRule);
					}
				}
			}
		}
		if(newLst.isEmpty() && !lst.isEmpty()){
			return lst;}
		return newLst;
	}
	
	/**
	 * Filters the rules based on the source group and destination group
	 * @param lst
	 * @param sourceGroup
	 * @param destinationGroup
	 * @return
	 */
	private TransactionRule filterRules(List<TransactionRule> lst, Long sourceGroup, Long destinationGroup) {
		TransactionRule tr = null;

		if (CollectionUtils.isNotEmpty(lst)) {
			for(TransactionRule tRule: lst){
				if((tRule.getSourcegroup().equals(sourceGroup)) && (tRule.getDestinationgroup().equals(destinationGroup))){
					tr = tRule;
					break;
				}
			}
			
			if(tr == null){
				for(TransactionRule tRule: lst){
					if(((tRule.getSourcegroup().equals(sourceGroup)) && (tRule.getDestinationgroup().equals(ConfigurationUtil.ANY_GROUP_ID))) ||
							((tRule.getSourcegroup().equals(ConfigurationUtil.ANY_GROUP_ID)) && (tRule.getDestinationgroup().equals(destinationGroup)))){

						tr = tRule;
						break;
					}
				}
			}
			
			if(tr == null){
				for(TransactionRule tRule: lst){
					if((tRule.getSourcegroup().equals(ConfigurationUtil.ANY_GROUP_ID)) && (tRule.getDestinationgroup().equals(ConfigurationUtil.ANY_GROUP_ID)))
					{

						tr = tRule;
						break;
					}
				}
			}
		}
		
		return tr;

	}
	
	/**
	 * Gets the Transaction rule for the given details
	 * @param serviceProviderId
	 * @param serviceId
	 * @param transactionTypeId
	 * @param channelCodeId
	 * @param sourceType
	 * @param sourceKYC
	 * @param destType
	 * @param destKYC
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public TransactionRule getTransactionRule(Long serviceProviderId, Long serviceId, Long transactionTypeId, Long channelCodeId,
			Integer sourceType, Long sourceKYC, Integer destType, Long destKYC) {
		TransactionRule tr = null;
		TransactionRuleQuery query = new TransactionRuleQuery();
		TransactionRuleDAO trDAO = DAOFactory.getInstance().getTransactionRuleDAO();
		query.setServiceProviderId((serviceProviderId != null) ? serviceProviderId : 0);
		query.setServiceId((serviceId != null) ? serviceId : 0);
		query.setTransactionTypeId((transactionTypeId != null) ? transactionTypeId : 0);
		query.setChannelCodeId((channelCodeId != null) ? channelCodeId : 0);
		query.setSourceType((sourceType != null) ? sourceType : 0);
		query.setSourceKYC((sourceKYC != null) ? sourceKYC : 0);
		query.setDestType((destType != null) ? destType : 0);
		query.setDestKYC((destKYC != null) ? destKYC : 0);
		List<TransactionRule> lst = trDAO.get(query);
		if (CollectionUtils.isNotEmpty(lst)) {
			tr = lst.get(0);
		}
		return tr;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public TransactionRule getTransactionRule(Long serviceProviderId, Long serviceId, Long transactionTypeId, Long channelCodeId,
			Long sourceGroup, Long destinationGroup) {
		
		TransactionRule tr = null;
		TransactionRuleQuery query = new TransactionRuleQuery();
		TransactionRuleDAO trDAO = DAOFactory.getInstance().getTransactionRuleDAO();
		query.setServiceProviderId((serviceProviderId != null) ? serviceProviderId : 0);
		query.setServiceId((serviceId != null) ? serviceId : 0);
		query.setTransactionTypeId((transactionTypeId != null) ? transactionTypeId : 0);
		query.setChannelCodeId((channelCodeId != null) ? channelCodeId : 0);
		query.setSourceGroup(sourceGroup);
		query.setDestinationGroup(destinationGroup);
		List<TransactionRule> lst = trDAO.get(query);
		
		if (CollectionUtils.isNotEmpty(lst)) {
			for(TransactionRule tRule: lst){
				if((tRule.getSourcegroup().equals(sourceGroup)) && (tRule.getDestinationgroup().equals(destinationGroup))){
					tr = tRule;
					break;
				}
			}
			
			if(tr == null){
				for(TransactionRule tRule: lst){
					if(((tRule.getSourcegroup().equals(sourceGroup)) && (tRule.getDestinationgroup().equals(ConfigurationUtil.ANY_GROUP_ID))) ||
							((tRule.getSourcegroup().equals(ConfigurationUtil.ANY_GROUP_ID)) && (tRule.getDestinationgroup().equals(destinationGroup)))){

						tr = tRule;
						break;
					}
				}
			}
			
			if(tr == null){
				for(TransactionRule tRule: lst){
					if((tRule.getSourcegroup().equals(ConfigurationUtil.ANY_GROUP_ID)) && (tRule.getDestinationgroup().equals(ConfigurationUtil.ANY_GROUP_ID)))
					{

						tr = tRule;
						break;
					}
				}
			}
		}
		
		return tr;
	}
	
	/**
	 * Calculate the individual charges for each charge type. 
	 * @param tr
	 * @param amount
	 * @return
	 * @throws InvalidChargeDefinitionException 
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public HashMap<TransactionCharge, BigDecimal> getTransactionCharges(TransactionRule tr, BigDecimal amount) throws InvalidChargeDefinitionException {
		TransactionCharge tc = null;
		ChargeDefinition cd = null;
		BigDecimal charge = BigDecimal.ZERO;
		HashMap<ChargeType, BigDecimal> map = new HashMap<ChargeType, BigDecimal>();
		HashMap<TransactionCharge, BigDecimal> chargeMap = new HashMap<TransactionCharge, BigDecimal>();
		if (tr != null) {
			Set<TransactionCharge> setTC = tr.getTransactionCharges();
			if (CollectionUtils.isNotEmpty(setTC)) {
				int size = setTC.size();
				TransactionCharge[] arrayTC = new TransactionCharge[size];//(TransactionCharge[])setTC.toArray()
				int count=0;
				
				/**
				 * Get the Array from the charges set and loop through them infinitely to find the
				 * all the charges for the given rule.
				 * Charge can be dependent on another charge so this loop 
				 * will try to find the dependent charge for each charge and mark it completed 
				 * once all charges are marked completed loop will end.
				 */
				for (TransactionCharge transactionCharge : setTC) {
					if(transactionCharge.getIsactive()){
					arrayTC[count] = transactionCharge;
					count++;
					}
				}
				for (int i=0; ; i++) {
					if(count ==0){
						break;
					}
					if (i == count) {
						i = 0;
					}
					tc = arrayTC[i];
					cd = tc.getChargeDefinition();
					if (cd.getChargeTypeByDependantchargetypeid() == null) {
						charge = calculateCharge(tc.getChargeDefinition(), amount);
						map.put(tc.getChargeType(), charge);
						chargeMap.put(tc, charge);
					} else {
						if (map.get(cd.getChargeTypeByDependantchargetypeid()) != null) {
							charge = calculateCharge(tc.getChargeDefinition(), map.get(cd.getChargeTypeByDependantchargetypeid()));
							map.put(tc.getChargeType(), charge);
							chargeMap.put(tc, charge);
						}
					}
					if (chargeMap.size() == count) {
						break;
					}
				}
			}
		}
		return chargeMap;
	}
	
	/**
	 * Calculates the total of all charges.
	 * @param chargeMap
	 * @return
	 */
	public BigDecimal calculateTotalCharge(HashMap<TransactionCharge, BigDecimal> chargeMap) {
		
		BigDecimal totalCharge = BigDecimal.ZERO;
		Set<TransactionCharge> keySet = chargeMap.keySet();
		for (TransactionCharge tc: keySet) {
			if (tc.getChargeDefinition().getIschargefromcustomer() != 0) {
				totalCharge = moneyService.add(totalCharge, chargeMap.get(tc));	
			}
		}
		return totalCharge;
	}
	
	/**
	 * Calculate the Charge for the given charge Definition.
	 * @param cd
	 * @param amount
	 * @return
	 * @throws InvalidChargeDefinitionException 
	 */	
	public BigDecimal calculateCharge(ChargeDefinition cd, BigDecimal amount) throws InvalidChargeDefinitionException {
		log.info("Calculating charge for amt:" + amount + " using charge def:"+ cd.getName());
		
		ChargePricing cp = null, appliedChargePricing = null;		
		BigDecimal serviceCharge = BigDecimal.ZERO;
		boolean isDefault = true;
		
		if (cd != null) {
			Set<ChargePricing> setCP = cd.getChargePricings();
			Iterator<ChargePricing> iteratorCP = setCP.iterator();
			while (iteratorCP.hasNext()) {
				cp = iteratorCP.next();
				if (cp.getIsdefault()!= null && cp.getIsdefault() != 0) {
					appliedChargePricing = cp;
				} else if (amount.compareTo(cp.getMinamount()) >= 0 && amount.compareTo(cp.getMaxamount()) <= 0) {							
					appliedChargePricing = cp;
					isDefault = false;
					break;
				}
			}
		}
		if(isDefault) {
			log.info("Applied charge pricing is the default one");
		}
		log.info("Applied charge pricing ID: " + appliedChargePricing.getId());		
		try {
			serviceCharge = appliedChargePricing.getCharge()!= null ? moneyService.calculateChargeFromExpr(amount, appliedChargePricing.getCharge()) : BigDecimal.ZERO;
			log.info("serviceCharge ->" + serviceCharge);
			BigDecimal minCharge = appliedChargePricing.getMincharge()!= null ? moneyService.calculateChargeFromExpr(amount, appliedChargePricing.getMincharge()) : BigDecimal.ZERO;
			BigDecimal maxCharge = appliedChargePricing.getMaxcharge()!= null ? moneyService.calculateChargeFromExpr(amount, appliedChargePricing.getMaxcharge()) : BigDecimal.ZERO;
			log.info("minCharge ->"+minCharge+"; maxCharge ->"+maxCharge);
			if(minCharge.compareTo(maxCharge) > 0) {
				throw new InvalidChargeDefinitionException("minCharge should not be greater than maxCharge");
			}
			log.info("minCharge ->"+minCharge);
			if(serviceCharge.compareTo(minCharge) <= 0){
				serviceCharge = minCharge;
			} else {							
				log.info("maxCharge ->"+maxCharge);
				if(serviceCharge.compareTo(maxCharge) >=0){
					serviceCharge = maxCharge;
				}
			}					
		} catch (UnknownFunctionException e) {
			log.error("Error while calculating serviceCharge from expr : " + e.getMessage());
			throw new InvalidChargeDefinitionException("Error while calculating serviceCharge from expr : " + e.getMessage());
		} catch (UnparsableExpressionException e) {
			log.error("Error while calculating serviceCharge from expr : " + e.getMessage());
			throw new InvalidChargeDefinitionException("Error while calculating serviceCharge from expr : " + e.getMessage());
		} catch (Exception e) {
			log.error("Error while calculating serviceCharge :"+ e.getMessage());
			throw new InvalidChargeDefinitionException("Error while calculating serviceCharge : " + e.getMessage());
		}		
		return serviceCharge;
	}
	
	/**
	 * Saves the Service Transaction details to the log table.
	 * @param sctl
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Long saveServiceTransactionLog(ServiceChargeTxnLog sctl) {
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		sctlDAO.save(sctl);
		return sctl.getId().longValue();	
	}
	/**
	 * Returns the Service charge Transaction Log entry by Transaction Log Id (Parent Transaction ID)
	 * @param transactionLogId
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public ServiceChargeTxnLog getServiceChargeTransactionLog(long transactionLogId) 
	{
		return getServiceChargeTransactionLog(transactionLogId,null);
	}
	
	/**
	 * Adds the sctlID to the transactionIdentifier table and returns the  
	 * Service charge Transaction Log entry by Transaction Log Id (Parent Transaction ID)
	 * @param transactionLogId
	 * @param trxnIdentifier
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public ServiceChargeTxnLog getServiceChargeTransactionLog(long transactionLogId,String trxnIdentifier) {
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDAO.getByTransactionLogId(transactionLogId);
		//adding the sctl to the confirmation transaction identifier
		if(trxnIdentifier!=null && !("".equals(trxnIdentifier)))
		{
			log.info("TransactionIdentifer is not null.table record is created");
			TransactionIdentifierServiceImpl trxnIdentifierService = new TransactionIdentifierServiceImpl();	
			trxnIdentifierService.createTrxnIdentifierDbEntry(trxnIdentifier, sctl.getId().longValue());
		}
		return sctl;
	}
	/**
	 * Updates the SCTL status to Confirmed
	 * @param sctl
	 * @param commodityTransaferId
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void confirmTheTransaction(ServiceChargeTxnLog sctl, long commodityTransaferId) {
		log.info("Confirms the Transaction --> " + sctl.getId());
		sctl.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
		sctl.setCommoditytransferid((commodityTransaferId!=0) ? commodityTransaferId: null);
		saveServiceTransactionLog(sctl);
	}
	
	/**
	 * Updates the SCTL status to Confirmed
	 * @param sctl
	 * @param commodityTransaferId
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void confirmTheTransaction(ServiceChargeTxnLog sctl) {
		log.info("Confirms the Transaction --> " + sctl.getId());
		sctl.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
		sctl.setFailurereason("");
		saveServiceTransactionLog(sctl);
	}
	
	/**
	 * Updates the SCTL status to Pending
	 * @param commodityTransaferId
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void setPendingStatus(long commodityTransaferId) {
		ChargeTxnCommodityTransferMapDAO cTxnCommodityTransferMapDAO = DAOFactory.getInstance().getTxnTransferMap();
		ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
		query.setCommodityTransferID(commodityTransaferId);
		List<ChargetxnTransferMap> ctTxnCommodityTransferMap = cTxnCommodityTransferMapDAO.get(query);
		BigDecimal sctlid = ctTxnCommodityTransferMap!=null&&!ctTxnCommodityTransferMap.isEmpty()?new BigDecimal(ctTxnCommodityTransferMap.get(0).getSctlid()):null;
		if(sctlid!=null){
		ServiceChargeTxnLog sctl = DAOFactory.getInstance().getServiceChargeTransactionLogDAO().getById(sctlid.longValue());
		if((!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())) &&
				(!CmFinoFIX.SCTLStatus_Pending_Resolved.equals(sctl.getStatus())) &&
				(!CmFinoFIX.SCTLStatus_Pending_Resolved_Processing.equals(sctl.getStatus()))){
			sctl.setStatus(CmFinoFIX.SCTLStatus_Pending);
			log.info("set Pending Status to sctl--> " + sctlid);
			saveServiceTransactionLog(sctl);
		}
		}
	}
	
	/**
	 * Updates the Pending SCTL status to Fail
	 * @param commodityTransaferId
	 * @param failureReason
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void setAsFailed(long commodityTransaferId, String failureReason) {
		ChargeTxnCommodityTransferMapDAO cTxnCommodityTransferMapDAO = DAOFactory.getInstance().getTxnTransferMap();
		ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
		query.setCommodityTransferID(commodityTransaferId);
		List<ChargetxnTransferMap> ctTxnCommodityTransferMap = cTxnCommodityTransferMapDAO.get(query);
		BigDecimal sctlid = ctTxnCommodityTransferMap!=null&&!ctTxnCommodityTransferMap.isEmpty()?new BigDecimal(ctTxnCommodityTransferMap.get(0).getSctlid()):null;
		if(sctlid!=null){
		ServiceChargeTxnLog sctl = DAOFactory.getInstance().getServiceChargeTransactionLogDAO().getById(sctlid.longValue());
		if (CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())) {
			sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
			sctl.setFailurereason(failureReason);
			log.info("Changing the Pending SCTL as Failed --> " + sctlid);
			saveServiceTransactionLog(sctl);
		}
		}
	}	
	
	/**
	 * Updates the SCTL CommodityTransferID
	 * @param sctl
	 * @param commodityTransaferId
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void addTransferID(ServiceChargeTxnLog sctl, long commodityTransaferId) {
		log.info("add the CommodityTransferID "+commodityTransaferId+" --> " + sctl.getId());
		sctl.setCommoditytransferid((commodityTransaferId!=0) ? commodityTransaferId: null);
		saveServiceTransactionLog(sctl);
	}
	
	/**
	 * Updates the SCTL status to Distribution completed
	 * @param sctl
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void completeTheTransaction(ServiceChargeTxnLog sctl) {
		log.info("Distribution Completed the Transaction --> " + sctl.getId());
		sctl.setStatus(CmFinoFIX.SCTLStatus_Distribution_Completed);
		sctl.setIschargedistributed(CmFinoFIX.Boolean_True);
		saveServiceTransactionLog(sctl);
	}
	
	/**
	 * Updates the SCTL status to Failure
	 * @param sctl
	 * @param failureReason
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void failTheTransaction(ServiceChargeTxnLog sctl, String failureReason) {
		log.info("Fails the Transaction --> " + sctl.getId() + " Reason:: " + failureReason);
		sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
		sctl.setFailurereason(failureReason);
		saveServiceTransactionLog(sctl);
	}
	
	/**
	 * Update the SCTL status to Pending for Agent for Confirmation
	 * @param sctl
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void chnageStatusToProcessing(ServiceChargeTxnLog sctl) {
		log.info("Send the Transaction for processing to Backend --> " + sctl.getId());
		sctl.setStatus(CmFinoFIX.SCTLStatus_Processing);
		saveServiceTransactionLog(sctl);
	}
	
	/**
	 * Update the SCTL status to Pending for Agent for Confirmation
	 * @param sctl
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void changeStatusToPending(ServiceChargeTxnLog sctl) {
		log.info("Change transaction to Pending " + sctl.getId());
		sctl.setStatus(CmFinoFIX.SCTLStatus_Pending);
		saveServiceTransactionLog(sctl);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void changeStatusToPendingResolved(ServiceChargeTxnLog sctl) {
		log.info("changeStatusToPendingResolved " + sctl.getId());
		sctl.setStatus(CmFinoFIX.SCTLStatus_Pending_Resolved);
		saveServiceTransactionLog(sctl);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void changeStatusToPendingResolvedProcessing(ServiceChargeTxnLog sctl) {
		log.info("changeStatusToPendingResolvedProcessing " + sctl.getId());
		sctl.setStatus(CmFinoFIX.SCTLStatus_Pending_Resolved_Processing);
		saveServiceTransactionLog(sctl);
	}
	
	/**
	 * Stores the Individual Charge amounts to the log table. 
	 * @param sctlId
	 * @param tc
	 * @param charge
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveTransactionChargeLog(long sctlId, TransactionCharge tc, BigDecimal charge ) {
		TransactionChargeLog tcl = new TransactionChargeLog();
		tcl.setServicechargetransactionlogid(sctlId);
		tcl.setTransactionCharge(tc);
		tcl.setCalculatedcharge(charge);
		log.info("Charge Calculated as " + tc.getChargeType().getName() + " is --> " + charge);
		TransactionChargeLogDAO tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();
		tclDAO.save(tcl);
	}
	
	/**
	 * Checks whether the given Agent / Partner has active service or not
	 * @param sc
	 * @return
	 * @throws InvalidServiceException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)	
	public boolean checksPartnerService(ServiceCharge sc) throws InvalidServiceException {
		boolean result = false;
		long serviceProviderId = getServiceProviderId(sc.getServiceProviderName());
		long serviceId = getServiceId(sc.getServiceName());
		
		Map<String, Long> sourceMap = getMDNDetails(sc.getSourceMDN());
		if (sourceMap.get(PARTNER_ID) != null && getPartnerService(sourceMap.get(PARTNER_ID), serviceProviderId, serviceId) != null) {
			result = true;
		}
		return result;
	}
	
	/**
	 * Returns the Partner Service Object for the given Partner and Service details
	 * @param sc
	 * @return
	 * @throws InvalidServiceException
	 */
	public PartnerServices getPartnerService(ServiceCharge sc) throws InvalidServiceException {
		PartnerServices partnerService = null;
		long serviceProviderId = getServiceProviderId(sc.getServiceProviderName());
		long serviceId = getServiceId(sc.getServiceName());
		Map<String, Long> sourceMap = getMDNDetails(sc.getSourceMDN());
		if (sourceMap.get(PARTNER_ID) != null) {
			partnerService = getPartnerService(sourceMap.get(PARTNER_ID), serviceProviderId, serviceId);
		}
		return partnerService;
	}
	
	/**
	 * Returns the Partner Service for the given Partner and Service
	 * @param partnerId
	 * @param serviceProviderId
	 * @param serviceId
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PartnerServices getPartnerService(long partnerId, long serviceProviderId, long serviceId) {
		PartnerServices ps = null;
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		List<PartnerServices> lstPS = psDAO.getPartnerServices(partnerId, serviceProviderId, serviceId);
		if (CollectionUtils.isNotEmpty(lstPS)) {
			ps = lstPS.get(0);
		}
		return ps;
	}
	
	/**
	 * Distribute the Transaction amount among the Partners involved in the Transaction.
	 * @param sctlId
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void distributeTransactionAmount(Long sctlId) {
		log.info("TransactionChargingService::distributeTransactionAmount for -->" + sctlId);
		
		ServiceChargeTransactionLogDAO sctlDAO = null;
		TransactionChargeLogDAO tclDAO = null;
		TransactionCharge tc = null;
		List<TransactionChargeLog> lstTCL = null;
		HashMap<TransactionChargeShareHolder, BigDecimal> shareMap = null;
		List<TxnAmountDstrbLog> lstTADL = new ArrayList<TxnAmountDstrbLog>();
		
		if (sctlId != null) {
			sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
			tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();
			ServiceChargeTxnLog sctl = sctlDAO.getById(sctlId);
			lstTCL = tclDAO.getBySCTLID(sctlId);

			sctl.setStatus(CmFinoFIX.SCTLStatus_Distribution_Started);
			sctlDAO.save(sctl);
			
			for (TransactionChargeLog tcl: lstTCL) {
				if (tcl.getCalculatedcharge().compareTo(BigDecimal.ZERO) > 0) {
					tc = tcl.getTransactionCharge();
					log.info("Processing charge distribution for transactionChargeLog with id: "+ tcl.getId() + " and txn Charge: "+ tc.getId());
					BigDecimal calculatedCharge = tcl.getCalculatedcharge();
					log.info("calculatedCharge ->"+ calculatedCharge);
					Set<SharePartner> setSP = tc.getSharePartners();
					shareMap = new HashMap<TransactionChargeShareHolder, BigDecimal>();
					for (SharePartner sp: setSP) {						
						try {							
							if( sp.getActualsharepercentage()!=null){							
								if(CmFinoFIX.ShareHolderType_Partner.equals(sp.getShareholdertype())){
									log.info("charge calculation for sharePartner of type Partner");
									TransactionChargeShareHolder tcShareHolder = new TransactionChargeShareHolder(sp.getPartner().getId().longValue());
									shareMap = updateShareMap(shareMap, tcShareHolder, calculateChargeShare(sp, calculatedCharge));
								}else if(CmFinoFIX.ShareHolderType_Source.equals(sp.getShareholdertype()) /*&& sctl.getSourcePartnerID() != null */ ){
									log.info("charge calculation for sharePartner of type Source");
									if(sctl.getSourcepartnerid() != null) //check based on subscriber type
									{	
										log.info("SourcePartnerID -> "+ sctl.getSourcepartnerid());
										TransactionChargeShareHolder tcShareHolder = new TransactionChargeShareHolder(sctl.getSourcepartnerid().longValue());
										shareMap = updateShareMap(shareMap, tcShareHolder, calculateChargeShare(sp, calculatedCharge));										
									}
									else if(tc.getIschrgdstrbapplicabletosrcsub() && sctl.getSourcemdn() != null)
									{
										SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(sctl.getSourcemdn());
										log.info("smdn -> "+ smdn);
										if(smdn != null)
										{
											TransactionChargeShareHolder tcShareHolder = new TransactionChargeShareHolder(smdn.getSubscriber().getId().longValue(), false);
											shareMap = updateShareMap(shareMap, tcShareHolder, calculateChargeShare(sp, calculatedCharge));
										}
									}
								}else if(CmFinoFIX.ShareHolderType_Destination.equals(sp.getShareholdertype()) /*&& sctl.getDestPartnerID() != null*/){
									log.info("charge calculation for sharePartner of type Destination");
									if(sctl.getDestpartnerid() != null)
									{	
										log.info("DestPartnerID -> " + sctl.getDestpartnerid());
										TransactionChargeShareHolder tcShareHolder = new TransactionChargeShareHolder(sctl.getDestpartnerid().longValue());
										shareMap = updateShareMap(shareMap, tcShareHolder, calculateChargeShare(sp, calculatedCharge));
									}
									else if(tc.getIschrgdstrbapplicabletodestsub() && StringUtils.isNotBlank(sctl.getDestmdn()))
									{
										SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(sctl.getDestmdn());
										log.info("smdn -> "+ smdn);
										if(smdn != null)
										{
											TransactionChargeShareHolder tcShareHolder = new TransactionChargeShareHolder(smdn.getSubscriber().getId().longValue(), false);
											shareMap = updateShareMap(shareMap, tcShareHolder, calculateChargeShare(sp, calculatedCharge));
										}
									}
								}else if(CmFinoFIX.ShareHolderType_RegistrationPartner.equals(sp.getShareholdertype())){
									log.info("charge calculation for sharePartner of type RegistrationPartner");
									Long registeringPartnerId = null;
									if (sctl.getSourcepartnerid() == null) {
										registeringPartnerId = getRegisteringPartner(sctl.getSourcemdn());
									} else if (sctl.getDestpartnerid() == null) {
										registeringPartnerId = getRegisteringPartner(sctl.getDestmdn());
									}
									log.info("registeringPartnerId -> "+ registeringPartnerId);
									TransactionChargeShareHolder tcShareHolder = new TransactionChargeShareHolder(registeringPartnerId);
									shareMap = updateShareMap(shareMap, tcShareHolder, calculateChargeShare(sp, calculatedCharge));
								}
							
							}
						} catch (Exception e) {
							log.info("TransactionChargingService::distributeTransactionAmount for -->" + sctl.getId() + " is Failed.");
							log.error("Error in retrieving/allocating share percentages for share partners :"+e.getMessage());
							failTheDistribution(sctl, "Error in retrieving/allocating share percentages for share partners");							
							return;
						}						
					}
					
					try {
						validateAggregateShare(shareMap, tcl.getCalculatedcharge());
					} catch (DistributionException e) {
						log.info("TransactionChargingService::distributeTransactionAmount for -->" + sctl.getId() + " is Failed.");
						log.error("Error in validating aggregate share for share partners :"+ e.getMessage());
						failTheDistribution(sctl, "Error in retrieving/allocating share percentages for share partners");							
						return;
					}
					
					shareMap = moneyService.roundOffTheShareMoney(shareMap);
					Set<TransactionChargeShareHolder> setPartners = shareMap.keySet();
					log.info("Constructed ShareMap for Distribution with amounts", shareMap);
					
					for (TransactionChargeShareHolder tcShareHolder: setPartners) {
						log.info("Distributing the charge for " + (tcShareHolder.getIsPartner()?"Partner":"Subscriber") + " --> " + tcShareHolder.getTransactionChargeShareHolderId() + " and charge amount is --> " + shareMap.get(tcShareHolder));
						
						try {
							if (tcShareHolder.getTransactionChargeShareHolderId() != null) {
								lstTADL = generateTADL(sctl, tc,  shareMap.get(tcShareHolder), lstTADL, tcShareHolder.getTransactionChargeShareHolderId(), tcShareHolder.getIsPartner());
							} else {
								// Get the Service Partner Id to distribute the charge if the Charge is calculated from user.
								if (tc.getChargeDefinition().getIschargefromcustomer() != 0) {
									log.info("genarate TADL for isChargeFromCustomer");
									long servicePartnerId = systemParametersService.getLong(SystemParameterKeys.SERVICE_PARTNER__ID_KEY);
									lstTADL = generateTADL(sctl, tc, shareMap.get(tcShareHolder), lstTADL, servicePartnerId, true);		
								} else {
									// Make the Transaction amount distribution log entry as completed as the destination partnerId is null. 
									// The amount will be with the Funding partner itself.
									ChargeDefinition cd = tc.getChargeDefinition();
									if (shareMap.get(tcShareHolder).compareTo(BigDecimal.ZERO) > 0) {
										lstTADL.add(getTADL(sctl.getId().longValue(), sctl.getTransactionid().longValue(), tc, cd.getPartner(), null, cd.getPocket(), 
											shareMap.get(tcShareHolder), true, false, false, true));
									}
								}
							}
						} catch (DistributionException e) {
							log.error("Error while generating transaction log during charge distribution :" + e.getMessage());
							return;
						}
					}					
				}
			}
			
			if (CollectionUtils.isNotEmpty(lstTADL)) {
				TransactionAmountDistributionLogDAO tadlDAO = DAOFactory.getInstance().getTransactionAmountDistributionLogDAO();
				for (TxnAmountDstrbLog tadl : lstTADL) {
					tadlDAO.save(tadl);
				}
			}
			
			//Update the Transaction in the Service charge Transaction log table as Distributed.
			sctl.setStatus(CmFinoFIX.SCTLStatus_Distribution_Completed);
			sctl.setIschargedistributed(CmFinoFIX.Boolean_True);
			sctlDAO.save(sctl);
			log.info("TransactionChargingService::distributeTransactionAmount for -->" + sctlId + " is Completed");
		}
	}
	
	/**
	 * Validate if aggregate share calculated from individual shares(in shareMap) exceeded the calculatedCharge
	 * @param shareMap
	 * @param calculatedCharge
	 * @throws DistributionException
	 */
	public void validateAggregateShare(HashMap<TransactionChargeShareHolder, BigDecimal> shareMap, BigDecimal calculatedCharge)  throws DistributionException{
		log.info("Validating if aggregate share calculated from individual shares(in shareMap) exceeded the calculatedCharge");
		BigDecimal aggregateShare = BigDecimal.ZERO;
		if (shareMap != null) {
			Iterator<TransactionChargeShareHolder> iterator = shareMap.keySet().iterator();
			while (iterator.hasNext()) {
				TransactionChargeShareHolder key = iterator.next();
				aggregateShare = aggregateShare.add(shareMap.get(key));
			}
		}
		log.info("aggregateShare ->" + aggregateShare + "; calculatedCharge ->"+ calculatedCharge);
		if(aggregateShare.compareTo(calculatedCharge)!= 0){
			throw new DistributionException("Aggregate Share is not equal to calculatedCharge");
		}
	}
	
	/**
	 * Calculate charge share based on minShareCharge and maxShargeCharge values that are obtained from sharePartner object
	 * @param sharePartner
	 * @param calculatedCharge
	 * @return
	 * @throws DistributionException 
	 */
	public BigDecimal calculateChargeShare(SharePartner sharePartner, BigDecimal calculatedCharge) throws DistributionException {
		
		BigDecimal shareCharge = BigDecimal.ZERO;
		try {
			shareCharge = moneyService.calculateChargeFromExpr(calculatedCharge, sharePartner.getActualsharepercentage());
			log.info("SharePercentage ->"+ sharePartner.getActualsharepercentage()+ "; shareCharge ->"+shareCharge);
			BigDecimal minShareCharge = moneyService.calculateChargeFromExpr(calculatedCharge, sharePartner.getMinsharepercentage());
			log.info("MinSharePercentage ->"+sharePartner.getMinsharepercentage()+"; minShareCharge ->" + minShareCharge);
			BigDecimal maxShareCharge = moneyService.calculateChargeFromExpr(calculatedCharge, sharePartner.getMaxsharepercentage());
			log.info("MaxSharePercentage ->"+sharePartner.getMaxsharepercentage()+"; maxShareCharge ->" + maxShareCharge);
			if(minShareCharge.compareTo(maxShareCharge) > 0) {
				throw new DistributionException("Error while calculating charge from expression : minShareCharge should not be greater than maxShareCharge");
			}
			if(shareCharge.compareTo(minShareCharge) < 0) {
				shareCharge = minShareCharge;
			} else if (shareCharge.compareTo(maxShareCharge) > 0) {
				shareCharge = maxShareCharge;
			}
			if(shareCharge.compareTo(BigDecimal.ZERO) == -1 || shareCharge.compareTo(calculatedCharge) == 1) {
				throw new DistributionException("shareCharge:" + shareCharge + " is invalid(either less than 0 or greater than calculated charge)");
			}
		} catch (UnknownFunctionException e) {			
			throw new DistributionException("Error while calculating charge " + e.getMessage());
		} catch (UnparsableExpressionException e) {			
			throw new DistributionException("Error while calculating charge " + e.getMessage());
		} catch (Exception e) {			
			throw new DistributionException("Error while calculating charge " + e.toString());
		}		
		return shareCharge;
	}
	
	/**
	 * Add the given Partner Id with percentage details to the Share Map
	 * @param shareMap
	 * @param partnerId
	 * @param percentage
	 * @return
	 */
	public HashMap<TransactionChargeShareHolder, BigDecimal> updateShareMap(HashMap<TransactionChargeShareHolder, BigDecimal> shareMap, TransactionChargeShareHolder tcShareHolder, BigDecimal percentage) {
		
		if (shareMap.get(tcShareHolder) != null) {
			BigDecimal sharePercent = moneyService.add(percentage, shareMap.get(tcShareHolder));
			shareMap.put(tcShareHolder, sharePercent);
		} else { 
			shareMap.put(tcShareHolder, percentage);
		}
		return shareMap;
	}
	
	/**
	 * Generates the Transaction amount Distribution log for the given SCTL and Partner
	 * @param sctl
	 * @param tc
	 * @param shareAmount
	 * @param lstTADL
	 * @param partnerId
	 * @return
	 * @throws DistributionException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<TxnAmountDstrbLog> generateTADL(ServiceChargeTxnLog sctl, TransactionCharge tc, BigDecimal shareAmount,
			List<TxnAmountDstrbLog> lstTADL, Long partnerId, boolean isPartner) throws DistributionException {
		PartnerServices ps;
		List<PartnerServices> lstParentPS;
		Map<Integer, DistributionChainLvl> mapDCL;
		if(isPartner)
		{
			ps = getPartnerService(partnerId, sctl.getServiceproviderid().longValue(), sctl.getServiceid().longValue());
			if (ps != null) {
				lstParentPS = new ArrayList<PartnerServices>();
				lstParentPS = getParentList(ps, lstParentPS);
				mapDCL = getDistributionChainLevelShares(ps);
				if (lstParentPS.size() > mapDCL.size()) {
					log.info("TransactionChargingService::distributeTransactionAmount for -->"
							+ sctl.getId() + " is Failed.");
					failTheDistribution(
							sctl,
							"Distribution Failed because of Distribution levels not matching with the number of "
									+ "Parents for the given Partner");
					throw new DistributionException(
							"Distribution levels are not properly defined.");
				}
				lstTADL = distributeChargeAmongSharedUpChainPartners(
						sctl.getId().longValue(), sctl.getTransactionid().longValue(), tc, ps,
						lstParentPS, shareAmount, mapDCL, lstTADL);
			} else {
				// If the There is no Partner Service for the Partner then the charge will be added to default Collector Pocket.
				PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
				Partner partner = partnerDAO.getById(partnerId);
				if(partner !=  null)
				{
					SubscriberMdn smdn = partner.getSubscriber().getSubscriberMdns().iterator().next();
					Pocket collectorPocket = subscriberService.getEmoneyPocket(smdn.getId().longValue(), true, true, false);
					if (shareAmount.compareTo(BigDecimal.ZERO) > 0) {
						lstTADL.add(getTADL(sctl.getId().longValue(), sctl.getTransactionid().longValue(), tc, partner, null, collectorPocket, shareAmount, true, 
								false, false, false));
					}				
				}		
			}
		} 
		else 
		{
			SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
			Subscriber subscriber = subscriberDao.getById(partnerId);
			if(subscriber != null)
			{
				SubscriberMdn smdn = subscriber.getSubscriberMdns().iterator().next();
				Pocket defaultSVAPocket = subscriberService.getEmoneyPocket(smdn.getId().longValue(), true, false, false);
				if (shareAmount.compareTo(BigDecimal.ZERO) > 0) {
					lstTADL.add(getTADL(sctl.getId().longValue(), sctl.getTransactionid().longValue(), tc, null, subscriber, defaultSVAPocket, shareAmount, true, 
							false, false, false));
				}	
			}				
		}	
		return lstTADL;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Long getRegisteringPartner(String mdn) {
		Long result = null;
		if (StringUtils.isNotBlank(mdn)) {
			SubscriberMDNDAO sMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMdn smdn = sMDNDAO.getByMDN(mdn);
			if (smdn != null) {
				Subscriber subscriber = smdn.getSubscriber();
				result = subscriber.getRegisteringpartnerid().longValue();
			}
		}
		log.info("Registering Partner Id for the MDN " + mdn + " is --> = " + result);
		return result;
	}
	
	/**
	 * Logs the Distribution of Transaction amount among the Shared up chain partners.
	 * @param sctlId
	 * @param tcId
	 * @param ps
	 * @param lstParentPS
	 * @param amount
	 * @param mapDCL
	 * @param lstTADL
	 * @return
	 */
	public List<TxnAmountDstrbLog> distributeChargeAmongSharedUpChainPartners(long sctlId, long transactionId, 
			TransactionCharge tc, PartnerServices ps, List<PartnerServices> lstParentPS, BigDecimal amount, Map<Integer, 
			DistributionChainLvl> mapDCL, List<TxnAmountDstrbLog> lstTADL) {
		log.info("Distribute charge for the Partnerservice --> " + ps.getId());
		
		BigDecimal amt = BigDecimal.ZERO;
		TxnAmountDstrbLog tadl = null;
		
		if (CollectionUtils.isNotEmpty(lstParentPS)) {
			int size = lstParentPS.size();
			for (int i=0; i<size; i++) {
				PartnerServices p = lstParentPS.get(i);
				if (p != null) {
					DistributionChainLvl dcl = mapDCL.get(size - i);
					BigDecimal share = moneyService.calculateShareAmount(amount, dcl.getCommission());
					amt = moneyService.add(amt, share);
					// Logs the Distribution of partners Service charge among the shared up chain partners
					if (share.compareTo(BigDecimal.ZERO) > 0) {
						PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
						Pocket colectorPocket = pocketDao.getById(p.getCollectorpocket().getId());
						tadl = getTADL(sctlId, transactionId, tc, p.getPartnerByPartnerid(), null, colectorPocket, share, true, false, true, false);
						lstTADL.add(tadl);
					}
				}
			}
		}
		// Logs the Distribution of partners Service charge after the shared up chain
		if (moneyService.subtract(amount, amt).compareTo(BigDecimal.ZERO) > 0) {
			PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
			Pocket colectorPocket = pocketDao.getById(ps.getCollectorpocket().getId());
			tadl = getTADL(sctlId, transactionId, tc, ps.getPartnerByPartnerid(), null, colectorPocket, moneyService.subtract(amount, amt), true, false, false, false);
			lstTADL.add(tadl);
		}
		return lstTADL;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void failTheDistribution(ServiceChargeTxnLog sctl, String failureReason) {
		if (sctl != null) {
			sctl.setStatus(CmFinoFIX.SCTLStatus_Distribution_Failed);
			sctl.setFailurereason(failureReason);
			saveServiceTransactionLog(sctl);
		}
	}
	
	/**
	 * Gets the Share percentages for each level in the Distribution chain for the given PartnerServices. 
	 * @param ps
	 */
	public Map<Integer, DistributionChainLvl> getDistributionChainLevelShares(PartnerServices ps) {
		Map<Integer, DistributionChainLvl> mapDCL = new HashMap<Integer, DistributionChainLvl>();;
		if (ps != null) {
			DistributionChainTemp dct = ps.getDistributionChainTemp();
			if (dct != null) {
				Set<DistributionChainLvl> setDCL = dct.getDistributionChainLvls();
				if (CollectionUtils.isNotEmpty(setDCL)) {
					for (DistributionChainLvl dcl: setDCL) {
						mapDCL.put(((Long)dcl.getDistributionlevel()).intValue(), dcl);
					}
				}				
			}
			log.info("Distribution chain levels for the DCT --> " + mapDCL.size());
		}
		return mapDCL;
	}

	/**
	 * Returns the List of Parents for the given Partner Service.
	 * @param ps
	 * @param lstParentPS
	 * @return
	 */
	public List<PartnerServices> getParentList(PartnerServices ps, List<PartnerServices> lstParentPS) {
		if (ps!= null && CmFinoFIX.IsServiceChargeShare_Shared_Up_Chain.equals(ps.getIsservicechargeshare())) {
			 Partner parent = ps.getPartnerByParentid();
			 if (parent != null) {
				 PartnerServices childPS = getPartnerService(parent.getId().longValue(), ps.getPartnerByServiceproviderid().getId().longValue(), 
						 ps.getService().getId().longValue());
				 lstParentPS.add(childPS);
				 getParentList(childPS, lstParentPS);
			 }			
		}
		return lstParentPS;
	}
	
	public TxnAmountDstrbLog getTADL(Long sctlID, Long transactionId, TransactionCharge tc, Partner partner, Subscriber subscriber,  Pocket pocket, 
			BigDecimal amount, boolean isPartOfCharge, boolean isActualAmt, boolean isPartOfSharedUpChain, boolean isSelf) {
		
		
		
		TxnAmountDstrbLog tadl = new TxnAmountDstrbLog();

		BigDecimal taxAmount = BigDecimal.ZERO;

		if ((!isSelf) && tc.getChargeDefinition().getIstaxable() != 0) {
			ServiceCharge sc = new ServiceCharge();
			sc.setTransactionAmount(amount);
			taxAmount = taxService.calculateTax(sc);
		}
		
		tadl.setServicechargetransactionlogid(new BigDecimal(sctlID));
		tadl.setTransactionid(new BigDecimal(transactionId));
		tadl.setTransactionCharge(tc);
		if(partner != null)
		{
			tadl.setPartner(partner);
		}
		else if(subscriber !=null)
		{
			tadl.setSubscriber(subscriber);
		}
		tadl.setPocket(pocket);
		tadl.setShareamount(moneyService.subtract(amount, taxAmount));
		tadl.setTaxamount(taxAmount);
		tadl.setIspartofcharge((short) (isPartOfCharge?1:0));
		tadl.setIsactualamount((short) (isActualAmt?1:0));
		tadl.setIspartofsharedupchain((short) (isPartOfSharedUpChain?1:0));
		if (isSelf) {
			tadl.setStatus(CmFinoFIX.TADLStatus_Completed);
			tadl.setFailurereason("Self Transaction, so it is stored as success");
		} else {
			tadl.setStatus(CmFinoFIX.TADLStatus_Initialized);
		}
		
		return tadl;
	}
	
	/**
	 * Creates the Log for the Transaction amount distribution
	 * @param sctlID
	 * @param tcID
	 * @param partner
	 * @param pocket
	 * @param amount
	 * @param isPartOfCharge
	 * @param isActualAmt
	 * @param isPartOfSharedUpChain
	 * @param settlementType
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveTADL(Long sctlID, Long transactionId, TransactionCharge tc, Partner partner, Subscriber subscriber, Pocket pocket, BigDecimal amount, boolean isPartOfCharge, boolean isActualAmt, 
			boolean isPartOfSharedUpChain) {
		TransactionAmountDistributionLogDAO tadlDAO = DAOFactory.getInstance().getTransactionAmountDistributionLogDAO();
		TxnAmountDstrbLog tadl = new TxnAmountDstrbLog();
		
		tadl.setServicechargetransactionlogid(new BigDecimal(sctlID));
		tadl.setTransactionid(new BigDecimal(transactionId));
		tadl.setTransactionCharge(tc);
		tadl.setPartner(partner);
		tadl.setSubscriber(subscriber);
		tadl.setPocket(pocket);
		tadl.setShareamount(amount);
		tadl.setIspartofcharge((short) (isPartOfCharge?1:0));
		tadl.setIsactualamount((short) (isActualAmt?1:0));
		tadl.setIspartofsharedupchain((short) (isPartOfSharedUpChain?1:0));
		tadl.setStatus(CmFinoFIX.TADLStatus_Initialized);
		
		tadlDAO.save(tadl);
	}
	
	/**
	 * Updates the Audit information based on the Service and Source type
	 * @param serviceProviderId
	 * @param serviceId
	 * @param sourceType
	 * @param sourceId
	 * @param KYCLevelId
	 * @param amount
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void doServiceAudit(Long serviceProviderId, Long serviceId, Integer sourceType, Long sourceId, Long KYCLevelId, BigDecimal amount) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(new Timestamp().getTime());
		ServiceAuditDAO saDAO = DAOFactory.getInstance().getServiceAuditDAO();
		ServiceAudit sa = saDAO.getServiceAudit(serviceProviderId, serviceId, sourceType, sourceId, KYCLevelId);
		
		if (sa == null) {
			sa = new ServiceAudit();
			sa.setServiceproviderid(new BigDecimal(serviceProviderId));
			sa.setServiceid(new BigDecimal(serviceId));
			sa.setSourcetype(sourceType.longValue());
			sa.setSourceid(new BigDecimal(sourceId));
			sa.setKyclevelid(new BigDecimal(KYCLevelId));
			sa.setCurrentdailytxnscount(1);
			sa.setCurrentdailyexpenditure(amount);
			sa.setCurrentweeklytxnscount(1);
			sa.setCurrentweeklyexpenditure(amount);
			sa.setCurrentmonthlytxnscount(1);
			sa.setCurrentmonthlyexpenditure(amount);
			sa.setLasttransactiontime(new Timestamp(now.getTimeInMillis()));
			saDAO.save(sa);
		} else {
			Calendar lastTransation = Calendar.getInstance();
			lastTransation.setTimeInMillis(sa.getLasttransactiontime().getTime());
			if (now.get(Calendar.DATE) != lastTransation.get(Calendar.DATE)) {
				sa.setPreviousdailyexpenditure(sa.getCurrentdailyexpenditure());
				sa.setPreviousdailytxnscount(sa.getCurrentdailytxnscount());
				sa.setCurrentdailyexpenditure(amount);
				sa.setCurrentdailytxnscount(1);
			} else {
				sa.setCurrentdailyexpenditure(amount.add(sa.getCurrentdailyexpenditure()));
				sa.setCurrentdailytxnscount(sa.getCurrentdailytxnscount() + 1);
			}
			
			if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || 
					(now.getTimeInMillis() - lastTransation.getTimeInMillis()) > 7  * 24 * 60 * 60 * 1000) {
				sa.setCurrentweeklyexpenditure(sa.getCurrentweeklyexpenditure());
				sa.setPreviousweeklytxnscount(sa.getCurrentweeklytxnscount());
				sa.setCurrentweeklyexpenditure(amount);
				sa.setCurrentweeklytxnscount(1);
			} else {
				sa.setCurrentweeklyexpenditure(amount.add(sa.getCurrentweeklyexpenditure()));
				sa.setCurrentweeklytxnscount(sa.getCurrentweeklytxnscount()  +1);
			}
			
			if (now.get(Calendar.MONTH) != lastTransation.get(Calendar.MONTH)) {
				sa.setPreviousmonthlyexpenditure(sa.getCurrentmonthlyexpenditure());
				sa.setPreviousmonthlytxnscount(sa.getCurrentmonthlytxnscount());
				sa.setCurrentmonthlyexpenditure(amount);
				sa.setCurrentmonthlytxnscount(1);
			} else {
				sa.setCurrentmonthlyexpenditure(amount.add(sa.getCurrentmonthlyexpenditure()));
				sa.setCurrentmonthlytxnscount(sa.getCurrentmonthlytxnscount() + 1);
			}
			sa.setLasttransactiontime(new Timestamp(now.getTimeInMillis()));
			saDAO.save(sa);
			log.debug("Service Audit entry created ");
		}
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)	
	public void updateTransactionStatus(TransactionResponse tr, ServiceChargeTxnLog sctl){
		
		if(!isTransactionSuccessful(tr)) {
			ChargeTxnCommodityTransferMapDAO chargeTxnCTMapDao = DAOFactory.getInstance().getTxnTransferMap();
			String text = "";
			
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getId().longValue());
			List<ChargetxnTransferMap> ctxnMapList = chargeTxnCTMapDao.get(query);
			
			if((null != ctxnMapList) && (ctxnMapList.size() > 0)){
				ChargetxnTransferMap map = ctxnMapList.get(0);
				Long ctId = map.getCommoditytransferid().longValue();
				CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
				CommodityTransfer ct = ctDao.getById(ctId);
				if( ct !=null){
				text = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferfailurereason());}
				failTheTransaction(sctl, text);	
			}
			/*
			 * In some failure cases like emoney-emoney self transfer and wrongpin commodity transfer entry won't get created.
			 * So, the text message is fetched directly from the transaction response
			 */
			else
			{
				String errorMsg = tr.getMessage();
				failTheTransaction(sctl, errorMsg);	
			}
		}
	}
	
	public boolean isTransactionSuccessful(TransactionResponse transactionResponse){
		
		boolean flag = false;
		
		Set<String> successNotificationCodes = new HashSet<String>();
		successNotificationCodes.add(CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_CashOutAtATMConfirmationPrompt.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_BillDetails.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_BillpaymentInquirySuccessful.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_AirtimePurchaseInquiry.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_TransferToUnRegisteredConfirmationPrompt.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_InterEmoneyTransferInquirySuccessful.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_NFCPocketTopupInquirySuccess.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_QRpaymentInquirySuccessful.toString());
		successNotificationCodes.add(CmFinoFIX.NotificationCode_IBTInquiry.toString());
		if(successNotificationCodes.contains(transactionResponse.getCode())){
			flag = true;
		}
		
		log.info("FIXMessageHandler : isTransactionSuccessful transactionResponse.getCode()="+transactionResponse.getCode()+", flag="+flag);
		
		return flag;
	}

}
