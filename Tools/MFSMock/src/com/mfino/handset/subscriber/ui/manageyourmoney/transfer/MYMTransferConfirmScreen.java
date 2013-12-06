package com.mfino.handset.subscriber.ui.manageyourmoney.transfer;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.youraccount.topup.TopUpReceiptScreen;
import com.mfino.handset.subscriber.util.MfinoConfigData;
import com.mfino.handset.subscriber.util.ResponseData;
import com.mfino.handset.subscriber.util.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 *
 */
public class MYMTransferConfirmScreen extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form topUpconfirmForm;
    private StringItem stringItem;
    
	public MYMTransferConfirmScreen(MfinoConfigData mFinoConfigData, Displayable parent, String confirmMessage)
	{
		super(mFinoConfigData);
		this.parent = parent;
		stringItem = new StringItem("", "");
		stringItem.setText(confirmMessage);
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		topUpconfirmForm = new Form("Confirm");
		topUpconfirmForm.append(stringItem);
		topUpconfirmForm.addCommand(mFinoConfigData.cancelCommand);
		topUpconfirmForm.addCommand(mFinoConfigData.continueCommand);
		topUpconfirmForm.setCommandListener(this);
        display.setCurrent(topUpconfirmForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.cancelCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.continueCommand){
			
			mFinoConfigData.setServiceName("transfer");
			
			WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
			ResponseData responseData = webApiHttpWrapper.getResponseData();
			
        	mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
        	mFinoConfigData.setTransferId(responseData.getTransferId());
        	
/*        	String sourcePocketString = "12".equals(mFinoConfigData.getSourcePocketCode()) ? "E-Money Account" : "Bank Account";
        	String destinationPocketString = "12".equals(mFinoConfigData.getDestinationPocketCode()) ? "E-Money Account" : "Bank Account";
        	
        	String destinationAccountString = "";
        	if(mFinoConfigData.isYourWallet()){
        		destinationAccountString = "to your "+destinationPocketString;
        	} else{
        		destinationAccountString = "to "+mFinoConfigData.getDestinationMdn() + " "+destinationPocketString;
        	}*/
        	
//        	String receiptMessage = "Do you want to trasfer "+mFinoConfigData.getAmount() + " from your "+sourcePocketString + " " + destinationAccountString;
        	String receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg();
        	new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
