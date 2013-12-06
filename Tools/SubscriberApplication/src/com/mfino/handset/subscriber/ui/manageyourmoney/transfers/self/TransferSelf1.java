package com.mfino.handset.subscriber.ui.manageyourmoney.transfers.self;

import com.mfino.handset.subscriber.constants.Constants;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;

/**
 * @author sasidhar
 *
 */
public class TransferSelf1 extends AbstractMfinoConfig implements CommandListener {

    public Display display;
    private Displayable parent;
    private Form form;
    private ChoiceGroup choiceGroup;
    private static final String EMoneyToBank = Constants.FEATURE_TRANSFER_EAZYTOBANK;
    private static final String BankToEMoney = Constants.FEATURE_TRANSFER_BANKTOEAZY;
    private static final String[] yourAccountMenu = {EMoneyToBank, BankToEMoney};

    public TransferSelf1(UserDataContainer mfinoConfigData, Displayable parent) {
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
            if (EMoneyToBank.equals(name)) {
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                mFinoConfigData.setDestinationPocketCode(Constants.POCKET_CODE_BANK);
                mFinoConfigData.setServiceName(Constants.SERVICE_WALLET);
            } else if (BankToEMoney.equals(name)) {
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_BANK);
                mFinoConfigData.setDestinationPocketCode(Constants.POCKET_CODE_EMONEY);
                mFinoConfigData.setServiceName(Constants.SERVICE_BANK);
            }
            mFinoConfigData.setDestinationMdn(mFinoConfigData.getSourceMdn());
            new SelfTransferAmountForm2(mFinoConfigData, displayable);
        }
    }
}
