package com.mfino.web.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mfino.fix.CmFinoFIX;
import com.mfino.service.AuthorizationService;

/**
 *
 * @author Siddhartha Chinthapally
 */
@Controller
public class FileDownloadController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String USER_DOWNLOAD = "user";
    private static final String LOP_DOWNLOAD = "lop";
    private static final String CommodityTransfer_DOWNLOAD = "commoditytransfer";
    private static final String PendingCommodityTransfer_DOWNLOAD = "pendingcommoditytransfer";
    private static final String CashFlow_DOWNLOAD = "cashflow";
    private static final String SubscriberMDN_DOWNLOAD = "subscriberMDN";
    private static final String BulkUploadEntry_DOWNLOAD = "bulkUploadEntry";
    private static final String BulkUploadFileEntry_DOWNLOAD = "bulkUploadFileEntry";
    private static final String PendingTransactionsFile_DOWNLOAD = "pendingtransactionsfile";
    private static final String PendingTransactionsEntry_DOWNLOAD = "pendingtransactionsentry";
    private static final String SERVICE_PARTNER_DOWNLOAD = "servicePartner";
    private static final String BUSINESS_PARTNER_DOWNLOAD = "businessPartner";
    private static final String INTEGRATION_PARTNER_DOWNLOAD = "integrationPartner";
    private static final String SCTL_DOWNLOAD = "sctlLogs";
    private static final String TADL_DOWNLOAD = "tadlogs";
    private static final String LEDGER_DOWNLOAD = "ledger";
    private static final String BULK_TRANSFER_DOWNLOAD="bulkTransferDownload";
    
    @Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

    @RequestMapping("/download.htm")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
    	String type = request.getParameter("dType");
    	String viewName;
    	log.info("dType value is :" + type);
    	if (USER_DOWNLOAD.equals(type)) { 
            viewName = "UserExcelView";
        } else if (CommodityTransfer_DOWNLOAD.equals(type) || PendingCommodityTransfer_DOWNLOAD.equals(type)) {
        	viewName = "CommodityTransferExcelView";
        } else if (CashFlow_DOWNLOAD.equals(type)) {
        	viewName = "CashFlowExcelView";
        } else if(LOP_DOWNLOAD.equals(type)) {
        	viewName = "LOPExcelView";
        } else if (SubscriberMDN_DOWNLOAD.equals(type) && 
        			authorizationService.isAuthorized(CmFinoFIX.Permission_SubscriberList_Download_Excel)) {
        	viewName = "SubscriberMDNExcelView";
        } else if (BulkUploadEntry_DOWNLOAD.equals(type)) {
        	viewName = "BulkUploadExcelView";
        } else if (BulkUploadFileEntry_DOWNLOAD.equals(type)) {
        	viewName = "BulkUploadFileExcelView";
        } else if (PendingTransactionsFile_DOWNLOAD.equals(type)) {
        	viewName = "PendingTransactionsFileExcelView";
        } else if (PendingTransactionsEntry_DOWNLOAD.equals(type)) {
        	viewName = "PendingTransactionsEntryExcelView";
        } else if(SERVICE_PARTNER_DOWNLOAD.equalsIgnoreCase(type) && 
        			authorizationService.isAuthorized(CmFinoFIX.Permission_ServicePartner_Download_Excel)){
        	viewName = "ServicePartnerExcelView";
        } else if(BUSINESS_PARTNER_DOWNLOAD.equalsIgnoreCase(type)) {
        	viewName = "BusinessPartnerExcelView";
        } else if(INTEGRATION_PARTNER_DOWNLOAD.equalsIgnoreCase(type)) {
        	viewName = "IntegrationPartnerExcelView";
        } else if(SCTL_DOWNLOAD.equalsIgnoreCase(type) && 
        			authorizationService.isAuthorized(CmFinoFIX.Permission_ChargeTransactions_Download_Excel)) {
        	viewName = "ServiceChargeTransactionExcelView";
        } else if(TADL_DOWNLOAD.equalsIgnoreCase(type)) {
        	viewName = "ChargeDistributionExcelView";
        } else if(LEDGER_DOWNLOAD.equalsIgnoreCase(type) && 
        			authorizationService.isAuthorized(CmFinoFIX.Permission_PocketTransactions_Download_Excel)) {
        	viewName = "TransactionLedgerExcelView";
        } else if(BULK_TRANSFER_DOWNLOAD.equalsIgnoreCase(type)) {
        	viewName = "BulkTransferExcelView";
        } else {
            throw new UnsupportedOperationException(type);
        }
    	log.info("resultant viewname :" + viewName);
    	return new ModelAndView(viewName);
    }
}