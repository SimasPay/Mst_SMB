package com.mfino.handset.subscriber.ui.manageyourmoney.transfer;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class MYMTransferMWalletMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form walletForm;
    private ChoiceGroup choiceGroup;
    
//	private static final String YOUR_WALLET = "Your Wallet";
//    private static final String OTHER_WALLET = "Other Wallet";
    private static final String MONEY = "Money";
    private static final String AIRTIME = "Airtime";
    
    private static final String[] yourAccountMenu = {MONEY, AIRTIME};
    
	public MYMTransferMWalletMenu(MfinoConfigData mFinoConfigData, Displayable parent){
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
            
            if (name.equals(MONEY)) {
            	mFinoConfigData.setYourWallet(false);
            	new MYMTransferDestinationMdnForm(mFinoConfigData, displayable);
            }  else if (name.equals(AIRTIME)){
            	mFinoConfigData.setServiceName("shareLoad");
            	new AirtimeTransferMdnForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
