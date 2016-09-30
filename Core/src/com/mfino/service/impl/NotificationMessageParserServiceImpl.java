/**
 * 
 */
package com.mfino.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.SctlSettlementMap;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.EnumTextService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author Deva
 *
 */
@Service("NotificationMessageParserServiceImpl")
public class NotificationMessageParserServiceImpl implements NotificationMessageParserService {

    NotificationWrapper notificationWrapper;
    private static Map<String, Integer> notificationVariablesMap = new HashMap<String, Integer>();
    private boolean appendNotificationCode = true;
    private static final Integer invoiceNumber = 100;
    
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
    private static Logger log = LoggerFactory.getLogger(NotificationMessageParserServiceImpl.class);
    static {
        notificationVariablesMap.put("ServiceName", CmFinoFIX.NotificationVariables_ServiceName);
        notificationVariablesMap.put("SenderMDN", CmFinoFIX.NotificationVariables_SenderMDN);
        notificationVariablesMap.put("Amount", CmFinoFIX.NotificationVariables_Amount);
        notificationVariablesMap.put("Currency", CmFinoFIX.NotificationVariables_Currency);
        notificationVariablesMap.put("TransferID", CmFinoFIX.NotificationVariables_TransferID);
        notificationVariablesMap.put("TransactionID", CmFinoFIX.NotificationVariables_TransactionID);
        notificationVariablesMap.put("ReceiverMDN", CmFinoFIX.NotificationVariables_ReceiverMDN);
        notificationVariablesMap.put("CurrentDateTime", CmFinoFIX.NotificationVariables_CurrentDateTime);
        notificationVariablesMap.put("TransactionDateTime", CmFinoFIX.NotificationVariables_TransactionDateTime);
        notificationVariablesMap.put("MinimumTransactionAmountLimit", CmFinoFIX.NotificationVariables_MinimumTransactionAmountLimit);
        notificationVariablesMap.put("MaximumTransactionAmountLimit", CmFinoFIX.NotificationVariables_MaximumTransactionAmountLimit);
        notificationVariablesMap.put("CommodityBalanceValue", CmFinoFIX.NotificationVariables_CommodityBalanceValue);
        notificationVariablesMap.put("SenderName", CmFinoFIX.NotificationVariables_SenderName);
        notificationVariablesMap.put("PINMininumDigit", CmFinoFIX.NotificationVariables_PINMininumDigit);
        notificationVariablesMap.put("PINMaximumDigit", CmFinoFIX.NotificationVariables_PINMaximumDigit);
        notificationVariablesMap.put("TransactionType", CmFinoFIX.NotificationVariables_TransactionType);
        notificationVariablesMap.put("TransferStatus", CmFinoFIX.NotificationVariables_TransferStatus);
        notificationVariablesMap.put("PayerMDN", CmFinoFIX.NotificationVariables_PayerMDN);
        notificationVariablesMap.put("AirtimeBalanceValue", CmFinoFIX.NotificationVariables_AirtimeBalanceValue);
        notificationVariablesMap.put("ReceiverAccountName", CmFinoFIX.NotificationVariables_ReceiverAccountName);
        notificationVariablesMap.put("OptionalTextMessage", CmFinoFIX.NotificationVariables_OptionalTextMessage);
        notificationVariablesMap.put("BankAccountBalanceValue", CmFinoFIX.NotificationVariables_BankAccountBalanceValue);
        notificationVariablesMap.put("PocketType", CmFinoFIX.NotificationVariables_PocketType);
        notificationVariablesMap.put("CustomerServiceShortCode", CmFinoFIX.NotificationVariables_CustomerServiceShortCode);
        notificationVariablesMap.put("Restrictions", CmFinoFIX.NotificationVariables_Restrictions);
        notificationVariablesMap.put("Commodity", CmFinoFIX.NotificationVariables_Commodity);
        notificationVariablesMap.put("MinimumBalanceAllowed", CmFinoFIX.NotificationVariables_MinimumBalanceAllowed);
        notificationVariablesMap.put("MaximumBalanceAllowed", CmFinoFIX.NotificationVariables_MaximumBalanceAllowed);
        notificationVariablesMap.put("BankAccountServletURL", CmFinoFIX.NotificationVariables_BankAccountServletURL);
        notificationVariablesMap.put("SourceMDN", CmFinoFIX.NotificationVariables_SourceMDN);
        notificationVariablesMap.put("ParentTransactionID", CmFinoFIX.NotificationVariables_ParentTransactionID);
        notificationVariablesMap.put("FlashOnly", CmFinoFIX.NotificationVariables_FlashOnly);
        notificationVariablesMap.put("BankAccountCurrency", CmFinoFIX.NotificationVariables_BankAccountCurrency);
        notificationVariablesMap.put("BankAccountTransactionDate", CmFinoFIX.NotificationVariables_BankAccountTransactionDate);
        notificationVariablesMap.put("BankAccountTransactionType", CmFinoFIX.NotificationVariables_BankAccountTransactionType);
        notificationVariablesMap.put("BankAccountTransactionAmount", CmFinoFIX.NotificationVariables_BankAccountTransactionAmount);
        notificationVariablesMap.put("BankAccountServletPath", CmFinoFIX.NotificationVariables_BankAccountServletPath);
        notificationVariablesMap.put("EmbeddedText", CmFinoFIX.NotificationVariables_EmbeddedText);
        notificationVariablesMap.put("NotificationCode", CmFinoFIX.NotificationVariables_NotificationCode);
        notificationVariablesMap.put("SubscriberPIN", CmFinoFIX.NotificationVariables_SubscriberPIN);
        notificationVariablesMap.put("MerchantPIN", CmFinoFIX.NotificationVariables_MerchantPIN);
        notificationVariablesMap.put("DestMDN", CmFinoFIX.NotificationVariables_DestMDN);
        notificationVariablesMap.put("LOPID", CmFinoFIX.NotificationVariables_LOPID);
        notificationVariablesMap.put("MaximumLOPAmountLimit", CmFinoFIX.NotificationVariables_MaximumLOPAmountLimit);
        notificationVariablesMap.put("PostpaidMDN", CmFinoFIX.NotificationVariables_PostpaidMDN);
        notificationVariablesMap.put("WebSite", CmFinoFIX.NotificationVariables_WebSite);
        notificationVariablesMap.put("ConfirmationCode", CmFinoFIX.NotificationVariables_ConfirmationCode);
        notificationVariablesMap.put("Username", CmFinoFIX.NotificationVariables_Username);
		notificationVariablesMap.put("CardPANSuffix", CmFinoFIX.NotificationVariables_CardPANSuffix);
		notificationVariablesMap.put("PocketDescription", CmFinoFIX.NotificationVariables_PocketDescription);
		notificationVariablesMap.put("BankName", CmFinoFIX.NotificationVariables_BankName);
        notificationVariablesMap.put("BillAmountValue", CmFinoFIX.NotificationVariables_BillAmountValue);
        notificationVariablesMap.put("BillPaymentID", CmFinoFIX.NotificationVariables_BillPaymentID);
        notificationVariablesMap.put("ContactCenterNo", CmFinoFIX.NotificationVariables_ContactCenterNo);
        notificationVariablesMap.put("serviceCharge", CmFinoFIX.NotificationVariables_serviceCharge);
        notificationVariablesMap.put("transactionAmount", CmFinoFIX.NotificationVariables_transactionAmount);
        notificationVariablesMap.put("OneTimePin", CmFinoFIX.NotificationVariables_OneTimePin);
        notificationVariablesMap.put("KycLevel", CmFinoFIX.NotificationVariables_KycLevel);
        notificationVariablesMap.put("PartnerCode", CmFinoFIX.NotificationVariables_PartnerCode);
        notificationVariablesMap.put("NumberOfTriesLeft", CmFinoFIX.NotificationVariables_NumberOfTriesLeft);
        notificationVariablesMap.put("Service", CmFinoFIX.NotificationVariables_Service);
        notificationVariablesMap.put("AppURL", CmFinoFIX.NotificationVariables_AppURL);
        notificationVariablesMap.put("OriginalTransferID", CmFinoFIX.NotificationVariables_OriginalTransferID);
        notificationVariablesMap.put("BulkTransferID", CmFinoFIX.NotificationVariables_BulkTransferID);
        notificationVariablesMap.put("minAmount", CmFinoFIX.NotificationVariables_minAmount);
        notificationVariablesMap.put("maxAmount", CmFinoFIX.NotificationVariables_maxAmount);
        notificationVariablesMap.put("ValidDenominations", CmFinoFIX.NotificationVariables_ValidDenominations);
        notificationVariablesMap.put("IntegrationName", CmFinoFIX.NotificationVariables_IntegrationName);
        notificationVariablesMap.put("AuthenticationKey", CmFinoFIX.NotificationVariables_AuthenticationKey);
        notificationVariablesMap.put("InstitutionID", CmFinoFIX.NotificationVariables_InstitutionID);
        notificationVariablesMap.put("IPAddress", CmFinoFIX.NotificationVariables_IPAddress);
        notificationVariablesMap.put("FirstName", CmFinoFIX.NotificationVariables_FirstName);
        notificationVariablesMap.put("LastName", CmFinoFIX.NotificationVariables_LastName);
        notificationVariablesMap.put("CustomerName", CmFinoFIX.NotificationVariables_CustomerName);
        notificationVariablesMap.put("AgentName", CmFinoFIX.NotificationVariables_AgentName);
        notificationVariablesMap.put("InvoiceNumber", invoiceNumber);
        notificationVariablesMap.put("SenderFirstName", CmFinoFIX.NotificationVariables_SenderFirstName);
        notificationVariablesMap.put("SenderLastName", CmFinoFIX.NotificationVariables_SenderLastName);
        notificationVariablesMap.put("ReceiverFirstName", CmFinoFIX.NotificationVariables_ReceiverFirstName);
        notificationVariablesMap.put("ReceiverLastName", CmFinoFIX.NotificationVariables_ReceiverLastName);   
		notificationVariablesMap.put("SettlementStatus", CmFinoFIX.NotificationVariables_SettlementStatus); 
		notificationVariablesMap.put("TradeName", CmFinoFIX.NotificationVariables_TradeName); 
		notificationVariablesMap.put("OTPExpirationTime", CmFinoFIX.NotificationVariables_OTPExpirationTime);
		notificationVariablesMap.put("TransID", CmFinoFIX.NotificationVariables_TransID);
		notificationVariablesMap.put("ParentTransID", CmFinoFIX.NotificationVariables_ParentTransID);
		notificationVariablesMap.put("CardPAN", CmFinoFIX.NotificationVariables_CardPAN); 
		notificationVariablesMap.put("CardAlias", CmFinoFIX.NotificationVariables_CardAlias); 
		notificationVariablesMap.put("OldCardAlias", CmFinoFIX.NotificationVariables_OldCardAlias); 
	    notificationVariablesMap.put("CardPan", CmFinoFIX.NotificationVariables_CardPan);
	    notificationVariablesMap.put("MaxFavoriteCount", CmFinoFIX.NotificationVariables_MaxFavoriteCount);
	    notificationVariablesMap.put("FavoriteValue", CmFinoFIX.NotificationVariables_FavoriteValue);
	    notificationVariablesMap.put("FavoriteLabel", CmFinoFIX.NotificationVariables_FavoriteLabel);
	    notificationVariablesMap.put("SubscriberStatus", CmFinoFIX.NotificationVariables_SubscriberStatus);
	    notificationVariablesMap.put("OtherMDN",CmFinoFIX.NotificationVariables_OtherMDN);
	    notificationVariablesMap.put("RemainingBlockTimeMinutes",CmFinoFIX.NotificationVariables_RemainingBlockTimeMinutes);
	    notificationVariablesMap.put("RemainingBlockTimeHours",CmFinoFIX.NotificationVariables_RemainingBlockTimeHours);
    }

    public NotificationMessageParserServiceImpl(NotificationWrapper notificationWrapper) {
        this.notificationWrapper = notificationWrapper;
    }

    public NotificationMessageParserServiceImpl() {
		// TODO Auto-generated constructor stub
	}

    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public String buildMessage() {
        return buildMessage(notificationWrapper,appendNotificationCode);
    }
    
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
    public String buildMessage(NotificationWrapper notificationWrapper,boolean appendNotificationCode) {
    	SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
        NotificationDAO notificationDAO = DAOFactory.getInstance().getNotificationDAO();
        NotificationQuery query = new NotificationQuery();
        query.setNotificationCode(notificationWrapper.getCode());
        query.setNotificationMethod(notificationWrapper.getNotificationMethod());
        query.setCompany(notificationWrapper.getCompany());
        Integer language = notificationWrapper.getLanguage();
        if(language == null) {
        	language = systemParametersServiceImpl.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
        }
        query.setLanguage(language);
        List<Notification> list = notificationDAO.getLanguageBasedNotifications(query);
        if (list.size() == 0) {
            log.error("No notification method found so returning null");
            return null;
        }
        Notification notification = (Notification) list.get(0);
        notificationWrapper.setSMSNotificationCode(notification.getSmsnotificationcode());
        notificationWrapper.setAccessCode(notification.getAccesscode());
        String notificationText = notification.getText().toString();
        SubscriberMdn senderMdn = null;
        if(notificationText.contains("$(SenderFirstName)") || notificationText.contains("$(SenderLastName)"))
        {
        	senderMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(notificationWrapper.getSourceMDN());
        	if(senderMdn == null)
        		senderMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(notificationWrapper.getSenderMDN());			
        }
        SubscriberMdn receiverMdn = null;
        if(notificationText.contains("$(ReceiverFirstName)") || notificationText.contains("$(ReceiverLastName)"))
        {
        	receiverMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(notificationWrapper.getReceiverMDN());
        	if(receiverMdn == null)
        		receiverMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(notificationWrapper.getDestMDN());		
        }
        if (!appendNotificationCode) {
        	if(notificationText.contains("$(NotificationCode)"))
        	{
        		notificationText = notificationText.replace("$(NotificationCode)", "");
        	}
        }
        List<TextPart> textParts = extractTextParts(notificationText);
        StringBuilder notificationBuilder = new StringBuilder();
        CommodityTransfer commodityTransfer = notificationWrapper.getCommodityTransfer();
        SctlSettlementMap pendingSettlement = notificationWrapper.getPendingSettlement();
        Pocket sourcePocket = notificationWrapper.getSourcePocket();
        for (TextPart textPart : textParts) {
            if (textPart.isVariable) {

                Integer notificationVariable = notificationVariablesMap.get(textPart.text);
                if (CmFinoFIX.NotificationVariables_ServiceName.equals(notificationVariable)) {
                    notificationBuilder.append("Service Name");
                } else if (CmFinoFIX.NotificationVariables_SourceMDN.equals(notificationVariable)
                        || CmFinoFIX.NotificationVariables_SenderMDN.equals(notificationVariable)) {
                    if (commodityTransfer != null && commodityTransfer.getSourcemdn() != null) {
                        notificationBuilder.append(commodityTransfer.getSourcemdn());
                    } else if(notificationWrapper.getSourceMDN()!=null){
                        notificationBuilder.append(notificationWrapper.getSourceMDN());
                    } else {
                        log.error("Pass Source MDN");
                    }
                } else if (CmFinoFIX.NotificationVariables_Amount.equals(notificationVariable)) {
                    NumberFormat numberFormat = MfinoUtil.getNumberFormat();
                    BigDecimal amount = null;
                    if (notificationWrapper.getAmount() != null) {
                    	amount = notificationWrapper.getAmount();
                    } else if(commodityTransfer != null){
                    	amount = commodityTransfer.getAmount() == null? BigDecimal.ZERO : commodityTransfer.getAmount();	
                    }
                    else if(notificationWrapper.getLastNFCCheckBalanceEntry() != null){
                    	amount = notificationWrapper.getLastNFCCheckBalanceEntry().getAmount() == null? BigDecimal.ZERO : notificationWrapper.getLastNFCCheckBalanceEntry().getAmount();	
                    }
                    notificationBuilder.append(numberFormat.format(amount));
                } else if (CmFinoFIX.NotificationVariables_TransferID.equals(notificationVariable)) {
                	if(notificationWrapper.getSctlID()!=null){
                		notificationBuilder.append(notificationWrapper.getSctlID());
                	}else{
                    notificationBuilder.append(commodityTransfer.getId());
                	}
                } else if (CmFinoFIX.NotificationVariables_TransactionID.equals(notificationVariable)) {
                	if (commodityTransfer != null) {
                        notificationBuilder.append(commodityTransfer.getSctlId() != null ? commodityTransfer.getSctlId() : commodityTransfer.getId());
                    }
                	else if(notificationWrapper.getSctlID()!=null){
                		notificationBuilder.append(notificationWrapper.getSctlID());
                	}
                	else  if (notificationWrapper.getTransactionId() != null) {
                        notificationBuilder.append(notificationWrapper.getTransactionId());
                    } 
                } else if (CmFinoFIX.NotificationVariables_ReceiverMDN.equals(notificationVariable)
                        || CmFinoFIX.NotificationVariables_PostpaidMDN.equals(notificationVariable)) {
                	if(notificationWrapper.getReceiverMDN()!=null){
                		notificationBuilder.append(notificationWrapper.getReceiverMDN());
                	}else if (commodityTransfer != null && commodityTransfer.getDestmdn() != null) {
                        notificationBuilder.append(commodityTransfer.getDestmdn());
                    } else {
                        log.error("Pass Dest MDN");
                    }
                } else if (CmFinoFIX.NotificationVariables_TransactionDateTime.equals(notificationVariable)) {
                    String time = null;
                    if (commodityTransfer != null && commodityTransfer.getCreatetime() != null) {
                        time = getDateFormat().format(commodityTransfer.getCreatetime());
                    }else if(notificationWrapper.getServiceChargeTransactionLog()!=null && notificationWrapper.getServiceChargeTransactionLog().getCreatetime()!=null){
                        time = getDateFormat().format(notificationWrapper.getServiceChargeTransactionLog().getCreatetime());
                    } else {
                        time = getDateFormat().format(new Date());
                    }
                    notificationBuilder.append(time);
                } else if (CmFinoFIX.NotificationVariables_CurrentDateTime.equals(notificationVariable)) {
                    notificationBuilder.append(getDateFormat().format(new Date()));
                } else if (CmFinoFIX.NotificationVariables_CustomerServiceShortCode.equals(notificationVariable)) {
                	if (notificationWrapper.getCompany() != null) {
                		notificationBuilder.append(notificationWrapper.getCompany().getCustomerservicenumber());
                	} else {
                		notificationBuilder.append("881");
                	}
                } else if (CmFinoFIX.NotificationVariables_MinimumTransactionAmountLimit.equals(notificationVariable)) {
                    if (sourcePocket != null && sourcePocket.getPocketTemplate() != null && sourcePocket.getPocketTemplate().getMinamountpertransaction() != null) {
                        notificationBuilder.append(sourcePocket.getPocketTemplate().getMinamountpertransaction());
                    } else {
                        notificationBuilder.append(textPart.text);
                    }
                } else if (CmFinoFIX.NotificationVariables_MaximumTransactionAmountLimit.equals(notificationVariable)) {
                    if (sourcePocket != null && sourcePocket.getPocketTemplate() != null) {
                        notificationBuilder.append(sourcePocket.getPocketTemplate().getMaxamountpertransaction());
                    } else {
                        notificationBuilder.append(textPart.text);
                    }
                } else if (CmFinoFIX.NotificationVariables_PINMininumDigit.equals(notificationVariable)) {
                	notificationBuilder.append(systemParametersServiceImpl.getPinLength());
                } else if (CmFinoFIX.NotificationVariables_PINMaximumDigit.equals(notificationVariable)) {
                	notificationBuilder.append(systemParametersServiceImpl.getPinLength());
                } else if (CmFinoFIX.NotificationVariables_AirtimeBalanceValue.equals(notificationVariable) || CmFinoFIX.NotificationVariables_CommodityBalanceValue.equals(notificationVariable)) {
                    if (sourcePocket != null) {
                        NumberFormat numberFormat = MfinoUtil.getNumberFormat();
                        BigDecimal amount = (BigDecimal) (sourcePocket.getCurrentbalance() == null? BigDecimal.ZERO : sourcePocket.getCurrentbalance());
                        notificationBuilder.append(numberFormat.format(amount));
                    } else {
                        notificationBuilder.append(0);
                    }
                } else if (CmFinoFIX.NotificationVariables_Restrictions.equals(notificationVariable)) {
                    notificationBuilder.append(buildRestrictionsString());
                } else if (CmFinoFIX.NotificationVariables_Commodity.equals(notificationVariable)) {
                    notificationBuilder.append(buildCommodityString(commodityTransfer,notificationWrapper));
                } else if (CmFinoFIX.NotificationVariables_TransactionType.equals(notificationVariable)) {
                    notificationBuilder.append(buildMsgTypeString(notificationWrapper));
                } else if (CmFinoFIX.NotificationVariables_TransferStatus.equals(notificationVariable)) {
                    notificationBuilder.append(buildTransferStatusString(notificationWrapper));
                } else if (CmFinoFIX.NotificationVariables_MinimumBalanceAllowed.equals(notificationVariable)) {
                    if (sourcePocket != null && sourcePocket.getPocketTemplate() != null) {
                        notificationBuilder.append(sourcePocket.getPocketTemplate().getMinimumstoredvalue());
                    } else {
                        notificationBuilder.append(0);
                    }
                } else if (CmFinoFIX.NotificationVariables_MaximumBalanceAllowed.equals(notificationVariable)) {
                    if (notificationWrapper.getSourcePocket() != null && notificationWrapper.getSourcePocket().getPocketTemplate() != null) {
                        notificationBuilder.append(sourcePocket.getPocketTemplate().getMaximumstoredvalue());
                    } else {
                        notificationBuilder.append(0);
                    }
                } else if (CmFinoFIX.NotificationVariables_ParentTransactionID.equals(notificationVariable)) {
                    throw new UnhandledException("Unhandled Block ", null);
                } else if (CmFinoFIX.NotificationVariables_BankAccountServletURL.equals(notificationVariable)) {
                    throw new UnhandledException("Unhandled Block ", null);
                } else if (CmFinoFIX.NotificationVariables_BankAccountServletPath.equals(notificationVariable)) {
                    throw new UnhandledException("Unhandled Block ", null);
                } else if (CmFinoFIX.NotificationVariables_FlashOnly.equals(notificationVariable)) {
                    throw new UnhandledException("Unhandled Block ", null);
                } else if (CmFinoFIX.NotificationVariables_BankAccountBalanceValue.equals(notificationVariable)) {
                    throw new UnhandledException("Unhandled Block ", null);
                } else if (CmFinoFIX.NotificationVariables_BankAccountCurrency.equals(notificationVariable)) {
                    if (notificationWrapper.getLastBankTrxnEntry() != null) {
                    	notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getCurrency());
                    }
                } else if (CmFinoFIX.NotificationVariables_Currency.equals(notificationVariable)) {
                    if (notificationWrapper.getCommodityTransfer() != null && StringUtils.isNotBlank(notificationWrapper.getCommodityTransfer().getCurrency())) {
                    	notificationBuilder.append(notificationWrapper.getCommodityTransfer().getCurrency());
                    }
                    else{
                    	notificationBuilder.append(systemParametersServiceImpl.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
                    }
                } else if (CmFinoFIX.NotificationVariables_BankAccountTransactionDate.equals(notificationVariable)) {
                	if (notificationWrapper.getLastBankTrxnEntry() != null) {
                    	notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getBankTransactionDate());
                    }
                } else if (CmFinoFIX.NotificationVariables_BankAccountTransactionType.equals(notificationVariable)) {
                	if (notificationWrapper.getLastBankTrxnEntry() != null) {
	            		if (notificationWrapper.getLastBankTrxnEntry().getBankTransactionCodeDescription() != null) {
	            			notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getBankTransactionCodeDescription());
	            		}
	            		if(notificationWrapper.getLastBankTrxnEntry().getBankTransactionFlag() != null)
	        			{
	            			notificationBuilder.append(" (");
	            			notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getBankTransactionFlag());
	            			notificationBuilder.append(')');
	        			}
	            		if (notificationWrapper.getLastBankTrxnEntry().getTransactionType() != null) {
	            			notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getTransactionType());
	            		}
                	}
                } else if (CmFinoFIX.NotificationVariables_BankAccountTransactionAmount.equals(notificationVariable)) {
                	NumberFormat numberFormat = MfinoUtil.getNumberFormat();
                    BigDecimal amount = notificationWrapper.getLastBankTrxnEntry() == null? BigDecimal.ZERO : 
                    	notificationWrapper.getLastBankTrxnEntry().getAmount();
                    notificationBuilder.append(numberFormat.format(amount));
                }
                else if(CmFinoFIX.NotificationVariables_CardPAN.equals(notificationVariable)){
                	if (notificationWrapper.getCardPan() != null ) {
                		notificationBuilder.append(notificationWrapper.getCardPan());
                	} else if(notificationWrapper.getLastBankTrxnEntry() != null){
                		notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getSourceCardPAN());
                	}
                	else if(notificationWrapper.getLastNFCCheckBalanceEntry() != null){
                		notificationBuilder.append(notificationWrapper.getLastNFCCheckBalanceEntry().getSourceCardPAN());
                	}
                }
                else if(CmFinoFIX.NotificationVariables_CardAlias.equals(notificationVariable)){
                	if (notificationWrapper.getCardAlias() != null ) {
                		notificationBuilder.append(notificationWrapper.getCardAlias());
                	} else if(notificationWrapper.getLastBankTrxnEntry() != null){
                		notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getCardAlias());
                	}
                	else if(notificationWrapper.getLastNFCCheckBalanceEntry() != null){
                		notificationBuilder.append(notificationWrapper.getLastNFCCheckBalanceEntry().getCardAlias());
                	}
                } 
                else if(CmFinoFIX.NotificationVariables_OldCardAlias.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getOldCardAlias() != null ? notificationWrapper.getOldCardAlias() : "");
                } 
                else if (CmFinoFIX.NotificationVariables_ReceiverAccountName.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getReceiverAccountName());
                } else if (CmFinoFIX.NotificationVariables_OptionalTextMessage.equals(notificationVariable)) {
                    throw new UnhandledException("Unhandled Block ", null);
                } else if (CmFinoFIX.NotificationVariables_SenderName.equals(notificationVariable)) {
                    if (notificationWrapper.getCommodityTransfer() != null) {
                        notificationBuilder.append(notificationWrapper.getCommodityTransfer().getSourcesubscribername());
                    }
                } else if (CmFinoFIX.NotificationVariables_EmbeddedText.equals(notificationVariable)) {
                    throw new UnhandledException("Unhandled Block ", null);
                } else if (CmFinoFIX.NotificationVariables_NotificationCode.equals(notificationVariable)) {
                    notificationBuilder.append("(");
                    notificationBuilder.append(notificationWrapper.getCode());
                    notificationBuilder.append(")");
                } else if (CmFinoFIX.NotificationVariables_LOPID.equals(notificationVariable)) {
                    notificationBuilder.append(notificationWrapper.getLOP().getId());
                } else if (CmFinoFIX.NotificationVariables_MaximumLOPAmountLimit.equals(notificationVariable)) {
                     notificationBuilder.append(notificationWrapper.getDistributionChainLevel().getMaxlopamount());
                }else if (CmFinoFIX.NotificationVariables_WebSite.equals(notificationVariable)) {
                    notificationBuilder.append(ConfigurationUtil.getAppURL());
                } else if (CmFinoFIX.NotificationVariables_Username.equals(notificationVariable)) {
                    notificationBuilder.append(notificationWrapper.getUsername());
                } else if (CmFinoFIX.NotificationVariables_ConfirmationCode.equals(notificationVariable)) {
                     notificationBuilder.append(notificationWrapper.getConfirmationCode());
                }
 				else if (CmFinoFIX.NotificationVariables_PocketDescription.equals(notificationVariable)) {
					if (notificationWrapper.getPocketDescription() != null)
						notificationBuilder.append(notificationWrapper.getPocketDescription());
				}
 				else if (CmFinoFIX.NotificationVariables_BankName.equals(notificationVariable)) {
					if (notificationWrapper.getBankName() != null)
						notificationBuilder.append(notificationWrapper.getBankName());
					else if (notificationWrapper.getLastBankTrxnEntry() != null && notificationWrapper.getLastBankTrxnEntry().getBankName() != null){
						notificationBuilder.append(notificationWrapper.getLastBankTrxnEntry().getBankName());
					}
				}
 				else if(CmFinoFIX.NotificationVariables_BillAmountValue.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getBillAmount()!=null?notificationWrapper.getBillAmount():"");
                }else if(CmFinoFIX.NotificationVariables_BillPaymentID.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getBillPaymentID()!=null?notificationWrapper.getBillPaymentID():"");
                }else if(CmFinoFIX.NotificationVariables_ContactCenterNo.equals(notificationVariable)){
                	if(notificationWrapper.getBank()!=null){
                    	notificationBuilder.append(notificationWrapper.getBank().getContactnumber()!=null?notificationWrapper.getBank().getContactnumber():"");
                    	}else{
                    		notificationBuilder.append("");
                    		}
                } else if(CmFinoFIX.NotificationVariables_serviceCharge.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getServiceCharge());
                } else if(CmFinoFIX.NotificationVariables_transactionAmount.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getTransactionAmount());
                }else if(CmFinoFIX.NotificationVariables_OneTimePin.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getOneTimePin());
                }else if(CmFinoFIX.NotificationVariables_SubscriberPIN.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getSubscriberPin());
                }else if(CmFinoFIX.NotificationVariables_OTPExpirationTime.equals(notificationVariable)) {
            	notificationBuilder.append(notificationWrapper.getOtpExpirationTime());
            	}else if(CmFinoFIX.NotificationVariables_KycLevel.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getKycLevel());
                }else if(CmFinoFIX.NotificationVariables_DestMDN.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getDestMDN());
                }else if(CmFinoFIX.NotificationVariables_PartnerCode.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getPartnerCode());
                }else if(CmFinoFIX.NotificationVariables_NumberOfTriesLeft.equals(notificationVariable)) {
                	notificationBuilder.append(notificationWrapper.getNumberOfTriesLeft());
                }else if(CmFinoFIX.NotificationVariables_Service.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getService());
                }else if(CmFinoFIX.NotificationVariables_AppURL.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getAppURL());
                }else if(CmFinoFIX.NotificationVariables_OriginalTransferID.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getOriginalTransferID());
                }else if(CmFinoFIX.NotificationVariables_BulkTransferID.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getBulkTransferId());
                }else if(CmFinoFIX.NotificationVariables_minAmount.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getMinAmount());
                }else if(CmFinoFIX.NotificationVariables_maxAmount.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getMaxAmount());
                }else if(CmFinoFIX.NotificationVariables_ValidDenominations.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getValidDenominations());
                }
                else if(CmFinoFIX.NotificationVariables_IntegrationName.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getIntegrationName());
                }
                else if(CmFinoFIX.NotificationVariables_AuthenticationKey.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getAuthenticationKey());
                }
                else if(CmFinoFIX.NotificationVariables_InstitutionID.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getInstitutionID());
                }
                else if(CmFinoFIX.NotificationVariables_IPAddress.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getIPAddress());
                }
                else if(CmFinoFIX.NotificationVariables_FirstName.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getFirstName());
                }
                else if(CmFinoFIX.NotificationVariables_LastName.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getLastName());
                }
                else if(CmFinoFIX.NotificationVariables_SenderFirstName.equals(notificationVariable)){
                	if(senderMdn != null)
        			{
        				notificationBuilder.append(senderMdn.getSubscriber().getFirstname());
        			}
                	else notificationBuilder.append("");
                }
                else if(CmFinoFIX.NotificationVariables_SenderLastName.equals(notificationVariable)){
                	if(senderMdn != null)
        			{
        				notificationBuilder.append(senderMdn.getSubscriber().getLastname());
        			}
                	else notificationBuilder.append("");
                }
                else if(CmFinoFIX.NotificationVariables_ReceiverFirstName.equals(notificationVariable)){
                	if(receiverMdn != null)
        			{
        				notificationBuilder.append(receiverMdn.getSubscriber().getFirstname());
        			}
                	else notificationBuilder.append("");
                }
                else if(CmFinoFIX.NotificationVariables_ReceiverLastName.equals(notificationVariable)){
                	if(receiverMdn != null)
        			{
        				notificationBuilder.append(receiverMdn.getSubscriber().getLastname());
        			}
                	else notificationBuilder.append("");
                }
                else if(CmFinoFIX.NotificationVariables_CustomerName.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getFirstName() + " " + notificationWrapper.getLastName());
                }
                else if(CmFinoFIX.NotificationVariables_AgentName.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getFirstName());
                }
				 else if(CmFinoFIX.NotificationVariables_TradeName.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getTradeName());
                }
                else if(CmFinoFIX.NotificationVariables_SettlementStatus.equals(notificationVariable)){
                	if(pendingSettlement != null)
                	{
                		String status = enumTextService.getEnumTextValue(CmFinoFIX.TagID_SettlementStatus, notificationWrapper.getLanguage(), pendingSettlement.getStatus());
                		notificationBuilder.append(status);
                	}
                	else
                	{
                		notificationBuilder.append("");
                	}
                }
                else if(invoiceNumber.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getInvoiceNumber());
                }
                else if(CmFinoFIX.NotificationVariables_TransID.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getTransID());
                }
                else if(CmFinoFIX.NotificationVariables_ParentTransID.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getParentTransID());
                }
                else if(CmFinoFIX.NotificationVariables_CardPan.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getCardPan());
                }
                else if(CmFinoFIX.NotificationVariables_MaxFavoriteCount.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getMaxFavoriteCount());
                }
                else if(CmFinoFIX.NotificationVariables_FavoriteLabel.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getFavoriteLabel());
                }
                else if(CmFinoFIX.NotificationVariables_FavoriteValue.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getFavoriteValue());
                }
                else if(CmFinoFIX.NotificationVariables_SubscriberStatus.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getSubscriberStatus());
                }
                else if(CmFinoFIX.NotificationVariables_OtherMDN.equals(notificationVariable)){
                	String otherMDN = notificationWrapper.getOtherMDN();
                	notificationBuilder.append(otherMDN.substring(0, otherMDN.length()-3) + "***"); 
                }
                else if(CmFinoFIX.NotificationVariables_RemainingBlockTimeMinutes.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getRemainingBlockTimeMinutes()); 
                }
                else if(CmFinoFIX.NotificationVariables_RemainingBlockTimeHours.equals(notificationVariable)){
                	notificationBuilder.append(notificationWrapper.getRemainingBlockTimeHours()); 
                }
                else {//if (CmFinoFIX.NotificationVariables_PayerMDN.equals(notificationVariable) || CmFinoFIX.NotificationVariables_PocketType.equals(notificationVariable)) {
                    notificationBuilder.append(textPart.text);
                }
            } else {
                notificationBuilder.append(textPart.text);
            }
        }
        return notificationBuilder.toString();
    }

    /**
     * @return
     */
    private String buildRestrictionsString() {
        throw new UnhandledException("Unhandled Block ", null);
    }

    /**
     * @return
     */
    private String buildCommodityString(CommodityTransfer commodityTransfer,NotificationWrapper notificationWrapper) {
        String returnValue = "Unknown";
        if (notificationWrapper.getCommodityTransfer() != null) {
            returnValue = enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, notificationWrapper.getLanguage(), notificationWrapper.getCommodityTransfer().getCommodity());
        } else if (notificationWrapper.getSourcePocket() != null) {
            returnValue = enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, notificationWrapper.getLanguage(), notificationWrapper.getSourcePocket().getPocketTemplate().getCommodity());
        }
        return returnValue;
    }

    private String buildMsgTypeString(NotificationWrapper notificationWrapper) {
        String returnValue = "Unknown";
    	TransactionType tt = null;
    	TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
        if (notificationWrapper.getServiceChargeTransactionLog() != null) {
        	tt = ttDAO.getById(notificationWrapper.getServiceChargeTransactionLog().getTransactiontypeid().longValue());
			if(tt != null) {
				returnValue = tt.getDisplayname();
			}
        }
        else if (notificationWrapper.getCommodityTransfer() != null) {
        	ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
        	ServiceChargeTxnLog sctl = sctlDAO.getById(notificationWrapper.getCommodityTransfer().getSctlId());
        	if (sctl != null) {
        		tt = ttDAO.getById(sctl.getTransactiontypeid().longValue());
			}
        	returnValue = (tt != null) ? tt.getDisplayname() : notificationWrapper.getCommodityTransfer().getSourcemessage();
        }
        return returnValue;
    }

    private String buildTransferStatusString(NotificationWrapper notificationWrapper) {
        String returnValue = "Unknown";
        ServiceChargeTxnLog sctl = null;
        if (notificationWrapper.getCommodityTransfer() != null) {
        	ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
        	sctl = sctlDAO.getById(notificationWrapper.getCommodityTransfer().getSctlId());
        }
        else if (notificationWrapper.getServiceChargeTransactionLog() != null) {
        	sctl = notificationWrapper.getServiceChargeTransactionLog();
        }
    	if (sctl != null) {
    		int sctlStatus = (int) sctl.getStatus();
    		switch (sctlStatus) {
			case 0:
				returnValue = "Inquiry";
				break;
				
			case 1: 
				returnValue = "Processing";
				break;
				
			case 2: case 3: case 4: case 6: case 7: case 8: case 9:
			case 10: case 11: case 12: case 13: case 14: case 15: 
				returnValue = "Done";
				break;
			
			case 5:
				returnValue = "Fail";
				break;

			case 16: case 17: case 18:
				returnValue = "Pending";
				break;
				
			case 19:
				returnValue = "Expired";
				break;
				
			default:
				break;
			}
    	}
        return returnValue;
    }

    /**
     * @param text
     */
    public List<TextPart> extractTextParts(String notificationText) {
        List<TextPart> textParts = new ArrayList<TextPart>();
        Pattern p = Pattern.compile("\\$\\([\\w]+\\)");
//		String notificationText = "Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(CustomerServiceShortCode). REF: $(TransactionID)";
        Matcher matcher = p.matcher(notificationText);
        int lastIndex = 0;
        while (matcher.find()) {
            String preMatchedString = notificationText.substring(lastIndex, matcher.start());
            String postMatchedString = notificationText.substring(matcher.start() + 2, matcher.end() - 1);
            if (preMatchedString.length() > 0) {
                textParts.add(new TextPart(preMatchedString, false));
            }
            if (postMatchedString.length() > 0) {
                textParts.add(new TextPart(postMatchedString, true));
            }
            lastIndex = matcher.end();
        }
        String finalMatchedString = notificationText.substring(lastIndex);
        if (finalMatchedString.length() > 0) {
            textParts.add(new TextPart(finalMatchedString, false));
        }
        return textParts;
    }

    public static class TextPart {
        String text;
        boolean isVariable = false;
        public TextPart(String text, boolean isVariable) {
            this.text = text;
            this.isVariable = isVariable;
        }
    }

    private DateFormat getDateFormat() {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
        // Making timezone as a configurable property
        TimeZone zone = ConfigurationUtil.getLocalTimeZone();
        df.setTimeZone(zone);
        return df;
    }

	/**
	 * @return the appendNotificationCode
	 */
	public boolean isAppendNotificationCode() {
		return appendNotificationCode;
	}

	/**
	 * @param appendNotificationCode the appendNotificationCode to set
	 */
	public void setAppendNotificationCode(boolean appendNotificationCode) {
		this.appendNotificationCode = appendNotificationCode;
	}
}