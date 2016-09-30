/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateConfigDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Groups;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Merchant;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.User;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.DefaultPocketMaintainerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;

@Service("PocketServiceImpl")
public class PocketServiceImpl implements PocketService{
	private static BigDecimal ZERO = new BigDecimal(0);
	private static Logger log = LoggerFactory.getLogger(PocketServiceImpl.class);
	
	PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	
	@Autowired
	@Qualifier("DefaultPocketMaintainerServiceImpl")
	private DefaultPocketMaintainerService defaultPocketMaintainerService;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	// Will be called when the Account Number is Changed.

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void handleCardPanChange(Pocket pocket) {
		log.debug(" Inside handle Account Number change");

		// If the pokcet type is of BankAccount
		// Status is set to "Initialized".
		Long typeL = pocket.getPocketTemplate().getType();
		Integer typeLI = typeL.intValue();
		
		if (typeLI.equals(CmFinoFIX.PocketType_BankAccount)) {
			pocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
			log.debug(" changed status");
		}
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket createDefaultPocket(PocketTemplate defaultPocketTemplate, SubscriberMdn subscriberMDN) {
		return createActivePocket(defaultPocketTemplate, subscriberMDN, true);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket createActivePocket(PocketTemplate defaultPocketTemplate, SubscriberMdn subscriberMDN, boolean isDefault) {
		Pocket pocket = new Pocket();

		pocket.setPocketTemplate(defaultPocketTemplate);
		pocket.setSubscriberMdn(subscriberMDN);
		pocket.setStatustime(new Timestamp());
		pocket.setStatus(CmFinoFIX.PocketStatus_Active);

		pocket.setIsdefault((short) Boolean.compare(isDefault, false));

		if (isDefault) {
			defaultPocketMaintainerService.setDefaultPocket(pocket, true);
		}

		if (subscriberMDN.getSubscriber().getCompany() != null) {
			pocket.setCompany(subscriberMDN.getSubscriber().getCompany());
		}
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		pocketDAO.save(pocket);

		return pocket;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean isDefaultAirTimeSVA(Pocket p) {
		Boolean bool_true = Boolean.valueOf(true);
		
		Long tempPocketL = p.getPocketTemplate().getType();
		Integer tempPocketLI = tempPocketL.intValue();
		
		Long tempCOmoditytL = p.getPocketTemplate().getCommodity();
		Integer tempCOmoditytL1 = tempCOmoditytL.intValue();
		
		if (p != null && p.getPocketTemplate() != null && tempPocketLI.equals(CmFinoFIX.PocketType_SVA)
		        && tempCOmoditytL1 != null && tempCOmoditytL1.equals(CmFinoFIX.Commodity_Airtime)
		        && bool_true.equals(p.getIsdefault())) {
			return true;
		}
		return false;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void changeStatusBasedOnMerchantAndSubscriber(Pocket p) {
		if (p == null) {
			return;
		}
		SubscriberMdn sub = p.getSubscriberMdn();
		if (sub == null) {
			return;
		}

		// @XC: Do we still maintain subscriber status in the backend?
		Long tempStatusL = sub.getStatus();
		Integer tempStatusLI = tempStatusL.intValue();
		
		if (tempStatusLI.equals(CmFinoFIX.SubscriberStatus_Retired)) {
			p.setStatus(CmFinoFIX.PocketStatus_Retired);
		}

		SubscriberMdn mdn = p.getSubscriberMdn();
		if (mdn == null) {
			return;
		}
		
		Long tempmdnStatusL = mdn.getStatus();
		Integer tempmdnStatusLI = tempmdnStatusL.intValue();
		
		if (tempmdnStatusLI.equals(CmFinoFIX.SubscriberStatus_Retired)) {
			p.setStatus(CmFinoFIX.PocketStatus_Retired);
		}

		Merchant merchant = sub.getSubscriber().getMerchant();
		if (merchant == null) {
			return;
		}
		
		Long tempmerchantStatusL = merchant.getStatus();
		Integer tempmerchantStatusLI = tempmerchantStatusL.intValue();
		
		if (tempmerchantStatusLI.equals(CmFinoFIX.SubscriberStatus_Retired)) {
			if (isDefaultAirTimeSVA(p)) {
				p.setStatus(CmFinoFIX.PocketStatus_Retired);
			}
		}
	}

	/**
	 * The following steps shold get the Pocket balance 1. Identify the most
	 * recetly started Txn. Let us call it MR_Transfer 2. Get the balance based
	 * on whether the pocket is source or destination. 3. Add/deduct the
	 * transfer amount based on whether the transaction succeded or failed.
	 * Note: pending transfers are assumed to have succeeded for calculation
	 * purposes. 4. Find all the 'resolve as fail' Txns that happened after the
	 * most recently started Txn and before EOD. Add back this amount to the
	 * pocket balance as this Txn has failed. Note: If the transfer used to
	 * calculate the pocket balance is a 'resolve as fail' transfer, take care
	 * to not include it again in step 4.
	 * 
	 * @param pocket
	 *            The pocket for which the balance needs to be calculated
	 * @param end
	 *            The EOD time
	 * @return
	 * @deprecated use getPocketBalanceAsOf3
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BigDecimal getPocketBalanceAsOf(Pocket pocket, Date end, List<CommodityTransfer> rafTxns) {

		if (null == pocket) {
			return null;
		}

		// STEP 1
		CmFinoFIX.CRCommodityTransfer transferRecord = getMostRecentTransferBefore(pocket, end);

		if (transferRecord == null) {
			return null;
		}

		// STEP 2
		BigDecimal pocketBalance = ZERO;
		if (transferRecord.getPocketBySourcePocketID().getId().equals(pocket.getId())) {
			pocketBalance = transferRecord.getSourcePocketBalance();
		}
		else {
			pocketBalance = transferRecord.getDestPocketBalance();
		}

		/**
		 * STEP 3 Check if the transaction failed. If the transaction failed
		 * then don't have to rededuct the transfer amount
		 */
		BigDecimal transferAmount = transferRecord.getAmount();
		if (transferRecord instanceof CommodityTransfer && !CmFinoFIX.TransferStatus_Completed.equals(transferRecord.getTransferStatus())) {
			transferAmount = ZERO;
		}

		// If the Txn is pending, then we don't add the balance to the
		// destination. We only deduct from the source.
		Long destPocketID = transferRecord.getDestPocketID();
		if (transferRecord instanceof PendingCommodityTransfer && destPocketID != null && destPocketID.equals(pocket.getId())) {
			transferAmount = ZERO;
		}

		// For pending transfer (we assume they succeed) and successful
		// transfers deduct/add the transfer amount to get the final balance.
		if (transferRecord.getPocketBySourcePocketID().getId().equals(pocket.getId())) {
//			pocketBalance -= transferAmount;
			pocketBalance = pocketBalance.subtract(transferAmount);
		}
		else {
//			pocketBalance += transferAmount;
			pocketBalance = pocketBalance.add(transferAmount);
		}

		/**
		 * STEP 4 Add up all the txns that were resolved as fail between the
		 * time the last transfer started and the end time
		 */
		BigDecimal resolveAsFailAmount = ZERO;
		// CommodityTransferDAO ctDAO = new CommodityTransferDAO();
		// List<CommodityTransfer> resolvedAsFailTxns =
		// ctDAO.getResolvedAsFailedTxnsBetween(pocket,
		// transferRecord.getStartTime(), end);
		//
		List<CommodityTransfer> resolvedAsFailTxns = getResolvedAsFailedTxnsBetween(transferRecord.getStartTime(), end, rafTxns);
		if (null != resolvedAsFailTxns) {
			for (CommodityTransfer ct : resolvedAsFailTxns) {
				// If a resolve as failed Txn is what is used to compute the
				// current balance, don't include it again.
				if (ct.getId().equals(transferRecord.getID())) {
					continue;
				}
//				resolveAsFailAmount += ct.getAmount();
				resolveAsFailAmount = resolveAsFailAmount.add(ct.getAmount());
				log.info("RAF Txn = " + ct.getId() + ", Amount = " + ct.getAmount());
			}
		}

		// LogFactory.getLogger().info("Pocket ID = " + pocket.getID());
		// LogFactory.getLogger().info("Transfer Record ID = " +
		// transferRecord.getID());
		// LogFactory.getLogger().info("Pocket Balance After Txn = " +
		// pocketBalance);
		// LogFactory.getLogger().info("Resolve As Fail Amount = " +
		// resolveAsFailAmount);

//		return pocketBalance + resolveAsFailAmount;
		return pocketBalance.add(resolveAsFailAmount);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BigDecimal getPocketBalanceAsOf2(Pocket pocket, Date end) {

		if (null == pocket) {
			return null;
		}

		// STEP 1
		CmFinoFIX.CRCommodityTransfer transferRecord = getMostRecentTransferBefore(pocket, end);

		if (transferRecord == null) {
			return null;
		}

		// STEP 2
		BigDecimal pocketBalance = ZERO;
		if (transferRecord.getPocketBySourcePocketID().getId().equals(pocket.getId())) {
			pocketBalance = transferRecord.getSourcePocketBalance();
		}
		else {
			pocketBalance = transferRecord.getDestPocketBalance();
		}

		/**
		 * STEP 3 Check if the transaction failed. If the transaction failed
		 * then don't have to rededuct the transfer amount
		 */
		BigDecimal transferAmount = transferRecord.getAmount();
		if (transferRecord instanceof CommodityTransfer && !CmFinoFIX.TransferStatus_Completed.equals(transferRecord.getTransferStatus())) {
			transferAmount = ZERO;
		}

		// If the Txn is pending, then we don't add the balance to the
		// destination. We only deduct from the source.
		Long destPocketID = transferRecord.getDestPocketID();
		if (transferRecord instanceof PendingCommodityTransfer && destPocketID != null && destPocketID.equals(pocket.getId())) {
			transferAmount = ZERO;
		}

		// For pending transfer (we assume they succeed) and successful
		// transfers deduct/add the transfer amount to get the final balance.
		if (transferRecord.getPocketBySourcePocketID().getId().equals(pocket.getId())) {
//			pocketBalance -= transferAmount;
			pocketBalance = pocketBalance.subtract(transferAmount);
		}
		else {
//			pocketBalance += transferAmount;
			pocketBalance = pocketBalance.add(transferAmount); 
		}

		/**
		 * STEP 4 Add up all the txns that were resolved as fail between the
		 * time the last transfer started and the end time
		 */
		BigDecimal resolveAsFailAmount = ZERO;
		CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
		List<CommodityTransfer> resolvedAsFailTxns = ctDAO.getResolvedAsFailedTxnsBetween(pocket, transferRecord.getStartTime(), end);

		if (null != resolvedAsFailTxns) {
			for (CommodityTransfer ct : resolvedAsFailTxns) {
				// If a resolve as failed Txn is what is used to compute the
				// current balance, don't include it again.
				if (ct.getId().equals(transferRecord.getID())) {
					continue;
				}
//				resolveAsFailAmount += ct.getAmount();
				resolveAsFailAmount = resolveAsFailAmount.add(ct.getAmount());
				log.info("RAF Txn = " + ct.getId() + ", Amount = " + ct.getAmount());
			}
		}

		// LogFactory.getLogger().info("Pocket ID = " + pocket.getID());
		// LogFactory.getLogger().info("Transfer Record ID = " +
		// transferRecord.getID());
		// LogFactory.getLogger().info("Pocket Balance After Txn = " +
		// pocketBalance);
		// LogFactory.getLogger().info("Resolve As Fail Amount = " +
		// resolveAsFailAmount);

//		return pocketBalance + resolveAsFailAmount;
		return pocketBalance.add(resolveAsFailAmount);
	}

	/**
	 * STEP 1: Find the first txn started after asOfDate. The next Txn as the
	 * accurate balance as of given time. Else go to STEP 2 STEP 2: Not the
	 * current pocket balance. As no Txns have taken place this will most likely
	 * be the balance as of given time. STEP 3: Deduct any RAF Txns between
	 * given time and the time balance is read from DB to get the accurate
	 * balance as of given time.
	 * 
	 * @param pocket
	 * @param asOfDate
	 * @param rafTxns
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BigDecimal getPocketBalanceAsOf3(Pocket pocket, Date asOfDate, List<CommodityTransfer> rafTxns) {
		if (null == pocket) {
			return null;
		}

		BigDecimal pocketBalance = ZERO;
		Date end = new Date();
		// STEP 1: Find the first txn started after asOfDate
		CRCommodityTransfer transfer = getFirstTransferAfter(pocket, asOfDate);
		if (null != transfer) {
			log.info("Pocket ID = " + pocket.getId());
			if (transfer.getPocketBySourcePocketID().getId().equals(pocket.getId())) {
				pocketBalance = transfer.getSourcePocketBalance();
			}
			else if (transfer.getDestPocketID().equals(pocket.getId())) {
				pocketBalance = transfer.getDestPocketBalance();
			}
			end = transfer.getStartTime();
		}
		else {
			pocketBalance = new BigDecimal(pocket.getCurrentbalance());
		}

		// STEP 3: Deduct the RAF between asOfDate and end (the time
		// currentBalance is read from the DB or the time the first txn
		// happened)
		BigDecimal rafAmount = ZERO;
		List<CommodityTransfer> resolvedAsFailTxns = getResolvedAsFailedTxnsBetween(asOfDate, end, rafTxns);
		if (null != resolvedAsFailTxns) {
			for (CommodityTransfer ct : resolvedAsFailTxns) {
//				rafAmount += ct.getAmount();
				rafAmount = rafAmount.add(ct.getAmount());
			}
			log.info("RAF Amount = " + rafAmount);
		}

//		return pocketBalance - rafAmount;
		return pocketBalance.subtract(rafAmount);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<CommodityTransfer> getResolvedAsFailedTxnsBetween(Date start, Date end, List<CommodityTransfer> allRAFList) {

		if (null == allRAFList || allRAFList.size() == 0) {
			return null;
		}

		List<CommodityTransfer> requiredRAFList = new LinkedList<CommodityTransfer>();
		for (CommodityTransfer ct : allRAFList) {
			if (ct.getStarttime().before(start) && ct.getCsractiontime().compareTo(start) >= 0 && ct.getCsractiontime().before(end)) {
				requiredRAFList.add(ct);
			}
		}
		return requiredRAFList;
	}

	// Returns the most recently 'started' transfer for the given pocket before
	// the given time.
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CmFinoFIX.CRCommodityTransfer getMostRecentTransferBefore(Pocket pocket, Date end) {

		PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		PendingCommodityTransfer pendingCommodityTransfer = pctDAO.getLastTransferBefore(pocket, end);

		CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransfer commodityTransfer = ctDAO.getLastTransferBefore(pocket, end);
		CmFinoFIX.CRCommodityTransfer transferRecord = null;

		if (null == commodityTransfer) {
			transferRecord = pendingCommodityTransfer;
		}
		else if (null == pendingCommodityTransfer) {
			transferRecord = commodityTransfer;
		}
		else if (commodityTransfer.getId().longValue() > pendingCommodityTransfer.getId().longValue()) {
			transferRecord = commodityTransfer;
		}
		else {
			transferRecord = pendingCommodityTransfer;
		}

		return transferRecord;
	}

	// Returns the first 'started' transfer for the given pocket after the given
	// time.
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CmFinoFIX.CRCommodityTransfer getFirstTransferAfter(Pocket pocket, Date end) {

		PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		PendingCommodityTransfer pendingCommodityTransfer = pctDAO.getFirstTransferAfter(pocket, end);

		CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransfer commodityTransfer = ctDAO.getFirstTransferAfter(pocket, end);
		CmFinoFIX.CRCommodityTransfer transferRecord = null;

		if (null == commodityTransfer) {
			transferRecord = pendingCommodityTransfer;
		}
		else if (null == pendingCommodityTransfer) {
			transferRecord = commodityTransfer;
		}
		else if (commodityTransfer.getId().longValue() < pendingCommodityTransfer.getId().longValue()) {
			transferRecord = commodityTransfer;
		}
		else {
			transferRecord = pendingCommodityTransfer;
		}

		return transferRecord;
	}

	/**
	 * Generates 16 digit cardpan based on mdn.<br>
	 * Mdn should be of either 10 or 12 digits without the countrycode.
	 * 
	 * @param mdn
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String generateSVAEMoney16DigitCardPAN(String mdn) throws InvalidMDNException, EmptyStringException {
//		mdn = MfinoUtil.normalizeMDN(mdn);
		mdn = subscriberMdnService.denormalizeMDN(mdn);
		if(mdn.length()==0)
			throw new InvalidMDNException("length of mdn is 0");
		String cardPAN=null;
		Random rand = new Random();	
		long randNum=rand.nextLong();
		if(randNum<0)
			randNum = -randNum;
		if(mdn.length()>5){
			cardPAN ="00"+mdn.substring(0,5)+String.valueOf(randNum);
		}else{
			cardPAN ="00"+mdn+String.valueOf(randNum);
		}
		return cardPAN.substring(0,16);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String generateLakupandia16DigitCardPAN(String mdn) throws InvalidMDNException, EmptyStringException {
		
		mdn = subscriberMdnService.denormalizeMDN(mdn);
		
		if(mdn.length()==0)
			throw new InvalidMDNException("length of mdn is 0");
		
		String cardPAN=null;
		Random rand = new Random();	
		long randNum=rand.nextLong();
		
		if(randNum<0)
			randNum = -randNum;
		
		if(mdn.length()>5){
			
			cardPAN ="00"+mdn.substring(0,5)+String.valueOf(randNum);
		
		}else{
			
			cardPAN ="00"+mdn+String.valueOf(randNum);
		}
		
		return cardPAN.substring(0,16);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket createDefaultActivePocket(PocketTemplate pocketTemplate, SubscriberMdn subscriberMdn) {
		Pocket pocket = new Pocket();

		pocket.setPocketTemplate(pocketTemplate);
		pocket.setSubscriberMdn(subscriberMdn);
		pocket.setStatustime(new Timestamp());
		pocket.setStatus(CmFinoFIX.PocketStatus_Active);
		pocket.setIsdefault((short) Boolean.compare(true, false));
		if (subscriberMdn.getSubscriber().getCompany() != null) {
			pocket.setCompany(subscriberMdn.getSubscriber().getCompany());
		}
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		pocketDAO.save(pocket);

		return pocket;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket createDefaultActivePocket(Long pocketTemplateID,
			SubscriberMdn subscriberMDN) {
		PocketTemplateDAO ptDao = DAOFactory.getInstance().getPocketTemplateDao();
		return createDefaultActivePocket(ptDao.getById(pocketTemplateID), subscriberMDN);
	}	
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket createDefaultBankPocket(Long pocketTemplateID,SubscriberMdn subscriberMDN,String cardPan) {
		PocketTemplateDAO ptDao = DAOFactory.getInstance().getPocketTemplateDao();
		PocketTemplate pocketTemplate = ptDao.getById(pocketTemplateID);
		Pocket pocket = new Pocket();
		pocket.setPocketTemplate(pocketTemplate);
		pocket.setSubscriberMdn(subscriberMDN);
		pocket.setStatustime(new Timestamp());
		pocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
		pocket.setIsdefault((short) Boolean.compare(true, false));
		if(cardPan!=null){
		pocket.setCardpan(cardPan);
		}
		if (subscriberMDN.getSubscriber().getCompany() != null) {
			pocket.setCompany(subscriberMDN.getSubscriber().getCompany());
		}
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		pocketDAO.save(pocket);

		return pocket;
		
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)	
	public Pocket createPocket(Long pocketTemplateID,
			SubscriberMdn subscriberMDN, Integer pocketstatus,boolean isDefault,String CardPan) {
		PocketTemplateDAO ptDao = DAOFactory.getInstance().getPocketTemplateDao();
		PocketTemplate pocketTemplate = ptDao.getById(pocketTemplateID);
		
		return createPocket(pocketTemplate, subscriberMDN, pocketstatus, isDefault,CardPan);
		
	}	
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)	
	public Pocket createPocket(PocketTemplate pocketTemplate,
			SubscriberMdn subscriberMDN, Integer pocketstatus,boolean isDefault,String CardPan,String CardAlias){
		Pocket pocket = createPocket(pocketTemplate, subscriberMDN, pocketstatus, isDefault, CardPan);
		pocket.setCardalias(CardAlias);
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		pocketDAO.save(pocket);
		return pocket;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket createPocket(PocketTemplate pocketTemplate,
			SubscriberMdn subscriberMDN, Integer pocketstatus,boolean isDefault,String CardPan) {
				Pocket pocket = new Pocket();
		pocket.setPocketTemplate(pocketTemplate);
		pocket.setSubscriberMdn(subscriberMDN);
		pocket.setStatustime(new Timestamp());
		pocket.setStatus(pocketstatus);
		if(CardPan!=null){
		pocket.setCardpan(CardPan);
		}
		if(isDefault){
		pocket.setIsdefault((short) Boolean.compare(true, false));
		}
		if (subscriberMDN.getSubscriber().getCompany() != null) {
			pocket.setCompany(subscriberMDN.getSubscriber().getCompany());
		}
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		pocketDAO.save(pocket);

		return pocket;
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PocketTemplate getPocketTemplateFromPocketTemplateConfig(Long kycLevel, Boolean isDefault, Integer pocketType, 
			Integer subscriberType, Integer businessPartnerType, Long groupID)
	{
		return getPocketTemplateFromPocketTemplateConfig(kycLevel, isDefault, null, null, pocketType, subscriberType, businessPartnerType, groupID);
	}
	
	/*
	 * If the groupID is null or if there is no pocket template existing for the given groupID and configuration,
	 * then the pocket template with default group(System Group ANY) with the same configuration is returned
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PocketTemplate getPocketTemplateFromPocketTemplateConfig(Long kycLevel, Boolean isDefault, Boolean isSuspensePocket, Boolean isCollectorPocket, 
			Integer pocketType, Integer subscriberType, Integer businessPartnerType, Long groupID)
	{
		PocketTemplateConfigDAO dao = DAOFactory.getInstance().getPocketTemplateConfigDao();
		PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
		if(kycLevel != null)
		{
			query.set_KYCLevel(kycLevel);
		}
		if(isDefault != null)
		{
			query.set_isDefault(isDefault);
		}
		if(isSuspensePocket != null)
		{
			query.set_isSuspensePocket(isSuspensePocket);
		}
		else
		{
			query.set_isSuspensePocket(false);
		}
		if(isCollectorPocket != null)
		{
			query.set_isCollectorPocket(isCollectorPocket);
		}
		else
		{
			query.set_isCollectorPocket(false);
		}
		if(pocketType != null)
		{
			query.set_pocketType(pocketType);
		}
		if(subscriberType != null)
		{
			query.set_subscriberType(subscriberType);
		}
		if(businessPartnerType != null)
		{
			query.set_businessPartnerType(businessPartnerType);
		}
		Groups defaultGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
		if(groupID != null)
		{
			query.set_GroupID(groupID);
		}
		else
		{
			query.set_GroupID(defaultGroup.getId().longValue());
		}
		List<PocketTemplateConfig> results = dao.get(query);
		if (results.size() > 0) {
			return results.get(0).getPocketTemplate();
		}
		/*
		 * If the result set is empty, then check if GroupID is not null and query the db for pocket templates with default group.
		 * If GroupID is null we already queried the db and empty list is returned. so, need not repeat the task again.
		 */
		else if(groupID !=null)
		{
			query.set_GroupID(defaultGroup.getId().longValue());
			results = dao.get(query);
			if (results.size() > 0) {
				return results.get(0).getPocketTemplate();
			}
		}
		return null;
	}
	
	
	/*
	 * This is similar to the above function except that it fetches the bank pocket template for a given configuration.
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PocketTemplate getBankPocketTemplateFromPocketTemplateConfig(Integer bankAccountType, boolean isDefault, Integer subscriberType, Integer businessPartnerType, Long groupID)
	{
		PocketTemplateConfigDAO dao = DAOFactory.getInstance().getPocketTemplateConfigDao();
		PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
		
		query.set_KYCLevel(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue());
		query.set_isDefault(isDefault);
		query.set_pocketType(CmFinoFIX.PocketType_BankAccount);
		
		if(subscriberType != null)
		{
			query.set_subscriberType(subscriberType);
		}
		if(businessPartnerType != null)
		{
			query.set_businessPartnerType(businessPartnerType);
		}
		query.set_isCollectorPocket(Boolean.FALSE);
		query.set_isSuspensePocket(Boolean.FALSE);
		Groups defaultGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
		if(groupID != null)
		{
			query.set_GroupID(groupID);
		}
		else
		{
			query.set_GroupID(defaultGroup.getId().longValue());
		}
		List<PocketTemplateConfig> results = dao.get(query);
		if (results.size() > 0) {
			 Iterator<PocketTemplateConfig> it = results.iterator();
	         while(it.hasNext())
	         {
	        	 PocketTemplateConfig pocketTemplateConfig = it.next();
	        	 if(pocketTemplateConfig.getPocketTemplate().getBankaccountcardtype().equals(bankAccountType))
	        	 {
	        		 return pocketTemplateConfig.getPocketTemplate();
	        	 }
	         }
			
		}
		/*
		 * If the result set is empty, then check if GroupID is not null and query the db for pocket templates with default group.
		 * If GroupID is null we already queried the db and empty list is returned. so, need not repeat the task again.
		 */
		else if(groupID !=null)
		{
			query.set_GroupID(defaultGroup.getId().longValue());
			results = dao.get(query);
			if (results.size() > 0) {
				Iterator<PocketTemplateConfig> it = results.iterator();
		         while(it.hasNext())
		         {
		        	 PocketTemplateConfig pocketTemplateConfig = it.next();
		        	 if(pocketTemplateConfig.getPocketTemplate().getBankaccountcardtype().equals(bankAccountType))
		        	 {
		        		 return pocketTemplateConfig.getPocketTemplate();
		        	 }
		         }
			}
		}
		return null;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean isAllowed(PocketTemplate pocketTemplate,
			SubscriberMdn mdn) {
		KYCLevelDAO kycLevelDAO = DAOFactory.getInstance().getKycLevelDAO();
		List<KYCLevel> levels=kycLevelDAO.getAll();
		Subscriber sub=mdn.getSubscriber();
		
		//check for not to add collector pocket to subscriber
		if(Boolean.valueOf(pocketTemplate.getIscollectorpocket().toString())&&CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
			return false;
		}
		
		//check for not to add suspense pocket to subscriber
		if(Boolean.valueOf(pocketTemplate.getIscollectorpocket().toString())&&CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
			return false;
		}
		
		//check for not to add system pocket to subscriber
		if(Boolean.valueOf(pocketTemplate.getIscollectorpocket().toString())&&CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
			return false;
		}
		return true;
	}

	/**
	 * Returns the Default Pocket for the given Pocket Template for given Partner
	 * @param partner
	 * @param pocketTemplateId
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getDefaultPocket(Partner partner, long pocketTemplateId, boolean isSuspencePocket) {
		log.debug("Getting the Default Pocket of Pocket Template --> " + pocketTemplateId);
		Pocket result = null;
		PocketDAO dao = DAOFactory.getInstance().getPocketDAO();
		PocketQuery query = new PocketQuery();
		
		if (partner != null) {
			Subscriber subscriber = partner.getSubscriber();
			if (subscriber != null) {
				 Set<SubscriberMdn> subscriberMdnCol = subscriber.getSubscriberMdns();
				 if((subscriberMdnCol != null) && (subscriberMdnCol.size() > 0)){
					 Long id = subscriberMdnCol.iterator().next().getId().longValue();
					 query.setMdnIDSearch(id);
				 }
			}
		}
		query.setPocketTemplateID(pocketTemplateId);
		query.setIsDefault(CmFinoFIX.Boolean_True);
		query.setIsSuspencePocketAllowed(isSuspencePocket);
		List<Pocket> lst = dao.get(query);
		if (CollectionUtils.isNotEmpty(lst)) {
			result = lst.get(0);
			log.info("Default Pocket Id -- > " + result.getId());
		}
		
		return result;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getDefaultPocket(Partner partner, long pocketTemplateId) {
		return getDefaultPocket(partner, pocketTemplateId, false);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getDefaultPocket(SubscriberMdn sMDN, String pocketCode){
		log.info("FIXMessageHandler :: getDefaultPocket sMDN="+sMDN+", pocketCode="+pocketCode);
		Pocket subPocket = null;
		if(sMDN==null){
			return null;
		}
			
		//For 2.5 If pocket code is 1 then it is EMoney if it is 2 then Bank.
		if(pocketCode!=null && pocketCode.equals(String.valueOf(2))){
			subPocket = subscriberService.getDefaultPocket(sMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		}
		else if(pocketCode!=null && CmFinoFIX.PocketType_NFC.toString().equals(pocketCode)) {
			subPocket = subscriberService.getDefaultPocket(sMDN.getId().longValue(), CmFinoFIX.PocketType_NFC, CmFinoFIX.Commodity_Money);
		}
		else if(pocketCode!=null && CmFinoFIX.PocketType_LakuPandai.toString().equals(pocketCode)) {
			subPocket = subscriberService.getDefaultPocket(sMDN.getId().longValue(), CmFinoFIX.PocketType_LakuPandai, CmFinoFIX.Commodity_Money);
		}
		else {
			subPocket = subscriberService.getDefaultPocket(sMDN.getId().longValue(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
		} 
		return subPocket;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean checkCount(PocketTemplate pocketTemplate,
			SubscriberMdn subMdn) {
		Set<Pocket> pockets = subMdn.getPockets();
		int count = 0;
		for(Pocket pocket:pockets){
			if(pocketTemplate.equals(pocket.getPocketTemplate())
					&&(CmFinoFIX.PocketStatus_Active.equals(pocket.getStatus())||CmFinoFIX.PocketStatus_Initialized.equals(pocket.getStatus()))){
				count++;
			}
		
	}
		return count<pocketTemplate.getNumberofpocketsallowedformdn();
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<Pocket> get(PocketQuery query) throws MfinoRuntimeException{
		return pocketDao.get(query);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void save(Pocket pocket) throws MfinoRuntimeException{
		pocketDao.save(pocket);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getById(Long pocketId) throws MfinoRuntimeException
	{
		if(pocketId!=null){
			return pocketDao.getById(pocketId);
		}
		else 
			return null;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getByCardPan(String cardPan) throws MfinoRuntimeException
	{	
		PocketQuery query = new PocketQuery();
		query.setCardPan(cardPan);
		List<Pocket> pockets = pocketDao.get(query);
		if (CollectionUtils.isNotEmpty(pockets)) {
    		return pockets.get(0);
    	}
		return null;		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getByCardAlias(String cardAlias) throws MfinoRuntimeException
	{	
		PocketQuery query = new PocketQuery();
		query.setCardAlias(cardAlias);
		List<Pocket> pockets = pocketDao.get(query);
		if (CollectionUtils.isNotEmpty(pockets)) {
    		return pockets.get(0);
    	}
		return null;		
	}
	
	/**
	 * Getting the Suspence pocket for the bulk upload user
	 * @param user
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getSuspencePocket(User user) {
		Pocket result = null;
		if (user != null) {
			Set<Partner> setPartner = user.getPartners();
			if (CollectionUtils.isNotEmpty(setPartner)) {
				Partner partner = setPartner.iterator().next();
				result = getSuspencePocket(partner);
			}
		}
		return result;
	}
	
	/**
	 * Returns the Suspense pocket for the given Partner 
	 * @param partner
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getSuspencePocket(Partner partner) {
		Pocket result = null;
		if (partner != null) {
			long suspencePocketTemplateId = systemParametersService.getLong(SystemParameterKeys.SUSPENCE_POCKET_TEMPLATE_ID_KEY);
			result = getDefaultPocket(partner, suspencePocketTemplateId, true);
		}
		return result;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public Pocket getById(Long pocketId,LockMode lockMode) throws MfinoRuntimeException
	{
		if(pocketId!=null){
			return pocketDao.getById(pocketId,lockMode);
		}
		else 
			return null;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getNFCPocket(SubscriberMdn subscriberMDN, String cardPAN) {
		PocketQuery query = new PocketQuery();
		query.setCardPan(cardPAN);
		query.setMdnIDSearch(subscriberMDN.getId().longValue());
		query.setPocketType(CmFinoFIX.PocketType_NFC);		
		List<Pocket> list = pocketDao.get(query);
		if(!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getPocketAferEvicting(Pocket pocket) {		
		return pocketDao.getPocketAfterEvicting(pocket);
	}
	
	/**
	 * Returns the Default bank pockets for the given MDN list
	 * @param mdnlist
	 * @return
	 */
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public List<Pocket> getDefaultBankPocketByMdnList(List<Long> mdnlist) {
		if (CollectionUtils.isNotEmpty(mdnlist)) {
			return pocketDao.getDefaultBankPocketByMdnList(mdnlist);
		}
		else {
			return null;
		}
	}
	
	public List<Long> getLakuPandaiPockets() {
		return pocketDao.getLakuPandaiPockets();
	}
}