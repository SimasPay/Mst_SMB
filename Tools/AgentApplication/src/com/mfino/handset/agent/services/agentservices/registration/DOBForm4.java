/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.registration;

import com.mfino.handset.agent.constants.Constants;
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
public class DOBForm4 extends CustomizedForm implements ActionListener {

    UserDataContainer mfinoConfigData;
    CustomTextField tfDateOfBirth;
//    CustomTextField tfAmount;

    public DOBForm4(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;
        this.previousForm = mfinoConfigData.getPreviousFormHolder();

        CustomLabel lblDOB = new CustomLabel("DateofBirth");
//        CustomLabel lblAmount = new CustomLabel("Amount");

        tfDateOfBirth = new CustomTextField(CustomTextField.NUMERIC);
        tfDateOfBirth.setHint("DDMMYYYY");

//        tfAmount = new CustomTextField(CustomTextField.DECIMAL);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Next", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblDOB);
        this.addComponent(tfDateOfBirth);

//        this.addComponent(lblAmount);
//        this.addComponent(tfAmount);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfDateOfBirth.getText())) {
                Dialog d=CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }

//            mfinoConfigData.setAmount(tfAmount.getText());
            mfinoConfigData.setDateOfBirth(tfDateOfBirth.getText());

            AppIdForm5 form = new  AppIdForm5(mfinoConfigData);
            form.show();

        } else if (ae.getCommand().getId() == 2) {
            
            if(this.previousForm == null)
                this.previousForm = new SubscriberNameForm3(mfinoConfigData);
            this.previousForm.show();
        }
    }
}
