/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.cashininquiry;

import com.mfino.handset.agent.IResponseReceivedListener;
import com.mfino.handset.agent.TransactionResultForm;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.agentservices.cahsinconfirmation.CashinConfirmation1;
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
public class AmountForm3 extends CustomizedForm implements ActionListener, IResponseReceivedListener {

    UserDataContainer userAppData;
    CustomTextField tfAmount;
//    private Dialog waitingDialog;

    public AmountForm3(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.userAppData = mcd;

        this.previousForm = userAppData.getPreviousFormHolder();

        CustomLabel lblAmount = new CustomLabel("Amount");

        tfAmount = new CustomTextField(CustomTextField.NUMERIC);
        tfAmount.setMaxSize(8);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Submit", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

//        this.addComponent(lblChannelCode);
//        this.addComponent(tfChannelCode);

        this.addComponent(lblAmount);
        this.addComponent(tfAmount);
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfAmount.getText())) {
                Dialog d=CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            userAppData.setAmount(tfAmount.getText());
            userAppData.setServiceName(Constants.SERVICE_AGENT);
            userAppData.setTransactionName(Constants.TRANSACTION_CASHIN_INQUIRY);

            HttpExecutorThread thread = new HttpExecutorThread(userAppData, this);
            thread.start();
//            this.waitingDialog = CustomDialogs.createWaitingForHTTPResponseDialog();
//            this.waitingDialog.show();
            return;
        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new DestMDNAccountTypeForm2(userAppData);
            }

            this.previousForm.show();
        }
    }

    public void responseReceived(ResponseDataContainer rd) {

//        if(this.waitingDialog!=null)
//            this.waitingDialog.dispose();
        if (Constants.NOTIFICATIONCODE_INQUIRY_SUCCESS.equals(rd.getMsgCode())) {
            userAppData.setTransactioncharges(rd.getTransactionCharges());
            userAppData.setTransferId(rd.getTransferId());
            userAppData.setDebitAmount(rd.getDebitAmount());
            userAppData.setCreditAmount(rd.getCreditAmount());
            userAppData.setParentTxnId(rd.getParentTxnId());
            CashinConfirmation1 form = new CashinConfirmation1(userAppData);
            form.show();
        } else {
            TransactionResultForm form = new TransactionResultForm(userAppData, rd);
            form.show();
        }
    }
}
