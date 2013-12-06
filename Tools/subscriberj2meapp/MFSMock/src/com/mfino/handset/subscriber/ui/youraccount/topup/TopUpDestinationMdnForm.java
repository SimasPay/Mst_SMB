package com.mfino.handset.subscriber.ui.youraccount.topup;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 *
 */
public class TopUpDestinationMdnForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form topUpDestinationMdnForm;
    private TextField destinationMdn;
    
	public TopUpDestinationMdnForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
		topUpDestinationMdnForm = new Form("Destination Phone#");
		destinationMdn = new TextField("Enter Your Friends Phone#", "", 14, TextField.DECIMAL);		
		topUpDestinationMdnForm.append(destinationMdn);
		topUpDestinationMdnForm.addCommand(mFinoConfigData.backCommand);
		topUpDestinationMdnForm.addCommand(mFinoConfigData.nextCommand);
		topUpDestinationMdnForm.setCommandListener(this);
        display.setCurrent(topUpDestinationMdnForm);
	}
	
	public TopUpDestinationMdnForm(MfinoConfigData mFinoConfigData)
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
				new TopUpAmountForm(mFinoConfigData, displayable);
            }
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
