package com.mfino.handset.subscriber.midlets;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.mfino.handset.subscriber.ui.MfinoConfig;
import com.mfino.handset.subscriber.ui.MobileBankingMenu;
import com.mfino.handset.subscriber.ui.youraccount.activation.ActivationMdnForm;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 */
public class MobileBankingMidlet extends MIDlet implements CommandListener, MfinoConfig {
	
	private MfinoConfigData mFinoConfigData;
    public Display display;
    private Form loginForm; 
    private StringItem stringItem;
    private TextField phoneNumber;
    private TextField mPin;
    private Image mFinoLogo;
    private Alert alert;
    
    public void startApp() {
    	
    	mFinoConfigData = new MfinoConfigData();
    	mFinoConfigData.setMobileBankingMidlet(this);
    	
    	loginForm = new Form("Login");
    	
    	mFinoConfigData.setChannelId("7");
    	mFinoConfigData.setMode("1");
    	
    	mFinoConfigData.setBankId("1");
    	
    	mFinoConfigData.setMobileBankingMidlet(this);
    	mFinoConfigData.setSourceMdn(mFinoConfigData.getSourceMdn());
    	
        display = Display.getDisplay(this);
        
        stringItem = new StringItem("", "");
        
        try {
			mFinoLogo = Image.createImage("/com/mfino/handset/resources/zenith _edited.png");
		} catch (IOException e) {
			e.printStackTrace();
		}

        phoneNumber = new TextField("Phone Number", "", 14, TextField.DECIMAL);
        mPin = new TextField("PIN", "", 14, TextField.PASSWORD);
        
        loginForm.append(mFinoLogo);
        loginForm.append(phoneNumber);
        loginForm.append(mPin);
        
        loginForm.addCommand(mFinoConfigData.nextCommand);
        loginForm.addCommand(mFinoConfigData.activationCommand);
        
        loginForm.setCommandListener(this);
        display.setCurrent(loginForm);
    }

    public void pauseApp() {
    	pauseApp();
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    public void commandAction(Command command, Displayable displayable) {
    	
    	if(command == mFinoConfigData.nextCommand){
			String confirmMessage = "";

			if((phoneNumber.getString().equals("")) || ((mPin.getString().equals("")))) {
                alert = new Alert("Error", "Please enter required fields", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
			else{
				mFinoConfigData.setSourceMdn(phoneNumber.getString().trim());
				mFinoConfigData.setSourcePin(mPin.getString().trim());

				new MobileBankingMenu(mFinoConfigData, displayable);
			}
		} 
    	else if(command == mFinoConfigData.activationCommand){
    		mFinoConfigData.setServiceName("activation");
    		new ActivationMdnForm(mFinoConfigData, displayable);
		}
		else if(command == mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
			mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
		}
	}

	public void setConfigData(MfinoConfigData mFinoConfigData) {
		this.mFinoConfigData = mFinoConfigData;
	}

	public MfinoConfigData getMfinoConfigData() {
		return this.mFinoConfigData;
	}
}
