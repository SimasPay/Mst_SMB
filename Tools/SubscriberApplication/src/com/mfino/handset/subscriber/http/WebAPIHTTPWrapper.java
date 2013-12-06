package com.mfino.handset.subscriber.http;

import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.constants.Constants;
import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
import com.mfino.handset.subscriber.datacontainers.EncryptedResponseDataContainer;
import com.mfino.handset.subscriber.datacontainers.EncryptedUserDataContainer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import org.bouncycastle.crypto.params.KeyParameter;

public class WebAPIHTTPWrapper {

    private EncryptedUserDataContainer edContainer;
    private KeyParameter keyParameter;
    //    public static String webAPIUrl = "http://41.203.113.122:8080/webapi/dynamic";
//    public static String webAPIUrl = "http://192.168.1.167:8080/webapi/dynamic";
    private static String webAPIUrl = "http://192.168.1.191:8080/webapi/dynamic";
//    public static String webAPIUrl = "http://115.119.120.118:8080/webapi/dynamic";

    public WebAPIHTTPWrapper(UserDataContainer udcontainer) {
        this.edContainer = EncryptedUserDataContainer.createSecureApplicationDataContainer(udcontainer);
        this.keyParameter = udcontainer.getAESKey();
    }

    public String getUrl() {
        String requestUrl = webAPIUrl
                + "?" + Constants.PARAMETER_CHANNEL_ID + "=" + Constants.CONSTANT_CHANNEL_ID
                + "&" + Constants.PARAMETER_SERVICE_NAME + "=" + edContainer.getServiceName()
                + "&" + Constants.PARAMETER_SOURCE_MDN + "=" + edContainer.getSourceMdn()
                + "&" + Constants.PARAMETER_SOURCE_PIN + "=" + edContainer.getSourcePin()
                + "&" + Constants.PARAMETER_TRANSACTIONNAME + "=" + edContainer.getTransactionName();
        if (Constants.TRANSACTION_ACTIVATION.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_OTP + "=" + edContainer.getOTP()
                    + "&" + Constants.PARAMETER_ACTIVATION_CONFIRMPIN + "=" + edContainer.getActivationConfirmPin()
                    + "&" + Constants.PARAMETER_ACTIVATION_NEWPIN + "=" + edContainer.getActivationNewPin();
        } else if (Constants.TRANSACTION_CHECKBALANCE.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + edContainer.getSourcePocketCode();
        } else if (Constants.TRANSACTION_TRANSFER_INQUIRY.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + edContainer.getSourcePocketCode()
                    + "&" + Constants.PARAMETER_DEST_MDN + "=" + edContainer.getDestinationMdn()
                    + "&" + Constants.PARAMETER_AMOUNT + "=" + edContainer.getAmount()
                    + "&" + Constants.PARAMETER_DEST_POCKET_CODE + "=" + edContainer.getDestinationPocketCode();

        } else if (Constants.TRANSACTION_TRANSFER.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + edContainer.getSourcePocketCode()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + edContainer.getTransferId()
                    + "&" + Constants.PARAMETER_DEST_MDN + "=" + edContainer.getDestinationMdn()
                    + "&" + Constants.PARAMETER_CONFIRMED + "=" + edContainer.getConfirmed()
                    + "&" + Constants.PARAMETER_PARENTTXN_ID + "=" + edContainer.getParentTxnId()
                    + "&" + Constants.PARAMETER_DEST_POCKET_CODE + "=" + edContainer.getDestinationPocketCode();
        } else if (Constants.TRANSACTION_CASHOUT_INQUIRY.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_AMOUNT + "=" + edContainer.getAmount()
                    + "&" + Constants.PARAMETER_AGENT_CODE + "=" + edContainer.getAgentCode()
                    + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + edContainer.getSourcePocketCode();
        } else if (Constants.TRANSACTION_TRANSACTIONSTATUS.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + edContainer.getTransferId();
        } else if (Constants.TRANSACTION_CASHOUT.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_AGENT_CODE + "=" + edContainer.getAgentCode()
                    + "&" + Constants.PARAMETER_PARENTTXN_ID + "=" + edContainer.getParentTxnId()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + edContainer.getTransferId()
                    + "&" + Constants.PARAMETER_CONFIRMED + "=" + edContainer.getConfirmed();
        } else if (Constants.TRANSACTION_LOGIN.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl
                    + "&" + Constants.PARAMETER_SALT + "=" + edContainer.getSalt()
                    + "&" + Constants.PARAMETER_AUTHENTICATION_STRING + "=" + edContainer.getAuthenticationString()
                    + "&sourcePIN=abcd";
        } else if (Constants.TRANSACTION_CHANGEPIN.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_NEW_PIN + "=" + edContainer.getNewPin()
                    + "&" + Constants.PARAMETER_CONFIRM_PIN + "=" + edContainer.getConfirmPin();
        } else if (Constants.TRANSACTION_PURCHASE_INQUIRY.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_AMOUNT + "=" + edContainer.getAmount()
                    + "&" + Constants.PARAMETER_PARTNER_CODE + "=" + edContainer.getPartnerCode()
                    + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + edContainer.getSourcePocketCode();
        } else if (Constants.TRANSACTION_PURCHASE.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_PARTNER_CODE + "=" + edContainer.getPartnerCode()
                    + "&" + Constants.PARAMETER_PARENTTXN_ID + "=" + edContainer.getParentTxnId()
                    + "&" + Constants.PARAMETER_TRANSFER_ID + "=" + edContainer.getTransferId()
                    + "&" + Constants.PARAMETER_CONFIRMED + "=" + edContainer.getConfirmed();
        } else if (Constants.TRANSACTION_HISTORY.equals(edContainer.getTransactionName())) {
            requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE + "=" + edContainer.getSourcePocketCode();
        }

        return requestUrl;
    }

    public ResponseDataContainer getResponseData() {

//        System.out.println();
//        System.out.println();
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
            responseData.setMsgCode("101010");
            responseData.setErrorMsg("HTTP Error: Error connecting to webapi, Please check your connectivity");

//            e.printStackTrace();
        } catch (Exception e) {
            responseData.setMsgCode("202020");
            responseData.setErrorMsg("Generic Error: Please contact mFino support team");

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

        return ResponseDataContainer.createResponseData(responseData, keyParameter);
    }
}
