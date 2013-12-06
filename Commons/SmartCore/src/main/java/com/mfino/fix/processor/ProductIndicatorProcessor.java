/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ProductIndicatorDAO;
import com.mfino.dao.query.ProductIndicatorQuery;
import com.mfino.domain.Company;
import com.mfino.domain.ProductIndicator;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSProductIndicator;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.service.UserService;
import com.mfino.uicore.web.WebContextError;
/**
 *
 * @author Diwakar
 */
public class ProductIndicatorProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSProductIndicator realMsg = (CMJSProductIndicator) msg;
        ProductIndicatorQuery query = new ProductIndicatorQuery();
        ProductIndicatorDAO dao = DAOFactory.getInstance().getProductIndicatorDAO();
        if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
            CMJSProductIndicator.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Product_Indicator_Add)) {
                for (CMJSProductIndicator.CGEntries e : entries) {
                    ProductIndicator l = new ProductIndicator();
                    updateEntity(l, e);
                    try {
                        dao.save(l);
                    } catch (ConstraintViolationException t) {
                        handleUniqueConstraintViolation(t);
                    }
                    updateMessage(l, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to add new Product Indicator Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSProductIndicator.CGEntries.FieldName_ProductIndicatorCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);

        } else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            if (UserService.getUserCompany() != null) {
                query.setCompany(UserService.getUserCompany());
            }
            if (realMsg.getTransactionTypeSearch() != null) {
                query.setTransactionType(realMsg.getTransactionTypeSearch());
            }
//            if (realMsg.getCompanyCodeSearch() != null) {
//                query.setCompanyCode(realMsg.getCompanyCodeSearch());
//            }
            if (realMsg.getProductIndicatorCodeSearch() != null && realMsg.getProductIndicatorCodeSearch().length() > 0) {
                query.setProductCode(realMsg.getProductIndicatorCodeSearch());
            }

            query.setId(realMsg.getIDSearch());

            List<ProductIndicator> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                ProductIndicator s = results.get(i);
                CMJSProductIndicator.CGEntries entry = new CMJSProductIndicator.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        }
        return realMsg;
    }

    private void updateMessage(ProductIndicator productindicator, CMJSProductIndicator.CGEntries entry) {

        entry.setID(productindicator.getID());
        entry.setTransactionUICategory(productindicator.getTransactionUICategory());
        if (productindicator.getCompany() != null) {
            entry.setCompanyName(productindicator.getCompany().getCompanyName());
        }
        entry.setProductIndicatorCode(productindicator.getProductIndicatorCode());
        entry.setChannelSourceApplication(productindicator.getChannelSourceApplication());
        entry.setChannelSourceApplicationText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SourceApplication, null, entry.getChannelSourceApplication()));
        entry.setTransactionUICategoryText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, entry.getTransactionUICategory()));
        if (productindicator.getChannelText() != null) {
            entry.setChannelText(productindicator.getChannelText());
        }
        if (productindicator.getRequestorID() != null) {
            entry.setRequestorID(productindicator.getRequestorID());
        }
        if (productindicator.getProductDescription() != null) {
            entry.setProductDescription(productindicator.getProductDescription());
        }
        if (productindicator.getCreateTime() != null) {
            entry.setCreateTime(productindicator.getCreateTime());
        }
        if (productindicator.getCreatedBy() != null) {
            entry.setCreatedBy(productindicator.getCreatedBy());
        }
        if (productindicator.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(productindicator.getLastUpdateTime());
        }
        if (productindicator.getUpdatedBy() != null) {
            entry.setUpdatedBy(productindicator.getUpdatedBy());
        }
    }

    private void updateEntity(ProductIndicator pi, CmFinoFIX.CMJSProductIndicator.CGEntries e) {

        if (e.getTransactionUICategory() != null) {
            pi.setTransactionUICategory(e.getTransactionUICategory());
        }
        if (e.getChannelSourceApplication() != null) {
            pi.setChannelSourceApplication(e.getChannelSourceApplication());
        }
//        if (e.getCompanyCode() != null) {
//            pi.setCompanyCode(e.getCompanyCode());
//        }

        pi.setCompany(UserService.getUserCompany());

        if (e.getCompanyID() != null) {
            CompanyDAO dao = DAOFactory.getInstance().getCompanyDAO();
            Company company = dao.getById(e.getCompanyID());
            pi.setCompany(company);
        }
        if (e.getRequestorID() != null) {
            pi.setRequestorID(e.getRequestorID());
        }
        if (e.getProductDescription() != null) {
            pi.setProductDescription(e.getProductDescription());
        }
        if (e.getChannelText() != null) {
            pi.setChannelText(e.getChannelText());
        }
        if (e.getProductIndicatorCode() != null) {
            pi.setProductIndicatorCode(e.getProductIndicatorCode());
        }
    }
    private void handleUniqueConstraintViolation(ConstraintViolationException cvError) throws ConstraintViolationException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        String message = MessageText._("Product Indicator with same Txn Type, Channel, Company Code, Requestor ID, Product Description and Channel Text Already Exists");
        error.setErrorDescription(message);
        error.allocateEntries(1);
        error.getEntries()[0] = new CmFinoFIX.CMJSError.CGEntries();
        error.getEntries()[0].setErrorName(CmFinoFIX.CMJSProductIndicator.CGEntries.FieldName_ProductIndicatorCode);
        error.getEntries()[0].setErrorDescription(message);
        WebContextError.addError(error);
        log.warn(message, cvError);
        throw cvError;
    }
}
