package com.mfino.handset.subscriber.ui.buysomething;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.buysomething.purchase.BuySomethingPurchaseMenu;
import com.mfino.handset.subscriber.ui.youraccount.topup.TopUpMenu;
import com.mfino.handset.subscriber.util.MfinoConfigData;

public class BuySomethingMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form buySomethingForm;
    private ChoiceGroup choiceGroup;
    
    private static final String AIRTIME_PURCHASE = "Airtime Purchase";
	private static final String RETAIL_PURCHASE = "Retail Purchase";
    
    private static final String[] buySomethingMenu = {AIRTIME_PURCHASE, RETAIL_PURCHASE};
    
	public BuySomethingMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        buySomethingForm = new Form("Buy");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, buySomethingMenu, null);
        buySomethingForm.append(choiceGroup);
        buySomethingForm.addCommand(mFinoConfigData.backCommand);
        buySomethingForm.addCommand(mFinoConfigData.nextCommand);
        buySomethingForm.setCommandListener(this);
        display.setCurrent(buySomethingForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(AIRTIME_PURCHASE)) {
            	new TopUpMenu(mFinoConfigData, displayable);
            } else if (name.equals(RETAIL_PURCHASE)) {
            	new BuySomethingPurchaseMenu(mFinoConfigData, displayable);
            } 
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}

