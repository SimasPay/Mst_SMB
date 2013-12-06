///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mfino.handset.agent._unused;
//
//import com.mfino.handset.agent.services.agentservices.cashininquiry.AgentCashinInInquiryForm1;
//import com.mfino.handset.agent.util.CustomDialogs;
//import com.mfino.handset.agent.datacontainers.SubscriberDataContainer;
//import com.mfino.handset.agent.widgets.CustomizedForm;
//import com.sun.lwuit.ButtonGroup;
//import com.sun.lwuit.Command;
//import com.sun.lwuit.Component;
//import com.sun.lwuit.Form;
//import com.sun.lwuit.RadioButton;
//import com.sun.lwuit.events.ActionEvent;
//import com.sun.lwuit.events.ActionListener;
//import com.sun.lwuit.events.FocusListener;
//import com.sun.lwuit.table.TableLayout;
//
///**
// *
// * @author karthik
// */
//public class WalletServices extends CustomizedForm implements ActionListener {
//
//    private SubscriberDataContainer mfinoConfigData;
//    private RadioButton btnAgentCashIn;
//    private RadioButton btnAgentCashOut;
//    private RadioButton btnCheckBalance;
//    
//    private RadioButton btnResetPin;
//    private RadioButton btnTransferToBank;
//    private RadioButton btnTransferToEmoney;
//    private RadioButton btnChangePin;
//    private CustomizedForm previousForm;
//    private ButtonGroup bg;
//
//    public WalletServices(SubscriberDataContainer mfinoConfigData) {
//        this.mfinoConfigData = mfinoConfigData;
//
//        this.previousForm = mfinoConfigData.getPreviousFormHolder();
//
//        btnAgentCashIn = new RadioButton("Cash in");
//        btnAgentCashIn.addFocusListener(new CashInListener());
//        
//        btnAgentCashOut = new RadioButton("Cash out");
//        btnAgentCashOut.addFocusListener(new CashOutListener());
//        
//        btnCheckBalance = new RadioButton("Check Balance");
//        btnCheckBalance.addFocusListener(new CheckBalanceListener());
//        
//        btnResetPin = new RadioButton("Reset Pin");
//        btnResetPin.addFocusListener(new ResetPinListener());
//        
//        btnChangePin = new RadioButton("Change Pin");
//        btnChangePin.addFocusListener(new ChangePinListener());
//        
//        btnTransferToBank = new RadioButton("Transfer To bank");
//        btnTransferToBank.addFocusListener(new TransfetToBankListener());
//        
//        btnTransferToEmoney = new RadioButton("Transfer To emoney");
//        btnTransferToEmoney.addFocusListener(new TransferToEmoneyListener());
//
//        bg = new ButtonGroup();
//        bg.add(btnAgentCashIn);
//        bg.add(btnAgentCashOut);
//        bg.add(btnCheckBalance);
//        bg.add(btnChangePin);
//        bg.add(btnResetPin);
//        bg.add(btnTransferToBank);
//        bg.add(btnTransferToEmoney);
//        
//        TableLayout tl = new TableLayout(4, 1);
//        this.setLayout(tl);
//
//        this.addComponent(btnAgentCashIn);
//        this.addComponent(btnAgentCashOut);
//        this.addComponent(btnResetPin);
//        this.addComponent(btnChangePin);
//        this.addComponent(btnCheckBalance);
//        this.addComponent(btnTransferToBank);
//        this.addComponent(btnTransferToEmoney);
//        
//        Command cmd1 = new Command("Next", 1);
//        Command cmd2 = new Command("Back", 2);
//
//        this.addCommand(cmd1);
//        this.addCommand(cmd2);
//
//        this.addCommandListener(this);
//
//    }
//
//
//    public void actionPerformed(ActionEvent ae) {
//        if (ae.getCommand().getId() == 1) {
//
//            if (bg.getSelectedIndex() == 0) {
//                mfinoConfigData.setPreviousFormHolder(this);
//                AgentCashinInInquiryForm1 form = new  AgentCashinInInquiryForm1(mfinoConfigData);
//                form.show();
//            } else if (bg.getSelectedIndex() == 1) {
//                mfinoConfigData.setPreviousFormHolder(this);
//                AgentCashoutInquiry form = new  AgentCashoutInquiry(mfinoConfigData);
//                form.show();
//            } else {
//               CustomDialogs.showTransactionNotAvailableDialog(); 
//            }
//
//        } else if (ae.getCommand().getId() == 2) {
//
//            this.previousForm.show();
//        }
//
//    }
//
//    private class CashInListener implements FocusListener {
//
//        public void focusGained(Component cmpnt) {
//            btnAgentCashIn.setSelected(true);
//        }
//
//        public void focusLost(Component cmpnt) {
//            btnAgentCashIn.setSelected(false);
//        }
//    }
//
//    private class CashOutListener implements FocusListener {
//
//        public void focusGained(Component cmpnt) {
//            btnAgentCashOut.setSelected(true);
//        }
//
//        public void focusLost(Component cmpnt) {
//            btnAgentCashOut.setSelected(false);
//        }
//    }
//    private class CheckBalanceListener implements FocusListener {
//
//        public void focusGained(Component cmpnt) {
//            btnCheckBalance.setSelected(true);
//        }
//
//        public void focusLost(Component cmpnt) {
//            btnCheckBalance.setSelected(false);
//        }
//    }
//
//    
//    private class ChangePinListener implements FocusListener {
//
//        public void focusGained(Component cmpnt) {
//            btnChangePin.setSelected(true);
//        }
//
//        public void focusLost(Component cmpnt) {
//            btnChangePin.setSelected(false);
//        }
//    }
//
//    private class ResetPinListener implements FocusListener {
//
//        public void focusGained(Component cmpnt) {
//            btnResetPin.setSelected(true);
//        }
//
//        public void focusLost(Component cmpnt) {
//            btnResetPin.setSelected(false);
//        }
//    }
//    private class TransfetToBankListener implements FocusListener {
//
//        public void focusGained(Component cmpnt) {
//            btnTransferToBank.setSelected(true);
//        }
//
//        public void focusLost(Component cmpnt) {
//            btnTransferToBank.setSelected(false);
//        }
//    }
//
//    private class TransferToEmoneyListener implements FocusListener {
//
//        public void focusGained(Component cmpnt) {
//            btnTransferToEmoney.setSelected(true);
//        }
//
//        public void focusLost(Component cmpnt) {
//            btnTransferToEmoney.setSelected(false);
//        }
//    }
//}
