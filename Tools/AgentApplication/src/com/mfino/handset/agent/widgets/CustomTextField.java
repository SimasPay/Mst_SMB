/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.widgets;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.util.StringUtil;
import com.sun.lwuit.Component;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.FocusListener;

/**
 *
 * @author karthik
 */
public class CustomTextField extends TextField implements DataChangedListener {

    public CustomTextField() {
        super();
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.addFocusListener(new CTFFocusListener());
    }

    public CustomTextField(String name, int constraint) {
        super(name);
        setUp(constraint);
    }

    public CustomTextField(int constraint) {
        super();
        setUp(constraint);
    }

    private void setUp(int constraint) {
        this.setConstraint(constraint);
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        this.addFocusListener(new CTFFocusListener());
        if (constraint == NUMERIC || constraint == PASSWORD || constraint == PHONENUMBER) {
            this.setInputMode("123");
        }
        if (constraint == PASSWORD) {
            this.setMaxSize(Constants.FIELD_LENGTH_PIN);
            this.setConstraint(TextField.NUMERIC | TextField.PASSWORD);
            this.addDataChangeListener(this);
        }
        if (constraint == PHONENUMBER) {
            this.setMaxSize(Constants.FIELD_LENGTH_MOBIILENO);
        }
    }

    public void dataChanged(int type, int index) {

        if (type == 1) {
            String str = getText();
            if (StringUtil.isBlank(str)) {
                return;
            }
            char c = str.charAt(str.length() - 1);
            if (!Character.isDigit(c)) {
                if (str.length() == 1) {
                    str = "";
                } else {
                    str = str.substring(0, str.length() - 2);
                }
            }
            this.setText(str);
        }
    }

    private class CTFFocusListener implements FocusListener {

        public void focusGained(Component cmpnt) {
            CustomTextField.this.getStyle().setBgColor(Constants.COLOUR_SLATEGRAY);
        }

        public void focusLost(Component cmpnt) {
            CustomTextField.this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        }
    }
}
