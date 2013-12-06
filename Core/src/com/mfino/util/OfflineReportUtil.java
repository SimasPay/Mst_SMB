/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class OfflineReportUtil {
    private static final Map<Integer, String> msgTypeVsUICategory = new HashMap<Integer, String>(100);
    private static final Map<Integer, Integer> requestVsResponse = new HashMap<Integer, Integer>(20);
    private static final Map<Integer, Integer> requestVsReversal = new HashMap<Integer, Integer>(20);
//
//    public static final String BOB_ACCOUNT = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, CmFinoFIX.PocketType_BOBAccount);
//    public static final String SVA = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, CmFinoFIX.PocketType_SVA);
//    public static final String MA_CHECK_INVENTORY = "MA_Check_Inventory";
//    public static final String CHANGE_MA_PIN = "Change_MA_PIN";
//    public static final String CHANGE_MPIN = "Change_MPIN";
//    public static final String RESET_MA_PIN = "Reset_MA_PIN";
//    public static final String RESET_MPIN = "Reset_MPIN";
//    public static final String MCOMM_ACTIVATION = "Mcomm_Activation";
//    public static final String DOMPET_BALANCE_INQUIRY = "Dompet_Balance_Inquiry";
//    public static final String EMONEY_BALANCE_INQUIRY = "EMoney_Balance_Inquiry";
//    public static final String DOMPET_SELF_TOPUP = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_Dompet_Self_Topup);
//    public static final String DOMPET_TOPUP_ANOTHER = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_Dompet_Topup_Another);
//    public static final String DOMPET_MONEY_TRANSFER = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer);
//    public static final String DOMPET_LAST_3 = "Dompet_Last_3";
//    public static final String EMONEY_LAST_3 = "EMoney_Last_3";
//    public static final String MA_LAST_3 = "MA_Last_3";
//    public static final String SHARELOAD = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_Shareload);
//    public static final String MA_TOPUP = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_MA_Topup);
//    public static final String MA_TRANSFER = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_MA_Transfer);
//    public static final String BULK_TOPUP = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_BulkTopup);
//    public static final String BULK_TRANSFER = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_BulkTransfer);
//    public static final String BANK_CHANNEL_PAYMENT = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_Bank_Channel_Payment);
//    public static final String BANK_CHANNEL_TOPUP = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_Bank_Channel_Topup);
//    public static final String DISTRIBUTE_LOP = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_Distribute_LOP);
//    public static final String VA_EMONEY_CASHIN = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_VA_EMoney_CashIn);
//    public static final String VA_TOPUP = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_VA_Topup);
//    public static final String VA_PAYMENT = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, CmFinoFIX.TransactionUICategory_VA_Payment);
//    public static final String VA_EMONEY_CASHIN_INQUIRY = "VA_EMoney_CashIn_Inquiry";
//    public static final String VA_TOPUP_INQUIRY = "VA_Topup_Inquiry";
//    public static final String VA_PAYMENT_INQUIRY = "VA_Payment_Inquiry";
//	    private static final List<String> systemMDNs = new ArrayList<String>();
//	    private static final List<Integer> msgToOperator = new ArrayList<Integer>();
//	    private static final List<Integer> msgToBank = new ArrayList<Integer>();
//	    public static final String CBOSS_PREPAID = "CBOSS Prepaid";
//
//    static {                
//        systemMDNs.add(ConfigurationUtil.getSystemMDN());
//        systemMDNs.add(ConfigurationUtil.getLOPDistributorMDN());
//        systemMDNs.add(ConfigurationUtil.getSVACollectorMDN());
//        systemMDNs.add(ConfigurationUtil.getPrepaidSourceMDN());
//        systemMDNs.add(ConfigurationUtil.getPostpaidSourceMDN());
//
//        msgToOperator.add(CmFinoFIX.MsgType_CommodityTransferToOperator);
//        msgToOperator.add(CmFinoFIX.MsgType_GetMDNBillDebtsToOperator);
//        msgToOperator.add(CmFinoFIX.MsgType_GetMDNInfoToOperator);
//        
//        
//        msgToBank.add(CmFinoFIX.MsgType_BalanceInquiryToBank);
//        msgToBank.add(CmFinoFIX.MsgType_BankAccountPinSetupToBank);
//        msgToBank.add(CmFinoFIX.MsgType_BankAccountTopupToBank);
//        msgToBank.add(CmFinoFIX.MsgType_BankAccountTopupReversalToBank);
//        msgToBank.add(CmFinoFIX.MsgType_ChangeBankAccountPinToBank);
//        msgToBank.add(CmFinoFIX.MsgType_GetLastTransactionsToBank);
//        msgToBank.add(CmFinoFIX.MsgType_MoneyTransferToBank);
//        msgToBank.add(CmFinoFIX.MsgType_MoneyTransferReversalToBank);
//        msgToBank.add(CmFinoFIX.MsgType_TransferInquiryToBank);
//
//        msgToBank.add(CmFinoFIX.MsgType_BankChannelPaymentRequest);
//        msgToBank.add(CmFinoFIX.MsgType_BankChannelQueryRequest);
//        msgToBank.add(CmFinoFIX.MsgType_BankChannelTopupRequest);
//
//
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankAccountActivation, MCOMM_ACTIVATION);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankAccountBalanceInquiry, ""); //Could be Dompet_Balance_Inquiry or Emoney_Balance_Inquiry
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankAccountToBankAccount, DOMPET_MONEY_TRANSFER); //Could be emoney Txns as well....
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankAccountTopup, ""); //self or others?
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankChannelPaymentRequest, BANK_CHANNEL_PAYMENT);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankChannelQueryRequest, "Bank_Channel_Inquiry");
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankChannelTopupRequest, BANK_CHANNEL_TOPUP);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BulkUploadDistribute, BULK_TRANSFER);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BulkUploadTopup, BULK_TOPUP);
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_ChangePin, ""); //MPIN or MA PIN depending on the servlet path
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_CheckBalance, MA_CHECK_INVENTORY);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_EmptySVAPocket,""); //Could be emoney or airtime empty       
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_GetBankAccountTransactions, ""); //could be Dompet_Last_3 or Emoney_Last_3
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_GetMDNBillDebtsFromOperator, ""); //??
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_GetMDNBillDebtsToOperator, ""); //??
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_GetTransactions, ""); //Sub Last 3 or MA Last 3 depending on the servlet path
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HChangePin, CHANGE_MA_PIN); //MPIN or MA PIN depending on the servlet path
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HCheckInventory, MA_CHECK_INVENTORY);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HDistribute, MA_TRANSFER);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HGenerateLOP, "Generate_LOP"); //Not present in the list sent by SMART
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HLOPDistribute, DISTRIBUTE_LOP);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HLast5Transfers, MA_LAST_3);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HResetPin, ""); //MPIN or MA PIN depending on the servlet path
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HShareLoad, SHARELOAD);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HTopup, MA_TOPUP);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_H2HTransferInquiry, "H2HTransferInquiry");
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_MobileAgentDistribute, MA_TRANSFER);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_MobileAgentRecharge, MA_TOPUP);
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_MoneyTransferToBank, ""); //??
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_ResetPin, ""); //Reset MPIN or MA PIN depending on the servlet path
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_ShareLoad, SHARELOAD);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_SubscriberActivation, MCOMM_ACTIVATION);
//        
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_TransfersBulkUpload, BULK_TRANSFER);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankChannelEMoneyInquiry, VA_EMONEY_CASHIN_INQUIRY);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_VABankChannelTopupQueryRequest, VA_TOPUP_INQUIRY);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_VABankChannelPaymentQueryRequest, VA_PAYMENT_INQUIRY);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_BankChannelEMoneyTransfer, VA_EMONEY_CASHIN);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_VABankChannelTopupRequest, VA_TOPUP);
//        msgTypeVsUICategory.put(CmFinoFIX.MsgType_VABankChannelPaymentRequest, VA_PAYMENT);
//
//        requestVsResponse.put(CmFinoFIX.MsgType_BalanceInquiryToBank, CmFinoFIX.MsgType_BalanceInquiryFromBank);
//        requestVsResponse.put(CmFinoFIX.MsgType_BankAccountPinSetupToBank, CmFinoFIX.MsgType_BankResponse);
//        requestVsResponse.put(CmFinoFIX.MsgType_BankAccountTopupToBank, CmFinoFIX.MsgType_BankResponse);
//        requestVsResponse.put(CmFinoFIX.MsgType_BankAccountTopupReversalToBank, CmFinoFIX.MsgType_BankResponse);
//        requestVsResponse.put(CmFinoFIX.MsgType_CommodityTransferToOperator, CmFinoFIX.MsgType_CommodityTransferFromOperator);
//        requestVsResponse.put(CmFinoFIX.MsgType_GetLastTransactionsToBank, CmFinoFIX.MsgType_GetLastTransactionsFromBank);
//        requestVsResponse.put(CmFinoFIX.MsgType_MoneyTransferToBank, CmFinoFIX.MsgType_MoneyTransferFromBank);
//        requestVsResponse.put(CmFinoFIX.MsgType_GetMDNBillDebtsToOperator, CmFinoFIX.MsgType_GetMDNBillDebtsFromOperator);
//        requestVsResponse.put(CmFinoFIX.MsgType_GetMDNInfoToOperator, CmFinoFIX.MsgType_GetMDNInfoFromOperator);
//        requestVsResponse.put(CmFinoFIX.MsgType_BankChannelPaymentRequest, CmFinoFIX.MsgType_BankChannelResponse);
//        requestVsResponse.put(CmFinoFIX.MsgType_BankChannelQueryRequest, CmFinoFIX.MsgType_BankChannelResponse);
//        requestVsResponse.put(CmFinoFIX.MsgType_BankChannelTopupRequest, CmFinoFIX.MsgType_BankChannelResponse);
//        requestVsResponse.put(CmFinoFIX.MsgType_TransferInquiryToBank, CmFinoFIX.MsgType_TransferInquiryFromBank);
//        requestVsResponse.put(CmFinoFIX.MsgType_MoneyTransferReversalToBank, CmFinoFIX.MsgType_BankResponse);
//        requestVsResponse.put(CmFinoFIX.MsgType_ChangeBankAccountPinToBank, CmFinoFIX.MsgType_BankResponse);
//
//        requestVsReversal.put(CmFinoFIX.MsgType_MoneyTransferToBank, CmFinoFIX.MsgType_MoneyTransferReversalToBank);
//        requestVsReversal.put(CmFinoFIX.MsgType_BankAccountTopup, CmFinoFIX.MsgType_BankAccountTopupReversalToBank);
//    }


    public static String stripRx(String mdn){
        if(StringUtils.isEmpty(mdn))
            return mdn;
        
        int index = mdn.indexOf('R');
        if(index > 0)
            return mdn.substring(0, index);
        return mdn;
    }

    public static Long getUploadIDFromMsg(String msg) {

        //No FIX Msg class in Java for 1099 :-(
        //Hack Hack Hack
        int startIndex = msg.indexOf(CmFinoFIX.TagID_BulkUploadID + "=");
        startIndex += 5;
        int endIndex = msg.indexOf(CmFinoFIX.TagID_BulkUploadLineNumber+"");
        String uploadIDStr = msg.substring(startIndex, endIndex);

        endIndex = 0;
        for (int i = 0; i < uploadIDStr.length(); i++) {
            char c = uploadIDStr.charAt(i);
            if (Character.isDigit(c)) {
                endIndex = i;
            } else {
                break;
            }
        }
        uploadIDStr = uploadIDStr.substring(0, endIndex+1);

        return Long.parseLong(uploadIDStr);
    }
}
