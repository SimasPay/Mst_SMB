/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.agentservices.cashininquiry;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
import com.mfino.handset.agent.widgets.CustomLabel;
import com.mfino.handset.agent.widgets.CustomTextField;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;

/**
 *
 * @author karthik
 */
public class DestMDNAccountTypeForm2 extends CustomizedForm implements ActionListener {

    UserDataContainer mfinoConfigData;
    CustomTextField tfDestMDN;
    ComboBox cbAccountType;

    public DestMDNAccountTypeForm2(UserDataContainer mcd) {
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.mfinoConfigData = mcd;

        this.previousForm = mfinoConfigData.getPreviousFormHolder();

        CustomLabel lblDestMDN = new CustomLabel("Dest Mobile No");
        CustomLabel lblDestAccountType = new CustomLabel("Dest AccountType");

        tfDestMDN = new  CustomTextField(CustomTextField.PHONENUMBER);
        tfDestMDN.setHint("Dest Mobile No");

        String[] accTypes = {Constants.CONSTANT_EMONEY};
        cbAccountType = new ComboBox(accTypes);
        cbAccountType.setRenderer(new CheckBoxRenderer());
        cbAccountType.setSelectedIndex(0);

        Command cmd1 = new Command("Back", 2);
        Command cmd2 = new Command("Next", 1);
        this.addCommand(cmd1);
        this.addCommand(cmd2);
        this.addCommandListener(this);

        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.addComponent(lblDestMDN);
        this.addComponent(tfDestMDN);

        this.addComponent(lblDestAccountType);
        this.addComponent(cbAccountType);

    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand().getId() == 1) {

            if (StringUtil.isBlank(tfDestMDN.getText())) {
                Dialog d=CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }

            if(cbAccountType.getSelectedIndex()!=0){
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this);
                d.show();
                return;
            }
            
            mfinoConfigData.setDestinationMdn(tfDestMDN.getText());
            if (cbAccountType.getSelectedIndex() == 0) {
                mfinoConfigData.setDestinationPocketCode(Constants.POCKET_CODE_EMONEY);
            }

            mfinoConfigData.setPreviousFormHolder(this);
            AmountForm3 form = new  AmountForm3(mfinoConfigData);
            form.show();

        } else if (ae.getCommand().getId() == 2) {
            if(this.previousForm==null)
                this.previousForm = new AgentCashinInInquiryForm1(mfinoConfigData);
            this.previousForm.show();
        }
    }

    private class CheckBoxRenderer extends CheckBox implements ListCellRenderer {

        public CheckBoxRenderer() {
            super("");
        }

        public Component getListCellRendererComponent(List list, Object o, int index, boolean isSelected) {
            setText("" + o);
            if (isSelected) {
                setFocus(true);
                setSelected(true);
            } else {
                setFocus(false);
                setSelected(false);
            }
            return this;
        }

        public Component getListFocusComponent(List list) {
            setText("");
            setFocus(true);
            setSelected(true);
            return this;
        }
    }
}
