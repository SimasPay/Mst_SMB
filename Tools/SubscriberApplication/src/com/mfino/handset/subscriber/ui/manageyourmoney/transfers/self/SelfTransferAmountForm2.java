package com.mfino.handset.subscriber.ui.manageyourmoney.transfers.self;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

/**
 * @author sasidhar
 *
 */
public class SelfTransferAmountForm2 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form amountForm;
    private TextField tfAmount;

    public SelfTransferAmountForm2(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        amountForm = new Form("Enter Amount");
        tfAmount = new TextField("Amount", "", 8, TextField.NUMERIC);
        amountForm.append(tfAmount);
        amountForm.addCommand(mFinoConfigData.backCommand);
        amountForm.addCommand(mFinoConfigData.nextCommand);
        amountForm.setCommandListener(this);
        display.setCurrent(amountForm);
    }

    public SelfTransferAmountForm2(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {
            if (ConfigurationUtil.isBlank(tfAmount.getString())) {
                alert = new Alert("Error", "Amount cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setAmount(tfAmount.getString());
                new SelfTransferPinForm3(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
