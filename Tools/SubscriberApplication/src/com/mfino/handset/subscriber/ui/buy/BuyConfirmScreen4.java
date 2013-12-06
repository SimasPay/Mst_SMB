package com.mfino.handset.subscriber.ui.buy;

import com.mfino.handset.subscriber.constants.Constants;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.ResponseReceiptScreen;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
import com.mfino.handset.subscriber.http.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 *
 */
public class BuyConfirmScreen4 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form tfcashoutConfirmForm;
    private StringItem stringItem;

    public BuyConfirmScreen4(UserDataContainer mFinoConfigData, Displayable parent, String confirmMessage) {
        super(mFinoConfigData);
        this.parent = parent;
        stringItem = new StringItem("", "");
        stringItem.setText(confirmMessage);
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        tfcashoutConfirmForm = new Form("Confirm");
        tfcashoutConfirmForm.append(stringItem);
        tfcashoutConfirmForm.addCommand(mFinoConfigData.cancelCommand);
        tfcashoutConfirmForm.addCommand(mFinoConfigData.continueCommand);
        tfcashoutConfirmForm.setCommandListener(this);
        display.setCurrent(tfcashoutConfirmForm);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.cancelCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.continueCommand) {

            mFinoConfigData.setTransactionName(Constants.TRANSACTION_PURCHASE);
            mFinoConfigData.setConfirmed(Constants.CONSTANT_VALUE_TRUE);

            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mFinoConfigData);
            ResponseDataContainer responseData = wrapper.getResponseData();

            String receiptMessage = responseData.getMsgCode() + " : " + responseData.getMsg();
            new ResponseReceiptScreen(mFinoConfigData, displayable, receiptMessage);
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
