package com.mfino.mce.backend.impl;

import static com.mfino.mce.backend.util.BackendUtil.getUiCategory;
import static com.mfino.mce.core.util.MCEUtil.isNullOrEmpty;
import static com.mfino.mce.core.util.MCEUtil.safeString;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.CommodityTransferSequenceGenerator;
import com.mfino.mce.backend.CommodityTransferService;

/**
 * 
 * @author sasidhar
 *
 */
public class CommodityTransferServiceImpl extends BaseServiceImpl implements CommodityTransferService{
	
	private Integer timeout;
	
	private CommodityTransferSequenceGenerator commodityTransferSequenceGenerator;
	
	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, String bucketType, Integer billingType, 
			Integer initialTransferStatus) {
		return createPCT(requestFix, objSourceSubscriber, objDestSubscriber, objSourcePocket, objDestPocket, objSourceSubMdn, objDestSubMdn, sourceMessage, amount, charges, 
				null, bucketType, billingType, initialTransferStatus);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, 
			Integer billingType, Integer initialTransferStatus) {
		return createPCT(requestFix, objSourceSubscriber, objDestSubscriber, objSourcePocket, objDestPocket, objSourceSubMdn, objDestSubMdn, sourceMessage, amount, charges, 
				taxAmount, bucketType, billingType, initialTransferStatus, null);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, 
			Integer billingType, Integer initialTransferStatus, String destinationBankAccountNo) {
		return createPCT(requestFix, objSourceSubscriber, objDestSubscriber, objSourcePocket, objDestPocket, objSourceSubMdn, objDestSubMdn, sourceMessage, amount, charges, 
				taxAmount, bucketType, billingType, initialTransferStatus, destinationBankAccountNo, null);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, 
			Integer billingType, Integer initialTransferStatus, String destinationBankAccountNo, String sourceBankAccountNo)
	{
		log.info("CommodityTransferServiceImpl : createPCT()");
		PendingCommodityTransfer pct = new PendingCommodityTransfer();
	
		if(CmFinoFIX.PocketType_SVA.equals(objDestPocket.getPocketTemplate().getType())){
			pct.setDestBankAccountName((safeString(objDestSubscriber.getFirstName()) + " " + safeString(objDestSubscriber.getLastName())));
			
			if(CmFinoFIX.PocketType_SVA.equals(objDestPocket.getPocketTemplate().getType())){
				
			}
		}

		pct.setTransferStatus(CmFinoFIX.TransferStatus_Initialized);
		pct.setDestMDN(objDestSubMdn.getMDN());
		pct.setDestPocketID(objDestPocket.getID());
		pct.setDestPocketType(objDestPocket.getPocketTemplate().getType());
		pct.setDestSubscriberID(objDestSubscriber.getID());
		pct.setDestSubscriberName((safeString(objDestSubscriber.getFirstName()) + " " + safeString(objDestSubscriber.getLastName())));
		
		pct.setPocketBySourcePocketID(objSourcePocket);
		
		if(objDestPocket.getPocketTemplate().getAllowance() != null){
			pct.setDestPocketAllowance(objDestPocket.getPocketTemplate().getAllowance());
		}
		
		pct.setSourceMessage(safeString(sourceMessage));
		
		if(objDestPocket.getPocketTemplate().getType().intValue() ==  CmFinoFIX.PocketType_BankAccount || objDestPocket.getPocketTemplate().getType().intValue() == CmFinoFIX.PocketType_NFC){
			if(StringUtils.isNotBlank(destinationBankAccountNo))
			{
				pct.setDestCardPAN(destinationBankAccountNo);
			}
			else
			{
				pct.setDestCardPAN(safeString(objDestPocket.getCardPAN()));
			}
		}
		else if(objDestPocket.getPocketTemplate().getType().intValue() ==  CmFinoFIX.PocketType_SVA){
			
			pct.setDestPocketBalance(objDestPocket.getCurrentBalance());
			
			//the source and dest pocket template should be the same
			if(objDestPocket.getPocketTemplate().getCommodity().intValue() == CmFinoFIX.Commodity_Money) {
//				pct.setDestCardPAN(MCEUtil.SOURCE_CARD_NUMBER_OMNIBUS);
				pct.setDestCardPAN(safeString(objDestPocket.getCardPAN()));
			}
		}
		else if(objDestPocket.getPocketTemplate().getType().intValue() ==  CmFinoFIX.PocketType_BOBAccount){
			if(objDestPocket.getPocketTemplate().getOperatorCode() != null){
				pct.setOperatorCode(objDestPocket.getPocketTemplate().getOperatorCode());
			}
			else{
				// TODO set default operator code ask sridhar.
			}
		}
		
		//second part starts from here
		
		pct.setUpdatedBy(safeString(requestFix.getOperatorName()));
		pct.setCreatedBy(safeString(requestFix.getOperatorName()));
	//	pct.setExpirationTimeout(MCEUtil.timeOut); //TODO change this later put in every fix message
		pct.setExpirationTimeout(timeout);
		
		if(!isNullOrEmpty(requestFix.getSourceIP())){
			pct.setSourceIP(requestFix.getSourceIP());
		}
		if(!isNullOrEmpty(requestFix.getWebClientIP())){
			pct.setWebClientIP(requestFix.getWebClientIP());
		}
		if(!isNullOrEmpty(requestFix.getServletPath())){
			pct.setServletPath(requestFix.getServletPath());
		}
		
		if(objSourceSubscriber.getCompany() != null){
			pct.setCompany(objSourceSubscriber.getCompany());
		}
		
		pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_Inititalized);
		pct.setAmount(amount);
		if(charges != null){
			pct.setCharges(charges);
		}
		else{
			pct.setCharges(BigDecimal.valueOf(0));
		}
		if (taxAmount != null) {
			pct.setTaxAmount(taxAmount);
		} else {
			pct.setTaxAmount(BigDecimal.ZERO);
		}
		pct.setMsgType(requestFix.getMessageType());

		pct.setBucketType(safeString(bucketType));
		pct.setCommodity(objSourcePocket.getPocketTemplate().getCommodity());
		pct.setCurrency(objSourceSubscriber.getCurrency());

		pct.setDestMDN(objDestSubMdn.getMDN());
		pct.setmFinoServiceProviderByMSPID(coreDataWrapper.getMSPID(requestFix.getMSPID()));
		
		if(objSourcePocket.getPocketTemplate().getType().intValue() ==  CmFinoFIX.PocketType_BOBAccount){
			if(objSourcePocket.getPocketTemplate().getOperatorCode() != null){
				pct.setOperatorCode(objSourcePocket.getPocketTemplate().getOperatorCode());
			}
			else{
				// TODO set default operator code ask sridhar.
			}
		}
		
		if(objSourcePocket.getPocketTemplate().getBankCode() != null){
			pct.setBankCode(objSourcePocket.getPocketTemplate().getBankCode());
		}
		

		pct.setSourceApplication(requestFix.getSourceApplication());
		pct.setBillingType(billingType);
		pct.setSourceMDN(objSourceSubMdn.getMDN());
		pct.setSourcePocketType(objSourcePocket.getPocketTemplate().getType());
		pct.setSubscriberBySourceSubscriberID(objSourceSubscriber);
		pct.setSubscriberMDNBySourceMDNID(objSourceSubMdn);
		pct.setSourceSubscriberName(safeString(objSourceSubscriber.getFirstName()) + " " + safeString(objSourceSubscriber.getLastName()));
		pct.setStartTime(new Timestamp());
		//TransactionsLog tLog =  coreDataWrapper.getTransactionsLogById(requestFix.getTransactionID());
		TransactionsLog tLog = new TransactionsLog();
		tLog.setID(requestFix.getTransactionID());
		pct.setTransactionsLogByTransactionID(tLog);
		pct.setTransferStatus(initialTransferStatus);
		pct.setLocalRevertRequired(true);
		
		if(objSourcePocket.getPocketTemplate().getType().intValue()	==	CmFinoFIX.PocketType_BankAccount) {
			if(StringUtils.isNotBlank(sourceBankAccountNo)) {
				pct.setSourceCardPAN(sourceBankAccountNo);
			}
			else {
				pct.setSourceCardPAN(safeString(objSourcePocket.getCardPAN()));
			}
		}

		if(objSourcePocket.getPocketTemplate().getAllowance() != null)
			pct.setSourcePocketAllowance(objSourcePocket.getPocketTemplate().getAllowance());

		if(objSourcePocket.getPocketTemplate().getType().intValue()	==	CmFinoFIX.PocketType_SVA)
		{
			if(objSourcePocket.getCurrentBalance() != null)
				pct.setSourcePocketBalance(objSourcePocket.getCurrentBalance());
			else
				pct.setSourcePocketBalance(BigDecimal.valueOf(0));

			if(pct.getCommodity().intValue() == CmFinoFIX.Commodity_Money)
			{
				/*pct.setSourceCardPAN(MCEUtil.SOURCE_CARD_NUMBER_OMNIBUS); //TODO check with sridhar
				pct.setBankCode(MCEUtil.SOURCE_BANK_CODE_FOR_OMNIBUS);*/
				pct.setSourceCardPAN(safeString(objSourcePocket.getCardPAN())); 
				pct.setBankCode(objSourcePocket.getPocketTemplate().getBankCode());
			}
		}
		
		pct.setUICategory(getUiCategory(requestFix, objSourcePocket, objDestPocket));
		
		//TODO code for has similar records using channel and uicategory
		try{
			pct.setID(commodityTransferSequenceGenerator.getNextTransferID());
			coreDataWrapper.save(pct);
		}catch(Exception exp){
			log.error(exp.getMessage(), exp);
			throw new BackendRuntimeException(exp);
		}
		
		ChargeTxnCommodityTransferMap txnTransferMap = new ChargeTxnCommodityTransferMap();
		txnTransferMap.setCommodityTransferID(pct.getID());
		txnTransferMap.setSctlId(requestFix.getServiceChargeTransactionLogID());
		
		ChargeTxnCommodityTransferMapDAO txnTransferMapDAO = DAOFactory.getInstance().getTxnTransferMap();
		txnTransferMapDAO.save(txnTransferMap);
		return pct;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CommodityTransfer movePctToCt(PendingCommodityTransfer pct) {
		//log.info("CommodityTransferServiceImpl : movePctToCt()");
		CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransfer existingCT = ctDAO.getById(pct.getID());
		if(existingCT != null){
			//this method is used for only log purposes
			isExistingCtDifferentFromPct(existingCT,pct);
			return null;
		}else{
			log.info(String.format("CommodityTransferServiceImpl : movePctToCt : Moving pct(ID: %d) to ct, as record is not existing in Ct",pct.getID()));
			CommodityTransfer ct = new CommodityTransfer();
			
			ClassMetadata classMetadata = getSessionFactory().getClassMetadata(CommodityTransfer.class);
			ct.copy(pct,classMetadata);
			ct.setID(pct.getID());
			coreDataWrapper.save(ct);		
			coreDataWrapper.delete(pct);
			return ct; 
		}
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer getPendingCT(Long sctlId) {
		log.debug("CommodityTransferServiceImpl :: getPendingCT BEGIN sctlId="+sctlId);
		PendingCommodityTransfer pendingCT = null;
		
		List<ChargeTxnCommodityTransferMap> ctxnMap = coreDataWrapper.getBySctlID(sctlId);
		
		for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
			pendingCT = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
			
			if(pendingCT != null) break;
		}
		
		log.debug("CommodityTransferServiceImpl :: getPendingCT END");
		return pendingCT;
	}

	public CommodityTransferSequenceGenerator getCommodityTransferSequenceGenerator() {
		return commodityTransferSequenceGenerator;
	}

	public void setCommodityTransferSequenceGenerator(CommodityTransferSequenceGenerator commodityTransferSequenceGenerator) {
		this.commodityTransferSequenceGenerator = commodityTransferSequenceGenerator;
	}
	
	private boolean isExistingCtDifferentFromPct(CommodityTransfer existingCT, PendingCommodityTransfer pct){
		boolean different = false;
		if(!areLongsEqual(pct.getTransactionsLogByTransactionID().getID(), existingCT.getTransactionsLogByTransactionID().getID())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in TransactionID",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areIntegersEqual(pct.getMsgType(),existingCT.getMsgType())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in MsgType",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areIntegersEqual(pct.getUICategory(),existingCT.getUICategory())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in UICategory",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areIntegersEqual(pct.getNotificationCode(),existingCT.getNotificationCode())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in NotificationCode",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areStringsEqual(pct.getSourceMDN(),existingCT.getSourceMDN())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourceMDN",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areLongsEqual(pct.getPocketBySourcePocketID().getID(),existingCT.getPocketBySourcePocketID().getID())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourcePocketID",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areStringsEqual(pct.getSourceMessage(),existingCT.getSourceMessage())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourceMessage",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areStringsEqual(pct.getDestMDN(),existingCT.getDestMDN())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in DestMDN",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areLongsEqual(pct.getDestPocketID(),existingCT.getDestPocketID())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in DestPocketID",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areBigDecimalsEqual(pct.getAmount(),existingCT.getAmount())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in Amount",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areBigDecimalsEqual(pct.getCharges(),existingCT.getCharges())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in charges",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areBigDecimalsEqual(pct.getTaxAmount(),existingCT.getTaxAmount())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in taxAmount",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(!areIntegersEqual(pct.getSourceApplication(),existingCT.getSourceApplication())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourceApplication",pct.getID(),existingCT.getID()));
			different = true;
		}
		if(different == true){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ. So, Ingnoring this PCT to Move to CT",pct.getID(),existingCT.getID()));
		}else{
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) are Equal. So, Ingnoring this PCT to Move to CT as they are equal",pct.getID(),existingCT.getID()));
		}
		return different;
	}
	
	private boolean areIntegersEqual(Integer a,Integer b){
		if(a == null && b == null){
			return true;
		}
		if(a != null && b != null && a.equals(b)){
			return true;
		}
		return false;
	}
	
	private boolean areLongsEqual(Long a,Long b){
		if(a == null && b == null){
			return true;
		}
		if(a != null && b != null && a.equals(b)){
			return true;
		}
		return false;
	}
	
	private boolean areBigDecimalsEqual(BigDecimal a,BigDecimal b){
		if(a == null && b == null){
			return true;
		}
		if(a != null && b != null && a.equals(b)){
			return true;
		}
		return false;
	}
	
	private boolean areStringsEqual(String a,String b){
		if(a == null && b == null){
			return true;
		}
		if(a != null && b != null && a.equalsIgnoreCase(b)){
			return true;
		}
		return false;
	}
}
