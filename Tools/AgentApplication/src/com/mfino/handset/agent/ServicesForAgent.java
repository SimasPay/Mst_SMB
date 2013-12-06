/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.account.AccountServices;
import com.mfino.handset.agent.services.agentservices.AgentSpecificServices;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
import com.mfino.handset.agent.services.account.transfer.TransferServices1;
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

    private UserDataContainer agentDataContainer;
    private CustomRadioButton btnAccountServices;
    private CustomRadioButton btnTransfers;
    private CustomRadioButton btnAgentServices;
    ButtonGroup bg;

    public ServicesForAgent(UserDataContainer mfinoConfigData) {
        this.agentDataContainer = mfinoConfigData;
        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        try {
            this.agentDataContainer.setHomePage(this);
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        btnAccountServices = new CustomRadioButton("Account Services");

        btnTransfers = new CustomRadioButton("Transfers");
//        
        btnAgentServices = new CustomRadioButton("Agent Services");

        bg = new ButtonGroup();
        bg.add(btnAgentServices);
        bg.add(btnAccountServices);
        bg.add(btnTransfers);

        this.addComponent(btnAgentServices);
        this.addComponent(btnAccountServices);
        this.addComponent(btnTransfers);

        Command cmd1 = new Command("Logout", 2);
        Command cmd2 = new Command("Next", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            if (bg.getSelectedIndex() == 0) {
                agentDataContainer.setPreviousFormHolder(this);
                AgentSpecificServices form = new AgentSpecificServices(agentDataContainer);
                form.show();
            } else if (bg.getSelectedIndex() == 1) {
                agentDataContainer.setPreviousFormHolder(this);
                AccountServices form = new AccountServices(agentDataContainer);
                form.show();
            } else if (bg.getSelectedIndex() == 2) {
                agentDataContainer.setPreviousFormHolder(this);
                TransferServices1 form = new TransferServices1(agentDataContainer);
                form.show();
            }
        } else if (ae.getCommand().getId() == 2) {
            eaZyMoneyAgent midlet = agentDataContainer.getMobileBankingMidlet();
            agentDataContainer = null;
            midlet.startApp();
        }

    }

    private class AccountFocusListener implements FocusListener {

        public void focusGained(Component cmpnt) {
            btnAccountServices.setSelected(true);
            btnAgentServices.setSelected(false);
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
            btnAgentServices.setSelected(true);
//            btnPaymentServices.setSelected(false);
//            btnWalletServices.setSelected(false);
        }

        public void focusLost(Component cmpnt) {
            btnAgentServices.setSelected(false);
        }
    }

    private class PaymentFocusListener implements FocusListener {

        public void focusGained(Component cmpnt) {
            btnAccountServices.setSelected(false);
            btnAgentServices.setSelected(false);
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
            btnAgentServices.setSelected(false);
//            btnPaymentServices.setSelected(false);
//            btnWalletServices.setSelected(true);
        }

        public void focusLost(Component cmpnt) {
//            btnWalletServices.setSelected(false);
        }
    }
}
