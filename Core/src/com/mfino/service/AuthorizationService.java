/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.domain.PermissionItem;
import com.mfino.domain.User;
import com.mfino.enums.ItemType;

/**
 * @author Sreenath
 *
 */
public interface AuthorizationService {
	
    public void Init();

    /**
     * Checks whether the current Role has the specified permission
     * @param permission
     * @return
     */
    public boolean isAuthorized(Integer permission);

     /**
      * Checks if the given combination of itemType, msgname and action is permitted.
      * We have to infer the field here.
      * The following algorithm is used to infer the field:
      * 1. For 'read'(select), 'create'(insert) and 'destroy' actions most often the field is 'All'. If the role is Merchant
      * then check 'selfdownline' (only if 'All' is not given). If selfdownline is not valid for the combination, return false.
      * 2. For 'update' action, the field is immaterial as long as the Role has permission for at least one field.
      * The exact field check is done in the respective processors using the specific permission.
      * 3. For 'default' action. Same as update. Allow as long as the Role has permission for one field.
      *
      * @param itemType
      * @param msgClassName
      * @param action
      * @return
      */
     public boolean isAuthorized(ItemType itemType, String msgClassName, String action);

    /**
     * Checks if the combination of permission items is permitted for the current Role.
     *
     * @param type - One of <code>ItemType.FixMsg</code>
     * @param itemID -
     * @param fieldID
     * @param action
     * @param defaultValue - The default value to be returned if the combination does not map to any permission.
     * @return
     */
    public boolean isAuthorized(ItemType type, String itemID, String fieldID, String action, boolean exactMatch, boolean defaultValue);


    // returns the itemIds for the current user
    /**
     * 
     * @param type
     * @return
     */
    public String[] getEnabledItemIds(int type);

    /**
     * 
     * @return
     */
	public List<PermissionItem> getPermissionItemsList();

	/**
	 * 
	 * @param permissionItemsList
	 */
	public void setPermissionItemsList(List<PermissionItem> permissionItemsList);
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public boolean enablePinPrompt(User user);

}
