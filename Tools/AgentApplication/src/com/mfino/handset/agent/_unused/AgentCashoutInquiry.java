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
//public class AgentCashoutInquiry extends CustomizedForm implements ActionListener {
//
//    SubscriberDataContainer mfinoConfigData;
//    CustomTextField tfAgentMDN;
//    CustomTextField tfAgentPin;
//    CustomTextField tfChannelCode;
//    CustomTextField tfDestMDN;
//    CustomTextField tfTransferID;
//    CustomTextField tfSourcePocketCode;
//
//    public AgentCashoutInquiry(SubscriberDataContainer mcd) {
//        this.mfinoConfigData = mcd;
//        Label lblAgentMDN = new Label("Mobile No");
//        Label lblAgentPin = new Label("Pin");
//        Label lblChannelCode = new Label("Channel Code");
//        Label lblPartnerCode = new Label("Partner Code");
//        Label lblSourcePocketCode = new Label("SourcePocketCode");
//        Label lblAmount = new Label("Amount");
//
//        tfDestMDN = new  CustomTextField(CustomTextField.PHONENUMBER);
//        tfDestMDN.setHint("Mobile No");
//
//        tfAgentPin = new  CustomTextField(CustomTextField.NUMERIC);
//        tfAgentPin.setHint("Pin");
//
//        tfChannelCode = new  CustomTextField();
//
//        tfAgentMDN = new  CustomTextField(CustomTextField.PHONENUMBER);
//        tfTransferID = new  CustomTextField(CustomTextField.NUMERIC);
//        tfSourcePocketCode = new  CustomTextField();
//        tfChannelCode = new  CustomTextField();
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
//
//        this.addComponent(lblAgentPin);
//        this.addComponent(tfAgentPin);
//
//        this.addComponent(lblAmount);
//        this.addComponent(tfSourcePocketCode);
//        
//        this.addComponent(lblPartnerCode);
//        this.addComponent(tfDestMDN);
//
//        this.addComponent(lblChannelCode);
//        this.addComponent(tfChannelCode);
//
//        this.addComponent(lblSourcePocketCode);
//        this.addComponent(tfTransferID);
//
//
//    }
//
//    public void actionPerformed(ActionEvent ae) {
//
//        if (ae.getCommand().getId() == 1) {
//
////            mfinoConfigData.setSourceMdn(tfAgentMDN.getText());
//            mfinoConfigData.setSourcePin(tfAgentPin.getText());
//            mfinoConfigData.setChannelId(tfChannelCode.getText());
//            mfinoConfigData.setAmount(tfTransferID.getText());
//            mfinoConfigData.setSourcePocketCode(tfSourcePocketCode.getText());
//            mfinoConfigData.setPartnerCode(tfDestMDN.getText());
//            mfinoConfigData.setServiceName(Constants.SERVICE_WALLET);
//            mfinoConfigData.setTransactionName(Constants.TRANSACTION_CASHOUT_INQUIRY);
//
////            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mfinoConfigData);
////            ResponseData rd = wrapper.getResponseData();
////            mfinoConfigData.setPreviousFormHolder(this);
////            mfinoConfigData.setTransferId(rd.getTransferId());
//            
//            AgentCashoutConfirmation form = new AgentCashoutConfirmation(mfinoConfigData);
//            form.show();
//
//        }
//        else if(ae.getCommand().getId()==2){
//            
//            mfinoConfigData.getPreviousFormHolder().show();
//            
//        }
//       
//
//    }
//}
