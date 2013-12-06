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

public class ActivationConfirmNewPin4 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form activationPinForm;
    private TextField tfConfirmPin;

    public ActivationConfirmNewPin4(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Confirm New PIN");
    }

    public ActivationConfirmNewPin4(UserDataContainer mFinoConfigData, Displayable parent, String pinLabel) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        activationPinForm = new Form(pinLabel);
        tfConfirmPin = new TextField("Confirm New PIN", null, 4, TextField.PASSWORD | TextField.NUMERIC);
        activationPinForm.append(tfConfirmPin);
        activationPinForm.addCommand(mFinoConfigData.backCommand);
        activationPinForm.addCommand(mFinoConfigData.nextCommand);
        activationPinForm.setCommandListener(this);

        display.setCurrent(activationPinForm);
    }

    public ActivationConfirmNewPin4(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {

//        if (displayable == activationPinForm) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(tfConfirmPin.getString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                if (!tfConfirmPin.getString().equals(mFinoConfigData.getActivationNewPin())) {
                    alert = new Alert("Error", "New pin and confirm should be same", null, AlertType.ERROR);
                    alert.setTimeout(Alert.FOREVER);
                    display.setCurrent(alert);
                } else {
                    mFinoConfigData.setActivationConfirmPin(tfConfirmPin.getString());
                    new LegalDisclosure5(mFinoConfigData, parent);
                }
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
//        } else if (displayable == legalDisclaimerForm) {
//            if (command == super.mFinoConfigData.cancelCommand) {
//                display.setCurrent(mFinoConfigData.getMobileBankingMenuDisplay());
//            } else {
//                mFinoConfigData.setActivationConfirmPin(tfConfirmPin.getString().trim());
//                WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
//                ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();
//                String receiptMessage = responseData.getMsgCode() + ":" + responseData.getMsg();
//                new ResponseReceiptScreen(mFinoConfigData, displayable, receiptMessage);
//            }
//        }

    }
}
