package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PermissionItemsDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.PermissionItem;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.MfinoUser;
import com.mfino.enums.ItemType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.AuthorizationService;
import com.mfino.service.GrantedAuthorityService;
import com.mfino.service.UserService;

/**
 *
 * @author Siddhartha Chinthapally
 */
@Service("AuthorizationServiceImpl")
public class AuthorizationServiceImpl implements AuthorizationService{

    // Caching the PermisionItems Objects.
    private static List<PermissionItem> permissionItemsList = null;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public void Init() {
        PermissionItemsDAO permissionItemsDAO = DAOFactory.getInstance().getPermissionItemsDAO();
        permissionItemsList = permissionItemsDAO.getAll();
    }

    /**
     * Checks whether the current Role has the specified permission
     * @param permission
     * @return
     */
    public boolean isAuthorized(Integer permission) {
        GrantedAuthority currentPermission = new GrantedAuthorityService(permission);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return false;
        }

        Collection<GrantedAuthority> permissions = (Collection<GrantedAuthority>) auth.getAuthorities();    
        if (CollectionUtils.isEmpty(permissions)) {
            return false;
        } 
        
        if (permissions.contains(currentPermission)) {
        	return true;
        }
        
        //According to the spec the GrantedAuthorities are supposed to be sorted in ascending order.
//        int index = Arrays.binarySearch(permissions, currentPermission);
//        if (index >= 0) {
//            return true;
//        }
        return false;
    }

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
     public boolean isAuthorized(ItemType itemType, String msgClassName, String action) {

    	 if(CmFinoFIX.JSaction_Select.equals(action) || CmFinoFIX.JSaction_Insert.equals(action) || CmFinoFIX.JSaction_Delete.equals(action)) {
            boolean isAuthAll = isAuthorized(itemType, msgClassName, "All", action, true, true);
            if(isAuthAll)
                return true;
            
            if(userService.isMerchant()){
                 return isAuthorized(itemType, msgClassName, "selfdownline", action, true, false);
            }
        } else if (CmFinoFIX.JSaction_Update.equals(action)){
            return isAuthorized(itemType, msgClassName, "", action, false, true);
        } else if ("default".equals(action)) {
            return isAuthorized(itemType, msgClassName, "", action, false, true);
        }
     
        return false;
    }

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
    public boolean isAuthorized(ItemType type, String itemID, String fieldID, String action, boolean exactMatch, boolean defaultValue) {
        boolean retValueBoolean = false;

        List<Integer> matches = getAllMatches(type, itemID, fieldID, action, exactMatch);

        if(CollectionUtils.isEmpty(matches))
            return defaultValue;

        //Check if there is at least one of the permission items is authorized!
        for(Integer match : matches){
            PermissionItem aItem = (PermissionItem) getPermissionItemsList().get(match);
            Long temp = aItem.getPermission();
            Integer tempI = temp.intValue();
            retValueBoolean = isAuthorized(tempI);
            if(true == retValueBoolean)
                break;
        }

        return retValueBoolean;
    }

    //check
    private List<Integer> getAllMatches(ItemType type, String itemID, String fieldID, String action, boolean exactMatch){
        int ptype = -1;
        if (ItemType.ExtJSItem.equals(type)) {
            ptype = 1;
        } else if (ItemType.FixMessage.equals(type)) {
            ptype = 0;
        }
        else if(ItemType.GetRequest.equals(type))
        {
            ptype =2;
        }

        PermissionItem pItem = new PermissionItem();

        pItem.setAction(action);
        pItem.setFieldid(fieldID);
        pItem.setItemid(itemID);
        pItem.setItemtype(ptype);

        List<Integer> matches = new ArrayList<Integer>();

        for(int i = 0; i < getPermissionItemsList().size(); i++) {
            PermissionItem listItem = (PermissionItem) getPermissionItemsList().get(i);
            if(exactMatch){
                if(pItem.equals(listItem))
                    matches.add(i);                
            } else {
                if(pItem.matchesWithoutField(listItem))
                    matches.add(i);                
            }
        }
        return matches;
    }

    // returns the itemIds for the current user
    public String[] getEnabledItemIds(int type) {
        Set<String> pItems = new HashSet<String>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return new String[0];
        }

        Collection<GrantedAuthority> permissions = (Collection<GrantedAuthority>) auth.getAuthorities();

        for (int i = 0; i < getPermissionItemsList().size(); i++) {
            PermissionItem item = (PermissionItem) getPermissionItemsList().get(i);
            Integer perm = (int) item.getPermission();
            Integer itemType = (int) item.getItemtype();
            if (itemType.intValue() == type) {
                GrantedAuthority currentPerm = new GrantedAuthorityService(perm);
                if (permissions.contains(currentPerm)) {
                	pItems.add(item.getItemid());
                }
                
//                int index = Arrays.binarySearch(permissions, currentPerm);
//                if (index >= 0) {
//                    pItems.add(item.getItemID());
//                }
            }
        }
        return pItems.toArray(new String[0]);
    }

	public List<PermissionItem> getPermissionItemsList() {
		if(permissionItemsList == null){
			Init();
		}
		return permissionItemsList;
	}

	public void setPermissionItemsList(List<PermissionItem> permissionItemsList) {
		AuthorizationServiceImpl.permissionItemsList = permissionItemsList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mfino.service.AuthorizationService#enablePinPrompt(com.mfino.domain.User)
	 */
	public boolean enablePinPrompt(MfinoUser user) {
		if(user==null){
			return false;
		}
		if(isAuthorized(CmFinoFIX.Permission_PinPrompt)){
			Set<Partner> partners =user.getPartners();
			if(partners==null||partners.isEmpty()){
				return false;
			}
			Subscriber sub = partners.iterator().next().getSubscriber();
			Set<SubscriberMdn> mdns = sub.getSubscriberMdns();
			if(mdns==null||mdns.isEmpty()){
				return false;
			}
			if(StringUtils.isBlank(mdns.iterator().next().getDigestedpin())){
				return true;
			}
		}
		return false;
	}
}
