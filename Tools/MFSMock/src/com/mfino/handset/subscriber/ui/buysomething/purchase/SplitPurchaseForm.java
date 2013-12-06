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

public class SplitPurchaseForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form splitPurchasePinForm;
    private TextField friendOnePhone;
    private TextField friendTwoPhone;
    
	public SplitPurchaseForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter PIN");
	}
	
	public SplitPurchaseForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		splitPurchasePinForm = new Form(pinLabel);
		friendOnePhone = new TextField("Enter Friend One Phone#", "", 14, TextField.DECIMAL);		
		friendTwoPhone = new TextField("Enter Friend Two Phone#", "", 14, TextField.DECIMAL);
		splitPurchasePinForm.append(friendOnePhone);
		splitPurchasePinForm.append(friendTwoPhone);
		splitPurchasePinForm.addCommand(mFinoConfigData.backCommand);
		splitPurchasePinForm.addCommand(mFinoConfigData.continueCommand);
		splitPurchasePinForm.setCommandListener(this);
        display.setCurrent(splitPurchasePinForm);
	}
	
	public SplitPurchaseForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		String confirmMessage = "";
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.continueCommand){
			
			if((friendOnePhone.getString().equals("")) && (friendTwoPhone.getString().equals(""))) {
                alert = new Alert("Error", "Atleast one phone number is required", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setFriendOnePhoneNumber(friendOnePhone.getString().trim());
				mFinoConfigData.setFriendTwoPhoneNumber(friendTwoPhone.getString().trim());
				
				if((null != mFinoConfigData.getFriendOnePhoneNumber()) && !("".equals(mFinoConfigData.getFriendOnePhoneNumber()))){
					mFinoConfigData.setFriendOne(true);
				}
				if((null != mFinoConfigData.getFriendTwoPhoneNumber()) && !("".equals(mFinoConfigData.getFriendTwoPhoneNumber()))){
					mFinoConfigData.setFriendTwo(true);
				}
				
				if(!(mFinoConfigData.isFriendOne()) && (mFinoConfigData.isFriendTwo())){
					mFinoConfigData.setFriendOnePhoneNumber(mFinoConfigData.getFriendTwoPhoneNumber());
					mFinoConfigData.setFriendTwoPhoneNumber(null);
					mFinoConfigData.setFriendOne(true);
					mFinoConfigData.setFriendTwo(false);
				}
				
				mFinoConfigData.setFriendOnePinConfirmed(false);
				mFinoConfigData.setFriendTwoPinConfirmed(false);
				
				confirmMessage = "Do you want to evenly split the bill between you";
				
				if((mFinoConfigData.isFriendOne()) && (mFinoConfigData.isFriendTwo())){
					confirmMessage = confirmMessage + ", " + mFinoConfigData.getFriendOnePhoneNumber() + ", and " + mFinoConfigData.getFriendTwoPhoneNumber() + "?";
					confirmMessage = confirmMessage + "\n" + "(Each pays $7)";
				}
				else{
					confirmMessage = confirmMessage + " and " + mFinoConfigData.getFriendOnePhoneNumber() + "?";
					confirmMessage = confirmMessage + "\n" + "(Each pays $10.5)";
				}
				
				new SplitPurchaseConfirmScreen(mFinoConfigData, displayable,confirmMessage);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
