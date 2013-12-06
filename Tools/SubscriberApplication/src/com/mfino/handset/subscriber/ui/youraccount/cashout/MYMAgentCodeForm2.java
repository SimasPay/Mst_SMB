package com.mfino.handset.subscriber.ui.youraccount.cashout;


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
public class MYMAgentCodeForm2 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form formAgentCode;
    private TextField tfAgentCode;

    public MYMAgentCodeForm2(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Agent Code");
    }

    public MYMAgentCodeForm2(UserDataContainer mFinoConfigData, Displayable parent, String transferFromLabelStr) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        formAgentCode = new Form(transferFromLabelStr);
        tfAgentCode = new TextField(transferFromLabelStr, null, 20, TextField.ANY);
        formAgentCode.append(tfAgentCode);
        formAgentCode.addCommand(mFinoConfigData.backCommand);
        formAgentCode.addCommand(mFinoConfigData.nextCommand);
        formAgentCode.setCommandListener(this);
        display.setCurrent(formAgentCode);
    }

    public MYMAgentCodeForm2(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(tfAgentCode.getString())) {
                alert = new Alert("Error", "Agent code can not be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setAgentCode(tfAgentCode.getString());
                new MYMCashoutAmountForm3(mFinoConfigData, parent);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
