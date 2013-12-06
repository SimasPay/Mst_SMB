/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.cahsinconfirmation;

import com.mfino.handset.agent.IResponseReceivedListener;
import com.mfino.handset.agent.TransactionResultForm;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.http.HttpExecutorThread;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
import com.mfino.handset.agent.widgets.CustomLabel;
import com.mfino.handset.agent.widgets.CustomTextField;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 *
 * @author karthik
 */
public class CashinConfirmationAmounts2 extends CustomizedForm implements ActionListener, IResponseReceivedListener {

    UserDataContainer mfinoConfigData;
    CustomTextField tfDebitAmount;
    CustomTextField tfCreditAmount;
    CustomTextField tfCharges;
//    CustomTextField tfChannelCode;
    CheckBox cb;

    public CashinConfirmationAmounts2(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;

        this.previousForm = mfinoConfigData.getPreviousFormHolder();

        CustomLabel lblDebitAmount = new CustomLabel("You will be debited:");
        CustomLabel lblCreditAmount = new CustomLabel("User will be credtied:");
        CustomLabel lblCharges = new CustomLabel("Charges");
//        CustomLabel lblChannelCode = new CustomLabel("Channel Code");

        tfDebitAmount = new CustomTextField(CustomTextField.NUMERIC);
        tfDebitAmount.setText(mfinoConfigData.getDebitAmount());
        tfDebitAmount.setEditable(false);

        tfCreditAmount = new CustomTextField(CustomTextField.NUMERIC);
        tfCreditAmount.setText(mfinoConfigData.getCreditAmount());
        tfCreditAmount.setEditable(false);

        tfCharges = new CustomTextField(CustomTextField.NUMERIC);
        tfCharges.setText(mfinoConfigData.getTransactioncharges());
        tfCharges.setEditable(false);

//        tfChannelCode = new CustomTextField();
//        tfChannelCode.setText(mfinoConfigData.getChannelId());
//        tfChannelCode.setEditable(false);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Submit", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblDebitAmount);
        this.addComponent(tfDebitAmount);

        this.addComponent(lblCreditAmount);
        this.addComponent(tfCreditAmount);

        this.addComponent(lblCharges);
        this.addComponent(tfCharges);

//        this.addComponent(lblChannelCode);
//        this.addComponent(tfChannelCode);

        cb = new CheckBox("Confirm");
        cb.addFocusListener(new CBFocusListner(cb));
        cb.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.addComponent(cb);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

//            mfinoConfigData.setSourceMdn(tfAgentMDN.getText());
//            mfinoConfigData.setChannelId(Constants.CONSTANT_CHANNEL_ID);
//            mfinoConfigData.setAmount(tfDestMDN.getText());
//            mfinoConfigData.setPartnerCode(tfDebitAmount.getText());
//            mfinoConfigData.setServiceName(Constants.SERVICE_AGENT);
            mfinoConfigData.setTransactionName(Constants.TRANSACTION_CASHIN);
            if (cb.isSelected()) {
                mfinoConfigData.setConfirmed(Constants.CONSTANT_VALUE_TRUE);
                HttpExecutorThread thread = new HttpExecutorThread(mfinoConfigData, this);
                thread.start();
            } else {
                mfinoConfigData.setConfirmed(Constants.CONSTANT_VALUE_FALSE);
                mfinoConfigData.setTransferId(null);
                mfinoConfigData.setParentTxnId(null);
                mfinoConfigData.getHomePage().show();
            }

//            this.waitingDialog = CustomDialogs.createWaitingForHTTPResponseDialog();
//            this.waitingDialog.show();
            return;

        } else if (ae.getCommand().getId() == 2) {
            if(this.previousForm == null)
                this.previousForm = new CashinConfirmation1(mfinoConfigData);
            this.previousForm.show();
        }
    }

    private class CBFocusListner implements FocusListener {

        CheckBox cb;

        public CBFocusListner(CheckBox cb) {
            this.cb = cb;
        }

        public void focusGained(Component cmpnt) {
            this.cb.getStyle().setBgColor(Constants.COLOUR_SLATEGRAY);
        }

        public void focusLost(Component cmpnt) {
            this.cb.getStyle().setBgColor(Constants.COLOUR_GRAY);
        }
    }
//    private Dialog waitingDialog;

    public void responseReceived(ResponseDataContainer responseData) {
//        if(this.waitingDialog!=null)
//            this.waitingDialog.dispose();
        mfinoConfigData.setPreviousFormHolder(this);
        TransactionResultForm form = new  TransactionResultForm(mfinoConfigData, responseData);

        form.show();
    }
}
