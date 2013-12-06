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
import com.mfino.handset.subscriber.ui.youraccount.topup.TopUpReceiptScreen;
import com.mfino.handset.subscriber.util.MfinoConfigData;

public class BuySomethingPinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form buySomethingPinForm;
    private TextField pin;
    
	public BuySomethingPinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter PIN");
	}
	
	public BuySomethingPinForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		buySomethingPinForm = new Form(pinLabel);
		pin = new TextField(pinLabel, "", 14, TextField.PASSWORD);		
		buySomethingPinForm.append(pin);
		buySomethingPinForm.addCommand(mFinoConfigData.backCommand);
		buySomethingPinForm.addCommand(mFinoConfigData.continueCommand);
		buySomethingPinForm.setCommandListener(this);
        display.setCurrent(buySomethingPinForm);
	}
	
	public BuySomethingPinForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.continueCommand){
			
			System.out.println("BuySomethingPinForm: "+mFinoConfigData.isFriendOne());
			System.out.println("BuySomethingPinForm: "+mFinoConfigData.isFriendTwo());

			System.out.println("BuySomethingPinForm: "+mFinoConfigData.getFriendOnePhoneNumber());
			System.out.println("BuySomethingPinForm: "+mFinoConfigData.getFriendTwoPhoneNumber());
			
			if (pin.getString().equals("")) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setSourcePin(pin.getString().trim());
				
				String receiptMessage = "";
				
				receiptMessage = "		Receipt		" + "\n" + "\n" + 
								 "Purchased $21 at Pizza Hut #556 Palo Alto " + "\n";
								 
				String billSplitMessage = "Bill split between you";
				
				if((mFinoConfigData.isFriendOne()) && (mFinoConfigData.isFriendTwo())){
					billSplitMessage = billSplitMessage + ", " + mFinoConfigData.getFriendOnePhoneNumber() + " and " + mFinoConfigData.getFriendTwoPhoneNumber();	
				}
				else if(mFinoConfigData.isFriendOne()){
					billSplitMessage = billSplitMessage + " and " + mFinoConfigData.getFriendOnePhoneNumber();
				}
				else{
					billSplitMessage = "";
				}
				
				String confirmMessage = "2011-08-26" + "\n" + "Confirm #PQ876000124568";
				
				receiptMessage = receiptMessage + billSplitMessage +  "\n\n" + confirmMessage;
				
				new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
