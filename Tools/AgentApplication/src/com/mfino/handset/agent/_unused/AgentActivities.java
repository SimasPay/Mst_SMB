///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mfino.handset.agent._unused;
//
//import com.mfino.handset.agent.services.agentservices.registration.SubscriberRegistrationForm1;
//import com.mfino.handset.agent.datacontainers.SubscriberDataContainer;
//import com.mfino.handset.agent.widgets.CustomizedForm;
//import com.sun.lwuit.Button;
//import com.sun.lwuit.events.ActionEvent;
//import com.sun.lwuit.events.ActionListener;
//import com.sun.lwuit.table.TableLayout;
//
//
///**
// *
// * @author karthik
// */
//public class AgentActivities extends CustomizedForm{
//    
//    private SubscriberDataContainer mfinoConfigData;
//    private Button btnSubscriberRegistration;
//    private Button btnAgentActivation;
//    private Button btnCashIn;
//    private Button btnCashOut;
//    
//    public AgentActivities(SubscriberDataContainer mfinoConfigData){
//        this.mfinoConfigData = mfinoConfigData;
//        
//        btnSubscriberRegistration = new Button("Subscriber Registration");
//        btnAgentActivation = new Button("AgentActivation");
//        btnCashIn = new Button("Cash In");
//        btnCashOut  = new Button("Cash Out");
//        
//        btnSubscriberRegistration.addActionListener(new SubscriberRegistrationActionListener());
//        btnAgentActivation.addActionListener(new AgentActivationActionListener());
//        btnCashIn.addActionListener(new CashInActionListener());
//        btnCashOut.addActionListener(new CashOutActionListener());
//        
//        TableLayout tl = new TableLayout(4,1);
//        this.setLayout(tl);
//        
//        this.addComponent(btnSubscriberRegistration);
//        this.addComponent(btnAgentActivation);
//        this.addComponent(btnCashIn);
//        this.addComponent(btnCashOut);
//        
//    }
//    
//    private class AgentActivationActionListener implements ActionListener{
//
//        public void actionPerformed(ActionEvent ae) {
//            mfinoConfigData.setPreviousFormHolder(AgentActivities.this);
//            SubscriberRegistrationForm1 form = new SubscriberRegistrationForm1(mfinoConfigData);
//            form.show();
//
//        
//        }
//    }
//    private class SubscriberRegistrationActionListener implements ActionListener{
//
//        public void actionPerformed(ActionEvent ae) {
//            mfinoConfigData.setPreviousFormHolder(AgentActivities.this);
//            SubscriberRegistrationForm1 form = new SubscriberRegistrationForm1(mfinoConfigData);
//            form.show();
//        
//        }
//    }
//    private class CashInActionListener implements ActionListener{
//
//        public void actionPerformed(ActionEvent ae) {
//            mfinoConfigData.setPreviousFormHolder(AgentActivities.this);
//            SubscriberRegistrationForm1 form = new SubscriberRegistrationForm1(mfinoConfigData);
//            form.show();
//        }
//    }
//    private class CashOutActionListener implements ActionListener{
//
//        public void actionPerformed(ActionEvent ae) {
//            mfinoConfigData.setPreviousFormHolder(AgentActivities.this);
//            SubscriberRegistrationForm1 form = new SubscriberRegistrationForm1(mfinoConfigData);
//            form.show();        
//        }
//    }
//}
