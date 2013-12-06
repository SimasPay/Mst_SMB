/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.datacontainers;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.security.CryptoService;
import com.mfino.handset.agent.util.StringUtil;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 *
 * @author karthik
 */
public class EncryptedUserDataContainer {

    private AgentDataContainer container;

    private EncryptedUserDataContainer(AgentDataContainer container) {
        this.container = container;
    }

    public static EncryptedUserDataContainer createSecureApplicationDataContainer(AgentDataContainer container) {
        EncryptedUserDataContainer sac = new EncryptedUserDataContainer(container);
        return sac;
    }
    private String sourcePin;
    private String Amount;
    private String TransferId;
    private String ParentTxnId;
    private String SecretAnswer;
    private String OldPin;
    private String NewPin;
    private String ConfirmPin;
    private String TransactionID;
    private String Transactioncharges;
    private String creditAmount;
    private String debitAmount;
    private String DateOfBirth;
    private String AccountType;
    private String EncryptedOTP;
    private String actiationNewPIN;
    private String actiationConfirmPIN;

    /**
     * @return the container
     */
    public AgentDataContainer getContainer() {
        return container;
    }

    /**
     * @return the sourceMdn
     */
    public String getSourceMdn() {
        return container.getSourceMdn();
    }

    /**
     * @return the destinationMdn
     */
    public String getDestinationMdn() {
        return container.getDestinationMdn();
    }

    private String encryptString(String str) {
        if (str == null) {
            return null;
        }
        try {
            return new String(CryptoService.binToHex(CryptoService.encryptWithAES(container.getAESKey(), str.getBytes(Constants.UTF_8))));
        } catch (Exception ex) {
        }
        return null;
    }

    private String encryptWithOTP(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] strBytes = str.getBytes(Constants.UTF_8);
            String smdn = this.container.getSourceMdn();
            smdn = StringUtil.normalizeMDN(smdn);
            char[] pwd = CryptoService.generateHash(smdn, this.container.getOTP());
            byte[] salt = {0, 0, 0, 0, 0, 0, 0, 0};
            return new String(CryptoService.binToHex(CryptoService.encryptWithPBE(strBytes, pwd, salt, 20)));
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * @return the sourcePin
     */
    public String getSourcePin() {
        return encryptString(container.getSourcePin());
    }

    /**
     * @return the Amount
     */
    public String getAmount() {
        return encryptString(container.getAmount());
    }

    /**
     * @return the TransferId
     */
    public String getTransferId() {
        return encryptString(container.getTransferId());
    }

    /**
     * @return the ParentTxnId
     */
    public String getParentTxnId() {
        return encryptString(container.getParentTxnId());
    }

    /**
     * @return the SecretAnswer
     */
    public String getSecretAnswer() {
        return encryptString(container.getSecretAnswer());
    }

    /**
     * @return the OldPin
     */
    public String getOldPin() {
        return encryptString(container.getOldPin());
    }

    /**
     * @return the NewPin
     */
    public String getNewPin() {
        return encryptString(container.getNewPin());
    }

    /**
     * @return the ConfirmPin
     */
    public String getConfirmPin() {
        return encryptString(container.getConfirmPin());
    }

    /**
     * @return the OTP
     */
    public String getOTP() {
        return encryptWithOTP(Constants.ZEROES_STRING);
    }

    /**
     * @return the TransactionID
     */
    public String getTransactionID() {
        return encryptString(container.getTransactionID());
    }

    /**
     * @return the Transactioncharges
     */
    public String getTransactioncharges() {
        return encryptString(container.getTransactioncharges());
    }

    /**
     * @return the creditAmount
     */
    public String getCreditAmount() {
        return encryptString(container.getCreditAmount());
    }

    /**
     * @return the debitAmount
     */
    public String getDebitAmount() {
        return encryptString(container.getDebitAmount());
    }

    /**
     * @return the authenticationString
     */
    public String getAuthenticationString() {
        return container.getAuthenticationString();
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return container.getSalt();
    }

    /**
     * @return the confirmed
     */
    public String getConfirmed() {
        return container.getConfirmed();
    }

    /**
     * @return the sourcePocketCode
     */
    public String getSourcePocketCode() {
        return container.getSourcePocketCode();
    }

    /**
     * @return the destinationPocketCode
     */
    public String getDestinationPocketCode() {
        return container.getDestinationPocketCode();
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return container.getServiceName();
    }

    /**
     * @return the keyParameter
     */
    public KeyParameter getKeyParameter() {
        return container.getAESKey();
    }

    /**
     * @return the TransactionName
     */
    public String getTransactionName() {
        return container.getTransactionName();
    }

    /**
     * @return the merchantCode
     */
    public String getPartnerCode() {
        return container.getPartnerCode();
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return container.getFirstName();
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return container.getLastName();
    }

    /**
     * @return the applicationID
     */
    public String getApplicationID() {
        return container.getApplicationID();
    }

    /**
     * @return the SubscriberMDN
     */
    public String getSubscriberMDN() {
        return container.getSubscriberMDN();
    }

    /**
     * @return the DateOfBirth
     */
    public String getDateOfBirth() {
        return encryptString(container.getDateOfBirth());
    }

    /**
     * @return the AccountType
     */
    public String getAccountType() {
        return encryptString(container.getAccountType());
    }

    /**
     * @return the ConfirmPin
     */
    public String getActivationConfirmPin() {
        return encryptWithOTP(container.getActivationConfirmPin());
    }

    public String getActivationNewPin() {
        return encryptWithOTP(container.getActivationNewPin());
    }
}