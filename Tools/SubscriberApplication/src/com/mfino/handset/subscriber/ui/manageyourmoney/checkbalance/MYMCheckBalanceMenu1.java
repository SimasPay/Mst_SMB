package com.mfino.handset.subscriber.ui.manageyourmoney.checkbalance;

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
public class MYMCheckBalanceMenu1 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form manageYourMoneyForm;
    private ChoiceGroup choiceGroup;
    private static final String[] yourAccountMenu = {Constants.LABEL_EMONEY,Constants.LABEL_BANK};

    public MYMCheckBalanceMenu1(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());

        manageYourMoneyForm = new Form("Select Source");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        manageYourMoneyForm.append(choiceGroup);
        manageYourMoneyForm.addCommand(mFinoConfigData.backCommand);
        manageYourMoneyForm.addCommand(mFinoConfigData.nextCommand);
        manageYourMoneyForm.setCommandListener(this);
        display.setCurrent(manageYourMoneyForm);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());
            
            if(Constants.LABEL_BANK.equals(name)){
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_BANK);
            }
            else{
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
            }
            new MYMCheckBalancePinForm2(mFinoConfigData, displayable);
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
