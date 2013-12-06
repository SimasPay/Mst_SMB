package com.mfino.handset.subscriber.ui.youraccount;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.youraccount.changepin.ChangePinOldPinForm;
import com.mfino.handset.subscriber.ui.youraccount.resetpin.ResetPinSecretAnswerForm;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class PinManagementMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form yourAccountForm;
    private ChoiceGroup choiceGroup;
    
//	private static final String TOP_UP = "Top Up";
//    private static final String ACTIVATION = "Activation";
    private static final String CHANGE_PIN = "Change Pin";
    private static final String RESET_PIN = "Reset Pin";
    
    private static final String[] yourAccountMenu = {CHANGE_PIN, RESET_PIN};
    
	public PinManagementMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        yourAccountForm = new Form("Your Account");
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

/*            if (name.equals(TOP_UP)) {
            	mFinoConfigData.setServiceName("recharge");
            	new TopUpMenu(mFinoConfigData, displayable);
            } else if (name.equals(ACTIVATION)) {
            	mFinoConfigData.setServiceName("Activation");
            	new ActivationPinForm(mFinoConfigData, displayable);
            }*/ 
            
            if (name.equals(CHANGE_PIN)) {
            	mFinoConfigData.setServiceName("changePin");
            	new ChangePinOldPinForm(mFinoConfigData, displayable);
            } else if (name.equals(RESET_PIN)){
            	mFinoConfigData.setServiceName("resetPin");
            	new ResetPinSecretAnswerForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
