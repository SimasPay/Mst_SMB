package com.mfino.mce.component.iso.common;

import java.io.Serializable;

public class Envelope implements Serializable {

    // internal vars --------------------------------------------------------------------------------------------------

  
    private byte[] payload;

    // constructors ---------------------------------------------------------------------------------------------------

    public Envelope() {
    }

    public Envelope( byte[] payload) {
  
        this.payload = payload;
    }
    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Envelope{")
                .append(", payload=").append(payload == null ? null : payload.length + "bytes")
                .append('}').toString();
    }
}
