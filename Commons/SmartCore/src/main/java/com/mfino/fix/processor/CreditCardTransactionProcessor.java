/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.CreditCardTransactionQuery;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCreditCardTransaction;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.service.UserService;
import com.mfino.util.MfinoUtil;

/**
 *
 * @author Raju
 */
public class CreditCardTransactionProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSCreditCardTransaction realMsg = (CMJSCreditCardTransaction) msg;

        CreditCardTransactionDAO carddao = DAOFactory.getInstance().getCreditCardTransactionDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSCreditCardTransaction.CGEntries[] entries = realMsg.getEntries();

            for (CMJSCreditCardTransaction.CGEntries e : entries) {
                CreditCardTransaction s = carddao.getById(e.getID());

                // Check for Stale Data
                if (!e.getRecordVersion().equals(s.getVersion())) {
                    handleStaleDataException();
                }
//                updateEntity(s, e);
                carddao.save(s);
//                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            CreditCardTransactionQuery query=new CreditCardTransactionQuery();
            query.setId(realMsg.getIDSearch());
            if(StringUtils.isNotBlank(realMsg.getDestMDNSearch())) {
                query.setDestMdn(realMsg.getDestMDNSearch());
            }
            if(realMsg.getStartDateSearch() != null) {
                query.setCreateTimeGE(realMsg.getStartDateSearch());
            }
            if(realMsg.getEndDateSearch() != null) {
                query.setCreateTimeLT(realMsg.getEndDateSearch());
            }
            if(realMsg.getLastUpdateStartTime() != null) {
                query.setLastUpdateTimeGE(realMsg.getLastUpdateStartTime());
            }
            if(realMsg.getLastUpdateEndTime() != null) {
                query.setLastUpdateTimeLT(realMsg.getLastUpdateEndTime());
            }
            if(StringUtils.isNotBlank(realMsg.getAuthIdSearch())) {
                query.setAuthId(realMsg.getAuthIdSearch());
            }
            if(realMsg.getTransactionIdSearch() != null) {
                query.setTransactionId(realMsg.getTransactionIdSearch());
            }
            if(StringUtils.isNotBlank(realMsg.getBankReferenceNumberSearch())) {
                query.setBankReferenceNumber(realMsg.getBankReferenceNumberSearch());
            }
            if(UserService.getUserCompany()!=null){
            	query.setCompany(UserService.getUserCompany());
            }
            if(StringUtils.isNotBlank(realMsg.getOperationSearch()))
            	query.setOperation(realMsg.getOperationSearch());
            if(StringUtils.isNotBlank(realMsg.getTransStatusSearch()))
            	query.setTransStatus(realMsg.getTransStatusSearch());
            
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<CreditCardTransaction> results = carddao.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                CreditCardTransaction card = results.get(i);
                CMJSCreditCardTransaction.CGEntries entry = new CMJSCreditCardTransaction.CGEntries();                      
                updateMessage(card, entry);
                realMsg.getEntries()[i] = entry;
            }
           realMsg.setsuccess(CmFinoFIX.Boolean_True);
           realMsg.settotal(query.getTotal());
        } 
//        else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
//            CMJSCreditCardTransaction.CGEntries[] entries = realMsg.getEntries();
//
//            for (CMJSCreditCardTransaction.CGEntries e : entries) {
//                CreditCardTransaction card = new CreditCardTransaction();
////                updateEntity(card, e);
//                carddao.save(card);
////                updateMessage(card, e);
//            }
//
//            realMsg.setsuccess(CmFinoFIX.Boolean_True);
//            realMsg.settotal(entries.length);
//        }
        return realMsg;
    }

    private void updateMessage(CreditCardTransaction card, CMJSCreditCardTransaction.CGEntries entry) {
        entry.setID(card.getID());
        if (card.getPaymentMethod() != null) {
            entry.setPaymentMethod(card.getPaymentMethod());
        }
        if (card.getErrCode() != null) {
            entry.setErrCode(card.getErrCode());
        }
        if(card.getCCFailureReason()!=null){
        	entry.setCCFailureReason(card.getCCFailureReason());
        }
        if(card.getCCBucketType()!=null){
        	entry.setCCBucketType(card.getCCBucketType());
        }
        if (card.getUserCode() != null) {
            entry.setUserCode(card.getUserCode());
        }
        if(card.getMDN()!=null){
        	entry.setMDN(card.getMDN());
        }
        if (card.getTransStatus() != null) {
            entry.setTransStatus(card.getTransStatus());
        }
        if (card.getCurrCode() != null) {
            entry.setCurrCode(card.getCurrCode());
            entry.setCurrencyName(MfinoUtil.getCurrencyName(card.getCurrCode()));
        }
        if(card.getNSIATransCompletionTime()!=null){
        	entry.setNSIATransCompletionTime(card.getNSIATransCompletionTime());
        }
        if (card.getEUI() != null) {
            entry.setEUI(card.getEUI());
        }
        if (card.getTransactionDate() != null) {
            entry.setTransactionDate(card.getTransactionDate());
        }
        if (card.getTransType() != null) {
            entry.setTransType(card.getTransType());
        }
        if (card.getIsBlackListed() != null) {
            entry.setIsBlackListed(card.getIsBlackListed());
        }
        if (card.getFraudRiskLevel() != null) {
            entry.setFraudRiskLevel(card.getFraudRiskLevel());
        }
        if (card.getFraudRiskScore() != null) {
            entry.setFraudRiskScore(card.getFraudRiskScore());
        }
        if (card.getExceedHighRisk() != null) {
            entry.setExceedHighRisk(card.getExceedHighRisk());
        }
        if (card.getCardType() != null) {
            entry.setCardType(card.getCardType());
        }
        if (card.getCardNoPartial() != null) {
            entry.setCardNoPartial(card.getCardNoPartial());
        }
        if (card.getCardName() != null) {
            entry.setCardName(card.getCardName());
        }
        if(card.getAmount() != null) {
            entry.setAmount(card.getAmount());
        }
        if (card.getAcquirerBank() != null) {
            entry.setAcquirerBank(card.getAcquirerBank());
        }
        if (card.getBankResCode() != null) {
            entry.setBankResCode(card.getBankResCode());
        }
        if (card.getBankResMsg() != null) {
            entry.setBankResMsg(card.getBankResMsg());
        }
        if (card.getAuthID() != null) {
            entry.setAuthID(card.getAuthID());
        }
        if (card.getBankReference() != null) {
            entry.setBankReference(card.getBankReference());
        }
        if (card.getWhiteListCard() != null) {
            entry.setWhiteListCard(card.getWhiteListCard());
        }
        if (card.getBillReferenceNumber() != null) {
            entry.setBillReferenceNumber(card.getBillReferenceNumber());
        }
        if(card.getTransactionID() != null) {
            entry.setTransactionID(card.getTransactionID());
        }
        if (card.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(card.getLastUpdateTime());
        }
        if (card.getUpdatedBy() != null) {
            entry.setUpdatedBy(card.getUpdatedBy());
        }
        if (card.getCreateTime() != null) {
            entry.setCreateTime(card.getCreateTime());
        }
        if (card.getCreatedBy() != null) {
            entry.setCreatedBy(card.getCreatedBy());
        }
        entry.setCCBucketTypeText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType, null, card.getCCBucketType()));
        entry.setCCFailureReasonText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CCFailureReason,null , card.getCCFailureReason()));
        entry.setOperation(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_Operation, null, card.getOperation()));
        if(card.getTransactionID()!=null){
            entry.setTransactionID(card.getTransactionID());	
        }
        if(card.getTransactionID()!=null && card.getCCFailureReason()==null){
            if(card.getCommodityTransferFromCreditCardTransactionID()!=null && card.getCommodityTransferFromCreditCardTransactionID().size()==1){           
            	entry.setCCFailureReasonText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null,card.getCommodityTransferFromCreditCardTransactionID().iterator().next().getTransferFailureReason()));            	
            }else if(card.getPendingCommodityTransferFromCreditCardTransactionID()!=null && card.getPendingCommodityTransferFromCreditCardTransactionID().size()==1){
            	entry.setCCFailureReasonText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null,card.getPendingCommodityTransferFromCreditCardTransactionID().iterator().next().getTransferFailureReason()));
            }
            
        }
//		String transType=null;
//        if ("1".equals(card.getOperation())) {
//			transType = "CC_Payment";
//		} else if ("2".equals(card.getOperation())) {
//			transType = "CC_TopUp";
//		}
//        entry.setOperation(transType);        
    }
}
