package com.mfino.merchant.ui.myaccount;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.merchant.ui.AbstractMfinoConfig;
import com.mfino.handset.merchant.util.Constants;
import com.mfino.handset.merchant.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class BusinessReportForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    
    private Form businessReportForm = new Form("Business Report");
    private String[] businessReportMenu = {Constants.TODAY, Constants.LAST_DAY, Constants.LAST_WEEK};
    private ChoiceGroup pinManagementChoiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, businessReportMenu, null);
    
    private Form pinForm = new Form("Enter Pin");
    private TextField pin = new TextField("PIN", "", 6, TextField.PASSWORD);

    private Form receiptForm = new Form("Receipt");
    private StringItem receiptMessage = new StringItem("", "");
    
    private String confirmString = "";
    private String receiptString = "";
    
	public BusinessReportForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		
		businessReportForm.append(pinManagementChoiceGroup);
		businessReportForm.addCommand(mFinoConfigData.backCommand);
		businessReportForm.addCommand(mFinoConfigData.nextCommand);
		businessReportForm.setCommandListener(this);
		
		pinForm.append(pin);
		pinForm.addCommand(mFinoConfigData.backCommand);
		pinForm.addCommand(mFinoConfigData.nextCommand);
		pinForm.setCommandListener(this);
		
		receiptForm.append(receiptMessage);
		receiptForm.addCommand(mFinoConfigData.exitCommand);
		receiptForm.addCommand(mFinoConfigData.menuCommand);
		receiptForm.setCommandListener(this);
		
        display.setCurrent(businessReportForm);
	}
	
	public BusinessReportForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		
		if(displayable == businessReportForm){
			if(command == mFinoConfigData.nextCommand){
		        String name = pinManagementChoiceGroup.getString(pinManagementChoiceGroup.getSelectedIndex());
		        
		        System.out.println("Business Report Form "+name);
		        
		        if(name.equals(Constants.TODAY)){
		        	receiptMessage.setText(Constants.balanceCheckMsg);
		        } else if(name.equals(Constants.LAST_DAY)){
		        	receiptMessage.setText(Constants.lastDaySummaryMsg);
		        } else if(name.equals(Constants.LAST_WEEK)){
		        	receiptMessage.setText(Constants.weeklySummaryMsg);
		        }
		        
		        display.setCurrent(receiptForm);
		        
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(parent);
			}
		} else if (displayable == pinForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(receiptForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(businessReportForm);
			}
		}  else if (displayable == receiptForm){
			if(command == mFinoConfigData.exitCommand){
				mFinoConfigData.getMobileBankingMidlet().startApp();
			} else if(command == mFinoConfigData.menuCommand){
				display.setCurrent(mFinoConfigData.getMobileBankingMenuDisplay());
			}			
		}
	}
	
}
