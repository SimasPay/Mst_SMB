/**
 * 
 */
package com.mfino.service;

import com.mfino.dao.LOPDAO;
import com.mfino.domain.LetterOfPurchase;

/**
 * @author Sreenath
 *
 */
public interface LOPService {
	/**
	 * 
	 * @param lop
	 */
    public void checkAndSetExpiredStatus(LetterOfPurchase lop);

    /**
     * 
     * @param lop
     * @param dao
     */
    public void checkAndSetExpiredStatus(LetterOfPurchase lop, LOPDAO dao);

    /**
     * 
     * @param lop
     */
    public void resetCurrentWeeklyAmount(LetterOfPurchase lop);

}
