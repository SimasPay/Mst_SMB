/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.domain;

import com.mfino.fix.CmFinoFIX.CRPermissionItems;

/**
 *
 * @author sandeepjs
 */
public class PermissionItems extends CRPermissionItems {


    @Override
    public boolean equals(Object newObject)
    {

        if(!(newObject instanceof PermissionItems))
            return false;

        PermissionItems pNewObject = (PermissionItems) newObject;


        if(this.getItemType().equals(pNewObject.getItemType()) &&
            this.getItemID().trim().equals(pNewObject.getItemID().trim()) &&
            this.getFieldID().trim().equals(pNewObject.getFieldID().trim()) &&
            this.getAction().trim().equals(pNewObject.getAction().trim()))
        {
            return true;
        }
        else
            return false;
        
    }

    public boolean matchesWithoutField(PermissionItems permissionItems){
        return this.getItemType().equals(permissionItems.getItemType()) &&
            this.getItemID().trim().equals(permissionItems.getItemID().trim()) &&
            this.getAction().trim().equals(permissionItems.getAction().trim());
    }

}
