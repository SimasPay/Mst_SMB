/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.BrandDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MDNRangeDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.query.MDNRangeQuery;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.Brand;
import com.mfino.domain.MDNRange;
import com.mfino.domain.Merchant;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMDNRange;
import com.mfino.i18n.MessageText;
import com.mfino.service.MDNRangeService;
import com.mfino.service.MerchantService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.service.UserService;

/**
 *
 * @author ADMIN
 */
public class MDNRangeProcessor extends BaseFixProcessor {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final boolean Success = true;
    //private final boolean Failure = false;

    public void processChildMDNsInParentMDNRange(Merchant merchant) {
        log.info("processChildMDNsInParentMDNRange " + merchant.getID());
        MerchantDAO dao = DAOFactory.getInstance().getMerchantDAO();
        MerchantQuery que = new MerchantQuery();
        que.setParentID(merchant.getID());
        List<Merchant> childMerchantlist = dao.get(que);//child list
        for (int i = 0; i < childMerchantlist.size(); i++) {
            Merchant childMerchant = ((Merchant) childMerchantlist.toArray()[i]);
            String mdn = MerchantService.getMDNFromMerchant(childMerchant);

            //Need to take care of MDNs which are tagged with R
            int findR = mdn.indexOf('R');
            if (findR != -1) {
                mdn = mdn.substring(0, findR);
            }
//            String prefixCode = MerchantService.getPrefixCodeFromMdn(mdn);
//            if (prefixCode == null) {
//                childMerchant.setRangeCheck(childMerchant.getRangeCheck() == null ? CmFinoFIX.RangeCheck_MDNNotInParentsRange : childMerchant.getRangeCheck() | CmFinoFIX.RangeCheck_MDNNotInParentsRange);
//                continue;
//            }
            // assumption is international code would be of length 2.
            if (MDNRangeService.isMDNInParentsRange(Long.parseLong(mdn.substring(2)), merchant) == Success) {
                log.info("Clearing Range check " + CmFinoFIX.RangeCheck_MDNNotInParentsRange);
                childMerchant.setRangeCheck(childMerchant.getRangeCheck() == null ? 0 : childMerchant.getRangeCheck() & CmFinoFIX.RangeCheck_MDNRangeNotInParentsRange);
            } else {
                log.info("seeting Range check " + CmFinoFIX.RangeCheck_MDNNotInParentsRange);
                childMerchant.setRangeCheck(childMerchant.getRangeCheck() == null ? CmFinoFIX.RangeCheck_MDNNotInParentsRange : childMerchant.getRangeCheck() | CmFinoFIX.RangeCheck_MDNNotInParentsRange);
            }
            dao.save(childMerchant);
        }
    }

    private void updateEntity(MDNRange mdnRange, CmFinoFIX.CMJSMDNRange.CGEntries e) {
        if (e.getStartPrefix() != null) {
            mdnRange.setStartPrefix(e.getStartPrefix());
        }

        if (e.getEndPrefix() != null) {
            mdnRange.setEndPrefix(e.getEndPrefix());
        }

        if (e.getMerchantID() != null) {
            MerchantDAO mdao = DAOFactory.getInstance().getMerchantDAO();
            Merchant m = mdao.getById(e.getMerchantID());
            mdnRange.setMerchant(m);
        }

        if (e.getBrandID() != null) {
            BrandDAO dao = DAOFactory.getInstance().getBrandDAO();
            Brand brand = dao.getById(e.getBrandID());
            mdnRange.setBrand(brand);
        }

        if (e.getCreateTime() != null) {
            mdnRange.setCreateTime(e.getCreateTime());
        }

        if (e.getLastUpdateTime() != null) {
            mdnRange.setLastUpdateTime(e.getLastUpdateTime());
        }

        if (e.getUpdatedBy() != null) {
            mdnRange.setUpdatedBy(e.getUpdatedBy());
        }

        if (e.getCreatedBy() != null) {
            mdnRange.setCreatedBy(e.getCreatedBy());
        }

    }

    private void updateMessage(MDNRange mdnRange, CMJSMDNRange.CGEntries entry) {

        entry.setID(mdnRange.getID());

        if (mdnRange.getStartPrefix() != null) {
            entry.setStartPrefix(mdnRange.getStartPrefix());
        }

        if (mdnRange.getEndPrefix() != null) {
            entry.setEndPrefix(mdnRange.getEndPrefix());
        }

        if (mdnRange.getMerchant() != null) {
            entry.setMerchantID(mdnRange.getMerchant().getID());
        }

        if (mdnRange.getBrand() != null) {
            entry.setBrandName(mdnRange.getBrand().getBrandName());
            entry.setPrefixCode(mdnRange.getBrand().getPrefixCode());
            entry.setBrandID(mdnRange.getBrand().getID());
        }

        if (mdnRange.getCreateTime() != null) {
            entry.setCreateTime(mdnRange.getCreateTime());
        }

        if (mdnRange.getCreatedBy() != null) {
            entry.setCreatedBy(mdnRange.getCreatedBy());
        }

        if (mdnRange.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(mdnRange.getLastUpdateTime());
        }

        if (mdnRange.getUpdatedBy() != null) {
            entry.setUpdatedBy(mdnRange.getUpdatedBy());
        }

        if (mdnRange.getVersion() != null) {
            entry.setRecordVersion(mdnRange.getVersion());
        }

        if (Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_MDNRange_Edit) || UserService.isMerchant() && MerchantService.getMerchantIDOfLoggedInUser() != entry.getMerchantID() && MerchantService.isAuthorizedForLOP(MerchantService.getMerchantIDOfLoggedInUser())) {
            entry.setGridHideIndex(false);
        } else {
            entry.setGridHideIndex(true);
        }

    }

    public CFIXMsg process(
            CFIXMsg msg) throws Exception {
        CMJSMDNRange realMsg = (CMJSMDNRange) msg;

        MDNRangeDAO mdnRangeDao = DAOFactory.getInstance().getMDNRangeDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMDNRange.CGEntries[] entries = realMsg.getEntries();

            for (CMJSMDNRange.CGEntries e : entries) {
                MDNRange mdnRange = mdnRangeDao.getById(e.getID());

                // Check for Stale Data
                if (!e.getRecordVersion().equals(mdnRange.getVersion())) {
                    handleStaleDataException();
                }

                updateEntity(mdnRange, e);
                if (MDNRangeService.isUpdateRangeOK(mdnRange, e) == Success) {
                    mdnRangeDao.save(mdnRange);
                    Merchant merchant = mdnRange.getMerchant();
                    processChildMDNsInParentMDNRange(merchant);
                    MDNRangeService.processChildMDNRangeInParentMDNRange(merchant);
                } else {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Current Merchant range is out of parents range"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                    newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                    newEntries[0].setErrorName(MessageText._("MDN Range"));
                    newEntries[0].setErrorDescription(MessageText._("Not allowed"));
                    return errorMsg;
                }

                updateMessage(mdnRange, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            MDNRangeQuery query = new MDNRangeQuery();

            query.setId(realMsg.getIDSearch());
            if (realMsg.getMerchantIDSearch() != null) {
                query.setMerchantId(realMsg.getMerchantIDSearch());
            }

            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<MDNRange> results = mdnRangeDao.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                MDNRange mdnRange = results.get(i);
                CMJSMDNRange.CGEntries entry = new CMJSMDNRange.CGEntries();
                updateMessage(mdnRange, entry);
                realMsg.getEntries()[i] = entry;
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMDNRange.CGEntries[] entries = realMsg.getEntries();

            for (CMJSMDNRange.CGEntries e : entries) {
                MDNRange mdnRange = new MDNRange();
                updateEntity(mdnRange, e);
                if (MDNRangeService.isUpdateRangeOK(mdnRange, e) == Success) {
                    mdnRangeDao.save(mdnRange);
                    Merchant merchant = mdnRange.getMerchant();
                    processChildMDNsInParentMDNRange(merchant);
                    MDNRangeService.processChildMDNRangeInParentMDNRange(merchant);
                } else {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Current Merchant range is out of parents range"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                    newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                    newEntries[0].setErrorName(MessageText._("MDN Range"));
                    newEntries[0].setErrorDescription(MessageText._("Not allowed"));
                    return errorMsg;
                }

                updateMessage(mdnRange, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMDNRange.CGEntries[] entries = realMsg.getEntries();

            for (CMJSMDNRange.CGEntries e : entries) {
                MDNRange mdnRange = mdnRangeDao.getById(e.getID());
                Merchant merchant = mdnRange.getMerchant();
                mdnRangeDao.deleteById(e.getID());
                processChildMDNsInParentMDNRange(merchant);
                MDNRangeService.processChildMDNRangeInParentMDNRange(merchant);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }
}
