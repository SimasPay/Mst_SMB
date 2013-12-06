package com.mfino.handset.agent.services.account.checkbalance;

import com.mfino.handset.agent.services.listeners.IResponseListener;
import com.mfino.handset.agent.services.TransactionResultForm;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.account.AccountServices;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.http.HttpExecutorThread;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.widgets.CustomLabel;
import com.mfino.handset.agent.widgets.CustomTextField;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;

/**
 *
 * @author karthik
 */
public class CheckBalanceForm extends CustomizedForm implements ActionListener, IResponseListener {

    AgentDataContainer SubscriberDataContainer;
    CustomTextField tfSourcePin;
    ComboBox cbAccountType;
//    Dialog waitingDialog;

    public CheckBalanceForm(AgentDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.SubscriberDataContainer = mcd;

        this.previousForm = SubscriberDataContainer.getPreviousForm();

        CustomLabel lblSourcePin = new CustomLabel("Pin");
        CustomLabel lblAccountType = new CustomLabel("AccountType");

        tfSourcePin = new CustomTextField(CustomTextField.PASSWORD);
        tfSourcePin.setHint("pin");
//        tfSourcePin.setEditable(false);

        String[] accTypes = {Constants.CONSTANT_EMONEY, Constants.CONSTANT_BANK};
        cbAccountType = new ComboBox(accTypes);
        cbAccountType.setRenderer(new CheckBoxRenderer());
        cbAccountType.setSelectedIndex(1);
        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);
        this.addCommand(cmd2);
        this.addCommand(cmd1);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblAccountType);
        this.addComponent(cbAccountType);

        this.addComponent(lblSourcePin);
        this.addComponent(tfSourcePin);

        this.setTitle("CheckBalance");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);


    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfSourcePin.getText())) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }

            SubscriberDataContainer.setSourcePin(tfSourcePin.getText());
            if (cbAccountType.getSelectedIndex() == 0) {
                SubscriberDataContainer.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                SubscriberDataContainer.setServiceName(Constants.SERVICE_WALLET);
            } else {
                SubscriberDataContainer.setSourcePocketCode(Constants.POCKET_CODE_BANK);
                SubscriberDataContainer.setServiceName(Constants.SERVICE_BANK);
            }

            SubscriberDataContainer.setTransactionName(Constants.TRANSACTION_CHECKBALANCE);

            SubscriberDataContainer.setPreviousForm(this);
            HttpExecutorThread executor = new HttpExecutorThread(SubscriberDataContainer, this);
            executor.start();

//            this.waitingDialog = CustomDialogs.createWaitingForHTTPResponseDialog();
//            this.waitingDialog.show();
            return;
        } else if (ae.getCommand().getId() == 2) {
            if (this.previousForm == null) {
                this.previousForm = new AccountServices(SubscriberDataContainer);
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

        SubscriberDataContainer.setPreviousForm(this);
        TransactionResultForm form = new TransactionResultForm(SubscriberDataContainer, rd);
        form.show();
    }

    private class CheckBoxRenderer extends CheckBox implements ListCellRenderer {

        public CheckBoxRenderer() {
            super("");
        }

        public Component getListCellRendererComponent(List list, Object o, int index, boolean isSelected) {
            setText("" + o);
            if (isSelected) {
                setFocus(true);
                setSelected(true);
            } else {
                setFocus(false);
                setSelected(false);
            }
            return this;
        }

        public Component getListFocusComponent(List list) {
            setText("");
            setFocus(true);
            setSelected(true);
            return this;
        }
    }
}
