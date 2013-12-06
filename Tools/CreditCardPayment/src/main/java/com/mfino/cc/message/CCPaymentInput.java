/**
 * 
 */
package com.mfino.cc.message;

import java.math.BigDecimal;

import com.mfino.util.ConfigurationUtil;

/**
 * Contains the input parameters for credit card payment transactions. These
 * values are required by the gateway system. But they are not taken as a input
 * from the customer directly. Most of these parameters are merchant related.
 */
public final class CCPaymentInput {

    /**
     * ID of the merchant - provided by Payment Gateway system
     */
    private final String merchantID;
    private String operation;
    /*
     *
     *
     */
    private String mdn;
    /*
     *
     *
     */
    private String description;
    /**
     * Payment method that will be used for a transaction. For Credit Card, the
     * value is always 1
     */
    private final String paymentMethod;
    /**
     * Currency in which this transaction is paid.
     */
    // TODO: Find out from Smart if the currencyCode should come as an input
    // from Customer
    private final String currencyCode;
    /**
     * Transaction password for the Payment Gateway system. Provided by the
     * Payment gateway system when account is created.
     */
    private final String transactionPwd;
    /**
     * Randomly generated ID for each transaction
     */
    private String merchantTxnID;
    /**
     * The URL to which the Payment gateway system will return the result
     */
    private String returnURL;
    /**
     * Transaction ID provided by Infinitium. For a new Sales or Authorise
     * transaction, this is always 0. This is currently not a required
     * parameter. But for a capture transaction this is required and should be
     * retrieved from the database.
     */
    private String transactionID;
    /**
     * this amount would be retrieved from multix in case of postpaid
     * Wantedly placed it as string, so that will amount in case of possible
     * else will send error message
     */
    private String ccBucketType;
    private String sessionID;
    public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getCCBucketType() {
		return ccBucketType;
	}

	public void setCCBucketType(String ccBucketType) {
		this.ccBucketType = ccBucketType;
	}
    private BigDecimal amount;
    private String authId;
    private static String postURL;
    private String billReferenceNumber;
    private Long subscriberid;
    private Long pocketid;
    /**
     * Adding the nsiapayment gateway parameters
     * @return
     */
    private String paymentGateway;

    public String getPaymentGateway() {
        return paymentGateway;
    }
    private String nsiachainnum;
    private String nsiaCurrency;
    private String nsiapurchaseCurrency;
    private String nsiaacquireBin;
    private String nsiapassword;
    private String nsiawords;
    private String nsiaType;
    private String nsiaBasket;
    private String nsiaMALLID;
    private String sourceIP;

    public String getNsiaMALLID() {
        return nsiaMALLID;
    }

    public String getNsiaBasket() {
        return nsiaBasket;
    }

    public String getNsiaCurrency() {
        return nsiaCurrency;
    }

    public String getNsiaType() {
        return nsiaType;
    }

    public String getNsiaacquireBin() {
        return nsiaacquireBin;
    }

    public String getNsiachainnum() {
        return nsiachainnum;
    }

    public String getNsiapassword() {
        return nsiapassword;
    }

    public String getNsiapurchaseCurrency() {
        return nsiapurchaseCurrency;
    }

    public String getNsiawords() {
        return nsiawords;
    }
    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public Long getPocketid() {
        return pocketid;
    }

    public void setPocketid(Long pocketid) {
        this.pocketid = pocketid;
    }

    public Long getSubscriberid() {
        return subscriberid;
    }

    public void setSubscriberid(Long subscriberid) {
        this.subscriberid = subscriberid;
    }

    /**
     * Default constructor
     */
    public CCPaymentInput() {
        // Set the values for the fields. Ideally, these values should be read
        // from either a File or a Database and not hardcoded here
        // TODO: Read the values from a secure datastore

        merchantID = ConfigurationUtil.getCreditcardMerchantid();
        paymentMethod = "1";
        currencyCode = "IDR";
        transactionPwd = ConfigurationUtil.getCreditcardTransactionPassword();
        returnURL = "http://localhost:8080/CreditCardPayment/";
        merchantTxnID = "";
//        amount = 0l;
        amount = BigDecimal.ZERO;
        postURL = ConfigurationUtil.getCreditcardGatewayURL();
        if(ConfigurationUtil.getCreditcardGatewayName().equalsIgnoreCase("NSIAPAY")){
        nsiachainnum = "NA";
        nsiaCurrency = "360";
        nsiapurchaseCurrency = "360";
        nsiaacquireBin = "123456";
        nsiapassword = "123456";
        nsiaType = "IMMEDIATE";
        paymentGateway = ConfigurationUtil.getCreditcardGatewayName();
        nsiaMALLID = ConfigurationUtil.getCreditcardTransactionMALLID();
    }
    }

    /**
     * Construct and return the signature. Signature is required by the Payment
     * Gateway to ensure the transaction is from a secure party. Amount will
     * have to be added to the signature in the HTML page after user inputs
     */
    public String getSignature() {
        return "##" + merchantID.toUpperCase() + "##" + transactionPwd.toUpperCase() + "##" + merchantTxnID.toUpperCase() + "##";
    }
     public String getNSIASignature() {
        return "##" + merchantID.toUpperCase() + "##" + transactionPwd.toUpperCase() + "##" + merchantTxnID.toUpperCase() + "##";
    }
    public String getPostURL() {
        // this should be static URL
        return postURL;
    }
    public String getBillReferenceNumber() {
        return billReferenceNumber;
    }
    public void setBillReferenceNumber(String billReferenceNumber) {
        this.billReferenceNumber = billReferenceNumber;
    }

    /**
     * @return the merchantTxnID
     */
    public String getMerchantTxnID() {
        return merchantTxnID;
    }

    /**
     * @return the merchantID
     */
    public String getMerchantID() {
        return merchantID;
    }

    /**
     * @return the merchantID
     */
    public String getAuthID() {
        return authId;
    }

    /**
     * @return the merchantID
     */
    public void setAuthID(String authId) {
        this.authId = authId;
    }

    /**
     * @return the mdn
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * @return the paymentMethod
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * @return the currencyCode
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * @return the returnURL
     */
    public String getReturnURL() {
        return returnURL;
    }

    /**
     * @return the transactionID
     */
    public String getTransactionID() {
        return transactionID;
    }

    public BigDecimal getAmount() {
        return amount;
    }
     public String getTransactionPwd() {
        return transactionPwd;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMerchantTransactionID(String id) {
        this.merchantTxnID = id;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public void setOperation(String op) {
        this.operation = op;
    }

    public String getOperation() {
        return operation;
    }

	@Override
	public String toString() {
		return "CCPaymentInput [merchantID=" + merchantID + ", operation="
				+ operation + ", mdn=" + mdn + ", description=" + description
				+ ", paymentMethod=" + paymentMethod + ", currencyCode="
				+ currencyCode + ", transactionPwd=" + transactionPwd
				+ ", merchantTxnID=" + merchantTxnID + ", returnURL="
				+ returnURL + ", transactionID=" + transactionID + ", amount="
				+ amount + ", authId=" + authId + ", billReferenceNumber="
				+ billReferenceNumber + ", subscriberid=" + subscriberid
				+ ", pocketid=" + pocketid + "]";
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getSourceIP() {
		return sourceIP;
	}
}
