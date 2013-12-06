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
public class LoanAccountNumberForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form loanAccountNoForm;
    private TextField loanAccountNumber;
    
	public LoanAccountNumberForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		loanAccountNoForm = new Form("Enter Bill Id");
		loanAccountNumber = new TextField("Loan Account#", "", 14, TextField.NUMERIC);		
		loanAccountNoForm.append(loanAccountNumber);
		loanAccountNoForm.addCommand(mFinoConfigData.backCommand);
		loanAccountNoForm.addCommand(mFinoConfigData.nextCommand);
		loanAccountNoForm.setCommandListener(this);
        display.setCurrent(loanAccountNoForm);
	}
	
	public LoanAccountNumberForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){

			if (loanAccountNumber.getString().equals("")) {
                alert = new Alert("Error", "Loan Account# cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setAmount(loanAccountNumber.getString());
				new BillPayAmountForm(mFinoConfigData, displayable);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}

