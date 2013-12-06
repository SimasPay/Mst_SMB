package com.mfino.handset.subscriber.ui.manageyourmoney.transfer;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.youraccount.topup.TopUpReceiptScreen;
import com.mfino.handset.subscriber.util.MfinoConfigData;
import com.mfino.handset.subscriber.util.ResponseData;
import com.mfino.handset.subscriber.util.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 *
 */
public class MYMTransferPinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form pinForm;
    private TextField pin;
    
	public MYMTransferPinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter PIN");
	}
	
	public MYMTransferPinForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		pinForm = new Form(pinLabel);
		pin = new TextField(pinLabel, "", 14, TextField.PASSWORD);		
		pinForm.append(pin);
		pinForm.addCommand(mFinoConfigData.backCommand);
		pinForm.addCommand(mFinoConfigData.nextCommand);
		pinForm.setCommandListener(this);
        display.setCurrent(pinForm);
	}
	
	public MYMTransferPinForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			String receiptMessage = "";
			
			if (pin.getString().equals("")) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setSourcePin(pin.getString());
				mFinoConfigData.setServiceName("transferInquiry");
				
				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
                if (responseData.getParentTxnId() == null || responseData.getTransferId() == null) {
                	receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg(); 
                    new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
                }
                else{
                	mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
                	mFinoConfigData.setTransferId(responseData.getTransferId());
                	
                	String sourcePocketString = "12".equals(mFinoConfigData.getSourcePocketCode()) ? "E-Money Account" : "Bank Account";
                	String destinationPocketString = "12".equals(mFinoConfigData.getDestinationPocketCode()) ? "E-Money Account" : "Bank Account";
                	
                	String destinationAccountString = "";
                	if(mFinoConfigData.isYourWallet()){
                		destinationAccountString = "to your "+destinationPocketString;
                	} else{
                		destinationAccountString = "to "+mFinoConfigData.getDestinationMdn() + " "+destinationPocketString;
                	}
                	
                	String confirmMessage = "Do you want to trasfer "+mFinoConfigData.getAmount() + " from your "+sourcePocketString + " " + destinationAccountString;
                	new MYMTransferConfirmScreen(mFinoConfigData, displayable, confirmMessage);
                }
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
