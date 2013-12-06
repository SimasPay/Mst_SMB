package com.mfino.handset.subscriber.ui.manageyourmoney.transfers.friends;

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

/**
 * @author sasidhar
 *
 */
public class TransferPinForm3 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form pinForm;
    private TextField pin;

    public TransferPinForm3(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Enter PIN");
    }

    public TransferPinForm3(UserDataContainer mFinoConfigData, Displayable parent, String pinLabel) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        pinForm = new Form(pinLabel);
        pin = new TextField(pinLabel, null, 4, TextField.PASSWORD | TextField.NUMERIC);
        pinForm.append(pin);
        pinForm.addCommand(mFinoConfigData.backCommand);
        pinForm.addCommand(mFinoConfigData.sendCommand);
        pinForm.setCommandListener(this);
        display.setCurrent(pinForm);
    }

    public TransferPinForm3(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.sendCommand) {
            String receiptMessage = "";

            if (ConfigurationUtil.isBlank(pin.getString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setSourcePin(pin.getString());
                mFinoConfigData.setServiceName(Constants.SERVICE_WALLET);
                mFinoConfigData.setTransactionName(Constants.TRANSACTION_TRANSFER_INQUIRY);

                WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
                ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();

                if (!Constants.NOTIFICATIONCODE_TRANSFERINQUIRY_SUCCESS.equals(responseData.getMsgCode())) {
                    receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg();
                    new ResponseReceiptScreen(mFinoConfigData, displayable, receiptMessage);
                } else {
                    mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
                    mFinoConfigData.setTransferId(responseData.getTransferId());
                    mFinoConfigData.setDebitAmount(responseData.getDebitAmount());
                    mFinoConfigData.setTransactioncharges(responseData.getTransactionCharges());
                    mFinoConfigData.setCreditAmount(responseData.getCreditAmount());
                    String confirm = "Txn: Transfer? \n";
                    confirm = confirm + "Source : EMoney \n";
                    confirm = confirm + "Amount :" + mFinoConfigData.getDebitAmount() + " \n";
                    confirm = confirm + "Charges:" + mFinoConfigData.getTransactioncharges();

                    new TransferConfirmScreen4(mFinoConfigData, displayable, confirm);
                }
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
