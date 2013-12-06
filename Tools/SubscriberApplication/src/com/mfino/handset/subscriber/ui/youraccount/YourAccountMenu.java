package com.mfino.handset.subscriber.ui.youraccount;

import com.mfino.handset.subscriber.ui.youraccount.cashout.MYMAgentCodeForm2;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.ui.manageyourmoney.checkbalance.MYMCheckBalanceMenu1;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.ui.manageyourmoney.history.MYMHistoryMenu;
import com.mfino.handset.subscriber.ui.youraccount.changepin.ChangePinOldPinForm1;

public class YourAccountMenu extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form yourAccountForm;
    private ChoiceGroup choiceGroup;
    private static final String CHECK_BALANCE = "Check Balance";
    private static final String HISTORY = "History";
//    private static final String PIN_MANAGEMENT = "Pin Management";
//    private static final String FUND = "Fund";
    private static final String CASHOUT = "Cash Out";
    private static final String CHANGEPIN = "Change PIN";
    private static final String[] yourAccountMenu = {CHANGEPIN, CHECK_BALANCE, HISTORY, CASHOUT};

    public YourAccountMenu(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());

        yourAccountForm = new Form("Account Management");
        choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, yourAccountMenu, null);
        yourAccountForm.append(choiceGroup);
        yourAccountForm.addCommand(mFinoConfigData.backCommand);
        yourAccountForm.addCommand(mFinoConfigData.nextCommand);
        yourAccountForm.setCommandListener(this);
        display.setCurrent(yourAccountForm);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(CHECK_BALANCE)) {
                new MYMCheckBalanceMenu1(mFinoConfigData, displayable);
            } else if (name.equals(HISTORY)) {
                new MYMHistoryMenu(mFinoConfigData, displayable);
            } else if (name.equals(CHANGEPIN)) {
                new ChangePinOldPinForm1(mFinoConfigData, parent);
            } else if (name.equals(CASHOUT)) {
                new MYMAgentCodeForm2(mFinoConfigData, parent);
            }
//            else if(name.equals(CHANGE_PIN)){
//                new ChangePinOldPinForm1(mFinoConfigData, parent);
//            }

//            } else if (name.equals(PIN_MANAGEMENT)) {
//                new PinManagementMenu(mFinoConfigData, displayable);
//            } else if (name.equals(FUND)) {
//                mFinoConfigData.setYourPhone(true);
//                mFinoConfigData.setYourWallet(true);
//                mFinoConfigData.setDestinationMdn(mFinoConfigData.getSourceMdn());
//                new MYMTransferFromPocketMenu2(mFinoConfigData, displayable);
//            }
            /*else if (name.equals(ACTIVATE)){
            mFinoConfigData.setServiceName("Activation");
            }*/
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
