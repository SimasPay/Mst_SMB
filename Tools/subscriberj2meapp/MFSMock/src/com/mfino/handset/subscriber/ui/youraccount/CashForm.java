package com.mfino.handset.subscriber.ui.youraccount;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.MfinoConfigData;
import com.mfino.handset.subscriber.util.ResponseData;
import com.mfino.handset.subscriber.util.WebAPIHTTPWrapper;

public class CashForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;

    private Form agentCodeForm = new Form("Enter Agent Code");
    private TextField agentCode = new TextField("Enter Agent Code", "", 16, TextField.DECIMAL);
    
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
    
	public CashForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());

		agentCodeForm.append(agentCode);
		agentCodeForm.addCommand(mFinoConfigData.backCommand);
		agentCodeForm.addCommand(mFinoConfigData.nextCommand);
		agentCodeForm.setCommandListener(this);
		
		amountForm.append(amount);
		amountForm.addCommand(mFinoConfigData.backCommand);
		amountForm.addCommand(mFinoConfigData.nextCommand);
		amountForm.setCommandListener(this);

		pinForm.append(pin);
		pinForm.addCommand(mFinoConfigData.backCommand);
		pinForm.addCommand(mFinoConfigData.nextCommand);
		pinForm.setCommandListener(this);
		
		confirmForm.append(confirmMessage);
		confirmForm.addCommand(mFinoConfigData.cancelCommand);
		confirmForm.addCommand(mFinoConfigData.nextCommand);
		confirmForm.setCommandListener(this);
		
		receiptForm.append(receiptMessage);
		receiptForm.addCommand(mFinoConfigData.exitCommand);
		receiptForm.addCommand(mFinoConfigData.menuCommand);
		receiptForm.setCommandListener(this);
		
        display.setCurrent(agentCodeForm);
	}
	
	public CashForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		
		if(displayable == agentCodeForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(amountForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(parent);
			}
		} else if (displayable == amountForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(pinForm);
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(agentCodeForm);
			}			
		} else if (displayable == pinForm){
			if(command == mFinoConfigData.nextCommand){
				display.setCurrent(pinForm);
				setValues();
				
/*				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
	        	mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
	        	mFinoConfigData.setTransferId(responseData.getTransferId());

	        	receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();
				receiptMessage.setText(receiptString);*/
				
				display.setCurrent(confirmForm);
				
			} else if(command == mFinoConfigData.backCommand){
				display.setCurrent(amountForm);
			}			
		} else if (displayable == confirmForm){
			
			if(command == mFinoConfigData.nextCommand){
				
/*				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
	        	mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
	        	mFinoConfigData.setTransferId(responseData.getTransferId());

	        	receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();
				receiptMessage.setText(receiptString);*/
				
				display.setCurrent(receiptForm);
			} else if(command == mFinoConfigData.cancelCommand){
				display.setCurrent(parent);
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
		mFinoConfigData.setAgentCode(agentCode.getString().trim());
		mFinoConfigData.setAmount(amount.getString().trim());
        mFinoConfigData.setSourcePin(pin.getString().trim());
	}
}
