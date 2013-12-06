package com.mfino.handset.subscriber.ui.buysomething.purchase;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class BuySomethingPurchaseMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form buySomeThingPurchaseForm;
    private StringItem si;
    private ChoiceGroup choiceGroup;
    
	private static final String PURCHASE = "Purchase";
	private static final String SPLIT_PURCHASE = "Split Purchase";
	
    private static final String[] splitPurchaseMenu = {PURCHASE, SPLIT_PURCHASE};
    
	public BuySomethingPurchaseMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        buySomeThingPurchaseForm = new Form("Purchase");
        
        si = new StringItem("", "");
        String text = "\nPizza Hut #556 Palo Alto \nwants to bill you 21$ for a purchase. \nClick Purchase to do the payment.";
        si.setText(text);
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, splitPurchaseMenu, null);
        
        buySomeThingPurchaseForm.append(si);
        buySomeThingPurchaseForm.append(choiceGroup);
        
        buySomeThingPurchaseForm.addCommand(mFinoConfigData.cancelCommand);
        buySomeThingPurchaseForm.addCommand(mFinoConfigData.purchaseCommand);
        buySomeThingPurchaseForm.setCommandListener(this);
        display.setCurrent(buySomeThingPurchaseForm);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.cancelCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.purchaseCommand){
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());
//			boolean selectedFlags[] = new boolean[choiceGroup.size()]; 
//			choiceGroup.getSelectedFlags(selectedFlags);
			
            if(SPLIT_PURCHASE.equals(name)){
            	System.out.println();
            	new SplitPurchaseForm(mFinoConfigData, displayable);
            } 
            else{
            	new BuySomethingPinForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
