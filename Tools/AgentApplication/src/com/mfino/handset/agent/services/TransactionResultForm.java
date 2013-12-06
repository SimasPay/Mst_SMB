/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.util.StringUtil;
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
public class TransactionResultForm extends CustomizedForm implements ActionListener {

    private AgentDataContainer mfinoConfigdata;

    public TransactionResultForm(AgentDataContainer mcd, ResponseDataContainer rd) {
        this.mfinoConfigdata = mcd;
        this.previousForm = mfinoConfigdata.getPreviousForm();
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);

        this.getTitleStyle().setBgColor(Constants.COLOUR_GRAY);

        if (mcd.getResultFormTitle() == null) {
            this.setTitle("Result");
        } else {
            this.setTitle(mcd.getResultFormTitle());
        }

        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.getStyle().setBorder(Border.createEmpty());
        ta.getStyle().setBgColor(Constants.COLOUR_GRAY);
        ta.setFocusable(false);
        ta.getSelectedStyle().setBgColor(Constants.COLOUR_GRAY);

        this.addComponent(ta);

        Command com2 = new Command("Home", 2);
        this.addCommand(com2);
        this.addCommandListener(this);

        String text = "";
        if (StringUtil.isBlank(rd.getMsgCode()) || StringUtil.isBlank(rd.getMessage())) {
            text = "An error occured.Please try after sometime";
        } else {
            text = rd.getMsgCode() + " : " + rd.getMessage();
        }
        ta.setText(text);
        mfinoConfigdata.setPreviousForm(null);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 2) {

            try {
                if (this.mfinoConfigdata.getHomePage() == null) {
                    this.mfinoConfigdata.setHomePage(new ServicesForAgent(mfinoConfigdata));
                }
            } catch (Exception ex) {
            }
            this.mfinoConfigdata.getHomePage().show();
        }
    }
}
