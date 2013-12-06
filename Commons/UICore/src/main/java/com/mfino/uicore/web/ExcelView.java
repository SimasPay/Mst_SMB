package com.mfino.uicore.web;


/**
 *	Deprecated
 *
 * @author Siddhartha Chinthapally
 */
public class ExcelView {
    /*private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String USER_DOWNLOAD = "user";
    private static final String LOP_DOWNLOAD = "lop";
    private static final String BULKLOP_DOWNLOAD = "bulkLop";
    private static final String CommodityTransfer_DOWNLOAD = "commoditytransfer";
    private static final String PendingCommodityTransfer_DOWNLOAD = "pendingcommoditytransfer";
    private static final String CashFlow_DOWNLOAD = "cashflow";
    private static final String SubscriberMDN_DOWNLOAD = "subscriberMDN";
    private static final String BulkUploadEntry_DOWNLOAD = "bulkUploadEntry";
    private static final String BulkUploadFileEntry_DOWNLOAD = "bulkUploadFileEntry";
    private static final String PendingTransactionsFile_DOWNLOAD = "pendingtransactionsfile";
    private static final String PendingTransactionsEntry_DOWNLOAD = "pendingtransactionsentry";
    private static final String CreditCard_DOWNLOAD = "creditCard";
    private static final String SERVICE_PARTNER_DOWNLOAD = "servicePartner";
    private static final String BUSINESS_PARTNER_DOWNLOAD = "businessPartner";
    private static final String INTEGRATION_PARTNER_DOWNLOAD = "integrationPartner";
    private static final String SCTL_DOWNLOAD = "sctlLogs";
    private static final String TADL_DOWNLOAD = "tadlogs";
    private static final String LEDGER_DOWNLOAD = "ledger";
    private static final String REPORT_DOWNLOAD = "report";
    private static final String BULK_TRANSFER_DOWNLOAD="bulkTransferDownload";
    
    private DateFormat df = getDateFormat();
    
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
  
    @Override
    @SuppressWarnings("unchecked")
    protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //CREATE THE SHEET

        String type = request.getParameter("dType");
        try {
            if (USER_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=Users.xls");
                UserExcelView usrView=new UserExcelView();
                usrView.buildExcelViewDocument(request, workbook);
            }else if (CommodityTransfer_DOWNLOAD.equals(type) || PendingCommodityTransfer_DOWNLOAD.equals(type)) {
                CommodityTransferExcelView ctView=new CommodityTransferExcelView();
                ctView.buildExcelViewDocument(request, workbook);
                response.setHeader("Content-Disposition", "attachment;filename=Transactions.xls");
            }else if (CashFlow_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=CashFlow.xls");
                CashFlowExcelView cfView=new CashFlowExcelView();
                cfView.buildExcelViewDocument(request, workbook);
            }else if(LOP_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=AirtimeLOP.xls");
                LOPExcelView lopView=new LOPExcelView();
                lopView.buildExcelViewDocument(request, workbook);                
            }else if(BULKLOP_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=AirtimeBulkLOP.xls");
                BulkLOPExcelView lopView=new BulkLOPExcelView();
                lopView.buildExcelViewDocument(request, workbook);
            }else if (SubscriberMDN_DOWNLOAD.equals(type) && authorizationService.isAuthorized(CmFinoFIX.Permission_SubscriberList_Download_Excel)) {
                response.setHeader("Content-Disposition", "attachment;filename=Subscribers.xls");
                SubscriberMDNExcelView subMdnView=new SubscriberMDNExcelView();
                subMdnView.buildExcelViewDocument(request, workbook);
            }else if (BulkUploadEntry_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=BulkRecords.xls");
                BulkUploadExcelView buView=new BulkUploadExcelView();
                buView.buildExcelViewDocument(request, workbook);
            }else if (BulkUploadFileEntry_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=BulkFileRecords.xls");
                BulkUploadFileExcelView bufView=new BulkUploadFileExcelView();
                bufView.buildExcelViewDocument(request, workbook);
            }else if (PendingTransactionsFile_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=PendingTransactionsFile.xls");
                PendingTransactionsFileExcelView ptfView=new PendingTransactionsFileExcelView();
                ptfView.buildExcelViewDocument(request, workbook);
            }else if (PendingTransactionsEntry_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=PendingTransactionsEntry.xls");
                PendingTransactionsEntryExcelView pteView=new PendingTransactionsEntryExcelView();
                pteView.buildExcelViewDocument(request, workbook);
            }else if (CreditCard_DOWNLOAD.equals(type)) {
                response.setHeader("Content-Disposition", "attachment;filename=CreditCardTransactions.xls");
                CreditCardTransactionsExcelView ccView=new CreditCardTransactionsExcelView();
                ccView.buildExcelViewDocument(request, workbook);
            }else if(SERVICE_PARTNER_DOWNLOAD.equalsIgnoreCase(type) && authorizationService.isAuthorized(CmFinoFIX.Permission_ServicePartner_Download_Excel)){
            	String partnerType = request.getParameter(CmFinoFIX.CMJSPartner.FieldName_PartnerTypeSearch);
            	if(StringUtils.isNotBlank(partnerType)&&(CmFinoFIX.TagID_BusinessPartnerTypeAgent==Integer.parseInt(partnerType))){
            		response.setHeader("Content-Disposition", "attachment;filename=Agent.xls");
            	}else{
            		response.setHeader("Content-Disposition", "attachment;filename=Partner.xls");
            	}
                ServicePartnerExcelView spExcelView = new ServicePartnerExcelView();
                spExcelView.buildExcelViewDocument(request, workbook);
            }else if(BUSINESS_PARTNER_DOWNLOAD.equalsIgnoreCase(type)){
                response.setHeader("Content-Disposition", "attachment;filename=BusinessPartner.xls");
                BusinessPartnerExcelView bpExcelView = new BusinessPartnerExcelView();
                bpExcelView.buildExcelViewDocument(request, workbook);   
            }else if(INTEGRATION_PARTNER_DOWNLOAD.equalsIgnoreCase(type)){
                response.setHeader("Content-Disposition", "attachment;filename=IntegrationPartner.xls");
                IntegrationPartnerExcelView ipExcelView = new IntegrationPartnerExcelView();
                ipExcelView.buildExcelViewDocument(request, workbook);            	
            }
            else if(SCTL_DOWNLOAD.equalsIgnoreCase(type) && authorizationService.isAuthorized(CmFinoFIX.Permission_ChargeTransactions_Download_Excel)){
                response.setHeader("Content-Disposition", "attachment;filename=Transactions.xls");
                ServiceChargeTransactionExcelView sctlExcelView = new ServiceChargeTransactionExcelView();
                sctlExcelView.buildExcelViewDocument(request, workbook);            	
            }
            else if(TADL_DOWNLOAD.equalsIgnoreCase(type)){
                response.setHeader("Content-Disposition", "attachment;filename=ChargeDistribution.xls");
                ChargeDistributionExcelView ipExcelView = new ChargeDistributionExcelView();
                ipExcelView.buildExcelViewDocument(request, workbook);            	
            }
            else if(LEDGER_DOWNLOAD.equalsIgnoreCase(type) && authorizationService.isAuthorized(CmFinoFIX.Permission_PocketTransactions_Download_Excel)){
                response.setHeader("Content-Disposition", "attachment;filename=Transactions.xls");
                TransactionLedgerExcelView ipExcelView = new TransactionLedgerExcelView();
                ipExcelView.buildExcelViewDocument(request, workbook);            	
            }
            else if(BULK_TRANSFER_DOWNLOAD.equalsIgnoreCase(type)){
            	String filename = "BulkTransfer_" + request.getParameter(CmFinoFIX.CMJSBulkUploadEntry.FieldName_IDSearch) + ".xls";
                response.setHeader("Content-Disposition", "attachment;filename=" + filename );
                BulkTransferExcelView ipExcelView = new BulkTransferExcelView();
                ipExcelView.buildExcelViewDocument(request, workbook);            	
            }
            else{
                throw new UnsupportedOperationException(type);
            }           
        } catch (Exception ex) {
            log.error("Error in ExcelView"+ ex.getMessage(), ex);
        } 
    }
    
     public DateFormat getDateFormat() {
        df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
        // Making timezone as a configurable property
        TimeZone zone = ConfigurationUtil.getLocalTimeZone();
        df.setTimeZone(zone);
        return df;
    }*/
}