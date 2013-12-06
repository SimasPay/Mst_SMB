/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.widgets;

import com.mfino.handset.agent.constants.Constants;
import com.sun.lwuit.Component;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.events.FocusListener;

/**
 *
 * @author karthik
 */
public class CustomRadioButton extends RadioButton{
    
    public CustomRadioButton(String name){
        super(name);
        getStyle().setBgColor(Constants.COLOUR_GRAY);
        addFocusListener(new RadioButtonFocusListener(this));
    }
    private class RadioButtonFocusListener implements FocusListener {

        private RadioButton rb;
        public RadioButtonFocusListener(RadioButton rButton) {
            rb = rButton;
        }
        public void focusGained(Component cmpnt) {
            rb.setSelected(true);
            rb.getStyle().setBgColor(Constants.COLOUR_GRAY);
        }
        public void focusLost(Component cmpnt) {
            rb.setSelected(false);
            rb.getStyle().setBgColor(Constants.COLOUR_GRAY);
        }
    }
}
