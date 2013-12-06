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
public class BillIdForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form billIdForm;
    private TextField billId;
    
	public BillIdForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		billIdForm = new Form("Enter Bill Id");
		billId = new TextField("Bill Id#", "", 14, TextField.NUMERIC);		
		billIdForm.append(billId);
		billIdForm.addCommand(mFinoConfigData.backCommand);
		billIdForm.addCommand(mFinoConfigData.nextCommand);
		billIdForm.setCommandListener(this);
        display.setCurrent(billIdForm);
	}
	
	public BillIdForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){

			if (billId.getString().equals("")) {
                alert = new Alert("Error", "Bill Id cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setAmount(billId.getString());
				new BillPayAmountForm(mFinoConfigData, displayable);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}

