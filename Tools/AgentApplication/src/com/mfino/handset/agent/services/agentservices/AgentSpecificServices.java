/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices;

import com.mfino.handset.agent.services.agentservices.cashininquiry.AgentCashinInInquiryForm1;
import com.mfino.handset.agent.services.agentservices.registration.SubscriberRegistrationForm1;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
import com.mfino.handset.agent.widgets.CustomRadioButton;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Command;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.table.TableLayout;

/**
 *
 * @author karthik
 */
public class AgentSpecificServices extends CustomizedForm implements ActionListener {

    private UserDataContainer mfinoConfigData;
    private CustomRadioButton btnSubscriberRegistration;
    private CustomRadioButton btnCashIn;
//    private RadioButton btnCashOut;
//    private RadioButton btnBillpay;
//    private RadioButton btnMerchantpay;
    private ButtonGroup bg;

    public AgentSpecificServices(UserDataContainer mfinoConfigData) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mfinoConfigData;

        this.previousForm = mfinoConfigData.getPreviousFormHolder();

        btnSubscriberRegistration = new CustomRadioButton("Subscriber Registration");
//        btnSubscriberRegistration.addFocusListener(new RadioButtonFocusListener(btnSubscriberRegistration));

        btnCashIn = new CustomRadioButton("Cash in");
//        btnCashIn.addFocusListener(new RadioButtonFocusListener(btnCashIn));

//        btnCashOut = new RadioButton("Cash out");
//        btnCashOut.addFocusListener(new CashOutListener());
//        
//        btnBillpay = new RadioButton("Billpay");
//        btnBillpay.addFocusListener(new BillpayListener());
//        
//        btnMerchantpay = new RadioButton("Merchantpay");
//        btnMerchantpay.addFocusListener(new MerchantpayListener());

        bg = new ButtonGroup();
        bg.add(btnSubscriberRegistration);
        bg.add(btnCashIn);
//        bg.add(btnCashOut);
//        bg.add(btnBillpay);
//        bg.add(btnMerchantpay);

        TableLayout tl = new TableLayout(7, 1);
        this.setLayout(tl);

        this.addComponent(btnSubscriberRegistration);
        this.addComponent(btnCashIn);
//        this.addComponent(btnCashOut);
//        this.addComponent(btnBillpay);
//        this.addComponent(btnMerchantpay);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Next", 1);

        this.addCommand(cmd1);
        this.addCommand(cmd2);

        this.addCommandListener(this);

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            if (bg.getSelectedIndex() == 0) {
                mfinoConfigData.setPreviousFormHolder(this);
                SubscriberRegistrationForm1 form = new SubscriberRegistrationForm1(mfinoConfigData);
                form.show();
            } else if (bg.getSelectedIndex() == 1) {
                mfinoConfigData.setPreviousFormHolder(this);
                AgentCashinInInquiryForm1 form = new AgentCashinInInquiryForm1(mfinoConfigData);
                form.show();
            } 

        } else if (ae.getCommand().getId() == 2) {
            this.previousForm.show();
        }
    }
}
