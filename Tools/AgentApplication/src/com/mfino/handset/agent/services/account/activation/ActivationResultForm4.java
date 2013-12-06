/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.account.activation;

import com.mfino.handset.agent.*;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Command;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author karthik
 */
public class ActivationResultForm4 extends CustomizedForm implements ActionListener {

    private AgentDataContainer mfinoConfigdata;

    public ActivationResultForm4(AgentDataContainer mcd, ResponseDataContainer rd) {
        this.mfinoConfigdata = mcd;
        this.previousForm = mfinoConfigdata.getPreviousForm();
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);

        this.setTitle("Confirmation");
        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.getStyle().setBorder(Border.createEmpty());
        ta.getStyle().setBgColor(Constants.COLOUR_GRAY);
        ta.getUnselectedStyle().setBorder(Border.createEmpty());
        ta.getUnselectedStyle().setBgColor(Constants.COLOUR_GRAY);

        this.addComponent(ta);

        Command com1 = new Command("Close", 2);
        Command com2 = new Command("Login", 1);
        this.addCommand(com1);
        this.addCommand(com2);
        this.addCommandListener(this);

        try {
            String text = rd.getMessage();
            ta.setText(text);
        } catch (Exception ex) {
        }

        mfinoConfigdata.setPreviousForm(null);
    }

    public void actionPerformed(ActionEvent ae) {
        eaZyMoneyAgent midley = this.mfinoConfigdata.getMobileBankingMidlet();
        this.mfinoConfigdata = null;
        if (ae.getCommand().getId() == 1) {
            midley.startApp();
        } else if (ae.getCommand().getId() == 2) {
            midley.destroyApp(true);
            midley.notifyDestroyed();
        }
    }
}
