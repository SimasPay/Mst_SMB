package com.mfino.handset.subscriber.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 *
 */
public class DestinationMdnForm extends AbstractMfinoConfig implements CommandListener{
	
	private Displayable parent;
    private Display display;
    private Form destinationMdnForm;
    private TextField amount;
    
	public DestinationMdnForm(MfinoConfigData mFinoConfigData, Displayable parent){
		super(mFinoConfigData);
		this.parent = parent;
		destinationMdnForm = new Form("Destination Mobile");
		amount = new TextField("Mobile#", "", 14, TextField.NUMERIC);		
		destinationMdnForm.append(amount);
		destinationMdnForm.addCommand(mFinoConfigData.backCommand);
		destinationMdnForm.addCommand(mFinoConfigData.nextCommand);
		destinationMdnForm.setCommandListener(this);
        display.setCurrent(destinationMdnForm);
	}
	
	public DestinationMdnForm(MfinoConfigData mFinoConfigData)
	{
		super(mFinoConfigData);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == super.mFinoConfigData.backCommand){
			display.setCurrent(parent);
		}
		else if(command == super.mFinoConfigData.nextCommand){
			
		}
		else if(command == super.mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}
}
