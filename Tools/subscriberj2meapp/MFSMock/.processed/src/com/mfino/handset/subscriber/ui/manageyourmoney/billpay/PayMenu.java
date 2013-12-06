package com.mfino.handset.subscriber.ui.manageyourmoney.billpay;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.youraccount.billpay.LoanAccountNumberForm;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class PayMenu extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form billPayForm;
    private ChoiceGroup choiceGroup;
    
	private static final String PAY_BILL = "Pay Bill";
    private static final String PAY_LOAN = "Pay Loan";
    
    private static final String[] billersMenu = {PAY_BILL, PAY_LOAN};
    
	public PayMenu(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        billPayForm = new Form("Bill Pay");
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
			mFinoConfigData.setServiceName("billPaymentInquiry");
			
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());
            
            if (name.equals(PAY_BILL)) {
            	new MYMBillPaySelectBillMenu(mFinoConfigData, displayable);
            } else if (name.equals(PAY_LOAN)) {
            	mFinoConfigData.setBillerName("ICICI");
            	new LoanAccountNumberForm(mFinoConfigData, displayable);
            } 
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
