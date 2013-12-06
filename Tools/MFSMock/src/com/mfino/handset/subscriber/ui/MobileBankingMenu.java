package com.mfino.handset.subscriber.ui;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import com.mfino.handset.subscriber.ui.buysomething.BuySomethingMenu;
import com.mfino.handset.subscriber.ui.manageyourmoney.billpay.PayMenu;
import com.mfino.handset.subscriber.ui.manageyourmoney.transfer.MYMTransferMWalletMenu;
import com.mfino.handset.subscriber.ui.youraccount.YourAccountMenu;
import com.mfino.handset.subscriber.util.MfinoConfigData;

/**
 * @author sasidhar
 *
 */
public class MobileBankingMenu extends AbstractMfinoConfig implements CommandListener{

	private Displayable parent;
    public Display display;
    private List list;
    private Image imgRightArrow;
    
    public MobileBankingMenu(MfinoConfigData mfinoConfigData, Displayable displayable) {
    	super();
    	this.mFinoConfigData = mfinoConfigData;
    	this.parent = displayable;
    	display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
    	
    	mFinoConfigData.setChannelId("7");
    	mFinoConfigData.setMode("1");
    	mFinoConfigData.setBankId("1");

        list = new List("Select One", List.IMPLICIT);

        try 
        {
        	imgRightArrow = Image.createImage("/com/mfino/handset/resources/rightarrow.png");

//TODO delete later
//        	list.append("Your Account", imgRightArrow);
//        	list.append("Manage Your Money", imgRightArrow);
//        	list.append("Buy Something", imgRightArrow);

            list.append("Transfer", imgRightArrow);
            list.append("Pay", imgRightArrow);
            list.append("Buy", imgRightArrow);
            list.append("My Account", imgRightArrow);
        	
        } catch (IOException ex) {
            Form eform = new Form("Error");
            eform.append("Failed to load images");
        }
        
        list.addCommand(mfinoConfigData.exitCommand);
        list.setCommandListener(this);
        display.setCurrent(list);
	}
    
	public void commandAction(Command command, Displayable displayable) {
		
		mFinoConfigData.setMobileBankingMenuDisplay(displayable);
		
		if(command == mFinoConfigData.exitCommand){
			mFinoConfigData.getMobileBankingMidlet().startApp();
		} else if (displayable == list) {
            List down = (List) display.getCurrent();
            switch (down.getSelectedIndex()) {
                case 0:
//TODO deletelater  new YourAccountMenu(mFinoConfigData, displayable);
                	new MYMTransferMWalletMenu(mFinoConfigData, displayable);
                    break;
                case 1:
//TODO deletelater  new ManageYourMoneyMenu(mFinoConfigData, displayable);
                	new PayMenu(mFinoConfigData, displayable);
                    break;
                case 2:
                	new BuySomethingMenu(mFinoConfigData, displayable);
//                	new BuySomethingPurchaseMenu(mFinoConfigData, displayable);
                    break;
                case 3:
                	new YourAccountMenu(mFinoConfigData, displayable);
                	break;
            }
        }
	}

}
