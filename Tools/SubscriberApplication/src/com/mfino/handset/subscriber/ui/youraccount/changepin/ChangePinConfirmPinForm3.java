package com.mfino.handset.subscriber.ui.youraccount.changepin;

import com.mfino.handset.subscriber.constants.Constants;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.ResponseReceiptScreen;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
import com.mfino.handset.subscriber.http.WebAPIHTTPWrapper;

public class ChangePinConfirmPinForm3 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form confirmPinForm;
    private TextField confirmPin;

    public ChangePinConfirmPinForm3(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        confirmPinForm = new Form("Change PIN");
        confirmPin = new TextField("Confirm New PIN", null, 4, TextField.PASSWORD | TextField.NUMERIC);
        confirmPinForm.append(confirmPin);
        confirmPinForm.addCommand(mFinoConfigData.backCommand);
        confirmPinForm.addCommand(mFinoConfigData.sendCommand);
        confirmPinForm.setCommandListener(this);
        display.setCurrent(confirmPinForm);
    }

    public ChangePinConfirmPinForm3(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.sendCommand) {

            if (ConfigurationUtil.isBlank(confirmPin.getString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {

                if (!confirmPin.getString().equals(mFinoConfigData.getNewPin())) {
                    alert = new Alert("Error", "New pin and confirm pin should be same.", null, AlertType.ERROR);
                    alert.setTimeout(Alert.FOREVER);
                    display.setCurrent(alert);
                } else {
                    mFinoConfigData.setConfirmPin(confirmPin.getString().trim());
                    mFinoConfigData.setServiceName(Constants.SERVICE_ACCOUNT);
                    mFinoConfigData.setTransactionName(Constants.TRANSACTION_CHANGEPIN);
                    WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
                    ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();
                    String receiptMessage = responseData.getMsgCode() + ":" + responseData.getMsg();
                    new ResponseReceiptScreen(mFinoConfigData, displayable, receiptMessage);
                }
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
