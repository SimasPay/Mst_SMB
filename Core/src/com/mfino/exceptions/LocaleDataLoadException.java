/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.exceptions;

/**
 * 
 * @author Venkata Krishna Teja D
 */
public class LocaleDataLoadException extends Exception {

    public LocaleDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocaleDataLoadException(String message) {
        super(message);
    }
}
