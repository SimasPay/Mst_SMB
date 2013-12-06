/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.activation;

import com.mfino.handset.agent.eaZyMoneyAgent;
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
public class AgentActivationForm1 extends CustomizedForm implements ActionListener {

    private AgentDataContainer mfinoConfigData;
    private CustomTextField tfAgentMDN;
    private CustomTextField tfOTP;

    public AgentActivationForm1(AgentDataContainer mcd) {
        this.mfinoConfigData = mcd;
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        CustomLabel lblAgentMDN = new CustomLabel("Mobile Number");
        CustomLabel lblOTP = new CustomLabel("Activation Key");

//        this.previousForm = mfinoConfigData.getPreviousForm();

        tfOTP = new CustomTextField(CustomTextField.PASSWORD);
        tfOTP.setHint("Activation Key");

        tfAgentMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfAgentMDN.setHint("Mobile Number");

        Command cmd1 = new Command("OK", 1);
        Command cmd2 = new Command("Back", 2);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblAgentMDN);
        this.addComponent(tfAgentMDN);

        this.addComponent(lblOTP);
        this.addComponent(tfOTP);

        this.setTitle("Deatils");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }
//2869 444333

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {
//            mfinoConfigData.setSourceMdn(tfAgentMDN.getText());
            if (StringUtil.isBlank(tfOTP.getText()) || StringUtil.isBlank(tfAgentMDN.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            try {
                mfinoConfigData.setSourceMdn(tfAgentMDN.getText());
            } catch (Exception ex) {
            }
            mfinoConfigData.setOTP(tfOTP.getText());
//            mfinoConfigData.setServiceName(Constants.SERVICE_ACCOUNT);
//            mfinoConfigData.setTransactionName(Constants.TRANSACTION_AGENTACTIVATION);

//            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mfinoConfigData);
//            ResponseData rd = wrapper.getResponseData();

            mfinoConfigData.setPreviousForm(this);

//            TransactionResultForm form = new  TransactionResultForm(mfinoConfigData,rd);
            NewPinConfirmPinForm2 form = new NewPinConfirmPinForm2(mfinoConfigData);
            form.show();
        } else if (ae.getCommand().getId() == 2) {
            eaZyMoneyAgent midley = this.mfinoConfigData.getMobileBankingMidlet();
            this.mfinoConfigData = null;
            midley.startApp();
        }
    }
}