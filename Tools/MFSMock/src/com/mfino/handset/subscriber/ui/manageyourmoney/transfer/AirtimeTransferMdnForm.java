package com.mfino.handset.subscriber.ui.manageyourmoney.transfer;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.youraccount.topup.AirtimeTransferAmountForm;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 *
 */
public class AirtimeTransferMdnForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form airtimeTransferDestinationMdnForm;
    private TextField destinationMdn;
    
	public AirtimeTransferMdnForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		airtimeTransferDestinationMdnForm = new Form("Destination Phone#");
		destinationMdn = new TextField("Enter Your Friends Phone#", "", 16, TextField.DECIMAL);		
		airtimeTransferDestinationMdnForm.append(destinationMdn);
		airtimeTransferDestinationMdnForm.addCommand(mFinoConfigData.backCommand);
		airtimeTransferDestinationMdnForm.addCommand(mFinoConfigData.nextCommand);
		airtimeTransferDestinationMdnForm.setCommandListener(this);
        display.setCurrent(airtimeTransferDestinationMdnForm);
	}
	
	public AirtimeTransferMdnForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			
            if (destinationMdn.getString().equals("")) {
                alert = new Alert("Error", "Phone# cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }else{
				mFinoConfigData.setDestinationMdn(destinationMdn.getString().trim());
				new AirtimeTransferAmountForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
