package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer.CGEntries;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.CommodityTransferUpdateMessage;

@Service("CommodityTransferUpdateMessageImpl")
public class CommodityTransferUpdateMessageImpl implements CommodityTransferUpdateMessage {

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
    private SubscriberMDNDAO subMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
    private ChargeTxnCommodityTransferMapDAO ctMapDao = DAOFactory.getInstance().getTxnTransferMap();
    
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
    public void updateMessage(CommodityTransfer c,
            PendingCommodityTransfer pct,
            CMJSCommodityTransfer.CGEntries entry, CMJSCommodityTransfer realMsg) {

        entry.setID(c.getId().longValue());
        entry.setTransactionID(c.getTransactionLog().getId().longValue());
        //      entry.setJSMsgType(c.getMsgType());
        entry.setMSPID(c.getMfinoServiceProvider().getId().longValue());
        entry.setTransferStatus(c.getTransferstatus());
        if (c.getTransferfailurereason() != null) {
            entry.setTransferFailureReason(c.getTransferfailurereason().intValue());
        }
        if (c.getNotificationcode() != null) {
            entry.setNotificationCode(c.getNotificationcode().intValue());
            String codeText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_NotificationCode, null, c.getNotificationcode());
            entry.setNotificationCodeName(c.getNotificationcode() + GeneralConstants.SINGLE_SPACE +(codeText!=null?codeText:""));
        }
        if (c.getUicategory() != null) {
            int type = c.getUicategory().intValue();
            entry.setTransactionUICategory(c.getUicategory().intValue());
            
            ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
    		Long sctlId = ctMapDao.getSCTLIdByCommodityTransferId(c.getId().longValue());
    		ServiceChargeTxnLog sctl = null;
    		if (sctlId != null) {
    			sctl = sctlDAO.getById(sctlId);
    		}
       		if(sctl != null){
       			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
       			TransactionType tt = ttDAO.getById(sctl.getTransactiontypeid().longValue());
       			entry.setTransactionTypeText(tt.getDisplayname());
       		}else{
       			entry.setTransactionTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, c.getUicategory()));
       		}
       		// Added as part of GT Request to identify the internal transaction type like E-B, E-E, B-E, B-B
       		entry.setInternalTxnType(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, c.getUicategory()));
    		
            if (type == CmFinoFIX.TransactionUICategory_Empty_SVA || type == CmFinoFIX.TransactionUICategory_MA_Transfer || type == CmFinoFIX.TransactionUICategory_MA_Topup || type == CmFinoFIX.TransactionUICategory_BulkTransfer || type == CmFinoFIX.TransactionUICategory_BulkTopup) {
                if (c.getSubscriber() != null && c.getSubscriber().getMfinoUserBySubscriberuserid() != null) {
                    entry.setSourceUserName(c.getSubscriber().getMfinoUserBySubscriberuserid().getUsername());
                }
            }
            if (type == CmFinoFIX.TransactionUICategory_Empty_SVA || type == CmFinoFIX.TransactionUICategory_Distribute_LOP || type == CmFinoFIX.TransactionUICategory_MA_Transfer || type == CmFinoFIX.TransactionUICategory_BulkTransfer) {
                Long sId = c.getDestsubscriberid().longValue();
                SubscriberDAO subdao = DAOFactory.getInstance().getSubscriberDAO();
                Subscriber sub = subdao.getById(sId);
                if (sub != null) {
                    if (sub.getMfinoUserBySubscriberuserid() != null) {
                        entry.setDestinationUserName(sub.getMfinoUserBySubscriberuserid().getUsername());
                    }
                }
            }
        }
        if (c.getStarttime() != null) {
            entry.setStartTime(c.getStarttime());
        }
        if (c.getEndtime() != null) {
            entry.setEndTime(c.getEndtime());
        }
        if (c.getSourcereferenceid() != null) {
            entry.setSourceReferenceID(c.getSourcereferenceid());
        }
        if (c.getDestmdn() != null) {
            entry.setSourceMDN(c.getDestmdn());
        }
        entry.setSourceMDNID(c.getSubscriberMdn().getId().longValue());
        entry.setSourceSubscriberID(c.getSubscriber().getId().longValue());
        if (c.getSourcesubscribername() != null) {
            entry.setSourceSubscriberName(c.getSourcesubscribername());
        }
        entry.setSourcePocketType((c.getSourcepockettype()).intValue());
        entry.setSourcePocketID(c.getPocket().getId().longValue());
        if (c.getSourcepocketbalance() != null) {
            entry.setSourcePocketBalance(new BigDecimal(c.getSourcepocketbalance()));
        }
        if (c.getPocket() != null && c.getPocket().getPocketTemplateByPockettemplateid() != null) {
            entry.setSourcePocketTemplateDescription(c.getPocket().getPocketTemplateByPockettemplateid().getDescription());
            if (c.getSourcecardpan() != null) {
//              entry.setSourceCardPAN(c.getSourceCardPAN());
          	 entry.setSourceCardPAN(c.getPocket().getCardpan()!=null?c.getPocket().getCardpan():"");
          }
        }
       
        if (c.getDestmdn() != null) {
            entry.setDestMDN(c.getDestmdn());
        }
        if (c.getDestmdnid() != null) {
            entry.setDestMDNID(c.getDestmdnid().longValue());
        }
        if (c.getDestsubscriberid() != null) {
            entry.setDestSubscriberID(c.getDestsubscriberid().longValue());
        }
        if (c.getDestsubscribername() != null) {
            entry.setDestSubscriberName(c.getDestsubscribername());
        } else {
            if (c.getDestmdn() != null) {
                String mdn = c.getDestmdn();
                SubscriberMdn submdn = subMdnDao.getByMDN(mdn);
                if (submdn != null) {
                    entry.setDestSubscriberName(submdn.getSubscriber().getFirstname() + " " + submdn.getSubscriber().getLastname());
                }
            }
        }
        if (c.getDestpockettype() != null) {
            entry.setDestPocketType(c.getDestpockettype().intValue());
        }
        if (c.getDestpocketid() != null) {
            entry.setDestPocketID(c.getDestpocketid().longValue());
        }
        if (c.getDestpocketbalance() != null) {
            entry.setDestPocketBalance(new BigDecimal(c.getDestpocketbalance()));
        }
        if (c.getDestpocketid() != null) {
            Pocket destPocket = this.pocketDao.getById(c.getDestpocketid().longValue());
            if (destPocket != null && destPocket.getPocketTemplateByPockettemplateid() != null) {
                entry.setDestPocketTemplateDescription(destPocket.getPocketTemplateByPockettemplateid().getDescription());
            }
            if (c.getDestcardpan() != null) {
//              entry.setDestCardPAN(c.getDestCardPAN());
            	entry.setDestCardPAN(destPocket.getCardpan()!=null?destPocket.getCardpan():"");
          }
        }
     
        if (c.getBillingtype() != null) {
            entry.setBillingType(c.getBillingtype().intValue());
            entry.setBillingTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BillingType, null, c.getBillingtype()));
        }
        if(realMsg.getIsMiniStatementRequest()!=null&&realMsg.getIsMiniStatementRequest()){
        	entry.setServiceChargeTransactionLogID(ctMapDao.getSCTLIdByCommodityTransferId(c.getId().longValue()));
        	processCreditDebits(realMsg,c,entry);
        }
        entry.setAmount(c.getAmount());
        entry.setCharges(c.getCharges());
        entry.setTaxAmount(c.getTaxamount());
        entry.setCommodity((c.getCommodity()).intValue());
        if (c.getBuckettype() != null) {
            entry.setBucketType(c.getBuckettype());
            entry.setBucketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType, null, c.getBuckettype()));
        }
        entry.setSourceApplication((c.getSourceapplication()).intValue());
        if (c.getCurrency() != null) {
            entry.setCurrency(c.getCurrency());
        }
        if (c.getBankcode() != null) {
            entry.setBankCode(c.getBankcode().intValue());
        }
        if (c.getOperatorcode() != null) {
            entry.setOperatorCode(c.getOperatorcode().intValue());
        }
        if (c.getOperatorresponsetime() != null) {
            entry.setOperatorResponseTime(c.getOperatorresponsetime());
        }
        if (c.getOperatorresponsecode() != null) {
            entry.setOperatorResponseCode(c.getOperatorresponsecode().intValue());
        }
        if (c.getBankrejectreason() != null) {
            entry.setOperatorRejectReason(c.getBankrejectreason());
        }
        if (c.getOperatorerrortext() != null) {
            entry.setOperatorErrorText(c.getOperatorerrortext());
        }
        if (c.getOperatorauthorizationcode() != null) {
            entry.setOperatorAuthorizationCode(c.getOperatorauthorizationcode());
        }
        if (c.getBankretrievalreferencenumber() != null) {
            entry.setBankRetrievalReferenceNumber(c.getBankretrievalreferencenumber());
        }
        if (c.getBanksystemtraceauditnumber() != null) {
            entry.setBankSystemTraceAuditNumber(c.getBanksystemtraceauditnumber());
        }
        if (c.getBankresponsetime() != null) {
            entry.setBankResponseTime(c.getBankresponsetime());
        }
        if (c.getBankresponsecode() != null) {
            entry.setBankResponseCode(c.getBankresponsecode().intValue());
            entry.setBankResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankResponseCode, null, c.getBankresponsecode()));
        }
        if (c.getBankrejectreason() != null) {
            entry.setBankRejectReason(c.getBankrejectreason());
        }
        if (c.getBankerrortext() != null) {
            entry.setBankErrorText(c.getBankerrortext());
        }
        if (c.getBankauthorizationcode() != null) {
            entry.setBankAuthorizationCode(c.getBankauthorizationcode());
        }
        if (c.getLastupdatetime() != null) {
            entry.setLastUpdateTime(c.getLastupdatetime());
        }
        if (c.getUpdatedby() != null) {
            entry.setUpdatedBy(c.getUpdatedby());
        }
        if (c.getReversalcount() != null) {
            entry.setReversalCount(c.getReversalcount().intValue());
        }
        if (c.getDistributionChainLvl() != null) {
            entry.setDistributionLevel(((Long)c.getDistributionChainLvl().getDistributionlevel()).intValue());
        }
        if (c.getSourcemessage() != null) {
            entry.setSourceMessage(c.getSourcemessage());
        }
        if (c.getSourceip() != null) {
            entry.setSourceIP(c.getSourceip());
        }
        if (c.getSourceterminalid() != null) {
            entry.setSourceTerminalID(c.getSourceterminalid());
        }
        if (c.getServletpath() != null) {
            entry.setServletPath(c.getServletpath());
        }
        if (c.getLevelpermissions() != null) {
            entry.setLevelPermissions(c.getLevelpermissions().intValue());
            entry.setLevelPermissionsText(enumTextService.getLevelPermissionsText(c.getLevelpermissions().intValue()));
        }
        if (c.getTopupperiod() != null) {
            entry.setTopupPeriod(c.getTopupperiod().longValue());
        }
        if (c.getBankreversalresponsetime() != null) {
            entry.setBankReversalResponseTime(c.getBankreversalresponsetime());
        }
        if (c.getBankreversalresponsecode() != null) {
            entry.setBankReversalResponseCode(c.getBankreversalresponsecode().intValue());
            entry.setBankReversalResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankReversalResponseCode, null, c.getBankreversalresponsecode()));
        }
        if (c.getBankreversalrejectreason() != null) {
            entry.setBankReversalRejectReason(c.getBankreversalrejectreason());
            entry.setBankReversalRejectReasonText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankReversalRejectReason, null, c.getBankreversalrejectreason()));
        }
        if (c.getBankreversalerrortext() != null) {
            entry.setBankReversalErrorText(c.getBankreversalerrortext());
        }
        if (c.getBankreversalauthorizationcode() != null) {
            entry.setBankReversalAuthorizationCode(c.getBankreversalauthorizationcode());
        }
        if (c.getCsraction() != null) {
            entry.setCSRAction(c.getCsraction().intValue());
            entry.setCSRActionText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_CSRAction, null, c.getCsraction()));
        }
        if (c.getCsractiontime() != null) {
            entry.setCSRActionTime(c.getCsractiontime());
        }
        if (c.getCsruserid() != null) {
            entry.setCSRUserID(c.getCsruserid().longValue());
        }
        if (c.getCsrusername() != null) {
            entry.setCSRUserName(c.getCsrusername());
        }
        if (c.getCsrcomment() != null) {
            entry.setCSRComment(c.getCsrcomment());
        }
        if (c.getIso8583Processingcode() != null) {
            entry.setISO8583_ProcessingCode(c.getIso8583Processingcode());
            entry.setISO8583_ProcessingCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_ProcessingCode, null, c.getIso8583Processingcode()));
        }
        if (c.getIso8583Systemtraceauditnumber() != null) {
            entry.setISO8583_SystemTraceAuditNumber(c.getIso8583Systemtraceauditnumber());
        }
        if (null != c.getIso8583Retrievalreferencenum()) {
            entry.setISO8583_RetrievalReferenceNum(c.getIso8583Retrievalreferencenum());
        }
        if (c.getIso8583Localtxntimehhmmss() != null) {
            entry.setISO8583_LocalTxnTimeHhmmss(c.getIso8583Localtxntimehhmmss());
        }
        if (c.getIso8583Merchanttype() != null) {
            entry.setISO8583_MerchantType(c.getIso8583Merchanttype());
            entry.setISO8583_MerchantTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_MerchantType, null, c.getIso8583Merchanttype()));
        }
        if (c.getIso8583Acquiringinstidcode() != null) {
            entry.setISO8583_AcquiringInstIdCode(c.getIso8583Acquiringinstidcode().intValue());
            entry.setISO8583_AcquiringInstitutionIdentificationCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_AcquiringInstIdCode, null, c.getIso8583Acquiringinstidcode()));
        }
        if (c.getIso8583Cardacceptoridcode() != null) {
            entry.setISO8583_CardAcceptorIdCode(c.getIso8583Cardacceptoridcode());
        }
        if (c.getIso8583Variant() != null) {
            entry.setISO8583_Variant(c.getIso8583Variant());
            entry.setISO8583_VariantText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_Variant, null, c.getIso8583Variant()));
        }
        if (c.getIso8583Responsecode() != null) {
            entry.setISO8583_ResponseCode(c.getIso8583Responsecode());
            entry.setISO8583_ResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_ResponseCode, null, c.getIso8583Responsecode()));
        }
        if (c.getLetterOfPurchase() != null) {
            if (c.getLetterOfPurchase().getActualamountpaid() != null) {
                entry.setPaidAmount(c.getLetterOfPurchase().getActualamountpaid());
            }
            if (c.getLetterOfPurchase().getId() != null) {
                entry.setLOPID(c.getLetterOfPurchase().getId().longValue());
            }
        }
        if (pct != null) {
            if (pct.getOperatoractionrequired() != null) {
                entry.setOperatorActionRequired(pct.getOperatoractionrequired());
            }
            if (pct.getLocalrevertrequired() != null) {
                entry.setLocalRevertRequired(pct.getLocalrevertrequired());
            }
            if (pct.getBankreversalrequired() != null) {
                entry.setBankReversalRequired(pct.getBankreversalrequired());
            }
        }
        if (c.getBulkuploadlinenumber() != null) {
            entry.setBulkUploadLineNumber(c.getBulkuploadlinenumber().intValue());
        }
        if (c.getBulkuploadid() != null) {
            entry.setBulkUploadID(c.getBulkuploadid().longValue());
        }
        if (c.getOperatorreversalcount() != null) {
            entry.setOperatorReversalCount(c.getOperatorreversalcount().intValue());
        }
        if (c.getOperatorreversalerrortext() != null) {
            entry.setOperatorReversalErrorText(c.getOperatorreversalerrortext());
        }
        if (c.getOperatorreversalrejectreason() != null) {
            entry.setOperatorReversalRejectReason(c.getOperatorreversalrejectreason());
        }
        if (c.getOperatorreversalresponsecode() != null) {
            entry.setOperatorReversalResponseCode(c.getOperatorreversalresponsecode().intValue());
            entry.setOperatorReversalResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorReversalResponseCode, null, c.getOperatorreversalresponsecode()));
        }
        if (c.getOperatorreversalresponsetime() != null) {
            entry.setOperatorReversalResponseTime(c.getOperatorreversalresponsetime());
        }

        if (c.getOperatorrrn() != null) {
            entry.setOperatorRRN(c.getOperatorrrn());
        }
        if (c.getOperatorstan() != null) {
            entry.setOperatorSTAN(c.getOperatorstan());
        }
        if (c.getCreditCardTransaction() != null && c.getCreditCardTransaction().getId() != null) {
            entry.setCreditCardTransactionID(c.getCreditCardTransaction().getId().longValue());
//            entry.setPaymentMethod(c.getCreditCardTransaction().getPaymentMethod());
//            entry.setErrCode(c.getCreditCardTransaction().getErrCode());
//            entry.setUserCode(c.getCreditCardTransaction().getUserCode());
//            entry.setTransStatus(c.getCreditCardTransaction().getTransStatus());
//            entry.setCurrCode(c.getCreditCardTransaction().getCurrCode());
//            entry.setEUI(c.getCreditCardTransaction().getEUI());
//            entry.setTransactionDate(c.getCreditCardTransaction().getTransactionDate());
//            entry.setTransType(c.getCreditCardTransaction().getTransType());
//            entry.setIsBlackListed(c.getCreditCardTransaction().getIsBlackListed());
//            entry.setFraudRiskLevel(c.getCreditCardTransaction().getFraudRiskLevel());
//            entry.setFraudRiskScore(c.getCreditCardTransaction().getFraudRiskScore());
//            entry.setExceedHighRisk(c.getCreditCardTransaction().getExceedHighRisk());
//            entry.setCardType(c.getCreditCardTransaction().getCardType());
//            entry.setCardNoPartial(c.getCreditCardTransaction().getCardNoPartial());
//            entry.setCardName(c.getCreditCardTransaction().getCardName());
//            entry.setAcquirerBank(c.getCreditCardTransaction().getAcquirerBank());
//            entry.setBankResCode(c.getCreditCardTransaction().getBankResCode());
//            entry.setBankResMsg(c.getCreditCardTransaction().getBankResMsg());
//            entry.setAuthID(c.getCreditCardTransaction().getAuthID());
//            entry.setBankReference(c.getCreditCardTransaction().getBankReference());
//            entry.setWhiteListCard(c.getCreditCardTransaction().getWhiteListCard());
//            entry.setBillReferenceNumber(c.getCreditCardTransaction().getBillReferenceNumber());
        }
        if (c.getProductindicatorcode() != null) {
            entry.setProductIndicatorCode(c.getProductindicatorcode());
        }
        entry.setCreateTime(c.getCreatetime());
        entry.setCreatedBy(c.getCreatedby());

        entry.setAmountText(c.getAmount() + GeneralConstants.SINGLE_SPACE + c.getCurrency());
        entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, c.getTransferstatus()));
        entry.setCommodityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, c.getCommodity()));
        entry.setAccessMethodText(channelCodeService.getChannelNameBySourceApplication((c.getSourceapplication()).intValue()));
        entry.setTransferFailureReasonText(CmFinoFIX.TransferStatus_Completed.equals(c.getTransferstatus())?"":enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, c.getTransferfailurereason()));
        entry.setSourcePocketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SourcePocketType, null, c.getSourcepockettype()));
        entry.setDestPocketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_DestPocketType, null, c.getDestpockettype()));
        entry.setOperatorResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorResponseCode, null, c.getOperatorresponsecode()));
        entry.setOperatorCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorCodeForRouting, null, c.getOperatorcode()));
//        entry.setBankCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting, null, c.getBankCode()));
        entry.setRecordVersion(((Long)c.getVersion()).intValue());
    }
	
	private void processCreditDebits(CMJSCommodityTransfer realMsg, CommodityTransfer c, CGEntries entry) {
    	entry.setTransactionID(c.getId().longValue());
    	if(realMsg.getSourceDestnPocketID()!=null){
    		if(c.getPocket() != null
    				&&c.getPocket().getId().equals(realMsg.getSourceDestnPocketID())){
    			entry.setDebitAmount(c.getAmount().add(c.getCharges()));
    			entry.setCreditAmount(null);
    			if(c.getSourcepocketbalance()!=null){
    			if((c.getTransferstatus()).equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
    			entry.setSourcePocketClosingBalance(new BigDecimal( c.getSourcepocketbalance()).subtract(entry.getDebitAmount()));
    			}else{
    				entry.setSourcePocketClosingBalance(new BigDecimal(c.getSourcepocketbalance()));
    			}
    			}
    		}else if(realMsg.getSourceDestnPocketID().equals(c.getDestpocketid())){
    			entry.setCreditAmount(c.getAmount());
    			entry.setDebitAmount(null);
    			if(c.getDestpocketbalance()!=null){
    			if((c.getTransferstatus()).equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
        			entry.setDestPocketClosingBalance(new BigDecimal(c.getDestpocketbalance()).add(entry.getCreditAmount()));
        			}else{
        				entry.setDestPocketClosingBalance(new BigDecimal(c.getDestpocketbalance()));
        			}
    			}
    		}
    	}else  if (realMsg.getSourceDestMDNAndID() != null) {
            String[] mdnAndID = realMsg.getSourceDestMDNAndID().split(",");
            Long mdnID = null;
            String mdn = null;
            if (mdnAndID.length == 2 && StringUtils.isNumeric(mdnAndID[1])) {
               mdnID = NumberUtils.toLong(mdnAndID[1]);
               mdn = mdnAndID[0];
            }
           if(c.getSubscriberMdn().getId().equals(mdnID)
        		   ||(c.getSourcemdn()!=null&&c.getSourcemdn().equals(mdn))){
        	   entry.setDebitAmount(c.getAmount().add(c.getCharges()));
        	   entry.setCreditAmount(null);
        	   if(c.getSourcepocketbalance()!=null){
        		if((c.getTransferstatus()).equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
        			entry.setSourcePocketClosingBalance(new BigDecimal(c.getSourcepocketbalance()).subtract(entry.getDebitAmount()));
        			}else{
        				entry.setSourcePocketClosingBalance(new BigDecimal(c.getSourcepocketbalance()));
        			}
        	   }
           }else if((c.getDestmdnid()!=null&&c.getDestmdnid().equals(mdnID))
        		   ||(c.getDestmdn()!=null&&c.getDestmdn().equals(mdn))){
        	   entry.setCreditAmount(c.getAmount());
        	   entry.setDebitAmount(null);
        	   if(c.getDestpocketbalance()!=null){
        	   if((c.getTransferstatus()).equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
       			entry.setDestPocketClosingBalance(new BigDecimal(c.getDestpocketbalance()).add(entry.getCreditAmount()));
       			}else{
       				entry.setDestPocketClosingBalance(new BigDecimal(c.getDestpocketbalance()));
       			}
        	   }
           }
        }
	}
}
