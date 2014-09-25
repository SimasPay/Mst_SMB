package com.mfino.bsim.iso8583.processor.fixtoiso;

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
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMQRPaymentReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.service.SubscriberService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class QRPaymentReversalToBankProcessor extends BankRequestProcessor {

	public Log log = LogFactory.getLog(this.getClass());
	
	private SubscriberService subscriberService;
	
	public QRPaymentReversalToBankProcessor() {
		try {
			isoMsg.setMTI("0420");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	
	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		
		CMQRPaymentReversalToBank msg = (CMQRPaymentReversalToBank)fixmsg;

		// use the MDN of the global account
		String mdn = msg.getSourceMDNToUseForBank();
		if (mdn == null) {
			mdn = msg.getSourceMDN();
		}
		try {
			String mpan = MfinoUtil.CheckDigitCalculation(msg.getSourceMDN());
            isoMsg.set(2, mpan);
            String defaultDE3=CmFinoFIX.ISO8583_ProcessingCode_XLink_Payment0;
            Pocket sourcePocket = subscriberService.getDefaultPocket(msg.getSourceMDN(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
            PocketTemplate pocketTemplate = null;
            Integer pocketTempType = null;
            String processingCodePrefix = "50";
            log.info("QRPaymentReversalToBankProcessor :: process appending processing Code Prefix as :"+processingCodePrefix);
            if(sourcePocket!=null){
            pocketTemplate = sourcePocket.getPocketTemplate();
			pocketTempType = pocketTemplate.getBankAccountCardType();
			if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
				defaultDE3=processingCodePrefix+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+"00"; ;
			}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
				defaultDE3=processingCodePrefix+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+"00";
			}
            }
            isoMsg.set(3,defaultDE3);//default de-3 will be overwritten depending on dest a/c type
            log.info("QRPaymentReversalToBankProcessor :: process default "+ defaultDE3);
            String processingCode=null;
            IntegrationSummaryDao isDAO  = DAOFactory.getInstance().getIntegrationSummaryDao();
            IntegrationSummaryQuery isQuery = new IntegrationSummaryQuery();
            ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
            ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
            ServiceChargeTransactionLog sctl;
            Long transferID = msg.getTransferID();
            Long sctlID;
            String reconciliationID1 = null;
            if(transferID!=null){
            	sctlQuery.setTransferID(transferID);
            	log.info("QRPaymentReversalToBankProcessor :: process Transfer ID :"+transferID);
            	List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
            	if(CollectionUtils.isNotEmpty(list)){
            		sctl = list.get(0);
            		sctlID = sctl.getID();
            		log.info("QRPaymentReversalToBankProcessor :: process Sctl ID :"+sctlID);
            		isQuery.setSctlID(sctlID);
            		List<IntegrationSummary> isList = isDAO.get(isQuery);
            		if(CollectionUtils.isNotEmpty(isList)){
            			IntegrationSummary iSummary = isList.get(0);
            			reconciliationID1 = iSummary.getReconcilationID1();
            			log.info("QRPaymentReversalToBankProcessor :: process ReconciliationID1 :"+reconciliationID1);
            			log.info("QRPaymentReversalToBankProcessor :: Dumping message fields :" + msg.DumpFields());
            			log.info("QRPaymentReversalToBankProcessor :: SourceMDN " + msg.getSourceMDN());
            			log.info("QRPaymentReversalToBankProcessor :: source Pocket" + sourcePocket.DumpFields());
            			
            			if(sourcePocket!=null && StringUtils.isNotBlank(reconciliationID1))
            				{
            				log.info("pocketTemplate.getBankAccountCardType() "  + pocketTemplate.getBankAccountCardType());
            				log.info("dumping pocketemplate fields " + pocketTemplate.DumpFields());
            				
            				if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_SavingsAccount)){
            					processingCode=processingCodePrefix+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+reconciliationID1 ;
            				}else if(pocketTempType.equals(CmFinoFIX.BankAccountCardType_CheckingAccount)){
            					processingCode=processingCodePrefix+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+reconciliationID1;
            				}
            				log.info("QRPaymentReversalToBankProcessor :: process Setting ProcessingCode :"+processingCode+" in DE-3");
            				isoMsg.set(3,processingCode);
            			}
                		
            		}	   		
            	}
            }
            long amount = msg.getAmount().longValue()*(100);
            isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0") );
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
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
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
			isoMsg.set(37, 	StringUtilities.leftPadWithCharacter( msg.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, msg.getSourceMDN());
			/*isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(48, msg.getTransactionID().toString());*/
			isoMsg.set(49,constantFieldsMap.get("49"));
			isoMsg.set(60, "No bank response");
			String reversalInfoStr = "0200" + paddedSTAN;
			reversalInfoStr = reversalInfoStr+ DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime());
			log.info("QRPaymentReversalToBankProcessor :: process originaltransfertime = in de-90 "+ DateTimeFormatter.getMMDDHHMMSS(msg.getTransferTime()));
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("32"), '0', 11);
			reversalInfoStr = reversalInfoStr + FixToISOUtil.padOnLeft(constantFieldsMap.get("32"), '0', 11);
			isoMsg.set(90, reversalInfoStr);
			
			isoMsg.set(98,msg.getBillerCode());
			isoMsg.set(102, msg.getSourceCardPAN()); 
			if(msg.getLanguage().equals(0))
				   isoMsg.set(121,constantFieldsMap.get("english"));
				else
				   isoMsg.set(121,constantFieldsMap.get("bahasa"));
			
			
		}
		catch (ISOException ex) {
			log.error("QRPaymentReversalToBankProcessor :: process ", ex);
		}catch (Exception e) {
			log.error("QRPaymentReversalToBankProcessor :: process ", e);
		}
		return isoMsg;
	}


	public SubscriberService getSubscriberService() {
		return subscriberService;
	}


	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
}
