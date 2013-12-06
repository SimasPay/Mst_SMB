package com.mfino.handset.subscriber.ui.youraccount.activation;

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

public class ActivationSecretAnswerForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form activationSecretAnswerForm;
    private TextField secretAnswer;
    
	public ActivationSecretAnswerForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter OTP");
	}
	
	public ActivationSecretAnswerForm(MfinoConfigData mFinoConfigData, Displayable parent, String label){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		activationSecretAnswerForm = new Form(label);
		secretAnswer = new TextField("Enter OTP", "", 14, TextField.PASSWORD);		
		activationSecretAnswerForm.append(secretAnswer);
		activationSecretAnswerForm.addCommand(mFinoConfigData.backCommand);
		activationSecretAnswerForm.addCommand(mFinoConfigData.nextCommand);
		activationSecretAnswerForm.setCommandListener(this);
        display.setCurrent(activationSecretAnswerForm);
	}
	
	public ActivationSecretAnswerForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			
			if (secretAnswer.getString().equals("")) {
                alert = new Alert("Error", "Secret Answer cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setSecretAnswer(secretAnswer.getString());
				new ActivationPinForm(mFinoConfigData, displayable);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
