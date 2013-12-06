package com.mfino.domain;

import com.mfino.fix.CmFinoFIX;

public class Partner extends CmFinoFIX.CRPartner{
	
	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if(obj == null){
            return false;
        } else {
            if(getID() != null){
            	return (getID().equals(((Partner)obj).getID()));
            }
            
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		if(null != this.getID()){
			return this.getID().intValue();
		}
		
		return -1;
	}
	
	@Override
	public String toString() {
		return "PartnerId:"+getID() + ", TradeName:"+getTradeName();
	}
}
