package com.mfino.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.session.HibernateSessionHolder;

public class ConfigurationUtil {

	// this cannot be initialized because the static initializer of the log
	// factory depends on ConfigurationUtil
	// which makes the ConfigurationUtil not able to depend on anything
	// it will be initialized as the last step of the static initializer.
	private static Logger log = LoggerFactory.getLogger(ConfigurationUtil.class);
	private static Properties _configurationProperties = new Properties();
	private static File _configFile;
	private static String _propertyFileName = "mfino.properties";
	private static String _propertyDir = ".mfino";
	private static boolean isWindows = false;
	private static boolean isUnixLike = false;
	
	public static final Long ANY_GROUP_ID = 1L;
	public static final Long DEFAULT_CHANNEL_ID = 7L;
	
	enum ConfigurationKey {

		ApplicationURL("mfino.application.url", "http://staging.mfino.com:8080/AdminApplication"),
		MerchantAddSubject("mfino.merchant.add.email.subject", "mWallet System Advisories"),
		ResetPasswordSubject("mfino.user.reset_password.email.subject", "mWallet System Advisories"),
		AddUserSubject("mfino.user.add.email.subject", "mWallet System Advisories"),
		EmailAdditionalMessage("mfino.email.additional.message", " "),
		EmailSignature("mfino.email.signature", "mWallet Admin"),
		MailServer("mfino.mail.server", "smtp.gmail.com"),
		MailServerPort("mfino.mail.server.port", "465"),
		MailServerRequireAuth("mfino.mail.server.require_auth", "true"),
		MailServerAuthName("mfino.mail.server.auth_name", "donotreply.mfino@gmail.com"),
		MailServerFromName("mfino.mail.server.from_name", "local"),
		MailServerAuthPassword("mfino.mail.server.auth_password", "donotreplymFino260"),
		MailServerRequireSSL("mfino.mail.server.require_ssl", "true"),
		I18NMessagePath("mfino.i18n.message", "/com/mfino/i18n/message/"),
		MerchantDefaultPocketTemplateID("mfino.merchant_default_pocket_template_id", "3"),
		PINLength("mfino.pin_length", "6"),
		BackendURL("mfino.backend.URL", "http://localhost:30013/"),
		DataPushUserName("mfino.datapush.username", "zsmart"),
		DataPushPassword("mfino.datapush.password", "zsmart123"),
		UploadFileSizeLimit("mfino.upload_file_size_limit", "5"),
		PendingTransactionTimePeriod1("mfino.alert.pending_transaction.interval_1", "01:30"),
		PendingTransactionTimePeriod2("mfino.alert.pending_transaction.interval_2", "12:50"),
		PendingTransactionTimePeriod3("mfino.alert.Pending_transaction.interval_3", "24:00"),
		PendingTransactionThreshold1("mfino.alert.pending_transaction.threshold_1", "50000"),
		PendingTransactionThreshold2("mfino.alert.pending_transaction.threshold_2", "50000"),
		PendingTransactionThreshold3("mfino.alert.pending_transaction.threshold_3", "50000"),
		PendingTransactionLimitMail1("mfino.alert.pending_transaction.mail_1", "support@mfino.com"),
		PendingTransactionLimitMail2("mfino.alert.pending_transaction.mail_2", "support@mfino.com"),
		PendingTransactionLimitMail3("mfino.alert.pending_transaction.mail_3", "support@mfino.com"),
		PendingTransactionLimitSMS1("mfino.alert.pending_transaction.SMS_1", "6201"),
		PendingTransactionLimitSMS2("mfino.alert.pending_transaction.SMS_2", "6202"),
		PendingTransactionLimitSMS3("mfino.alert.pending_transaction.SMS_3", "6203"),
		BackendUsername("mfino.multix.username", "username"),
		BackendPassword("mfino.multix.password", "password"),
		BulkRemittanceImportFtpServer("mfino.bulk_remittance.import.ftp_server_url", "ftp://localhost:21/data"),
		BulkRemittanceImportFtpUsername("mfino.bulk_remittance.import.ftp_username", "mfino"),
		BulkRemittanceImportFtpPassword("mfino.bulk_remittance.import.ftp_password", "mfino"),
		BulkRemittanceImportFtpDoLogin("mfino.bulk_remittance.import.ftp_dologin", "true"),
		BulkRemittanceImportBatchSize("mfino.bulk_remittance.import.batch_size", "10"),
		BulkRemittanceExportFtpServer("mfino.bulk_remittance.export.ftp_server_url", "ftp://localhost:21/data"),
		BulkRemittanceExportFtpUsername("mfino.bulk_remittance.export.ftp_username", "mfino"),
		BulkRemittanceExportFtpPassword("mfino.bulk_remittance.export.ftp_password", "mfino"),
		BulkRemittanceExportFtpDoLogin("mfino.bulk_remittance.export.ftp_dologin", "true"),
		BulkRemittanceExportBatchSize("mfino.bulk_remittance.export.batch_size", "10"),
		GroupIDImportFtpServer("mfino.groupid.ftp_server_url", "ftp://localhost:21/data"),
		GroupIDImportFtpUsername("mfino.groupid.ftp_username", "mfino"),
		GroupIDImportFtpPassword("mfino.groupid.ftp_password", "mfino"),
		TempDir("temp.dir", null),
		SocketTimeout("mfino.backend.socket.timeout", "300000"),
		Version("mfino.version", "0.1"),
		LocalTimeZone("mfino.local_timezone", "Asia/Jakarta"),
		LOPExpirationInDays("mfino.lop.expiry.days", "1"),
		HibernateMFinoDBConfigFile("mfino.hibernate.mfino.config", null),
		HibernateMFinoReportDBConfigFile("mfino.hibernate.mfino.report.config", null),
		HibernateCBOSSSyncDBConfigFile("mfino.hibernate.cboss.sync", "cbosssync.cfg.xml"),
		ExcelDownloadRowLimit("mfino.download.row_limit", "1000"),
		Log4jConfigFile("mfino.log4j.config", null),
		DefaultLanguage("mfino.default.language", "0"),
		SystemMDN("mfino.mdn.system", "6288191"),
		LOPDistributorMDN("mfino.mdn.lop_distributor", "6288192"),
		SVACollectorMDN("mfino.mdn.sva_collector", "6288193"),
		PostpaidSourceMDN("mfino.mdn.postpaid_source", "628811"),
		PrepaidSourceMDN("mfino.mdn.prepaid_source", "628812"),
		EMailAttachmentSizeLimit("mfino.email.attachment_size_limit", "1048576"), //1M
		CBOSSSyncBatchSize("mfino.cboss_sync.batch_size", "100"),
		DefaultPocketTemplateCBOSSPrepaid("mfino.default.pocket_template.cboss_prepaid", "1"),
		DefaultPocketTemplateCBOSSPostpaid("mfino.default.pocket_template.cboss_postpaid", "2"),
		DefaultPocketTemplateSVAAirTime("mfino.default.pocket_template.sva_airtime", "3"),
		DefaultPocketTemplateSVAMoney("mfino.default.pocket_template.sva_money", "4"),
		DefaultPocketTemplateCreditCard("mfino.default.pocket_template.credit_card", "6"),
		SubscriberReportBatchSize("mfino.report.subscriber_report.batch_size", "25000"),
		MerchantReportBatchSize("mfino.report.merchant_report.batch_size", "5000"),
		MerchantReportTxnBatchSize("mfino.report.merchant_report.txn_batch_size", "50000"),
		OpenAPIReportBatchSize("mfino.report.openapi_report.batch_size", "100000"),
		ActivityReportBatchSize("mfino.report.activity_report.batch_size", "50000"),
		DistributionTreeReportLevels("mfino.report.distribution_tree_report.levels", "5"),
		SMSGatewayURL("mfino.sms.gateway.url", "http://localhost:6013/cgi-bin/sendsms"),
		SMSGatewayUser("mfino.sms.gateway.username", "Smart"),
		SMSGatewayPassword("mfino.sms.gateway.password", "Smart2010"),
		DLRHostURL("mfino.dlr.report.url", "http://localhost:8080/SMSProxy/DLRServlet"),
		SMSAlertsGatewayURL("mfino.smsalerts.gateway.url", "http://localhost:6013/cgi-bin/sendsms"),
		SMSAlertsGatewayUser("mfino.smsalerts.gateway.username", "Smart"),
		SMSAlertsGatewayPassword("mfino.smsalerts.gateway.password", "Smart2010"),
		SMSAlertsDLRHostURL("mfino.smsalerts.dlr.report.url", "http://localhost:8080/SMSAlerts/DLRServlet"),
		SMSAlertsMesssageLength("mfino.smsalerts.message.length", "160"),
		LogRootPath("mfino.log.root.path", "/var/log/mfino/"),
		MaxMarketingCategory("mfino.marketing_category.max_value", "69"),
		CreditcardMerchantid("mfino.creditcard_payment.merchantid", "000100013000619"),
		//passwords was ycvcz for infinitum and merchant id was smartfren
		CreditcardTransactionPassword("mfino.creditcard_payment.transaction_password", "0kj9h8g76f"),
        CreditcardChainNum("mfino.creditcard_payment.chain_num","NA"),
        CreditcardCurrency("mfino.creditcard_payment.currency","360"),
        CreditcardPurchaseCurrency("mfino.creditcard_payment.purchase_currency","360"),
        CreditcardAcquirerBin("mfino.creditcard_payment.acquirer_bin","123456"),
        CreditcardPassword("mfino.creditcard_payment.password","123456"),
        CreditcardNSIAType("mfino.creditcard_payment.nsia_type","IMMEDIATE"),
		CreditcardTransactionMALLID("mfino.creditcard_payment.MALLID", "221"),
		CreditcardGatewayName("mfino.creditcard_payment.gateway_Name", "NSIAPAY"),
        CreditcardPaymentMethod("mfino.creditcard_payment.payment_method","1"),
        CreditcardCurrencyCode("mfino.creditcard_payment.currency_code","IDR"),
        CreditcardReturnURL("mfino.creditcard_payment.return_URL","http://localhost:8080/CreditCardPayment/"),        
		//https://dvlp.infinitium.com:443/payment/PaymentWindow.jsp
		CreditcardGatewayURL("mfino.creditcard_payment.gateway_URL", "http://luna.nsiapay.com/ipg_payment/RegisterOrderInfo"),
		CreditcardRegistrationExpirationTimeInHrs("mfino.creditcard_payment.registration_expiration_time_in_hrs", "24"),
		DefaultEmailSubject("mfino.mail.default_subject", "mFino System Notification"),
		CustomerServiceShortCode("mfino.notification.var.CustomerServiceShortCode", "881"),
		MaxPINLength("mfino.max.pin.length","6"),
		MinPINLength("mfino.min.pin.length","6"),
		useHSM("mfino.use.hsm","false"),
		useOptimizedSLC("mfino.use.optimized.slc","false"),
		useHashedPIN("mfino.use.hashed.pin","false"),
		useRSA("mfino.use.rsa","false"),
		H2HSmartMerchantParentUsername("mfino.h2hmerchant.smart.parent.username", "top_d1"),        
		H2HMobile8MerchantParentUsername("mfino.h2hmerchant.mobile8.parent.username", "merchant"),
		RegisterCCSubscriberSubject("mfino.subscriber.register.email.subject", "mWallet System Advisories"),
		ForgotPasswordCCSubscriberCodeBody("mfino.CCSubscriber.forgotpassword.body","To reset your password, " +
		"Dear Customer, please kindly click $(confirmationURL)?ForgotPasswordconfirmationCode=$(ForgotPasswordconfirmationCode)&MDN=$(userName) Or go to $(confirmationURL) and input $(ForgotPasswordconfirmationCode) on the confirmation code in the form for resetting the password."),
		ForgotPasswordCCSubscriberSubject("mfino.CCSubscriber.forgotpassword.subject","mWallet Forgot Pin Request"),
		CreditcardMaximumAmountLimit("mfino.creditcard_payment.maximum_amount_limit", "70000"),
		CreditcardMaximumAmountAlertMessage("mfino.creditcard_payment.maximum_amount_alert", "Your request for topup transaction with the amount $(Amount) is kept pending for verification. Your topup will be done post verfication Refs#{CCTransactionID}"),

		RegisterCCSubscriberStandardBody(
				"mfino.register_subscriber.email.standard_body",
				"Yang terhormat Bapak atau Ibu,  \n\n" +
				"Dear Customer, \nAnda menerima email ini secara otomatis karena untuk memvalidasi " +
				"email address yang digunakan untuk registrasi user baru pada SMARTFREN Website. \n\n" +
				"This email is sent automatically to validate email address use for new user id registration " +
				"at our SMARTFREN Website. \n\n" +
				"Jika Anda bukan pemilik email ini, maka silahkan kirim balik email ini dengan mem-forward " +
				"email ini ke customer service kami ke registration.service@smart-telecom.co.id karena " +
				"seseorang mungkin salah mendaftarkan email anda. \n\n" +
				"If you are not the owner of this email address then please disregard this email " +
				"or forward this email to our customer service at registration.service@smart-telecom.co.id  " +
		"as someone has possibly wrongly registered with your email address.\n\n"),
		RegisterCCSubscriberCodeBody(
				"mfino.register_subscriber.email.code_body",
				"Untuk mengkonfimasikan email address, silakan klik " +
				"$(autoConfirmationURL)?confirmationCode=$(confirmationCode)&username=$(userName), " +
				"ketik $(confirmationURL) (halaman konfirmasi) dan input " +
				"$(confirmationCode) (scramble code) pada kode konfirmasi untuk mengkonfirmasikan email adress anda. \n\n" +
				"To confirm your email address, please kindly click $(autoConfirmationURL)?confirmationCode=$(confirmationCode)&username=$(userName). Or go to " +
				"$(confirmationURL) and input $(confirmationCode) on the confirmation code in the " +
		"form to confirm your email address.\n\n"),
		RegisterCCSubscriberAdditionalMsg(
				"mfino.register_subscriber.email.additional_msg",
				"Setelah konfirmasi selesai, petugas kami akan menghubungi anda via telephone dan " +
				"mengkonfirmasikan pendaftaran anda.\n \n" +
				"Once you have completed the above confirmation required, our representative will " +
		"contact you via phone and confirm your registration accordingly.\n\n"),
		RegisterCCSubscriberSignature("mfino.register_subscriber.email.signature", "Best regards,\nSMARTFREN"),
		RegisterCCSubscriberDisclaimer("mfino.register_subscriber.disclaimer", 
				"Pengguna dengan ini mengakui  bahwa semua informasi yang diperlukan dan diberikan adalah " +
				"valid dan memberikan izin kepada SMARTFREN untuk melakukan verifikasi suara dengan melakukan " +
				"panggilan telepon ke pelanggan.<p> Pengguna dengan ini setuju dan memberikan izin kepada " +
				"SMARTFREN dengan hak penuh untuk menonaktifkan / menolak / membatalkan pendaftaran user id, " +
				"dan untuk menolak / membatalkan pemesanan atau transaksi yang dianggap mencurigakan tanpa " +
				"pemberitahuan terlebih dahulu.<p>Semua modifikasi data secara ketat dimonitor dan harus " +
				"mengikuti Kebijakan Data Modifikasi. Setiap perubahan akan dikonfirmasikan melalui email " +
				"agar perubahan berlaku.<p>Setelah pendaftaran data selesai, sebuah email akan dikirim ke " +
				"alamat pengguna email sebagai konfirmasi dan Anda diminta untuk mengkonfirmasi dalam " +
				"waktu $(expirationTime) jam atau data pendaftaran akan dibatalkan.<p>" +
				"<hr> <b>English version</b>:<br> User hereby acknowledge that all required and given " +
				"information valid and granted Permission to SMARTFREN  to conduct voice verification by " +
				"performing phone call to customer. <p>User hereby agree and granted permission for " +
				"SMARTFREN full right to deactivate / disapprove / cancel user id registration, and to " +
				"reject / cancel any booking or transactions that is considered as suspicious without " +
				"prior notification.<p> All registration data modification is strictly monitored and " +
				"must follow Data Modification Policy. Any changes will be confirmed by email for the " +
				"changes to take effect.<p> After registration data completed, an email will be sent to " +
				"user email address as confirmation and you are required to confirm within $(expirationTime)hours or " +
		"registration data will be canceled."),
		AllowInterCompanyCCPayment("mfino.cc.payment.allow_inter_company", "false"),
		CCPaymentNotificationBody("mfino.creditcard_payment.notification", "Terimakasih Anda sudah menggunakan fasitas pembelian melalui SMARTFREN\nBerikut ini adalah informasi transaksi yang telah Anda lakukan\n\tTanggal	 : 	$(Date)\n\tJam	 : 	$(Time)\n\tJenis Transaksi	 : 	$(TransactionType)\n\tNomor Tujuan	 : 	$(Destination)\n\tNominal	 : 	$(Amount)\n\tBerita	 : 	$(Description)\n\tCreditCardTransactionId:	$(CreditCardTransactionID)\n\tStatus	 : 	$(Status)\n\tSemoga informasi ini bermanfaat bagi Anda.\n\tTerima kasih.\nHormat Kami,\nSMARTFREN\n-----------------------------------------------------------------------------------------------------------\nThank you for purchase at SMARTFREN\nWe would like to inform you transactions you have performed:\n\tDate	 : 	$(Date)\n\tTime	 : 	$(Time)\n\tTransaction Type	 : 	$(TransactionType)\n\tMDN	 : 	$(Destination)\n\tAmount	 : 	$(Amount)\n\tRemark	 : 	$(Description)\n\tCreditCardTransactionId:	$(CreditCardTransactionID)\n\tStatus	 : 	$(Status)\n\tWe hope this information is useful for you.\n\tThank you.\nBest regards,\nSMARTFREN"),
		CCPaymentNotificationSubject("mfino.creditcard_payment.email.subject", "mWallet System Advisories"),
		CCPaymentPopupNotificationBahasa("mfino.cc.payment.popup_notification_bahasa", 
				"Pelanggan (Anda) setuju untuk mengikuti semua prosedur dan memahami bahwa semua transaksi yang " +
				"dilakukan akan ditinjau oleh tim review kami untuk menjamin transaksi yang dilakukan dengan kartu asli. " +
				"Harap dicatat bahwa konfirmasi pembelian akan dikirimkan kepada anda lewat email (alamat email yang " +
		"dimasukkan pada proses pendaftaran)."),
		CCPaymentDeploymentURL("mfino.creditcard_payment.deployment_url","http://122.183.18.94:8080/CreditCardPayment"),
		CCPaymentPopupNotificationEnglish("mfino.cc.payment.popup_notification_english", 
				"Customer (You) agree to follow all procedures and understand that all transactions made will " +
				"be subject to review by our fraud review team to ensure the transaction is made with a genuine card. " +
				"Please note that purchase confirmation will be sent to you via email " +
		"(email address entered at the time of registration)."),
		CCDestinationLimit("mfino.creditcard_payment.destinationlimit","5"),
		CCTransactionCompletionMessage("mfino.creditcard_payment.transactioncompletion.message","Your request for amount ${Amount} is under process. It will be processed after verification Refs#{CCTransactionID}."),
		BPRKSRoutingCode("mfino.report.BPRKS_Routing_Code","152"), 
		UpdateCCSubscriberCodeBody(
				"mfino.update_subscriber.email.code_body",
				"Untuk mengkonfimasikan email address, silakan klik " +
				"$(autoConfirmationURL)?confirmationCode=$(confirmationCode)&username=$(userName), " +
				"ketik $(confirmationURL) (halaman konfirmasi) dan input " +
				"$(confirmationCode) (scramble code) pada kode konfirmasi untuk mengkonfirmasikan email adress anda. \n\n" +
				"To confirm your profile updates, please kindly click $(autoConfirmationURL)?confirmationCode=$(confirmationCode)&username=$(userName). Or go to " +
				"$(confirmationURL) and input $(confirmationCode) on the confirmation code in the " +
		"form to confirm your profile updates.\n\n"),
		UpdateCCSubscriberStandardBody(
				"mfino.update_subscriber.email.standard_body",
				"Yang terhormat Bapak atau Ibu,  \n\n" +
				"Dear Customer, \nAnda menerima email ini secara otomatis karena untuk memvalidasi " +
				"email address yang digunakan untuk registrasi user baru pada SMARTFREN Website. \n\n" +
				"This email is sent automatically to confirm your profile updates  " +
				"at our SMARTFREN Website. \n\n" +
				"Jika Anda bukan pemilik email ini, maka silahkan kirim balik email ini dengan mem-forward " +
				"email ini ke customer service kami ke registration.service@smart-telecom.co.id karena " +
				"seseorang mungkin salah mendaftarkan email anda. \n\n" +
				"If you are not the owner of this email address then please disregard this email " +
				"or forward this email to our customer service at registration.service@smart-telecom.co.id  " +
		"as someone has possibly wrongly registered with your email address.\n\n"), 
		CCCodeNotificationSMSC("mfino.creditcard_code_notification_smsc","807"), 
		CCCodeNotificationMsg("mfino.creditcard_code_notificationmsg","Your Scramble code for CreditCardRegistration is $(scramblecode)"),
		NSIARemoteAddress("mfino.nsiaremote_address","114.110.20.14"),
		NSIARemoteIPCheck("mfino.nsiaremoteip_check","false"),
		CreditcardUpdateExpirationTimeInHrs("mfino.creditcard_payment.updateprofile_expiration_time_in_hrs", "24"),
		CCCodeNotificationSource("mfino.creditcard_code_notification_source","817"), 
		CCCodeNotificationMsgForEditEmail("mfino.creditcard_code_notificationmsg_editemail","Your Scramble code for Edit CreditCard email is $(scramblecode)"), 
		CCTopUpDenominations("mfino.creditcard_topup_denominations","50000,100000,200000"),
		CCTopUpDataDenominations("mfino.creditcard_topupdata_denominations","60000,600000,600000"),
		SMARTEMoneyPartnerCode("mfino.smart.partnercode", "9999"),
		CurrencyCodes("mfino.currencycodes","360,840"),
		CurrencyNames("mfino.currencyNames","IDR,USD"),
		PartnerRegistrationMail(
				"mfino.partner.registration.email",
				"Dear $(tradename) \n\n" +
				"You have been registered as a $(partnerType) Partner.\n"+
				"Here are the login details:\n"+
				"username: $(username)\n"+
				"password: $(password)  "),
		EmailVerificationSubject("mfino.email.verification.subject","Email Verification"),
		EmailVerificationMessage(
						"mfino.email.verification.message","To confirm email, click link $(AppURL)/emailVerification.htm?subscriberID=$(subscriberID)&email=$(email)"),
		PartnerBrandPrefix("mfino.partner.brandprefix","1"), 
		BulkUploadSubscriberKYClevel("mfino.bulkupload.subscriber.kyclevel","3"),
		BulkUploadSubscriberNotificationMethod("mfino.bulkupload.subscriber.notification.method", "4"),
		BulkUploadSubscriberEmailSubject("mfino.bulkupload.subscriber.email.subject", "OneTimePassword"),
		BulkUploadSubscriberSendEmail("mfino.bulkupload.subscriber.send.email", "true"),
		BulkUploadDefaultCurrency("mfino.bulkupload.default.currency","NGN"),
		BulkUploadDefaultTimeZone("mfino.bulkupload.default.timezone", "WAT"),
		IntialKYClevel("mfino.intial.kyclevel","1"), 
		CountryCode("mfino.countrycode","234"), 
		OTPExpirationTime("mfino.otpexpirationtime","24"),
		OTPMailSubject("mfino.otpmail.subject","OTP mail"),
		AuthenticationKeyMailSubject("mfino.authenticationkey.mail.subject","Authentication key"),
		olapURL("mfino.olap.url","http://localhost:8080/saiku"),
		ReportSchedulerURL("mfino.report.url","http://localhost:8084/ReportScheduler"), 
		ReportDir("mfino.report.directory","D:\\Report"), 
		OpenOfficePort("mfino.openoffice.port","8100"),
		BulkTransferApproverSubject("mfino.bulktransfer.approver.notify.subject","Bulk Transfer Request"),
		BulkTranferApproverMessage("mfino.bulktransfer.approver.notify.message","Bulk Transfer Request for approval"),
		ConcurrentLogins("mfino.allow.user.concurrent.logins","true"),
		ReportProductName("report.productname","eMoney"),
		SendOTPOnIntialized("mfino.otp.onintialized","true"),
		IsEMoneyPocketRequired("mfino.emoney.pocket.required","true"),
		IncludePartnerInSLC("mfino.include.partner.slc","false"),
		MfinoNettingLedgerEntries("mfino.netting.ledger.entries","true"),
		TransactionDateTimeFormat("mfino.transaction.datetime.format","dd/MM/yy HH:mm"),
		RequiresNameWhenMoneyTransferedToUnregisterd("mfino.requires.name.when.money.transfer.to.unregistered","true"),
		IsReactivationRequired("mfino.reactivation.required","false"),
		KYCUpgradeFTPServer("mfino.kycupgrade.ftp.server","127.0.0.1"),
		KYCUpgradeFTPPort("mfino.kycupgrade.ftp.port","21"),
		KYCUpgradeFTPUser("mfino.kycupgrade.ftp.user","user"),
		KYCUpgradeFTPPassword("mfino.kycupgrade.ftp.password","password"),
		KYCUpgradeFTPDownloadFilePath("mfino.kycupgrade.ftp.download.filepath","/upgrade/upgrade.csv"),
		KYCUpgradeNotifyEmail("mfino.kycupgrade.notify.email","test@test.com"),
		KYCUpgradeEmailSubject("mfino.kycupgrade.email.subject","kyc upgrade email"),
		KYCUpgradeEmailMessage("mfino.kycupgrade.email.message", "Successfully processed the kyc upgrade request"),
		CurrencyFormatLocale("mfino.currency.format.locale", "us"),
		UseRealHSM("mfino.use.real.hsm","false"),
		RequiresSuccessfullTransactionsInEmoneyHistory("mfino.requires.successful.transactions.in.emoney.history","true"),
		PdfHistoryFooter("mfino.pdf.history.footer","\u00a9 2013 PT Smartfren Telecom. All Rights reserved"),
		EmailPdfHistorySubject("mfino.email.pdf.history.subject","Smartfren Uangku Electronic Statement"),
		EmailPdfHistoryBody("mfino.email.pdf.history.body","Thank you for using Uangku E-Statements Services. Please find your requested Uangku Transaction History for your selected time period.   Enter your Uangku PIN to view the document."),
		pdfHistoryDateFormat("mfino.transaction.history.pdf.datetime.format","dd/MM/yyyy"),
		ReportFooter("mfino.report.footer","\u00a9 2013 PT Smartfren Telecom. All Rights reserved"),
		PromoImagePath("promo.image.path","images/promoImage.png"),
		SendOTPBeforeApproval("mfino.send.otp.before.approval", "true"),
		DateFormatInReportFileNames("dateFormatInReportFileNames","yyyyMMdd"),
		KTPServerURL("mfino.ktpserver.base.url","null"),
		KTPServerTimeout("mfino.ktpserver.server.timeout","10000"),
		CapitalizationAuthorizedEmail("mfino.capitalization.authorized.email","abc@abc.com"),
		MailSesTransport("mfino.mail.transport.protocol", "aws"),
		MailAwsUser("mfino.mail.aws.user", "AKIAIZW252ECC7BIJ5QQ"),
		MailAwsSecret("mfino.mail.aws.password", "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"),
		MailAwsFrom("mfino.mail.aws.from", "noreply@dimo.co.id"),
		MailUseSmtp("mfino.mail.use.smtp", "true"),
		SubscriberProfileImageFilePath("mfino.subscriber.profile.image.filepath","images/profile"),
		KTPImagePath("ktp.image.path", "images/id/");
		
		private final String key;
		private final String defaultValue;

		public String getKey() {
			return key;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		ConfigurationKey(String key, String defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}
	}

	static {
		try {
			boolean foundFile = false;
			int codeSourceFilePrefixlength = 6;

			System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
			
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				isWindows = true;
			} else if (osName.startsWith("Linux") || osName.startsWith("Mac")) {
				isUnixLike = true;
				codeSourceFilePrefixlength = 5;
			}

			if (!foundFile) {
				Context ctx = new InitialContext();
				String warType =  new String();
				log.info("searching wartype:");
				try{
				warType = (String)ctx.lookup("java:comp/env/mfino.Properties");
				log.info("wartype="+warType);
				}catch(Exception e){
					log.warn("jndi env not found.getting path for mfino.properties from the mfino_conf folder for servicemix" );
				}
				
				if(warType!=null && !(warType.equals(""))){
					log.info("Getting path for mfino.properties from jndi env.");
				//get path from the jndi environment variable set in tomcat context.xml
				String[] path = ((String) ctx.lookup( "java:comp/env/mfino."+warType+".mfino.Properties" )).split("file:");
				log.info("path" + path[1]);
 			    _configFile = new File(path[1]);
				}else{
					String path=ConfigurationUtil.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(codeSourceFilePrefixlength);
					log.info("path" + path);
					//same level as the full jar location 
					File dirFile = new File(path);
					// same level as deploy folder of servicemix
					log.info("directory parent="+dirFile.getParent());
					//entering the mfino_conf folder
					String confDir = dirFile.getParentFile().getParent()+"/mfino_conf";
						_configFile = new File(confDir, _propertyFileName);
						log.info("Try getting property file " + _configFile);

				}
				log.info("Config file exist="+_configFile.exists()+"Config null check="+(_configFile==null));
				if (_configFile != null && _configFile.exists()) {
					log.info("config file found and not null");
						FileInputStream fis = new FileInputStream(_configFile);
						_configurationProperties.load(fis);
						fis.close();
						foundFile = true;
				}
			}

			if (!foundFile) {
				String userHome = System.getProperty("user.home");
				_configFile =
					new File(new File(userHome, _propertyDir), _propertyFileName);

				log.info("Try getting property file " + _configFile);
				if (_configFile != null && _configFile.exists()) {
					FileInputStream fis = new FileInputStream(_configFile);
					_configurationProperties.load(fis);
					fis.close();
					foundFile = true;
				}
			}

			if (!foundFile) {
				log.error("Did not find configuration file");
			} else {
				log.info(_configurationProperties.toString());
			}

		} catch (Exception ex) {
			log.error("Failed to load configuration file.\n", ex);
		}
	}

	private static String get(String key) {
		return _configurationProperties.getProperty(key);
	}

	private static String get(String key, String defaultValue) {
		String value = get(key);
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		} else {
			return value;
		}
	}

	private static String getConfigFilePath() {
		return _configFile.getParentFile().getAbsolutePath();
	}

	private static Logger getLog(){
		if (log == null)
			log = LoggerFactory.getLogger(ConfigurationUtil.class);
		return log;
	}
	public static String getMailServer() {
		return get(ConfigurationKey.MailServer);
	}
	public static String getForgotPasswordCCSubscriberCodeBody() {
		return get(ConfigurationKey.ForgotPasswordCCSubscriberCodeBody);
	}
	public static String getForgotPasswordCCSubscriberSubject() {
		return get(ConfigurationKey.ForgotPasswordCCSubscriberSubject);
	}

	public static String getMailServerAuthName() {
		return get(ConfigurationKey.MailServerAuthName);
	}

	public static String getMailServerAuthPassword() {
		return get(ConfigurationKey.MailServerAuthPassword);
	}

	public static String getH2HSmartMerchantParentUsername() {
		return get(ConfigurationKey.H2HSmartMerchantParentUsername);
	}

	public static String getH2HMobile8MerchantParentUsername() {
		return get(ConfigurationKey.H2HMobile8MerchantParentUsername);
	}

	private static String get(ConfigurationKey key) {
		return get(key.getKey(), key.getDefaultValue());
	}

	private static int getInteger(ConfigurationKey key) {
		String value = get(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			getLog().error("Invalid value for configuration " + key.getKey() + ". Using default value.", ex);
			return Integer.parseInt(key.getDefaultValue());
		}
	}
	
	private static boolean getBoolean(ConfigurationKey key) {
		String value = get(key);
		try {
			return Boolean.parseBoolean(value);
		} catch (NumberFormatException ex) {
			getLog().error("Invalid value for configuration " + key.getKey() + ". Using default value.", ex);
			return Boolean.parseBoolean(key.getDefaultValue());
		}
	}
	private static long getLong(ConfigurationKey key) {
		String value = get(key);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException ex) {
			getLog().error("Invalid value for configuration " + key.getKey() + ". Using default value.", ex);
			return Long.parseLong(key.getDefaultValue());
		}
	}

	public static int getMailServerPort() {
		String value = get(ConfigurationKey.MailServerPort);
		return Integer.parseInt(value);
	}

	public static String getPendingTransactionThreshold1() {
		return get(ConfigurationKey.PendingTransactionThreshold1);
	}

	public static String getPendingTransactionThreshold2() {
		return get(ConfigurationKey.PendingTransactionThreshold2);
	}

	public static String getPendingTransactionThreshold3() {
		return get(ConfigurationKey.PendingTransactionThreshold3);
	}

	public static String getPendingTransactionLimitMail1() {
		return get(ConfigurationKey.PendingTransactionLimitMail1);
	}

	public static String getPendingTransactionLimitMail2() {
		return get(ConfigurationKey.PendingTransactionLimitMail2);
	}

	public static String getPendingTransactionLimitMail3() {
		return get(ConfigurationKey.PendingTransactionLimitMail3);
	}
	public static String getPendingTransactionLimitSMS1() {
		return get(ConfigurationKey.PendingTransactionLimitSMS1);
	}
	public static String getPendingTransactionLimitSMS2() {
		return get(ConfigurationKey.PendingTransactionLimitSMS2);
	}
	public static String getPendingTransactionLimitSMS3() {
		return get(ConfigurationKey.PendingTransactionLimitSMS3);
	}
	public static String getPendingTransactionTimePeriod1() {
		return get(ConfigurationKey.PendingTransactionTimePeriod1);
	}

	public static String getPendingTransactionTimePeriod2() {
		return get(ConfigurationKey.PendingTransactionTimePeriod2);
	}

	public static String getCreditcardMaximumAmountLimit() {
		return get(ConfigurationKey.CreditcardMaximumAmountLimit);
	}
	public static String getCreditcardMaximumAmountAlertMessage() {
		return get(ConfigurationKey.CreditcardMaximumAmountAlertMessage);
	}
	public static String getPendingTransactionTimePeriod3() {
		return get(ConfigurationKey.PendingTransactionTimePeriod3);
	}

	public static String getMailServerFromName() {
		return get(ConfigurationKey.MailServerFromName);
	}

	public static int getExcelRowLimit() {
		String limit = get(ConfigurationKey.ExcelDownloadRowLimit);
		return Integer.parseInt(limit);
	}

	public static int getDistributionTreeReportLevels() {
		String levels = get(ConfigurationKey.DistributionTreeReportLevels);
		return Integer.parseInt(levels);
	}

	public static boolean getMailServerRequireAuth() {
		String value = get(ConfigurationKey.MailServerRequireAuth);
		return Boolean.parseBoolean(value);
	}

	public static boolean getMailServerRequireSSL() {
		String value = get(ConfigurationKey.MailServerRequireSSL);
		return Boolean.parseBoolean(value);
	}

	public static String getI18NMessagePath() {
		return get(ConfigurationKey.I18NMessagePath);
	}

	public static String getMerchantDefaultPocketTemplateID() {
		return get(ConfigurationKey.MerchantDefaultPocketTemplateID);
	}
	public static Long getDefaultPocketTemplateCreditCard() {
		return getLong(ConfigurationKey.DefaultPocketTemplateCreditCard);
	}

	public static String getPINLength() {
		return get(ConfigurationKey.PINLength);
	}

	public static String getBackendURL() {
		return get(ConfigurationKey.BackendURL);
	}

	public static String getUploadFileSizeLimit() {
		return get(ConfigurationKey.UploadFileSizeLimit);
	}

	public static String getKTPImagePath(){
		return get(ConfigurationKey.KTPImagePath);
	}
	
	public static String getBackendUsername() {
		return get(ConfigurationKey.BackendUsername);
	}

	public static String getBackendPassword() {
		return get(ConfigurationKey.BackendPassword);
	}

	public static String getCustomerServiceShortCode() {
		return get(ConfigurationKey.CustomerServiceShortCode);
	}

	public static String getBulkRemittanceImportFtpServer() {
		return get(ConfigurationKey.BulkRemittanceImportFtpServer);
	}

	public static String getBulkRemittanceImportFtpUsername() {
		return get(ConfigurationKey.BulkRemittanceImportFtpUsername);
	}

	public static String getBulkRemittanceImportFtpPassword() {
		return get(ConfigurationKey.BulkRemittanceImportFtpPassword);
	}

	public static boolean getBulkRemittanceImportFtpDoLogin() {
		return Boolean.parseBoolean(get(ConfigurationKey.BulkRemittanceImportFtpDoLogin));
	}

	public static int getBulkRemittanceImportBatchSize() {
		return Integer.parseInt(get(ConfigurationKey.BulkRemittanceImportBatchSize));
	}

	public static String getBulkRemittanceExportFtpServer() {
		return get(ConfigurationKey.BulkRemittanceExportFtpServer);
	}

	public static String getBulkRemittanceExportFtpUsername() {
		return get(ConfigurationKey.BulkRemittanceExportFtpUsername);
	}

	public static String getBulkRemittanceExportFtpPassword() {
		return get(ConfigurationKey.BulkRemittanceExportFtpPassword);
	}

	public static boolean getBulkRemittanceExportFtpDoLogin() {
		return Boolean.parseBoolean(get(ConfigurationKey.BulkRemittanceExportFtpDoLogin));
	}

	public static int getBulkRemittanceExportBatchSize() {
		return Integer.parseInt(get(ConfigurationKey.BulkRemittanceExportBatchSize));
	}
	public static String getBulkUploadDefaultCurrency() {
		return get(ConfigurationKey.BulkUploadDefaultCurrency);
	}
	
	public static String getBulkUploadDefaultTimeZone() {
		return get(ConfigurationKey.BulkUploadDefaultTimeZone);
	}
	public static String getGroupIDImportFtpServer() {
		return get(ConfigurationKey.GroupIDImportFtpServer);
	}

	public static String getGroupIDImportFtpUsername() {
		return get(ConfigurationKey.GroupIDImportFtpUsername);
	}

	public static String getGroupIDImportFtpPassword() {
		return get(ConfigurationKey.GroupIDImportFtpPassword);
	}

	public static String getTempDir() {
		String tempDir = get(ConfigurationKey.TempDir.getKey());
		if (tempDir == null || tempDir.length() <= 0) {
			// if no temp dir is set, use the current application directory
			return System.getProperty("java.io.tmpdir");
		} else {
			return tempDir;
		}
	}
	
	public static String getReportDir() {
		String reportDir = get(ConfigurationKey.ReportDir.getKey());
		if (reportDir == null || reportDir.length() <= 0) {
			return getTempDir();
		} else {
			 File reportDirectory = new File(reportDir);
			 if(!reportDirectory.exists()||!reportDirectory.isDirectory()){
				 reportDirectory.mkdirs();
			 }
			return reportDir;
		}
	}

	public static int getSocketTimeout() {
		return getInteger(ConfigurationKey.SocketTimeout);
	}

	public static String getVersion() {
		return get(ConfigurationKey.Version);
	}

	public static TimeZone getLocalTimeZone() {
		String timezoneID = get(ConfigurationKey.LocalTimeZone);
		return TimeZone.getTimeZone(timezoneID);
	}

//	public static TimeZone getServerTimeZone() {
//		TimeZone timeZone = TimeZone.getTimeZone(SystemParametersUtil.getString(SystemParameterKeys.TIME_ZONE));
//		if(timeZone == null)
//		{
//			String timezoneID = get(ConfigurationKey.LocalTimeZone);
//			return TimeZone.getTimeZone(timezoneID);
//		}
//		return timeZone;
//	}
	
	public static TimeZone getTimeZone(SessionFactory sessionFactory, HibernateSessionHolder hibernateSessionHolder) {
		String timezoneID = get(ConfigurationKey.LocalTimeZone);
		return TimeZone.getTimeZone(timezoneID);
	}
	
	
	public static int getLOPExpirationInDays() {
		return getInteger(ConfigurationKey.LOPExpirationInDays);
	}

	public static String getHibernateMFinoDBConfigFile() {
		return MfinoUtil.translateToAbsolutePath(
				get(ConfigurationKey.HibernateMFinoDBConfigFile), getConfigFilePath());
	}

	public static String getHibernateMFinoReportDBConfigFile() {
		return MfinoUtil.translateToAbsolutePath(
				get(ConfigurationKey.HibernateMFinoReportDBConfigFile),
				getConfigFilePath());
	}

	public static String getHibernateCBOSSSyncDBConfigFile() {
		return MfinoUtil.translateToAbsolutePath(
				get(ConfigurationKey.HibernateCBOSSSyncDBConfigFile),
				getConfigFilePath());
	}

	public static String getLog4jConfigFile() {
		return MfinoUtil.translateToAbsolutePath(
				get(ConfigurationKey.Log4jConfigFile), getConfigFilePath());
	}

	public static String getDataPushUserName() {
		return get(ConfigurationKey.DataPushUserName);
	}

	public static String getDataPushPassword() {
		return get(ConfigurationKey.DataPushPassword);
	}

	public static String getAppURL() {
		return get(ConfigurationKey.ApplicationURL);
	}

	public static String getMerchantAddSubject() {
		return get(ConfigurationKey.MerchantAddSubject);
	}

	public static String getAdditionalMsg() {
		return get(ConfigurationKey.EmailAdditionalMessage);
	}

	public static String getEmailSignature() {
		return get(ConfigurationKey.EmailSignature);
	}

	public static String getResetPasswordSubject() {
		return get(ConfigurationKey.ResetPasswordSubject);
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isUnixLike() {
		return isUnixLike;
	}

	public static String getUserInsertSubject() {
		return get(ConfigurationKey.AddUserSubject);
	}

	public static String getSystemMDN() {
		return get(ConfigurationKey.SystemMDN);
	}

	public static String getLOPDistributorMDN() {
		return get(ConfigurationKey.LOPDistributorMDN);
	}

	public static String getSVACollectorMDN() {
		return get(ConfigurationKey.SVACollectorMDN);
	}

	public static String getPostpaidSourceMDN() {
		return get(ConfigurationKey.PostpaidSourceMDN);
	}

	public static String getPrepaidSourceMDN() {
		return get(ConfigurationKey.PrepaidSourceMDN);
	}

	public static long getEMailAttachmentSizeLimit() {
		return getLong(ConfigurationKey.EMailAttachmentSizeLimit);
	}

	public static Integer getBatchSize() {
		return getInteger(ConfigurationKey.CBOSSSyncBatchSize);
	}

	public static Long getDefaultPocketTemplateCBOSSPrepaid() {
		return getLong(ConfigurationKey.DefaultPocketTemplateCBOSSPrepaid);
	}

	public static Long getDefaultPocketTemplateCBOSSPostpaid() {
		return getLong(ConfigurationKey.DefaultPocketTemplateCBOSSPostpaid);
	}

	public static Long getDefaultPocketTemplateSVAAirime() {
		return getLong(ConfigurationKey.DefaultPocketTemplateSVAAirTime);
	}

	public static Long getDefaultPocketTemplateSVAMoney() {
		return getLong(ConfigurationKey.DefaultPocketTemplateSVAMoney);
	}

	public static Integer getSubscriberReportBatchSize() {
		return getInteger(ConfigurationKey.SubscriberReportBatchSize);
	}

	public static Integer getMerchantReportBatchSize() {
		return getInteger(ConfigurationKey.MerchantReportBatchSize);
	}

	public static Integer getMerchantReportTxnBatchSize() {
		return getInteger(ConfigurationKey.MerchantReportTxnBatchSize);
	}

	public static Integer getOpenAPIReportBatchSize() {
		return getInteger(ConfigurationKey.OpenAPIReportBatchSize);
	}

	public static Integer getActivityReportBatchSize() {
		return getInteger(ConfigurationKey.ActivityReportBatchSize);
	}

	public static Integer getMaxMarketingCategory() {
		return getInteger(ConfigurationKey.MaxMarketingCategory);
	}

	public static String getDefaultEmailSubject() {
		return get(ConfigurationKey.DefaultEmailSubject);
	}

	public static String getSMSGatewayURL() {
		return get(ConfigurationKey.SMSGatewayURL);
	}

	public static String getSMSGatewayUser() {
		return get(ConfigurationKey.SMSGatewayUser);
	}

	public static String getSMSGatewayPassword() {
		return get(ConfigurationKey.SMSGatewayPassword);
	}

	public static String getDLRHost() {
		return get(ConfigurationKey.DLRHostURL);
	}

	public static String getSMSAlertsGatewayURL() {
		return get(ConfigurationKey.SMSAlertsGatewayURL);
	}

	public static String getSMSAlertsGatewayUser() {
		return get(ConfigurationKey.SMSAlertsGatewayUser);
	}

	public static String getSMSAlertsGatewayPassword() {
		return get(ConfigurationKey.SMSAlertsGatewayPassword);
	}

	public static String getSMSAlertsDLRHost() {
		return get(ConfigurationKey.SMSAlertsDLRHostURL);
	}
	public static String getSMSAlertsMesssageLength() {
		return get(ConfigurationKey.SMSAlertsMesssageLength);
	}

	public static String getLogRootPath() {
		return get(ConfigurationKey.LogRootPath);
	}

	public static Integer getMaxPINLength() {
		return getInteger(ConfigurationKey.MaxPINLength);
	}
	public static Integer getMinPINLength() {
		return getInteger(ConfigurationKey.MinPINLength);
	}          
	public static Boolean getuseHSM() {
		return getBoolean(ConfigurationKey.useHSM);
	}          
	public static Boolean getuseHashedPIN() {
		return getBoolean(ConfigurationKey.useHashedPIN);
	}  
	public static Boolean getuseRSA() {
		return getBoolean(ConfigurationKey.useRSA);
	}  
	public static Boolean getuseOptimizedSLC() {
		return getBoolean(ConfigurationKey.useOptimizedSLC);
	}
	public static String getCreditcardMerchantid() {
		return get(ConfigurationKey.CreditcardMerchantid);
	}

	public static String getCreditcardTransactionPassword() {
		return get(ConfigurationKey.CreditcardTransactionPassword);
	}

	public static String getCreditcardChainNum() {
		return get(ConfigurationKey.CreditcardChainNum);
	}
	
	public static String getCreditcardCurrency() {
		return get(ConfigurationKey.CreditcardCurrency);
	}
	
	public static String getCreditcardPurchaseCurrency() {
		return get(ConfigurationKey.CreditcardPurchaseCurrency);
	}
	
	public static String getCreditcardAcquirerBin() {
		return get(ConfigurationKey.CreditcardAcquirerBin);
	}
	
	public static String getCreditcardPassword() {
		return get(ConfigurationKey.CreditcardPassword);
	}
	
	public static String getCreditcardNSIAType() {
		return get(ConfigurationKey.CreditcardNSIAType);
	}
	
	public static String getCreditcardPaymentMethod() {
		return get(ConfigurationKey.CreditcardPaymentMethod);
	}
	
	public static String getCreditcardCurrencyCode() {
		return get(ConfigurationKey.CreditcardCurrencyCode);
	}
	
	public static String getCreditcardReturnURL() {
		return get(ConfigurationKey.CreditcardReturnURL);
	}
	
	public static String getCreditcardGatewayURL() {
		return get(ConfigurationKey.CreditcardGatewayURL);
	}
	public static String getCreditcardGatewayName() {
		return get(ConfigurationKey.CreditcardGatewayName);
	}
	public static String getCreditcardTransactionMALLID() {
		return get(ConfigurationKey.CreditcardTransactionMALLID);
	}

	
	public static Integer getCreditcardRegistrationExpirationTimeInHrs() {
		return getInteger(ConfigurationKey.CreditcardRegistrationExpirationTimeInHrs);
	}

	public static String getRegisterCCSubscriberSubject() {
		return get(ConfigurationKey.RegisterCCSubscriberSubject);
	}

	public static String getRegisterCCSubscriberCodeBody() {
		return get(ConfigurationKey.RegisterCCSubscriberCodeBody);
	}

	public static String getRegisterCCSubscriberStandardBody() {
		return get(ConfigurationKey.RegisterCCSubscriberStandardBody);
	}

	public static String getUpdateCCSubscriberCodeBody() {
		return get(ConfigurationKey.UpdateCCSubscriberCodeBody);
	}

	public static String getUpdateCCSubscriberStandardBody() {
		return get(ConfigurationKey.UpdateCCSubscriberStandardBody);
	}
	public static String getRegisterCCSubscriberAdditionalMsg() {
		return get(ConfigurationKey.RegisterCCSubscriberAdditionalMsg);
	}

	public static String getRegisterCCSubscriberSignature() {
		return get(ConfigurationKey.RegisterCCSubscriberSignature);
	}

	public static String getRegisterCCSubscriberDisclaimer() {
		return get(ConfigurationKey.RegisterCCSubscriberDisclaimer);
	}

	public static boolean isInterCompanyCCPaymentAllowed(){
		String val = get(ConfigurationKey.AllowInterCompanyCCPayment);
		return Boolean.parseBoolean(val);
	}

	public static String getCCPaymentNotificationBody() {
		return get(ConfigurationKey.CCPaymentNotificationBody);
	}

	public static String getCCPaymentNotificationSubject() {
		return get(ConfigurationKey.CCPaymentNotificationSubject);
	}

	public static String getCCPaymentPopupNotificationBahasa() {
		return get(ConfigurationKey.CCPaymentPopupNotificationBahasa);
	}

	public static String getCCPaymentPopupNotificationEnglish() {
		return get(ConfigurationKey.CCPaymentPopupNotificationEnglish);
	}
	public static String getCCPaymentDeploymentURL(){
		return get(ConfigurationKey.CCPaymentDeploymentURL);
	}
	public static Integer getBPRKSRoutingCode(){
		return getInteger(ConfigurationKey.BPRKSRoutingCode);
	}
	public static Integer getCCDestinationLimit(){
		return getInteger(ConfigurationKey.CCDestinationLimit);
	}
	public static String getCCCodeNotificationSMSC(){
		return get(ConfigurationKey.CCCodeNotificationSMSC);
	}
	public static String getCCCodeNotificationMsg(){
		return get(ConfigurationKey.CCCodeNotificationMsg);
	}

	public static String getNSIARemoteAddress() {
		return get(ConfigurationKey.NSIARemoteAddress);
	}
	public static String getNSIARemoteIPCheck() {
		return get(ConfigurationKey.NSIARemoteIPCheck);
	}

	public static Integer getCreditcardUpdateExpirationTimeInHrs() {
		return getInteger(ConfigurationKey.CreditcardUpdateExpirationTimeInHrs);
	}
	public static String getCCCodeNotificationSource() {
		return get(ConfigurationKey.CCCodeNotificationSource);
	}
	public static String getCCTransactionCompletionMessage() {
		return get(ConfigurationKey.CCTransactionCompletionMessage);
	}

	public static String getCCCodeNotificationMsgForEditEmail() {
		return get(ConfigurationKey.CCCodeNotificationMsgForEditEmail);
	}
	public static String getCCTopUpDenominations() {
		return get(ConfigurationKey.CCTopUpDenominations);
	}
	public static String getCCTopUpDataDenominations() {
		return get(ConfigurationKey.CCTopUpDataDenominations);
	}
	public static Integer getSMARTEMoneyPartnerCode() {
		return getInteger(ConfigurationKey.SMARTEMoneyPartnerCode);
	}
	public static String getCurrencyCodes() {
		return get(ConfigurationKey.CurrencyCodes);
	}
	public static String getCurrencyNames() {
		return get(ConfigurationKey.CurrencyNames);
	}
	public static String getPartnerRegistrationMail() {
		return get(ConfigurationKey.PartnerRegistrationMail);
	}
	public static String getPartnerBrandPrefix() {
		return get(ConfigurationKey.PartnerBrandPrefix);
	}

	public static Long getBulkUploadSubscriberKYClevel() {
		return getLong(ConfigurationKey.BulkUploadSubscriberKYClevel);
	}
	
	public static Integer getBulkUploadSubscriberNotificationMethod() {
		return getInteger(ConfigurationKey.BulkUploadSubscriberNotificationMethod);
	}
	
	public static String getBulkUploadSubscriberEmailSubject() {
		return get(ConfigurationKey.BulkUploadSubscriberEmailSubject);
	}
	
	public static boolean getBulkUploadSubscriberSendEmail() {
		return getBoolean(ConfigurationKey.BulkUploadSubscriberSendEmail);
	}

	public static Long getIntialKyclevel() {
		return getLong(ConfigurationKey.IntialKYClevel);
	}

	public static String getCountryCode() {
		String 	countryCode = get(ConfigurationKey.CountryCode);
		return countryCode;
	}
	public static Integer getOTPExpirationTime() {
		return getInteger(ConfigurationKey.OTPExpirationTime);
	}

	public static String getOTPMailSubsject() {
		return get(ConfigurationKey.OTPMailSubject);
	}
	
	public static String getAuthenticationKeyMailSubject() {
		return get(ConfigurationKey.AuthenticationKeyMailSubject);
	}

	public static String getReportURL() {
		return get(ConfigurationKey.ReportSchedulerURL);
	}
	
	public static Integer getOpenOfficePort() {
		return getInteger(ConfigurationKey.OpenOfficePort);
	}
	
	public static String getBulkTransferApproverSubject() {
		return get(ConfigurationKey.BulkTransferApproverSubject);
	}
	
	public static String getBulkTranferApproverMessage() {
		return get(ConfigurationKey.BulkTranferApproverMessage);
	}
	
	public static String olapUrl() {
		return get(ConfigurationKey.olapURL);
	}
	
	public static boolean getConcurrentLoginsAllowed() {
		return getBoolean(ConfigurationKey.ConcurrentLogins);
	}
	
	public static String getReportProductName() {
		return get(ConfigurationKey.ReportProductName);
	}
	
	public static boolean getSendOTPOnIntialized() {
		return getBoolean(ConfigurationKey.SendOTPOnIntialized);
	}
	
	public static boolean getIsEMoneyPocketRequired() {
		return getBoolean(ConfigurationKey.IsEMoneyPocketRequired);
	}
        
        public static boolean getIncludePartnerInSLC() {
		return getBoolean(ConfigurationKey.IncludePartnerInSLC);
	}
        
    public static boolean getMfinoNettingLedgerEntries() {
    	return getBoolean(ConfigurationKey.MfinoNettingLedgerEntries);
    }
    public static boolean getRequiresNameWhenMoneyTransferedToUnregisterd() {
		return getBoolean(ConfigurationKey.RequiresNameWhenMoneyTransferedToUnregisterd);
	}
	public static String getEmailVerificationSubject() {
		return get(ConfigurationKey.EmailVerificationSubject);
	}
	public static String getEmailVerificationMessage() {
		return get(ConfigurationKey.EmailVerificationMessage);
	}
    public static String getTransactionDateTimeFormat() {
    	return get(ConfigurationKey.TransactionDateTimeFormat);
    }
    public static boolean getIsReactivationRequired() {
		return getBoolean(ConfigurationKey.IsReactivationRequired);
	}
    
	public static String getKYCUpgradeFTPServer() {
		return get(ConfigurationKey.KYCUpgradeFTPServer);
	}

	public static int getKYCUpgradeFTPPort() {
		return getInteger(ConfigurationKey.KYCUpgradeFTPPort);
	}

	public static String getKYCUpgradeFTPUser() {
		return get(ConfigurationKey.KYCUpgradeFTPUser);
	}

	public static String getKYCUpgradeFTPPassword() {
		return get(ConfigurationKey.KYCUpgradeFTPPassword);
	}

	public static String getKYCUpgradeFTPDownloadFilePath() {
		return get(ConfigurationKey.KYCUpgradeFTPDownloadFilePath);
	}

	public static String getKYCUpgradeNotifyEmail() {
		return get(ConfigurationKey.KYCUpgradeNotifyEmail);
	}
	
	public static String getKYCUpgradeEmailSubject() {
		return get(ConfigurationKey.KYCUpgradeEmailSubject);
	}

	public static String getKYCUpgradeEmailMessage() {
		return get(ConfigurationKey.KYCUpgradeEmailMessage);
	}
	
	public static String getCurrencyFormatLocale() {
		return get(ConfigurationKey.CurrencyFormatLocale);
	}
	
	public static boolean getUseRealHSM() { 
	 	return getBoolean(ConfigurationKey.UseRealHSM); 
	 } 
	
	public static boolean getRequiresSuccessfullTransactionsInEmoneyHistory() { 
	 	return getBoolean(ConfigurationKey.RequiresSuccessfullTransactionsInEmoneyHistory); 
	 } 
	
	public static String getPdfHistoryFooter() { 
	 	return get(ConfigurationKey.PdfHistoryFooter); 
	 } 
	
	public static String getEmailPdfHistorySubject() { 
	 	return get(ConfigurationKey.EmailPdfHistorySubject); 
	 }
	
	public static String getEmailPdfHistoryBody() { 
	 	return get(ConfigurationKey.EmailPdfHistoryBody); 
	 } 
	
	public static String getPdfHistoryDateFormat() { 
	 	return get(ConfigurationKey.pdfHistoryDateFormat); 
	 } 
	
	public static String getReportFooter() { 
	 	return get(ConfigurationKey.ReportFooter); 
	 }
	public static String getPromoImagepath() { 
	 	return get(ConfigurationKey.PromoImagePath); 
	 }
	public static String getDateFormatInReportFileNames() { 
	 	return get(ConfigurationKey.DateFormatInReportFileNames); 
	 }
	
	public static String getKTPServerURL() {
		
		return get(ConfigurationKey.KTPServerURL);
	}
	
	public static int getKTPServerTimeout() {
		
		return Integer.parseInt(get(ConfigurationKey.KTPServerTimeout));
	}
	
	public static boolean getSendOTPBeforeApproval() {
		return getBoolean(ConfigurationKey.SendOTPBeforeApproval);
	}
	
	public static String getCapitalizationAuthorizedEmail() {
		return get(ConfigurationKey.CapitalizationAuthorizedEmail);
	}
	
	public static String getMailSesTransport() {
		return get(ConfigurationKey.MailSesTransport);
	}
	
	public static String getMailAwsUser() {
		return get(ConfigurationKey.MailAwsUser);
	}
	
	public static String getMailAwsPassword() {
		return get(ConfigurationKey.MailAwsSecret);
	}

	public static String getMailAwsFrom() {
		return get(ConfigurationKey.MailAwsFrom);
	}
	public static boolean isUseSmtp() {
		return getBoolean(ConfigurationKey.MailUseSmtp);
	}
	public static String getSubscriberProfileImageFilePath() {
		return get(ConfigurationKey.SubscriberProfileImageFilePath);
	}	
}