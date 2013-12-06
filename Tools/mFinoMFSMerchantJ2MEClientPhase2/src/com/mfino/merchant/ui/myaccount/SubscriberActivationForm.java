package com.mfino.merchant.ui.myaccount;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.merchant.ui.AbstractMfinoConfig;
import com.mfino.handset.merchant.util.MfinoConfigData;
import com.mfino.handset.merchant.util.ResponseData;
import com.mfino.handset.merchant.util.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 */
public class SubscriberActivationForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    
    private Form mdnForm = new Form("Enter Mobile#");
    private TextField mdn = new TextField("Mobile#", "", 16, TextField.DECIMAL);

    private Form secretAnswerForm = new Form("Secret Answer");
    private TextField secretAnswer = new TextField("Secret Answer", "", 6, TextField.PASSWORD);
    
    private Form newPinForm = new Form("Enter New Pin");
    private TextField newPin = new TextField("New Pin", "", 6, TextField.PASSWORD);

    private Form confirmNewPinForm = new Form("Confirm Pin");
    private TextField confirmNewPin = new TextField("Confirm Pin", "", 6, TextField.PASSWORD);
    
    private Form receiptForm = new Form("Receipt");
    private StringItem receiptMessage = new StringItem("", "");
    
    private String confirmString = "";
    private String receiptString = "";
    
	public SubscriberActivationForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		
		mdnForm.append(mdn);
		mdnForm.addCommand(mFinoConfigData.backCommand);
		mdnForm.addCommand(mFinoConfigData.nextCommand);
		mdnForm.setCommandListener(this);
		
		newPinForm.append(newPin);
		newPinForm.addCommand(mFinoConfigData.backCommand);
		newPinForm.addCommand(mFinoConfigData.nextCommand);
		newPinForm.setCommandListener(this);
		
		confirmNewPinForm.append(confirmNewPin);
		confirmNewPinForm.addCommand(mFinoConfigData.backCommand);
		confirmNewPinForm.addCommand(mFinoConfigData.nextCommand);
		confirmNewPinForm.setCommandListener(this);
		
		secretAnswerForm.append(secretAnswer);
		secretAnswerForm.addCommand(mFinoConfigData.backCommand);
		secretAnswerForm.addCommand(mFinoConfigData.nextCommand);
		secretAnswerForm.setCommandListener(this);
		
		receiptForm.append(receiptMessage);
		receiptForm.addCommand(mFinoConfigData.exitCommand);
		receiptForm.addCommand(mFinoConfigData.menuCommand);
		receiptForm.setCommandListener(this);
		
        display.setCurrent(mdnForm);
	}
	
	public SubscriberActivationForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		
		if(displayable == mdnForm){
			if(command == mFinoConfigData.nextCommand){
				if (mdn.getString().equals("")) {
	                alert = new Alert("Error", "Mobile# cannot be empty", null, AlertType.ERROR);
	                alert.setTimeout(Alert.FOREVER);
	                display.setCurrent(alert);
	            }
				
				display.setCurrent(secretAnswerForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(parent);
			}
		} else if (displayable == secretAnswerForm){
			if(command == mFinoConfigData.nextCommand){
				if (secretAnswer.getString().equals("")) {
	                alert = new Alert("Error", "Secret answer cannot be empty", null, AlertType.ERROR);
	                alert.setTimeout(Alert.FOREVER);
	                display.setCurrent(alert);
	            }
				
				display.setCurrent(newPinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(mdnForm);
			}
		} else if (displayable == newPinForm){
			if (newPin.getString().equals("")) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(confirmNewPinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(secretAnswerForm);
			}
		} else if (displayable == confirmNewPinForm){
			if(command == mFinoConfigData.nextCommand){
				if (confirmNewPin.getString().equals("")) {
	                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
	                alert.setTimeout(Alert.FOREVER);
	                display.setCurrent(alert);
	            }
				
				setValues();
				
				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
				receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();
				receiptMessage.setText(receiptString);
				
				display.setCurrent(receiptForm);
				
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(newPinForm);
			}
		} else if (displayable == receiptForm){
			if(command == mFinoConfigData.exitCommand){
				mFinoConfigData.getMobileBankingMidlet().startApp();
			} else if(command == mFinoConfigData.menuCommand){
				display.setCurrent(mFinoConfigData.getMobileBankingMenuDisplay());
			}			
		}
	}
	
	private void setValues(){

		mFinoConfigData.setServiceName("Activation");
		mFinoConfigData.setSourceMdn(mdn.getString().trim());
        mFinoConfigData.setNewPin(newPin.getString().trim());
        mFinoConfigData.setConfirmPin(confirmNewPin.getString().trim());
        mFinoConfigData.setSourcePin(newPin.getString().trim());
        mFinoConfigData.setSecretAnswer(secretAnswer.getString().trim());
	}
}
