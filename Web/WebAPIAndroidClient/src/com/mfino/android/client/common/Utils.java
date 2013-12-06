package com.mfino.android.client.common;

public class Utils
{
    // put the webapi residing server url, do not use localhost, as
    // the connection will go the emulator itself
    public static String URL                 = "";
    public static String SourceMDN           = "sourceMDN";
    public static String SourcePIN           = "sourcePIN";
    public static String SecretAnswer        = "secretAnswer";
    public static String ContactNumber       = "contactNumber";
    public static String Mode                = "mode";
    public static String SubscriberModeValue = "1";
    public static String MerchantModeValue   = "2";
    public static String BankModeValue       = "3";
    public static String ServiceName         = "serviceName";
    public static String ChannelID           = "channelId";
    public static String ChannelIDValue      = "7";
    public static String DestMDN             = "destMDN";
    public static String OldPIN              = "oldPIN";
    public static String NewPIN              = "newPIN";
    public static String Amount              = "amount";
    public static String EncodingValue       = "utf-8";
    public static String Activation          = "activation";
    public static String ShareLoad           = "transfer";
    public static String ChangePin           = "changePin";
    public static String ResetPin            = "resetPin";
    public static String LastNTxns           = "getTransactions";
    public static String BucketType          = "bucketType";
    public static String CardPanSuffix       = "cardPANSuffix";
    public static String BankId              = "bankID";

    public static String Recharge            = "recharge";
    public static String AirtimeTransfer     = "transfer";
    public static String CheckBalance        = "checkBalance";

    public static String TransferInquiry     = "transferInquiry";
    public static String MoneyTransfer       = "transfer";
    public static String TransferID          = "transferID";
    public static String ParentTxnID         = "parentTxnID";
    public static int    TimeoutConnection   = 3000;
    public static int    TimeoutSocket       = 5000;

}
