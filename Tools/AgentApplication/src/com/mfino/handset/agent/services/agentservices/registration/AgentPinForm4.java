/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.registration;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.widgets.CustomLabel;
import com.mfino.handset.agent.widgets.CustomTextField;
import com.sun.lwuit.Command;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 *
 * @author karthik
 */
public class AgentPinForm4 extends CustomizedForm implements ActionListener {

    AgentDataContainer mfinoConfigData;
    CustomTextField tfAgentPin;

    public AgentPinForm4(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;
        this.previousForm = mfinoConfigData.getPreviousForm();

        CustomLabel lblAgentPin = new CustomLabel("Pin");
        tfAgentPin = new CustomTextField(TextArea.PASSWORD);
        tfAgentPin.setHint("Pin");

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblAgentPin);
        this.addComponent(tfAgentPin);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);


    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {
            if (StringUtil.isBlank(tfAgentPin.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            mfinoConfigData.setSourcePin(tfAgentPin.getText());
            mfinoConfigData.setPreviousForm(this);
            RegisterConfirmation5 form = new RegisterConfirmation5(mfinoConfigData);
            form.show();
        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new DOBAppIDForm3(mfinoConfigData);
            }
            this.previousForm.show();
        }
    }
}
