/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.LOPDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.domain.LOP;
import com.mfino.domain.Merchant;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.LOPService;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author Venkata Krishna Teja D
 */
@Service("LOPServiceImpl")
public class LOPServiceImpl implements LOPService {

	/*
	 * (non-Javadoc)
	 * @see com.mfino.service.LOPService#checkAndSetExpiredStatus(com.mfino.domain.LOP)
	 */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void checkAndSetExpiredStatus(LOP lop) {
        checkAndSetExpiredStatus(lop, null);
    }

    /*
     * (non-Javadoc)
     * @see com.mfino.service.LOPService#checkAndSetExpiredStatus(com.mfino.domain.LOP, com.mfino.dao.LOPDAO)
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void checkAndSetExpiredStatus(LOP lop, LOPDAO dao) {
        // Here check if the record is expired or not.
        // Get the Created Time and Check with the configured time.
        // If the Status is not pending then return.
        if (false == (CmFinoFIX.LOPStatus_Pending.equalsIgnoreCase(lop.getStatus()))) {
            return;
        }

        int configuredLOPExpiryDays = ConfigurationUtil.getLOPExpirationInDays();

        // Now Convert this timestamp
        Calendar lopCreatedTimeCalendar = Calendar.getInstance(ConfigurationUtil.getLocalTimeZone());
        lopCreatedTimeCalendar.setTime(lop.getCreateTime());
        lopCreatedTimeCalendar.set(Calendar.HOUR_OF_DAY, 00);
        lopCreatedTimeCalendar.set(Calendar.SECOND, 00);
        lopCreatedTimeCalendar.set(Calendar.MINUTE, 00);

        lopCreatedTimeCalendar.add(Calendar.DAY_OF_MONTH, configuredLOPExpiryDays);

        Calendar currentCalendar = Calendar.getInstance(ConfigurationUtil.getLocalTimeZone());
        currentCalendar.set(Calendar.HOUR_OF_DAY, 00);
        currentCalendar.set(Calendar.SECOND, 00);
        currentCalendar.set(Calendar.MINUTE, 00);

        if(currentCalendar.compareTo(lopCreatedTimeCalendar) >=0 ) {
            // If we reach here set the LOP status to expired.
            lop.setStatus(CmFinoFIX.LOPStatus_Expired);
            if(null == dao) {
                return;
            }
            // If we reach here the we need to save the data to db.
            dao.save(lop);

            resetCurrentWeeklyAmount(lop);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mfino.service.LOPService#resetCurrentWeeklyAmount(com.mfino.domain.LOP)
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void resetCurrentWeeklyAmount(LOP lop) {
        // Revert the amount that is distributed back to the current weekly amount.
        // When the LOP is created the Distributed amount of this lop is added to
        // the currently weekly purchase.
        // When the LOP gets expired or rejected,
        // the amount that is added to the current weekly purchase should
        // be deducted and must allow the max usage as the distributed amount in
        // the LOP is not used.
        BigDecimal lopAmount = lop.getAmountDistributed();

        Merchant merchantForThisLOP = lop.getMerchantBySubscriberID();
        BigDecimal currentAmount = merchantForThisLOP.getCurrentweeklypurchaseamount();
        merchantForThisLOP.setCurrentweeklypurchaseamount(currentAmount.subtract(lopAmount));

        MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();
        merchantDAO.save(merchantForThisLOP);
    }
}
