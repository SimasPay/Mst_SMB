/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.registration;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.agentservices.AgentSpecificServices;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
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
public class SubscriberRegistrationForm1 extends CustomizedForm implements ActionListener {

    UserDataContainer mfinoConfigData;
    CustomTextField tfAgentMDN;
    CustomTextField tfAgentPin;

    public SubscriberRegistrationForm1(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;
        CustomLabel lblAgentPin = new CustomLabel("Pin");
        CustomLabel lblAgentMDN = new CustomLabel("Mobile No");
        this.previousForm = mfinoConfigData.getPreviousFormHolder();
        tfAgentMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfAgentMDN.setText(mfinoConfigData.getSourceMdn());
        tfAgentMDN.setEditable(false);

        tfAgentPin = new CustomTextField(TextArea.PASSWORD);
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

//            mfinoConfigData.setSourceMdn(tfAgentMDN.getText());

            if (StringUtil.isBlank(tfAgentPin.getText())) {
                Dialog d =CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }

            mfinoConfigData.setSourcePin(tfAgentPin.getText());

//            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mfinoConfigData);
//            ResponseData rd = wrapper.getResponseData();
            mfinoConfigData.setPreviousFormHolder(this);
//            TransactionResultForm form = new  TransactionResultForm(mfinoConfigData,rd);
            SubscriberMDNAccountTypeForm2 form = new  SubscriberMDNAccountTypeForm2(mfinoConfigData);

            form.show();

        } else if (ae.getCommand().getId() == 2) {

            if(this.previousForm==null)
                this.previousForm = new AgentSpecificServices(mfinoConfigData);
            
            this.previousForm.show();
//            mfinoConfigData.getPreviousFormHolder().show();

        }


    }
}
