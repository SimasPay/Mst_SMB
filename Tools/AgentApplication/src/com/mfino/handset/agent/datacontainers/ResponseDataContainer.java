/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.datacontainers;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.security.CryptoService;
import java.util.Vector;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 *
 * @author karthik
 */
public class ResponseDataContainer {

    private EncryptedResponseDataContainer erdContainer;
    private KeyParameter keyParameter;

    private ResponseDataContainer(EncryptedResponseDataContainer rd) {
        this.erdContainer = rd;
    }

    public static ResponseDataContainer createResponseData(EncryptedResponseDataContainer rd, KeyParameter kp) {
        ResponseDataContainer sac = new ResponseDataContainer(rd);
        sac.setKeyParameter(kp);
        return sac;
    }
    private String Amount;
    private String TransferId;
    private String ParentTxnId;
    private String Transactioncharges;
    private String creditAmount;
    private String debitAmount;
    private String transactionTime;
    private String refId;
    private String msgCode;
    private String TransactionCharges;

    /**
     * @return the messages
     */
//    public Vector getMessagesList() {
//        return erdContainer.getMessagesList();
//    }

    private String decryptString(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] strBytes = CryptoService.hexToBin(str.toCharArray());
            byte[] strOut = CryptoService.decryptWithAES(keyParameter, strBytes);
            return new String(strOut, Constants.UTF_8);
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @return the container
     */
    public EncryptedResponseDataContainer getContainer() {
        return erdContainer;
    }

    /**
     * @return the Amount
     */
    public String getAmount() {
            return decryptString(erdContainer.getEncryptedAmount());
    }

    /**
     * @return the TransferId
     */
    public String getTransferId() {
            return decryptString(erdContainer.getEncryptedTransferId());
    }

    /**
     * @return the ParentTxnId
     */
    public String getParentTxnId() {
            return decryptString(erdContainer.getEncryptedParentTxnId());
    }

    /**
     * @return the Transactioncharges
     */
    public String getTransactioncharges() {
            return decryptString(erdContainer.getEncryptedTransactionCharges());
    }

    /**
     * @return the creditAmount
     */
    public String getCreditAmount() {
            return decryptString(erdContainer.getEncryptedCreditAmount());
    }

    /**
     * @return the debitAmount
     */
    public String getDebitAmount() {
            return decryptString(erdContainer.getEncryptedDebitAmount());
    }

    /**
     * @return the authenticationString
     */
    public String getAuthenticationString() {
        return erdContainer.getAuthenticationString();
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return erdContainer.getSalt();
    }

//    /**
//     * @return the sourcePocketCode
//     */
//    public String getSourcePocketCode() {
//        return erdContainer.getSourcePocketCode();
//    }
//
//    /**
//     * @return the destinationPocketCode
//     */
//    public String getDestinationPocketCode() {
//        return udContainer.getDestinationPocketCode();
//    }
//
//    /**
//     * @return the udContainer
//     */
//    private SubscriberDataContainer getUdContainer() {
//        return udContainer;
//    }
    /**
     * @return the transactionTime
     */
    public String getTransactionTime() {
        return erdContainer.getTransactionTime();
    }

    /**
     * @return the refId
     */
    public String getRefId() {
            return decryptString(erdContainer.getEncryptedRefId());
    }

    /**
     * @return the msgCode
     */
    public String getMsgCode() {
        return erdContainer.getMsgCode();
    }

    /**
     * @return the TransactionCharges
     */
    public String getTransactionCharges() {
            return decryptString(erdContainer.getEncryptedTransactionCharges());
    }

    public String getEncryptedAESKey() {
        return erdContainer.getEncryptedAESkey();
    }

    /**
     * @return the keyParameter
     */
    private KeyParameter getKeyParameter() {
        return keyParameter;
    }

    /**
     * @param keyParameter the keyParameter to set
     */
    private void setKeyParameter(KeyParameter keyParameter) {
        this.keyParameter = keyParameter;
    }
    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return erdContainer.getErrorMsg();
    }

    public String getMessage(){
        return erdContainer.getMsg();
    }
    
}
