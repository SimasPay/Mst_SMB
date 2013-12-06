///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mfino.handset.agent._unused;
//
//import com.mfino.handset.agent.TransactionResultForm;
//import com.mfino.handset.agent.constants.Constants;
//import com.mfino.handset.agent.datacontainers.SecureResponseDataContainer;
//import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
//import com.mfino.handset.agent.datacontainers.SubscriberDataContainer;
//import com.mfino.handset.agent.widgets.CustomTextField;
//import com.mfino.handset.agent.widgets.CustomizedForm;
//import com.sun.lwuit.Command;
//import com.sun.lwuit.Label;
//import com.sun.lwuit.TextArea;
//import com.sun.lwuit.events.ActionEvent;
//import com.sun.lwuit.events.ActionListener;
//import com.sun.lwuit.table.TableLayout;
//
///**
// *
// * @author karthik
// */
//public class SubscriberActivation extends CustomizedForm implements ActionListener {
//
//    SubscriberDataContainer mfinoConfigData;
//    CustomTextField tfAgentMDN;
//    CustomTextField tfAgentPin;
//    CustomTextField tfChannelCode;
//    CustomTextField tfMerchantCode;
//    CustomTextField tfAmount;
//    CustomTextField tfSourcePocketCode;
//
//    public SubscriberActivation(SubscriberDataContainer mcd) {
//        this.mfinoConfigData = mcd;
//        Label lblAgentMDN = new Label("MobileNo");
//        Label lblAgentPin = new Label("Pin");
//        Label lblChannelCode = new Label("Channel Code");
//        Label lblMerchantCode = new Label("Partner Code");
//        Label lblSourcePocketCode = new Label("SourcePocketCode");
//        Label lblAmount = new Label("Amount");
//
//        tfMerchantCode = new  CustomTextField();
//        tfMerchantCode.setHint("MDN");
//
//        tfAgentPin = new  CustomTextField(TextArea.PASSWORD);
//        tfAgentPin.setHint("Pin");
//
//        tfChannelCode = new  CustomTextField();
//
//        tfAgentMDN = new  CustomTextField(CustomTextField.PHONENUMBER);
//        tfAmount = new  CustomTextField(CustomTextField.DECIMAL);
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
//        this.addComponent(lblMerchantCode);
//        this.addComponent(tfMerchantCode);
//
//        this.addComponent(lblChannelCode);
//        this.addComponent(tfChannelCode);
//
//        this.addComponent(lblSourcePocketCode);
//        this.addComponent(tfAmount);
//
//        this.addComponent(lblAmount);
//        this.addComponent(tfSourcePocketCode);
//
//    }
//
//    public void actionPerformed(ActionEvent ae) {
//
//        if (ae.getCommand().getId() == 1) {
//
////            mfinoConfigData.setSourceMdn(tfAgentMDN.getText());
//            mfinoConfigData.setSourcePin(tfAgentPin.getText());
//            mfinoConfigData.setChannelId(Constants.CONSTANT_CHANNEL_ID);
//            mfinoConfigData.setAmount(tfAmount.getText());
//            mfinoConfigData.setSourcePocketCode(tfSourcePocketCode.getText());
//            mfinoConfigData.setPartnerCode(tfMerchantCode.getText());
//            mfinoConfigData.setServiceName(Constants.SERVICE_AGENT);
//            mfinoConfigData.setTransactionName(Constants.TRANSACTION_ACTIVATION);
//
////            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mfinoConfigData);
////            ResponseDataContainer rd = ResponseDataContainer
////            rd.setMsg("this is reponse data");
////            TransactionResultForm form = new  TransactionResultForm(mfinoConfigData,rd);
////            mfinoConfigData.setPreviousFormHolder(this);
////            form.show();
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
