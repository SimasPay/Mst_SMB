/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.transactionstatus;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.account.AccountServices;
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
public class TransactionStatusForm1 extends CustomizedForm implements ActionListener {

    private AgentDataContainer mfinoConfigData;
    private CustomTextField tfAgentMDN;
    private CustomTextField tfAgentPin;

    public TransactionStatusForm1(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;
        CustomLabel lblAgentMDN = new CustomLabel("Mobile No");
        CustomLabel lblAgentPin = new CustomLabel("Pin");
        this.previousForm = mfinoConfigData.getPreviousForm();

        tfAgentPin = new CustomTextField(CustomTextField.PASSWORD);
        tfAgentPin.setHint("Pin");
//        tfAgentPin.setText("123456");
        
        tfAgentMDN = new CustomTextField(CustomTextField.PHONENUMBER);
        tfAgentMDN.setText(mfinoConfigData.getSourceMdn());
        tfAgentMDN.setEditable(false);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
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
                Dialog d=CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            mfinoConfigData.setSourcePin(tfAgentPin.getText());
            mfinoConfigData.setServiceName(Constants.SERVICE_ACCOUNT);
            mfinoConfigData.setTransactionName(Constants.TRANSACTION_TRANSACTIONSTATUS);

//            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mfinoConfigData);
//            ResponseData rd = wrapper.getResponseData();
            mfinoConfigData.setPreviousForm(this);

//            TransactionResultForm form = new  TransactionResultForm(mfinoConfigData, rd);
            TransactionIDForm2 form = new  TransactionIDForm2(mfinoConfigData);
            
            

            form.show();
        } else if (ae.getCommand().getId() == 2) {
            if(this.previousForm==null)
                this.previousForm = new AccountServices(mfinoConfigData);                        
            this.previousForm.show();
        }
    }
}