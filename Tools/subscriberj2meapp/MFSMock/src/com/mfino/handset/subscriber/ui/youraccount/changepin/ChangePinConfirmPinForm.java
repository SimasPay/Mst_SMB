package com.mfino.handset.subscriber.ui.youraccount.changepin;

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

public class ChangePinConfirmPinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form confirmPinForm;
    private TextField confirmPin;
    
	public ChangePinConfirmPinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		confirmPinForm = new Form("Change PIN");
		confirmPin = new TextField("Confirm New PIN", "", 14, TextField.PASSWORD);		
		confirmPinForm.append(confirmPin);
		confirmPinForm.addCommand(mFinoConfigData.backCommand);
		confirmPinForm.addCommand(mFinoConfigData.nextCommand);
		confirmPinForm.setCommandListener(this);
        display.setCurrent(confirmPinForm);
	}
	
	public ChangePinConfirmPinForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			
			if (confirmPin.getString().equals("")) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setConfirmPin(confirmPin.getString().trim());

				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
			
				String receiptMessage = responseData.getMsgCode() + ":" + responseData.getMsg();
				
				new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
