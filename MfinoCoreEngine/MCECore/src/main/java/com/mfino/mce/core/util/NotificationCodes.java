package com.mfino.mce.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sasidhar
 *
 */
public enum NotificationCodes {
	
	Success(0,0,true),
	Failure(10000,0 ,true),
	MobileAgentRechargeFailed(10001,1 ,true),
	ShareLoadFailed(10002,2 ,true),
	BeginDBTransactionFailed(10003,3 ,true),
	BankAccountBalanceDetails(10004,4 ,true),
	DBUpdateSubscriberPocketsFailed(10005,5 ,true),
	RequiredParametersMissing(10006,6 ,true),
	MDNIsNotActive(10007,7 ,true),
	DefaultBankAccountPocketNotFound(10008,8 ,true),
	BankAccountCardPANMissing(10009,9 ,true),
	PocketTemplateBankCodeMissing(10010,10 ,true),
	MDNNotFound(10011,11 ,true),
	DBGetMDNPocketsFailed(10012,12 ,true),
	BOBPocketNotFound(10013,13 ,true),
	DBSubscriberRecordUpdateFailed(10014,14 ,true),
	DBUpdateSubscriberMDNRecordFailed(10015,15 ,true),
	BalanceTooLow(10016,16 ,true),
	BalanceTooHigh(10017,17 ,true),
	AboveDailyTransactionsCountLimit(10018,18 ,true),
	AboveWeeklyTransactionsCountLimit(10019,19 ,true),
	AboveMonthlyTransactionsCountLimit(10020,20 ,true),
	AboveDailyExpenditureLimit(10021,21 ,true),
	AboveWeeklyExpenditureLimit(10022,22 ,true),
	AboveMonthlyExpenditureLimit(10023,23 ,true),
	DBInsertPendingTransferRecordFailed(10024,24 ,true),
	SVABalanceDetails(10025,25 ,true),
	ChangePINCompleted(10026,26 ,true),
	MDNIsRestricted(10027,27 ,true),
	BalanceInquiryCompleted(10028,28 ,true),
	WrongPINSpecified(10029,29 ,true),
	BankAccountPocketNotActive(10030,30 ,true),
	BankAccountChangePinPending(10031,31 ,true),
	NoSVAPocketsWereFound(10032,32 ,true),
	PocketTemplateOperatorCodeMissing(10033,33 ,true),
	BOBPocketIsRestricted(10034,34 ,true),
	BOBPocketNotActive(10035,35 ,true),
	BankAccountGetTransactionsPending(10036,36 ,true),
	DBGetTransactionsFailed(10037,37 ,true),
	NoCompletedTransactionsWereFound(10038,38 ,true),
	CommodityTransaferDetails(10039,39 ,true),
	GetTransactionsCompleted(10040,40 ,true),
	BankAccountBalanceInquiryPending(10041,41 ,true),
	BankAccountTopupPending(10042,42 ,true),
	MobileAgentDistributeCompletedToSenderMDN(10043,43 ,true),
	DestAirtimeSVAPocketNotFound(10044,44 ,true),
	MobileAgentRechargePending(10045,45 ,true),
	SourceAirtimeSVAPocketNotFound(10046,46 ,true),
	ResetPINCompleted(10047,47 ,true),
	ShareLoadPending(10048,48 ,true),
	WrongAuthenticationPhraseSpecified(10049,49 ,true),
	PocketStatusDoesNotEnableActivation(10050,50 ,true),
	PromptForBankAccountActivation(10051,51 ,true),
	BOBPocketActivationCompleted(10052,52 ,true),
	MDNActivationCompleted(10053,53 ,true),
	MDNAlreadyActivated(10054,54 ,true),
	DBGetSubscriberMDNFailed(10055,55 ,true),
	WrongCardPANSuffixSpecified(10056,56 ,true),
	BankAccountActivationPending(10057,57 ,true),
	BankAccountPocketAlreadyActivated(10058,58 ,true),
	TransferRecordNotFound(10059,59 ,true),
	TransferRecordChangedStatus(10060,60 ,true),
	BankAccountToBankAccountPending(10061,61 ,true),
	BankAccountPinSetupRejectedByBank1(10062,62 ,true),
	BankAccountActivationCompleted(10063,63 ,true),
	BankAccountChangePinRejectedByBank1(10064,64 ,true),
	BankAccountChangePinCompleted(10065,65 ,true),
	BankAccountGetTransactionsRejectedByBank1(10066,66 ,true),
	BankAccountTransactionDetails(10067,67 ,false),
	BankAccountBalanceInquiryRejectedByBank1(10068,68 ,true),
	DBCommitTransactionFailed(10069,69 ,true),
	DBUpdatePendingTransferRecordFailed(10070,70 ,true),
	BankAccountTransferInquiryRejectedByBank1(10071,71 ,true),
	BankAccountToBankAccountConfirmationPrompt(10072,72 ,false),
	DBGetPendingCommodityTransferFailed(10073,73 ,true),
	BankAccountTopupRejectedByBank1(10074,74 ,true),
	MobileAgentRechargeCompleted(10075,75 ,true),
	ShareLoadCompleted(10076,76 ,true),
	BankAccountTopupCompleted(10077,77 ,true),
	MobileAgentRechargeRejectedByOperator(10078,78 ,true),
	ShareLoadRejectedByOperator(10079,79 ,true),
	BankAccountTopupRejectedByOperator(10080,80 ,true),
	BankAccountToBankAccountCompletedToSenderMDN(10081,81 ,true),
	BankAccountToBankAccountRejectedByBank1(10082,82 ,true),
	BankAccountActivationFailed(10083,83 ,true),
	BankAccountChangePinFailed(10084,84 ,true),
	BankAccountGetTransactionsFailed(10085,85 ,true),
	BankAccountBalanceInquiryFailed(10086,86 ,true),
	BankAccountTransferInquiryFailed(10087,87 ,true),
	BankAccountTopupFailed(10088,88 ,true),
	BankAccountToBankAccountFailed(10089,89 ,true),
	BOBPocketActivationPending(10090,90 ,true),
	GetMDNInfoFromOperatorFailed(10091,91 ,true),
	GetMDNInfoFromOperatorReturnInActive(10092,92 ,true),
	DBInsertPocketRecordFailed(10093,93 ,true),
	DBInsertSubscriberMDNRecordFailed(10094,94 ,true),
	DBInsertSubscriberRecordFailed(10095,95 ,true),
	DBBeginTransactionFailed(10096,96 ,true),
	TopupNotAllowed(10097,97 ,true),
	NotSubAgentDistributeNotAllowed(10098,98 ,true),
	SubscriberActivationFailedWrongPinLength(10099,99 ,true),
	SenderAirTimeSVAPocketNotActive(10100,100 ,true),
	ReceiverAirTimeSVAPocketNotActive(10101,101 ,true),
	DBGetP2PsFailed(10103,103 ,true),
	BOBPocketAlreadyActivated(10104,104 ,true),
	DistributeNotAllowed(10105,105 ,true),
	TransferAmountAboveMaximumAllowed(10106,106 ,true),
	TransferAmountBelowMinimumAllowed(10107,107 ,true),
	SenderSVAPocketRestricted(10108,108 ,true),
	ReceiverSVAPocketRestricted(10109,109 ,true),
	RequestIsBeingProcessed(10110,110 ,true),
	ServiceNotAvailable(10111,111 ,true),
	TransferIDDoesNotBelongToSourceMDN(10112,112 ,true),
	TransferIDNotFound(10113,113 ,true),
	DBGetTransactionFailed(10114,114 ,true),
	DBInsertLOPRecordFailed(10116,116 ,true),
	DBGetLOPFailed(10117,117 ,true),
	LOPIDNotFound(10118,118 ,true),
	LOPIDDoesNotBelongToSourceMDN(10119,119 ,true),
	NotAllowedForH2HDueToInvalidIP(10120,120 ,true),
	SubscriberStatusDoesNotEnableActivation(10121,121 ,true),
	MDNStatusDoesNotEnableActivation(10122,122 ,true),
	BankChannelTopupCompleted(10123,123 ,true),
	BankChannelTopupCompletedToReceiverMDN(10124,124 ,true),
	BankChannelPaymentCompleted(10125,125 ,true),
	BankChannelPaymentCompletedToReceiverMDN(10126,126 ,true),
	NotAllowedToGenerateLOP(10127,127 ,true),
	DestinationMDNIsNotActive(10128,128 ,true),
	DestinationMDNIsRestricted(10129,129 ,true),
	DestinationBOBPocketNotActive(10130,130 ,true),
	DestinationBOBPocketIsRestricted(10131,131 ,true),
	DestinationDefaultBankAccountPocketNotFound(10132,132 ,true),
	DestinationBankAccountPocketNotActive(10133,133 ,true),
	DestinationBankAccountCardPANMissing(10134,134 ,true),
	DestinationPocketTemplateBankCodeMissing(10135,135 ,true),
	DestinationBOBPocketNotFound(10136,136 ,true),
	BankAccountChangePINRejected(10137,137 ,true),
	PINResetRequired(10138,138 ,true),
	DistributeToSelfNotAllowed(10139,139 ,true),
	LOPDistributeAmountMismatch(10140,140 ,true),
	LOPDistributeDestMDNMismatch(10141,141 ,true),
	LOPStatusDoesNotEnableDistribution(10142,142 ,true),
	DBUpdateLOPRecordFailed(10143,143 ,true),
	DBUpdateMerchantRecordFailed(10144,144 ,true),
	BankAccountToBankAccountCancelledBySubscriber(10145,145 ,true),
	H2HGenerateLOPCompleted(10146,146 ,true),
	H2HTransferInquiryCompleted(10147,147 ,true),
	H2HLOPDetailsCompleted(10148,148 ,true),
	MerchantMCommActivationRequest(10149,149 ,true),
	SourceSVAPocketAlreadyEmpty(10150,150 ,true),
	EmptySVAPocketCompleted(10151,151 ,true),
	BulkUploadFileTypeNotSupported(10152,152 ,true),
	BulkUploadFileFormatError(10153,153 ,true),
	BulkUploadFileSourceMDNInvalid(10154,154 ,true),
	BulkUploadDBInserFailed(10155,155 ,true),
	BulkUploadDBUpdateFailed(156,156 ,true),
	BulkUploadCompleted(10157,157 ,true),
	ValidateMerchantCompleted(10158,158 ,true),
	NoActionRequiredForPendingCommodityTransfer(10159,159 ,true),
	InvalidCSRActionForPendingCommodityTransfer(10160,160 ,true),
	SourceMDNNotFoundForCSRAction(10161,161 ,true),
	DBInsertBulkUploadEntryRecordFailed(10162,162 ,true),
	DBUpdateBulkUploadEntryRecordFailed(10163,163 ,true),
	CSRActionCompleted(10166,166 ,true),
	DestinationMerchantBecameRetired(10167,167 ,true),
	SourceMerchantBecameRetired(10168,168 ,true),
	MoneySVAPocketNotFound(10169,169 ,true),
	AirtimeSVAPocketNotFound(10170,170 ,true),
	DestinationMDNBecameRetired(10171,171 ,true),
	SourceMDNBecameRetired(10172,172 ,true),
	UnregisteredEMoneyIsNotAllowedToTransfer(10173,173 ,true),
	DBGenericFailed(10174,174 ,true),
	BulkUploadDestinationDompetInvalid(10175,175 ,true),
	BulkUploadDuplicateFile(10176,176 ,true),
	LOPAmountAboveMaximumAllowed(10177,177 ,true),
	MobileAgentRechargeCompletedToReceiverMDN(10200,200 ,true),
	ShareLoadCompletedToReceiverMDN(10201,201 ,true),
	ChangeEPINCompleted(10202,202 ,true),
	ResetEPINCompleted(10203,203 ,true),
	ChangeEPINFailedInvalidPINLength(10204,204 ,true),
	ChangeEPINFailedInvalidConfirmPIN(10205,205 ,true),
	ChangeEPINFailedIndenticalCurrentPINEntered(10206,206 ,true),
	ResetEPINFailedInvalidSecretAnswer(10207,207 ,true),
	ResetEPINFailedInvalidPINLength(10208,208 ,true),
	ChangeMPINFailed(10209,209 ,true),
	ResetMPINFailed(10210,210 ,true),
	TransactionFailedDueToTimeLimitTransactionReached(10211,211 ,true),
	TransactionFailedDueToInvalidAmount(10212,212 ,true),
	MobileAgentRechargeFailedDueToRestrictedAccount(10213,213 ,true),
	ShareLoadRechargeFailedDueToRestrictedAccount(10214,214 ,true),
	TransactionFailedDueToDifferentGRC(10215,215 ,true),
	TransactionFailedDueToInsufficientBalance(10216,216 ,true),
	GetLast5TransactionsFailed(10217,217 ,true),
	AccountSecurityLockedDueToSecretAnswer(10218,218 ,true),
	BankAccountPostpaidBillPaymentCompleted(10219,219 ,true),
	MobileAgentDistributeCompletedToReceiverMDN(10220,220 ,true),
	ShareLoadFailedDueToInsufficientBalance(10221,221 ,true),
	ShareLoadFailedDueToBuddyList(10222,222 ,true),
	BankAccountToBankAccountCompletedToReceiverMDN(10223,223 ,true),
	AirtimeLowBalanceNotification(10224,224 ,true),
	AccountSuspendNotification(10225,225 ,true),
	AccountSelfSuspendNotification(10226,226 ,true),
	SMSAlertActivation(10227,227 ,true),
	SMSAlertDeactivation(10228,228 ,true),
	ReleaseSecurityLocked(10229,229 ,true),
	ReleaseAbsoluteLocked(10230,230 ,true),
	ReleaseSelfSuspension(10231,231 ,true),
	BSM_12_BankTransactionFailedInvalidRequest(10232,232 ,true),
	BSM_13_BankTransactionFailedInvalidAmount(10233,233 ,true),
	BSM_40_BankTransactionFailedInvalidRequestedFunction(10234,234 ,true),
	BSM_51_BankTransactionFailedInsufficientBalance(10235,235 ,true),
	BSM_52_BankTransactionFailedCheckingAccountNotExist(10236,236 ,true),
	BSM_53_BankTransactionFailedSavingAccountNotExist(10237,237 ,true),
	BSM_54_BankTransactionFailedExpiredCard(10238,238 ,true),
	BSM_57_BankTransactionFailedCardholderRestriction(10239,239 ,true),
	BSM_62_BankTransactionFailedCardRestricted(10240,240 ,true),
	BSM_63_BankTransactionFailedPINDecryptionError(10241,241 ,true),
	BSM_68_BankTransactionFailedTimeOutResponse(10242,242 ,true),
	BSM_75_BankTransactionFailedPINSecurityLocked(10243,243 ,true),
	BSM_76_BankTransactionFailedAccountNotFound(10244,244 ,true),
	BSM_91_BankTransactionFailedConnectionDown(10245,245 ,true),
	BSM_94_BankTransactionFailedDuplicateTransmission(10246,246 ,true),
	BSM_96_BankTransactionFailedSystemMalfunction(10247,247 ,true),
	BankAccountTopupCompletedToReceiverMDN(10248,248 ,true),
	SVABalanceDetailsFailed(10249,249 ,true),
	MDNActivationFailed(10250,250 ,true),
	ReleaseSuspension(10251,251 ,true),
	TransactionFailedDueToAmountBelowMinimumSpendingLimitPerTransaction(10252,252 ,true),
	TransactionFailedDueToAmountExceedMaximumSpendingLimitPerTransaction(10253,253 ,true),
	CBOSS_ShareLoadFailed_5_DueSelfTopUp(10254,254 ,true),
	CBOSS_ShareLoadFailed_6_DueToInvalidAmount(10255,255 ,true),
	CBOSS_ShareLoadFailed_23_DueToInsufficientBalance(10256,256 ,true),
	CBOSS_ShareLoadFailed_24_DestinationIsPostpaidSubscriber(10257,257 ,true),
	CBOSS_ShareLoadFailed_25_StatusNotAllowed(10258,258 ,true),
	CBOSS_MobileAgentRechargeFailed_1_DueToIncorrectSettlementMethod(10259,259 ,true),
	CBOSS_MobileAgentRechargeFailed_4_SubscriberIsTerminated(10260,260 ,true),
	DestinationMDNNotFound(10261,261 ,true),
	BSM_55_BankTransactionFailedInvalidPIN(10262,262 ,true),
	BSM_GeneralFailure(10263,263 ,true),
	GeneralPendingMessage(264,264 ,true),
	BSM_06_BankTransactionFailed_XLinkGeneralFailure(10265,265 ,true),
	ResetEPINFailedInvalidConfirmPIN(10266,266 ,true),
	CBOSS_BankAccountTopupFailed_1_DueToIncorrectSettlementMethod(10267,267 ,true),
	CBOSS_BankAccountTopupFailed_4_SubscriberIsTerminated(10268,268 ,true),
	BSM_61_ExceededAmountLimit(10269,269 ,true),
	BSM_65_WithdrawalExceededLimitFrequency(10270,270 ,true),
	BSM_14_InvalidCardNumber(10271,271 ,true),
	CBOSS_CheckBalance(10272,272 ,true),
	DompetActivationForReplacementCardSuccess(273,273 ,true),
	E_Money_CheckBalance(10274,274 ,true),
	CBOSS_MobileAgentRechargeFailed_2_DueToInvalidAmount(10275,275 ,true),
	ResetPin_New_MPIN_ToSubscriber(10277,277 ,true),
	ResetPin_New_MerchantPIN_ToMerchant(10278,278 ,true),
	MerchantRestriction_Activate(10279,279 ,true),
	MerchantRestriction_Release(10280,280 ,true),
	DompetRestriction_Activate(10281,281 ,true),
	DompetRestriction_Release(10282,282 ,true),
	CBOSS_Restriction_Activate(10283,283 ,true),
	CBOSSRestriction_Release(10284,284 ,true),
	MDNAccountSuspendNotification(10285,285 ,true),
	MDNReleaseSuspension(10286,286 ,true),
	Activation_PINNotTheSameWithSubsPIN(10287,287 ,true),
	MerchantActivationforBOBPocketAlreadyActive(10288,288 ,true),
	RetireMerchantButSubsStillActive(10289,289 ,true),
	BSM_30_Invalid_PIN_Card_Block(10290,290 ,true),
	Resolve_Transaction_To_Success(10291,291 ,true),
	Resolve_Transaction_To_Fail(10292,292 ,true),
	EMoneytoEMoneyCompleteToSender(10293,293 ,true),
	EMoneytoEMoneyCompleteToReceiver(10294,294 ,true),
	EMoneytoEMoneyFailed_SelfTransfer(10295,295 ,true),
	CashInToEMoneyCompletedToSender(10296,296 ,true),
	CashInToEMoneyCompletedToReceiver(10297,297 ,true),
	CashOutFromEMoneyCompletedToSender(10298,298 ,true),
	CashOutFromEMoneyCompletedToReceiver(10299,299 ,true),
	SenderEMoneyPocketIsRestricted(10300,300 ,true),
	DestinationEMoneyPocketIsRestricted(10301,301 ,true),
	DestinationEMoneyPocketNotFound(10302,302 ,true),
	EMoneyRestriction_Activate(10303,303 ,true),
	EMoneyRestriction_Release(10304,304 ,true),
	BankAccountToEMoneyCompletedToSender(10305,305 ,true),
	BankAccountToEMoneyCompletedToReceiver(10306,306 ,true),
	EMoneyToBankAccountCompletedToSender(10307,307 ,true),
	EMoneyToBankAccountCompletedToReceiver(10308,308 ,true),
	EMoneyToBankAccountMerchantCompletedToSender(10309,309 ,true),
	EMoneyToBankAccountMerchantCompletedToReceiver(10310,310 ,true),
	EmptySVAMoneyPocketCompleted(10311,311 ,true),
	SourceSVAEMoneyPocketAlreadyEmpty(10312,312 ,true),
	SourceSVAEMoneyPocketNotFound(10313,313 ,true),
	UpgradeEMoneyPocket(10314,314 ,true),
	DowngradeEMoneyPocket(10315,315 ,true),
	EMoneyPocketIsRetiredNotification(10316,316 ,true),
	EMoneyLowBalanceNotification(10317,317 ,true),
	BankTopUpFailedDueToSystemMaintenance(10318,318 ,true),
	GenericHTMLPage(10500,500 ,true),
	Multix_Comm_Unknown_Error(10501,501 ,true),
	Multix_Comm_Completed_Susccesfully(10502,502 ,true),
	Multix_Comm_Failure_Message(10503,503 ,true),
	Multix_Comm_Error(10504,504 ,true),
	CBOSS_ShareLoadFailed_46_DueToDenominationNotAvailable(10319,319 ,true),
	CBOSS_ShareLoadFailed_47_DueToBalanceBelowTransferPulsaLimit(10320,320 ,true),
	CBOSS_ShareLoadFailed_48_DueToServiceAvailableOnlyForPrepaidCustomers(10321,321 ,true),
	NBS_RC_05_UnidentifedError(10322,322 ,true),
	NBS_RC_12_ReversalCannotBeImplemented(10323,323 ,true),
	NBS_RC_14_MDNNotFoundInBillingSystem(10324,324 ,true),
	NBS_RC_30_InvalidISOMessage(10325,325 ,true),
	NBS_RC_68_TimeOutFromBillingSystem(10326,326 ,true),
	NBS_RC_70_NoVoucherAvailable(10327,327 ,true),
	NBS_RC_78_SourceMDNSuspended(10328,328 ,true),
	NBS_RC_79_DestMDNSuspended(10329,329 ,true),
	NBS_RC_88_BillAlreadyPaid(10330,330 ,true),
	NBS_RC_89_LinkFailedBetweenmFinoAndNBS(10331,331 ,true),
	NBS_RC_91_DatabaseProblem(10332,332 ,true),
	NBS_RC_96_SystemErrorOrMalfunction(10333,333 ,true),
	NBS_RC_63_ReversalFailedDueToPaymentRequestIsNotReceivedByBillingSystem(10334,334 ,true),
	NBS_RC_31_BankNotSupportedByBillingSystem(10335,335 ,true),
	CreditCardTopupCompleted(10336,336 ,true),
	CreditCardTopupCompletedToReceiverMDN(10337,337 ,true),
	CreditCardPaymentCompleted(10338,338 ,true),
	CreditCardPaymentCompletedToReceiverMDN(10339,339 ,true),
	MerchantSuspendNotification(10523,523 ,true),
	MerchantResumeNotification(10524,525 ,true),
	MerchantSelfSuspendNotification(10525,526 ,true),
	MerchantSelfResumeNotification(10526,527 ,true),
	MerchantSelfSuspendFailedNotification(10527,528 ,true),
	MerchantSelfResumeFailedNotification(10528,529 ,true),
	MerchantSuspendFailedNotification(10529,530 ,true),
	MerchantResumeFailedNotification(10530,531 ,true),
	RequiredSMSParametersMissing(10531,532 ,true),
	MerchantOutOfRangeNotification(10532,533 ,true),
	DestinationOutOfRangeNotification(10534,534 ,true),
	NotDescendentNotification(10535,535 ,true),
	ResetDescendentPINCompleted(10536,536 ,true),
	InvalidSMSCommand(10537,537 ,true),
	ShareLoadPocketRestriction(10538,538 ,true),
	DifferentCompanyCode(10539,539 ,true),
	CheckDetailTransaction(10540,540 ,true),
	CheckDetailTransactionNotFound(10541,541 ,true),
	SimilarTransactionFound(10542,542 ,true),
	DownlineSVABalanceDetails(10543,543 ,true),
	DownlineAirTimeSVAPocketNotActive(10544,544 ,true),
	DownlineSVAPocketRestricted(10545,545 ,true),
	InvalidWebAPIRequest_ParameterMissing(10546,546 ,true),
	CCActivated(10547,547 ,true),
	CCRejected(10548,548 ,true),
	InvalidSMSCommandToCode(10549,549 ,true),
	MultixGenericCCResponse(10550,550 ,true),
	InterBankTransferNotAllowed(10551,551 ,true),
	SubscriberSettingsChangeComplete(10552,552 ,true),
	NoValidEmailExistingForSubscriber(10553,553 ,true),
	EMoneyTransferRequestCompleted(10554,554 ,true),
	DestinationMoneySVAPocketNotActive(10555,555 ,true),
	BankCodeDoesNotMatch(10556,556 ,true),
	EMoneyReversalRequestCompleted(10557,557 ,true),
	CommodityReversalRequestCompleted(10558,558 ,true),
	InvalidSMSAlertRequest_ParameterMissing(10559,559 ,true),
	SMSMessageLength_Exceeded(10560,560 ,true),
	InvalidSMSAlertRequest_ServerIP(10561,561 ,true),
	PartnerID_ShortCode_NotFound(10562,562 ,true),
	InvalidSMSAlertRequest_Token(10563,563 ,true),
	SMSAlertRequest_Success(10564,564 ,true),
	BillPaymentInquiryDetails(10565,565 ,true),
	BillPaymentInquiryCompleted(10566,566 ,true),
	BillPaymentToBankCompleted(10567,567 ,true),
	BillPaymentInquiryToBankFailed(10568,568 ,true),
	BillPaymentToBankFailed(10569,569 ,true),
	BillPaymentTopupCompleted(10570,570 ,true),
	SourceMoneyPocketNotFound(10571,571 ,true),
	DestinationMoneyPocketNotFound(10572,572 ,true),
	SourceDefaultBankAccountPocketNotFound(10573,573 ,true),
	SourceDefaultSVAMoneyPocketNotFound(10574,574 ,true),
	BankAccountRetirementCompleted(10575,575 ,true),
	BankAccountRetirementFailed(10576,576 ,true),
	EmoneyRetirementCompleted(10577,577 ,true),
	EmoneyRetirementFailed(10578,578 ,true),
	InvalidNotificationMethodInChangeSettingsWebAPIRequest(10579,579 ,true),
	InvalidEmailInChangeSettingsWebAPIRequest(10580,580 ,true),
	InvalidLanguageInChangeSettingsWebAPIRequest(10581,581 ,true),
	ActivationFailedOneOrMoreUnretiredBankPockets(10582,582 ,true),
	ActivationFailedOneOrMoreUnretiredSVAEMoneyPockets(10583,583 ,true),
	InvalidSourcePocketCode(10584,584 ,true),
	InvalidDestinationPocketCode(10585,585 ,true),
	SubscriberDoesNOTHavePocketOfThisType(10586,586 ,true),
	SVAEMoneyPocketCardPANMissing(10587,587 ,true),
	SVAEMoneyPocketAlreadyActivated(10588,588 ,true),
	SetDefaultMoneyPocketSuccessful(10589,589 ,true),
	SetDefaultMoneyPocketFailed(10590,590 ,true),
	EMoneyActivationSuccessful(10591,591 ,true),
	EMoneyActivationFailed(10592,592 ,true),
	BankAccountRetirementCompletedWithDefaultWarning(10593,593 ,true),
	SVAEMoneyAccountRetirementCompletedWithDefaultWarning(10594,594 ,true),
	MoreThanOneUnretiredEMoneyPocketPocketcodeRequired(10595,595 ,true),
	MoreThanOneUnretiredBankPocketPocketcodeRequired(10596,596 ,true),
	GetEMoneyHistorySupportedOnlyForSMART(10597,597 ,true),
	MoreThanOneDefaultBankPocketsPocketCodeRequired(10598,598 ,true),
	MoreThanOneDefaultEMoneyPocketsPocketCodeRequired(10599,599 ,true),
	DestinationDefaultSVAMoneyPocketNotFound(10600,600 ,true),
	NotSVAEMoneyAccount(10601,601 ,true),
	NotBankAccount(10602,602 ,true),
	BillPaymentReversalCompleted(10603,603 ,true),
	BillPaymentTopupReversalCompleted(10604,604 ,true),
	BillPaymentTopupInquiryDetails(10605,605 ,true),
	BillPaymentTopupInquiryCompleted(10606,606 ,true),
	BillPaymentTopupInvalidDenomination(10607,607 ,true),
	NotMoneyAccount(10608,608 ,true),
	CardPANMissingFromOneOfYourMoneyPockets(10609,609 ,true),
	BillPayment_88_BillAlreadyPaid(10610,610 ,true),
	BankAccountAlreadyRetired(10611,611 ,true),
	MoneyPocketNotActive(10612,612 ,true),
	InternalSystemError(909090,-1,true),
	ChargeDistributionCompleted(10636, 636, false),
	PurchaseFromEMoneyCompletedToSender(10644,644 ,true),
	PurchaseFromEMoneyCompletedToReceiver(10645,645 ,true),
	AgentToAgentTransferCompletedToSender(10648,648 ,true),
	AgentToAgentTransferCompletedToReceiver(10649,649 ,true),
	BillPayCompletedToSender(10653,653,true),
	BillPayCompletedToReceiver(10654,654,true),
	BillPayPending(10656,656,true),
	AirtimePurchaseInquiry(10660,660,false),
	AirtimePurchaseConfirmation(10661,661,true),
	AirtimePurchaseConfirmationReceiver(10664,664,true),
	AirtimePurchaseFailed(10662,662,true),
	DSTVTransactionCannotBeCancelled(10665,665,true),
	ReverseTransactionCompleteToSender(10666,666,true),
	ReverseTransactionCompleteToReceiver(10667,667,true),
	FeatureNotAvailable(10650,650,false),
	IBTRestricted(10675,675,true),
	TransferToUnRegisteredConfirmationPrompt(10676,676, false),
	CashOutToUnRegisteredConfirmationPrompt(10677,677, false),
	TransferToUnRegisteredCompletedToSender(10678,678, true),
	TransferToUnRegisteredCompletedToReceiver(10679,679, true),
	CashOutToUnRegisteredCompletedToSender(10680,680, true),
	CashOutToUnRegisteredCompletedToReceiver(10681,681, true),
	IBTPending(10682,682, true),
	IBTFailed(10683,683, true),
	IBTInquiry(10684,684, true),
	IBTConfirmation(10685,685, true),
	TransactionCannotBeCancelled(10688,688, false),
	AirtimePurchaseReverted(10689,689, false),
	AirtimePurchaseRevertFailed(10690,690, false),
	BulkTransferCompletedToPartner(10691,691,false),
	BulkTransferCompletedToSubscriber_Dummy(10692,692,false),
	BulkTransferCompletedToSubscriber(10693,693,true),
	GetAvialableBalance(10694,694,false),
	BillaySuccessfulToOnBehalfOfMDN(10697,697,true),
	BillayPendingToOnBehalfOfMDN(10698,698,true),
	BulkTransferReverseCompletedToPartner(10701,701,false),
	TransferToBankAccountCompletedToSender(10703,703,true),
	TransferToBankAccountCompletedToReceiver(10704,704,true),
	CashOutAtATMConfirmationPrompt(10708,708,false),
	CashOutAtATMConfirmedToSender(10709,709,true),
	SuccessfulCashOutFromATM(10711,711,false),
	BillpaymentInquirySuccessful(10713,713,false),
	BillpaymentFailed(10714,714,true),
	
	BillpaymentConfirmationSuccessful(10715,715,true),
	
	AutoReverseSuccess(10716, 716, false),
	AutoReverseSuccessToSource(10718, 718, true),

	BankResponseDoNotHonor(11005,719,true),
	BankResponseRequestInProgress(11009,720,true),
	BankResponseSuspectedMalfunction(11022,721,true),
	BankResponseRecordNotFound(11025,722,false),
	BankResponseDuplicateRecord(11026,723,false),
	BankResponseFunctionNotSupported(11040,724,false),
	BankResponseNotSufficientFunds(11051,725,true),
	BankResponseNoCheckAccount(11052,726,true),
	BankResponseNoSavingsAccount(11053,727,true),
	BankResponseIncorrectPin(11055,728,true),
	BankResponseSuspectedFraud(11059,729,true),
	BankResponseExceedsWithdrawlLimit(11061,730,true),
	BankResponseRestrictedCard(11062,731,true),
	BankResponseSecurityViolation(11063,732,true),
	BankResponseExceedsWithdrawlFrequency(11065,733,true),
	BankResponsePinTriesExceeded(11075,734,true),
	BankResponseRoutingError(11092,735,false),
	BankResponseViolationOfLaw(11093,736,false),
	BankResponseDuplicatetransaction(11094,737,false),
	BankResponseReconcileError(11095,738,false),
	BankResponseSystemMalfunction(11096,739,false),
	BankResponseExceedsCashLimit(11098,740,true),
	BankResponseError(11006,741,true),
	BankResponseInvalidTransaction(11012,742,true),
	BankResponseInvalidAmount(11013,743,true),
	BankResponseInvalidCardNumber(11014,744,true),
	BankResponseCardExpired(11054,745,true),
	BankResponseTransactionNotPermitted(11057,746,true),
	BillDetails(12021,2021,false),
	GetBillDetailsFailed(12022,2022,false),
	SenderBankPocketIsRestricted(10747, 747, false),
	BillpaymentConfirmationSuccessfulToDest(102027,2027,true),
	FundAllocationConfirmationPrompt(10758,758,false),
	FundAllocationConfirmedToSender(10759,759,true),
	FundWithdrawalConfirmationPrompt(10760,760,false),
	FundWithdrawalConfirmedToMerchant(10761,761,true),
	FundAllocatedExpired(10762,762,true),
	InsufficientFunds(10763,763,true),
	RegenFACAuto(10764,764,true),
	RegenFACManual(10765,765,true),
	PartnerNotFoundNewFac(10766,766,true),
	DestinationAgentIsNotActiveNewFac(10767,767,true),
	DestinationAgentNotFoundNewFac(10768,768,true),
	DestinationAgentIsRestrictedNewFac(10769,769,true),
	DestinationMoneyPocketNotFoundNewFac(10770,770,true),
	MoneyPocketNotActiveNewFac(10771,771,true),
	TransferRecordChangedStatusNewFac(10772,772,true),
	TransferRecordNotFoundNewFac(10773,773,true),
	ServiceNOTAvailableForAgentNewFac(10774,774,true),
	SourceMoneyPocketNotFoundNewFac(10775,775,true),
	InvalidFundAccessCodeNewFac(10776,776,true),
	ReverseFundRequestInitaited(10777,777,true),
	PartnerNotFoundReversal(10778,778,true),
	DestinationAgentIsNotActiveReversal(10779,779,true),
	DestinationAgentNotFoundReversal(10780,780,true),
	DestinationAgentIsRestrictedReversal(10781,781,true),
	DestinationMoneyPocketNotFoundReversal(10782,782,true),
	MoneyPocketNotActiveReversal(10783,783,true),
	TransferRecordChangedStatusReversal(10784,784,true),
	TransferRecordNotFoundReversal(10785,785,true),
	ServiceNOTAvailableForAgentReversal(10786,786,true),
	SourceMoneyPocketNotFoundReversal(10787,787,true),
	InvalidFundAccessCodeReversal(10788,788,true),
	InsufficientFundsNewFac(10789,789,true),
	InsufficientFundsReversal(10790,790,true),
	FundPartialWithdrawalConfirmedToMerchant(10791,791,true),
	NotEligibleMerchant(10792,792,true),
	NotEligibleMerchantNewFac(10793,793,true),
	NotEligibleMerchantReversal(10794,794,true), 
	FundAllocationSuccessfulToOnBehalfOfMDN	(10795,795,true), 
	FundPartialWithdrawalConfirmedToOnBehalfOfMDN(10796,796,true),
	InvalidFundRequest(10797,797,true),
	FundAllocatedExpiredReversal(10798,798,true),
	FundAllocatedExpiredNewFac(10799,799,true),
	FundWithdrawalConfirmedToOnBehalfOfMDN(10802,802,true),
	InvalidFundDefinitionOrPurpose(10803,803,false),
	BillpaymentConfirmationSuccessfulToReceiver(102029,2029,true),
	BillpaymentConfirmationSuccess(102030,2030,true),
	FundingOfAgentSuccessToSender(10804,804,false),
	FundingOfAgentSuccessToReciever(10805,805,true),
	InvalidFundAccessCodeNewFacSpecificMerchant(10809,809,false),
	InterEmoneyTransferInquirySuccessful(10820,820,false),
	InterEmoneyTransferFailed(10821,821,true),
	InterEmoneyTransferConfirmationSuccessful(10822,822,true),
	InterEmoneyTransferConfirmationSuccessfulToDest(10823,823,true),
	NewSubscriberActivation(102033,2033,true),
	FundAllocationReversalToSender(10824,824,false),
	FundAllocationReversalToOnBehalfOfMDN(10825,825,true),
	FundCompleteWithdrawalConfirmedToMerchant(10826,826,true),
	FundCompleteWithdrawalConfirmedToOnBehalfOfMDN(10827,827,true),
	FundAllocationReversalToReceiver(10828,828,true),
	FRSCPaymentInquirySuccessful(10830,830,false),
	FRSCPaymentFailed(10831,831,true),
	FRSCPaymentConfirmationSuccessful(10832,832,true),
	FRSCPaymentConfirmationSuccessfulToOnBehalfOfMDN(10833,833,true),
	NFC_Pocket_Balance(10834,834,false),
	NFCCardLinkSuccess(10835,836,false),
	NFCCardLinkFailed(10836,837,false),
	NFCCardUnlinkSuccess(10838,838,false),
	NFCCardUnlinkFailed(10839,839,false),
	NFCPocketTopupInquirySuccess(10841,841,true),
	NFCPocketTopupSuccess(10842,842,true),
	NFCCardActive(10843,843,false),
	NFCCardNotActive(10844,844,false),
	DonationCompleteToSender(10849,849,true),
	DonationCompleteToReceiver(10850,850,true),
	GetUserAPIKeyFailed(12102,2102,false),
	GetUserAPIKeySuccess(12103,2103,false),
	PLNWrongIDPELID(12115,2079,true),
	PLNWrongMeterID(12116,2080,true),
	PLNBillIsPending(12117,2081,true),
	PLNUnderPurchase(12118,2082,true),
	PLNOverPurchase(12119,2083,true),
	PLNRegNoExpired(12120,2084,true),
	PLNBillAlreadyPaid(12121,2085,true),
	PLNLatestBillNA(12122,2086,true),
	PLNSuccess(12123,2087,true),
	PLNTimeout(12124,2088,true),
	SubscriberDetailsSuccessMessage(12035,2035,true),
	SubscriberDetailsFailMessage(12036,2036,true),
	QueryBalanceDetails(12095,2095,false),
	QRpaymentInquirySuccessful(12109,2109,false),
	QRpaymentFailed(12110,2110,true),
	QRpaymentConfirmationSuccessful(12111,2111,true),
	QRPaymentCompletedToReceiver(12112,2112,true),
	InternalLoginError(12125,2125,false),
	AgentSubscriberCashinSuccessToSender(12156,2156,true),
	AgentSubscriberCashinSuccessToReceiver(12157,2157,true),
	TransferToUangkuToSender(12176,2176,true),
	AboveMonthlyIncommingLimit(12177,2317 ,true);
	
	private static Map<Integer,NotificationCodes> nofificationCodeMap = null;
	private static Map<Integer, Integer> notificationCodetoInternalErroCodeMap = null;
	
	private static Map<Integer,NotificationCodes> receiverNofificationCodeMap = null;
	
	private static Map<Integer,NotificationCodes> onBehalfOfNotificationCodeMap = null;
	
	NotificationCodes(Integer internalErrorCode, Integer notificationCode, Boolean isNotificationRequired){
		this.internalErrorCode = internalErrorCode;
		this.notificationCode = notificationCode;
		this.isNotificationRequired = isNotificationRequired;
	}
	
	private Integer messageType;
	private Integer externalResponseCode;
	private Integer internalErrorCode;
	private Integer notificationCode;
	private Boolean isNotificationRequired;
	
	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public Integer getExternalResponseCode() {
		return externalResponseCode;
	}

	public void setExternalResponseCode(Integer externalResponseCode) {
		this.externalResponseCode = externalResponseCode;
	}

	public Integer getInternalErrorCode() {
		return internalErrorCode;
	}

	public void setInternalErrorCode(Integer internalErrorCode) {
		this.internalErrorCode = internalErrorCode;
	}

	public Integer getNotificationCode() {
		return notificationCode;
	}

	public void setNotificationCode(Integer notificationCode) {
		this.notificationCode = notificationCode;
	}
	
	// this code need to be static so when the class loads all the data is loaded.
	static
	{
		nofificationCodeMap = new HashMap<Integer, NotificationCodes>();
		notificationCodetoInternalErroCodeMap = new HashMap<Integer, Integer>();
		
		NotificationCodes[] notificationCodesArr = NotificationCodes.values();
		
		for (int i = 0; i < notificationCodesArr.length; i++) {
			nofificationCodeMap.put(notificationCodesArr[i].getInternalErrorCode(), notificationCodesArr[i]);
			notificationCodetoInternalErroCodeMap.put(notificationCodesArr[i].getNotificationCode(), notificationCodesArr[i].getInternalErrorCode());
		}
		
		receiverNofificationCodeMap = new HashMap<Integer, NotificationCodes>();
		onBehalfOfNotificationCodeMap = new HashMap<Integer, NotificationCodes>();

		receiverNofificationCodeMap.put(BankAccountToBankAccountCompletedToSenderMDN.getInternalErrorCode(), BankAccountToBankAccountCompletedToReceiverMDN);
		receiverNofificationCodeMap.put(CashInToEMoneyCompletedToSender.getInternalErrorCode(), CashInToEMoneyCompletedToReceiver);
		receiverNofificationCodeMap.put(AgentSubscriberCashinSuccessToSender.getInternalErrorCode(), AgentSubscriberCashinSuccessToReceiver);
		receiverNofificationCodeMap.put(BankAccountToEMoneyCompletedToSender.getInternalErrorCode(), BankAccountToEMoneyCompletedToReceiver);
		receiverNofificationCodeMap.put(CashInToEMoneyCompletedToSender.getInternalErrorCode(), CashInToEMoneyCompletedToReceiver);
		receiverNofificationCodeMap.put(CashOutFromEMoneyCompletedToSender.getInternalErrorCode(), CashOutFromEMoneyCompletedToReceiver);
		receiverNofificationCodeMap.put(EMoneytoEMoneyCompleteToSender.getInternalErrorCode(), EMoneytoEMoneyCompleteToReceiver);
		receiverNofificationCodeMap.put(EMoneyToBankAccountMerchantCompletedToSender.getInternalErrorCode(), EMoneyToBankAccountMerchantCompletedToReceiver);
		receiverNofificationCodeMap.put(EMoneyToBankAccountCompletedToSender.getInternalErrorCode(), EMoneyToBankAccountCompletedToReceiver);
		receiverNofificationCodeMap.put(PurchaseFromEMoneyCompletedToSender.getInternalErrorCode(), PurchaseFromEMoneyCompletedToReceiver);
		receiverNofificationCodeMap.put(AgentToAgentTransferCompletedToSender.getInternalErrorCode(), AgentToAgentTransferCompletedToReceiver);
		receiverNofificationCodeMap.put(BillPayCompletedToSender.getInternalErrorCode(), BillPayCompletedToReceiver);
		receiverNofificationCodeMap.put(AirtimePurchaseConfirmation.getInternalErrorCode(), AirtimePurchaseConfirmationReceiver);
		receiverNofificationCodeMap.put(ReverseTransactionCompleteToSender.getInternalErrorCode(), ReverseTransactionCompleteToReceiver);
		receiverNofificationCodeMap.put(TransferToUnRegisteredCompletedToSender.getInternalErrorCode(), TransferToUnRegisteredCompletedToReceiver);
		receiverNofificationCodeMap.put(CashOutToUnRegisteredCompletedToSender.getInternalErrorCode(), CashOutToUnRegisteredCompletedToReceiver);
		receiverNofificationCodeMap.put(BulkTransferCompletedToSubscriber_Dummy.getInternalErrorCode(), BulkTransferCompletedToSubscriber);
		receiverNofificationCodeMap.put(TransferToBankAccountCompletedToSender.getInternalErrorCode(), TransferToBankAccountCompletedToReceiver);
		receiverNofificationCodeMap.put(FundPartialWithdrawalConfirmedToMerchant.getInternalErrorCode(),FundPartialWithdrawalConfirmedToMerchant);
		receiverNofificationCodeMap.put(FundWithdrawalConfirmedToMerchant.getInternalErrorCode(), FundWithdrawalConfirmedToMerchant);
        receiverNofificationCodeMap.put(BillpaymentConfirmationSuccessful.getInternalErrorCode(),BillPayCompletedToReceiver);
        receiverNofificationCodeMap.put(FundingOfAgentSuccessToSender.getInternalErrorCode(),FundingOfAgentSuccessToReciever);
        receiverNofificationCodeMap.put(BillpaymentConfirmationSuccess.getInternalErrorCode(), BillpaymentConfirmationSuccessfulToReceiver);
        receiverNofificationCodeMap.put(FundCompleteWithdrawalConfirmedToMerchant.getInternalErrorCode(), FundCompleteWithdrawalConfirmedToMerchant);
        receiverNofificationCodeMap.put(FundAllocationReversalToSender.getInternalErrorCode(), FundAllocationReversalToReceiver);
        receiverNofificationCodeMap.put(QRpaymentConfirmationSuccessful.getInternalErrorCode(),QRPaymentCompletedToReceiver);
        receiverNofificationCodeMap.put(DonationCompleteToSender.getInternalErrorCode(),DonationCompleteToReceiver);

		onBehalfOfNotificationCodeMap.put(BillPayCompletedToSender.getInternalErrorCode(),BillaySuccessfulToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(BillPayPending.getInternalErrorCode(),BillayPendingToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(BillpaymentConfirmationSuccessful.getInternalErrorCode(),BillpaymentConfirmationSuccessfulToDest);
		onBehalfOfNotificationCodeMap.put(FundAllocationConfirmedToSender.getInternalErrorCode(), FundAllocationSuccessfulToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(FundPartialWithdrawalConfirmedToMerchant.getInternalErrorCode(),FundPartialWithdrawalConfirmedToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(FundWithdrawalConfirmedToMerchant.getInternalErrorCode(), FundWithdrawalConfirmedToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(RegenFACAuto.getInternalErrorCode(), RegenFACAuto);
		onBehalfOfNotificationCodeMap.put(RegenFACManual.getInternalErrorCode(),RegenFACManual);
		onBehalfOfNotificationCodeMap.put(BillpaymentConfirmationSuccess.getInternalErrorCode(),BillaySuccessfulToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(FundAllocationReversalToSender.getInternalErrorCode(), FundAllocationReversalToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(FundCompleteWithdrawalConfirmedToMerchant.getInternalErrorCode(), FundCompleteWithdrawalConfirmedToOnBehalfOfMDN);
		onBehalfOfNotificationCodeMap.put(FRSCPaymentConfirmationSuccessful.getInternalErrorCode(), FRSCPaymentConfirmationSuccessfulToOnBehalfOfMDN);

	}

	public static NotificationCodes getNotificationCode(Integer internalErrorCode)
	{
		return nofificationCodeMap.get(internalErrorCode);
	}
	
	public static NotificationCodes getReceiverNotificationCode(Integer internalErrorCode)
	{
		return receiverNofificationCodeMap.get(internalErrorCode);
	}
	
	
	public static NotificationCodes getOnBehalfOfNotificationCode(Integer errorCode){
		return onBehalfOfNotificationCodeMap.get(errorCode);
	}

	public Boolean getIsNotificationRequired() {
		return isNotificationRequired;
	}

	public void setIsNotificationRequired(Boolean isNotificationRequired) {
		this.isNotificationRequired = isNotificationRequired;
	}
	
	public static Integer getNotificationCodeFromInternalCode(Integer internalErrorCode){
		
		// in case the internal error code is not set lets treat it as success for now
		// ideally there should no case where internalErrocode is not passed. 
		if(internalErrorCode==null)
		{
			internalErrorCode = 0; 
		}
		return getNotificationCode(internalErrorCode).getNotificationCode();
	}
	
	public static Integer getInternalErrorCodeFromNotificationCode(Integer notificationCode)
	{
		return notificationCodetoInternalErroCodeMap.get(notificationCode);
	}
	
}
