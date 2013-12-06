package com.mfino.handset.subscriber.ui.manageyourmoney.transfers;

import com.mfino.handset.subscriber.constants.Constants;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.manageyourmoney.transfers.friends.TransferDestinationMdnForm1;
import com.mfino.handset.subscriber.ui.manageyourmoney.transfers.self.TransferSelf1;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;

/**
 * @author sasidhar
 *
 */
public class Transfers extends AbstractMfinoConfig implements CommandListener {

    public Display display;
    private Displayable parent;
    private Form form;
    private ChoiceGroup choiceGroup;
    private static final String SelfTransfer = Constants.FEATURE_TRANSFER_SELF;
    private static final String OtherTransfer = Constants.FEATURE_TRANSFER_OTHERS;
    private static final String[] yourAccountMenu = {SelfTransfer, OtherTransfer};

    public Transfers(UserDataContainer mfinoConfigData, Displayable parent) {
        super(mfinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());

        form = new Form("Select Transfer Type");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        form.append(choiceGroup);
        form.addCommand(mFinoConfigData.backCommand);
        form.addCommand(mFinoConfigData.nextCommand);
        form.setCommandListener(this);
        display.setCurrent(form);

    }

    public void commandAction(Command command, Displayable displayable) {

        if (command == mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == mFinoConfigData.nextCommand) {
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());
            if (SelfTransfer.equals(name)) {
                new TransferSelf1(mFinoConfigData, displayable);
            } else if (OtherTransfer.equals(name)) {
                new TransferDestinationMdnForm1(mFinoConfigData, displayable);
            }
        }
    }
}
