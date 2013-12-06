package com.mfino.handset.merchant.util;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import com.mfino.handset.merchant.midlets.MobileBankingMidlet;

/**
 * @author sasidhar
 */
public class MfinoConfigData {
	
	private MobileBankingMidlet mobileBankingMidlet;
	private Displayable mobileBankingMenuDisplay; 
	
	public static String webAPIUrl = "http://demo.mfino.com:8080/webapi/dynamic";
	
	private String sourceMdn;
	private String destinationMdn;
	private String sourcePin;
	private String destinationPin;
	private String amount;
	private String channelId;
	private String sourcePocketCode;
	private String destinationPocketCode;
	private String bucketType = "CAL";
	private String bankId;
	private String mode;
	private String serviceName;
	private String transferId;
	private String parentTxnId;
	private String secretAnswer;
	private String oldPin;
	private String newPin;
	private String confirmed;
	private String confirmPin;
	private String billerName;
	private String customerId;
	private String billDetails;
	private String friendOnePhoneNumber;
	private String friendTwoPhoneNumber;
	
    private boolean yourPhone;
    private boolean yourWallet;
    private boolean splitPurchase;
    private boolean friendOne;
    private boolean friendTwo;
    private boolean friendOnePinConfirmed;
    private boolean friendTwoPinConfirmed;
    
	public Command backCommand = new Command("Back", Command.BACK, 1);
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
	
	public void setSourceMdn(String sourceMdn) {
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
	
	public String getDestinationPin() {
		return destinationPin;
	}
	
	public void setDestinationPin(String destinationPin) {
		this.destinationPin = destinationPin;
	}
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
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
	
	public String getBucketType() {
		return bucketType;
	}
	
	public void setBucketType(String bucketType) {
		this.bucketType = bucketType;
	}
	
	public String getBankId() {
		return bankId;
	}
	
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
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

	public MobileBankingMidlet getMobileBankingMidlet() {
		return mobileBankingMidlet;
	}

	public void setMobileBankingMidlet(MobileBankingMidlet mobileBankingMidlet) {
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

	public boolean isYourPhone() {
		return yourPhone;
	}

	public void setYourPhone(boolean yourPhone) {
		this.yourPhone = yourPhone;
	}

	public String getConfirmPin() {
		return confirmPin;
	}

	public void setConfirmPin(String confirmPin) {
		this.confirmPin = confirmPin;
	}

	public boolean isYourWallet() {
		return yourWallet;
	}

	public void setYourWallet(boolean yourWallet) {
		this.yourWallet = yourWallet;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getBillDetails() {
		return billDetails;
	}

	public void setBillDetails(String billDetails) {
		this.billDetails = billDetails;
	}

	public Displayable getMobileBankingMenuDisplay() {
		return mobileBankingMenuDisplay;
	}

	public void setMobileBankingMenuDisplay(Displayable mobileBankingMenuDisplay) {
		this.mobileBankingMenuDisplay = mobileBankingMenuDisplay;
	}
	
    public String getBillerName() {
		return billerName;
	}

	public void setBillerName(String billerName) {
		this.billerName = billerName;
	}

	public boolean isSplitPurchase() {
		return splitPurchase;
	}

	public void setSplitPurchase(boolean splitPurchase) {
		this.splitPurchase = splitPurchase;
	}

	public String getFriendOnePhoneNumber() {
		return friendOnePhoneNumber;
	}

	public void setFriendOnePhoneNumber(String friendOnePhoneNumber) {
		this.friendOnePhoneNumber = friendOnePhoneNumber;
	}

	public String getFriendTwoPhoneNumber() {
		return friendTwoPhoneNumber;
	}

	public void setFriendTwoPhoneNumber(String friendTwoPhoneNumber) {
		this.friendTwoPhoneNumber = friendTwoPhoneNumber;
	}

	public boolean isFriendOne() {
		return friendOne;
	}

	public void setFriendOne(boolean friendOne) {
		this.friendOne = friendOne;
	}

	public boolean isFriendTwo() {
		return friendTwo;
	}

	public void setFriendTwo(boolean friendTwo) {
		this.friendTwo = friendTwo;
	}

	public boolean isFriendOnePinConfirmed() {
		return friendOnePinConfirmed;
	}

	public void setFriendOnePinConfirmed(boolean friendOnePinConfirmed) {
		this.friendOnePinConfirmed = friendOnePinConfirmed;
	}

	public boolean isFriendTwoPinConfirmed() {
		return friendTwoPinConfirmed;
	}

	public void setFriendTwoPinConfirmed(boolean friendTwoPinConfirmed) {
		this.friendTwoPinConfirmed = friendTwoPinConfirmed;
	}
}
