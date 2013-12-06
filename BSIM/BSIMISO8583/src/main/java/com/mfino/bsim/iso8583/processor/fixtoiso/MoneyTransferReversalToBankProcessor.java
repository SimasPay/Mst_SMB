package com.mfino.bsim.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other1;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.FixToISOUtil;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.service.SubscriberService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class MoneyTransferReversalToBankProcessor extends BankRequestProcessor {

	public Log log = LogFactory.getLog(this.getClass());
	
	public MoneyTransferReversalToBankProcessor() {
		try {
			isoMsg.setMTI("0420");
		}
		catch (ISOException ex) {
			log.error(ex);
		}
	}
	
	private SubscriberService subscriberService;
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	
	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		
		CMMoneyTransferReversalToBank msg = (CMMoneyTransferReversalToBank)fixmsg;

		// use the MDN of the global account
		String mdn = msg.getSourceMDNToUseForBank();
		if (mdn == null) {
			mdn = msg.getSourceMDN();
		}

		try {
			String mpan = MfinoUtil.CheckDigitCalculation(msg.getSourceMDN());
            isoMsg.set(2, mpan);
            String defaultDE3=ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;
            Pocket sourcePocket = subscriberService.getDefaultPocket(msg.getSourceMDN(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
            PocketTemplate pocketTemplate = null;
            Integer pocketTempType = null;
            if(sourcePocket!=null){
            pocketTemplate = sourcePocket.getPocketTemplate();
			pocketTempType = pocketTemplate.getBankAccountCardType();
			if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
				defaultDE3="49"+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+"00"; ;
			}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
				defaultDE3="49"+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+"00";
			}
            }
			isoMsg.set(3,defaultDE3);// default de-3 will be overwritten based on destination ac type
			log.info("MoneyTransferReversalToBankProcessor :: process default " + defaultDE3);
			String processingCode=null;
            IntegrationSummaryDao isDAO  = DAOFactory.getInstance().getIntegrationSummaryDao();
            IntegrationSummaryQuery isQuery = new IntegrationSummaryQuery();
            ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
            ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
            ServiceChargeTransactionLog sctl = null;
            Long transferID = msg.getTransferID();
            log.info("MoneyTransferReversalToBankProcessor :: transferID msg.getTransferID()"+transferID);
            TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
            IntegrationSummary iSummary = null;
            Long sctlID;
            String reconciliationID1 = null;
            if(transferID!=null){
            	sctlQuery.setTransferID(transferID);
            	log.info("MoneyTransferReversalToBankProcessor :: process Transfer ID :"+transferID);
            	List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
            	if(CollectionUtils.isNotEmpty(list)){
            		sctl = list.get(0);
            		sctlID = sctl.getID();
            		log.info("MoneyTransferReversalToBankProcessor :: process Sctl ID :"+sctlID);
            		isQuery.setSctlID(sctlID);
            		List<IntegrationSummary> isList = isDAO.get(isQuery);
            		if(CollectionUtils.isNotEmpty(isList)){
            			iSummary = isList.get(0);
            			reconciliationID1 = iSummary.getReconcilationID1();
            			log.info("MoneyTransferReversalToBankProcessor :: process ReconciliationID1 :"+reconciliationID1);
            			log.info("MoneyTransferReversalToBankProcessor :: Dumping message fields :" + msg.DumpFields());
            			log.info("MoneyTransferReversalToBankProcessor :: SourceMDN " + msg.getSourceMDN());
            			log.info("MoneyTransferReversalToBankProcessor :: source Pocket" + sourcePocket.DumpFields());
            			if(sourcePocket!=null && StringUtils.isNotBlank(reconciliationID1))
            				{
            				log.info("pocketTemplate.getBankAccountCardType() "  + pocketTemplate.getBankAccountCardType());
            				log.info("dumping pocketemplate fields " + pocketTemplate.DumpFields());
            				if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
            					processingCode="49"+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+reconciliationID1 ;
            				}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
            					processingCode="49"+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+reconciliationID1;
            				}
            				log.info("MoneyTransferReversalToBankProcessor :: process Setting ProcessingCode :"+processingCode+" in DE-3");
            				isoMsg.set(3,processingCode);
            			}
                		
            		}	   		
            	}
            }

			long amount = msg.getAmount().longValue()*(100);
			isoMsg.set(4, StringUtilities.leftPadWithCharacter(amount+ "", 18, "0")); 
			
			Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
			stan = stan % 1000000;
			String paddedSTAN = FixToISOUtil.padOnLeft(stan.toString(), '0', 6);

			// create a new timestamp for the reversal as we cannot use the one
			// from moneytransferMessage
			Timestamp ts = DateTimeUtil.getGMTTime();
			Timestamp localTS = DateTimeUtil.getLocalTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts));
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(26,constantFieldsMap.get("26"));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, constantFieldsMap.get("32"));// 33
			//isoMsg.set(34, msg.getSourceCardPAN());//trac data
			isoMsg.set(37,StringUtilities.leftPadWithCharacter(msg.getBankSystemTraceAuditNumber(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, msg.getSourceMDN());
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(48, msg.getTransactionID().toString());
			String reversalInfoStr = "0200" + paddedSTAN;
			//to show DE-90 from original transfer time for money transfer reversal type. 
			sctlID = msg.getServiceChargeTransactionLogID();
			sctl = sctlDAO.getById(sctlID);
			TransactionType ttType = ttDAO.getById(sctl.getTransactionTypeID());
			log.info("MoneyTransferReversalToBankProcessor :: process TTtype ID :" + ttType.getID());
			if(ttType.getTransactionName().equalsIgnoreCase(ServiceAndTransactionConstants.TRANSACTION_TRANSFER)){
        		isQuery.setSctlID(sctlID);
        		List<IntegrationSummary> isList = isDAO.get(isQuery);
        		if(CollectionUtils.isNotEmpty(isList)){
        				iSummary = isList.get(0);
            			String reconciliationID3 = iSummary.getReconcilationID3();
            			log.info("MoneyTransferReversalToBankProcessor :: process ReconciliationID3 : " + reconciliationID3);
            			if(StringUtils.isNotBlank(reconciliationID3)){
            			reversalInfoStr = reversalInfoStr+ reconciliationID3;
            			}else{
            				reversalInfoStr = reversalInfoStr+ DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime());
            			}
			}	
			}else{
			reversalInfoStr = reversalInfoStr+ DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime());
			}
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("32"), '0', 11);
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("32"), '0', 11);
			isoMsg.set(90, reversalInfoStr);
			
			isoMsg.set(100, msg.getBankCode().toString());
			isoMsg.set(102, msg.getSourceCardPAN()); 
			isoMsg.set(103, msg.getDestCardPAN());
			if(msg.getLanguage().equals(0))
				   isoMsg.set(121,constantFieldsMap.get("english"));
				else
				   isoMsg.set(121,constantFieldsMap.get("bahasa"));
			
			isoMsg.set(127, msg.getDestBankCode());//Destination Institution Code.
			
		}
		catch (ISOException ex) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", ex);
		}catch (Exception e) {
			log.error("MoneyTransferReversalToBankProcessor :: process ", e);
		}
		return isoMsg;
	}
}
