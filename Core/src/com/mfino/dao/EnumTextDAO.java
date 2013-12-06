/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.EnumText;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author xchen
 */
public class EnumTextDAO extends BaseDAO<EnumText> {

    
    public List<EnumText> get(EnumTextQuery query) {

        Criteria criteria = createCriteria();
        if (query.getTagId() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CREnumText.FieldName_TagID, query.getTagId()));
        }
//        if (query.getLanguage() != null) {
//            criteria.add(Restrictions.eq(CmFinoFIX.CREnumText.FieldName_Language, query.getLanguage()));
//        } else {
//            criteria.add(Restrictions.eq(CmFinoFIX.CREnumText.FieldName_Language, CmFinoFIX.Language_English));
//        }
        if (query.getEnumCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CREnumText.FieldName_EnumCode, query.getEnumCode()).ignoreCase());
        }
        if(query.getTagName() != null)
        {
            addLikeStartRestriction(criteria, CmFinoFIX.CREnumText.FieldName_TagName, query.getTagName());
        }
        if (query.getLanguage() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CREnumText.FieldName_Language, query.getLanguage()));
        }
        if(query.getFieldName() != null)
        {
            addLikeStartRestriction(criteria, CmFinoFIX.CREnumText.FieldName_EnumValue, query.getFieldName());
        }
        if(query.getDisplayText() != null)
        {
            addLikeStartRestriction(criteria, CmFinoFIX.CREnumText.FieldName_DisplayText, query.getDisplayText());
        }
        
         // Paging
        processPaging(query, criteria);

        //applying Order
        applyOrder(query, criteria);

        @SuppressWarnings("unchecked")
        List<EnumText> results = (List<EnumText>) criteria.list();

        return results;
    }
}
