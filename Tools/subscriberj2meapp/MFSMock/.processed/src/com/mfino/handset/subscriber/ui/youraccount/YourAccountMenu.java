package com.mfino.handset.subscriber.ui.youraccount;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.manageyourmoney.checkbalance.MYMCheckBalanceMenu;
import com.mfino.handset.subscriber.ui.manageyourmoney.history.MYMHistoryMenu;
import com.mfino.handset.subscriber.ui.manageyourmoney.transfer.MYMTransferFromPocketMenu;
import com.mfino.handset.subscriber.util.Constants;
import com.mfino.handset.subscriber.util.MfinoConfigData;

public class YourAccountMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form yourAccountForm;
    private ChoiceGroup choiceGroup;
    
    private static final String CHECK_BALANCE = "Check Balance";
	private static final String HISTORY = "History";
	private static final String PIN_MANAGEMENT = "Pin Management";
	private static final String FUND = "Fund";
	private static final String CASH_IN = "Cash In";
	private static final String CASH_OUT = "Cash Out";
    
    private static final String[] yourAccountMenu = {CHECK_BALANCE, HISTORY, PIN_MANAGEMENT, FUND, CASH_IN, CASH_OUT};
    
	public YourAccountMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        yourAccountForm = new Form("Buy");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        yourAccountForm.append(choiceGroup);
        yourAccountForm.addCommand(mFinoConfigData.backCommand);
        yourAccountForm.addCommand(mFinoConfigData.nextCommand);
        yourAccountForm.setCommandListener(this);
        display.setCurrent(yourAccountForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(CHECK_BALANCE)) {
            	new MYMCheckBalanceMenu(mFinoConfigData, displayable);
            } else if (name.equals(HISTORY)) {
            	new MYMHistoryMenu(mFinoConfigData, displayable);
            } else if (name.equals(PIN_MANAGEMENT)){
            	new PinManagementMenu(mFinoConfigData, displayable);
            } else if (name.equals(FUND)){
            	mFinoConfigData.setYourPhone(true);
            	mFinoConfigData.setYourWallet(true);
            	mFinoConfigData.setDestinationMdn(mFinoConfigData.getSourceMdn());
            	new MYMTransferFromPocketMenu(mFinoConfigData, displayable);
            } else if (name.equals(CASH_IN)){
            	mFinoConfigData.setServiceName(Constants.CASH_IN_INQUIRY);
            	new CashForm(mFinoConfigData, displayable);
            } else if (name.equals(CASH_OUT)){
            	mFinoConfigData.setServiceName(Constants.CASH_OUT_INQUIRY);
            	new CashForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}

