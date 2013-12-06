package com.mfino.handset.subscriber.ui.youraccount.activation;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.util.ConfigurationUtil;

public class ActivationPinForm3 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form activationPinForm;
    private TextField tfNewPin;

    public ActivationPinForm3(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Enter New PIN");
    }

    public ActivationPinForm3(UserDataContainer mFinoConfigData, Displayable parent, String pinLabel) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        activationPinForm = new Form(pinLabel);
        tfNewPin = new TextField("Enter New PIN", null, 4, TextField.PASSWORD|TextField.NUMERIC);
        activationPinForm.append(tfNewPin);
        activationPinForm.addCommand(mFinoConfigData.backCommand);
        activationPinForm.addCommand(mFinoConfigData.nextCommand);
        activationPinForm.setCommandListener(this);
        display.setCurrent(activationPinForm);
    }

    public ActivationPinForm3(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(tfNewPin.getString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setActivationNewPin(tfNewPin.getString());
                new ActivationConfirmNewPin4(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
