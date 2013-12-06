/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.integrationtestharness.commons;

/**
 *
 * @author sunil
 */
public class ITHConstants {

    public static final String SMS_PIN="sms_pin";
    public static final String SMS_OLDPIN="sms_oldpin";
    public static final String SMS_NEWPIN="sms_newpin";
    public static final String SMS_AMOUNT="sms_amount";
    public static final String SMS_MCASHPIN="sms_mcashpin";
    public static final String SMS_CONTACTNUMBER="sms_contactnumber";
    public static final String SMS_SERVICENAME="sms_servicename";
    public static final String SMS_SOURCEMSISDN="sms_sourcemsisdn";
    public static final String SMS_SECRETANSWER="sms_secretanswer";
    public static final String SMS_DESTMSISDN="sms_destmsisdn";
    public static final String SMS_BUCKETTYPE="sms_buckettype";
    public static final String SMS_RECHARGEAMOUNT="sms_rechargeamount";
    public static final String SMS_DISTRIBUTEAMOUNT="sms_distributieamount";
    public static final String OUTGOINGURL="sms_outgoingurl";
    public static final String HTMLRESPONSE="htmlresponse";
    public static final String SMS_MESSAGE="sms_message";
    
    //API Constants
    public static final String SUBSRICPTION_ACTIVATION="subsricption_activation";
    public static final String CHANGE_PIN="change_pin";
    public static final String MERCHANT_CHANGE_PIN="merchant_change_pin";
    public static final String RESET_PIN="reset_pin";
    public static final String MERCHANT_RESET_PIN="merchant_reset_pin";
    public static final String GET_TRANSACTIONS="get_transactions";
    public static final String CHECK_BALANCE="check_balance";
    public static final String CHANGE_MCASH_PIN="change_mcash_pin";
    public static final String MOBILE_AGENT_RECHARGE="mobile_agent_recharge";
    public static final String MCASH_TOPUP="mcash_topup";
    public static final String GET_MCASH_TRANSACTIONS="get_mcash_transactions";
    public static final String MOBILE_AGENT_DISTRIBUTE="mobile_agent_distribute";
    public static final String MERCHANT_SHARE_LOAD="share_load";
    public static final String SHARE_LOAD="merchant_share_load";
    public static final String MCASH_TO_MCASH="mcash_to_mcash";
    public static final String MCASH_BALANCE_INQUIRY="mcash_to_mcash";
    public static final String MERCHANT_MPIN_RESET="merchant_mpin_reset";
    public static final String FREQUENCY_TEST="frequency_test";
    public static final String MERCHANT_CHECK_BALANCE="merchant_check_balance";
    public static final String MERCHANT_GET_TRANSACTIONS="merchant_get_transactions";

    //Bit representation of API's
    public static final int SUBSRICPTION_ACTIVATION_INT=1;
    public static final int CHANGE_PIN_INT=2;
    public static final int RESET_PIN_INT=4;
    public static final int GET_TRANSACTIONS_INT=8;
    public static final int CHECK_BALANCE_INT=16;
    public static final int CHANGE_MCASH_PIN_INT=32;
    public static final int MOBILE_AGENT_RECHARGE_INT=64;
    public static final int MCASH_TOPUP_INT=128;
    public static final int GET_MCASH_TRANSACTIONS_INT=256;
    public static final int MOBILE_AGENT_DISTRIBUTE_INT=512;
    public static final int SHARE_LOAD_INT=1024;
    public static final int MCASH_TO_MCASH_INT=2048;
    public static final int MCASH_BALANCE_INQUIRY_INT=4096;
    public static final int MERCHANT_MPIN_RESET_INT=8192;
    public static final int FREQUENCY_TEST_INT=16384;
    public static final int MERCHANT_CHANGE_PIN_INT=33668;
    public static final int MERCHANT_RESET_PIN_INT=67336;
    public static final int MERCHANT_CHECK_BALANCE_INT=134672;
    public static final int MERCHANT_SHARE_LOAD_INT=269344;
    public static final int MERCHANT_GET_TRANSACTIONS_INT=538688;

    //Type of List
    public static final int SUBSCRIBER_LIST = 1;
    public static final int MERCHANT_LIST = 2;
    public static final int MCASH_LIST = 3;

    public static final String SUBSCRIBER_FILE ="subscriber_report" ;
    public static final String MERCHANT_FILE = "merchant_report";
    public static final String  MCASH_FILE = "mcash_report";
}
