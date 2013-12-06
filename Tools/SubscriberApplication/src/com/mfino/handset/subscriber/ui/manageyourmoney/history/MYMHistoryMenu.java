package com.mfino.handset.subscriber.ui.manageyourmoney.history;

import com.mfino.handset.subscriber.constants.Constants;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

/**
 * @author sasidhar
 */
public class MYMHistoryMenu extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form historyForm;
    private ChoiceGroup choiceGroup;
    private static final String E_MONEY = Constants.CONSTANT_EMONEY;
    private static final String BANK = Constants.CONSTANT_BANK;
    private static final String[] yourAccountMenu = {E_MONEY, BANK};

    public MYMHistoryMenu(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());

        historyForm = new Form("History");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        historyForm.append(choiceGroup);
        historyForm.addCommand(mFinoConfigData.backCommand);
        historyForm.addCommand(mFinoConfigData.nextCommand);
        historyForm.setCommandListener(this);
        display.setCurrent(historyForm);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(E_MONEY)) {
                mFinoConfigData.setServiceName(Constants.SERVICE_WALLET);
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                new MYMHistoryPinForm(mFinoConfigData, displayable);
            } else if (name.equals(BANK)) {
                mFinoConfigData.setServiceName(Constants.SERVICE_BANK);
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_BANK);
                new MYMHistoryPinForm(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
