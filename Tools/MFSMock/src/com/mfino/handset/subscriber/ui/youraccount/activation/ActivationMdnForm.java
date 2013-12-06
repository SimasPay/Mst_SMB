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
import com.mfino.handset.subscriber.util.MfinoConfigData;

public class ActivationMdnForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form activationMdnForm;
    private TextField mdn;
    
	public ActivationMdnForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter MDN");
	}
	
	public ActivationMdnForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		activationMdnForm = new Form(pinLabel);
		mdn = new TextField(pinLabel, "", 16, TextField.DECIMAL);		
		activationMdnForm.append(mdn);
		activationMdnForm.addCommand(mFinoConfigData.backCommand);
		activationMdnForm.addCommand(mFinoConfigData.nextCommand);
		activationMdnForm.setCommandListener(this);
        display.setCurrent(activationMdnForm);
	}
	
	public ActivationMdnForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			
			if (mdn.getString().equals("")) {
                alert = new Alert("Error", "MDN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setSourceMdn(mdn.getString().trim());
				new ActivationSecretAnswerForm(mFinoConfigData, displayable);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
