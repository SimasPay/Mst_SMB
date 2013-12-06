package com.mfino.uicore.fix.processor.impl;

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
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer.CGEntries;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CRPendingCommodityTransfer;
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
    public void updateMessage(CRCommodityTransfer c,
            CRPendingCommodityTransfer pct,
            CMJSCommodityTransfer.CGEntries entry, CMJSCommodityTransfer realMsg) {

        entry.setID(c.getID());
        entry.setTransactionID(c.getTransactionsLogByTransactionID().getID());
        //      entry.setJSMsgType(c.getMsgType());
        entry.setMSPID(c.getmFinoServiceProviderByMSPID().getID());
        entry.setTransferStatus(c.getTransferStatus());
        if (c.getTransferFailureReason() != null) {
            entry.setTransferFailureReason(c.getTransferFailureReason());
        }
        if (c.getNotificationCode() != null) {
            entry.setNotificationCode(c.getNotificationCode());
            String codeText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_NotificationCode, null, c.getNotificationCode());
            entry.setNotificationCodeName(c.getNotificationCode() + GeneralConstants.SINGLE_SPACE +(codeText!=null?codeText:""));
        }
        if (c.getUICategory() != null) {
            int type = c.getUICategory();
            entry.setTransactionUICategory(c.getUICategory());
            
            ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
    		Long sctlId = ctMapDao.getSCTLIdByCommodityTransferId(c.getID());
    		ServiceChargeTransactionLog sctl = null;
    		if (sctlId != null) {
    			sctl = sctlDAO.getById(sctlId);
    		}
       		if(sctl != null){
       			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
       			TransactionType tt = ttDAO.getById(sctl.getTransactionTypeID());
       			entry.setTransactionTypeText(tt.getDisplayName());
       		}else{
       			entry.setTransactionTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, c.getUICategory()));
       		}
       		// Added as part of GT Request to identify the internal transaction type like E-B, E-E, B-E, B-B
       		entry.setInternalTxnType(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, c.getUICategory()));
    		
            if (type == CmFinoFIX.TransactionUICategory_Empty_SVA || type == CmFinoFIX.TransactionUICategory_MA_Transfer || type == CmFinoFIX.TransactionUICategory_MA_Topup || type == CmFinoFIX.TransactionUICategory_BulkTransfer || type == CmFinoFIX.TransactionUICategory_BulkTopup) {
                if (c.getSubscriberBySourceSubscriberID() != null && c.getSubscriberBySourceSubscriberID().getUser() != null) {
                    entry.setSourceUserName(c.getSubscriberBySourceSubscriberID().getUser().getUsername());
                }
            }
            if (type == CmFinoFIX.TransactionUICategory_Empty_SVA || type == CmFinoFIX.TransactionUICategory_Distribute_LOP || type == CmFinoFIX.TransactionUICategory_MA_Transfer || type == CmFinoFIX.TransactionUICategory_BulkTransfer) {
                Long sId = c.getDestSubscriberID();
                SubscriberDAO subdao = DAOFactory.getInstance().getSubscriberDAO();
                Subscriber sub = subdao.getById(sId);
                if (sub != null) {
                    if (sub.getUser() != null) {
                        entry.setDestinationUserName(sub.getUser().getUsername());
                    }
                }
            }
        }
        if (c.getStartTime() != null) {
            entry.setStartTime(c.getStartTime());
        }
        if (c.getEndTime() != null) {
            entry.setEndTime(c.getEndTime());
        }
        if (c.getSourceReferenceID() != null) {
            entry.setSourceReferenceID(c.getSourceReferenceID());
        }
        if (c.getSourceMDN() != null) {
            entry.setSourceMDN(c.getSourceMDN());
        }
        entry.setSourceMDNID(c.getSubscriberMDNBySourceMDNID().getID());
        entry.setSourceSubscriberID(c.getSubscriberBySourceSubscriberID().getID());
        if (c.getSourceSubscriberName() != null) {
            entry.setSourceSubscriberName(c.getSourceSubscriberName());
        }
        entry.setSourcePocketType(c.getSourcePocketType());
        entry.setSourcePocketID(c.getPocketBySourcePocketID().getID());
        if (c.getSourcePocketBalance() != null) {
            entry.setSourcePocketBalance(c.getSourcePocketBalance());
        }
        if (c.getPocketBySourcePocketID() != null && c.getPocketBySourcePocketID().getPocketTemplate() != null) {
            entry.setSourcePocketTemplateDescription(c.getPocketBySourcePocketID().getPocketTemplate().getDescription());
            if (c.getSourceCardPAN() != null) {
//              entry.setSourceCardPAN(c.getSourceCardPAN());
          	 entry.setSourceCardPAN(c.getPocketBySourcePocketID().getCardPAN()!=null?c.getPocketBySourcePocketID().getCardPAN():"");
          }
        }
       
        if (c.getDestMDN() != null) {
            entry.setDestMDN(c.getDestMDN());
        }
        if (c.getDestMDNID() != null) {
            entry.setDestMDNID(c.getDestMDNID());
        }
        if (c.getDestSubscriberID() != null) {
            entry.setDestSubscriberID(c.getDestSubscriberID());
        }
        if (c.getDestSubscriberName() != null) {
            entry.setDestSubscriberName(c.getDestSubscriberName());
        } else {
            if (c.getDestMDN() != null) {
                String mdn = c.getDestMDN();
                SubscriberMDN submdn = subMdnDao.getByMDN(mdn);
                if (submdn != null) {
                    entry.setDestSubscriberName(submdn.getSubscriber().getFirstName() + " " + submdn.getSubscriber().getLastName());
                }
            }
        }
        if (c.getDestPocketType() != null) {
            entry.setDestPocketType(c.getDestPocketType());
        }
        if (c.getDestPocketID() != null) {
            entry.setDestPocketID(c.getDestPocketID());
        }
        if (c.getDestPocketBalance() != null) {
            entry.setDestPocketBalance(c.getDestPocketBalance());
        }
        if (c.getDestPocketID() != null) {
            Pocket destPocket = this.pocketDao.getById(c.getDestPocketID());
            if (destPocket != null && destPocket.getPocketTemplate() != null) {
                entry.setDestPocketTemplateDescription(destPocket.getPocketTemplate().getDescription());
            }
            if (c.getDestCardPAN() != null) {
//              entry.setDestCardPAN(c.getDestCardPAN());
            	entry.setDestCardPAN(destPocket.getCardPAN()!=null?destPocket.getCardPAN():"");
          }
        }
     
        if (c.getBillingType() != null) {
            entry.setBillingType(c.getBillingType());
            entry.setBillingTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BillingType, null, c.getBillingType()));
        }
        if(realMsg.getIsMiniStatementRequest()!=null&&realMsg.getIsMiniStatementRequest()){
        	entry.setServiceChargeTransactionLogID(ctMapDao.getSCTLIdByCommodityTransferId(c.getID()));
        	processCreditDebits(realMsg,c,entry);
        }
        entry.setAmount(c.getAmount());
        entry.setCharges(c.getCharges());
        entry.setTaxAmount(c.getTaxAmount());
        entry.setCommodity(c.getCommodity());
        if (c.getBucketType() != null) {
            entry.setBucketType(c.getBucketType());
            entry.setBucketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType, null, c.getBucketType()));
        }
        entry.setSourceApplication(c.getSourceApplication());
        if (c.getCurrency() != null) {
            entry.setCurrency(c.getCurrency());
        }
        if (c.getBankCode() != null) {
            entry.setBankCode(c.getBankCode());
        }
        if (c.getOperatorCode() != null) {
            entry.setOperatorCode(c.getOperatorCode());
        }
        if (c.getOperatorResponseTime() != null) {
            entry.setOperatorResponseTime(c.getOperatorResponseTime());
        }
        if (c.getOperatorResponseCode() != null) {
            entry.setOperatorResponseCode(c.getOperatorResponseCode());
        }
        if (c.getOperatorRejectReason() != null) {
            entry.setOperatorRejectReason(c.getOperatorRejectReason());
        }
        if (c.getOperatorErrorText() != null) {
            entry.setOperatorErrorText(c.getOperatorErrorText());
        }
        if (c.getOperatorAuthorizationCode() != null) {
            entry.setOperatorAuthorizationCode(c.getOperatorAuthorizationCode());
        }
        if (c.getBankRetrievalReferenceNumber() != null) {
            entry.setBankRetrievalReferenceNumber(c.getBankRetrievalReferenceNumber());
        }
        if (c.getBankSystemTraceAuditNumber() != null) {
            entry.setBankSystemTraceAuditNumber(c.getBankSystemTraceAuditNumber());
        }
        if (c.getBankResponseTime() != null) {
            entry.setBankResponseTime(c.getBankResponseTime());
        }
        if (c.getBankResponseCode() != null) {
            entry.setBankResponseCode(c.getBankResponseCode());
            entry.setBankResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankResponseCode, null, c.getBankResponseCode()));
        }
        if (c.getBankRejectReason() != null) {
            entry.setBankRejectReason(c.getBankRejectReason());
        }
        if (c.getBankErrorText() != null) {
            entry.setBankErrorText(c.getBankErrorText());
        }
        if (c.getBankAuthorizationCode() != null) {
            entry.setBankAuthorizationCode(c.getBankAuthorizationCode());
        }
        if (c.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(c.getLastUpdateTime());
        }
        if (c.getUpdatedBy() != null) {
            entry.setUpdatedBy(c.getUpdatedBy());
        }
        if (c.getReversalCount() != null) {
            entry.setReversalCount(c.getReversalCount());
        }
        if (c.getDistributionChainLevelByDCTLevelID() != null) {
            entry.setDistributionLevel(c.getDistributionChainLevelByDCTLevelID().getDistributionLevel());
        }
        if (c.getSourceMessage() != null) {
            entry.setSourceMessage(c.getSourceMessage());
        }
        if (c.getSourceIP() != null) {
            entry.setSourceIP(c.getSourceIP());
        }
        if (c.getSourceTerminalID() != null) {
            entry.setSourceTerminalID(c.getSourceTerminalID());
        }
        if (c.getServletPath() != null) {
            entry.setServletPath(c.getServletPath());
        }
        if (c.getLevelPermissions() != null) {
            entry.setLevelPermissions(c.getLevelPermissions());
            entry.setLevelPermissionsText(enumTextService.getLevelPermissionsText(c.getLevelPermissions()));
        }
        if (c.getTopupPeriod() != null) {
            entry.setTopupPeriod(c.getTopupPeriod());
        }
        if (c.getBankReversalResponseTime() != null) {
            entry.setBankReversalResponseTime(c.getBankReversalResponseTime());
        }
        if (c.getBankReversalResponseCode() != null) {
            entry.setBankReversalResponseCode(c.getBankReversalResponseCode());
            entry.setBankReversalResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankReversalResponseCode, null, c.getBankReversalResponseCode()));
        }
        if (c.getBankReversalRejectReason() != null) {
            entry.setBankReversalRejectReason(c.getBankReversalRejectReason());
            entry.setBankReversalRejectReasonText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankReversalRejectReason, null, c.getBankReversalRejectReason()));
        }
        if (c.getBankReversalErrorText() != null) {
            entry.setBankReversalErrorText(c.getBankReversalErrorText());
        }
        if (c.getBankReversalAuthorizationCode() != null) {
            entry.setBankReversalAuthorizationCode(c.getBankReversalAuthorizationCode());
        }
        if (c.getCSRAction() != null) {
            entry.setCSRAction(c.getCSRAction());
            entry.setCSRActionText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_CSRAction, null, c.getCSRAction()));
        }
        if (c.getCSRActionTime() != null) {
            entry.setCSRActionTime(c.getCSRActionTime());
        }
        if (c.getCSRUserID() != null) {
            entry.setCSRUserID(c.getCSRUserID());
        }
        if (c.getCSRUserName() != null) {
            entry.setCSRUserName(c.getCSRUserName());
        }
        if (c.getCSRComment() != null) {
            entry.setCSRComment(c.getCSRComment());
        }
        if (c.getISO8583_ProcessingCode() != null) {
            entry.setISO8583_ProcessingCode(c.getISO8583_ProcessingCode());
            entry.setISO8583_ProcessingCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_ProcessingCode, null, c.getISO8583_ProcessingCode()));
        }
        if (c.getISO8583_SystemTraceAuditNumber() != null) {
            entry.setISO8583_SystemTraceAuditNumber(c.getISO8583_SystemTraceAuditNumber());
        }
        if (null != c.getISO8583_RetrievalReferenceNum()) {
            entry.setISO8583_RetrievalReferenceNum(c.getISO8583_RetrievalReferenceNum());
        }
        if (c.getISO8583_LocalTxnTimeHhmmss() != null) {
            entry.setISO8583_LocalTxnTimeHhmmss(c.getISO8583_LocalTxnTimeHhmmss());
        }
        if (c.getISO8583_MerchantType() != null) {
            entry.setISO8583_MerchantType(c.getISO8583_MerchantType());
            entry.setISO8583_MerchantTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_MerchantType, null, c.getISO8583_MerchantType()));
        }
        if (c.getISO8583_AcquiringInstIdCode() != null) {
            entry.setISO8583_AcquiringInstIdCode(c.getISO8583_AcquiringInstIdCode());
            entry.setISO8583_AcquiringInstitutionIdentificationCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_AcquiringInstIdCode, null, c.getISO8583_AcquiringInstIdCode()));
        }
        if (c.getISO8583_CardAcceptorIdCode() != null) {
            entry.setISO8583_CardAcceptorIdCode(c.getISO8583_CardAcceptorIdCode());
        }
        if (c.getISO8583_Variant() != null) {
            entry.setISO8583_Variant(c.getISO8583_Variant());
            entry.setISO8583_VariantText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_Variant, null, c.getISO8583_Variant()));
        }
        if (c.getISO8583_ResponseCode() != null) {
            entry.setISO8583_ResponseCode(c.getISO8583_ResponseCode());
            entry.setISO8583_ResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_ResponseCode, null, c.getISO8583_ResponseCode()));
        }
        if (c.getLOP() != null) {
            if (c.getLOP().getActualAmountPaid() != null) {
                entry.setPaidAmount(c.getLOP().getActualAmountPaid());
            }
            if (c.getLOP().getID() != null) {
                entry.setLOPID(c.getLOP().getID());
            }
        }
        if (pct != null) {
            if (pct.getOperatorActionRequired() != null) {
                entry.setOperatorActionRequired(pct.getOperatorActionRequired());
            }
            if (pct.getLocalRevertRequired() != null) {
                entry.setLocalRevertRequired(pct.getLocalRevertRequired());
            }
            if (pct.getBankReversalRequired() != null) {
                entry.setBankReversalRequired(pct.getBankReversalRequired());
            }
        }
        if (c.getBulkUploadLineNumber() != null) {
            entry.setBulkUploadLineNumber(c.getBulkUploadLineNumber());
        }
        if (c.getBulkUploadID() != null) {
            entry.setBulkUploadID(c.getBulkUploadID());
        }
        if (c.getOperatorReversalCount() != null) {
            entry.setOperatorReversalCount(c.getOperatorReversalCount());
        }
        if (c.getOperatorReversalErrorText() != null) {
            entry.setOperatorReversalErrorText(c.getOperatorReversalErrorText());
        }
        if (c.getOperatorReversalRejectReason() != null) {
            entry.setOperatorReversalRejectReason(c.getOperatorReversalRejectReason());
        }
        if (c.getOperatorReversalResponseCode() != null) {
            entry.setOperatorReversalResponseCode(c.getOperatorReversalResponseCode());
            entry.setOperatorReversalResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorReversalResponseCode, null, c.getOperatorReversalResponseCode()));
        }
        if (c.getOperatorReversalResponseTime() != null) {
            entry.setOperatorReversalResponseTime(c.getOperatorReversalResponseTime());
        }

        if (c.getOperatorRRN() != null) {
            entry.setOperatorRRN(c.getOperatorRRN());
        }
        if (c.getOperatorSTAN() != null) {
            entry.setOperatorSTAN(c.getOperatorSTAN());
        }
        if (c.getCreditCardTransaction() != null && c.getCreditCardTransaction().getID() != null) {
            entry.setCreditCardTransactionID(c.getCreditCardTransaction().getID());
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
        if (c.getProductIndicatorCode() != null) {
            entry.setProductIndicatorCode(c.getProductIndicatorCode());
        }
        entry.setCreateTime(c.getCreateTime());
        entry.setCreatedBy(c.getCreatedBy());

        entry.setAmountText(c.getAmount() + GeneralConstants.SINGLE_SPACE + c.getCurrency());
        entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, c.getTransferStatus()));
        entry.setCommodityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, c.getCommodity()));
        entry.setAccessMethodText(channelCodeService.getChannelNameBySourceApplication(c.getSourceApplication()));
        entry.setTransferFailureReasonText(CmFinoFIX.TransferStatus_Completed.equals(c.getTransferStatus())?"":enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, c.getTransferFailureReason()));
        entry.setSourcePocketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SourcePocketType, null, c.getSourcePocketType()));
        entry.setDestPocketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_DestPocketType, null, c.getDestPocketType()));
        entry.setOperatorResponseCodeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorResponseCode, null, c.getOperatorResponseCode()));
        entry.setOperatorCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorCodeForRouting, null, c.getOperatorCode()));
//        entry.setBankCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting, null, c.getBankCode()));
        entry.setRecordVersion(c.getVersion());
    }
	
	private void processCreditDebits(CMJSCommodityTransfer realMsg, CRCommodityTransfer c, CGEntries entry) {
    	entry.setTransactionID(c.getID());
    	if(realMsg.getSourceDestnPocketID()!=null){
    		if(c.getPocketBySourcePocketID()!=null
    				&&c.getPocketBySourcePocketID().getID().equals(realMsg.getSourceDestnPocketID())){
    			entry.setDebitAmount(c.getAmount().add(c.getCharges()));
    			entry.setCreditAmount(null);
    			if(c.getSourcePocketBalance()!=null){
    			if(c.getTransferStatus().equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
    			entry.setSourcePocketClosingBalance(c.getSourcePocketBalance().subtract(entry.getDebitAmount()));
    			}else{
    				entry.setSourcePocketClosingBalance(c.getSourcePocketBalance());
    			}
    			}
    		}else if(realMsg.getSourceDestnPocketID().equals(c.getDestPocketID())){
    			entry.setCreditAmount(c.getAmount());
    			entry.setDebitAmount(null);
    			if(c.getDestPocketBalance()!=null){
    			if(c.getTransferStatus().equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
        			entry.setDestPocketClosingBalance(c.getDestPocketBalance().add(entry.getCreditAmount()));
        			}else{
        				entry.setDestPocketClosingBalance(c.getDestPocketBalance());
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
           if(c.getSubscriberMDNBySourceMDNID().getID().equals(mdnID)
        		   ||(c.getSourceMDN()!=null&&c.getSourceMDN().equals(mdn))){
        	   entry.setDebitAmount(c.getAmount().add(c.getCharges()));
        	   entry.setCreditAmount(null);
        	   if(c.getSourcePocketBalance()!=null){
        		if(c.getTransferStatus().equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
        			entry.setSourcePocketClosingBalance(c.getSourcePocketBalance().subtract(entry.getDebitAmount()));
        			}else{
        				entry.setSourcePocketClosingBalance(c.getSourcePocketBalance());
        			}
        	   }
           }else if((c.getDestMDNID()!=null&&c.getDestMDNID().equals(mdnID))
        		   ||(c.getDestMDN()!=null&&c.getDestMDN().equals(mdn))){
        	   entry.setCreditAmount(c.getAmount());
        	   entry.setDebitAmount(null);
        	   if(c.getDestPocketBalance()!=null){
        	   if(c.getTransferStatus().equals(CmFinoFIX.TransactionsTransferStatus_Completed)){
       			entry.setDestPocketClosingBalance(c.getDestPocketBalance().add(entry.getCreditAmount()));
       			}else{
       				entry.setDestPocketClosingBalance(c.getDestPocketBalance());
       			}
        	   }
           }
        }
	}
}
