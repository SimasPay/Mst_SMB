package com.mfino.handset.subscriber.ui.manageyourmoney;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.manageyourmoney.billpay.MYMBillPaySelectBillMenu;
import com.mfino.handset.subscriber.ui.manageyourmoney.checkbalance.MYMCheckBalanceMenu;
import com.mfino.handset.subscriber.ui.manageyourmoney.history.MYMHistoryMenu;
import com.mfino.handset.subscriber.ui.manageyourmoney.transfer.MYMTransferMWalletMenu;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class ManageYourMoneyMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form manageYourMoneyForm;
    private ChoiceGroup choiceGroup;
    
	private static final String CHECK_BALANCE = "Check Balance";
    private static final String HISTORY = "History";
    private static final String TRANSFER = "Transfer";
    private static final String BILL_PAY = "Bill Pay";
    
    private static final String[] yourAccountMenu = {CHECK_BALANCE, HISTORY, TRANSFER, BILL_PAY};
    
	public ManageYourMoneyMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        manageYourMoneyForm = new Form("Manage Your Money");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        manageYourMoneyForm.append(choiceGroup);
        manageYourMoneyForm.addCommand(mFinoConfigData.backCommand);
        manageYourMoneyForm.addCommand(mFinoConfigData.nextCommand);
        manageYourMoneyForm.setCommandListener(this);
        display.setCurrent(manageYourMoneyForm);
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
            	mFinoConfigData.setServiceName("Activation");
            	new MYMHistoryMenu(mFinoConfigData, displayable);
            } else if (name.equals(TRANSFER)) {
            	mFinoConfigData.setServiceName("transferInquiry");
            	new MYMTransferMWalletMenu(mFinoConfigData, displayable);
            } else if (name.equals(BILL_PAY)){
            	mFinoConfigData.setServiceName("billPaymentInquiry");
            	new MYMBillPaySelectBillMenu(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
