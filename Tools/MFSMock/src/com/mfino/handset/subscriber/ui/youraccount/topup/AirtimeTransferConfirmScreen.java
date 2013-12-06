package com.mfino.handset.subscriber.ui.youraccount.topup;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.MfinoConfigData;
import com.mfino.handset.subscriber.util.ResponseData;
import com.mfino.handset.subscriber.util.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 *
 */
public class AirtimeTransferConfirmScreen extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form airtimeTransferConfirmForm;
    private StringItem stringItem;
    
	public AirtimeTransferConfirmScreen(MfinoConfigData mFinoConfigData, Displayable parent, String confirmMessage)
	{
		super(mFinoConfigData);
		this.parent = parent;
		stringItem = new StringItem("", "");
		stringItem.setText(confirmMessage);
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		airtimeTransferConfirmForm = new Form("Confirm");
		airtimeTransferConfirmForm.append(stringItem);
		airtimeTransferConfirmForm.addCommand(mFinoConfigData.cancelCommand);
		airtimeTransferConfirmForm.addCommand(mFinoConfigData.continueCommand);
		airtimeTransferConfirmForm.setCommandListener(this);
        display.setCurrent(airtimeTransferConfirmForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.cancelCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.continueCommand){
			String receiptMessage = "";
			
			WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
			ResponseData responseData = webApiHttpWrapper.getResponseData();
			
			receiptMessage = responseData.getMsgCode() + ":" + responseData.getMsg();
			
			new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
