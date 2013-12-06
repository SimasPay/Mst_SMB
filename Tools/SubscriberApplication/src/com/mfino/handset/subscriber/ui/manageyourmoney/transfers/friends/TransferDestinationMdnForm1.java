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
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

/**
 * @author sasidhar
 *
 */
public class TransferDestinationMdnForm1 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form topUpDestinationMdnForm;
    private TextField destinationMdn;

    public TransferDestinationMdnForm1(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        topUpDestinationMdnForm = new Form("Destination Phone#");
        destinationMdn = new TextField("Enter reciepient's Phone#", "", 13, TextField.PHONENUMBER);
        topUpDestinationMdnForm.append(destinationMdn);
        topUpDestinationMdnForm.addCommand(mFinoConfigData.backCommand);
        topUpDestinationMdnForm.addCommand(mFinoConfigData.nextCommand);
        topUpDestinationMdnForm.setCommandListener(this);
        display.setCurrent(topUpDestinationMdnForm);
    }

    public TransferDestinationMdnForm1(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(destinationMdn.getString())) {
                alert = new Alert("Error", "Phone# cannot be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setDestinationMdn(destinationMdn.getString().trim());
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                mFinoConfigData.setDestinationPocketCode(Constants.POCKET_CODE_EMONEY);
                new TransferAmountForm2(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
