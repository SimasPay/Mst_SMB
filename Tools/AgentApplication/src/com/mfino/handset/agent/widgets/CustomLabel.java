/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.widgets;

import com.mfino.handset.agent.constants.Constants;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;

/**
 *
 * @author karthik
 */
public class CustomLabel extends Label {

    public CustomLabel(String name) {
        super(name);
        getStyle().setBgColor(Constants.COLOUR_GRAY);
    }

    public CustomLabel(Image img) {
        super(img);
        getStyle().setBgColor(Constants.COLOUR_GRAY);
    }
}
