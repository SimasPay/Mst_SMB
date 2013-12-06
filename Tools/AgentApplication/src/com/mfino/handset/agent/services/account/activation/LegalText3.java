/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.activation;

import com.mfino.handset.agent.services.listeners.IResponseListener;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.http.HttpExecutorThread;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author karthik
 */
public class LegalText3 extends CustomizedForm implements ActionListener, IResponseListener {

    private AgentDataContainer mfinoConfigData;
//    private Dialog waitingDialog;

    public LegalText3(AgentDataContainer mcd) {
        this.mfinoConfigData = mcd;
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);

        this.previousForm = mfinoConfigData.getPreviousForm();

        Command cmd1 = new Command("Agree", 1);
        Command cmd2 = new Command("Cancel", 2);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.getStyle().setBorder(Border.createEmpty());
        ta.getStyle().setBgColor(Constants.COLOUR_GRAY);
        ta.getUnselectedStyle().setBorder(Border.createEmpty());
        ta.getUnselectedStyle().setBgColor(Constants.COLOUR_GRAY);
        ta.setText("Your consent through your eaZyMoney application is your consent to the terms and"
                + " conditions of this service. Zenith Bank retains the right to amend these terms any time without notice.");

        this.setTitle("Disclosure");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

        this.addComponent(ta);
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            mfinoConfigData.setServiceName(Constants.SERVICE_ACCOUNT);
            mfinoConfigData.setTransactionName(Constants.TRANSACTION_AGENTACTIVATION);

            HttpExecutorThread thread = new HttpExecutorThread(mfinoConfigData, this);
            thread.start();

//            this.waitingDialog = CustomDialogs.createWaitingForHTTPResponseDialog();
//            this.waitingDialog.show();
            return;

        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new AgentActivationForm1(mfinoConfigData);
            }
            this.previousForm.show();
        }
    }

    public void responseReceived(ResponseDataContainer rd) {

        if (rd == null) {
            Dialog d = CustomDialogs.createInvalidResponseDailog(previousForm);
            d.show();
            return;
        }

        mfinoConfigData.setPreviousForm(this);
        ActivationResultForm4 form = new ActivationResultForm4(mfinoConfigData, rd);
        form.show();
    }
}