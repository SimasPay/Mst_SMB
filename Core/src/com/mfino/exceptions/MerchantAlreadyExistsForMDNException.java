/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.exceptions;

/**
 *
 * @author sandeepjs
 */
public class MerchantAlreadyExistsForMDNException extends Exception {

    public MerchantAlreadyExistsForMDNException(String message)
    {
        super(message);
    }

}