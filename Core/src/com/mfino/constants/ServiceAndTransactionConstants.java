/**
 * 
 */
package com.mfino.constants;

/**
 * @author Chaitanya
 * 
 */
public class ServiceAndTransactionConstants {
	
	public static final String BANK_POCKET_CODE = "2";
	
	public static final String EMONEY_POCKET_CODE = "1";
	
	public static final String APP_TYPE_SUBSCRIBER= "subapp";
 	
    public static final String APP_TYPE_AGENT= "agentapp";
    
    public static final int NFC_CARDPAN_LENGTH = 16;

	// services
	public static final String SERVICE_ACCOUNT = "Account";

	public static final String SERVICE_WALLET = "Wallet";

	public static final String SERVICE_BANK = "Bank";

	public static final String SERVICE_AGENT = "AgentServices";
	
	public static final String SERVICE_SHOPPING = "Shopping";
	
	public static final String SERVICE_BUY = "Buy";
	
	public static final String SERVICE_PAYMENT="Payment";

	public static final String SERVICE_SYSTEM = "System";
	
	public static final String SERVICE_TELLER= "TellerService";
	
	public static final String SERVICE_NFC= "NFCService";
	
	// transactions
	public static final String TRANSACTION_ACTIVATION = "Activation";

	public static final String TRANSACTION_TRANSACTIONSTATUS = "TransactionStatus";

	public static final String TRANSACTION_CHANGEPIN = "ChangePIN";

	public static final String TRANSACTION_RESETPIN = "ResetPIN";

	public static final String TRANSACTION_AGENTACTIVATION = "AgentActivation";

	public static final String TRANSACTION_CHECKBALANCE = "CheckBalance";

	public static final String TRANSACTION_HISTORY = "History";

	public static final String TRANSACTION_TRANSFER = "Transfer";
	
	public static final String TRANSACTION_E2ETRANSFER = "E2ETransfer";
	
	public static final String TRANSACTION_B2ETRANSFER = "B2ETransfer";
	
	public static final String TRANSACTION_E2BTRANSFER = "E2BTransfer";
	
	public static final String TRANSACTION_TRANSFER_UNREGISTERED = "TransferToUnregistered";

	public static final String TRANSACTION_CASHOUT = "CashOut";
	
	public static final String TRANSACTION_CASHOUT_UNREGISTERED = "CashOutToUnregistered";

	public static final String TRANSACTION_SUBSCRIBERREGISTRATION = "SubscriberRegistration";
	
	public static final String TRANSACTION_SUBSCRIBER_REGISTRATION_THROUGH_WEB = "SubscriberRegistrationThroughWeb";
	
	public static final String TRANSACTION_PARTNER_REGISTRATION_THROUGH_API = "PartnerRegistrationThroughAPI";

	public static final String TRANSACTION_CASHIN = "CashIn";
	
	public static final String TRANSACTION_PURCHASE = "Purchase";
	
	public static final String TRANSACTION_FUNDREIMBURSE = "FundReimburse";
	
	public static final String TRANSACTION_AGENT_TO_AGENT_TRANSFER = "AgentToAgentTransfer";
	
	public static final String TRANSACTION_BILL_PAY = "BillPay";
	
	public static final String TRANSACTION_AIRTIME_PURCHASE = "AirtimePurchase";
	
	public static final String TRANSACTION_SEND_RECEIPT = "SendReceipt";
	
	public static final String	TRANSACTION_TRANSFER_INQUIRY	    = "TransferInquiry";

	public static final String	TRANSACTION_CASHIN_INQUIRY	        = "CashInInquiry";

	public static final String	TRANSACTION_CASHOUT_INQUIRY	        = "CashOutInquiry";

	public static final String 	TRANSACTION_PURCHASE_INQUIRY		= "PurchaseInquiry";
	
	public static final String 	TRANSACTION_AGENT_AGENT_TRANSFER_INQUIRY = "AgentToAgentTransferInquiry";
	
	public static final String TRANSACTION_BILL_PAY_INQUIRY 		= "BillPayInquiry";
	
	public static final String TRANSACTION_BILL_INQUIRY 		= "BillInquiry";
	
	public static final String TRANSACTION_AIRTIME_PURCHASE_INQUIRY = "AirtimePurchaseInquiry";

	public static final String TRANSACTION_CHARGE_SETTLEMENT 		= "ChargeSettlement";
	
	public static final String TRANSACTION_REVERSE_TRANSACTION		= "ReverseTransaction";
	
	public static final String TRANSACTION_REVERSE_CHARGE		= "ReverseCharge";
	
	public static final String TRANSACTION_INTERBANK_TRANSFER_INQUIRY = "InterBankTransferInquiry";
	
	public static final String TRANSACTION_INTERBANK_TRANSFER		= "InterBankTransfer";
	
	public static final String TRANSACTION_CASHOUT_UNREGISTERED_INQUIRY = "CashOutInquiryToUnregistered";
	
	public static final String TRANSACTION_BULK_TRANSFER_INQUIRY = "BulkTransferInquiry";
	
	public static final String TRANSACTION_BULK_TRANSFER		= "BulkTransfer";
	
	public static final String TRANSACTION_SUB_BULK_TRANSFER_INQUIRY = "SubBulkTransferInquiry";
	
	public static final String TRANSACTION_SUB_BULK_TRANSFER		= "SubBulkTransfer";
	
	public static final String TRANSACTION_SETTLE_BULK_TRANSFER_INQUIRY = "SettleBulkTransferInquiry";
	
	public static final String TRANSACTION_SETTLE_BULK_TRANSFER		= "SettleBulkTransfer";

	public static final String TRANSACTION_AGENT_BILL_PAY_INQUIRY 		= "AgentBillPayInquiry";
	
	public static final String TRANSACTION_AGENT_BILL_PAY = "AgentBillPay";
	
	public static final String TRANSACTION_TELLER_EMONEY_CLEARANCE_INQUIRY = "TellerEMoneyClearanceInquiry";

	public static final String TRANSACTION_TELLER_EMONEY_CLEARANCE = "TellerEMoneyClearance";
	
	public static final String TRANSACTION_CASHOUT_AT_ATM = "CashOutAtATM";
	
	public static final String TRANSACTION_CASHOUT_AT_ATM_INQUIRY = "CashOutAtATMInquiry";
	
	public static final String TRANSACTION_REGISTRATION_WITH_ACTIVATION = "RegistrationWithActivation";
	
	public static final String TRANSACTION_CHANGE_SETTINGS = "ChangeSettings";

	public static final String TRANSACTION_ADJUSTMENTS= "Adjustments";
	
	public static final String TRANSACTION_FUND_ALLOCATION_INQUIRY = "FundAllocationInquiry";
	
	public static final String TRANSACTION_FUND_ALLOCATION = "FundAllocation";
	
	public static final String TRANSACTION_FUND_WITHDRAWAL_INQUIRY	 = "FundWithdrawalInquiry";
	
	public static final String TRANSACTION_FUND_WITHDRAWAL	 = "FundWithdrawal";
	
	public static final String TRANSACTION_FUND_REVERSAL    =  "FundReversal";
	
    public static final String TRANSACTION_REFUND_INQUIRY = "RefundInquiry";
	
	public static final String TRANSACTION_TRANSFER_TO_SYSTEM_INQUIRY = "TransferToSystemInquiry";
	
	public static final String TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY = "TransferToTreasuryInquiry";
	
	public static final String TRANSACTION_REFUND = "Refund";
	
	public static final String TRANSACTION_TRANSFER_TO_SYSTEM = "TransferToSystem";
	
	public static final String TRANSACTION_TRANSFER_TO_TREASURY = "TransferToTreasury";
	
	public static final String TRANSACTION_PENDING_SETTLEMENTS_FOR_PARTNER = "PendingSettlementsForPartner";
	
	public static final String TRANSACTION_CASH_IN_TO_AGENT_INQUIRY = "CashInToAgentInquiry";
	
	public static final String TRANSACTION_CASH_IN_TO_AGENT = "CashInToAgent";

	public static final String TRANSACTION_REACTIVATION = "Reactivation";
	public static final String TRANSACTION_SUBSCRIBER_STATUS = "SubscriberStatus";
	
	public static final String TRANSACTION_GET_REGISTRATION_MEDIUM = "GetRegistrationMedium";
	
	public static final String TRANSACTION_GET_THIRD_PARTY_DATA = "GetThirdPartyData";
	
	public static final String TRANSACTION_GET_THIRD_PARTY_LOCATION = "GetThirdPartyLocation";
	
	public static final String TRANSACTION_RESEND_OTP ="ResendOtp";

	public static final String TRANSACTION_INTER_EMONEY_TRANSFER_INQUIRY = "InterEmoneyTransferInquiry";
	
    public static final String TRANSACTION_AIRTIME_PIN_PURCHASE = "AirtimePinPurchase";
	
    public static final String TRANSACTION_AIRTIME_PIN_PURCHASE_INQUIRY = "AirtimePinPurchaseInquiry";

	public static final String TRANSACTION_INTER_EMONEY_TRANSFER = "InterEmoneyTransfer";	
	
	public static final String TRANSACTION_SUBSCRIBER_DETAILS = "SubscriberDetails";
	
	public static final String TRANSACTION_RESETPIN_BY_OTP = "ResetPinByOTP";
	
	public static final String TRANSACTION_ADD_FAVORITE = "AddFavorite";
	
	public static final String TRANSACTION_EDIT_FAVORITE = "EditFavorite";
	
	public static final String TRANSACTION_DELETE_FAVORITE = "DeleteFavorite";
	
	public static final String TRANSACTION_GENERATE_FAVORITE_JSON = "GenerateFavoriteJSON";
	
	public static final String MFA_TRANSACTION_INQUIRY = "Inquiry";
	
	public static final String MFA_TRANSACTION_CONFIRM = "Confirm";

	public static final String TRANSACTION_FRSC_PAYMENT_INQUIRY="FRSCPaymentInquiry";
	
	public static final String TRANSACTION_CHANGEEMAIL="ChangeEmail";
	
	public static final String TRANSACTION_CHANGENICKNAME="ChangeNickname";
	
	public static final String TRANSACTION_CHANGEOTHERMDN = "ChangeOtherMDN";
	
	public static final String TRANSACTION_FRSC_PAYMENT="FRSCPayment";

	public static final String TRANSACTION_NFC_POCKET_BALANCE = "NFCPocketBalance";

	public static final String TRANSACTION_NFC_CARD_UNLINK = "NFCCardUnlink";

	public static final String TRANSACTION_FORGOTPIN_INQUIRY = "ForgotPinInquiry";
	
	public static final String TRANSACTION_FORGOTPIN = "ForgotPin";
	
	public static final String TRANSACTION_NFC_CARD_TOPUP = "NFCCardTopup";
	
	public static final String TRANSACTION_NFC_CARD_TOPUP_REVERSAL = "NFCCardTopupReversal";
	
	public static final String TRANSACTION_NFC_CARD_BALANCE = "NFCCardBalance";	
	
	public static final String TRANSACTION_NFC_CARD_LINK = "NFCCardLink";
	
	public static final String TRANSACTION_NFC_POCKET_TOPUP_INQUIRY = "NFCPocketTopupInquiry";
	
	public static final String TRANSACTION_NFC_POCKET_TOPUP = "NFCPocketTopup";
	
	public static final String TRANSACTION_GET_PUBLIC_KEY = "GetPublicKey";
	
	public static final String TRANSACTION_STARTIMES_PAYMENT_INQUIRY="StarTimesPaymentInquiry";
	
	public static final String TRANSACTION_STARTIMES_PAYMENT="StarTimesPayment";
	
	public static final String TRANSACTION_STARTIMES_QUERY_BALANCE="StarTimesQueryBalance";

	public static final String TRANSACTION_KYC_UPGRADE_INQUIRY="KYCUpgradeInquiry";
	
	public static final String TRANSACTION_KYCUpgrade="KYCUpgrade";
	
	public static final String TRANSACTION_MODIFY_NFC_CARD_ALIAS="ModifyNFCCardAlias";
	
	public static final String TRANSACTION_HISTORY_DETAILED_STATEMENT = "DetailedStatement";
	
	public static final String TRANSACTION_EMAIL_HISTORY_AS_PDF = "EmailHistoryAsPDF";
	
	public static final String TRANSACTION_DOWNLOAD_HISTORY_AS_PDF = "DownloadHistoryAsPDF";
	
	public static final String TRANSACTION_GET_USER_API_KEY			= "GetUserAPIKey";
	
	public static final String TRANSACTION_GET_PROMO_IMAGE			= "GetPromoImage";
	
	public static final String TRANSACTION_QR_PAYMENT_INQUIRY = "QRPaymentInquiry";
	
	public static final String TRANSACTION_QR_PAYMENT = "QRPayment";
	
	public static final String TRANSACTION_DONATION_INQUIRY = "DonationInquiry";
	
	public static final String TRANSACTION_DONATION = "Donation";

	// Messages
	
	public static final String	MESSAGE_MOBILE_TRANSFER	            = "Mobile Transfer";

	public static final String	MESSAGE_CASH_IN	                    = "Cash In";

	public static final String	MESSAGE_TRANSFER_UNREGISTERED       = "UnRegistered Transfer";

	public static final String	MESSAGE_CASH_OUT_UNREGISTERED       = "Cash Out Unregistered";
	
	public static final String	MESSAGE_CASH_OUT	                = "Cash Out";
	
	public static final String	MESSAGE_PURCHASE	                = "Purchase";
	
	public static final String	MESSAGE_BILL_PAY	                = "Bill Pay";
	
	public static final String	MESSAGE_AGENT_AGENT_TRANSFER        = "Agent_Agent_Transfer";
	
	public static final String	MESSAGE_AIRTIME_PURCHASE 			= "Airtime Purchase";
	
	public static final String	MESSAGE_TELLER_CASHIN_BANKTOEMONEY	= "TellerCashIn_BankToEmoney";
	
	public static final String	MESSAGE_TELLER_CASHIN_EMONEYTOEMONEY = "TellerCashIn_EmoneyToEmoney";

	public static final String	MESSAGE_TELLER_CASH_OUT	            = "TellerCashOut";
	
	public static final String	MESSAGE_TELLER_CASH_OUT_TRANSFERTOBANK = "TellerCashOut_TransferToBank";
	
	public static final String	MESSAGE_REVERSE_TRANSACTION        	= "Reverse Transaction";
	
	public static final String	MESSAGE_INTERBANK_TRANSFER	        = "InterBank Transfer";
	
	public static final String MESSAGE_CASHOUT_UNREGISTERED         = "CashOut_Unregistered";

	public static final String MESSAGE_BULK_TRANSFER                = "Bulk Transfer";
	
	public static final String MESSAGE_SUB_BULK_TRANSFER                = "Sub Bulk Transfer";
	
	public static final String MESSAGE_SETTLE_BULK_TRANSFER                = "Settle Bulk Transfer";
	
	public static final String	MESSAGE_AGENT_BILL_PAY	                = "Agent Bill Pay";
	public static final String MESSAGE_TELLER_CLEARANCE                = "Teller EMoney Clearance";
	
	public static final String MESSAGE_WITHDRAW_FROM_ATM				= "Withdraw From ATM";
	public static final String MESSAGE_REVERSE_FROM_ATM				= "Reverse From ATM";
	public static final String MESSAGE_AUTO_REVERSE					= "Auto Reverse";
	public static final String MESSAGE_CHARGE_SETTLEMENT			=	"Settlement Of Charge";
	
	public static final String MESSAGE_FUND_ALLOCATION			    =	"Fund Allocation";
	
	public static final String MESSAGE_FUND_WITHDRAWAL			   =	"Fund Withdrawal";
	
	public static final String MESSAGE_FUND_REVERSAL               =    "Fund Reversal";
	
	public static final String MESSAGE_MOVE_RETIRED_SUBSCRIBER_BALANCE_MONEY = "Move RetiredSubscriber BalanceMoney";
	
	public static final String MESSAGE_CASH_IN_TO_AGENT = "Cash In To Agent";
	
	public static final String MESSAGE_INTER_EMONEY_TRANSFER = "Inter Emoney Transfer";
	
	public static final String	MESSAGE_AIRTIME_PIN_PURCHASE 			= "Airtime Pin Purchase";
	
	public static final String MESSAGE_FRSC_PAYMENT = "FRSC Payment";
	
	public static final String TRANSACTION_GENERATE_OTP = "GenerateOTP";
	
	public static final String TRANSACTION_VALIDATE_OTP = "ValidateOTP";
	
	public static final String TRANSACTION_REGISTRATION_WITH_ACTIVATION_HUB = "RegistrationWithActivationForHub";
	
	public static final String MESSAGE_STARTIMES_PAYMENT = "StarTimes Payment";
	
	public static final String MESSAGE_QR_PAYMENT 			=	"QR Payment";
	
	public static final String MESSAGE_DONATION = "Donation";
	
	public static final String SUBSCRIBER_KTP_VALIDATION = "SubscriberKTPValidation";
	
	public static final String SUBSCRIBER_CLOSING_INQUIRY = "SubscriberClosingInquiry";
	
	public static final String SUBSCRIBER_CLOSING = "SubscriberClosing";
	
	public static final String TRANSACTION_CLOSE_ACCOUNT = "CloseAccount";
	
	public static final String PRODUCT_REFERRAL = "ProductReferral";
	
	public static final String SUBSCRIBER_UPGRADE = "SubscriberUpgrade";
}
