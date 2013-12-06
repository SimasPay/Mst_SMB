package com.mfino.handset.agent.services.account.changepin;

import com.mfino.handset.agent.services.listeners.IResponseListener;
import com.mfino.handset.agent.services.TransactionResultForm;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.account.AccountServices;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.widgets.CustomLabel;
import com.mfino.handset.agent.widgets.CustomTextField;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 *
 * @author karthik
 */
public class ChangepinForm1 extends CustomizedForm implements ActionListener, IResponseListener {

    AgentDataContainer agentDataContainer;
    CustomTextField tfSourcePin;
    CustomTextField tfNewPin;
    CustomTextField tfConfirmPin;
//    Dialog waitingDialog;

    public ChangepinForm1(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.agentDataContainer = mcd;

        this.previousForm = agentDataContainer.getPreviousForm();

        CustomLabel lblSourcePin = new CustomLabel("Pin");
        CustomLabel lblNewpin = new CustomLabel("New Pin");
        CustomLabel lblConfirmPin = new CustomLabel("Confirm New Pin");

        tfSourcePin = new CustomTextField(CustomTextField.PASSWORD);
        tfSourcePin.setHint("pin");
//        tfSourcePin.setEditable(false);

        tfNewPin = new CustomTextField(CustomTextField.PASSWORD);
        tfNewPin.setHint("New pin");

        tfConfirmPin = new CustomTextField(CustomTextField.PASSWORD);
        tfConfirmPin.setHint("Confirm Pin");

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblSourcePin);
        this.addComponent(tfSourcePin);

        this.addComponent(lblNewpin);
        this.addComponent(tfNewPin);

        this.addComponent(lblConfirmPin);
        this.addComponent(tfConfirmPin);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfSourcePin.getText())
                    || StringUtil.isBlank(tfNewPin.getText())
                    || StringUtil.isBlank(tfConfirmPin.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            if (!tfNewPin.getText().equals(tfConfirmPin.getText())) {
                Dialog d = CustomDialogs.createDialog("new pin and confirm pin are not same", this);
                d.show();
                return;
            }
            agentDataContainer.setSourcePin(tfSourcePin.getText());
            agentDataContainer.setServiceName(Constants.SERVICE_ACCOUNT);
            agentDataContainer.setTransactionName(Constants.TRANSACTION_CHANGEPIN);
            agentDataContainer.setNewPin(tfNewPin.getText());
            agentDataContainer.setConfirmPin(tfConfirmPin.getText());
            agentDataContainer.setPreviousForm(this);

            ChangePinConfirmation2 form = new ChangePinConfirmation2(agentDataContainer);
            form.show();

            return;
        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new AccountServices(agentDataContainer);
            }
            this.previousForm.show();
        }
    }

    public void responseReceived(ResponseDataContainer rd) {
        if (rd == null) {
            Dialog d = CustomDialogs.createInvalidResponseDailog(previousForm);
            d.show();
            return;
        }
        agentDataContainer.setPreviousForm(this);
        TransactionResultForm form = new TransactionResultForm(agentDataContainer, rd);
        form.show();
    }
}