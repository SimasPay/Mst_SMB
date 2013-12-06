/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.EnumTextDAO;
import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.EnumText;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSEnumText;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.EnumTextProcessor;
import com.mfino.uicore.web.WebContextError;

/**
 *
 * @author xchen
 */
@Service("EnumTextProcessorImpl")
public class EnumTextProcessorImpl extends BaseFixProcessor implements EnumTextProcessor{
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSEnumText realMsg = (CMJSEnumText) msg;

        EnumTextDAO dao = DAOFactory.getInstance().getEnumTextDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSEnumText.CGEntries[] entries = realMsg.getEntries();

            for (CMJSEnumText.CGEntries e : entries) {
                EnumText s = dao.getById(e.getID());
                updateEntity(s, e);
                dao.save(s);
                enumTextService.invalidateEnumTextSet(s.getTagID(), s.getLanguage());
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            EnumTextQuery query = new EnumTextQuery();

            query.setTagId(realMsg.getTagIDSearch());
            query.setFieldName(realMsg.getFieldNameSearch());
            query.setTagName(realMsg.getTagNameSearch());
            query.setLanguage(realMsg.getLanguage());
            query.setDisplayText(realMsg.getDisplayTextSearch());

            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            // Ordering the results by id in ascending order.
            query.setSortString("id:asc");

            List<EnumText> results = dao.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                EnumText s = results.get(i);
                CMJSEnumText.CGEntries entry =
                        new CMJSEnumText.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSEnumText.CGEntries[] entries = realMsg.getEntries();

            for (CMJSEnumText.CGEntries e : entries) {
                EnumText s = new EnumText();
                updateEntity(s, e);

                try {
                    dao.save(s);
                } catch (ConstraintViolationException t) {
                    handleUniqueConstraintViolation(t);
                }

                enumTextService.invalidateEnumTextSet(s.getTagID(), s.getLanguage());
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSEnumText.CGEntries[] entries = realMsg.getEntries();

            for (CMJSEnumText.CGEntries e : entries) {
                EnumText s = dao.getById(e.getID());
                enumTextService.invalidateEnumTextSet(s.getTagID(), s.getLanguage());
                dao.deleteById(e.getID());
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }

    public void updateEntity(EnumText s, CMJSEnumText.CGEntries e) {
        if (e.getID() != null) {
            s.setID(e.getID());
        }
        if (e.getEnumCode() != null) {
            s.setEnumCode(e.getEnumCode());
        }
        if (e.getEnumValue() != null) {
            s.setEnumValue(e.getEnumValue());
        }

        if (e.getLanguage() != null) {
            s.setLanguage(e.getLanguage());
        }
        if (e.getLastUpdateTime() != null) {
            s.setLastUpdateTime(e.getLastUpdateTime());
        }

        if (e.getTagID() != null) {
            s.setTagID((e.getTagID()));
        }

        if (e.getTagName() != null) {
            s.setTagName(e.getTagName());
        }

        if (e.getUpdatedBy() != null) {
            s.setUpdatedBy(e.getUpdatedBy());
        }

        if (e.getDisplayText() != null) {
            s.setDisplayText(e.getDisplayText());
        }
    }

    public void updateMessage(EnumText s, CMJSEnumText.CGEntries e) {

        if (s.getID() != null) {
            e.setID(s.getID());
        }
        if (s.getEnumCode() != null) {
            e.setEnumCode(s.getEnumCode());
        }
        if (s.getEnumValue() != null) {
            e.setEnumValue(s.getEnumValue());
        }

        if (s.getLanguage() != null) {
            e.setLanguage(s.getLanguage());
            e.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, s.getLanguage(), s.getLanguage()));
        }

        if (s.getLastUpdateTime() != null) {
            e.setLastUpdateTime(s.getLastUpdateTime());
        }

        if (s.getTagID() != null) {
            e.setTagID((s.getTagID()));
        }

        if (s.getTagName() != null) {
            e.setTagName(s.getTagName());
        }

        if (s.getUpdatedBy() != null) {
            e.setUpdatedBy(s.getUpdatedBy());
        }

        if (s.getDisplayText() != null) {
            e.setDisplayText(s.getDisplayText());
        }
    }

    private void handleUniqueConstraintViolation(ConstraintViolationException consVoilExp) throws ConstraintViolationException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        String message = MessageText._("Enum with same EnumCode, TagID and Language Already Exists");
        error.setErrorDescription(message);
        error.allocateEntries(1);
        error.getEntries()[0] = new CmFinoFIX.CMJSError.CGEntries();
        error.getEntries()[0].setErrorName(CmFinoFIX.CMJSEnumText.CGEntries.FieldName_EnumCode);
        error.getEntries()[0].setErrorDescription(message);
        WebContextError.addError(error);
        log.warn(message, consVoilExp);
        throw consVoilExp;
    }
}
