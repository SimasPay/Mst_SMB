/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.widgets;

import com.sun.lwuit.Form;

/**
 *
 * @author karthik
 */
public class CustomizedForm  extends Form{

    protected CustomizedForm previousForm;
    public CustomizedForm (String name) {
        super(name);
//        this.setTransitionOutAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 500));
//        this.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, true, 500));
    }

    public CustomizedForm () {
        super();
//        this.setTransitionOutAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 500));
//        this.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, true, 500));
    }
}
