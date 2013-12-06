/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.serialization;

import com.mfino.fix.CFIXMsg;

/**
 *
 * @author sandeepjs
 */
public interface IFixMessageSerializer {

    public abstract CFIXMsg send(CFIXMsg message);

    public abstract CFIXMsg send(CFIXMsg message, String URL);

    public abstract void sendAsync(CFIXMsg message, FixMessageSerializationHandler handler);

    public abstract void sendAsync(CFIXMsg message, FixMessageSerializationHandler handler, String URL);
}
