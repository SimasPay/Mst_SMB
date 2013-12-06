/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.transfer;

import com.mfino.handset.agent.services.transfer.agenttoagent.AgentCodeAmountForm1;
import com.mfino.handset.agent.services.ServicesForAgent;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
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
public class TransferServices1 extends CustomizedForm implements ActionListener {

    private AgentDataContainer agentDataContainer;
    private CustomRadioButton btneazytoBank;
    private CustomRadioButton btnBanktoeazy;
    private CustomRadioButton btnToAgent;
    private ButtonGroup bg;

    public TransferServices1(AgentDataContainer mfinoConfigData) {
        this.agentDataContainer = mfinoConfigData;
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.previousForm = mfinoConfigData.getPreviousForm();

        btneazytoBank = new CustomRadioButton("eaZyMoney to Bank");
        btnBanktoeazy = new CustomRadioButton("Bank to eaZyMoney");
        btnToAgent = new CustomRadioButton("To Agent");

        bg = new ButtonGroup();
        bg.add(btneazytoBank);
        bg.add(btnBanktoeazy);
//        bg.add(btnToAgent);

        TableLayout tl = new TableLayout(5, 1);
        this.setLayout(tl);

        this.addComponent(btneazytoBank);
        this.addComponent(btnBanktoeazy);
//        this.addComponent(btnToAgent);

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
                agentDataContainer.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                agentDataContainer.setDestinationPocketCode(Constants.POCKET_CODE_BANK);
                agentDataContainer.setServiceName(Constants.SERVICE_WALLET);
                agentDataContainer.setTransactionName(Constants.TRANSACTION_TRANSFER_INQUIRY);
                agentDataContainer.setDestinationMdn(agentDataContainer.getSourceMdn());
                AmountForm1 form = new AmountForm1(agentDataContainer);
                form.show();
            } else if (bg.getSelectedIndex() == 1) {
                agentDataContainer.setSourcePocketCode(Constants.POCKET_CODE_BANK);
                agentDataContainer.setDestinationPocketCode(Constants.POCKET_CODE_EMONEY);
                agentDataContainer.setServiceName(Constants.SERVICE_BANK);
                agentDataContainer.setTransactionName(Constants.TRANSACTION_TRANSFER_INQUIRY);
                agentDataContainer.setDestinationMdn(agentDataContainer.getSourceMdn());
                AmountForm1 form = new AmountForm1(agentDataContainer);
                form.show();
            } else if (bg.getSelectedIndex() == 2) {
                agentDataContainer.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                agentDataContainer.setDestinationPocketCode(Constants.POCKET_CODE_EMONEY);
                agentDataContainer.setServiceName(Constants.SERVICE_WALLET);
                agentDataContainer.setTransactionName(Constants.TRANSACTION_AGENT_AGENT_TRANSFER_INQUIRY);
                AgentCodeAmountForm1 form = new AgentCodeAmountForm1(agentDataContainer);
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
