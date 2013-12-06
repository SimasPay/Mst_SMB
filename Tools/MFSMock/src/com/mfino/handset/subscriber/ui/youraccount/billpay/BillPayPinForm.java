package com.mfino.handset.subscriber.ui.youraccount.billpay;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.youraccount.topup.TopUpReceiptScreen;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.util.MfinoConfigData;
import com.mfino.handset.subscriber.util.ResponseData;
import com.mfino.handset.subscriber.util.WebAPIHTTPWrapper;

public class BillPayPinForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form activationPinForm;
    private TextField pin;
    
	public BillPayPinForm(MfinoConfigData mFinoConfigData, Displayable parent){
		this(mFinoConfigData, parent, "Enter PIN");
	}
	
	public BillPayPinForm(MfinoConfigData mFinoConfigData, Displayable parent, String pinLabel){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		activationPinForm = new Form(pinLabel);
		pin = new TextField(pinLabel, "", 14, TextField.PASSWORD);		
		activationPinForm.append(pin);
		activationPinForm.addCommand(mFinoConfigData.backCommand);
		activationPinForm.addCommand(mFinoConfigData.nextCommand);
		activationPinForm.setCommandListener(this);
        display.setCurrent(activationPinForm);
	}
	
	public BillPayPinForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			
			if (pin.getString().equals("")) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setSourcePin(pin.getString().trim());
				mFinoConfigData.setSourcePocketCode(ConfigurationUtil.POCKET_CODE_BANK);
				mFinoConfigData.setCustomerId("1");
				
				System.out.println("Service Name : "+mFinoConfigData.getServiceName());
				
				String receiptMessage = "";
				WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
				ResponseData responseData = webApiHttpWrapper.getResponseData();
				
				String parentTxnId = responseData.getParentTxnId();
				String amount = responseData.getAmount();
				String billDetails = responseData.getBillDetails();
				
				if(null != billDetails){
					billDetails = ConfigurationUtil.replace(" ", "%20", billDetails);
				}
				
				System.out.println("parentTxnId "+parentTxnId);
				System.out.println("amount "+amount);
				System.out.println("billDetails "+billDetails);
				
				
				if(("565".equals(responseData.getMsgCode())) && (null != parentTxnId) && (null != amount) && (null != billDetails))
				{
					mFinoConfigData.setServiceName("billPayment");
					mFinoConfigData.setAmount(amount);
					mFinoConfigData.setParentTxnId(parentTxnId);
					mFinoConfigData.setBillDetails(billDetails);
					
					responseData = webApiHttpWrapper.getResponseData();
					
					if("567".equals(responseData.getMsgCode())){
						receiptMessage = "Paid " + amount + " to " + mFinoConfigData.getBillerName() + " " + responseData.getTransactionTime() + " Confirm#"+responseData.getTransferId();
					}
					else{
						receiptMessage = responseData.getMsgCode() + ":" + responseData.getMsg();
					}
				}
				else
				{
					receiptMessage = responseData.getMsgCode() + ":" + responseData.getMsg();
				}
				
				new TopUpReceiptScreen(mFinoConfigData, displayable, receiptMessage);
			}
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
