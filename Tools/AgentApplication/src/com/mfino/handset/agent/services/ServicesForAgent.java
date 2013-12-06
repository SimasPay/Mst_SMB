/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.account.AccountServices;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.eaZyMoneyAgent;
import com.mfino.handset.agent.services.transfer.TransferServices1;
import com.mfino.handset.agent.services.agentservices.registration.SubscriberMDNForm1;
import com.mfino.handset.agent.services.buy.BuyServices;
import com.mfino.handset.agent.services.servicetrxns.ServiceTrxns;
import com.mfino.handset.agent.widgets.CustomRadioButton;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 *
 * @author karthik
 */
public class ServicesForAgent extends CustomizedForm implements ActionListener {

    private AgentDataContainer agentDataContainer;
    private CustomRadioButton btnAccountServices;
    private CustomRadioButton btnTransfers;
    private CustomRadioButton btnSubRegistration;
    private CustomRadioButton btnServiceTxn;
    private CustomRadioButton btnBuy;
    ButtonGroup bg;

    public ServicesForAgent(AgentDataContainer mfinoConfigData) {
        this.agentDataContainer = mfinoConfigData;
        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        try {
            this.agentDataContainer.setHomePage(this);
        } catch (Exception ex) {
        }

        btnSubRegistration = new CustomRadioButton("Cust Registration");
        btnServiceTxn = new CustomRadioButton("Service Txn");
        btnBuy = new CustomRadioButton("Buy");
        btnTransfers = new CustomRadioButton("Transfer");
        btnAccountServices = new CustomRadioButton("Account");

        bg = new ButtonGroup();
        bg.add(btnSubRegistration);
        bg.add(btnServiceTxn);
        bg.add(btnBuy);
        bg.add(btnTransfers);
        bg.add(btnAccountServices);

        this.addComponent(btnSubRegistration);
        this.addComponent(btnServiceTxn);
        this.addComponent(btnBuy);
        this.addComponent(btnTransfers);
        this.addComponent(btnAccountServices);

        Command cmd1 = new Command("OK", 1);
        Command cmd2 = new Command("Logout", 2);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        this.setTitle("Services");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            CustomizedForm form = null;

            switch (bg.getSelectedIndex()) {
                case 0:
                    form = new SubscriberMDNForm1(agentDataContainer);
                    break;
                case 1:
                    form = new ServiceTrxns(agentDataContainer);
                    break;
                case 2:
                    form = new BuyServices(agentDataContainer);
                    break;
                case 3:
                    form = new TransferServices1(agentDataContainer);
                    break;
                case 4:
                    form = new AccountServices(agentDataContainer);
                    break;
            }
            form.show();
            agentDataContainer.setPreviousForm(this);
        } else if (ae.getCommand().getId() == 2) {
            eaZyMoneyAgent midlet = agentDataContainer.getMobileBankingMidlet();
            agentDataContainer = null;
            midlet.startApp();
        }

    }

    private class AccountFocusListener implements FocusListener {

        public void focusGained(Component cmpnt) {
            btnAccountServices.setSelected(true);
            btnBuy.setSelected(false);
//            btnPaymentServices.setSelected(false);
//            btnWalletServices.setSelected(false);
        }

        public void focusLost(Component cmpnt) {
            btnAccountServices.setSelected(false);
        }
    }

    private class AgentFocusListener implements FocusListener {

        public void focusGained(Component cmpnt) {
            btnAccountServices.setSelected(false);
            btnBuy.setSelected(true);
//            btnPaymentServices.setSelected(false);
//            btnWalletServices.setSelected(false);
        }

        public void focusLost(Component cmpnt) {
            btnBuy.setSelected(false);
        }
    }

    private class PaymentFocusListener implements FocusListener {

        public void focusGained(Component cmpnt) {
            btnAccountServices.setSelected(false);
            btnBuy.setSelected(false);
//            btnPaymentServices.setSelected(true);
//            btnWalletServices.setSelected(false);
        }

        public void focusLost(Component cmpnt) {
//            btnPaymentServices.setSelected(false);
        }
    }

    private class WalletFocusListener implements FocusListener {

        public void focusGained(Component cmpnt) {
            btnAccountServices.setSelected(false);
            btnBuy.setSelected(false);
//            btnPaymentServices.setSelected(false);
//            btnWalletServices.setSelected(true);
        }

        public void focusLost(Component cmpnt) {
//            btnWalletServices.setSelected(false);
        }
    }
}
