/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.activation;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
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
public class NewPinConfirmPinForm2 extends CustomizedForm implements ActionListener {

    private AgentDataContainer SubscriberDataContainerData;
    private CustomTextField tfNewPin;
    private CustomTextField tfConfirmPin;
//    private CustomTextField tfChannelCode;

    public NewPinConfirmPinForm2(AgentDataContainer mcd) {
        this.SubscriberDataContainerData = mcd;
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
//        CustomLabel lblChannelCode = new CustomLabel("ChannelCode");
        CustomLabel lblNewpin = new CustomLabel("Enter New pin");
        CustomLabel lblConfirmPin = new CustomLabel("Confirm New Pin");

        this.previousForm = SubscriberDataContainerData.getPreviousForm();

        tfNewPin = new CustomTextField(CustomTextField.PASSWORD);
        tfNewPin.setHint("Pin");

        tfConfirmPin = new CustomTextField(CustomTextField.PASSWORD);
        tfNewPin.setHint("Repeat Pin");

//        tfChannelCode = new CustomTextField();
//        tfChannelCode.setHint("channel Code");

        Command cmd1 = new Command("OK", 1);
        Command cmd2 = new Command("Back", 2);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblNewpin);
        this.addComponent(tfNewPin);

        this.addComponent(lblConfirmPin);
        this.addComponent(tfConfirmPin);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfNewPin.getText()) || StringUtil.isBlank(tfConfirmPin.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }

            SubscriberDataContainerData.setPreviousForm(this);
            SubscriberDataContainerData.setActivationNewPin(tfNewPin.getText());
            SubscriberDataContainerData.setActivationConfirmPin(tfConfirmPin.getText());
            LegalText3 form = new LegalText3(SubscriberDataContainerData);
            form.show();

        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new AgentActivationForm1(SubscriberDataContainerData);
            }
            this.previousForm.show();
        }
    }
}