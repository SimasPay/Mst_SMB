/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.servicetrxns.cashin;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.services.servicetrxns.ServiceTrxns;
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
public class AgentCashinInquiryForm1 extends CustomizedForm implements ActionListener {

    AgentDataContainer mfinoConfigData;
    CustomTextField tfDestMDN;
    CustomTextField tfAmount;

    public AgentCashinInquiryForm1(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;

        this.previousForm = mfinoConfigData.getPreviousForm();

        CustomLabel lblDestMDN = new CustomLabel("Enter Mobile");
        CustomLabel lblAmount = new CustomLabel("Enter Amount");

        tfDestMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfDestMDN.setHint("Mobile No");

        tfAmount = new CustomTextField(CustomTextField.NUMERIC);
        tfAmount.setHint("Amount");
        tfAmount.setMaxSize(Constants.FIELD_LENGTH_AMOUNT);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblDestMDN);
        this.addComponent(tfDestMDN);

        this.addComponent(lblAmount);
        this.addComponent(tfAmount);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfAmount.getText())||StringUtil.isBlank(tfDestMDN.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            mfinoConfigData.setPreviousForm(this);
            mfinoConfigData.setAmount(tfAmount.getText());
            mfinoConfigData.setDestinationMdn(tfDestMDN.getText());
            PinForm2 form = new PinForm2(mfinoConfigData);
            form.show();

        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new ServiceTrxns(mfinoConfigData);
            }
            this.previousForm.show();
        }
    }
}
