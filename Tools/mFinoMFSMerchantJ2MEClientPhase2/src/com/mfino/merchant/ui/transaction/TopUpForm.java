package com.mfino.merchant.ui.transaction;

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
public class TopUpForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    
    private Form sourceAccountForm = new Form("Select Source Account");
    private String[] sourceAccountMenu = {Constants.E_MONEY, Constants.BANK};
    private ChoiceGroup sourceAccountChoiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, sourceAccountMenu, null);
    
    private Form targetMdnForm = new Form("Enter Phone#");
    private TextField mdn = new TextField("Enter Phone#", "", 16, TextField.DECIMAL);
    
    private Form amountForm = new Form("Enter Amount");
    private TextField amount = new TextField("Enter Amount", "", 16, TextField.DECIMAL);
    
    private Form pinForm = new Form("Enter Pin");
    private TextField pin = new TextField("PIN", "", 6, TextField.PASSWORD);
    
    private Form confirmForm = new Form("Confirm");
    private StringItem confirmMessage = new StringItem("",""); 
    
    private Form receiptForm = new Form("Receipt");
    private StringItem receiptMessage = new StringItem("", "");
    
    private String confirmString = "";
    private String receiptString = "";
    
	public TopUpForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		
		sourceAccountForm.append(sourceAccountChoiceGroup);
		sourceAccountForm.addCommand(mFinoConfigData.backCommand);
		sourceAccountForm.addCommand(mFinoConfigData.nextCommand);
		sourceAccountForm.setCommandListener(this);
		
		targetMdnForm.append(mdn);
		targetMdnForm.addCommand(mFinoConfigData.backCommand);
		targetMdnForm.addCommand(mFinoConfigData.nextCommand);
		targetMdnForm.setCommandListener(this);
		
		amountForm.append(amount);
		amountForm.addCommand(mFinoConfigData.backCommand);
		amountForm.addCommand(mFinoConfigData.nextCommand);
		amountForm.setCommandListener(this);

		pinForm.append(pin);
		pinForm.addCommand(mFinoConfigData.backCommand);
		pinForm.addCommand(mFinoConfigData.nextCommand);
		pinForm.setCommandListener(this);
		
		confirmForm.append(confirmMessage);
		confirmForm.addCommand(mFinoConfigData.backCommand);
		confirmForm.addCommand(mFinoConfigData.nextCommand);
		confirmForm.setCommandListener(this);
		
		receiptForm.append(receiptMessage);
		receiptForm.addCommand(mFinoConfigData.exitCommand);
		receiptForm.addCommand(mFinoConfigData.menuCommand);
		receiptForm.setCommandListener(this);
		
        display.setCurrent(sourceAccountForm);
	}
	
	public TopUpForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		
		if(displayable == sourceAccountForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(targetMdnForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(parent);
			}
		} else if (displayable == targetMdnForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(amountForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(sourceAccountForm);
			}			
		} else if (displayable == amountForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(pinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(targetMdnForm);
			}			
		} else if (displayable == pinForm){
			if(command == mFinoConfigData.nextCommand){
				
				setValues();
				confirmString = "Do you want to purchase a "+amount.getString().trim() + " top-up for "+mFinoConfigData.getDestinationMdn();
				confirmMessage.setText(confirmString);
				
				display.setCurrent(confirmForm);
				
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(amountForm);
			}
		} else if (displayable == confirmForm){
			
			if(command == mFinoConfigData.nextCommand){
				
				mFinoConfigData.setServiceName("recharge");
				
				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
	        	mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
	        	mFinoConfigData.setTransferId(responseData.getTransferId());

	        	receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();
				receiptMessage.setText(receiptString);
				
				display.setCurrent(receiptForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(pinForm);
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
        	mFinoConfigData.setSourcePocketCode("12");
        } else if (name.equals(Constants.BANK)) {
        	mFinoConfigData.setSourcePocketCode("6");
        }
        
        mFinoConfigData.setDestinationPocketCode("12");
        mFinoConfigData.setDestinationMdn(mdn.getString().trim());
        mFinoConfigData.setAmount(amount.getString().trim());
        mFinoConfigData.setSourcePin(pin.getString().trim());
        
        mFinoConfigData.setServiceName("");
        
	}
}
