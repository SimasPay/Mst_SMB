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
import com.mfino.handset.merchant.util.ResponseData;
import com.mfino.handset.merchant.util.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 */
public class HistoryForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    
    private Form sourceAccountForm = new Form("Select Source Account");
    private String[] sourceAccountMenu = {Constants.E_MONEY, Constants.BANK};
    private ChoiceGroup sourceAccountChoiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, sourceAccountMenu, null);
    
    private Form pinForm = new Form("Enter Pin");
    private TextField pin = new TextField("PIN", "", 6, TextField.PASSWORD);
    
    private Form receiptForm = new Form("Receipt");
    private StringItem receiptMessage = new StringItem("", "");
    
    private String confirmString = "";
    private String receiptString = "";
    
	public HistoryForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		
		sourceAccountForm.append(sourceAccountChoiceGroup);
		sourceAccountForm.addCommand(mFinoConfigData.backCommand);
		sourceAccountForm.addCommand(mFinoConfigData.nextCommand);
		sourceAccountForm.setCommandListener(this);
		
		pinForm.append(pin);
		pinForm.addCommand(mFinoConfigData.backCommand);
		pinForm.addCommand(mFinoConfigData.nextCommand);
		pinForm.setCommandListener(this);
		
		receiptForm.append(receiptMessage);
		receiptForm.addCommand(mFinoConfigData.exitCommand);
		receiptForm.addCommand(mFinoConfigData.menuCommand);
		receiptForm.setCommandListener(this);
		
        display.setCurrent(sourceAccountForm);
	}
	
	public HistoryForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		
		if(displayable == sourceAccountForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(pinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(parent);
			}
		} else if (displayable == pinForm){
			if(command == mFinoConfigData.nextCommand){
				
				setValues();
				
				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
				if("4".equals(responseData.getMsgCode())){
					receiptString = responseData.getMsg();
				}
				else if("Currency".equals(responseData.getMsgCode())){
					receiptString = "E-Money Balance "+responseData.getMsg() + " on "+responseData.getTransactionTime();
				}
				else{
					receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();
				}
				
				receiptMessage.setText(receiptString);
				
				display.setCurrent(receiptForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(sourceAccountForm);
			}
		} else if (displayable == receiptForm){
			if(command == mFinoConfigData.exitCommand){
				mFinoConfigData.getMobileBankingMidlet().startApp();
			} else if(command == mFinoConfigData.menuCommand){
				display.setCurrent(mFinoConfigData.getMobileBankingMenuDisplay());
			}			
		}
	}
	
	private void setValues(){
        String name = sourceAccountChoiceGroup.getString(sourceAccountChoiceGroup.getSelectedIndex());

        if (name.equals(Constants.E_MONEY)) {
        	mFinoConfigData.setServiceName("svaemoneyHistory");
        	mFinoConfigData.setSourcePocketCode("12");
        } else if (name.equals(Constants.BANK)) {
        	mFinoConfigData.setServiceName("bankHistory");
        	mFinoConfigData.setSourcePocketCode("6");
        } 
        
        mFinoConfigData.setSourcePin(pin.getString().trim());
	}
}
