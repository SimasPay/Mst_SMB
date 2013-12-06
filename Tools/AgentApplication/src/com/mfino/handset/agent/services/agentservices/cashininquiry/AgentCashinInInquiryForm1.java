/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.cashininquiry;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.agentservices.AgentSpecificServices;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
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
public class AgentCashinInInquiryForm1 extends CustomizedForm implements ActionListener {

    UserDataContainer mfinoConfigData;
    CustomTextField tfAgentMDN;
    CustomTextField tfAgentPin;

    public AgentCashinInInquiryForm1(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;

        this.previousForm = mfinoConfigData.getPreviousFormHolder();

        CustomLabel lblAgentMDN = new CustomLabel("Mobile No");
        CustomLabel lblAgentPin = new CustomLabel("Pin");

        tfAgentMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfAgentMDN.setText(mfinoConfigData.getSourceMdn());
        tfAgentMDN.setEditable(false);

        tfAgentPin = new CustomTextField(CustomTextField.PASSWORD);
        tfAgentPin.setHint("Pin");
//        tfAgentPin.setText("123456");
        
        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Next", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblAgentMDN);
        this.addComponent(tfAgentMDN);

        this.addComponent(lblAgentPin);
        this.addComponent(tfAgentPin);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfAgentPin.getText())) {
                Dialog d=CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            mfinoConfigData.setPreviousFormHolder(this);
            mfinoConfigData.setSourcePin(tfAgentPin.getText());
            DestMDNAccountTypeForm2 form = new  DestMDNAccountTypeForm2(mfinoConfigData);
            form.show();

        } else if (ae.getCommand().getId() == 2) {
            if(this.previousForm == null)
                this.previousForm = new AgentSpecificServices(mfinoConfigData);
            this.previousForm.show();
        }
    }
}
