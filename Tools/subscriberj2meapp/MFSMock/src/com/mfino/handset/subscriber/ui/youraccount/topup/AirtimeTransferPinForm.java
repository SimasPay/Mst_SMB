package com.mfino.handset.subscriber.ui.youraccount.topup;

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
import com.mfino.handset.subscriber.util.ResponseData;
import com.mfino.handset.subscriber.util.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 *
 */
public class AirtimeTransferPinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form pinForm;
    private TextField pin;
    
	public AirtimeTransferPinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter PIN");
	}
	
	public AirtimeTransferPinForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
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
	
	public AirtimeTransferPinForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			String confirmMessage = "";
			
			if (pin.getString().equals("")) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setSourcePin(pin.getString());
				
				confirmMessage = "Do you want to transfer "+mFinoConfigData.getAmount().trim() + " airtime to "+mFinoConfigData.getDestinationMdn();
				
				new AirtimeTransferConfirmScreen(mFinoConfigData, displayable, confirmMessage);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
