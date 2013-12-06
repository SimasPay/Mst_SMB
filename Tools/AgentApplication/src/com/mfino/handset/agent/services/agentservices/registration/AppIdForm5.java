/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.registration;

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
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 *
 * @author karthik
 */
public class AppIdForm5 extends CustomizedForm implements ActionListener, IResponseReceivedListener {

    UserDataContainer mfinoConfigData;
    CustomTextField tfAppID;
//    CustomTextField tfChannelCode;

    public AppIdForm5(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;
        CustomLabel lblappID = new CustomLabel("App ID");
//        CustomLabel lblChannelCode = new CustomLabel("Channel Code");
        Label l = new Label();
        this.previousForm = mfinoConfigData.getPreviousFormHolder();
        tfAppID = new CustomTextField();
        tfAppID.setHint("Application ID");
        tfAppID.setMaxSize(Constants.FIELD_LENGTH_APPID);
//        tfAppID.setin
//        tfChannelCode = new CustomTextField();

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Submit", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblappID);
        this.addComponent(tfAppID);

//        this.addComponent(lblChannelCode);
//        this.addComponent(tfChannelCode);
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfAppID.getText())) {
                Dialog d=CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }

//            mfinoConfigData.setChannelId(tfChannelCode.getText());
            mfinoConfigData.setApplicationID(tfAppID.getText());
            mfinoConfigData.setServiceName(Constants.SERVICE_AGENT);
            mfinoConfigData.setTransactionName(Constants.TRANSACTION_SUBSCRIBERREGISTRATION);

            HttpExecutorThread thread = new HttpExecutorThread(mfinoConfigData, this);
            thread.start();
//            this.waitingDialog = CustomDialogs.createWaitingForHTTPResponseDialog();
//            this.waitingDialog.show();
            return;

        } else if (ae.getCommand().getId() == 2) {
            if(this.previousForm == null)
                this.previousForm = new DOBForm4(mfinoConfigData);
            this.previousForm.show();
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
