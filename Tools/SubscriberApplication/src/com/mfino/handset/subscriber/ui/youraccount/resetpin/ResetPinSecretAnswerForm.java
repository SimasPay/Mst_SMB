package com.mfino.handset.subscriber.ui.youraccount.resetpin;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.ui.AbstractMfinoConfig;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;

/**
 * @author sasidhar
 *
 */
public class ResetPinSecretAnswerForm extends AbstractMfinoConfig implements CommandListener {

    private Displayable parent;
    private Display display;
    private Form resetPinForm;
    private TextField secretAnswer;

    public ResetPinSecretAnswerForm(UserDataContainer mFinoConfigData, Displayable parent) {
        super(mFinoConfigData);
        this.parent = parent;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        resetPinForm = new Form("Reset PIN");
        secretAnswer = new TextField("Secret Answer", "", 14, TextField.ANY);
        resetPinForm.append(secretAnswer);
        resetPinForm.addCommand(mFinoConfigData.backCommand);
        resetPinForm.addCommand(mFinoConfigData.nextCommand);
        resetPinForm.setCommandListener(this);
        display.setCurrent(resetPinForm);
    }

    public ResetPinSecretAnswerForm(UserDataContainer mFinoConfigData) {
        super(mFinoConfigData);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == super.mFinoConfigData.backCommand) {
            display.setCurrent(parent);
        } else if (command == super.mFinoConfigData.nextCommand) {

            if (secretAnswer.getString().equals("")) {
                alert = new Alert("Error", "Secret Answer is required.", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                mFinoConfigData.setSecretAnswer(secretAnswer.getString().trim());
                new ResetPinNewPinForm(mFinoConfigData, displayable);
            }
        } else if (command == super.mFinoConfigData.exitCommand) {
            mFinoConfigData.getMobileBankingMidlet().destroyApp(true);
            mFinoConfigData.getMobileBankingMidlet().notifyDestroyed();
        }
    }
}
