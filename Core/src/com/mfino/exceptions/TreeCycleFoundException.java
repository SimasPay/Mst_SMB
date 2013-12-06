/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.exceptions;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class TreeCycleFoundException extends Exception {

    public TreeCycleFoundException() {
        super();
    }

    public TreeCycleFoundException(String errorMsg) {
        super(errorMsg);
    }

    public TreeCycleFoundException(String errorMsg, Throwable theCause) {
        super(errorMsg, theCause);
    }

    public TreeCycleFoundException(Throwable theCause) {
        super(theCause);
    }


}
