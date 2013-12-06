package com.mfino.handset.subscriber.ui.manageyourmoney.history;

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

public class MYMHistoryPinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form mymHistoryPinForm;
    private TextField pin;
    
	public MYMHistoryPinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter PIN");
	}
	
	public MYMHistoryPinForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		mymHistoryPinForm = new Form(pinLabel);
		pin = new TextField(pinLabel, "", 14, TextField.PASSWORD);		
		mymHistoryPinForm.append(pin);
		mymHistoryPinForm.addCommand(mFinoConfigData.backCommand);
		mymHistoryPinForm.addCommand(mFinoConfigData.nextCommand);
		mymHistoryPinForm.setCommandListener(this);
        display.setCurrent(mymHistoryPinForm);
	}
	
	public MYMHistoryPinForm(MfinoConfigData mFinoConfigData)
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
				mFinoConfigData.setSourcePin(pin.getString().trim());
				
				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
				if("4".equals(responseData.getMsgCode())){
					receiptMessage = responseData.getMsg();
				}
				else if("Currency".equals(responseData.getMsgCode())){
					receiptMessage = "E-Money Balance "+responseData.getMsg() + " on "+responseData.getTransactionTime();
				}
				else{
					receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg();
				}
				
				new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
