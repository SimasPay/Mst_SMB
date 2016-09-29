/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.DistributionChainLevelQuery;
import com.mfino.domain.Base;
import com.mfino.domain.DistributionChainLvl;
import com.mfino.domain.DistributionChainTemplate;

/**
 *
 * @author xchen
 */
public class DistributionChainLevelDAO extends BaseDAO<DistributionChainLvl> {

    public List<DistributionChainLvl> get(DistributionChainLevelQuery query) {

        Criteria criteria = createCriteria();

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(Base.FieldName_RecordID, query.getId()));
        }

        if (query.getDistributionChainTemplateID() != null) {
            final String templateNameAlias = DistributionChainLvl.FieldName_DistributionChainTemplateByTemplateID + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(DistributionChainLvl.FieldName_DistributionChainTemplateByTemplateID, templateNameAlias);
            criteria.add(Restrictions.eq(templateNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + DistributionChainTemplate.FieldName_RecordID,
                    query.getDistributionChainTemplateID()));
        }

        if (query.getLevel() != null) {
            criteria.add(Restrictions.eq(DistributionChainLvl.FieldName_DistributionLevel, query.getLevel()));
        }

        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<DistributionChainLvl> results = criteria.list();

        return results;
    }
}
