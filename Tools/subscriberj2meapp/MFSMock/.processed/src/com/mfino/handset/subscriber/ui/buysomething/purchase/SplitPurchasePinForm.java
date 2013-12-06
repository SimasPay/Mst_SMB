package com.mfino.handset.subscriber.ui.buysomething.purchase;

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

public class SplitPurchasePinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form splitPurchasePinForm;
    private TextField pin;
    
	public SplitPurchasePinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter PIN");
	}
	
	public SplitPurchasePinForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		
		pinLabel = this.mFinoConfigData.isFriendOnePinConfirmed() ? 
				"Enter PIN for " + this.mFinoConfigData.getFriendTwoPhoneNumber() : 
				"Enter PIN for " + this.mFinoConfigData.getFriendOnePhoneNumber();
		
		mFinoConfigData.setFriendOnePinConfirmed(true);
		
		splitPurchasePinForm = new Form(pinLabel);
		pin = new TextField(pinLabel, "", 14, TextField.PASSWORD);		
		splitPurchasePinForm.append(pin);
		
		splitPurchasePinForm.addCommand(mFinoConfigData.backCommand);
		splitPurchasePinForm.addCommand(mFinoConfigData.continueCommand);
		
		splitPurchasePinForm.setCommandListener(this);
        display.setCurrent(splitPurchasePinForm);
	}
	
	public SplitPurchasePinForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.continueCommand){
			
			if (pin.getString().equals("")) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				
				if((mFinoConfigData.isFriendTwo()) && !(mFinoConfigData.isFriendTwoPinConfirmed())){
					mFinoConfigData.setFriendTwoPinConfirmed(true);
					new SplitPurchasePinForm(mFinoConfigData, displayable);
				}
				else{
/*					String receiptMessage = "";
					
					receiptMessage = "		Receipt		" + "\n" + "\n" + 
									 "Purchased $21 at Pizza Hut #556 Palo Alto" + "\n" + "\n" + 
									 "		2011-08-2" + "\n" + 
									 "Confirm #PQ876000124568";
					
					new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);*/
					
					new BuySomethingPinForm(mFinoConfigData, displayable, "Enter Your PIN");
				}
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
