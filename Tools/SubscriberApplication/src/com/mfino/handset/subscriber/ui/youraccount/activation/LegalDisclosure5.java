package com.mfino.handset.subscriber.ui.youraccount.activation;

import com.mfino.handset.subscriber.constants.Constants;
import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
import com.mfino.handset.subscriber.ui.*;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.http.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 *
 */
public class LegalDisclosure5 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form topUpReceiptForm;
    private StringItem stringItem;

    public LegalDisclosure5(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        stringItem = new StringItem("", "");
        stringItem.setText("Your consent through your eaZyMoney application is your consent to the terms and"
                + " conditions of this service. Zenith Bank retains the right to amend these terms any time without notice.");
        topUpReceiptForm = new Form("Legal Disclosure");
        topUpReceiptForm.append(stringItem);
        topUpReceiptForm.addCommand(mFinoConfigData.cancelCommand);
        topUpReceiptForm.addCommand(mFinoConfigData.agreeCommand);
        topUpReceiptForm.setCommandListener(this);
        display.setCurrent(topUpReceiptForm);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.agreeCommand) {
            mFinoConfigData.setTransactionName(Constants.TRANSACTION_ACTIVATION);
            mFinoConfigData.setServiceName(Constants.SERVICE_ACCOUNT);
            WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
            ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();
            String receiptMessage = responseData.getMsgCode() + ":" + responseData.getMsg();
            new ActivationResponseReceiptScreen(mFinoConfigData, displayable, receiptMessage);
        } else if (command == super.mFinoConfigData.cancelCommand) {
            mFinoConfigData.setAESKey(null);
            mFinoConfigData.setSourcePin(null);
            mFinoConfigData.setSourceMdn(null);
            mFinoConfigData.getMobileBankingMidlet().startApp();
        }
    }
}
