package com.mfino.handset.subscriber.ui.youraccount.topup;

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

public class TopUpMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form topUpMenuForm;
    private ChoiceGroup choiceGroup;
    
	private static final String YOUR_PHONE = "Your Phone";
    private static final String FRIENDS_PHONE = "Friends Phone";
    
    private static final String[] topUpMenu = {YOUR_PHONE, FRIENDS_PHONE};
	
	public TopUpMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		
		mFinoConfigData.setMode("3");
		mFinoConfigData.setServiceName("recharge");
		mFinoConfigData.setBucketType("CAL");
		mFinoConfigData.setBankId("1");
		mFinoConfigData.setSourcePocketCode(ConfigurationUtil.POCKET_CODE_BANK);
		
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		topUpMenuForm = new Form("Airtime Purchase");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, topUpMenu, null);
        topUpMenuForm.append(choiceGroup);
        topUpMenuForm.addCommand(mFinoConfigData.backCommand);
        topUpMenuForm.addCommand(mFinoConfigData.nextCommand);
        topUpMenuForm.setCommandListener(this);
        display.setCurrent(topUpMenuForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(YOUR_PHONE)) {
            	mFinoConfigData.setYourPhone(true);
            	mFinoConfigData.setDestinationMdn(mFinoConfigData.getSourceMdn());
            	new TopUpAmountForm(mFinoConfigData, displayable);
            } else if (name.equals(FRIENDS_PHONE)) {
            	mFinoConfigData.setYourPhone(false);
            	new TopUpDestinationMdnForm(mFinoConfigData, displayable);
            } 
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
