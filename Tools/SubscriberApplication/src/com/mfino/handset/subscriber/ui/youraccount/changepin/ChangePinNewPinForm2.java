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

public class ChangePinNewPinForm2 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form newPinForm;
    private TextField newPin;

    public ChangePinNewPinForm2(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        newPinForm = new Form("Change PIN");
        newPin = new TextField("Enter New PIN", null, 4, TextField.PASSWORD | TextField.NUMERIC);
        newPinForm.append(newPin);
        newPinForm.addCommand(mFinoConfigData.backCommand);
        newPinForm.addCommand(mFinoConfigData.nextCommand);
        newPinForm.setCommandListener(this);
        display.setCurrent(newPinForm);
    }

    public ChangePinNewPinForm2(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(newPin.getString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {

                if (mFinoConfigData.getSourcePin().equals(newPin.getString())) {
                    alert = new Alert("Error", "Present pin and new pin can not be same", null, AlertType.ERROR);
                    alert.setTimeout(Alert.FOREVER);
                    display.setCurrent(alert);
                } else {
                    mFinoConfigData.setNewPin(newPin.getString().trim());
                    new ChangePinConfirmPinForm3(mFinoConfigData, displayable);
                }
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
