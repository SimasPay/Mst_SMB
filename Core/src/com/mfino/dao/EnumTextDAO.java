/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.EnumText;

/**
 *
 * @author xchen
 */
public class EnumTextDAO extends BaseDAO<EnumText> {

    
    public List<EnumText> get(EnumTextQuery query) {

        Criteria criteria = createCriteria();
        if (query.getTagId() != null) {
            criteria.add(Restrictions.eq(EnumText.FieldName_TagID, query.getTagId()));
        }
//        if (query.getLanguage() != null) {
//            criteria.add(Restrictions.eq(EnumText.FieldName_Language, query.getLanguage()));
//        } else {
//            criteria.add(Restrictions.eq(EnumText.FieldName_Language, CmFinoFIX.Language_English));
//        }
        if (query.getEnumCode() != null) {
            criteria.add(Restrictions.eq(EnumText.FieldName_EnumCode, query.getEnumCode()).ignoreCase());
        }
        if(query.getTagName() != null)
        {
            addLikeStartRestriction(criteria, EnumText.FieldName_TagName, query.getTagName());
        }
        if (query.getLanguage() != null) {
            criteria.add(Restrictions.eq(EnumText.FieldName_Language, query.getLanguage()));
        }
        if(query.getFieldName() != null)
        {
            addLikeStartRestriction(criteria, EnumText.FieldName_EnumValue, query.getFieldName());
        }
        if(query.getDisplayText() != null)
        {
            addLikeStartRestriction(criteria, EnumText.FieldName_DisplayText, query.getDisplayText());
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
