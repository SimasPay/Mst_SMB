package com.mfino.handset.subscriber.ui.youraccount.changepin;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

public class ChangePinOldPinForm1 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form oldPinForm;
    private TextField tfOldPin;

    public ChangePinOldPinForm1(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        oldPinForm = new Form("Change PIN");
        tfOldPin = new TextField("Present PIN", null, 4, TextField.PASSWORD|TextField.NUMERIC);
        oldPinForm.append(tfOldPin);
        oldPinForm.addCommand(mFinoConfigData.backCommand);
        oldPinForm.addCommand(mFinoConfigData.nextCommand);
        oldPinForm.setCommandListener(this);
        display.setCurrent(oldPinForm);
    }

    public ChangePinOldPinForm1(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(tfOldPin.getString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setSourcePin(tfOldPin.getString().trim());
                new ChangePinNewPinForm2(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
