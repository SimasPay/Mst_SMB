/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BrandDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.query.BrandQuery;
import com.mfino.domain.Brand;
import com.mfino.domain.Company;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBrand;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.BrandProcessor;

/**
 *
 * @author ADMIN
 */
@Service("BrandProcessorImpl")
public class BrandProcessorImpl extends BaseFixProcessor implements BrandProcessor{

    private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

    private void updateEntity(Brand brand, CmFinoFIX.CMJSBrand.CGEntries e) {
        if (e.getBrandName() != null) {
            brand.setBrandName(e.getBrandName());
        }
        if (e.getInternationalCountryCode() != null) {
            brand.setInternationalCountryCode(e.getInternationalCountryCode());
        }
        if (e.getPrefixCode() != null) {
            BrandQuery query = new BrandQuery();
            BrandDAO bdao = DAOFactory.getInstance().getBrandDAO();
            String prefixCode = e.getPrefixCode();
            query.setPrefixCodeLike(prefixCode.substring(0, 2));
            List<Brand> results = bdao.get(query);
            //setting to null if already prefix code exist for the newly entered prefix.
            boolean isPrefixAllowed = true;
            if (results.size() > 0) {
                if (prefixCode.length() == 3) {
                    for (int i = 0; i < results.size(); i++) {
                        Brand b = results.get(i);
                        if (b.getPrefixCode().length() == 2 ||
                                b.getPrefixCode().equals(prefixCode)) {
                            isPrefixAllowed = false;
                            break;
                        }
                    }
                } else {
                    isPrefixAllowed = false;
                }
            }
            if (isPrefixAllowed) {
                brand.setPrefixCode(prefixCode);
            } else {
                brand.setPrefixCode(null);
            }
        }
        if (e.getCompanyID() != null) {
            CompanyDAO dao = DAOFactory.getInstance().getCompanyDAO();
            Company company = dao.getById(e.getCompanyID());
            brand.setCompany(company);
        }
        //need to change it
        if (brand.getmFinoServiceProviderByMSPID() == null) {
            brand.setmFinoServiceProviderByMSPID(mspDAO.getById(1L));
        }
    }

    private void updateMessage(Brand brand, CMJSBrand.CGEntries entry) {

        entry.setID(brand.getID());

        if (brand.getBrandName() != null) {
            entry.setBrandName(brand.getBrandName());
        }
        if (brand.getInternationalCountryCode() != null) {
            entry.setInternationalCountryCode(brand.getInternationalCountryCode());
        }
        if (brand.getCompany() != null) {
            entry.setCompanyName(brand.getCompany().getCompanyName());
        }
        if (brand.getPrefixCode() != null) {
            entry.setPrefixCode(brand.getPrefixCode());
        }
        if (brand.getCompany() != null) {
            entry.setCompanyID(brand.getCompany().getID());
        }
        if (brand.getCreateTime() != null) {
            entry.setCreateTime(brand.getCreateTime());
        }
        if (brand.getCreatedBy() != null) {
            entry.setCreatedBy(brand.getCreatedBy());
        }
        if (brand.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(brand.getLastUpdateTime());
        }
        if (brand.getUpdatedBy() != null) {
            entry.setUpdatedBy(brand.getUpdatedBy());
        }
        if (brand.getVersion() != null) {
            entry.setRecordVersion(brand.getVersion());
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSBrand realMsg = (CMJSBrand) msg;

        BrandDAO dao = DAOFactory.getInstance().getBrandDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSBrand.CGEntries[] entries = realMsg.getEntries();

            for (CMJSBrand.CGEntries e : entries) {
                Brand brand = dao.getById(e.getID());

                // Check for Stale Data
                if (!e.getRecordVersion().equals(brand.getVersion())) {
                    handleStaleDataException();
                }

                updateEntity(brand, e);
                try {
                    dao.save(brand);
                } catch (ConstraintViolationException error) {
                    return handleUniqueConstraintViolation(error);
                }
                updateMessage(brand, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);

        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            BrandQuery query = new BrandQuery();
            //restricting to show the brand the loggined in user
            if (userService.getUserCompany() != null) {
                query.setCompany(userService.getUserCompany());
            }
            if (StringUtils.isNotBlank(realMsg.getPrefixCodeSearch())) {
                query.setPrefixCode(realMsg.getPrefixCodeSearch());
            }
            if (StringUtils.isNotBlank(realMsg.getBrandNameSearch())) {
                query.setBrandName(realMsg.getBrandNameSearch());
            }
//            if (StringUtils.isNotBlank(realMsg.getCompanyIDSearch())) {
//                query.setCompanyId(realMsg.getCompanyIDSearch());
//            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            query.setId(realMsg.getIDSearch());

            List<Brand> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Brand brand = results.get(i);

                CMJSBrand.CGEntries entry = new CMJSBrand.CGEntries();
                updateMessage(brand, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSBrand.CGEntries[] entries = realMsg.getEntries();
            if (authorizationService.isAuthorized(CmFinoFIX.Permission_Brand_Add)) {
                for (CMJSBrand.CGEntries e : entries) {
                    Brand brand = new Brand();
                    updateEntity(brand, e);
                    // check whether entered prefix is valid or not before saving the file.
                    if (brand.getPrefixCode() == null) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Prefix Code already exists"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                    try {
                        dao.save(brand);
                    } catch (ConstraintViolationException error) {
                        return handleUniqueConstraintViolation(error);
                    }
                    updateMessage(brand, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to add new Brand"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSBrand.CGEntries.FieldName_BrandName,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }

    public CMJSError handleUniqueConstraintViolation(ConstraintViolationException constraintExp) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._("Brand Name already exists");
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        log.warn(message, constraintExp);
        return errorMsg;
    }
}
