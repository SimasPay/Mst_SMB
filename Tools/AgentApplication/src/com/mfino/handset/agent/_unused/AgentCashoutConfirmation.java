///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mfino.handset.agent._unused;
//
//import com.mfino.handset.agent.constants.Constants;
//import com.mfino.handset.agent.datacontainers.SubscriberDataContainer;
//import com.mfino.handset.agent.widgets.CustomTextField;
//import com.mfino.handset.agent.widgets.CustomizedForm;
//import com.sun.lwuit.CheckBox;
//import com.sun.lwuit.Command;
//import com.sun.lwuit.Label;
//import com.sun.lwuit.events.ActionEvent;
//import com.sun.lwuit.events.ActionListener;
//import com.sun.lwuit.table.TableLayout;
//
///**
// *
// * @author karthik
// */
//public class AgentCashoutConfirmation extends CustomizedForm implements ActionListener {
//
//    SubscriberDataContainer mfinoConfigData;
//    CustomTextField tfAgentMDN;
//    CustomTextField tfPartnerCode;
//    CustomTextField tfAmount;
//    CustomTextField tfChannelCode;
//    private CustomizedForm previousForm;
//    CheckBox cb;
//
//    public AgentCashoutConfirmation(SubscriberDataContainer mcd) {
//        this.mfinoConfigData = mcd;
//
//        previousForm = mfinoConfigData.getPreviousFormHolder();
//
//        Label lblAgentMDN = new Label("Mobile No");
//        Label lblPartnerCode = new Label("Partner Code");
//        Label lblAmount = new Label("Amount");
//        Label lblChannelCode = new Label("Channel Code");
//
//        tfAgentMDN = new  CustomTextField(CustomTextField.PHONENUMBER);
//        tfAgentMDN.setText(mfinoConfigData.getSourceMdn());
//
//        tfPartnerCode = new  CustomTextField();
//        tfPartnerCode.setText(mfinoConfigData.getPartnerCode());
//
//        tfAmount = new  CustomTextField(CustomTextField.DECIMAL);
//        tfAmount.setText(mfinoConfigData.getAmount());
//
//        tfChannelCode = new  CustomTextField();
//        tfChannelCode.setText(mfinoConfigData.getChannelId());
//
//        Command cmd1 = new Command("Submit", 1);
//        Command cmd2 = new Command("Back", 2);
//        this.addCommand(cmd1);
//        this.addCommand(cmd2);
//        this.addCommandListener(this);
//
//        TableLayout layout = new TableLayout(10, 2);
//        this.setLayout(layout);
//
//        this.addComponent(lblAgentMDN);
//        this.addComponent(tfAgentMDN);
//        tfAgentMDN.setEditable(false);
//
//        this.addComponent(lblPartnerCode);
//        this.addComponent(tfPartnerCode);
//        tfPartnerCode.setEditable(false);
//
//        this.addComponent(lblChannelCode);
//        this.addComponent(tfChannelCode);
//        tfChannelCode.setEditable(false);
//
//        this.addComponent(lblAmount);
//        this.addComponent(tfAmount);
//        tfAmount.setEditable(false);
//
//        cb = new CheckBox("Confirm");
//        this.addComponent(cb);
//
//    }
//
//    public void actionPerformed(ActionEvent ae) {
//
//        if (ae.getCommand().getId() == 1) {
//
////            mfinoConfigData.setSourceMdn(tfAgentMDN.getText());
////            mfinoConfigData.setChannelId(Constants.CONSTANT_CHANNEL_ID);
////            mfinoConfigData.setAmount(tfAmount.getText());
////            mfinoConfigData.setPartnerCode(tfPartnerCode.getText());
////            mfinoConfigData.setServiceName(Constants.SERVICE_WALLET);
//            mfinoConfigData.setTransactionName(Constants.TRANSACTION_CASHOUT);
//            if (cb.isSelected()) {
//                mfinoConfigData.setConfirmed(Constants.CONSTANT_VALUE_TRUE);
//            } else {
//                mfinoConfigData.setConfirmed(Constants.CONSTANT_VALUE_FALSE);
//            }
//
////            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mfinoConfigData);
////            ResponseData rd = wrapper.getResponseData();
////            mfinoConfigData.setPreviousFormHolder(this);
////            TransactionResultForm form = new  TransactionResultForm(mfinoConfigData, rd);
////            form.show();
//
//        } else if (ae.getCommand().getId() == 2) {
//            this.previousForm.show();
//        }
//    }
//}
