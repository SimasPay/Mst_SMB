/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.Merchant;

/**
 * @author Sreenath
 *
 */
public interface MDNRangeService {

    /**
     * 
     * @param mdn
     * @param parent
     * @return
     * MDN used here is without international code and with brand prefix included.
     */
    public boolean isMDNInParentsRange(Long mdn, Merchant parent);

}
