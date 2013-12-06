package com.mfino.handset.subscriber.ui.manageyourmoney.transfer;

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
public class MYMTransferFromPocketMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form fromPocketForm;
    private ChoiceGroup choiceGroup;
    
	private static final String E_MONEY = "E-Money";
    private static final String BANK = "Bank";
    
    private static final String[] yourAccountMenu = {E_MONEY, BANK};
    
	public MYMTransferFromPocketMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        fromPocketForm = new Form("Select From Account");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        fromPocketForm.append(choiceGroup);
        fromPocketForm.addCommand(mFinoConfigData.backCommand);
        fromPocketForm.addCommand(mFinoConfigData.nextCommand);
        fromPocketForm.setCommandListener(this);
        display.setCurrent(fromPocketForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(E_MONEY)) {
            	mFinoConfigData.setSourcePocketCode(ConfigurationUtil.POCKET_CODE_EMONEY);
            	new MYMTransferToPocketMenu(mFinoConfigData, displayable);
            } else if (name.equals(BANK)) {
            	mFinoConfigData.setSourcePocketCode(ConfigurationUtil.POCKET_CODE_BANK);
            	new MYMTransferToPocketMenu(mFinoConfigData, displayable);
            } 
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
