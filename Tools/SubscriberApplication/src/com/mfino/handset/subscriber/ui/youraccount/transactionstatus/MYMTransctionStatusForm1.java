package com.mfino.handset.subscriber.ui.youraccount.transactionstatus;

import com.mfino.handset.subscriber.constants.Constants;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.TextField;

/**
 * @author sasidhar
 */
public class MYMTransctionStatusForm1 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form manageYourMoneyForm;
    private TextField tfTransactionID;

    public MYMTransctionStatusForm1(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        manageYourMoneyForm = new Form("Enter TransacationID");
        tfTransactionID = new TextField("Transaction ID", "", 14, TextField.NUMERIC);

        manageYourMoneyForm.append(tfTransactionID);
        manageYourMoneyForm.addCommand(mFinoConfigData.backCommand);
        manageYourMoneyForm.addCommand(mFinoConfigData.nextCommand);
        manageYourMoneyForm.setCommandListener(this);
        display.setCurrent(manageYourMoneyForm);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {
            String tranID = tfTransactionID.getString();
            if (ConfigurationUtil.isBlank(tranID)) {
                alert = new Alert("Error", "Transaction ID can not be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setTransferId(tranID);
                new MYMTransactionStatusPinForm2(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
