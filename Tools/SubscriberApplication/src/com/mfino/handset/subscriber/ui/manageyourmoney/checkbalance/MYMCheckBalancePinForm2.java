package com.mfino.handset.subscriber.ui.manageyourmoney.checkbalance;

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

public class MYMCheckBalancePinForm2 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form mymCheckBalancePinForm;
    private TextField pin;

    public MYMCheckBalancePinForm2(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Enter PIN");
    }

    public MYMCheckBalancePinForm2(UserDataContainer mFinoConfigData, Displayable parent, String pinLabel) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        mymCheckBalancePinForm = new Form(pinLabel);
        pin = new TextField(pinLabel, null, 4, TextField.PASSWORD|TextField.NUMERIC);
        mymCheckBalancePinForm.append(pin);
        mymCheckBalancePinForm.addCommand(mFinoConfigData.backCommand);
        mymCheckBalancePinForm.addCommand(mFinoConfigData.nextCommand);
        mymCheckBalancePinForm.setCommandListener(this);
        display.setCurrent(mymCheckBalancePinForm);
    }

    public MYMCheckBalancePinForm2(UserDataContainer mFinoConfigData) {
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
                mFinoConfigData.setServiceName(Constants.SERVICE_WALLET);
                mFinoConfigData.setTransactionName(Constants.TRANSACTION_CHECKBALANCE);
                mFinoConfigData.setSourcePin(pin.getString().trim());

                WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
                ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();

                if ("4".equals(responseData.getMsgCode())) {
                    receiptMessage = responseData.getMsg();
                } else if ("Currency".equals(responseData.getMsgCode())) {
                    receiptMessage = "E-Money Balance " + responseData.getMsg() + " on " + responseData.getTransactionTime();
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
