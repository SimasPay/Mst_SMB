/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.transfer.agenttoagent;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.services.transfer.TransferServices1;
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
public class AgentCodeAmountForm1 extends CustomizedForm implements ActionListener {

    AgentDataContainer userAppData;
    CustomTextField tfAmount;
    CustomTextField tfAgentCode;

    public AgentCodeAmountForm1(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.userAppData = mcd;

        this.previousForm = userAppData.getPreviousForm();

        CustomLabel lblAmount = new CustomLabel("Amount");
        CustomLabel lblAgentCode = new CustomLabel("Agent Code");

        tfAmount = new CustomTextField(CustomTextField.NUMERIC);
        tfAmount.setMaxSize(Constants.FIELD_LENGTH_AMOUNT);

        tfAgentCode = new CustomTextField();
        int a=10;a=a+10;
        tfAgentCode.setMaxSize(Constants.FIELD_LENGTH_AGENTCODE);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblAgentCode);
        this.addComponent(tfAgentCode);

        this.addComponent(lblAmount);
        this.addComponent(tfAmount);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfAmount.getText()) || StringUtil.isBlank(tfAgentCode.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            userAppData.setPreviousForm(this);
            userAppData.setAmount(tfAmount.getText());
            userAppData.setPartnerCode(tfAgentCode.getText());

            PinForm2 form = new PinForm2(userAppData);
            form.show();

        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new TransferServices1(userAppData);
            }
            this.previousForm.show();
        }
    }
}