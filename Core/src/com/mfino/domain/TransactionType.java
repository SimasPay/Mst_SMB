package com.mfino.domain;

import com.mfino.fix.CmFinoFIX;

public class TransactionType extends CmFinoFIX.CRTransactionType{

	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if(obj == null){
            return false;
        } else {
            if(getID() != null){
            	return (getID().equals(((TransactionType)obj).getID()));
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
		return "{id:"+getID() + ", name="+getTransactionName()+"}";
	}
}
