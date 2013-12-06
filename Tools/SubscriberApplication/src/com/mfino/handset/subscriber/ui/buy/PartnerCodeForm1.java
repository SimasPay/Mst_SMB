package com.mfino.handset.subscriber.ui.buy;


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
public class PartnerCodeForm1 extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form formPartnerCode;
    private TextField tfPartnerCode;

    public PartnerCodeForm1(UserDataContainer mFinoConfigData, Displayable parent) {
        this(mFinoConfigData, parent, "Agent Code");
    }

    public PartnerCodeForm1(UserDataContainer mFinoConfigData, Displayable parent, String transferFromLabelStr) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        formPartnerCode = new Form(transferFromLabelStr);
        tfPartnerCode = new TextField(transferFromLabelStr, null, 20, TextField.ANY);
        formPartnerCode.append(tfPartnerCode);
        formPartnerCode.addCommand(mFinoConfigData.backCommand);
        formPartnerCode.addCommand(mFinoConfigData.nextCommand);
        formPartnerCode.setCommandListener(this);
        display.setCurrent(formPartnerCode);
    }

    public PartnerCodeForm1(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (ConfigurationUtil.isBlank(tfPartnerCode.getString())) {
                alert = new Alert("Error", "Agent code can not be empty", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setPartnerCode(tfPartnerCode.getString());
                mFinoConfigData.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                new BuyAmountForm2(mFinoConfigData, parent);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
