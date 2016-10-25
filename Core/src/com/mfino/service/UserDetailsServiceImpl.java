/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mfino.domain.Merchant;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Siddhartha Chinthapally
 */

public class UserDetailsServiceImpl implements UserDetails {

	private static final long serialVersionUID = 19283719L;
	private MfinoUser theUser;
	private Collection<GrantedAuthority> authorities;
	private boolean isNonLocked = true;
	private boolean isEnabled = true;
	private boolean isNonExpired = true;

	public UserDetailsServiceImpl(MfinoUser user) {
		this.theUser = user;
		loadAuthorities();
		loadAuthenticationAttributes();
	}

	private void loadAuthenticationAttributes() {		
		isNonLocked = checkIsNonLocked();
		isEnabled = checkIsEnabled();
		isNonExpired = checkIsNonExpired();
	}

	private boolean checkIsNonExpired() {

		Long status = theUser.getStatus();
		if (status == null || CmFinoFIX.UserStatus_Retired.equals(status)
				|| CmFinoFIX.UserStatus_Rejected.equals(status)
				|| CmFinoFIX.UserStatus_Expired.equals(status)) {
			return false;
		}

		if (CmFinoFIX.Role_Subscriber.equals(theUser.getRole())) {
			Set<Subscriber> subscriberSet = theUser
					.getSubscribersForSubscriberuserid();
			Iterator<Subscriber> subIter = subscriberSet.iterator();
			if (subIter.hasNext()) {
				Subscriber subscriber = subIter.next();
				Long subStatus = subscriber.getStatus().longValue();
				if (subStatus == null || CmFinoFIX.SubscriberStatus_Retired.equals(subStatus)) {
					return false;
				}
			}
		} else if (CmFinoFIX.Role_Merchant.equals(theUser.getRole())) {
			Set<Subscriber> subscriberSet = theUser.getSubscribersForUserid();
			Iterator<Subscriber> subIter = subscriberSet.iterator();
			if (subIter.hasNext()) {
				Subscriber subscriber = subIter.next();
				Long merStatus = subscriber.getMerchant().getStatus();
				if (merStatus == null || CmFinoFIX.SubscriberStatus_Retired.equals(merStatus)) {
					return false;
				}
			}
		}
		return true;

	}

	private boolean checkIsEnabled() {

		Long status = theUser.getStatus();
		if (CmFinoFIX.UserStatus_Confirmed.equals(status)
				|| CmFinoFIX.UserStatus_Registered.equals(status)) {
			return false;
		}

		if (CmFinoFIX.Role_Merchant.equals(theUser.getRole())) {
			// If Merchant, then do not enable those directly under SMART. They
			// are not real merchants. Just templates.

			Set<Subscriber> subscriberSet = theUser
					.getSubscribersForSubscriberuserid();
			Iterator<Subscriber> subIter = subscriberSet.iterator();
			if (subIter.hasNext()) {
				Subscriber subscriber = subIter.next();
				Merchant m = subscriber.getMerchant();
				if (null == m.getMerchant()) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean checkIsNonLocked() {
		Integer restr = theUser.getRestrictions();

		// Considered locked whether selfsuspended, suspened, securitylocked or
		// absolutelocked
		if (restr != null && restr.intValue() > 0) {
			return false;
		}

		// check for merchant and subscriber
		if (CmFinoFIX.Role_Merchant.equals(theUser.getRole())) {
			/** 
			 * There are no separate restrictions for Merchant. We use the
			 * ones from subscriber. There are restrictions in subscriber and 
			 * subscriber_mdn which are always kept in sync
			 */
			Set<Subscriber> subscriberSet = theUser.getSubscribersForUserid();
			Iterator<Subscriber> iter = subscriberSet.iterator();
			if (iter.hasNext()) {
				Long mRestr = iter.next().getRestrictions().longValue();
				if (mRestr != null && mRestr.intValue() > 0) {
					return false;
				}
			}
		} else if (CmFinoFIX.Role_Subscriber.equals(theUser.getRole())) {
			Set<Subscriber> subscriberSet = theUser
					.getSubscribersForSubscriberuserid();
			Iterator<Subscriber> iter = subscriberSet.iterator();
			if (iter.hasNext()) {
				Long mRestr = iter.next().getRestrictions().longValue();
				if (mRestr != null && mRestr.intValue() > 0) {
					return false;
				}
			}
		}
		return true;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		if (authorities == null) {
			loadAuthorities();
		}
		return authorities;
	}

	private void loadAuthorities() {
		Set<Integer> userAuthorities = theUser.getPermissions();
		Collection<GrantedAuthority> grantedAuthorityAdapters = new ArrayList<GrantedAuthority>();
		
		for (Integer auth : userAuthorities) {
			grantedAuthorityAdapters.add(new GrantedAuthorityService(auth));
		}
		authorities = grantedAuthorityAdapters;
//		authorities = grantedAuthorityAdapters.toArray(new GrantedAuthority[0]);
//		Arrays.sort(authorities);
	}

	
	public boolean isAccountNonExpired() {
		return isNonExpired;
	}
	
	public boolean isAccountNonLocked() {
		return isNonLocked;
	}

	public boolean isPasswordChangeRequired() {
		return theUser.getFirsttimelogin();
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public String getPassword() {
		return theUser.getPassword();
	}

	public String getUsername() {
		return theUser.getUsername();
	}

	public Integer getRole() {
		return theUser.getRole().intValue();
	}

	public String getLanguageCode() {
		return theUser.getLanguageCode();
	}
	
	//added as part of enhancement #2081
	@Override
	public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((theUser == null) ? 0 : theUser.getUsername().length());
	return result;
	}

	@Override
	public boolean equals(Object obj) {
	if (this == obj)
	return true;
	if (obj == null)
	return false;
	if (getClass() != obj.getClass())
	return false;
	UserDetailsServiceImpl other = (UserDetailsServiceImpl) obj;
	if (theUser == null) {
	if (other.theUser != null)
	return false;
	} else if (!theUser.getUsername().equals(other.theUser.getUsername()))
	return false;
	return true;
	}
}
