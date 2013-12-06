/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.domain;

import com.mfino.fix.CmFinoFIX.CRDistributionChainTemplate;

/**
 *
 * @author xchen
 */
public class DistributionChainTemplate extends CRDistributionChainTemplate {

	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else {
            if(getID() != null){
            	return (getID().equals(((DistributionChainTemplate)obj).getID()));
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
		return "DCTID:"+getID() + ",DctName:"+getName();
	}
}
