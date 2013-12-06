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

public class ActivationOTPForm2 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form activationOTPForm;
    private TextField tfOTP;

    public ActivationOTPForm2(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Enter Activation Key");
    }

    public ActivationOTPForm2(UserDataContainer mFinoConfigData, Displayable parent, String label) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        activationOTPForm = new Form(label);
        tfOTP = new TextField("Enter Activation Key", null, 4, TextField.PASSWORD|TextField.NUMERIC);
        activationOTPForm.append(tfOTP);
        activationOTPForm.addCommand(mFinoConfigData.backCommand);
        activationOTPForm.addCommand(mFinoConfigData.nextCommand);
        activationOTPForm.setCommandListener(this);
        display.setCurrent(activationOTPForm);
    }

    public ActivationOTPForm2(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(tfOTP.getString())) {
                alert = new Alert("Error", "Activation Key cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setOTP(tfOTP.getString());
                new ActivationPinForm3(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
