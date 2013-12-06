package com.mfino.merchant.ui.transaction;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.merchant.ui.AbstractMfinoConfig;
import com.mfino.handset.merchant.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class TransactionMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form walletForm;
    private ChoiceGroup choiceGroup;
    
	private static final String CASH_IN_OUT = "Cash In/Out";
    private static final String TOP_UP = "Top Up";
    private static final String PAY_BILL = "Pay Bill";
        
    private static final String[] yourAccountMenu = {CASH_IN_OUT, TOP_UP, PAY_BILL};
    
	public TransactionMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        walletForm = new Form("Select Transfer Type");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        walletForm.append(choiceGroup);
        walletForm.addCommand(mFinoConfigData.backCommand);
        walletForm.addCommand(mFinoConfigData.nextCommand);
        walletForm.setCommandListener(this);
        display.setCurrent(walletForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());
            
            if (name.equals(CASH_IN_OUT)) {
            	new CashMenu(mFinoConfigData, displayable);
            }  else if (name.equals(TOP_UP)){
            	new TopUpForm(mFinoConfigData, displayable);
            } else if (name.equals(PAY_BILL)){
            	new PayBillForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
