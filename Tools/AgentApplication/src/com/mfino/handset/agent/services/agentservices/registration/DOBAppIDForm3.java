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
public class DOBAppIDForm3 extends CustomizedForm implements ActionListener {

    AgentDataContainer mfinoConfigData;
    CustomTextField tfDateOfBirth;
//    CustomTextField tfAmount;
    CustomTextField tfAppID;

    public DOBAppIDForm3(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;
        this.previousForm = mfinoConfigData.getPreviousForm();

        CustomLabel lblDOB = new CustomLabel("DateofBirth");

        CustomLabel lblappID = new CustomLabel("App ID");
        tfAppID = new CustomTextField();
        tfAppID.setHint("Application ID");
        tfAppID.setMaxSize(Constants.FIELD_LENGTH_APPID);

        tfDateOfBirth = new CustomTextField();
        tfDateOfBirth.setHint("DD/MM/YYYY");
        tfDateOfBirth.setMaxSize(Constants.FIELD_LENGTH_DOB);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblDOB);
        this.addComponent(tfDateOfBirth);

        this.addComponent(lblappID);
        this.addComponent(tfAppID);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfDateOfBirth.getText()) || StringUtil.isBlank(tfAppID.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            String str = tfDateOfBirth.getText();
            if (!StringUtil.isValidDOB(str)) {
                Dialog d = CustomDialogs.createInvalidDateOfBirthDialog(this);
                d.show();
                return;
            }

            str = str.substring(0, 2) + str.substring(3, 5) + str.substring(6, 10);
            mfinoConfigData.setDateOfBirth(str);
            mfinoConfigData.setApplicationID(tfAppID.getText());
            mfinoConfigData.setServiceName(Constants.SERVICE_AGENT);
            mfinoConfigData.setTransactionName(Constants.TRANSACTION_SUBSCRIBERREGISTRATION);
            mfinoConfigData.setPreviousForm(this);

            AgentPinForm4 form = new AgentPinForm4(mfinoConfigData);
            form.show();

            return;

        } else if (ae.getCommand().getId() == 2) {

            if (this.previousForm == null) {
                this.previousForm = new SubscriberNameForm2(mfinoConfigData);
            }
            this.previousForm.show();
        }
    }
}
