package com.mfino.handset.subscriber.ui.youraccount.activation;
import com.mfino.handset.subscriber.ui.*;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

/**
 * @author sasidhar
 *
 */
public class ActivationResponseReceiptScreen extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form topUpReceiptForm;
    private StringItem stringItem;

    public ActivationResponseReceiptScreen(UserDataContainer mFinoConfigData, Displayable parent, String receiptMessage) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        stringItem = new StringItem("", "");
        stringItem.setText(receiptMessage);
        topUpReceiptForm = new Form("Activation");
        topUpReceiptForm.append(stringItem);
        topUpReceiptForm.addCommand(mFinoConfigData.loginCommand);
        topUpReceiptForm.setCommandListener(this);
        display.setCurrent(topUpReceiptForm);
    }

    public void commandAction(Command command, Displayable displayable) {
//        if (command == super.mFinoConfigData.backCommand) {
//            display.setCurrent(parent);
//        } else 
        if (command == super.mFinoConfigData.loginCommand) {
            mFinoConfigData.getMobileBankingMidlet().startApp();
        }
//        else if (command == super.mFinoConfigData.exitCommand) {
//            mFinoConfigData.setAESKey(null);
//            mFinoConfigData.setSourcePin(null);
//            mFinoConfigData.setSourceMdn(null);
//            mFinoConfigData.getMobileBankingMidlet().startApp();
//        }
    }
}
