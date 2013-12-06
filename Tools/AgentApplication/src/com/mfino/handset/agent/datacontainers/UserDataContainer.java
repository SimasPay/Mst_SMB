package com.mfino.handset.agent.datacontainers;

import com.mfino.handset.agent.eaZyMoneyAgent;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import javax.microedition.lcdui.Command;


import org.bouncycastle.crypto.params.KeyParameter;

/**
 * @author sasidhar
 */
public class UserDataContainer {

    private eaZyMoneyAgent mobileBankingMidlet;
    private Display mobileBankingMenuDisplay;
    private String sourceMdn;
    private String destinationMdn;
    private String sourcePin;
    private String amount;
    private String transferId;
    private String parentTxnId;
    private String secretAnswer;
    private String oldPin;
    private String newPin;
    private String confirmPin;
    private String otp;
    private String TransactionID;
    private String Transactioncharges;
    private String creditAmount;
    private String debitAmount;
    private String authenticationString;
    private String salt;
    private String confirmed;
    private String channelId;
    private String sourcePocketCode;
    private String destinationPocketCode;
    private String serviceName;
    private KeyParameter keyParameter;
    private String TransactionName;
    private String merchantCode;
    private CustomizedForm previousFormHolder;
    private String firstName;
    private String lastName;
    private String applicationID;
    private String SubscriberMDN;
    private String DateOfBirth;
    private String AccountType;
    private String ResponseMessage;
    private String ActivationNewPin;
    private String ActivationConfirmPin;
    private CustomizedForm homePage;
    private boolean loginResult;
    private Dialog presentDialog;
    
    public Command nextCommand = new Command("Next", Command.OK, 1);
    public Command exitCommand = new Command("Exit", Command.ITEM, 2);
    public Command sendCommand = new Command("Send", Command.ITEM, 1);
    public Command yesCommand = new Command("Yes", Command.ITEM, 1);
    public Command cancelCommand = new Command("Cancel", Command.ITEM, 1);
    public Command continueCommand = new Command("Continue", Command.ITEM, 1);
    public Command menuCommand = new Command("Menu", Command.ITEM, 1);
    public Command purchaseCommand = new Command("Purchase", Command.ITEM, 1);
    public Command activationCommand = new Command("Activate", Command.ITEM, 1);
    public Command loginCommand = new Command("Login", Command.ITEM, 1);
    
    
    public String getSourceMdn() {
        return sourceMdn;
    }

    public void setSourceMdn(String sourceMdn) throws Exception {
        this.sourceMdn = sourceMdn;
    }

    public String getDestinationMdn() {
        return destinationMdn;
    }

    public void setDestinationMdn(String destinationMdn) {
        this.destinationMdn = destinationMdn;
    }

    public String getSourcePin() {
        return sourcePin;
    }

    public void setSourcePin(String sourcePin) {
        this.sourcePin = sourcePin;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSourcePocketCode() {
        return sourcePocketCode;
    }

    public void setSourcePocketCode(String sourcePocketCode) {
        this.sourcePocketCode = sourcePocketCode;
    }

    public String getDestinationPocketCode() {
        return destinationPocketCode;
    }

    public void setDestinationPocketCode(String destinationPocketCode) {
        this.destinationPocketCode = destinationPocketCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getParentTxnId() {
        return parentTxnId;
    }

    public void setParentTxnId(String parentTxnId) {
        this.parentTxnId = parentTxnId;
    }

    public eaZyMoneyAgent getMobileBankingMidlet() {
        return mobileBankingMidlet;
    }

    public void setMobileBankingMidlet(eaZyMoneyAgent mobileBankingMidlet) {
        this.mobileBankingMidlet = mobileBankingMidlet;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getOldPin() {
        return oldPin;
    }

    public void setOldPin(String oldPin) {
        this.oldPin = oldPin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getConfirmPin() {
        return confirmPin;
    }

    public void setConfirmPin(String confirmPin) {
        this.confirmPin = confirmPin;
    }

    public Display getMobileBankingMenuDisplay() {
        return mobileBankingMenuDisplay;
    }

    public void setMobileBankingMenuDisplay(Display mobileBankingMenuDisplay) {
        this.mobileBankingMenuDisplay = mobileBankingMenuDisplay;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAuthenticationString() {
        return authenticationString;
    }

    public void setAuthenticationString(String authenticationString) {
        this.authenticationString = authenticationString;
    }

    public KeyParameter getAESKey() {
        return keyParameter;
    }

    public void setAESKey(byte[] kps) {
        if (kps == null) {
            this.keyParameter = null;
            return;
        }

        this.keyParameter = new KeyParameter(kps);
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the applicationID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * @param applicationID the applicationID to set
     */
    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * @return the SubscriberMDN
     */
    public String getSubscriberMDN() {
        return SubscriberMDN;
    }

    /**
     * @param SubscriberMDN the SubscriberMDN to set
     */
    public void setSubscriberMDN(String SubscriberMDN) {
        this.SubscriberMDN = SubscriberMDN;
    }

    /**
     * @return the DateOfBirth
     */
    public String getDateOfBirth() {
        return DateOfBirth;
    }

    /**
     * @param DateOfBirth the DateOfBirth to set
     */
    public void setDateOfBirth(String DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }

    /**
     * @return the AccountType
     */
    public String getAccountType() {
        return AccountType;
    }

    /**
     * @param AccountType the AccountType to set
     */
    public void setAccountType(String AccountType) {
        this.AccountType = AccountType;
    }

    /**
     * @return the TransactionName
     */
    public String getTransactionName() {
        return TransactionName;
    }

    /**
     * @param TransactionName the TransactionName to set
     */
    public void setTransactionName(String TransactionName) {
        this.TransactionName = TransactionName;
    }

    /**
     * @return the loginResult
     */
    public boolean isLoginResult() {
        return loginResult;
    }

    /**
     * @param loginResult the loginResult to set
     */
    public void setLoginResult(boolean loginResult) {
        this.loginResult = loginResult;
    }

    /**
     * @return the previousForm
     */
    public CustomizedForm getPreviousFormHolder() {
        return previousFormHolder;
    }

    /**
     * @param previousForm the previousForm to set
     */
    public void setPreviousFormHolder(CustomizedForm previousForm) {
        this.previousFormHolder = previousForm;
    }

//    /**
//     * @return the nextForm
//     */
//    public CustomizedForm getNextForm() {
//        return nextForm;
//    }
//
//    /**
//     * @param nextForm the nextForm to set
//     */
//    public void setNextForm(Form nextForm) {
//        this.nextForm = nextForm;
//    }
    /**
     * @return the ResponseMessage
     */
    public String getResponseMessage() {
        return ResponseMessage;
    }

    /**
     * @param ResponseMessage the ResponseMessage to set
     */
    public void setResponseMessage(String ResponseMessage) {
        this.ResponseMessage = ResponseMessage;
    }

    /**
     * @return the merchantCode
     */
    public String getPartnerCode() {
        return merchantCode;
    }

    /**
     * @param merchantCode the merchantCode to set
     */
    public void setPartnerCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    /**
     * @return the otp
     */
    public String getOTP() {
        return otp;
    }

    /**
     * @param otp the otp to set
     */
    public void setOTP(String otp) {
        this.otp = otp;
    }

    /**
     * @return the Transactioncharges
     */
    public String getTransactioncharges() {
        return Transactioncharges;
    }

    /**
     * @param Transactioncharges the Transactioncharges to set
     */
    public void setTransactioncharges(String Transactioncharges) {
        this.Transactioncharges = Transactioncharges;
    }

    /**
     * @return the TransactionID
     */
    public String getTransactionID() {
        return TransactionID;
    }

    /**
     * @param TransactionID the TransactionID to set
     */
    public void setTransactionID(String TransactionID) {
        this.TransactionID = TransactionID;
    }

    /**
     * @return the netCahsinAmount
     */
    public String getCreditAmount() {
        return creditAmount;
    }

    /**
     * @param netCahsinAmount the netCahsinAmount to set
     */
    public void setCreditAmount(String netCahsinAmount) {
        this.creditAmount = netCahsinAmount;
    }

    /**
     * @return the debitAmount
     */
    public String getDebitAmount() {
        return debitAmount;
    }

    /**
     * @param debitAmount the debitAmount to set
     */
    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    /**
     * @return the homePage
     */
    public CustomizedForm getHomePage() {
        return homePage;
    }

    /**
     * @param homePage the homePage to set
     */
    public void setHomePage(CustomizedForm homePage) throws Exception {

        if (this.homePage == null) {
            this.homePage = homePage;
        } else {
            throw new Exception("home page already set");
        }
    }

    public String getActivationNewPin() {
        return this.ActivationNewPin;
    }

    public void setActivationNewPin(String ActivationNewPin) {
        this.ActivationNewPin = ActivationNewPin;
    }

    public String getActivationConfirmPin() {
        return this.ActivationConfirmPin;
    }

    public void setActivationConfirmPin(String ActivationConfirmPin) {
        this.ActivationConfirmPin = ActivationConfirmPin;
    }

    /**
     * @return the presentDialog
     */
    public Dialog getPresentDialog() {
        return presentDialog;
    }

    /**
     * @param presentDialog the presentDialog to set
     */
    public void setPresentDialog(Dialog presentDialog) {
        this.presentDialog = presentDialog;
    }
    
    
}
