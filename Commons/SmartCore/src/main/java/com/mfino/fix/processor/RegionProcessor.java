/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.RegionDAO;
import com.mfino.dao.query.RegionQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Region;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSRegion;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.service.UserService;
import com.mfino.uicore.web.WebContextError;

/**
 *
 * @author Raju
 */
public class RegionProcessor extends BaseFixProcessor {

    private void updateEntity(Region reg, CMJSRegion.CGEntries e) {
        if (e.getRegionName() != null) {
            reg.setRegionName(e.getRegionName());
        }
        if (e.getRegionCode() != null) {
            reg.setRegionCode(e.getRegionCode());
        }
//        if (e.getCompanyName() != null) {
//            reg.setCompanyName(e.getCompanyName());
//        }
        if (e.getDescription() != null) {
            reg.setDescription(e.getDescription());
        }
        if (e.getCreatedBy() != null) {
            reg.setCreatedBy(e.getCreatedBy());
        }
        if (e.getUpdatedBy() != null) {
            reg.setUpdatedBy(e.getUpdatedBy());
        }
        if (e.getCreateTime() != null) {
            reg.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            reg.setLastUpdateTime(e.getLastUpdateTime());
        }
        if (e.getCompanyID() != null) {
            CompanyDAO dao = DAOFactory.getInstance().getCompanyDAO();
            Company company = dao.getById(e.getCompanyID());
            reg.setCompany(company);
        }
    }

    private void updateMessage(Region reg, CMJSRegion.CGEntries e) {
        e.setID(reg.getID());
        if (reg.getRegionName() != null) {
            e.setRegionName(reg.getRegionName());
        }
        if (reg.getRegionCode() != null) {
            e.setRegionCode(reg.getRegionCode());
        }
//        if (reg.getCompanyName() != null) {
//            e.setCompanyName(reg.getCompanyName());
//        }
        if (reg.getDescription() != null) {
            e.setDescription(reg.getDescription());
        }
        if (reg.getCreatedBy() != null) {
            e.setCreatedBy(reg.getCreatedBy());
        }
        if (reg.getUpdatedBy() != null) {
            e.setUpdatedBy(reg.getUpdatedBy());
        }
        if (reg.getCreateTime() != null) {
            e.setCreateTime(reg.getCreateTime());
        }
        if (reg.getLastUpdateTime() != null) {
            e.setLastUpdateTime(reg.getLastUpdateTime());
        }
        if (reg.getCompany() != null) {
            e.setCompanyID(reg.getCompany().getID());
            e.setCompanyName(reg.getCompany().getCompanyName());
        }
        e.setDisplayText(String.format("%s (%s)", reg.getRegionCode(), reg.getRegionName()));
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSRegion realMsg = (CMJSRegion) msg;
        RegionDAO dao = DAOFactory.getInstance().getRegionDAO();
        Region region = new Region();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSRegion.CGEntries[] entries = realMsg.getEntries();
            for (CMJSRegion.CGEntries e : entries) {
                Region reg = dao.getById(e.getID());
                updateEntity(reg, e);
                dao.save(reg);
                updateMessage(reg, e);
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            RegionQuery query = new RegionQuery();
            if (UserService.getUserCompany() != null) {
                query.setCompany(UserService.getUserCompany());
            }
            if (realMsg.getIDSearch() != null) {
                query.setRegionID(realMsg.getIDSearch());
            }
//            if (realMsg.getCompanyNameSearch() != null && realMsg.getCompanyNameSearch().length() > 0) {
//                query.setCompanyName(realMsg.getCompanyNameSearch());
//            }
            if (realMsg.getRegionNameSearch() != null && realMsg.getRegionNameSearch().length() > 0) {
                query.setRegionName(realMsg.getRegionNameSearch());
            }
            if (realMsg.getRegionCodeSearch() != null && realMsg.getRegionCodeSearch().length() > 0) {
                query.setRegionCode(realMsg.getRegionCodeSearch());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<Region> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Region s = results.get(i);
                CMJSRegion.CGEntries entry = new CMJSRegion.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSRegion.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Region_Add)) {
                for (CMJSRegion.CGEntries e : entries) {
                    updateEntity(region, e);
                    try {
                        dao.save(region);
                    } catch (ConstraintViolationException t) {
                        handleUniqueConstraintViolation(t);
                    }
                    updateMessage(region, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to add new Region"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSRegion.CGEntries.FieldName_RegionName,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }

    private void handleUniqueConstraintViolation(ConstraintViolationException cvError) throws ConstraintViolationException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        String message = MessageText._("Region Code Already Exists");
        error.setErrorDescription(message);
        error.allocateEntries(1);
        error.getEntries()[0] = new CmFinoFIX.CMJSError.CGEntries();
        error.getEntries()[0].setErrorName(CmFinoFIX.CMJSRegion.CGEntries.FieldName_RegionCode);
        error.getEntries()[0].setErrorDescription(message);
        WebContextError.addError(error);
        log.warn(message, cvError);
        throw cvError;
    }
}
