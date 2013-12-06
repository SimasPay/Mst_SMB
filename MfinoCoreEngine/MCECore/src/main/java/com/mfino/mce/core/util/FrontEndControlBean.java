/**
 * 
 */
package com.mfino.mce.core.util;

/**
 * @author Bala Sunku
 *
 */
public class FrontEndControlBean {
	
	private boolean isOfflineBank;
	
	public boolean getIsOfflineBank() {
		return isOfflineBank;
	}

	public void setIsOfflineBank(boolean isOfflineBank) {
		this.isOfflineBank = isOfflineBank;
	}
	
	/**
	 * Check and returns true if the 'isOfflineBank' value is true.
	 * @return
	 */
	public boolean isOfflineBank() {
		if (getIsOfflineBank()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Check and returns true if the 'isOfflineBank' value is false.
	 * @return
	 */
	public boolean isOnlineBank() {
		if (getIsOfflineBank()) {
			return false;
		}
		else {
			return true;
		}
	}

}
