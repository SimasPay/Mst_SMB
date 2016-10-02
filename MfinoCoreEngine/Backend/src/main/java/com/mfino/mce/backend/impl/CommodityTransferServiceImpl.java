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
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
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
			SubscriberMdn objSourceSubMdn, SubscriberMdn objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, String bucketType, Integer billingType, 
			Integer initialTransferStatus) {
		return createPCT(requestFix, objSourceSubscriber, objDestSubscriber, objSourcePocket, objDestPocket, objSourceSubMdn, objDestSubMdn, sourceMessage, amount, charges, 
				null, bucketType, billingType, initialTransferStatus);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMdn objSourceSubMdn, SubscriberMdn objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, 
			Integer billingType, Integer initialTransferStatus) {
		return createPCT(requestFix, objSourceSubscriber, objDestSubscriber, objSourcePocket, objDestPocket, objSourceSubMdn, objDestSubMdn, sourceMessage, amount, charges, 
				taxAmount, bucketType, billingType, initialTransferStatus, null);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMdn objSourceSubMdn, SubscriberMdn objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, 
			Integer billingType, Integer initialTransferStatus, String destinationBankAccountNo) {
		return createPCT(requestFix, objSourceSubscriber, objDestSubscriber, objSourcePocket, objDestPocket, objSourceSubMdn, objDestSubMdn, sourceMessage, amount, charges, 
				taxAmount, bucketType, billingType, initialTransferStatus, destinationBankAccountNo, null);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMdn objSourceSubMdn, SubscriberMdn objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, 
			Integer billingType, Integer initialTransferStatus, String destinationBankAccountNo, String sourceBankAccountNo)
	{
		log.info("CommodityTransferServiceImpl : createPCT()");
		PendingCommodityTransfer pct = new PendingCommodityTransfer();
	
		if(CmFinoFIX.PocketType_SVA.equals(objDestPocket.getPocketTemplate().getType())){
			pct.setDestbankaccountname((safeString(objDestSubscriber.getFirstname()) + " " + safeString(objDestSubscriber.getLastname())));
			
			if(CmFinoFIX.PocketType_SVA.equals(objDestPocket.getPocketTemplate().getType())){
				
			}
		}

		pct.setTransferstatus(CmFinoFIX.TransferStatus_Initialized);
		pct.setDestmdn(objDestSubMdn.getMdn());
		pct.setDestpocketid(objDestPocket.getId());
		pct.setDestpockettype(objDestPocket.getPocketTemplate().getType());
		pct.setDestsubscriberid(objDestSubscriber.getId());
		pct.setDestsubscribername((safeString(objDestSubscriber.getFirstname()) + " " + safeString(objDestSubscriber.getLastname())));
		
		pct.setPocket(objSourcePocket);
		
		if(objDestPocket.getPocketTemplate().getAllowance() != null){
			pct.setDestpocketallowance(objDestPocket.getPocketTemplate().getAllowance());
		}
		
		pct.setSourcemessage(safeString(sourceMessage));
		
		if(objDestPocket.getPocketTemplate().getType() ==  CmFinoFIX.PocketType_BankAccount || objDestPocket.getPocketTemplate().getType().intValue() == CmFinoFIX.PocketType_NFC){
			if(StringUtils.isNotBlank(destinationBankAccountNo))
			{
				pct.setDestcardpan(destinationBankAccountNo);
			}
			else
			{
				pct.setDestcardpan(safeString(objDestPocket.getCardpan()));
			}
		}
		else if(objDestPocket.getPocketTemplate().getType() ==  CmFinoFIX.PocketType_SVA){
			
			pct.setDestpocketbalance(objDestPocket.getCurrentbalance());
			
			//the source and dest pocket template should be the same
			if(objDestPocket.getPocketTemplate().getCommodity() == CmFinoFIX.Commodity_Money) {
//				pct.setDestCardPAN(MCEUtil.SOURCE_CARD_NUMBER_OMNIBUS);
				pct.setDestcardpan(safeString(objDestPocket.getCardpan()));
			}
		}
		else if(objDestPocket.getPocketTemplate().getType() ==  CmFinoFIX.PocketType_BOBAccount){
			if(objDestPocket.getPocketTemplate().getOperatorcode() != null){
				pct.setOperatorcode(objDestPocket.getPocketTemplate().getOperatorcode());
			}
			else{
				// TODO set default operator code ask sridhar.
			}
		}
		
		//second part starts from here
		
		pct.setUpdatedby(safeString(requestFix.getOperatorName()));
		pct.setCreatedby(safeString(requestFix.getOperatorName()));
	//	pct.setExpirationTimeout(MCEUtil.timeOut); //TODO change this later put in every fix message
		pct.setExpirationtimeout(timeout);
		
		if(!isNullOrEmpty(requestFix.getSourceIP())){
			pct.setSourceip(requestFix.getSourceIP());
		}
		if(!isNullOrEmpty(requestFix.getWebClientIP())){
			pct.setWebclientip(requestFix.getWebClientIP());
		}
		if(!isNullOrEmpty(requestFix.getServletPath())){
			pct.setServletpath(requestFix.getServletPath());
		}
		
		if(objSourceSubscriber.getCompany() != null){
			pct.setCompany(objSourceSubscriber.getCompany());
		}
		
		pct.setTransferfailurereason(Long.valueOf(CmFinoFIX.TransferFailureReason_Inititalized));
		pct.setAmount(amount);
		if(charges != null){
			pct.setCharges(charges);
		}
		else{
			pct.setCharges(BigDecimal.valueOf(0));
		}
		if (taxAmount != null) {
			pct.setTaxamount(taxAmount);
		} else {
			pct.setTaxamount(BigDecimal.ZERO);
		}
		pct.setMsgtype(requestFix.getMessageType());

		pct.setBuckettype(safeString(bucketType));
		pct.setCommodity(objSourcePocket.getPocketTemplate().getCommodity());
		pct.setCurrency(objSourceSubscriber.getCurrency());

		pct.setDestmdn(objDestSubMdn.getMdn());
		pct.setmFinoServiceProviderByMSPID(coreDataWrapper.getMSPID(requestFix.getMSPID()));
		
		if(objSourcePocket.getPocketTemplate().getType() ==  CmFinoFIX.PocketType_BOBAccount){
			if(objSourcePocket.getPocketTemplate().getOperatorcode() != null){
				pct.setOperatorcode(objSourcePocket.getPocketTemplate().getOperatorcode());
			}
			else{
				// TODO set default operator code ask sridhar.
			}
		}
		
		if(objSourcePocket.getPocketTemplate().getBankcode() != null){
			pct.setBankcode(objSourcePocket.getPocketTemplate().getBankcode());
		}
		

		pct.setSourceapplication(requestFix.getSourceApplication());
		pct.setBillingtype(Long.valueOf(billingType));
		pct.setSourcemdn(objSourceSubMdn.getMdn());
		pct.setSourcepockettype(objSourcePocket.getPocketTemplate().getType());
		pct.setSubscriber(objSourceSubscriber);
		pct.setSubscriberMdn(objSourceSubMdn);
		pct.setSourcesubscribername(safeString(objSourceSubscriber.getFirstname()) + " " + safeString(objSourceSubscriber.getLastname()));
		pct.setStarttime(new Timestamp());
		//TransactionLog tLog =  coreDataWrapper.getTransactionsLogById(requestFix.getTransactionID());
		TransactionLog tLog = new TransactionLog();
		tLog.setId(new BigDecimal(requestFix.getTransactionID()));
		pct.setTransactionLog(tLog);
		pct.setTransferstatus((int)initialTransferStatus);
		pct.setLocalrevertrequired(true);
		
		if(objSourcePocket.getPocketTemplate().getType()	==	CmFinoFIX.PocketType_BankAccount) {
			if(StringUtils.isNotBlank(sourceBankAccountNo)) {
				pct.setSourcecardpan(sourceBankAccountNo);
			}
			else {
				pct.setSourcecardpan(safeString(objSourcePocket.getCardpan()));
			}
		}

		if(objSourcePocket.getPocketTemplate().getAllowance() != null)
			pct.setSourcepocketallowance(objSourcePocket.getPocketTemplate().getAllowance());

		if(objSourcePocket.getPocketTemplate().getType()	==	CmFinoFIX.PocketType_SVA)
		{
			if(objSourcePocket.getCurrentbalance() != null)
				pct.setSourcepocketbalance(objSourcePocket.getCurrentbalance());
			else
				pct.setSourcepocketbalance(String.valueOf(BigDecimal.valueOf(0)));

			if(pct.getCommodity() == CmFinoFIX.Commodity_Money)
			{
				/*pct.setSourceCardPAN(MCEUtil.SOURCE_CARD_NUMBER_OMNIBUS); //TODO check with sridhar
				pct.setBankCode(MCEUtil.SOURCE_BANK_CODE_FOR_OMNIBUS);*/
				pct.setSourcecardpan(safeString(objSourcePocket.getCardpan())); 
				pct.setBankcode(objSourcePocket.getPocketTemplate().getBankcode());
			}
		}
		
		pct.setUicategory(Long.valueOf(getUiCategory(requestFix, objSourcePocket, objDestPocket)));
		
		//TODO code for has similar records using channel and uicategory
		try{
			pct.setId(new BigDecimal(commodityTransferSequenceGenerator.getNextTransferID()));
			coreDataWrapper.save(pct);
		}catch(Exception exp){
			log.error(exp.getMessage(), exp);
			throw new BackendRuntimeException(exp);
		}
		
		ChargeTxnCommodityTransferMap txnTransferMap = new ChargeTxnCommodityTransferMap();
		txnTransferMap.setCommoditytransferid(pct.getId().longValue());
		txnTransferMap.setSctlid(requestFix.getServiceChargeTransactionLogID().longValue());
		
		ChargeTxnCommodityTransferMapDAO txnTransferMapDAO = DAOFactory.getInstance().getTxnTransferMap();
		txnTransferMapDAO.save(txnTransferMap);
		return pct;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CommodityTransfer movePctToCt(PendingCommodityTransfer pct) {
		//log.info("CommodityTransferServiceImpl : movePctToCt()");
		CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransfer existingCT = ctDAO.getById(pct.getId().longValue());
		if(existingCT != null){
			//this method is used for only log purposes
			isExistingCtDifferentFromPct(existingCT,pct);
			return null;
		}else{
			log.info(String.format("CommodityTransferServiceImpl : movePctToCt : Moving pct(ID: %d) to ct, as record is not existing in Ct",pct.getId()));
			CommodityTransfer ct = new CommodityTransfer();
			
			ClassMetadata classMetadata = getSessionFactory().getClassMetadata(CommodityTransfer.class);
			ct.copy(pct,classMetadata);
			ct.setId(pct.getId());
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
			pendingCT = coreDataWrapper.getPCTById(ctxn.getCommoditytransferid().longValue());
			
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
		if(!areLongsEqual(pct.getTransactionLog().getId().longValue(), existingCT.getTransactionLog().getId().longValue())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in TransactionID",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areIntegersEqual((int)pct.getMsgtype(),(int)existingCT.getMsgtype())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in MsgType",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areIntegersEqual(pct.getUicategory().intValue(),existingCT.getUicategory().intValue())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in UICategory",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areIntegersEqual(pct.getNotificationcode().intValue(),existingCT.getNotificationcode().intValue())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in NotificationCode",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areStringsEqual(pct.getSourcemdn(),existingCT.getSourcemdn())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourceMDN",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areLongsEqual(pct.getPocket().getId().longValue(),existingCT.getPocket().getId().longValue())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourcePocketID",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areStringsEqual(pct.getSourcemessage(),existingCT.getSourcemessage())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourceMessage",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areStringsEqual(pct.getDestmdn(),existingCT.getDestmdn())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in DestMDN",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areLongsEqual(pct.getDestpocketid().longValue(),existingCT.getDestpocketid().longValue())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in DestPocketID",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areBigDecimalsEqual(pct.getAmount(),existingCT.getAmount())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in Amount",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areBigDecimalsEqual(pct.getCharges(),existingCT.getCharges())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in charges",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areBigDecimalsEqual(pct.getTaxamount(),existingCT.getTaxamount())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in taxAmount",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(!areIntegersEqual((int)pct.getSourceapplication(),(int)existingCT.getSourceapplication())){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ in sourceApplication",pct.getId(),existingCT.getId()));
			different = true;
		}
		if(different == true){
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) differ. So, Ingnoring this PCT to Move to CT",pct.getId(),existingCT.getId()));
		}else{
			log.error(String.format("CommodityTransferServiceImpl : movePctToCt : Error moving PCT to CT : pct(ID: %d) and existingCt(ID: %d) are Equal. So, Ingnoring this PCT to Move to CT as they are equal",pct.getId(),existingCT.getId()));
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
