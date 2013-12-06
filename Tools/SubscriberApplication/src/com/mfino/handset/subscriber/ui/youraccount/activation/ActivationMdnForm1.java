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

public class ActivationMdnForm1 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form activationMdnForm;
    private TextField mdn;

    public ActivationMdnForm1(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Enter  Mobile No");
    }

    public ActivationMdnForm1(UserDataContainer mFinoConfigData, Displayable parent, String pinLabel) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        activationMdnForm = new Form(pinLabel);
        mdn = new TextField(pinLabel, null, 13, TextField.PHONENUMBER);
        activationMdnForm.append(mdn);
        activationMdnForm.addCommand(mFinoConfigData.backCommand);
        activationMdnForm.addCommand(mFinoConfigData.nextCommand);
        activationMdnForm.setCommandListener(this);
        display.setCurrent(activationMdnForm);
    }

    public ActivationMdnForm1(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(mdn.getString())) {
                alert = new Alert("Error", "Mobile No cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                try {
                    mFinoConfigData.setSourceMdn(ConfigurationUtil.normalizeMDN(mdn.getString()));
                    new ActivationOTPForm2(mFinoConfigData, displayable);
                } catch (Exception ex) {
//                    ex.printStackTrace();
                }
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
