package com.mfino.handset.subscriber.ui.youraccount.resetpin;

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
public class ResetPinNewPinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form resetPinForm;
    private TextField newPin;
    
	public ResetPinNewPinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		resetPinForm = new Form("Reset PIN");
		newPin = new TextField("New Pin", "", 14, TextField.PASSWORD);
		resetPinForm.append(newPin);
		resetPinForm.addCommand(mFinoConfigData.backCommand);
		resetPinForm.addCommand(mFinoConfigData.nextCommand);
		resetPinForm.setCommandListener(this);
        display.setCurrent(resetPinForm);
	}
	
	public ResetPinNewPinForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			
			if (newPin.getString().equals("")){
                alert = new Alert("Error", "New Pin is required.", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setNewPin(newPin.getString().trim());
				
				WebAPIHTTPWrapper webAPIHTTPWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webAPIHTTPWrapper.getResponseData();
				
				String receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg();
				
				new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
