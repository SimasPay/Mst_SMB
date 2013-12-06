package com.mfino.handset.agent.http;

import com.mfino.handset.agent.datacontainers.EncryptedResponseDataContainer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.EncryptedUserDataContainer;

public class WebAPIHTTPWrapper {

    private EncryptedUserDataContainer container;

    public WebAPIHTTPWrapper(EncryptedUserDataContainer container) {
        this.container = container;
    }
//        public static String webAPIUrl = "http://41.203.113.124:8080/webapi/dynamic";
//        public static String webAPIUrl = "http://eazymoney.zenithbank.com/webapi/dynamic";
//    public static String webAPIUrl = "http://172.29.50.120:8080/webapi/dynamic";
        public static String webAPIUrl = "http://172.20.8.120:8080/webapi/dynamic";
//    public static String webAPIUrl = "http://localhost:8080/webapi/dynamic";
//    private static String webAPIUrl = "http://localhost:8080/webapi/sdynamic";
//      public static String webAPIUrl = "http://115.119.120.118:8080/webapi/dynamic";
//    public static String webAPIUrl = "http://192.168.1.167:8080/webapi/dynamic";
//    public static String webAPIUrl = "http://192.168.1.191:8080/webapi/dynamic";

    public String getUrl() {
        String requestUrl = "";
        requestUrl = webAPIUrl
                + "?" + Constants.PARAMETER_CHANNEL_ID + "=" + Constants.CONSTANT_CHANNEL_ID
                + "&" + Constants.PARAMETER_TRANSACTIONNAME + "=" + container.getTransactionName()
                + "&" + Constants.PARAMETER_SERVICE_NAME + "=" + container.getServiceName()
                + "&" + Constants.PARAMETER_SOURCE_MDN + "=" + container.getSourceMdn();

        if (Constants.TRANSACTION_AGENTACTIVATION.equals(container.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_OTP + "=" + container.getOTP()
                    + "&" + Constants.PARAMETER_ACTIVATION_NEWPIN + "=" + container.getActivationNewPin()
                    + "&" + Constants.PARAMETER_ACTIVATION_CONFIRMPIN + "=" + container.getActivationConfirmPin();

        } else if (Constants.TRANSACTION_LOGIN.equals(container.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_SALT + "=" + container.getSalt()
                    + "&" + Constants.PARAMETER_AUTHENTICATION_STRING + "=" + container.getAuthenticationString();
        } else if (Constants.TRANSACTION_SUBSCRIBERREGISTRATION.equals(container.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin()
                    + "&" + Constants.PARAMETER_SUB_FIRSTNAME + "=" + container.getFirstName()
                    + "&" + Constants.PARAMETER_SUB_LASTNAME + "=" + container.getLastName()
                    + "&" + Constants.PARAMETER_APPLICATION_ID + "=" + container.getApplicationID()
                    + "&" + Constants.PARAMETER_SUB_MDN + "=" + container.getSubscriberMDN()
                    + "&" + Constants.PARAMETER_DOB + "=" + container.getDateOfBirth()
                    + "&" + Constants.PARAMETER_ACCOUNT_TYPE + "=" + container.getAccountType();
//                    + "&" + Constants.PARAMETER_AMOUNT + "=" + mFinoConfigData.getAmount()
        } else if (Constants.TRANSACTION_CASHIN_INQUIRY.equals(container.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin()
                    + "&" + Constants.PARAMETER_AMOUNT + "=" + container.getAmount()
                    + "&" + Constants.PARAMETER_DEST_POCKET_CODE + "=" + container.getDestinationPocketCode()
                    + "&" + Constants.PARAMETER_DEST_MDN + "=" + container.getDestinationMdn();
        } else if (Constants.TRANSACTION_CASHIN.equals(container.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_CONFIRMED + "=" + container.getConfirmed()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + container.getTransferId()
                    + "&" + Constants.PARAMETER_DEST_MDN + "=" + container.getDestinationMdn()
                    + "&" + Constants.PARAMETER_PARENTTXN_ID + "=" + container.getParentTxnId();
        } else if (Constants.TRANSACTION_TRANSACTIONSTATUS.equals(container.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + container.getTransferId();
        } else if (Constants.TRANSACTION_CHECKBALANCE.equals(container.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin()
                    + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + container.getSourcePocketCode();
        } else if (Constants.TRANSACTION_TRANSFER_INQUIRY.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + container.getSourcePocketCode()
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin()
                    + "&" + Constants.PARAMETER_AMOUNT + "=" + container.getAmount()
                    + "&" + Constants.PARAMETER_DEST_POCKET_CODE + "=" + container.getDestinationPocketCode()
                    + "&" + Constants.PARAMETER_DEST_MDN + "=" + container.getDestinationMdn();

        } else if (Constants.TRANSACTION_TRANSFER.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + container.getSourcePocketCode()
                    + "&" + Constants.PARAMETER_PARENTTXN_ID + "=" + container.getParentTxnId()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + container.getTransferId()
                    + "&" + Constants.PARAMETER_DEST_MDN + "=" + container.getDestinationMdn()
                    + "&" + Constants.PARAMETER_CONFIRMED + "=" + container.getConfirmed()
                    + "&" + Constants.PARAMETER_DEST_POCKET_CODE + "=" + container.getDestinationPocketCode();

        } else if (Constants.TRANSACTION_HISTORY.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + container.getSourcePocketCode()
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin();
        } else if (Constants.TRANSACTION_CHANGEPIN.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_NEW_PIN + "=" + container.getNewPin()
                    + "&" + Constants.PARAMETER_CONFIRM_PIN + "=" + container.getConfirmPin()
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin();
        } else if (Constants.TRANSACTION_PURCHASE_INQUIRY.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_AMOUNT + "=" + container.getAmount()
                    + "&" + Constants.PARAMETER_PARTNER_CODE + "=" + container.getPartnerCode()
                    + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + container.getSourcePocketCode()
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin();
        } else if (Constants.TRANSACTION_PURCHASE.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_PARTNER_CODE + "=" + container.getPartnerCode()
                    + "&" + Constants.PARAMETER_PARENTTXN_ID + "=" + container.getParentTxnId()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + container.getTransferId()
                    + "&" + Constants.PARAMETER_CONFIRMED + "=" + container.getConfirmed();
        } else if (Constants.TRANSACTION_AGENT_AGENT_TRANSFER_INQUIRY.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_AGENT_CODE + "=" + container.getPartnerCode()
                    + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + container.getSourcePin()
                    + "&" + Constants.PARAMETER_AMOUNT + "=" + container.getAmount()
                    + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + container.getSourcePocketCode();
        } else if (Constants.TRANSACTION_AGENT_TO_AGENT_TRANSFER.equals(container.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_PARENTTXN_ID + "=" + container.getParentTxnId()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + container.getTransferId()
                    + "&" + Constants.PARAMETER_CONFIRMED + "=" + container.getConfirmed()
                    + "&" + Constants.PARAMETER_AGENT_CODE + "=" + container.getPartnerCode();
        }

//        System.out.println(requestUrl);

        return requestUrl;
    }

    public EncryptedResponseDataContainer getResponseData() {
//        System.out.println("WebAPIHTTPWrapper getResponseData() for URL " + getUrl());

        EncryptedResponseDataContainer responseData = new EncryptedResponseDataContainer();

        HttpConnection http = null;
        InputStream iStrm = null;

        try {
            http = (HttpConnection) Connector.open(getUrl());
            http.setRequestMethod(HttpConnection.GET);
            http.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");

            if (http.getResponseCode() == HttpConnection.HTTP_OK) {
                iStrm = http.openInputStream();  // open and return an input stream for connection
                int length = (int) http.getLength();
//                System.out.println("WebAPIHTTPWrapper length=" + length);

                String responseString = "";

                if (length != -1) {
                    byte serverData[] = new byte[length];
                    iStrm.read(serverData);
                    responseString = new String(serverData);
                } else {
                    ByteArrayOutputStream bStrm = new ByteArrayOutputStream();

                    int ch;
                    while ((ch = iStrm.read()) != -1) {
                        bStrm.write(ch);
                    }

                    responseString = new String(bStrm.toByteArray());
                    bStrm.close();
                }

//                System.out.println("File Contents: " + responseString);
                if (responseString.indexOf("<body>") != -1 && responseString.indexOf("</body>") != -1) {
                    responseString = responseString.substring(responseString.indexOf("<body>") + "<body>".length(), responseString.indexOf("</body>"));
                }

                XMLParser parser = new XMLParser();
                responseData = parser.parse(responseString);
            } else {
                responseData.setMsgCode("101010");
                responseData.setErrorMsg("HTTP Error: Error connecting to webapi, Please check your connectivity");
            }

        } catch (IOException e) {
            responseData.setMsgCode("1001");
            responseData.setMsg("Error occured.Please try after some time");
            responseData.setErrorMsg("Error occured.Please try after some time");
//            e.printStackTrace();
        } catch (Exception e) {
            responseData.setMsgCode("202020");
            responseData.setErrorMsg("Generic Error: Please contact mFino support team");
            responseData.setMsg("Generic Error: Please contact mFino support team");
//            e.printStackTrace();
        } finally {
            try {
                if (iStrm != null) {
                    iStrm.close();
                }
                if (http != null) {
                    http.close();
                }
            } catch (IOException ioe) {
//                ioe.printStackTrace();
            }
        }

        return responseData;
    }
}
