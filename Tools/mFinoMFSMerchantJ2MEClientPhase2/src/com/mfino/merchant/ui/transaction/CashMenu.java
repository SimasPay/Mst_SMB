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
public class CashMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form walletForm;
    private ChoiceGroup choiceGroup;
    
    private static final String CASH_IN = "Cash In";
    private static final String CASH_OUT = "Cash Out";
        
    private static final String[] yourAccountMenu = {CASH_IN, CASH_OUT};
    
	public CashMenu(MfinoConfigData mFinoConfigData, Displayable parent){
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
            
            if (name.equals(CASH_IN)) {
            	new CashInForm(mFinoConfigData, displayable);
            }  else if (name.equals(CASH_OUT)){
            	new CashOutForm(mFinoConfigData, displayable);
            } 
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
