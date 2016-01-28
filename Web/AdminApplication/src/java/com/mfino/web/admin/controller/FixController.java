/*
 * the editor.
 */
package com.mfino.web.admin.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.mfino.dao.AuditLogDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.AuditLog;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.enums.ItemType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSActorChannelMapping;
import com.mfino.fix.CmFinoFIX.CMJSActorChannelMappingValidator;
import com.mfino.fix.CmFinoFIX.CMJSAdjustments;
import com.mfino.fix.CmFinoFIX.CMJSAdjustmentsPocket;
import com.mfino.fix.CmFinoFIX.CMJSAgent;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectSettlement;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMJSBase;
import com.mfino.fix.CmFinoFIX.CMJSBranchCodes;
import com.mfino.fix.CmFinoFIX.CMJSClosedAccountSettlementMdn;
import com.mfino.fix.CmFinoFIX.CMJSExpirationType;
import com.mfino.fix.CmFinoFIX.CMJSFundDefinitions;
import com.mfino.fix.CmFinoFIX.CMJSFundEvents;
import com.mfino.fix.CmFinoFIX.CMJSLedger;
import com.mfino.fix.CmFinoFIX.CMJSMFSBiller;
import com.mfino.fix.CmFinoFIX.CMJSMFSBillerPartner;
import com.mfino.fix.CmFinoFIX.CMJSMoneyTransfer;
import com.mfino.fix.CmFinoFIX.CMJSPartner;
import com.mfino.fix.CmFinoFIX.CMJSPartnerByDCT;
import com.mfino.fix.CmFinoFIX.CMJSPurpose;
import com.mfino.fix.CmFinoFIX.CMJSRuleKey;
import com.mfino.fix.CmFinoFIX.CMJSRuleKeyComparision;
import com.mfino.fix.CmFinoFIX.CMJSScheduleTemplate;
import com.mfino.fix.CmFinoFIX.CMJSServiceProvider;
import com.mfino.fix.CmFinoFIX.CMJSShowBalanceDetails;
import com.mfino.fix.CmFinoFIX.CMJSSubscribers;
import com.mfino.fix.CmFinoFIX.CMJSTxnRuleAddnInfo;
import com.mfino.fix.CmFinoFIX.CMJSValidateChargeExpr;
import com.mfino.fix.processor.IFixProcessor;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.*;
import com.mfino.uicore.fix.processor.impl.ShowBalanceDetailsProcessorImpl;
import com.mfino.uicore.web.FixView;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author xchen
 */
@Controller
public class FixController {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	static {
		// this is required before start decoding fix messages
		CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
	}

	@Autowired
	@Qualifier("CheckBalanceProcessorImpl")
	private CheckBalanceProcessor checkBalanceProcessor;
	

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("DistributeChargesProcessorImpl")
	private DistributeChargesProcessor distributeChargesProcessor;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;

	@Autowired
	@Qualifier("DistributionChainLevelProcessorImpl")
	private DistributionChainLevelProcessor distributionChainLevelProcessor;
	
	@Autowired
	@Qualifier("DistributionChainTemplateProcessorImpl")
	private DistributionChainTemplateProcessor distributionChainTemplateProcessor;
	
	@Autowired
	@Qualifier("DuplicateNameCheckProcessorImpl")
	private DuplicateNameCheckProcessor duplicateNameCheckProcessor;
	
	@Autowired
	@Qualifier("DuplicatePartnerCodeCheckProcessorImpl")
	private DuplicatePartnerCodeCheckProcessor  duplicatePartnerCodeCheckProcessor;
	
	@Autowired
	@Qualifier("EmptySVAPocketProcessorImpl")
	private EmptySVAPocketProcessor  emptySVAPocketProcessor;
	
	@Autowired
	@Qualifier("EnumTextProcessorImpl")
	private EnumTextProcessor  enumTextProcessor;
	
	@Autowired
	@Qualifier("EnumTextSimpleProcessorImpl")
	private EnumTextSimpleProcessor  enumTextSimpleProcessor;
	
	@Autowired
	@Qualifier("GetAvialableBalanceProcessorImpl")
	private GetAvialableBalanceProcessor  getAvialableBalanceProcessor;
	
	@Autowired
	@Qualifier("GetBulkUploadFileDataProcessorImpl")
	private GetBulkUploadFileDataProcessor  getBulkUploadFileDataProcessor;
	
	@Autowired
	@Qualifier("GroupsProcessorImpl")
	private GroupsProcessor  groupsProcessor;
	
	@Autowired
	@Qualifier("IntegrationPartnerMappingProcessorImpl")
	private IntegrationPartnerMappingProcessor  integrationPartnerMappingProcessor;
	
	@Autowired
	@Qualifier("IPMappingprocessorImpl")
	private IPMappingprocessor  ipMappingprocessor;
	
	@Autowired
	@Qualifier("KYCFieldsPrcocessorImpl")
	private KYCFieldsPrcocessor  kycFieldsPrcocessor;
  	
	@Autowired
	@Qualifier("KYCProcessorImpl")
	private KYCProcessor  kycProcessor;

	@Autowired
	@Qualifier("LOPHistoryProcessorImpl")
	private LOPHistoryProcessor  lopHistoryProcessor;

	@Autowired
	@Qualifier("LOPProcessorImpl")
	private LOPProcessor  lopProcessor;

	@Autowired
	@Qualifier("MDNCheckProcessorImpl")
	private MDNCheckProcessor  mdnCheckProcessor;

	@Autowired
	@Qualifier("MfinoServiceproviderProcessorImpl")
    private MfinoServiceproviderProcessor mFinoServiceProviderProcessor;

	@Autowired
	@Qualifier("MFSBillerPartnerProcessorImpl")
	private MFSBillerPartnerProcessor mfsBillerPartnerProcessor;

	@Autowired
	@Qualifier("MFSBillerProcessorImpl")
	private MFSBillerProcessor mfsBillerProcessor;

	@Autowired
	@Qualifier("MFSDenominationsProcessorImpl")
	private MFSDenominationsProcessor mfsDenominationsProcessor;

	@Autowired
	@Qualifier("NotificationProcessorImpl")
	private NotificationProcessor notificationProcessor;

	@Autowired
	@Qualifier("OfflineReportProcessorImpl")
	private OfflineReportProcessor offlineReportProcessor;

	@Autowired
	@Qualifier("PartnerByDCTProcessorImpl")
	private PartnerByDCTProcessor partnerByDCTProcessor;

	@Autowired
	@Qualifier("PartnerRestrictionsProcessorImpl")
	private PartnerRestrictionsProcessor partnerRestrictionsProcessor;

	@Autowired
	@Qualifier("PartnerServicesProcessorImpl")
	private PartnerServicesProcessor partnerServicesProcessor;

	@Autowired
	@Qualifier("PendingCommodityTransferRequestProcessorImpl")
	private PendingCommodityTransferRequestProcessor pendingCommodityTransferRequestProcessor;

	@Autowired
	@Qualifier("PendingTransactionsEntryProcessorImpl")
	private PendingTransactionsEntryProcessor pendingTransactionsEntryProcessor;

	@Autowired
	@Qualifier("PendingTransactionsFileProcessorImpl")
	private PendingTransactionsFileProcessor pendingTransactionsFileProcessor;

	@Autowired
	@Qualifier("Person2PersonProcessorImpl")
	private Person2PersonProcessor person2PersonProcessor;

	@Autowired
	@Qualifier("PocketIssuerProcessorImpl")
	private PocketIssuerProcessor pocketIssuerProcessor;

	@Autowired
	@Qualifier("PocketProcessorImpl")
	private PocketProcessor pocketProcessor;

	@Autowired
	@Qualifier("PocketTemplateCheckProcessorImpl")
	private PocketTemplateCheckProcessor pocketTemplateCheckProcessor;

	@Autowired
	@Qualifier("PocketTemplateConfigCheckProcessorImpl")
	private PocketTemplateConfigCheckProcessor pocketTemplateConfigCheckProcessor;

	@Autowired
	@Qualifier("PocketTemplateConfigProcessorImpl")
	private PocketTemplateConfigProcessor pocketTemplateConfigProcessor;

	@Autowired
	@Qualifier("ApproveRejectBulkTransferProcessorImpl")
	private ApproveRejectBulkTransferProcessor approveRejectBulkTransferProcessor;

	@Autowired
	@Qualifier("ApproveRejectPartnerProcessorImpl")
	private ApproveRejectPartnerProcessor approveRejectPartnerProcessor;

	@Autowired
	@Qualifier("ApproveRejectSubscriberProcessorImpl")
	private ApproveRejectSubscriberProcessor approveRejectSubscriberProcessor;

	@Autowired
	@Qualifier("BankAdminProcessorImpl")
	private BankAdminProcessor bankAdminProcessor;

	@Autowired
	@Qualifier("BankProcessorImpl")
	private BankProcessor bankProcessor;

	@Autowired
	@Qualifier("BankTellerCashInConfirmProcessorImpl")
	private BankTellerCashInConfirmProcessor bankTellerCashInConfirmProcessor;

	@Autowired
	@Qualifier("BankTellerCashInInquiryProcessorImpl")
	private BankTellerCashInInquiryProcessor bankTellerCashInInquiryProcessor;

	@Autowired
	@Qualifier("BankTellerCashOutApproveProcessorImpl")
	private BankTellerCashOutApproveProcessor bankTellerCashOutApproveProcessor;

	@Autowired
	@Qualifier("BankTellerCashOutInquiryProcessorImpl")
	private BankTellerCashOutInquiryProcessor bankTellerCashOutInquiryProcessor;

	@Autowired
	@Qualifier("BankTellerUnregisteredCashOutApproveProcessorImpl")
	private BankTellerUnregisteredCashOutApproveProcessor bankTellerUnregisteredCashOutApproveProcessor;

	@Autowired
	@Qualifier("BankTellerUnregisteredCashOutInquiryProcessorImpl")
	private BankTellerUnregisteredCashOutInquiryProcessor bankTellerUnregisteredCashOutInquiryProcessor;

	@Autowired
	@Qualifier("BrandProcessorImpl")
	private BrandProcessor brandProcessor;

	@Autowired
	@Qualifier("BulkUploadEntryProcessorImpl")
	private BulkUploadEntryProcessor bulkUploadEntryProcessor;

	@Autowired
	@Qualifier("BulkUploadFileEntryProcessorImpl")
	private BulkUploadFileEntryProcessor bulkUploadFileEntryProcessor;

	@Autowired
	@Qualifier("BulkUploadFileProcessorImpl")
	private BulkUploadFileProcessor bulkUploadFileProcessor;

	@Autowired
	@Qualifier("BulkUploadProcessorImpl")
	private BulkUploadProcessor bulkUploadProcessor;

	@Autowired
	@Qualifier("CancelBulkTransferProcessorImpl")
	private CancelBulkTransferProcessor cancelBulkTransferProcessor;

	@Autowired
	@Qualifier("CashFlowProcessorImpl")
	private CashFlowProcessor cashFlowProcessor;

	@Autowired
	@Qualifier("ChangePinProcessorImpl")
	private ChangePinProcessor changePinProcessor;

	@Autowired
	@Qualifier("ChannelCodeProcessorImpl")
	private ChannelCodeProcessor channelCodeProcessor;

	@Autowired
	@Qualifier("ChargeDefinitionProcessorImpl")
	private ChargeDefinitionProcessor chargeDefinitionProcessor;

	@Autowired
	@Qualifier("ChargePricingProcessorImpl")
	private ChargePricingProcessor chargePricingProcessor;

	@Autowired
	@Qualifier("ChargeTransactionsViewProcessorImpl")
	private ChargeTransactionsViewProcessor chargeTransactionsViewProcessor;

	@Autowired
	@Qualifier("ChargeTypeProcessorImpl")
	private ChargeTypeProcessor chargeTypeProcessor;

	@Autowired
	@Qualifier("CheckBalanceForSubscriberProcessorImpl")
	private CheckBalanceForSubscriberProcessor checkBalanceForSubscriberProcessor;

	@Autowired
	@Qualifier("CheckChargeDefinitionProcessorImpl")
	private CheckChargeDefinitionProcessor checkChargeDefinitionProcessor;

	@Autowired
	@Qualifier("CheckDependantChargeTypeProcessorImpl")
	private CheckDependantChargeTypeProcessor checkDependantChargeTypeProcessor;

	@Autowired
	@Qualifier("CheckMDNProcessorImpl")
	private CheckMDNProcessor checkMDNProcessor;

	@Autowired
	@Qualifier("CheckServicePartnerProcessorImpl")
	private CheckServicePartnerProcessor checkServicePartnerProcessor;

	@Autowired
	@Qualifier("CheckTransactionChargeProcessorImpl")
	private CheckTransactionChargeProcessor checkTransactionChargeProcessor;

	@Autowired
	@Qualifier("CheckTransactionRuleProcessorImpl")
	private CheckTransactionRuleProcessor checkTransactionRuleProcessor;

	@Autowired
	@Qualifier("CommodityTransferProcessorImpl")
	private CommodityTransferProcessor commodityTransferProcessor;

	@Autowired
	@Qualifier("CompanyProcessorImpl")
	private CompanyProcessor companyProcessor;

	@Autowired
	@Qualifier("DCTNameCheckProcessorImpl")
	private DCTNameCheckProcessor dctNameCheckProcessor;

	@Autowired
	@Qualifier("DCTRestrictionsProcessorImpl")
	private DCTRestrictionsProcessor dctRestrictionsProcessor;

	@Autowired
	@Qualifier("RelationshipTypeProcessorImpl")
	private RelationshipTypeProcessor relationshipTypeProcessor;

	@Autowired
	@Qualifier("ReportProcessorImpl")
	private ReportProcessor reportProcessor;

	@Autowired
	@Qualifier("ResendAccessCodeProcessorImpl")
	private ResendAccessCodeProcessor resendAccessCodeProcessor;

	@Autowired
	@Qualifier("ResetAuthenticationKeyForIntegrationProcessorImpl")
	private ResetAuthenticationKeyForIntegrationProcessor resetAuthenticationKeyForIntegrationProcessor;

	@Autowired
	@Qualifier("ResetOTPProcessorImpl")
	private ResetOTPProcessor resetOTPProcessor;

	@Autowired
	@Qualifier("ResetPasswordProcessorImpl")
	private ResetPasswordProcessor resetPasswordProcessor;

	@Autowired
	@Qualifier("ResetPinProcessorImpl")
	private ResetPinProcessor resetPinProcessor;

	@Autowired
	@Qualifier("ReverseTransactionApproveRejectProcessorImpl")
	private ReverseTransactionApproveRejectProcessor reverseTransactionApproveRejectProcessor;

	@Autowired
	@Qualifier("ReverseTransactionConfirmProcessorImpl")
	private ReverseTransactionConfirmProcessor reverseTransactionConfirmProcessor;

	@Autowired
	@Qualifier("ReverseTransactionProcessorImpl")
	private ReverseTransactionProcessor reverseTransactionProcessor;

	@Autowired
	@Qualifier("RoleProcessorImpl")
	private RoleProcessor roleProcessor;

	@Autowired
	@Qualifier("ServiceChargeTransactionLogProcessorImpl")
	private ServiceChargeTransactionLogProcessor serviceChargeTransactionLogProcessor;

	@Autowired
	@Qualifier("ServicePartnerProcessorImpl")
	private ServicePartnerProcessor servicePartnerProcessor;

	@Autowired
	@Qualifier("ServiceProcessorImpl")
	private ServiceProcessor serviceProcessor;

	@Autowired
	@Qualifier("ServiceProviderProcessorImpl")
	private ServiceProviderProcessor serviceProviderProcessor;

	@Autowired
	@Qualifier("ServiceSettlementConfigProcessorImpl")
	private ServiceSettlementConfigProcessor serviceSettlementConfigProcessor;

	@Autowired
	@Qualifier("ServicesForServiceProviderProcessorImpl")
	private ServicesForServiceProviderProcessor servicesForServiceProviderProcessor;

	@Autowired
	@Qualifier("ServiceTransactionProcessorImpl")
	private ServiceTransactionProcessor serviceTransactionProcessor;

	@Autowired
	@Qualifier("SettlementTemplateCheckProcessorImpl")
	private SettlementTemplateCheckProcessor settlementTemplateCheckProcessor;

	@Autowired
	@Qualifier("SettlementTemplateProcessorImpl")
	private SettlementTemplateProcessor settlementTemplateProcessor;

	@Autowired
	@Qualifier("SharePartnerProcessorImpl")
	private SharePartnerProcessor sharePartnerProcessor;

	@Autowired
	@Qualifier("SubscriberAdditonalFieldsProcessorImpl")
	private SubscriberAdditonalFieldsProcessor subscriberAdditonalFieldsProcessor;

	@Autowired
	@Qualifier("TradeNameCheckProcessorImpl")
	private TradeNameCheckProcessor tradeNameCheckProcessor;
	
	@Autowired
	@Qualifier("SubscriberMdnProcessorImpl")
	private SubscriberMdnProcessor subscriberMdnProcessor;

	@Autowired
	@Qualifier("SubscriberProcessorImpl")
	private SubscriberProcessor subscriberProcessor;

	@Autowired
	@Qualifier("SystemParametersProcessorImpl")
	private SystemParametersProcessor systemParametersProcessor;

	@Autowired
	@Qualifier("TransactionAmountDistributorProcessImpl")
	private TransactionAmountDistributorProcess transactionAmountDistributorProcess;

	@Autowired
	@Qualifier("TransactionChargeProcessorImpl")
	private TransactionChargeProcessor transactionChargeProcessor;

	@Autowired
	@Qualifier("TransactionRuleProcessorImpl")
	private TransactionRuleProcessor transactionRuleProcessor;

	@Autowired
	@Qualifier("TransactionsForServiceProcessorImpl")
	private TransactionsForServiceProcessor transactionsForServiceProcessor;

	@Autowired
	@Qualifier("TransactionTypePartnerRestrictionsProcessorImpl")
	private TransactionTypePartnerRestrictionsProcessor transactionTypePartnerRestrictionsProcessor;

	@Autowired
	@Qualifier("TransactionTypeProcessorImpl")
	private TransactionTypeProcessor transactionTypeProcessor;

	@Autowired
	@Qualifier("UsernameCheckProcessorImpl")
	private UsernameCheckProcessor usernameCheckProcessor;

	@Autowired
	@Qualifier("UserProcessorImpl")
	private UserProcessor userProcessor;

	@Autowired
	@Qualifier("ValidatePinProcessorImpl")
	private ValidatePinProcessor validatePinProcessor;

	@Autowired
	@Qualifier("VerifyNonRegisteredBulkTransferProcessorImpl")
	private VerifyNonRegisteredBulkTransferProcessor verifyNonRegisteredBulkTransferProcessor;


	
	@Autowired
	@Qualifier("AgentCashInProcessorImpl")
	private AgentCashInProcessor agentCashInProcessor;
	
	@Autowired
	@Qualifier("ClosedAccountSettlementMdnProcessorImpl")
	private ClosedAccountSettlementMdnProcessor closedAccountSettlementMdnProcessor ;
	
	@Autowired
	@Qualifier("ApproveRejectSettlementProcessorImpl")
	private ApproveRejectSettlementProcessor approveRejectSettlementProcessor   ;	

	@Autowired
	@Qualifier("ScheduleTemplateProcessorImpl")
	private ScheduleTemplateProcessor scheduleTemplateProcessor   ;
	
	@Autowired
	@Qualifier("FundDefinitionsProcessorImpl")
	private  FundDefinitionsProcessor fundDefinitionsProcessor  ;
	
	@Autowired
	@Qualifier("FundEventsProcessorImpl")
	private FundEventsProcessor fundEventsProcessor   ;
	
	@Autowired
	@Qualifier("PurposeProcessorImpl")
	private PurposeProcessor purposeProcessor   ;

	@Autowired
	@Qualifier("ExpirationTypeProcessorImpl")
	private ExpirationTypeProcessor expirationTypeProcessor  ;
	
	@Autowired
	@Qualifier("ValidateChargeExprProcessorImpl")
	private ValidateChargeExprProcessor validateChargeExprProcessor  ;
	
	@Autowired
	@Qualifier("NotificationLogProcessorImpl")
	private NotificationLogProcessor notificationLogProcessor  ;
	
	@Autowired
	@Qualifier("ResendNotificationProcessorImpl")
	private ResendNotificationProcessor resendNotificationProcessor   ;
	

	@Autowired
	@Qualifier("ActorChannelMappingProcessorImpl")
	private ActorChannelMappingProcessor actorChannelMappingProcessor  ;

	@Autowired
	@Qualifier("ActorChannelMappingValidatorProcessorImpl")
	private ActorChannelMappingValidatorProcessor actorChannelMappingValidatorProcessor ;

	@Autowired
	@Qualifier("AdjustmentsPocketProcessorImpl")
	private AdjustmentsPocketProcessor adjustmentsPocketProcessor ;
	
	@Autowired
	@Qualifier("AdjustmentsProcessorImpl")
	private AdjustmentsProcessor adjustmentsProcessor    ;
		
	@Autowired
	@Qualifier("LedgerProcessorImpl")
	private LedgerProcessor ledgerProcessor  ;
	
	@Autowired
	@Qualifier("RuleKeyProcessorImpl")
	private RuleKeyProcessor ruleKeyProcessor  ;
	
	@Autowired
	@Qualifier("TransactionRuleAddnInfoProcessorImpl")
	private TransactionRuleAddnInfoProcessor transactionRuleAddnInfoProcessor ;
	
	@Autowired
	@Qualifier("RuleKeyComparisionProcessorImpl")
	private RuleKeyComparisionProcessor ruleKeyComparisionProcessor;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier("ShowBalanceDetailsProcessorImpl")
	private ShowBalanceDetailsProcessor showBalanceDetailsProcessor  ;
	
	@Autowired
	@Qualifier("MoneyTransferProcessorImpl")
	private MoneyTransferProcessor moneyTransferProcessor  ;
	
	@Autowired
	@Qualifier("BranchCodeProcessorImpl")
	private BranchCodeProcessor branchCodeProcessor;
	
	@Autowired
	@Qualifier("ServicePartnerProcessorspImpl")
	private ServicePartnerProcessorsp servicePartnerProcessorsp;
	
	@RequestMapping("/fix.htm")
	public View processFix(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			WebContextError.clear();
			// reading params using getReader().readLine() is removed since we
			// can read data only once in that manner
			// if we try reading using readLine() again, we will get null value
			// - hence read params using getParameter() method

			/*
			 * BufferedReader reader = request.getReader(); String line =
			 * reader.readLine();
			 * 
			 * StringBuffer sb = new StringBuffer(); while (line != null) {
			 * sb.append(line); line = reader.readLine(); } String
			 * completePostString = sb.toString();
			 */

			String completePostString = request.getParameter("data");
			CMultiXBuffer buf = new CMultiXBuffer();
			buf.Append(completePostString);


			CFIXMsg msg = CFIXMsg.fromFIX(buf);

			if (msg == null) {
				log.error("Failed to decode fix message: ("
						+ msg.DumpFields() + ") \n"
						+ MfinoUtil.dumpHttpRequest(request));
				CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
				// TODO : choose more meaningful error and get the description
				// from enum
				// text table
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				errorMsg.setErrorDescription(MessageText
						._("Bad message received by server."));
				// hide this error from the end user now, until we figure out
				// what's the
				// problem
				// return new FixView(errorMsg);
				return null;
			}

			// find the corresponding fix message processor by message class
			// name
			String msgClassName = msg.getClass().getName();
			IFixProcessor fixProcessor;
			String action = ((CMJSBase) msg).getaction();
			TransactionsLog tl = new TransactionsLog();

			if (msgClassName.equals(CMJSSubscribers.class.getName())) {
				fixProcessor = subscriberProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSSubscribers);
			} else if (msgClassName.equals(CmFinoFIX.CMJSUsers.class.getName())) {
				fixProcessor = userProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSUsers);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocket.class.getName())) {
				fixProcessor = pocketProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPocket);
			} else if (msgClassName.equals(CmFinoFIX.CMJSSubscriberMDN.class
					.getName())) {
				fixProcessor = subscriberMdnProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSSubscriberMDN);
			} else if (msgClassName.equals(CmFinoFIX.CMJSPocketTemplate.class
					.getName())) {
				fixProcessor = pocketIssuerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPocketTemplate);
			} else if (msgClassName.equals(CmFinoFIX.CMJSEnumText.class
					.getName())) {
				fixProcessor = enumTextProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSEnumText);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCommodityTransfer.class.getName())) {
				fixProcessor = commodityTransferProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCommodityTransfer);
			} else if (msgClassName.equals(CmFinoFIX.CMJSPerson2Person.class
					.getName())) {
				fixProcessor = person2PersonProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPerson2Person);
			} else if (msgClassName.equals(CmFinoFIX.CMJSEnumTextSimple.class
					.getName())) {
				fixProcessor = enumTextSimpleProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSEnumTextSimple);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResetPin.class
					.getName())) {
				fixProcessor = resetPinProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSResetPin);
			} else if (msgClassName.equals(CmFinoFIX.CMJSLOP.class.getName())) {
				fixProcessor = lopProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSLOP);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPendingCommodityTransferRequest.class
							.getName())) {
				fixProcessor = pendingCommodityTransferRequestProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPendingCommodityTransferRequest);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributionChainTemplate.class
							.getName())) {
				fixProcessor = distributionChainTemplateProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSDistributionChainTemplate);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributionChainLevel.class
							.getName())) {
				fixProcessor = distributionChainLevelProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSDistributionChainLevel);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCashFlow.class
					.getName())) {
				fixProcessor = cashFlowProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCashFlow);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBulkUpload.class
					.getName())) {
				fixProcessor = bulkUploadProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBulkUpload);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResetPassword.class
					.getName())) {
				fixProcessor = resetPasswordProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSResetPassword);
			} else if (msgClassName.equals(CmFinoFIX.CMJSNotification.class
					.getName())) {
				fixProcessor = notificationProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSNotification);
			} else if (msgClassName.equals(CmFinoFIX.CMJSUsernameCheck.class
					.getName())) {
				fixProcessor = usernameCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSUsernameCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocketTemplateCheck.class.getName())) {
				fixProcessor = pocketTemplateCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPocketTemplateCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributionTemplateCheck.class
							.getName())) {
				fixProcessor = dctNameCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSDistributionTemplateCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCheckBalance.class
					.getName())) {
				fixProcessor = checkBalanceProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckBalance);
			} else if (msgClassName.equals(CmFinoFIX.CMJSEmptySVAPocket.class
					.getName())) {
				fixProcessor = emptySVAPocketProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSEmptySVAPocket);
			} else if (msgClassName.equals(CmFinoFIX.CMJSMDNCheck.class
					.getName())) {
				fixProcessor = mdnCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSMDNCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBulkUploadEntry.class
					.getName())) {
				fixProcessor =  bulkUploadEntryProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBulkUploadEntry);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSGetBulkUploadFileData.class.getName())) {
				fixProcessor = getBulkUploadFileDataProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSGetBulkUploadFileData);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckBalanceForSubscriber.class
							.getName())) {
				fixProcessor = checkBalanceForSubscriberProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckBalanceForSubscriber);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCompany.class
					.getName())) {
				fixProcessor = companyProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCompany);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBrand.class.getName())) {
				fixProcessor = brandProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBrand);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBulkUploadFile.class
					.getName())) {
				fixProcessor = bulkUploadFileProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBulkUploadFile);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSBulkUploadFileEntry.class.getName())) {
				fixProcessor = bulkUploadFileEntryProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBulkUploadFileEntry);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChannelCode.class
					.getName())) {
				fixProcessor = channelCodeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSChannelCode);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPendingTransactionsFile.class
							.getName())) {
				fixProcessor = pendingTransactionsFileProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPendingTransactionsFile);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPendingTransactionsEntry.class
							.getName())) {
				fixProcessor = pendingTransactionsEntryProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPendingTransactionsEntry);
			} else if (msgClassName.equals(CmFinoFIX.CMJSLOPHistory.class
					.getName())) {
				fixProcessor = lopHistoryProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSLOPHistory);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSApproveRejectSubscriber.class
							.getName())) {
				fixProcessor = approveRejectSubscriberProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSApproveRejectSubscriber);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBankAdmin.class
					.getName())) {
				fixProcessor = bankAdminProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBankAdmin);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBank.class.getName())) {
				fixProcessor = bankProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBank);
			} else if (msgClassName.equals(CmFinoFIX.CMJSService.class
					.getName())) {
				fixProcessor = serviceProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSService);
			} else if (msgClassName.equals(CmFinoFIX.CMJSTransactionType.class
					.getName())) {
				fixProcessor = transactionTypeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTransactionType);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServiceTransaction.class.getName())) {
				fixProcessor = serviceTransactionProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSServiceTransaction);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionsForService.class
							.getName())) {
				fixProcessor = transactionsForServiceProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTransactionsForService);
			} else if (msgClassName.equals(CmFinoFIX.CMJSServiceProvider.class
					.getName())) {
				fixProcessor = serviceProviderProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSServiceProvider);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServicesForServiceProvider.class
							.getName())) {
				fixProcessor = servicesForServiceProviderProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSServicesForServiceProvider);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSSettlementTemplate.class.getName())) {
				fixProcessor = settlementTemplateProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSSettlementTemplate);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSSettlementTemplateCheck.class
							.getName())) {
				fixProcessor = settlementTemplateCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSSettlementTemplateCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSPartnerServices.class
					.getName())) {
				fixProcessor = partnerServicesProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPartnerServices);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServiceSettlementConfig.class
							.getName())) {
				fixProcessor = serviceSettlementConfigProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSServiceSettlementConfig);
			} else if (msgClassName.equals(CMJSPartner.class.getName())) {
				// fixProcessor = new PartnerProcessor();
				fixProcessor = servicePartnerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPartner);
			} else if (msgClassName.equals(CMJSAgent.class.getName())) {
				fixProcessor = servicePartnerProcessorsp;
				tl.setMessageCode(CmFinoFIX.MsgType_JSAgent);
			} else if (msgClassName.equals(CMJSServiceProvider.class.getName())) {
				fixProcessor = serviceProviderProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSServiceProvider);
			} else if (msgClassName.equals(CMJSMFSBiller.class.getName())) {
				fixProcessor = mfsBillerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSMFSBiller);
			} else if (msgClassName
					.equals(CMJSMFSBillerPartner.class.getName())) {
				fixProcessor = mfsBillerPartnerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSMFSBillerPartner);
			} else if (msgClassName.equals(CMJSPartnerByDCT.class.getName())) {
				fixProcessor = partnerByDCTProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPartnerByDCT);
			} else if (msgClassName.equals(CmFinoFIX.CMJSTradeNameCheck.class
					.getName())) {
				fixProcessor = tradeNameCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTradeNameCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionAmountDistributionLog.class
							.getName())) {
				fixProcessor = transactionAmountDistributorProcess;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTransactionAmountDistributionLog);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChargeType.class
					.getName())) {
				fixProcessor = chargeTypeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSChargeType);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDuplicateNameCheck.class.getName())) {
				fixProcessor = duplicateNameCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSDuplicateNameCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChargeDefinition.class
					.getName())) {
				fixProcessor = chargeDefinitionProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSChargeDefinition);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChargePricing.class
					.getName())) {
				fixProcessor = chargePricingProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSChargePricing);
			} else if (msgClassName.equals(CmFinoFIX.CMJSTransactionRule.class
					.getName())) {
				fixProcessor = transactionRuleProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTransactionRule);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionCharge.class.getName())) {
				fixProcessor = transactionChargeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTransactionCharge);
			} else if (msgClassName.equals(CmFinoFIX.CMJSSharePartner.class
					.getName())) {
				fixProcessor = sharePartnerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSSharePartner);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckDependantChargeType.class
							.getName())) {
				fixProcessor = checkDependantChargeTypeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckDependantChargeType);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckTransactionCharge.class
							.getName())) {
				fixProcessor = checkTransactionChargeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckTransactionCharge);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckTransactionRule.class.getName())) {
				fixProcessor = checkTransactionRuleProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckTransactionRule);
			} else if (msgClassName.equals(CmFinoFIX.CMJSKYCCheck.class
					.getName())) {
				fixProcessor = kycProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSKYCCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSKYCCheckFields.class
					.getName())) {
				fixProcessor = kycFieldsPrcocessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSKYCCheckFields);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSSubscribersAdditionalFields.class
							.getName())) {
				fixProcessor = subscriberAdditonalFieldsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSSubscribersAdditionalFields);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckServicePartner.class.getName())) {
				fixProcessor = checkServicePartnerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckServicePartner);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSApproveRejectPartner.class.getName())) {
				fixProcessor = approveRejectPartnerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSApproveRejectPartner);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResetOTP.class
					.getName())) {
				fixProcessor = resetOTPProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSResetOTP);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResendAccessCode.class
					.getName())) {
				fixProcessor = resendAccessCodeProcessor;
				tl.setMessageCode(CmFinoFIX.MessageType_JSResendAccessCode);
			} else if (msgClassName.equals(CMJSBankTellerCashInInquiry.class
					.getName())) {
				fixProcessor = bankTellerCashInInquiryProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_BankTellerCashIn);
			} else if (msgClassName.equals(CMJSBankTellerCashInConfirm.class
					.getName())) {
				fixProcessor = bankTellerCashInConfirmProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_BankTellerCashIn);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDuplicatePartnerCodeCheck.class
							.getName())) {
				fixProcessor = duplicatePartnerCodeCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSDuplicatePartnerCodeCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServiceChargeTransactions.class
							.getName())) {
				fixProcessor = serviceChargeTransactionLogProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSServiceChargeTransactions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSBankTellerCashOutInquiry.class
							.getName())) {
				fixProcessor = bankTellerCashOutInquiryProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBankTellerCashOutInquiry);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSBankTellerCashOutConfirm.class
							.getName())) {
				fixProcessor = bankTellerCashOutApproveProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBankTellerCashOutConfirm);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReport.class.getName())) {
				fixProcessor = reportProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSReport);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReverseTransaction.class.getName())) {
				fixProcessor = reverseTransactionProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSReverseTransaction);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReverseTransactionConfirm.class
							.getName())) {
				fixProcessor = reverseTransactionConfirmProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSReverseTransactionConfirm);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReverseTransactionApproveReject.class
							.getName())) {
				fixProcessor = reverseTransactionApproveRejectProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSReverseTransactionApproveReject);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckChargeDefinition.class.getName())) {
				fixProcessor = checkChargeDefinitionProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckChargeDefinition);
			} else if (msgClassName.equals(CmFinoFIX.CMJSOfflineReport.class
					.getName())) {
				fixProcessor = offlineReportProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSOfflineReport);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChangePin.class
					.getName())) {
				fixProcessor = changePinProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSChangePin);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSChargeTransactions.class.getName())) {
				fixProcessor = chargeTransactionsViewProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSChargeTransactions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCashOutUnregisteredInquiry.class
							.getName())) {
				fixProcessor = bankTellerUnregisteredCashOutInquiryProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCashOutUnregisteredInquiry);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCashOutUnregisteredConfirm.class
							.getName())) {
				fixProcessor = bankTellerUnregisteredCashOutApproveProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCashOutUnregisteredConfirm);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCheckMDN.class
					.getName())) {
				fixProcessor = checkMDNProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCheckMDN);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSApproveRejectBulkTranfer.class
							.getName())) {
				fixProcessor = approveRejectBulkTransferProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSApproveRejectBulkTranfer);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCancelBulkTranfer.class.getName())) {
				fixProcessor = cancelBulkTransferProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSCancelBulkTranfer);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSGetAvailableBalance.class.getName())) {
				fixProcessor = getAvialableBalanceProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSGetAvailableBalance);
			} else if (msgClassName.equals(CmFinoFIX.CMJSValidatePin.class
					.getName())) {
				fixProcessor = validatePinProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSValidatePin);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSVerifyNonRegisteredBulkTransfer.class
							.getName())) {
				fixProcessor = verifyNonRegisteredBulkTransferProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSVerifyNonRegisteredBulkTransfer);
			} else if (msgClassName.equals(CmFinoFIX.CMJSGroup.class.getName())) {
				fixProcessor = groupsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSGroup);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocketTemplateConfig.class.getName())) {
				fixProcessor = pocketTemplateConfigProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPocketTemplateConfig);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocketTemplateConfigCheck.class
							.getName())) {
				fixProcessor = pocketTemplateConfigCheckProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPocketTemplateConfigCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSDCTRestrictions.class
					.getName())) {
				fixProcessor = dctRestrictionsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSDCTRestrictions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPartnerRestrictions.class.getName())) {
				fixProcessor = partnerRestrictionsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPartnerRestrictions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionTypeForPartnerRestrictions.class
							.getName())) {
				fixProcessor = transactionTypePartnerRestrictionsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTransactionTypeForPartnerRestrictions);
			} else if (msgClassName.equals(CmFinoFIX.CMJSRelationshipType.class
					.getName())) {
				fixProcessor = relationshipTypeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSRelationshipType);
			} else if (msgClassName.equals(CmFinoFIX.CMJSSystemParameters.class
					.getName())) {
				fixProcessor = systemParametersProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSSystemParameters);
			} else if (msgClassName.equals(CmFinoFIX.CMJSMFSDenominations.class
					.getName())) {
				fixProcessor =mfsDenominationsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSMFSDenominations);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSIntegrationPartnerMapping.class
							.getName())) {
				fixProcessor = integrationPartnerMappingProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSIntegrationPartnerMapping);
			} else if (msgClassName.equals(CmFinoFIX.CMJSIPMapping.class
					.getName())) {
				fixProcessor = ipMappingprocessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSIPMapping);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSResetAuthenticationKeyForIntegration.class
							.getName())) {
				fixProcessor = resetAuthenticationKeyForIntegrationProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSResetAuthenticationKeyForIntegration);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributeChargesForm.class.getName())) {
				fixProcessor = distributeChargesProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSDistributeChargesForm);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSRole.class.getName())) {
				fixProcessor = roleProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSRole);
			} else if (msgClassName.equals(CmFinoFIX.CMAgentCashIn.class.getName())){
				fixProcessor = agentCashInProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_AgentCashIn);
		    }
			else if(msgClassName.equals(CMJSClosedAccountSettlementMdn.class.getName())){
				fixProcessor = closedAccountSettlementMdnProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSClosedAccountSettlementMdn);
			}
			else if(msgClassName.equals(CMJSApproveRejectSettlement.class.getName())){
				fixProcessor = approveRejectSettlementProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSApproveRejectSettlement);
			}
			else if(msgClassName.equals(CMJSScheduleTemplate.class.getName())){
				fixProcessor = scheduleTemplateProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSScheduleTemplate);
			}
			else if(msgClassName.equals(CMJSFundDefinitions.class.getName())){
				fixProcessor = fundDefinitionsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSFundDefinitions);
			}
			else if(msgClassName.equals(CMJSFundEvents.class.getName())){
				fixProcessor = fundEventsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSFundEvents);
			}
			else if(msgClassName.equals(CMJSPurpose.class.getName())){
				fixProcessor = purposeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSPurpose);
			}
			else if(msgClassName.equals(CMJSExpirationType.class.getName())){
				fixProcessor = expirationTypeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSExpirationType);
			}
			else if(msgClassName.equals(CMJSValidateChargeExpr.class.getName())){
				fixProcessor = validateChargeExprProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSValidateChargeExpr);
			}
			else if(msgClassName.equals(CmFinoFIX.CMJSNotificationLog.class.getName())){
				fixProcessor = notificationLogProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSNotificationLog);
			}
			else if(msgClassName.equals(CmFinoFIX.CMJSResendNotification.class.getName())){
				fixProcessor = resendNotificationProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSResendNotification);
			}
			else if(msgClassName.equals(CMJSActorChannelMapping.class.getName())){
				fixProcessor = actorChannelMappingProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSActorChannelMapping);
			}
			else if(msgClassName.equals(CMJSActorChannelMappingValidator.class.getName())){
				fixProcessor = actorChannelMappingValidatorProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSActorChannelMappingValidator);
			}
			else if(msgClassName.equals(CMJSAdjustmentsPocket.class.getName())){
				fixProcessor = adjustmentsPocketProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSAdjustmentsPocket);
			}
			else if(msgClassName.equals(CMJSAdjustments.class.getName())){
				fixProcessor = adjustmentsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSAdjustments);
			}
			else if(msgClassName.equals(CMJSLedger.class.getName())){
				fixProcessor = ledgerProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSLedger);
			}
			else if(msgClassName.equals(CMJSShowBalanceDetails.class.getName())){
				fixProcessor = showBalanceDetailsProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSShowBalanceDetails);
			}
			else if(msgClassName.equals(CMJSRuleKey.class.getName())){
				fixProcessor = ruleKeyProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSRuleKey);
			}
			else if(msgClassName.equals(CMJSTxnRuleAddnInfo.class.getName())){
				fixProcessor = transactionRuleAddnInfoProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSTxnRuleAddnInfo);
			}
			else if(msgClassName.equals(CMJSRuleKeyComparision.class.getName())){
				fixProcessor = ruleKeyComparisionProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSRuleKeyComparision);
			}
			else if(msgClassName.equals(CMJSMoneyTransfer.class.getName())){
				fixProcessor = moneyTransferProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSMoneyTransfer);
			}else if(msgClassName.equals(CMJSBranchCodes.class.getName())){
				fixProcessor = branchCodeProcessor;
				tl.setMessageCode(CmFinoFIX.MsgType_JSBranchCodes);
			}
			
			/*
			 * else if
			 * (msgClassName.equals(CmFinoFIX.CMJSBulkLOP.class.getName())) {
			 * fixProcessor = new BulkLOPProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSBulkLOP); } else if
			 * (msgClassName.equals(CmFinoFIX.CMJSMerchant.class.getName())) {
			 * fixProcessor = new MerchantProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchant); } else if
			 * (msgClassName.equals(CmFinoFIX.CMJSBiller.class.getName())) {
			 * fixProcessor = new BillerProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSBiller); }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSDenomination.class.getName()))
			 * { fixProcessor = new DenominationProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSDenomination); }else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSCreditCardDestination.class.getName())) {
			 * fixProcessor = new CCDestinationsProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSCreditCardDestination);
			 * }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSCreditCardTransaction.
			 * class.getName())) { fixProcessor = new
			 * CreditCardTransactionProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSCreditCardTransaction);
			 * }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSMerchantPrefixCode.class
			 * .getName())) { fixProcessor = new MerchantPrefixCodeProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantPrefixCode); }else
			 * if
			 * (msgClassName.equals(CmFinoFIX.CMJSSMSPartner.class.getName())) {
			 * fixProcessor = new SMSPartnerProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSSMSPartner); }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSSMSC.class.getName())) {
			 * fixProcessor = new SMSCProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSSMSC); }else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSResetAPItoken.class.getName())) {
			 * fixProcessor = new ResetAPItokenProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSResetAPItoken); }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSCardInfo.class.getName())) {
			 * fixProcessor = new CardInfoProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSCardInfo); }else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSGenerateBulkLOP.class.getName())) {
			 * fixProcessor = new GenerateBulkLOPProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSGenerateBulkLOP); } else if
			 * (msgClassName.equals(CmFinoFIX.CMJSGenerateLOP.class.getName()))
			 * { fixProcessor = new GenerateLOPProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSGenerateLOP); } else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSMerchantRecharge.class.getName())) {
			 * fixProcessor = new MerchantRechargeProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantRecharge); } else
			 * if
			 * (msgClassName.equals(CmFinoFIX.CMJSMerchantTransfer.class.getName
			 * ())) { fixProcessor = new MerchantTransferProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantTransfer); } else
			 * if
			 * (msgClassName.equals(CmFinoFIX.CMJSLOPDistribute.class.getName(
			 * ))) { fixProcessor = new LOPDistributeProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSLOPDistribute); } else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSBulkLOPDistribute.class.getName())) {
			 * fixProcessor = new BulkLOPDistributeProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSBulkLOPDistribute); } else
			 * if
			 * (msgClassName.equals(CmFinoFIX.CMJSMerchantResetPin.class.getName
			 * ())) { fixProcessor = new MerchantResetPin();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantResetPin); }else if
			 * (
			 * msgClassName.equals(CmFinoFIX.CMJSMerchantNameCheck.class.getName
			 * ())) { fixProcessor = new MerchantUsernameCheckProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantNameCheck); } else
			 * if
			 * (msgClassName.equals(CmFinoFIX.CMJSMerchantDCT.class.getName()))
			 * { fixProcessor = new MerchantDCTProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantDCT); }else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSParentGroupIdCheck.class.getName())) {
			 * fixProcessor = new ParentGroupIDCheckProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSParentGroupIdCheck); } else
			 * if (msgClassName.equals(CmFinoFIX.CMJSRegion.class.getName())) {
			 * fixProcessor = new RegionProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSRegion); } else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSProductIndicator.class.getName())) {
			 * fixProcessor = new ProductIndicatorProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSProductIndicator); }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSMDNRange.class.getName())) {
			 * fixProcessor = new MDNRangeProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMDNRange); }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSSMSCode.class.getName())) {
			 * fixProcessor = new SMSCodeProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSSMSCode); }else if
			 * (msgClassName.equals(CmFinoFIX.CMJSMerchantCode.class.getName()))
			 * { fixProcessor = new MerchantCodeProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantCode); } else if
			 * (msgClassName.equals(CmFinoFIX.CMJSSAPGroupID.class.getName())) {
			 * fixProcessor = new SAPGroupIDProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSSAPGroupID); } else if
			 * (msgClassName
			 * .equals(CmFinoFIX.CMJSMerchantCommission.class.getName())) {
			 * fixProcessor = new MerchantCommissionProcessor();
			 * tl.setMessageCode(CmFinoFIX.MsgType_JSMerchantCommission); }
			 */

			// else if
			// (msgClassName.equals(CmFinoFIX.CMJSEmptySVAMoneyPocket.class.getName()))
			// {
			// fixProcessor = new EmptySVAMoneyPocketProcessor();
			// tl.setMessageCode(CmFinoFIX.MsgType_JSEmptySVAMoneyPocket);
			// }
			else {
				tl.setMessageCode(-1);
				throw new UnsupportedOperationException(msgClassName);
			}
			if (action == null || !action.equalsIgnoreCase("read")) {
				mFinoServiceProvider msp = mFinoServiceProviderProcessor
						.getById(1);
				tl.setMessageData(completePostString);
				tl.setmFinoServiceProviderByMSPID(msp);
				tl.setTransactionTime(new Timestamp(new Date()));
				transactionLogService.save(tl);
			}
			// Check for Authorization
			boolean isAuth = authorizationService.isAuthorized(ItemType.FixMessage,
					msgClassName, action);
			if (isAuth) {
				String loggedUserName = userService.getCurrentUser()
						.getUsername();
				fixProcessor.setLoggedUserName(loggedUserName);
				fixProcessor.setIpAddress(request.getRemoteAddr());

				CFIXMsg returnMsg = fixProcessor.process(msg);
				if(!(CmFinoFIX.JSaction_Select.equals(action)))
				{
				AuditLogDAO auditLogDAO = DAOFactory.getInstance().getAuditLogDAO();
				AuditLog auditLog = new AuditLog();
				auditLog.setJSaction(action);
				auditLog.setMessageName(msgClassName);
				auditLog.setFixMessage(returnMsg.DumpFields());
				auditLogDAO.save(auditLog);
				}
				if (returnMsg instanceof CmFinoFIX.CMJSError
						&& ((CmFinoFIX.CMJSError) returnMsg).getErrorCode() != null
						&& ((CmFinoFIX.CMJSError) returnMsg).getErrorCode() > 0) {
					/**
					 * HACK HACK HACK FIXME: if transaction active then rollback
					 */
					/*
					 * if(transaction.isActive()) transaction.rollback();
					 */

				} else {
					/**
					 * HACK HACK HACK FIXME: if transaction active then commit
					 */
					/*
					 * if(transaction.isActive()) transaction.commit();
					 */
				}// process incoming message and return fix message view

				return new FixView(returnMsg);
			} else {
				log.info("Not authorized for message:" + msgClassName);
				CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				errorMsg.setErrorDescription(MessageText
						._("You are not authorized to perform this operation."));
				return new FixView(errorMsg);
			}
		} catch (Throwable throwable) {
			if (WebContextError.isEmpty()) {
				log.error("Error in fix controller", throwable);

				CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				// TODO : get the description from enum text table
				errorMsg.setErrorDescription(MessageText._("Server error: ")
						+ throwable.toString());
				return new FixView(errorMsg);
			} else {
				return new FixView(WebContextError.getError());
			}
		}
	}
}
