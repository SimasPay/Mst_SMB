/**
 * 
 */
package com.mfino.service;

import com.mfino.dao.LOPDAO;
import com.mfino.domain.LOP;

/**
 * @author Sreenath
 *
 */
public interface LOPService {
	/**
	 * 
	 * @param lop
	 */
    public void checkAndSetExpiredStatus(LOP lop);

    /**
     * 
     * @param lop
     * @param dao
     */
    public void checkAndSetExpiredStatus(LOP lop, LOPDAO dao);

    /**
     * 
     * @param lop
     */
    public void resetCurrentWeeklyAmount(LOP lop);

}
