package com.mfino.handset.subscriber.ui.youraccount.billpay;

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

/**
 * @author sasidhar
 *
 */
public class BillPayAmountForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form amountForm;
    private TextField amount;
    
	public BillPayAmountForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		amountForm = new Form("Enter Amount");
		amount = new TextField("Amount", "", 14, TextField.NUMERIC);		
		amountForm.append(amount);
		amountForm.addCommand(mFinoConfigData.backCommand);
		amountForm.addCommand(mFinoConfigData.nextCommand);
		amountForm.setCommandListener(this);
        display.setCurrent(amountForm);
	}
	
	public BillPayAmountForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){

			if (amount.getString().equals("")) {
                alert = new Alert("Error", "Amount cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setAmount(amount.getString());
				new BillPayPinForm(mFinoConfigData, displayable);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}

