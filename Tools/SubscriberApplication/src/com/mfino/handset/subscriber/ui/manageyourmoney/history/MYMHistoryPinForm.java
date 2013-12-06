package com.mfino.handset.subscriber.ui.manageyourmoney.history;

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
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
import com.mfino.handset.subscriber.http.WebAPIHTTPWrapper;
import com.mfino.handset.subscriber.util.ConfigurationUtil;

public class MYMHistoryPinForm extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form mymHistoryPinForm;
    private TextField pin;

    public MYMHistoryPinForm(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Enter PIN");
    }

    public MYMHistoryPinForm(UserDataContainer mFinoConfigData, Displayable parent, String pinLabel) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        mymHistoryPinForm = new Form(pinLabel);
        pin = new TextField(pinLabel, null, 4, TextField.PASSWORD | TextField.NUMERIC);
        mymHistoryPinForm.append(pin);
        mymHistoryPinForm.addCommand(mFinoConfigData.backCommand);
        mymHistoryPinForm.addCommand(mFinoConfigData.nextCommand);
        mymHistoryPinForm.setCommandListener(this);
        display.setCurrent(mymHistoryPinForm);
    }

    public MYMHistoryPinForm(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {
            String receiptMessage = "";

            if (ConfigurationUtil.isBlank(pin.getString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setSourcePin(pin.getString().trim());
                mFinoConfigData.setTransactionName(Constants.TRANSACTION_HISTORY);
                WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
                ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();

                if ("39".equals(responseData.getMsgCode())) {
                    receiptMessage = responseData.getMsg();
                } else {
                    receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg();
                }

                new ResponseReceiptScreen(mFinoConfigData, displayable, receiptMessage);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
