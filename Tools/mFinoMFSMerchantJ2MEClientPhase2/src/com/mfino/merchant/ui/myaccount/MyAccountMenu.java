package com.mfino.merchant.ui.myaccount;

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
public class MyAccountMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form walletForm;
    private ChoiceGroup choiceGroup;
    
	private static final String CHECK_BALANCE = "Check Balance";
    private static final String HISTORY = "History";
    private static final String PIN_MANAGEMENT = "Pin Management";
    private static final String BUSINESS_REPORT = "Business Report";
    private static final String ACTIVATION = "Subscriber Activation";
        
    private static final String[] yourAccountMenu = {CHECK_BALANCE, HISTORY, PIN_MANAGEMENT, BUSINESS_REPORT, ACTIVATION};
    
	public MyAccountMenu(MfinoConfigData mFinoConfigData, Displayable parent){
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
            
            if (name.equals(CHECK_BALANCE)) {
            	new CheckBalanceForm(mFinoConfigData, displayable);
            } else if (name.equals(HISTORY)){
            	new HistoryForm(mFinoConfigData, displayable);
            } else if (name.equals(PIN_MANAGEMENT)){
            	new PinManagementForm(mFinoConfigData, displayable);
            } else if (name.equals(BUSINESS_REPORT)){
            	new BusinessReportForm(mFinoConfigData, displayable);
            } else if (name.equals(ACTIVATION)){
            	new SubscriberActivationForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
