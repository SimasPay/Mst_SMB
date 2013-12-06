/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.CommodityTransferProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 *
 * @author sunil
 */
@Component("CommodityTransferExcelView")
public class CommodityTransferExcelView extends AbstractExcelView {

    private static final Integer start = 0;
    private DateFormat df = DateUtil.getExcelDateFormat();
   // private static final String CommodityTransfer_DOWNLOAD = "commoditytransfer";
    private static final String PendingCommodityTransfer_DOWNLOAD = "pendingcommoditytransfer";
    
    
    @Autowired
    @Qualifier("CommodityTransferProcessorImpl")
    private CommodityTransferProcessor commodityTransferProcessor;

    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, "Transfer ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "Time");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Transaction Type");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "Source MDN");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Destination MDN");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Amount");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Status");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "Status Reason");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "Channel Name");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Notification Code");

        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "Source Subscriber Name");

        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "Destination Subscriber Name");

        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "Source Pocket Type");

        HSSFCell header14 = getCell(sheet, currentRow, 13);
        setText(header14, "Destination Pocket Type");

        HSSFCell header15 = getCell(sheet, currentRow, 14);
        setText(header15, "Bucket Type");

        HSSFCell header16 = getCell(sheet, currentRow, 15);
        setText(header16, "Source Reference ID");

        HSSFCell header17 = getCell(sheet, currentRow, 16);
        setText(header17, "Source MDNID");

        HSSFCell header18 = getCell(sheet, currentRow, 17);
        setText(header18, "Destination MDNID");

        HSSFCell header19 = getCell(sheet, currentRow, 18);
        setText(header19, "Source SubscriberID");

        HSSFCell header20 = getCell(sheet, currentRow, 19);
        setText(header20, "Destination SubscriberID");

        HSSFCell header21 = getCell(sheet, currentRow, 20);
        setText(header21, "Bulk Upload id");

        HSSFCell header22 = getCell(sheet, currentRow, 21);
        setText(header22, "Source PocketID");

        HSSFCell header23 = getCell(sheet, currentRow, 22);
        setText(header23, "Destination PocketID");

        HSSFCell header24 = getCell(sheet, currentRow, 23);
        setText(header24, "Source Pocket Balance");

        HSSFCell header25 = getCell(sheet, currentRow, 24);
        setText(header25, "Destination Pocket Balance");

        HSSFCell header26 = getCell(sheet, currentRow, 25);
        setText(header26, "Source Account Number");

        HSSFCell header27 = getCell(sheet, currentRow, 26);
        setText(header27, "Destination Account Number");

        HSSFCell header28 = getCell(sheet, currentRow, 27);
        setText(header28, "Bank Code");

        HSSFCell header29 = getCell(sheet, currentRow, 28);
        setText(header29, "Operator Code");

        HSSFCell header30 = getCell(sheet, currentRow, 29);
        setText(header30, "Operator Response Time");

        HSSFCell header31 = getCell(sheet, currentRow, 30);
        setText(header31, "Operator Response Code");

        HSSFCell header32 = getCell(sheet, currentRow, 31);
        setText(header32, "Operator Authorization Code");

        HSSFCell header33 = getCell(sheet, currentRow, 32);
        setText(header33, "Bank Error Text");

        HSSFCell header34 = getCell(sheet, currentRow, 33);
        setText(header34, "Operator Error Text");

        HSSFCell header35 = getCell(sheet, currentRow, 34);
        setText(header35, "Create Time");

        HSSFCell header36 = getCell(sheet, currentRow, 35);
        setText(header36, "Last Update Time");

        HSSFCell header37 = getCell(sheet, currentRow, 36);
        setText(header37, "Last Reversal Time");

        HSSFCell header38 = getCell(sheet, currentRow, 37);
        setText(header38, "Created By");

        HSSFCell header39 = getCell(sheet, currentRow, 38);
        setText(header39, "Updated By");

        HSSFCell header40 = getCell(sheet, currentRow, 39);
        setText(header40, "Bank Reversal Response Time");

        HSSFCell header41 = getCell(sheet, currentRow, 40);
        setText(header41, "Bank Reversal Response Code");

        HSSFCell header42 = getCell(sheet, currentRow, 41);
        setText(header42, "Bank Reversal Reject Reason");

        HSSFCell header43 = getCell(sheet, currentRow, 42);
        setText(header43, "Bank Reversal Error Text");

        HSSFCell header44 = getCell(sheet, currentRow, 43);
        setText(header44, "Bank Reversal Authorization Code");

        HSSFCell header45 = getCell(sheet, currentRow, 44);
        setText(header45, "CSR Action");

        HSSFCell header46 = getCell(sheet, currentRow, 45);
        setText(header46, "CSR ActionTime");

        HSSFCell header47 = getCell(sheet, currentRow, 46);
        setText(header47, "CSR UserID");

        HSSFCell header48 = getCell(sheet, currentRow, 47);
        setText(header48, "CSR User Name");

        HSSFCell header49 = getCell(sheet, currentRow, 48);
        setText(header49, "CSR Comment");

        HSSFCell header50 = getCell(sheet, currentRow, 49);
        setText(header50, "System Trace Audit Number");

        HSSFCell header51 = getCell(sheet, currentRow, 50);
        setText(header51, "ISO8583_Response Code");

        HSSFCell header52 = getCell(sheet, currentRow, 51);
        setText(header52, "ISO8583_Variant");

        HSSFCell header53 = getCell(sheet, currentRow, 52);
        setText(header53, "ISO8583_Processing Code");

        HSSFCell header54 = getCell(sheet, currentRow, 53);
        setText(header54, "ISO8583_Merchant Type");

        HSSFCell header55 = getCell(sheet, currentRow, 54);
        setText(header55, "ISO8583_Local Transaction Time");

        HSSFCell header56 = getCell(sheet, currentRow, 55);
        setText(header56, "ISO8583_Card Acceptor Identification Code");

        HSSFCell header57 = getCell(sheet, currentRow, 56);
        setText(header57, "ISO8583_Acquiring Institution Identification Code");

        HSSFCell header58 = getCell(sheet, currentRow, 57);
        setText(header58, "Topup Period");

        HSSFCell header59 = getCell(sheet, currentRow, 58);
        setText(header59, "Servlet Path");

        HSSFCell header60 = getCell(sheet, currentRow, 59);
        setText(header60, "Source Message");

        HSSFCell header61 = getCell(sheet, currentRow, 60);
        setText(header61, "Source Terminal ID");

        HSSFCell header62 = getCell(sheet, currentRow, 61);
        setText(header62, "Source IP");

        HSSFCell header63 = getCell(sheet, currentRow, 62);
        setText(header63, "DCT Level Number");

        HSSFCell header64 = getCell(sheet, currentRow, 63);
        setText(header64, "LOP ID");

        HSSFCell header65 = getCell(sheet, currentRow, 64);
        setText(header65, "Level Permissions");

        HSSFCell header66 = getCell(sheet, currentRow, 65);
        setText(header66, "Reversal Count");

        HSSFCell header67 = getCell(sheet, currentRow, 66);
        setText(header67, "Commodity Type");

        HSSFCell header68 = getCell(sheet, currentRow, 67);
        setText(header68, "Bulk Upload LineNumber");

        HSSFCell header69 = getCell(sheet, currentRow, 68);
        setText(header69, "Currency");

        HSSFCell header70 = getCell(sheet, currentRow, 69);
        setText(header70, "Billing Type");

        HSSFCell header71 = getCell(sheet, currentRow, 70);
        setText(header71, "Source UserName");

        HSSFCell header72 = getCell(sheet, currentRow, 71);
        setText(header72, "Destination UserName");

        HSSFCell header73 = getCell(sheet, currentRow, 72);
        setText(header73, "Bank STAN");

        HSSFCell header74 = getCell(sheet, currentRow, 73);
        setText(header74, "Bank RRN");

        HSSFCell header75 = getCell(sheet, currentRow, 74);
        setText(header75, "Operator Retrieval Reference Number");

        HSSFCell header76 = getCell(sheet, currentRow, 75);
        setText(header76, "Operator System Trace Audit Number");

        HSSFCell header77 = getCell(sheet, currentRow, 76);
        setText(header77, "Operator Reversal Error Text");

        HSSFCell header78 = getCell(sheet, currentRow, 77);
        setText(header78, "Operator Reversal Response Time");

        HSSFCell header79 = getCell(sheet, currentRow, 78);
        setText(header79, "Operator Reversal Response Code");

        HSSFCell header80 = getCell(sheet, currentRow, 79);
        setText(header80, "Operator Reversal Reject Reason");

        HSSFCell header81 = getCell(sheet, currentRow, 80);
        setText(header81, "Product Indicator Code");
        
        HSSFCell header82 = getCell(sheet, currentRow, 81);
        setText(header82, "Internal Txn Type");
    }

    private void fillCommodityTransferCells(CMJSCommodityTransfer.CGEntries commoditytransfer, HSSFRow row) {
        row.createCell(0).setCellValue(commoditytransfer.getID());
        if (commoditytransfer.getStartTime() != null) {
            row.createCell(1).setCellValue(df.format(commoditytransfer.getStartTime()));
        } else {
            row.createCell(1).setCellValue("");
        }
        row.createCell(2).setCellValue(commoditytransfer.getTransactionTypeText());
        row.createCell(3).setCellValue(commoditytransfer.getSourceMDN());
        row.createCell(4).setCellValue(commoditytransfer.getDestMDN());
        if (commoditytransfer.getAmount() != null) {
            row.createCell(5).setCellValue(commoditytransfer.getAmount().doubleValue());
        } else {
            row.createCell(5).setCellValue("");
        }
        row.createCell(6).setCellValue(commoditytransfer.getTransferStatusText());
        row.createCell(7).setCellValue(commoditytransfer.getTransferFailureReasonText());
        row.createCell(8).setCellValue(commoditytransfer.getAccessMethodText());
        row.createCell(9).setCellValue(commoditytransfer.getNotificationCodeName());
        row.createCell(10).setCellValue(commoditytransfer.getSourceSubscriberName());
        row.createCell(11).setCellValue(commoditytransfer.getDestSubscriberName());
        row.createCell(12).setCellValue(commoditytransfer.getSourcePocketTypeText());
        row.createCell(13).setCellValue(commoditytransfer.getDestPocketTypeText());
        row.createCell(14).setCellValue(commoditytransfer.getBucketTypeText());
        row.createCell(15).setCellValue(commoditytransfer.getSourceReferenceID());
        if (commoditytransfer.getSourceMDNID() != null) {
            row.createCell(16).setCellValue(commoditytransfer.getSourceMDNID());
        } else {
            row.createCell(16).setCellValue("");
        }
        if (commoditytransfer.getDestMDNID() != null) {
            row.createCell(17).setCellValue(commoditytransfer.getDestMDNID());
        } else {
            row.createCell(17).setCellValue("");
        }
        if (commoditytransfer.getSourceSubscriberID() != null) {
            row.createCell(18).setCellValue(commoditytransfer.getSourceSubscriberID());
        } else {
            row.createCell(18).setCellValue("");
        }
        if (commoditytransfer.getDestSubscriberID() != null) {
            row.createCell(19).setCellValue(commoditytransfer.getDestSubscriberID());
        } else {
            row.createCell(19).setCellValue("");
        }
        if (commoditytransfer.getBulkUploadID() != null) {
            row.createCell(20).setCellValue(commoditytransfer.getBulkUploadID());
        } else {
            row.createCell(20).setCellValue("");
        }
        if (commoditytransfer.getSourcePocketID() != null) {
            row.createCell(21).setCellValue(commoditytransfer.getSourcePocketID());
        } else {
            row.createCell(21).setCellValue("");
        }
        if (commoditytransfer.getDestPocketID() != null) {
            row.createCell(22).setCellValue(commoditytransfer.getDestPocketID());
        } else {
            row.createCell(22).setCellValue("");
        }
        if (commoditytransfer.getSourcePocketBalance() != null) {
            row.createCell(23).setCellValue(commoditytransfer.getSourcePocketBalance().doubleValue());
        } else {
            row.createCell(23).setCellValue("");
        }
        if (commoditytransfer.getDestPocketBalance() != null) {
            row.createCell(24).setCellValue(commoditytransfer.getDestPocketBalance().doubleValue());
        } else {
            row.createCell(24).setCellValue("");
        }
        if (commoditytransfer.getSourceCardPAN() != null) {
            row.createCell(25).setCellValue(commoditytransfer.getSourceCardPAN());
        } else {
            row.createCell(25).setCellValue("");
        }
        if (commoditytransfer.getDestCardPAN() != null) {
            row.createCell(26).setCellValue(commoditytransfer.getDestCardPAN());
        } else {
            row.createCell(26).setCellValue("");
        }
        if(commoditytransfer.getBankCode() != null) {
            row.createCell(27).setCellValue(commoditytransfer.getBankCode());
        } else {
            row.createCell(27).setCellValue("");
        }
        row.createCell(28).setCellValue(commoditytransfer.getOperatorCodeForRoutingText());
        if (commoditytransfer.getOperatorResponseTime() != null) {
            row.createCell(29).setCellValue(df.format(commoditytransfer.getOperatorResponseTime()));
        } else {
            row.createCell(29).setCellValue("");
        }
        row.createCell(30).setCellValue(commoditytransfer.getOperatorResponseCodeText());
        if (commoditytransfer.getOperatorAuthorizationCode() != null) {
            row.createCell(31).setCellValue(commoditytransfer.getOperatorAuthorizationCode());
        } else {
            row.createCell(31).setCellValue("");
        }
        row.createCell(32).setCellValue(commoditytransfer.getBankErrorText());
        row.createCell(33).setCellValue(commoditytransfer.getOperatorErrorText());
        if (commoditytransfer.getCreateTime() != null) {
            row.createCell(34).setCellValue(df.format(commoditytransfer.getCreateTime()));
        } else {
            row.createCell(34).setCellValue("");
        }
        if (commoditytransfer.getLastUpdateTime() != null) {
            row.createCell(35).setCellValue(df.format(commoditytransfer.getLastUpdateTime()));
        } else {
            row.createCell(35).setCellValue("");
        }
        if (commoditytransfer.getLastReversalTime() != null) {
            row.createCell(36).setCellValue(df.format(commoditytransfer.getLastReversalTime()));
        } else {
            row.createCell(36).setCellValue("");
        }
        row.createCell(37).setCellValue(commoditytransfer.getCreatedBy());
        row.createCell(38).setCellValue(commoditytransfer.getUpdatedBy());
        if (commoditytransfer.getBankReversalResponseTime() != null) {
            row.createCell(39).setCellValue(df.format(commoditytransfer.getBankReversalResponseTime()));
        } else {
            row.createCell(39).setCellValue("");
        }
        row.createCell(40).setCellValue(commoditytransfer.getBankReversalResponseCodeText());
        row.createCell(41).setCellValue(commoditytransfer.getBankReversalRejectReasonText());
        row.createCell(42).setCellValue(commoditytransfer.getBankReversalErrorText());
        row.createCell(43).setCellValue(commoditytransfer.getBankReversalAuthorizationCode());
        row.createCell(44).setCellValue(commoditytransfer.getCSRActionText());
        if (commoditytransfer.getCSRActionTime() != null) {
            row.createCell(45).setCellValue(df.format(commoditytransfer.getCSRActionTime()));
        } else {
            row.createCell(45).setCellValue("");
        }
        if (commoditytransfer.getCSRUserID() != null) {
            row.createCell(46).setCellValue(commoditytransfer.getCSRUserID());
        } else {
            row.createCell(46).setCellValue("");
        }
        row.createCell(47).setCellValue(commoditytransfer.getCSRUserName());
        row.createCell(48).setCellValue(commoditytransfer.getCSRComment());
        row.createCell(49).setCellValue(commoditytransfer.getISO8583_SystemTraceAuditNumber());
        row.createCell(50).setCellValue(commoditytransfer.getISO8583_ResponseCodeText());
        row.createCell(51).setCellValue(commoditytransfer.getISO8583_VariantText());
        row.createCell(52).setCellValue(commoditytransfer.getISO8583_ProcessingCodeText());
        row.createCell(53).setCellValue(commoditytransfer.getISO8583_MerchantTypeText());
        row.createCell(54).setCellValue(commoditytransfer.getISO8583_LocalTxnTimeHhmmss());
        row.createCell(55).setCellValue(commoditytransfer.getISO8583_CardAcceptorIdCode());
        
        Integer acqCode = commoditytransfer.getISO8583_AcquiringInstIdCode();        
        row.createCell(56).setCellValue((acqCode != null)? acqCode.toString() : "");
        
        if (commoditytransfer.getTopupPeriod() != null) {
            row.createCell(57).setCellValue(commoditytransfer.getTopupPeriod());
        } else {
            row.createCell(57).setCellValue("");
        }
        row.createCell(58).setCellValue(commoditytransfer.getServletPath());
        row.createCell(59).setCellValue(commoditytransfer.getSourceMessage());
        row.createCell(60).setCellValue(commoditytransfer.getSourceTerminalID());
        row.createCell(61).setCellValue(commoditytransfer.getSourceIP());
        if (commoditytransfer.getDistributionLevel() != null) {
            row.createCell(62).setCellValue(commoditytransfer.getDistributionLevel());
        } else {
            row.createCell(62).setCellValue("");
        }
        if (commoditytransfer.getLOPID() != null) {
            row.createCell(63).setCellValue(commoditytransfer.getLOPID());
        } else {
            row.createCell(63).setCellValue("");
        }
        if (commoditytransfer.getLevelPermissions() != null) {
            row.createCell(64).setCellValue(commoditytransfer.getLevelPermissionsText());
        } else {
            row.createCell(64).setCellValue("");
        }
        if (commoditytransfer.getReversalCount() != null) {
            row.createCell(65).setCellValue(commoditytransfer.getReversalCount());
        } else {
            row.createCell(65).setCellValue("");
        }
        row.createCell(66).setCellValue(commoditytransfer.getCommodityText());
        if (commoditytransfer.getBulkUploadLineNumber() != null) {
            row.createCell(67).setCellValue(commoditytransfer.getBulkUploadLineNumber());
        } else {
            row.createCell(67).setCellValue("");
        }
        if (commoditytransfer.getCurrency() != null) {
            row.createCell(68).setCellValue(commoditytransfer.getCurrency());
        } else {
            row.createCell(68).setCellValue("");
        }
        row.createCell(69).setCellValue(commoditytransfer.getBillingTypeText());
        row.createCell(70).setCellValue(commoditytransfer.getSourceSubscriberName());
        row.createCell(71).setCellValue(commoditytransfer.getDestSubscriberName());
        if (commoditytransfer.getBankSystemTraceAuditNumber() != null) {
            row.createCell(72).setCellValue(commoditytransfer.getBankSystemTraceAuditNumber());
        }
        if (commoditytransfer.getBankRetrievalReferenceNumber() != null) {
            row.createCell(73).setCellValue(commoditytransfer.getBankRetrievalReferenceNumber());
        }
        row.createCell(74).setCellValue(commoditytransfer.getOperatorRRN());
        row.createCell(75).setCellValue(commoditytransfer.getOperatorSTAN());
        row.createCell(76).setCellValue(commoditytransfer.getOperatorReversalErrorText());
        if(commoditytransfer.getOperatorReversalResponseTime() != null){
            row.createCell(77).setCellValue(df.format(commoditytransfer.getOperatorReversalResponseTime()));
        }else{
            row.createCell(77).setCellValue("");
        }
        row.createCell(78).setCellValue(commoditytransfer.getOperatorReversalResponseCodeText());
        row.createCell(79).setCellValue(commoditytransfer.getOperatorReversalRejectReason());
        row.createCell(80).setCellValue(commoditytransfer.getProductIndicatorCode());
        row.createCell(81).setCellValue(StringUtils.isNotEmpty(commoditytransfer.getInternalTxnType()) ? commoditytransfer.getInternalTxnType() : StringUtils.EMPTY);
    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=Transactions.xls");
    	CMJSCommodityTransfer jsCT = new CMJSCommodityTransfer();
        String id = request.getParameter(CMJSCommodityTransfer.FieldName_IDSearch);
        if (id != null && id.trim().length() > 0) {
            jsCT.setIDSearch(Long.parseLong(id));
        }
        String sourcedestmdn = request.getParameter(CMJSCommodityTransfer.FieldName_SourceDestnMDN);
        if (sourcedestmdn != null && sourcedestmdn.trim().length() > 0) {
           jsCT.setSourceDestnMDN(sourcedestmdn);
        }
        String transferStatus = request.getParameter(CMJSCommodityTransfer.FieldName_TransactionsTransferStatus);
        if (transferStatus != null && !transferStatus.equals("undefined") && transferStatus.trim().length() > 0) {
            jsCT.setTransferStatus(Integer.parseInt(transferStatus));
        }
        String sourceMdn = request.getParameter(CMJSCommodityTransfer.FieldName_SourceMDN);
        if (sourceMdn != null && sourceMdn.trim().length() > 0) {
            jsCT.setSourceMDN(sourceMdn);
        }
        String destMdn = request.getParameter(CMJSCommodityTransfer.FieldName_DestMDN);
        if (destMdn != null && destMdn.trim().length() > 0) {
            jsCT.setDestMDN(destMdn);
        }
        String SourceApplicationSearch = request.getParameter(CMJSCommodityTransfer.FieldName_SourceApplicationSearch);
        if (SourceApplicationSearch != null && SourceApplicationSearch.trim().length() > 0) {
            jsCT.setSourceApplicationSearch(Integer.parseInt(SourceApplicationSearch));
        }
        String startDate = request.getParameter(CMJSCommodityTransfer.FieldName_StartTime);
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (startDate != null && startDate.trim().length() > 0) {
            Date stDate = df.parse(startDate);
            Timestamp tstDate = new Timestamp(stDate);
            jsCT.setStartTime(tstDate);
        }
        String endDate = request.getParameter(CMJSCommodityTransfer.FieldName_EndTime);
        if (endDate != null && endDate.trim().length() > 0) {
            Date enDate = df.parse(endDate);
            Timestamp tenDate = new Timestamp(enDate);
            jsCT.setEndTime(tenDate);
        }
        String sourceRefId = request.getParameter(CMJSCommodityTransfer.FieldName_SourceReferenceID);
        if (sourceRefId != null && sourceRefId.trim().length() > 0) {
            jsCT.setSourceReferenceID(sourceRefId);
        }
        String destRefId = request.getParameter(CMJSCommodityTransfer.FieldName_OperatorAuthorizationCode);
        if (destRefId != null && destRefId.trim().length() > 0) {
            jsCT.setOperatorAuthorizationCode(destRefId);
        }
        String transType = request.getParameter(CMJSCommodityTransfer.FieldName_TransactionUICategory);
        if (transType != null && transType.trim().length() > 0) {
            jsCT.setTransactionUICategory(Integer.parseInt(transType));
        }
        String type = request.getParameter("dType");
        if (PendingCommodityTransfer_DOWNLOAD.equals(type)) {
            jsCT.setTransferState(CmFinoFIX.TransferState_Pending);
        } else {
            jsCT.setTransferState(CmFinoFIX.TransferState_Complete);
        }
        String sctlid = request.getParameter(CMJSCommodityTransfer.FieldName_ServiceChargeTransactionLogID);
        if (sctlid != null && sctlid.trim().length() > 0) {
            jsCT.setServiceChargeTransactionLogID(Long.parseLong(sctlid));
            jsCT.setJSMsgType(CmFinoFIX.MsgType_JSChargeTransactions);
           }
        String createTime = request.getParameter(CMJSCommodityTransfer.FieldName_CreateTimeSearch);
        if (createTime != null && createTime.trim().length() > 0) {
            DateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SS");
            Date createDate = sdf.parse(createTime);
            Timestamp tCreateTime = new Timestamp(createDate);
            jsCT.setCreateTimeSearch(tCreateTime);
        }
        
        
        jsCT.setstart(start);
        jsCT.setlimit(ConfigurationUtil.getExcelRowLimit());

        jsCT.setaction(CmFinoFIX.JSaction_Select);
        CMJSCommodityTransfer processedList = (CMJSCommodityTransfer) commodityTransferProcessor.process(jsCT);


        HSSFSheet sheet = workbook.createSheet("Transactions");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;
        initializeWorkBook(sheet, currentRow);
        if (processedList.getEntries() != null) {
        for (CMJSCommodityTransfer.CGEntries lop : processedList.getEntries()) {
            currentRow++;
            HSSFRow row = sheet.createRow(currentRow);
            fillCommodityTransferCells(lop, row);
        }
    }
    }
}
