/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.cahsinconfirmation;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
import com.mfino.handset.agent.widgets.CustomLabel;
import com.mfino.handset.agent.widgets.CustomTextField;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Command;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 *
 * @author karthik
 */
public class CashinConfirmation1 extends CustomizedForm implements ActionListener {

    UserDataContainer mfinoConfigData;
    CustomTextField tfAgentMDN;
    CustomTextField tfDestMDN;
    CustomTextField tfDestAccountType;

    public CashinConfirmation1(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;

        this.previousForm = mfinoConfigData.getPreviousFormHolder();

        CustomLabel lblAgentMDN = new CustomLabel("Mobile No");
        CustomLabel lblDestMDN = new CustomLabel("Dest mobile No");
        CustomLabel lblDestAccountType = new CustomLabel("Dest Account");

        tfAgentMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfAgentMDN.setText(mfinoConfigData.getSourceMdn());
        tfAgentMDN.setEditable(false);

        tfDestMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfDestMDN.setText(mfinoConfigData.getDestinationMdn());
        tfDestMDN.setEditable(false);

        tfDestAccountType = new CustomTextField();
        if(Constants.POCKET_CODE_EMONEY.equals(mfinoConfigData.getDestinationPocketCode()))
            tfDestAccountType.setText(Constants.CONSTANT_EMONEY);
        else if(Constants.POCKET_CODE_BANK.equals(mfinoConfigData.getDestinationPocketCode()))
            tfDestAccountType.setText(Constants.CONSTANT_BANK);
        tfDestMDN.setEditable(false);

//        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Next", 1);
//        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblAgentMDN);
        this.addComponent(tfAgentMDN);

        this.addComponent(lblDestMDN);
        this.addComponent(tfDestMDN);

        this.addComponent(lblDestAccountType);
        this.addComponent(tfDestAccountType);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            mfinoConfigData.setPreviousFormHolder(this);
            CashinConfirmationAmounts2 form = new  CashinConfirmationAmounts2(mfinoConfigData);
            form.show();
        } else if (ae.getCommand().getId() == 2) {
//            this.previousForm.show();
        }
    }
    private class ShowListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
//            if(CashinConfirmation1.this.mfinoConfigData.getSourceMdn()!=null)
//                CashinConfirmation1.this.tfAgentMDN.setText(CashinConfirmation1.this.mfinoConfigData.getSourceMdn());
//            if(CashinConfirmation1.this.mfinoConfigData.getDestinationMdn()!=null)
//                CashinConfirmation1.this.tfDestMDN.setText(CashinConfirmation1.this.mfinoConfigData.getDestinationMdn());
//            if(CashinConfirmation1.this.mfinoConfigData.getDestinationPocketCode()!=null)
//                CashinConfirmation1.this.tfDestAccountType.setText(CashinConfirmation1.this.mfinoConfigData.getSourceMdn());
        }
    }
}
