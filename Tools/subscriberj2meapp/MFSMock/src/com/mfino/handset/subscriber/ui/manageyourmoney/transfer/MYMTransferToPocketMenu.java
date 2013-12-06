package com.mfino.handset.subscriber.ui.manageyourmoney.transfer;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class MYMTransferToPocketMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form toPocketForm;
    private ChoiceGroup choiceGroup;
    
	private static final String E_MONEY = "E-Money";
    private static final String BANK = "Bank";
    
    private static final String[] yourAccountMenu = {E_MONEY, BANK};
    
	public MYMTransferToPocketMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        toPocketForm = new Form("Select To Account");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        toPocketForm.append(choiceGroup);
        toPocketForm.addCommand(mFinoConfigData.backCommand);
        toPocketForm.addCommand(mFinoConfigData.nextCommand);
        toPocketForm.setCommandListener(this);
        display.setCurrent(toPocketForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(E_MONEY)) {
            	mFinoConfigData.setDestinationPocketCode(ConfigurationUtil.POCKET_CODE_EMONEY);
            } else if (name.equals(BANK)) {
            	mFinoConfigData.setDestinationPocketCode(ConfigurationUtil.POCKET_CODE_BANK);
            }
            
            if(mFinoConfigData.isYourWallet()){
	            if(mFinoConfigData.getSourcePocketCode().equals(mFinoConfigData.getDestinationPocketCode())){
	                alert = new Alert("Error", "Source and destination accounts cannot be same", null, AlertType.ERROR);
	                alert.setTimeout(Alert.FOREVER);
	                display.setCurrent(alert);    	
	            } else{
	            	new MYMTransferAmountForm(mFinoConfigData, displayable);
	            }
            }
            else{
            	new MYMTransferAmountForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
