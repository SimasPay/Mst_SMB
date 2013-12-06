package com.mfino.handset.subscriber.ui.buy;

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

/**
 * @author sasidhar
 *
 */
public class BuyPinForm3 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form pinForm;
    private TextField tfPin;

    public BuyPinForm3(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Enter your PIN");
    }

    public BuyPinForm3(UserDataContainer mFinoConfigData, Displayable parent, String pinLabel) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        pinForm = new Form(pinLabel);
        tfPin = new TextField(pinLabel, null, 4, TextField.PASSWORD|TextField.NUMERIC);
        pinForm.append(tfPin);
        pinForm.addCommand(mFinoConfigData.backCommand);
        pinForm.addCommand(mFinoConfigData.purchaseCommand);
        pinForm.setCommandListener(this);
        display.setCurrent(pinForm);
    }

    public BuyPinForm3(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.purchaseCommand) {

            if (ConfigurationUtil.isBlank(tfPin.toString())) {
                alert = new Alert("Error", "PIN cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setSourcePin(tfPin.getString());
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                mFinoConfigData.setServiceName(Constants.SERVICE_SHOPPING);
                mFinoConfigData.setTransactionName(Constants.TRANSACTION_PURCHASE_INQUIRY);

                WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
                ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();
                String receiptMessage = "";
                if (responseData.getParentTxnId() == null || responseData.getTransferId() == null) {
                    receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg();
                    new ResponseReceiptScreen(mFinoConfigData, displayable, receiptMessage);
                } else {

                    mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
                    mFinoConfigData.setTransferId(responseData.getTransferId());
                    mFinoConfigData.setCreditAmount(responseData.getCreditAmount());
                    mFinoConfigData.setDebitAmount(responseData.getDebitAmount());
                    mFinoConfigData.setTransactioncharges(responseData.getTransactionCharges());

                    String sourcePocketString = Constants.POCKET_CODE_EMONEY.equals(mFinoConfigData.getSourcePocketCode()) ? "E-Money Account" : "Bank Account";

                    String infoMsg = "Your " + sourcePocketString + " will be debited " + mFinoConfigData.getDebitAmount() + " "
                            + "and charges are " + mFinoConfigData.getTransactioncharges() + ". \n";
                    String confirmMessage = infoMsg + " \nConfirm?";
                    new BuyConfirmScreen4(mFinoConfigData, displayable, confirmMessage);
                }
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
