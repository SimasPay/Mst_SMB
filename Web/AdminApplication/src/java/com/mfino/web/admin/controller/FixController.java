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
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionLog;
import com.mfino.enums.ItemType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSActorChannelMapping;
import com.mfino.fix.CmFinoFIX.CMJSActorChannelMappingValidator;
import com.mfino.fix.CmFinoFIX.CMJSAddBankPocketToEmoneySubscriber;
import com.mfino.fix.CmFinoFIX.CMJSAdjustments;
import com.mfino.fix.CmFinoFIX.CMJSAdjustmentsPocket;
import com.mfino.fix.CmFinoFIX.CMJSAgent;
import com.mfino.fix.CmFinoFIX.CMJSAgentCloseApproveReject;
import com.mfino.fix.CmFinoFIX.CMJSAgentClosing;
import com.mfino.fix.CmFinoFIX.CMJSAgentClosingInquiry;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectAddBankPocketToEmoneySubscriber;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectSettlement;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMJSBase;
import com.mfino.fix.CmFinoFIX.CMJSBranchCodes;
import com.mfino.fix.CmFinoFIX.CMJSClosedAccountSettlementMdn;
import com.mfino.fix.CmFinoFIX.CMJSDistrict;
import com.mfino.fix.CmFinoFIX.CMJSExpirationType;
import com.mfino.fix.CmFinoFIX.CMJSFundDefinitions;
import com.mfino.fix.CmFinoFIX.CMJSFundEvents;
import com.mfino.fix.CmFinoFIX.CMJSLedger;
import com.mfino.fix.CmFinoFIX.CMJSMFSBiller;
import com.mfino.fix.CmFinoFIX.CMJSMFSBillerPartner;
import com.mfino.fix.CmFinoFIX.CMJSMoneyTransfer;
import com.mfino.fix.CmFinoFIX.CMJSPartner;
import com.mfino.fix.CmFinoFIX.CMJSPartnerByDCT;
import com.mfino.fix.CmFinoFIX.CMJSProductReferral;
import com.mfino.fix.CmFinoFIX.CMJSProvince;
import com.mfino.fix.CmFinoFIX.CMJSProvinceRegion;
import com.mfino.fix.CmFinoFIX.CMJSPurpose;
import com.mfino.fix.CmFinoFIX.CMJSRuleKey;
import com.mfino.fix.CmFinoFIX.CMJSRuleKeyComparision;
import com.mfino.fix.CmFinoFIX.CMJSScheduleTemplate;
import com.mfino.fix.CmFinoFIX.CMJSServiceProvider;
import com.mfino.fix.CmFinoFIX.CMJSShowBalanceDetails;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberClosing;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberClosingInquiry;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberEdit;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberUpgradeKyc;
import com.mfino.fix.CmFinoFIX.CMJSSubscribers;
import com.mfino.fix.CmFinoFIX.CMJSTxnRuleAddnInfo;
import com.mfino.fix.CmFinoFIX.CMJSValidateChargeExpr;
import com.mfino.fix.CmFinoFIX.CMJSVillage;
import com.mfino.fix.CmFinoFIX.CMRetireSubscriberEmoneyPocket;
import com.mfino.fix.processor.IFixProcessor;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.*;
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
	private DuplicatePartnerCodeCheckProcessor duplicatePartnerCodeCheckProcessor;

	@Autowired
	@Qualifier("EmptySVAPocketProcessorImpl")
	private EmptySVAPocketProcessor emptySVAPocketProcessor;

	@Autowired
	@Qualifier("EnumTextProcessorImpl")
	private EnumTextProcessor enumTextProcessor;

	@Autowired
	@Qualifier("EnumTextSimpleProcessorImpl")
	private EnumTextSimpleProcessor enumTextSimpleProcessor;

	@Autowired
	@Qualifier("GetAvialableBalanceProcessorImpl")
	private GetAvialableBalanceProcessor getAvialableBalanceProcessor;

	@Autowired
	@Qualifier("GetBulkUploadFileDataProcessorImpl")
	private GetBulkUploadFileDataProcessor getBulkUploadFileDataProcessor;

	@Autowired
	@Qualifier("GroupsProcessorImpl")
	private GroupsProcessor groupsProcessor;

	@Autowired
	@Qualifier("IntegrationPartnerMappingProcessorImpl")
	private IntegrationPartnerMappingProcessor integrationPartnerMappingProcessor;

	@Autowired
	@Qualifier("IPMappingprocessorImpl")
	private IPMappingprocessor ipMappingprocessor;

	@Autowired
	@Qualifier("KYCFieldsPrcocessorImpl")
	private KYCFieldsPrcocessor kycFieldsPrcocessor;

	@Autowired
	@Qualifier("KYCProcessorImpl")
	private KYCProcessor kycProcessor;

	@Autowired
	@Qualifier("LOPHistoryProcessorImpl")
	private LOPHistoryProcessor lopHistoryProcessor;

	@Autowired
	@Qualifier("LOPProcessorImpl")
	private LOPProcessor lopProcessor;

	@Autowired
	@Qualifier("MDNCheckProcessorImpl")
	private MDNCheckProcessor mdnCheckProcessor;

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
	private ClosedAccountSettlementMdnProcessor closedAccountSettlementMdnProcessor;

	@Autowired
	@Qualifier("ApproveRejectSettlementProcessorImpl")
	private ApproveRejectSettlementProcessor approveRejectSettlementProcessor;

	@Autowired
	@Qualifier("ScheduleTemplateProcessorImpl")
	private ScheduleTemplateProcessor scheduleTemplateProcessor;

	@Autowired
	@Qualifier("FundDefinitionsProcessorImpl")
	private FundDefinitionsProcessor fundDefinitionsProcessor;

	@Autowired
	@Qualifier("FundEventsProcessorImpl")
	private FundEventsProcessor fundEventsProcessor;

	@Autowired
	@Qualifier("PurposeProcessorImpl")
	private PurposeProcessor purposeProcessor;

	@Autowired
	@Qualifier("ExpirationTypeProcessorImpl")
	private ExpirationTypeProcessor expirationTypeProcessor;

	@Autowired
	@Qualifier("ValidateChargeExprProcessorImpl")
	private ValidateChargeExprProcessor validateChargeExprProcessor;

	@Autowired
	@Qualifier("NotificationLogProcessorImpl")
	private NotificationLogProcessor notificationLogProcessor;

	@Autowired
	@Qualifier("ResendNotificationProcessorImpl")
	private ResendNotificationProcessor resendNotificationProcessor;

	@Autowired
	@Qualifier("ActorChannelMappingProcessorImpl")
	private ActorChannelMappingProcessor actorChannelMappingProcessor;

	@Autowired
	@Qualifier("ActorChannelMappingValidatorProcessorImpl")
	private ActorChannelMappingValidatorProcessor actorChannelMappingValidatorProcessor;

	@Autowired
	@Qualifier("AdjustmentsPocketProcessorImpl")
	private AdjustmentsPocketProcessor adjustmentsPocketProcessor;

	@Autowired
	@Qualifier("AdjustmentsProcessorImpl")
	private AdjustmentsProcessor adjustmentsProcessor;

	@Autowired
	@Qualifier("LedgerProcessorImpl")
	private LedgerProcessor ledgerProcessor;

	@Autowired
	@Qualifier("RuleKeyProcessorImpl")
	private RuleKeyProcessor ruleKeyProcessor;

	@Autowired
	@Qualifier("TransactionRuleAddnInfoProcessorImpl")
	private TransactionRuleAddnInfoProcessor transactionRuleAddnInfoProcessor;

	@Autowired
	@Qualifier("RuleKeyComparisionProcessorImpl")
	private RuleKeyComparisionProcessor ruleKeyComparisionProcessor;

	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	@Autowired
	@Qualifier("ShowBalanceDetailsProcessorImpl")
	private ShowBalanceDetailsProcessor showBalanceDetailsProcessor;

	@Autowired
	@Qualifier("MoneyTransferProcessorImpl")
	private MoneyTransferProcessor moneyTransferProcessor;

	@Autowired
	@Qualifier("BranchCodeProcessorImpl")
	private BranchCodeProcessor branchCodeProcessor;

	@Autowired
	@Qualifier("ServicePartnerProcessorspImpl")
	private ServicePartnerProcessorsp servicePartnerProcessorsp;

	@Autowired
	@Qualifier("SubscriberClosingInquiryProcessorImpl")
	private SubscriberClosingInquiryProcessor subscriberClosingInquiryProcessor;

	@Autowired
	@Qualifier("SubscriberClosingProcessorImpl")
	private SubscriberClosingProcessor subscriberClosingProcessor;
	
	@Autowired
	@Qualifier("AgentClosingInquiryProcessorImpl")
	private AgentClosingInquiryProcessor agentClosingInquiryProcessor;

	@Autowired
	@Qualifier("AgentClosingProcessorImpl")
	private AgentClosingProcessor agentClosingProcessor;
	
	@Autowired
	@Qualifier("ApproveorRejectAgentClosingProcessorImpl")
	private ApproveorRejectAgentClosingProcessor approveOrRejectAgentClosingProcessor;

	@Autowired
	@Qualifier("ProvinceProcessorImpl")
	private ProvinceProcessor provinceProcessor;

	@Autowired
	@Qualifier("ProvinceRegionProcessorImpl")
	private ProvinceRegionProcessor provinceRegionProcessor;

	@Autowired
	@Qualifier("DistrictProcessorImpl")
	private DistrictProcessor districtProcessor;

	@Autowired
	@Qualifier("VillageProcessorImpl")
	private VillageProcessor villageProcessor;

	@Autowired
	@Qualifier("ProductReferralProcessorImpl")
	private ProductReferralProcessor productReferralProcessor;

	@Autowired
	@Qualifier("SubscriberUpgradeProcessorImpl")
	private SubscriberUpgradeProcessor subscriberUpgradeProcessor;

	@Autowired
	@Qualifier("SubscriberUpgradeKycProcessorImpl")
	private SubscriberUpgradeKycProcessor subscriberUpgradeKycProcessor;
	
	@Autowired
	@Qualifier("AddBankPocketToEmoneySubscriberProcessorImpl")
	private AddBankPocketToEmoneySubscriberProcessor addBankPocketToEmoneySubscriber;

	@Autowired
	@Qualifier("SubscriberEditProcessorImpl")
	private SubscriberEditProcessor subscriberEditProcessor;
	
	@Autowired
	@Qualifier("SuspendSubscriberEmoneyPocketProcessorImpl")
	private SuspendSubscriberEmoneyPocketProcessor suspendSubscriberEmoneyPocketProcessor;
	
	@Autowired
	@Qualifier("ReleaseSuspendSubscriberEmoneyPocketProcessorImpl")
	private ReleaseSuspendSubscriberEmoneyPocketProcessor releaseSuspendSubscriberEmoneyPocketProcessor;
	
	@Autowired
	@Qualifier("GetSubscriberUpgradeDataRequestProcessorImpl")
	private GetSubscriberUpgradeDataRequestProcessor getSubscriberUpgradeDataRequestProcessor;
	
	@Autowired
	@Qualifier("ApproveRejectAddBankPocketToEmoneySubscriberProcessorImpl")
	private ApproveRejectAddBankPocketToEmoneySubscriberProcessor approveRejectAddBankPocketToEmoneySubscriberProcessor;
	
	@Autowired
	@Qualifier("RetireEmoneyPocketSubscriberProcessorImpl")
	private RetireEmoneyPocketSubscriberProcessor retireEmoneyPocketSubscriberProcessor;
	
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

			log.info("@kris: processFix completePostString: "+completePostString);
			log.info("@kris: processFix msg: "+msg);
			
			if (msg == null) {
				log.error("Failed to decode fix message: (" + msg.DumpFields()
						+ ") \n" + MfinoUtil.dumpHttpRequest(request));
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
			TransactionLog tl = new TransactionLog();

			log.info("@kris: processFix msgClassName"+msgClassName);
			log.info("@kris: processFix action: "+action);
			
			if (msgClassName.equals(CMJSSubscribers.class.getName())) {
				fixProcessor = subscriberProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscribers);
			} else if (msgClassName.equals(CmFinoFIX.CMJSUsers.class.getName())) {
				fixProcessor = userProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSUsers);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocket.class.getName())) {
				fixProcessor = pocketProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPocket);
			} else if (msgClassName.equals(CmFinoFIX.CMJSSubscriberMDN.class
					.getName())) {
				fixProcessor = subscriberMdnProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscriberMDN);
			} else if (msgClassName.equals(CmFinoFIX.CMJSPocketTemplate.class
					.getName())) {
				fixProcessor = pocketIssuerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPocketTemplate);
			} else if (msgClassName.equals(CmFinoFIX.CMJSEnumText.class
					.getName())) {
				fixProcessor = enumTextProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSEnumText);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCommodityTransfer.class.getName())) {
				fixProcessor = commodityTransferProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCommodityTransfer);
			} else if (msgClassName.equals(CmFinoFIX.CMJSPerson2Person.class
					.getName())) {
				fixProcessor = person2PersonProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPerson2Person);
			} else if (msgClassName.equals(CmFinoFIX.CMJSEnumTextSimple.class
					.getName())) {
				fixProcessor = enumTextSimpleProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSEnumTextSimple);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResetPin.class
					.getName())) {
				fixProcessor = resetPinProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSResetPin);
			} else if (msgClassName.equals(CmFinoFIX.CMJSLOP.class.getName())) {
				fixProcessor = lopProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSLOP);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPendingCommodityTransferRequest.class
							.getName())) {
				fixProcessor = pendingCommodityTransferRequestProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPendingCommodityTransferRequest);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributionChainTemplate.class
							.getName())) {
				fixProcessor = distributionChainTemplateProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDistributionChainTemplate);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributionChainLevel.class
							.getName())) {
				fixProcessor = distributionChainLevelProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDistributionChainLevel);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCashFlow.class
					.getName())) {
				fixProcessor = cashFlowProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCashFlow);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBulkUpload.class
					.getName())) {
				fixProcessor = bulkUploadProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBulkUpload);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResetPassword.class
					.getName())) {
				fixProcessor = resetPasswordProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSResetPassword);
			} else if (msgClassName.equals(CmFinoFIX.CMJSNotification.class
					.getName())) {
				fixProcessor = notificationProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSNotification);
			} else if (msgClassName.equals(CmFinoFIX.CMJSUsernameCheck.class
					.getName())) {
				fixProcessor = usernameCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSUsernameCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocketTemplateCheck.class.getName())) {
				fixProcessor = pocketTemplateCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPocketTemplateCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributionTemplateCheck.class
							.getName())) {
				fixProcessor = dctNameCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDistributionTemplateCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCheckBalance.class
					.getName())) {
				fixProcessor = checkBalanceProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckBalance);
			} else if (msgClassName.equals(CmFinoFIX.CMJSEmptySVAPocket.class
					.getName())) {
				fixProcessor = emptySVAPocketProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSEmptySVAPocket);
			} else if (msgClassName.equals(CmFinoFIX.CMJSMDNCheck.class
					.getName())) {
				fixProcessor = mdnCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSMDNCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBulkUploadEntry.class
					.getName())) {
				fixProcessor = bulkUploadEntryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBulkUploadEntry);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSGetBulkUploadFileData.class.getName())) {
				fixProcessor = getBulkUploadFileDataProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSGetBulkUploadFileData);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckBalanceForSubscriber.class
							.getName())) {
				fixProcessor = checkBalanceForSubscriberProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckBalanceForSubscriber);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCompany.class
					.getName())) {
				fixProcessor = companyProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCompany);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBrand.class.getName())) {
				fixProcessor = brandProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBrand);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBulkUploadFile.class
					.getName())) {
				fixProcessor = bulkUploadFileProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBulkUploadFile);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSBulkUploadFileEntry.class.getName())) {
				fixProcessor = bulkUploadFileEntryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBulkUploadFileEntry);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChannelCode.class
					.getName())) {
				fixProcessor = channelCodeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSChannelCode);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPendingTransactionsFile.class
							.getName())) {
				fixProcessor = pendingTransactionsFileProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPendingTransactionsFile);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPendingTransactionsEntry.class
							.getName())) {
				fixProcessor = pendingTransactionsEntryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPendingTransactionsEntry);
			} else if (msgClassName.equals(CmFinoFIX.CMJSLOPHistory.class
					.getName())) {
				fixProcessor = lopHistoryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSLOPHistory);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSApproveRejectSubscriber.class
							.getName())) {
				fixProcessor = approveRejectSubscriberProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSApproveRejectSubscriber);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBankAdmin.class
					.getName())) {
				fixProcessor = bankAdminProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBankAdmin);
			} else if (msgClassName.equals(CmFinoFIX.CMJSBank.class.getName())) {
				fixProcessor = bankProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBank);
			} else if (msgClassName.equals(CmFinoFIX.CMJSService.class
					.getName())) {
				fixProcessor = serviceProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSService);
			} else if (msgClassName.equals(CmFinoFIX.CMJSTransactionType.class
					.getName())) {
				fixProcessor = transactionTypeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTransactionType);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServiceTransaction.class.getName())) {
				fixProcessor = serviceTransactionProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSServiceTransaction);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionsForService.class
							.getName())) {
				fixProcessor = transactionsForServiceProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTransactionsForService);
			} else if (msgClassName.equals(CmFinoFIX.CMJSServiceProvider.class
					.getName())) {
				fixProcessor = serviceProviderProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSServiceProvider);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServicesForServiceProvider.class
							.getName())) {
				fixProcessor = servicesForServiceProviderProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSServicesForServiceProvider);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSSettlementTemplate.class.getName())) {
				fixProcessor = settlementTemplateProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSettlementTemplate);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSSettlementTemplateCheck.class
							.getName())) {
				fixProcessor = settlementTemplateCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSettlementTemplateCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSPartnerServices.class
					.getName())) {
				fixProcessor = partnerServicesProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPartnerServices);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServiceSettlementConfig.class
							.getName())) {
				fixProcessor = serviceSettlementConfigProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSServiceSettlementConfig);
			} else if (msgClassName.equals(CMJSPartner.class.getName())) {
				// fixProcessor = new PartnerProcessor();
				fixProcessor = servicePartnerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPartner);
			} else if (msgClassName.equals(CMJSAgent.class.getName())) {
				fixProcessor = servicePartnerProcessorsp;
				tl.setMessagecode(CmFinoFIX.MsgType_JSAgent);
			} else if (msgClassName.equals(CMJSServiceProvider.class.getName())) {
				fixProcessor = serviceProviderProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSServiceProvider);
			} else if (msgClassName.equals(CMJSMFSBiller.class.getName())) {
				fixProcessor = mfsBillerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSMFSBiller);
			} else if (msgClassName
					.equals(CMJSMFSBillerPartner.class.getName())) {
				fixProcessor = mfsBillerPartnerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSMFSBillerPartner);
			} else if (msgClassName.equals(CMJSPartnerByDCT.class.getName())) {
				fixProcessor = partnerByDCTProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPartnerByDCT);
			} else if (msgClassName.equals(CmFinoFIX.CMJSTradeNameCheck.class
					.getName())) {
				fixProcessor = tradeNameCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTradeNameCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionAmountDistributionLog.class
							.getName())) {
				fixProcessor = transactionAmountDistributorProcess;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTransactionAmountDistributionLog);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChargeType.class
					.getName())) {
				fixProcessor = chargeTypeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSChargeType);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDuplicateNameCheck.class.getName())) {
				fixProcessor = duplicateNameCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDuplicateNameCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChargeDefinition.class
					.getName())) {
				fixProcessor = chargeDefinitionProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSChargeDefinition);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChargePricing.class
					.getName())) {
				fixProcessor = chargePricingProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSChargePricing);
			} else if (msgClassName.equals(CmFinoFIX.CMJSTransactionRule.class
					.getName())) {
				fixProcessor = transactionRuleProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTransactionRule);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionCharge.class.getName())) {
				fixProcessor = transactionChargeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTransactionCharge);
			} else if (msgClassName.equals(CmFinoFIX.CMJSSharePartner.class
					.getName())) {
				fixProcessor = sharePartnerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSharePartner);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckDependantChargeType.class
							.getName())) {
				fixProcessor = checkDependantChargeTypeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckDependantChargeType);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckTransactionCharge.class
							.getName())) {
				fixProcessor = checkTransactionChargeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckTransactionCharge);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckTransactionRule.class.getName())) {
				fixProcessor = checkTransactionRuleProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckTransactionRule);
			} else if (msgClassName.equals(CmFinoFIX.CMJSKYCCheck.class
					.getName())) {
				fixProcessor = kycProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSKYCCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSKYCCheckFields.class
					.getName())) {
				fixProcessor = kycFieldsPrcocessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSKYCCheckFields);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSSubscribersAdditionalFields.class
							.getName())) {
				fixProcessor = subscriberAdditonalFieldsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscribersAdditionalFields);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckServicePartner.class.getName())) {
				fixProcessor = checkServicePartnerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckServicePartner);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSApproveRejectPartner.class.getName())) {
				fixProcessor = approveRejectPartnerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSApproveRejectPartner);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResetOTP.class
					.getName())) {
				fixProcessor = resetOTPProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSResetOTP);
			} else if (msgClassName.equals(CmFinoFIX.CMJSResendAccessCode.class
					.getName())) {
				fixProcessor = resendAccessCodeProcessor;
				tl.setMessagecode(CmFinoFIX.MessageType_JSResendAccessCode);
			} else if (msgClassName.equals(CMJSBankTellerCashInInquiry.class
					.getName())) {
				fixProcessor = bankTellerCashInInquiryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_BankTellerCashIn);
			} else if (msgClassName.equals(CMJSBankTellerCashInConfirm.class
					.getName())) {
				fixProcessor = bankTellerCashInConfirmProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_BankTellerCashIn);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDuplicatePartnerCodeCheck.class
							.getName())) {
				fixProcessor = duplicatePartnerCodeCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDuplicatePartnerCodeCheck);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSServiceChargeTransactions.class
							.getName())) {
				fixProcessor = serviceChargeTransactionLogProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSServiceChargeTransactions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSBankTellerCashOutInquiry.class
							.getName())) {
				fixProcessor = bankTellerCashOutInquiryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBankTellerCashOutInquiry);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSBankTellerCashOutConfirm.class
							.getName())) {
				fixProcessor = bankTellerCashOutApproveProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBankTellerCashOutConfirm);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReport.class.getName())) {
				fixProcessor = reportProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSReport);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReverseTransaction.class.getName())) {
				fixProcessor = reverseTransactionProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSReverseTransaction);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReverseTransactionConfirm.class
							.getName())) {
				fixProcessor = reverseTransactionConfirmProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSReverseTransactionConfirm);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSReverseTransactionApproveReject.class
							.getName())) {
				fixProcessor = reverseTransactionApproveRejectProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSReverseTransactionApproveReject);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCheckChargeDefinition.class.getName())) {
				fixProcessor = checkChargeDefinitionProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckChargeDefinition);
			} else if (msgClassName.equals(CmFinoFIX.CMJSOfflineReport.class
					.getName())) {
				fixProcessor = offlineReportProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSOfflineReport);
			} else if (msgClassName.equals(CmFinoFIX.CMJSChangePin.class
					.getName())) {
				fixProcessor = changePinProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSChangePin);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSChargeTransactions.class.getName())) {
				fixProcessor = chargeTransactionsViewProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSChargeTransactions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCashOutUnregisteredInquiry.class
							.getName())) {
				fixProcessor = bankTellerUnregisteredCashOutInquiryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCashOutUnregisteredInquiry);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCashOutUnregisteredConfirm.class
							.getName())) {
				fixProcessor = bankTellerUnregisteredCashOutApproveProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCashOutUnregisteredConfirm);
			} else if (msgClassName.equals(CmFinoFIX.CMJSCheckMDN.class
					.getName())) {
				fixProcessor = checkMDNProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCheckMDN);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSApproveRejectBulkTranfer.class
							.getName())) {
				fixProcessor = approveRejectBulkTransferProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSApproveRejectBulkTranfer);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSCancelBulkTranfer.class.getName())) {
				fixProcessor = cancelBulkTransferProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSCancelBulkTranfer);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSGetAvailableBalance.class.getName())) {
				fixProcessor = getAvialableBalanceProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSGetAvailableBalance);
			} else if (msgClassName.equals(CmFinoFIX.CMJSValidatePin.class
					.getName())) {
				fixProcessor = validatePinProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSValidatePin);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSVerifyNonRegisteredBulkTransfer.class
							.getName())) {
				fixProcessor = verifyNonRegisteredBulkTransferProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSVerifyNonRegisteredBulkTransfer);
			} else if (msgClassName.equals(CmFinoFIX.CMJSGroup.class.getName())) {
				fixProcessor = groupsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSGroup);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocketTemplateConfig.class.getName())) {
				fixProcessor = pocketTemplateConfigProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPocketTemplateConfig);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPocketTemplateConfigCheck.class
							.getName())) {
				fixProcessor = pocketTemplateConfigCheckProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPocketTemplateConfigCheck);
			} else if (msgClassName.equals(CmFinoFIX.CMJSDCTRestrictions.class
					.getName())) {
				fixProcessor = dctRestrictionsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDCTRestrictions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSPartnerRestrictions.class.getName())) {
				fixProcessor = partnerRestrictionsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPartnerRestrictions);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSTransactionTypeForPartnerRestrictions.class
							.getName())) {
				fixProcessor = transactionTypePartnerRestrictionsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTransactionTypeForPartnerRestrictions);
			} else if (msgClassName.equals(CmFinoFIX.CMJSRelationshipType.class
					.getName())) {
				fixProcessor = relationshipTypeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSRelationshipType);
			} else if (msgClassName.equals(CmFinoFIX.CMJSSystemParameters.class
					.getName())) {
				fixProcessor = systemParametersProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSystemParameters);
			} else if (msgClassName.equals(CmFinoFIX.CMJSMFSDenominations.class
					.getName())) {
				fixProcessor = mfsDenominationsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSMFSDenominations);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSIntegrationPartnerMapping.class
							.getName())) {
				fixProcessor = integrationPartnerMappingProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSIntegrationPartnerMapping);
			} else if (msgClassName.equals(CmFinoFIX.CMJSIPMapping.class
					.getName())) {
				fixProcessor = ipMappingprocessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSIPMapping);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSResetAuthenticationKeyForIntegration.class
							.getName())) {
				fixProcessor = resetAuthenticationKeyForIntegrationProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSResetAuthenticationKeyForIntegration);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSDistributeChargesForm.class.getName())) {
				fixProcessor = distributeChargesProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDistributeChargesForm);
			} else if (msgClassName.equals(CmFinoFIX.CMJSRole.class.getName())) {
				fixProcessor = roleProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSRole);
			} else if (msgClassName.equals(CmFinoFIX.CMAgentCashIn.class
					.getName())) {
				fixProcessor = agentCashInProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_AgentCashIn);
			} else if (msgClassName.equals(CMJSClosedAccountSettlementMdn.class
					.getName())) {
				fixProcessor = closedAccountSettlementMdnProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSClosedAccountSettlementMdn);
			} else if (msgClassName.equals(CMJSApproveRejectSettlement.class
					.getName())) {
				fixProcessor = approveRejectSettlementProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSApproveRejectSettlement);
			} else if (msgClassName
					.equals(CMJSScheduleTemplate.class.getName())) {
				fixProcessor = scheduleTemplateProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSScheduleTemplate);
			} else if (msgClassName.equals(CMJSFundDefinitions.class.getName())) {
				fixProcessor = fundDefinitionsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSFundDefinitions);
			} else if (msgClassName.equals(CMJSFundEvents.class.getName())) {
				fixProcessor = fundEventsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSFundEvents);
			} else if (msgClassName.equals(CMJSPurpose.class.getName())) {
				fixProcessor = purposeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSPurpose);
			} else if (msgClassName.equals(CMJSExpirationType.class.getName())) {
				fixProcessor = expirationTypeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSExpirationType);
			} else if (msgClassName.equals(CMJSValidateChargeExpr.class
					.getName())) {
				fixProcessor = validateChargeExprProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSValidateChargeExpr);
			} else if (msgClassName.equals(CmFinoFIX.CMJSNotificationLog.class
					.getName())) {
				fixProcessor = notificationLogProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSNotificationLog);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSResendNotification.class.getName())) {
				fixProcessor = resendNotificationProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSResendNotification);
			} else if (msgClassName.equals(CMJSActorChannelMapping.class
					.getName())) {
				fixProcessor = actorChannelMappingProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSActorChannelMapping);
			} else if (msgClassName
					.equals(CMJSActorChannelMappingValidator.class.getName())) {
				fixProcessor = actorChannelMappingValidatorProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSActorChannelMappingValidator);
			} else if (msgClassName.equals(CMJSAdjustmentsPocket.class
					.getName())) {
				fixProcessor = adjustmentsPocketProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSAdjustmentsPocket);
			} else if (msgClassName.equals(CMJSAdjustments.class.getName())) {
				fixProcessor = adjustmentsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSAdjustments);
			} else if (msgClassName.equals(CMJSLedger.class.getName())) {
				fixProcessor = ledgerProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSLedger);
			} else if (msgClassName.equals(CMJSShowBalanceDetails.class
					.getName())) {
				fixProcessor = showBalanceDetailsProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSShowBalanceDetails);
			} else if (msgClassName.equals(CMJSRuleKey.class.getName())) {
				fixProcessor = ruleKeyProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSRuleKey);
			} else if (msgClassName.equals(CMJSTxnRuleAddnInfo.class.getName())) {
				fixProcessor = transactionRuleAddnInfoProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSTxnRuleAddnInfo);
			} else if (msgClassName.equals(CMJSRuleKeyComparision.class
					.getName())) {
				fixProcessor = ruleKeyComparisionProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSRuleKeyComparision);
			} else if (msgClassName.equals(CMJSMoneyTransfer.class.getName())) {
				fixProcessor = moneyTransferProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSMoneyTransfer);
			} else if (msgClassName.equals(CMJSBranchCodes.class.getName())) {
				fixProcessor = branchCodeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSBranchCodes);
			} else if (msgClassName.equals(CMJSSubscriberClosingInquiry.class
					.getName())) {
				fixProcessor = subscriberClosingInquiryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscriberClosingInquiry);
			} else if (msgClassName.equals(CMJSSubscriberClosing.class
					.getName())) {
				fixProcessor = subscriberClosingProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscriberClosing);
			} else if (msgClassName.equals(CMJSProvince.class.getName())) {
				fixProcessor = provinceProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSProvince);
			} else if (msgClassName.equals(CMJSProvinceRegion.class.getName())) {
				fixProcessor = provinceRegionProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSProvinceRegion);
			} else if (msgClassName.equals(CMJSDistrict.class.getName())) {
				fixProcessor = districtProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSDistrict);
			} else if (msgClassName.equals(CMJSVillage.class.getName())) {
				fixProcessor = villageProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSVillage);
			} else if (msgClassName
					.equals(CmFinoFIX.CMJSSubscriberUpgrade.class.getName())) {
				fixProcessor = subscriberUpgradeProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscriberUpgrade);
			}else if (msgClassName.equals(CMJSAgentClosingInquiry.class
					.getName())) {
				fixProcessor = agentClosingInquiryProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSAgentClosingInquiry);
			} else if (msgClassName.equals(CMJSAgentClosing.class
					.getName())) {
				fixProcessor = agentClosingProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSAgentClosing);
				
			} else if (msgClassName.equals(CMJSAgentCloseApproveReject.class.getName())) {
				
				fixProcessor = approveOrRejectAgentClosingProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSAgentCloseApproveReject);
				
			} else if (msgClassName.equals(CMJSProductReferral.class.getName())) {
				fixProcessor = productReferralProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSProductReferral);
			} else if (msgClassName.equals(CMJSSubscriberUpgradeKyc.class.getName())){
				fixProcessor = subscriberUpgradeKycProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscriberUpgradeKyc);
			}else if (msgClassName.equals(CMJSAddBankPocketToEmoneySubscriber.class.getName())){
				fixProcessor = addBankPocketToEmoneySubscriber;
				tl.setMessagecode(CmFinoFIX.MsgType_JSAddBankPocketToEmoneySubscriber);
			} else if (msgClassName.equals(CmFinoFIX.CMSuspendSubscriberEmoneyPocket.class.getName())) {
				fixProcessor = suspendSubscriberEmoneyPocketProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_SuspendSubscriberEmoneyPocket);
			} else if (msgClassName.equals(CmFinoFIX.CMReleaseSuspendSubscriberEmoneyPocket.class.getName())) {
				fixProcessor = releaseSuspendSubscriberEmoneyPocketProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_ReleaseSuspendSubscriberEmoneyPocket);
			} else if (msgClassName.equals(CmFinoFIX.CMGetSubscriberUpgradeDataRequest.class.getName())) {
				fixProcessor = getSubscriberUpgradeDataRequestProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_GetSubscriberUpgradeDataRequest);
			}else if (msgClassName.equals(CMJSApproveRejectAddBankPocketToEmoneySubscriber.class.getName())){
				fixProcessor = approveRejectAddBankPocketToEmoneySubscriberProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSApproveRejectAddBankPocketToEmoneySubscriber);
			}else if (msgClassName.equals(CMRetireSubscriberEmoneyPocket.class.getName())){
				fixProcessor = retireEmoneyPocketSubscriberProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscriberClosing);
			}else if (msgClassName.equals(CMJSSubscriberEdit.class.getName())){
				fixProcessor = subscriberEditProcessor;
				tl.setMessagecode(CmFinoFIX.MsgType_JSSubscriberEdit);
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
				tl.setMessagecode(-1);
				throw new UnsupportedOperationException(msgClassName);
			}
			
			log.info("@kris: processFix getMessagecode: "+tl.getMessagecode());
			
			if (action == null || !action.equalsIgnoreCase("read")) {
				MfinoServiceProvider msp = mFinoServiceProviderProcessor
						.getById(1);
				tl.setMessagedata(completePostString);
				tl.setMfinoServiceProvider(msp);
				tl.setTransactiontime(new Timestamp(new Date()));
				transactionLogService.save(tl);
			}
			// Check for Authorization
			boolean isAuth = authorizationService.isAuthorized(
					ItemType.FixMessage, msgClassName, action);
			if (isAuth) {
				String loggedUserName = userService.getCurrentUser()
						.getUsername();
				fixProcessor.setLoggedUserName(loggedUserName);
				fixProcessor.setIpAddress(request.getRemoteAddr());

				CFIXMsg returnMsg = fixProcessor.process(msg);
				if (!(CmFinoFIX.JSaction_Select.equals(action))) {
					AuditLogDAO auditLogDAO = DAOFactory.getInstance()
							.getAuditLogDAO();
					AuditLog auditLog = new AuditLog();
					auditLog.setJsaction(action);
					auditLog.setMessagename(msgClassName);
					auditLog.setFixmessage(returnMsg.DumpFields());
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
