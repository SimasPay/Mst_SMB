/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.servicetrxns.cashin;

import com.mfino.handset.agent.services.listeners.IResponseListener;
import com.mfino.handset.agent.services.TransactionResultForm;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.http.HttpExecutorThread;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.widgets.CustomLabel;
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
public class CashinConfirmation3 extends CustomizedForm implements ActionListener, IResponseListener {

    AgentDataContainer mfinoConfigData;

    public CashinConfirmation3(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;

        this.previousForm = mfinoConfigData.getPreviousForm();

        Command cmd1 = new Command("Cancel", 2);
        Command cmd2 = new Command("Confirm", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        String str1 = "MDN:" + mcd.getDestinationMdn();
        CustomLabel lblTxt1 = new CustomLabel(str1);
        String str2 = "Amount:" + mcd.getAmount();
        CustomLabel lblTxt2 = new CustomLabel(str2);
        String str3 = "Source:eaZyMoney";
        CustomLabel lblTxt3 = new CustomLabel(str3);

        this.addComponent(lblTxt1);
        this.addComponent(lblTxt2);
        this.addComponent(lblTxt3);

        this.setTitle("Confirmation");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            mfinoConfigData.setTransactionName(Constants.TRANSACTION_CASHIN);
            mfinoConfigData.setConfirmed(Constants.CONSTANT_VALUE_TRUE);

            HttpExecutorThread thread = new HttpExecutorThread(mfinoConfigData, this);
            thread.start();
            return;
        } else if (ae.getCommand().getId() == 2) {
            mfinoConfigData.getHomePage().show();
        }
    }

    public void responseReceived(ResponseDataContainer responseData) {
        if (responseData == null) {
            Dialog d = CustomDialogs.createInvalidResponseDailog(previousForm);
            d.show();
            return;
        }
        mfinoConfigData.setResultFormTitle("Confirmation");
        TransactionResultForm form = new TransactionResultForm(mfinoConfigData, responseData);
        form.show();
    }
}
