package com.mfino.handset.subscriber.ui.manageyourmoney.billpay;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.youraccount.billpay.BillIdForm;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class MYMBillPaySelectBillMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form billPayForm;
    private ChoiceGroup choiceGroup;
    
	private static final String PAY_RENT = "Pay Rent";
    private static final String PAY_UTILITY = "Pay Utility";
    private static final String PAY_WATER = "Pay Water";
    private static final String PAY_GAS = "Pay Gas";
    
    private static final String[] billersMenu = {PAY_RENT, PAY_UTILITY, PAY_WATER, PAY_GAS};
    
	public MYMBillPaySelectBillMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        billPayForm = new Form("Select Biller");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, billersMenu, null);
        billPayForm.append(choiceGroup);
        billPayForm.addCommand(mFinoConfigData.backCommand);
        billPayForm.addCommand(mFinoConfigData.nextCommand);
        billPayForm.setCommandListener(this);
        display.setCurrent(billPayForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		System.out.println("MYMBIllPaySelectBillMenu "+(command == super.mFinoConfigData.nextCommand));
		
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());
            
            if (name.equals(PAY_RENT)) {
            	mFinoConfigData.setBillerName("rent");
            } else if (name.equals(PAY_UTILITY)) {
            	mFinoConfigData.setBillerName("utility");
            } else if (name.equals(PAY_WATER)) {
            	mFinoConfigData.setBillerName("water");
            } else if (name.equals(PAY_GAS)) {
            	mFinoConfigData.setBillerName("gas");
            }
            
            new BillIdForm(mFinoConfigData, displayable);
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
