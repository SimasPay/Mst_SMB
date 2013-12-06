package com.mfino.sterling.crypto;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class ByteArrayFactoryBean extends AbstractFactoryBean<byte[]>{

    @Override
    public Class<?> getObjectType(){
        return byte[].class;
    }

    private String data;

    @Required
    public void setData(final String data){
        this.data = data;
    }

    @Override
    protected byte[] createInstance() throws Exception{
        final String[] tokens = data.split("\\s*,\\s*");
        final byte[] output = new byte[tokens.length];
        for(int i = 0; i < tokens.length; i++){
            output[i] = Byte.decode(tokens[i]).byteValue();
        }
        return output;
    }

}