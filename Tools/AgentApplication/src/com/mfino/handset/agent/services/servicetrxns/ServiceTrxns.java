/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.servicetrxns;

import com.mfino.handset.agent.services.ServicesForAgent;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.services.account.checkbalance.CheckBalanceForm;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.services.account.changepin.ChangepinForm1;
import com.mfino.handset.agent.services.account.history.AgentHistoryForm;
import com.mfino.handset.agent.services.servicetrxns.cashin.AgentCashinInquiryForm1;
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
public class ServiceTrxns extends CustomizedForm implements ActionListener {

    private AgentDataContainer agentDataContainer;
    private CustomRadioButton btnCashin;
//    private CustomRadioButton btnCheckBalance;
//    private CustomRadioButton btnChangePin;
    private ButtonGroup bg;

    public ServiceTrxns(AgentDataContainer mfinoConfigData) {
        this.agentDataContainer = mfinoConfigData;
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.previousForm = mfinoConfigData.getPreviousForm();

//        btnCheckBalance = new CustomRadioButton("Check balance");
        btnCashin = new CustomRadioButton("Cashin");
//        btnChangePin = new CustomRadioButton("Changepin");

        bg = new ButtonGroup();
//        bg.add(btnCheckBalance);
        bg.add(btnCashin);
//        bg.add(btnChangePin);

        TableLayout tl = new TableLayout(5, 1);
        this.setLayout(tl);

        this.addComponent(btnCashin);
//        this.addComponent(btnCheckBalance);
//        this.addComponent(btnChangePin);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);

        this.addCommand(cmd2);
        this.addCommand(cmd1);

        this.addCommandListener(this);

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            if (bg.getSelectedIndex() == 0) {
                agentDataContainer.setPreviousForm(this);
                AgentCashinInquiryForm1 form = new AgentCashinInquiryForm1(agentDataContainer);
                form.show();
            } 
        } else if (ae.getCommand().getId() == 2) {

            if (this.previousForm == null) {
                this.previousForm = new ServicesForAgent(agentDataContainer);
            }
            this.previousForm.show();
        }
    }
}
