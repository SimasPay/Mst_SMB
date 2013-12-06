/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.buy;

import com.mfino.handset.agent.services.ServicesForAgent;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.services.buy.purchase.CodeAmountForm1;
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
public class BuyServices extends CustomizedForm implements ActionListener {

    private AgentDataContainer agentDataContainer;
    private CustomRadioButton btnPurchase;
//    private CustomRadioButton btnBanktoeazy;
    private ButtonGroup bg;

    public BuyServices(AgentDataContainer mfinoConfigData) {
        this.agentDataContainer = mfinoConfigData;
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.previousForm = mfinoConfigData.getPreviousForm();

        btnPurchase = new CustomRadioButton("Purchase");
//        btnBanktoeazy = new CustomRadioButton("Bank to eaZyMoney");

        bg = new ButtonGroup();
        bg.add(btnPurchase);
//        bg.add(btnBanktoeazy);

        TableLayout tl = new TableLayout(5, 1);
        this.setLayout(tl);

        this.addComponent(btnPurchase);
//        this.addComponent(btnBanktoeazy);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("OK", 1);

        this.addCommand(cmd2);
        this.addCommand(cmd1);

        this.addCommandListener(this);

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            agentDataContainer.setPreviousForm(this);
            if (bg.getSelectedIndex() == 0) {
                CodeAmountForm1 form = new CodeAmountForm1(agentDataContainer);
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
