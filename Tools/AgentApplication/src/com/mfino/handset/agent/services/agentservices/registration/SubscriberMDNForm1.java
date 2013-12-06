/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.registration;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.services.ServicesForAgent;
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
public class SubscriberMDNForm1 extends CustomizedForm implements ActionListener {

    AgentDataContainer mfinoConfigData;
    CustomTextField tfSubscriberMDN;
//    CustomTextField tfAccountType;

    public SubscriberMDNForm1(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;

        this.previousForm = mfinoConfigData.getPreviousForm();
        CustomLabel lblSubscriberMDN = new CustomLabel("Subscriber Mobile No");
//        CustomLabel lblAccountType = new CustomLabel("Account Type");

        tfSubscriberMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfSubscriberMDN.setHint("Subscriber Mobile No");

//        tfAccountType = new CustomTextField(CustomTextField.NUMERIC);
//        tfAccountType.setHint("Account Type");
//        tfAccountType.setMaxSize(1);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblSubscriberMDN);
        this.addComponent(tfSubscriberMDN);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {
            if (StringUtil.isBlank(tfSubscriberMDN.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            mfinoConfigData.setSubscriberMDN(tfSubscriberMDN.getText());
            mfinoConfigData.setAccountType("1");
            mfinoConfigData.setPreviousForm(this);
            SubscriberNameForm2 form = new SubscriberNameForm2(mfinoConfigData);
            form.show();
        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new ServicesForAgent(mfinoConfigData);
            }
            this.previousForm.show();
        }
    }
}
