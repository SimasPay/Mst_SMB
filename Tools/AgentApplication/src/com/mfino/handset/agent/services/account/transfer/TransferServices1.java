/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.transfer;

import com.mfino.handset.agent.ServicesForAgent;
import com.mfino.handset.agent.constants.Constants;
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
public class TransferServices1 extends CustomizedForm implements ActionListener {

    private UserDataContainer agentDataContainer;
    private CustomRadioButton btneazytoBank;
    private CustomRadioButton btnBanktoeazy;
    private ButtonGroup bg;

    public TransferServices1(UserDataContainer mfinoConfigData) {
        this.agentDataContainer = mfinoConfigData;
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.previousForm = mfinoConfigData.getPreviousFormHolder();

        btneazytoBank = new CustomRadioButton("eaZyMoney to Bank");
        btnBanktoeazy = new CustomRadioButton("Bank to eaZyMoney");

        bg = new ButtonGroup();
        bg.add(btneazytoBank);
        bg.add(btnBanktoeazy);

        TableLayout tl = new TableLayout(5, 1);
        this.setLayout(tl);

        this.addComponent(btneazytoBank);
        this.addComponent(btnBanktoeazy);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Next", 1);

        this.addCommand(cmd1);
        this.addCommand(cmd2);

        this.addCommandListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            if (bg.getSelectedIndex() == 0) {
                agentDataContainer.setSourcePocketCode(Constants.POCKET_CODE_EMONEY);
                agentDataContainer.setDestinationPocketCode(Constants.POCKET_CODE_BANK);
                agentDataContainer.setServiceName(Constants.SERVICE_WALLET);
            } else if (bg.getSelectedIndex() == 1) {
                agentDataContainer.setSourcePocketCode(Constants.POCKET_CODE_BANK);
                agentDataContainer.setDestinationPocketCode(Constants.POCKET_CODE_EMONEY);
                agentDataContainer.setServiceName(Constants.SERVICE_BANK);
            }
            agentDataContainer.setPreviousFormHolder(this);
            agentDataContainer.setDestinationMdn(agentDataContainer.getSourceMdn());
            
            AmountPinForm2 form = new AmountPinForm2(agentDataContainer);
            form.show();
            
        } else if (ae.getCommand().getId() == 2) {

            if (this.previousForm == null) {
                this.previousForm = new ServicesForAgent(agentDataContainer);
            }
            this.previousForm.show();
        }
    }
}
