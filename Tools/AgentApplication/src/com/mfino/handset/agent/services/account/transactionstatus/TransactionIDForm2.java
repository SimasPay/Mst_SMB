/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.transactionstatus;

import com.mfino.handset.agent.services.listeners.IResponseListener;
import com.mfino.handset.agent.services.TransactionResultForm;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.http.HttpExecutorThread;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
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
public class TransactionIDForm2 extends CustomizedForm implements ActionListener, IResponseListener {

    private AgentDataContainer mfinoConfigData;
    private CustomTextField tfTransactionID;
//    private CustomTextField tfChannelCode;
//    private Dialog waitingDialog;

    public TransactionIDForm2(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;
//        CustomLabel lblChannelCode = new CustomLabel("ChannelCode");
        CustomLabel lblTransactionID = new CustomLabel("Transaction ID");
        this.previousForm = mfinoConfigData.getPreviousForm();

//        tfChannelCode = new CustomTextField();
//        tfChannelCode.setHint("channel Code");

        tfTransactionID = new CustomTextField(CustomTextField.NUMERIC);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Submit", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblTransactionID);
        this.addComponent(tfTransactionID);

//        this.addComponent(lblChannelCode);
//        this.addComponent(tfChannelCode);

        this.addShowListener(new ShowListener());
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {
//            mfinoConfigData.setSourceMdn(tfAgentMDN.getText());


            if (StringUtil.isBlank(tfTransactionID.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }

            mfinoConfigData.setTransferId(tfTransactionID.getText());
//            mfinoConfigData.setChannelId(tfChannelCode.getText());
            mfinoConfigData.setServiceName(Constants.SERVICE_ACCOUNT);
            mfinoConfigData.setTransactionName(Constants.TRANSACTION_TRANSACTIONSTATUS);

            HttpExecutorThread thread = new HttpExecutorThread(mfinoConfigData, this);
            thread.start();
//            this.waitingDialog = CustomDialogs.createWaitingForHTTPResponseDialog();
//            this.waitingDialog.show();
            return;

        } else if (ae.getCommand().getId() == 2) {
            this.previousForm.show();
        }
    }

    public void responseReceived(ResponseDataContainer responseData) {

        if (responseData == null) {
            Dialog d = CustomDialogs.createInvalidResponseDailog(previousForm);
            d.show();
            return;
        }

        mfinoConfigData.setPreviousForm(this);
        TransactionResultForm form = new TransactionResultForm(mfinoConfigData, responseData);

        form.show();
    }

    private class ShowListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            if (TransactionIDForm2.this.mfinoConfigData.getTransactionID() != null) {
                TransactionIDForm2.this.tfTransactionID.setText(TransactionIDForm2.this.mfinoConfigData.getTransactionID());
            }
        }
    }
}