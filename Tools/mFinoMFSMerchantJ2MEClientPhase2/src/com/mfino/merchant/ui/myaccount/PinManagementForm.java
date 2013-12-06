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
public class PinManagementForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    
    private Form pinManagementForm = new Form("PIN Management");
    private String[] pinManagementMenu = {Constants.CHANGE_PIN, Constants.RESET_PIN};
    private ChoiceGroup pinManagementChoiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, pinManagementMenu, null);
    
    private Form oldPinForm = new Form("Enter Old Pin");
    private TextField oldPin = new TextField("Old Pin", "", 6, TextField.PASSWORD);

    private Form newPinForm = new Form("Enter New Pin");
    private TextField newPin = new TextField("New Pin", "", 6, TextField.PASSWORD);

    private Form confirmNewPinForm = new Form("Confirm Pin");
    private TextField confirmNewPin = new TextField("Confirm Pin", "", 6, TextField.PASSWORD);
    
    private Form secretAnswerForm = new Form("Secret Answer");
    private TextField secretAnswer = new TextField("Secret Answer", "", 6, TextField.PASSWORD);
    
    private Form receiptForm = new Form("Receipt");
    private StringItem receiptMessage = new StringItem("", "");
    
    private String confirmString = "";
    private String receiptString = "";
    
	public PinManagementForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		
		pinManagementForm.append(pinManagementChoiceGroup);
		pinManagementForm.addCommand(mFinoConfigData.backCommand);
		pinManagementForm.addCommand(mFinoConfigData.nextCommand);
		pinManagementForm.setCommandListener(this);
		
		oldPinForm.append(oldPin);
		oldPinForm.addCommand(mFinoConfigData.backCommand);
		oldPinForm.addCommand(mFinoConfigData.nextCommand);
		oldPinForm.setCommandListener(this);
		
		newPinForm.append(newPin);
		newPinForm.addCommand(mFinoConfigData.backCommand);
		newPinForm.addCommand(mFinoConfigData.nextCommand);
		newPinForm.setCommandListener(this);
		
		confirmNewPinForm.append(confirmNewPin);
		confirmNewPinForm.addCommand(mFinoConfigData.backCommand);
		confirmNewPinForm.addCommand(mFinoConfigData.nextCommand);
		confirmNewPinForm.setCommandListener(this);
		
		secretAnswerForm.append(secretAnswer);
		secretAnswerForm.addCommand(mFinoConfigData.backCommand);
		secretAnswerForm.addCommand(mFinoConfigData.nextCommand);
		secretAnswerForm.setCommandListener(this);
		
		receiptForm.append(receiptMessage);
		receiptForm.addCommand(mFinoConfigData.exitCommand);
		receiptForm.addCommand(mFinoConfigData.menuCommand);
		receiptForm.setCommandListener(this);
		
        display.setCurrent(pinManagementForm);
	}
	
	public PinManagementForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		
		if(displayable == pinManagementForm){
			if(command == mFinoConfigData.nextCommand){
		        String name = pinManagementChoiceGroup.getString(pinManagementChoiceGroup.getSelectedIndex());
		        
		        if(name.equals(Constants.CHANGE_PIN)){
		        	display.setCurrent(oldPinForm);
		        }
		        else{
		        	display.setCurrent(secretAnswerForm);
		        }
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(parent);
			}
		} else if (displayable == secretAnswerForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(newPinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(pinManagementForm);
			}
		} else if (displayable == oldPinForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(newPinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(pinManagementForm);
			}
		} else if (displayable == newPinForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(confirmNewPinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(oldPinForm);
			}
		} else if (displayable == confirmNewPinForm){
			if(command == mFinoConfigData.nextCommand){
				setValues();
				
				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
				receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();
				receiptMessage.setText(receiptString);
				
				display.setCurrent(receiptForm);
				
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(newPinForm);
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
        String name = pinManagementChoiceGroup.getString(pinManagementChoiceGroup.getSelectedIndex());

        if (name.equals(Constants.CHANGE_PIN)) {
        	mFinoConfigData.setServiceName("changePin");
        } else if (name.equals(Constants.RESET_PIN)) {
        	mFinoConfigData.setServiceName("resetPin");
        } 
        
        mFinoConfigData.setOldPin(oldPin.getString().trim());
        mFinoConfigData.setNewPin(newPin.getString().trim());
	}
}
