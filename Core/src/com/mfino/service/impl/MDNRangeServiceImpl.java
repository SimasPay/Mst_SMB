/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.domain.MdnRange;
import com.mfino.domain.Merchant;
import com.mfino.service.MDNRangeService;

/**
 *
 * @author Diwakar
 */
@Service("MDNRangeServiceImpl")
public class MDNRangeServiceImpl implements MDNRangeService{

    private static Logger log = LoggerFactory.getLogger(MDNRangeServiceImpl.class);
    private static final boolean Success = true;
    private static final boolean Failure = false;

    /**
     * 
     * @param mdn
     * @param parent
     * @return
     * MDN used here is without international code and with brand prefix included.
     */
    public boolean isMDNInParentsRange(Long mdn, Merchant parent) {
        log.info("isMDNInParentsRange: MDN "+mdn + " where parent id is " + parent.getId());
        // an exception for top most guys
        if(parent.getMerchantByParentID() == null)
        {
            log.info("isMDNInParentsRange: Success, parent's parent is null");
            return Success;
        }
        Set<MdnRange> mdnRangeList = parent.getMdnRanges();
        if(mdnRangeList == null || mdnRangeList.size()<1){
            log.info("isMDNInParentsRange: Failure, range list is null");
            return Failure;
        }
        for (MdnRange range : mdnRangeList) {
            try {
                if ((mdn >= Long.parseLong(range.getBrand().getPrefixcode() + range.getStartprefix()))
                        && (mdn <= Long.parseLong(range.getBrand().getPrefixcode() + range.getEndprefix()))) {
                    log.info("isMDNInParentsRange: Success");
                    return Success;
                }
            } catch (NumberFormatException numFormatExp) {
                log.error("String could not be parsed into Long. Check the string properly", numFormatExp);
                return Failure;
            }
        }
        log.info("isMDNInParentsRange: Failure, final return due to unmatch of the ranges");
        return Failure;
    }
}
