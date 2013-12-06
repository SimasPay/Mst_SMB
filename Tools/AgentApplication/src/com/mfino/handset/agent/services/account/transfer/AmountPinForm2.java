/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.transfer;

import com.mfino.handset.agent.services.agentservices.cashininquiry.*;
import com.mfino.handset.agent.IResponseReceivedListener;
import com.mfino.handset.agent.TransactionResultForm;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.http.HttpExecutorThread;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
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
public class AmountPinForm2 extends CustomizedForm implements ActionListener, IResponseReceivedListener {

    UserDataContainer userAppData;
    CustomTextField tfAmount;
    CustomTextField tfSourcePin;
//    private Dialog waitingDialog;

    public AmountPinForm2(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.userAppData = mcd;

        this.previousForm = userAppData.getPreviousFormHolder();

        CustomLabel lblAmount = new CustomLabel("Amount");
        CustomLabel lblSourcePin = new CustomLabel("Pin");

        tfAmount = new CustomTextField(CustomTextField.NUMERIC);
        tfAmount.setMaxSize(Constants.FIELD_LENGTH_AMOUNT);
        tfSourcePin = new CustomTextField(CustomTextField.PASSWORD);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Submit", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblAmount);
        this.addComponent(tfAmount);

        this.addComponent(lblSourcePin);
        this.addComponent(tfSourcePin);
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfAmount.getText()) || StringUtil.isBlank(tfSourcePin.getText())) {
                Dialog d=CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            userAppData.setPreviousFormHolder(this);

            userAppData.setSourcePin(tfSourcePin.getText());
            userAppData.setAmount(tfAmount.getText());
            userAppData.setTransactionName(Constants.TRANSACTION_TRANSFER_INQUIRY);

            HttpExecutorThread thread = new HttpExecutorThread(userAppData, this);
            thread.start();
            return;

        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new DestMDNAccountTypeForm2(userAppData);
            }
            this.previousForm.show();
        }
    }

    public void responseReceived(ResponseDataContainer rd) {

        if (!rd.getMsgCode().equals(Constants.NOTIFICATIONCODE_INQUIRY_SUCCESS)) {
            TransactionResultForm form = new TransactionResultForm(userAppData, rd);
            form.show();
            return;
        }
        userAppData.setTransactioncharges(rd.getTransactionCharges());
        userAppData.setTransferId(rd.getTransferId());
        userAppData.setDebitAmount(rd.getDebitAmount());
        userAppData.setCreditAmount(rd.getCreditAmount());
        userAppData.setParentTxnId(rd.getParentTxnId());
        TransferConfirmationForm3 form = new TransferConfirmationForm3(userAppData);
        form.show();
    }
}
