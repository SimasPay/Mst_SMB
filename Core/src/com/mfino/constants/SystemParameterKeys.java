/**
 *
 */
package com.mfino.constants;

/**
 * @author Chaitanya
 *
 */
public class SystemParameterKeys {

	  //Before Correcting errors reported by Findbugs:
		//none of the fiels were final

	  //After Correcting the errors reported by Findbugs
		//all fields are final
	public static final String COUNTRY_CODE = "country.code";
	public static final String SUSPENSE_POCKET_ID_KEY = "suspense.pocket.id";
	public static final String CHARGES_POCKET_ID_KEY = "charges.pocket.id";
	public static final String PLATFORM_DUMMY_MDN_KEY = "platform.dummy.mdn";

	public static final String PIN_LENGTH = "pin.length";
	public static final String OTP_LENGTH = "otp.length";
	public static final String DEFAULT_CURRENCY_CODE = "default.currency.code";
	public static final String MAX_WRONGPIN_COUNT = "wrong.pin.count";
	public static final String SCTL_TIMEOUT	= "sctl.timeout";
	public static final String TIME_ZONE = "timezone";
	public static final String GLOBAL_ACCOUNT_POCKET_ID_KEY = "global.account.pocket.id";
	public static final String GLOBAL_SVA_POCKET_ID_KEY = "global.sva.pocket.id";
	public static final String TAX_POCKET_ID_KEY = "tax.pocket.id";
	public static final String TAX_PERCENTAGE = "tax.percentage";
	public static final String COLLECTOR_POCKET_TEMPLATE_ID_KEY = "collector.pocket.template.id";
	public static final String SERVICE_PARTNER__ID_KEY = "service.partner.id";
	public static final String INTERBANK_PARTNER_MDN_KEY = "interbank.partner.mdn";
	public static final String IN_PARTNER_SUFFIX = "INCODE_";
	public static final String DAYS_TO_SUSPEND_OF_NO_ACTIVATION = "days.to.suspend.of.no.activation";
	public static final String DAYS_TO_SUSPEND_OF_INACTIVE = "days.to.suspend.of.inactive";
	public static final String DAYS_TO_RETIRE_OF_SUSPENDED = "days.to.retire.of.suspended";
	public static final String DAYS_TO_GRAVE_OF_RETIRED = "days.to.grave.of.retired";
	public static final String DAYS_TO_NATIONAL_TREASURY_OF_GRAVED = "days.to.national.treasury.of.graved";
	public static final String DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY = "days.to.inactivate.of.active.subscriber.when.no.activity";
	public static final String DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = "days.to.inactivate.of.active.subscriber.when.no.fundmovement";
	public static final String DAYS_TO_EXPIRE_PASSWORD = "days.to.expirepassword";
	public static final String AUTOMATIC_RESEND_OTP = "automatic.resend.otp";
	public static final String POCKET_TEMPLATE_UNREGISTERED = "pocket_template_unregistered";
	public static final String SUSPENCE_POCKET_TEMPLATE_ID_KEY="suspence.pocket.template.id";
	public static final String SUBAPP_URL_KEY = "subapp.url";
	public static final String AGENTAPP_URL_KEY = "agentapp.url";
	public static final String PLATFORM_DUMMY_SUBSCRIBER_MDN = "platform.dummy.subscriber.mdn";
	public static final String DEFAULT_LANGUAGE_OF_SUBSCRIBER = "default.language.of.subscriber";

	public static final String CHARGE_REVERSAL_FUNDING_POCKET = "charge.reversal.funding.pocket";
	public static final String THIRDPARTY_PARTNER_MDN = "thirdparty.partner.mdn";
	public static final String FAC_PREFIX_VALUE = "fac.prefix.value";
	public static final String REVERSE_POCKET_ID = "reverse.pocket.id";
	public static final String CASHOUT_AT_ATM_EXPIRY_TIME = "cashout.at.atm.expiry.time";
	public static final String TRANSFER_TO_UNREGISTERED_EXPIRY_TIME = "transfer.to.unregistered.expiry.time";
	public static final String REVERSE_CHARGE_FOR_EXPIRED_TRANSFER_TO_UNREGISTERED = "reverse.charge.for.expired.transfer.to.unregistered";
	public static final String INTERSWITCH_CASHIN_PARTNERCODE = "interswitch.cashin.partnercode";
	public static final String MAX_VALUE_OF_CASHOUT_AT_ATM = "maximum.value.of.cashout.at.atm";
	public static final String MIN_REGISTRATION_AGE = "min.registration.age";
	public static final String MDN_LENGTH_WITH_COUNTRYCODE = "mdnlength.with.countrycode";
	public static final String MAX_MDN_LENGTH_WITH_COUNTRYCODE = "max.mdn.length.with.country.code";
	public static final String MIN_MDN_LENGTH_WITH_COUNTRYCODE = "min.mdn.length.with.country.code";
	public static final String INTEGRATION_AUTHENTICATION_KEY_LENGTH = "integration.authenticationkey.length";
	public static final String PASSWORD_HISTORY_COUNT = "password.history.count";
	public static final String PROFILE = "profile";
	public static final String MAX_NO_OF_ALLOWED_SUBSCRIBERS = "max.no.of.allowed.subscribers";
	public static final String ATM_TERMINAL_PREFIX_CODE = "atm.terminal.prefix.code";
	public static final String ATM_TERMINAL_DEFAULT_ACCOUNT_NUMBER = "atm.terminal.default.account.number";
	public static final String OTP_TIMEOUT_DURATION = "otp.timeout.duration";
	public static final String MAX_NO_OF_DAYS_TO_REVERSE_TXN = "max.no.of.days.to.reverse.transaction";
	public static final String NO_OF_DECIMALS = "no.of.decimals";
	public static final String RETIRED_SUBSCRIBER_SYSTEM_COLLECTOR_POCKET = "retired.subscriber.system.collector.pocket";
	public static final String NATIONAL_TREASURY_POCKET = "national.treasury.pocket";
	public static final String BANK_SERVICE_STATUS="bank.service.status";
	public static final String EMAIL_VERIFICATION_NEEDED = "email.verification.needed";
	public static final String FUNDING_POCKET_FOR_AGENT = "funding.pocket.for.agent";
	public static final String MAX_AMT_FOR_AGENT_FUNDING ="max.amt.for.agent.funding";
	public static final String NIBSS_INTER_EMONEY_TRANSFER_CODE = "nibss.inter.emoney.transfer.code";
	public static final String FRSC_PAYMENT_CODE = "frsc.payment.code";
    public static final String AIRTIME_PIN_PURCHASE_TERMINAL_ID = "airtime.pin.purchase.terminal.id";
	public static final String REGISTRATION_OTP_TIMEOUT_DURATION = "registration.otp.timeout.duration";
    public static final String AIRTIME_PIN_PURCHASE_BILLER_CODE = "airtime.pin.purchase.biller.code";
	public static final String ENCRYPT_FIX_MESSAGE = "mfino.encrypt.fixMessage";
	public static final String CATEGORY_BANK_CODES = "category.bankCodes";
	public static final String CATEGORY_PURCHASE = "category.purchase";
	public static final String CATEGORY_PAYMENTS = "category.payments";
	public static final String CATEGORY_PREPAID = "category.prepaid";
	public static final String CATEGORY_POSTPAID = "category.postpaid";	
	public static final String CATEGORY_PREPAIDPLN = "category.prepaidPLN";
	public static final String CATEGORY_POSTPAIDPLN = "category.postpaidPLN";	
	public static final String CATEGORY_PREPAIDPHONE = "category.prepaidPhone";
	public static final String CATEGORY_POSTPAIDPHONE = "category.postpaidPhone";
	public static final String CATEGORY_HELP = "category.help";
	public static final String CATEGORY_UPGRADE_ADDRESSLIST = "category.upgradeAddressList";
	public static final String CATEGORY_CASHOUT_ADDRESSLIST = "category.cashoutAddressList";
	public static final String ACTIVATION_SMS_INTERVAL_BULKUPLOAD="sms.interval.inactive.bulkupload";
    public static final String MAX_TXN_COUNT_IN_HISTORY = "max.txn.count.in.history";
	public static final String CASHOUT_AT_ATM_FAC_AS_PIN = "cashout.atm.fac.as.pin";
	public static final String RESET_PIN_MODE = "reset.pin.mode";
	public static final String LAST_BDV_DATE = "lastbdv.calculation.date";
	public static final String SEND_OTP_TO_OTHER_MDN = "send.otp.to.other.mdn";
	public static final String BANK_TRANSACTIONS_HISTORY_RECORD_ORDER_IS_ASCENDING = "bank.transactions.history.record.order.isAscending";
	public static final String NFC_CARD_TOPUP_PARTNER_CODE = "nfc.card.topup.partner.code";
	public static final String MAX_FAVORITES_PER_CATEGORY = "max.favorites.per.category";
	public static final String ALLOWED_PARTNERS_TOREGISTER_THROUGHAPI = "allowed.partners.toregister.throughapi";
	public static final String PARTNER_REGISTER_THROUGHAPI_APPROVAL = "partner.register.throughapi.approvalrequired";
	public static final String STARTIMES_BILLER_CODE = "startimes.biller.code";
	public static final String RESTRICT_BANKPOCKET_TOBUY_AIRTIME = "restrict.bankpocket.tobuy.airtime";
	public static final String MAX_DURATION_TO_FETCH_TXN_HISTORY = "max.duration.to.fetch.txn.history";
	public static final String FLASHIZ_BILLER_CODE = "flashiz.biller.code";
	public static final String MAX_OTP_TRAILS = "max.otp.trials";
	public static final String DONATION_PARTNER_MDN = "donation.partner.mdn"; 
	public static final String RESEND_OTP_BLOCK_DURATION_MINUTES = "resend.otp.block.duration.minutes";
	public static final String ABSOLUTE_LOCK_DURATION_HOURS = "absolute.lock.duration.hours";
	public static final String OTP_TIMEOUT_DURATION_MINUTES = "otp.timeout.duration.minutes";
	public static final String UANGKU_IBT_BILLER_CODE = "uangku.ibt.biller.code";
	
	public static final String TRANSFER_TO_UANGKU_PREFIX_NUMBER = "transfer.to.uangku.prefix.number";
	public static final String DATE_TO_EXPIRE_MOBILE_APP_PIN = "date.to.expire.mobile.app.pin";
}
