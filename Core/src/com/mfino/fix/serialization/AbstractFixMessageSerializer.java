/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;

/**
 *
 * @author sandeepjs
 */
public class AbstractFixMessageSerializer implements IFixMessageSerializer {

    protected String URL;
    protected String host;
    protected int port;
    protected String requestLine;

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    public CFIXMsg send(CFIXMsg msg) {
        throw new UnsupportedOperationException();
    }

    protected void parseHostNPort() {
        int length = "http://".length();

        String subUrl = URL.substring(0 + length);
        int indexi = subUrl.indexOf(":");
        int indexs = subUrl.indexOf("/");

        this.host = subUrl.substring(0, indexi);
        this.port = Integer.parseInt(subUrl.substring(indexi + 1, indexs));
        this.requestLine = subUrl.substring(indexs);
    }

    public CFIXMsg send(CFIXMsg message, String URL) {
        CFIXMsg retMessageVal = null;

        try {
            this.URL = URL;
            this.parseHostNPort();
            retMessageVal = this.send(message);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return retMessageVal;
    }

    public void sendAsync(CFIXMsg message, FixMessageSerializationHandler handler, String URL) {

        try {
            this.URL = URL;
            this.parseHostNPort();
            this.sendAsync(message, handler);
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
    }

    public void sendAsync(CFIXMsg message, FixMessageSerializationHandler handler) {
        throw new UnsupportedOperationException();
    }
}
