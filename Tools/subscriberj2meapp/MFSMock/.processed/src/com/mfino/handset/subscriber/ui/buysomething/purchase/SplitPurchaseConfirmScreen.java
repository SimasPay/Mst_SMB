package com.mfino.handset.subscriber.ui.buysomething.purchase;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 *
 */
public class SplitPurchaseConfirmScreen extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form topUpconfirmForm;
    private StringItem stringItem;
    
	public SplitPurchaseConfirmScreen(MfinoConfigData mFinoConfigData, Displayable parent, String confirmMessage)
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
			
			new SplitPurchasePinForm(mFinoConfigData, displayable);
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
