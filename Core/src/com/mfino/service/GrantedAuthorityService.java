/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.service;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class GrantedAuthorityService implements GrantedAuthority{
    
    private Integer userAuthority = null;

    public GrantedAuthorityService(Integer userAuthority) {
        this.userAuthority = userAuthority;
    }

    public String getAuthority() {
        return userAuthority.toString();
    }

    public int compareTo(Object o) {
        if(o == null)
            throw new IllegalArgumentException("Cannot compare with a null");
        if (o instanceof GrantedAuthorityService){
            GrantedAuthorityService gaa = (GrantedAuthorityService)o;
            return getAuthority().compareTo(gaa.getAuthority());
        } else
            throw new ClassCastException("Cannot cast to a GrantedAuthorityAdapter");        
    }
    
    public boolean equals(Object o) {
        if(o == null)
            throw new IllegalArgumentException("Cannot compare with a null");
        if (o instanceof GrantedAuthorityService){
            GrantedAuthorityService gaa = (GrantedAuthorityService)o;
            return getAuthority().equals(gaa.getAuthority());
        } else
            throw new ClassCastException("Cannot cast to a GrantedAuthorityAdapter");        
    }  
    
    @Override
    public int hashCode() {
    	// TODO Auto-generated method stub
    	return super.hashCode();
    }
}
