/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.DistributionChainLevelQuery;
import com.mfino.domain.DistributionChainLevel;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author xchen
 */
public class DistributionChainLevelDAO extends BaseDAO<DistributionChainLevel> {

    public List<DistributionChainLevel> get(DistributionChainLevelQuery query) {

        Criteria criteria = createCriteria();

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBase.FieldName_RecordID, query.getId()));
        }

        if (query.getDistributionChainTemplateID() != null) {
            final String templateNameAlias = CmFinoFIX.CRDistributionChainLevel.FieldName_DistributionChainTemplateByTemplateID + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(CmFinoFIX.CRDistributionChainLevel.FieldName_DistributionChainTemplateByTemplateID, templateNameAlias);
            criteria.add(Restrictions.eq(templateNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRDistributionChainTemplate.FieldName_RecordID,
                    query.getDistributionChainTemplateID()));
        }

        if (query.getLevel() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRDistributionChainLevel.FieldName_DistributionLevel, query.getLevel()));
        }

        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<DistributionChainLevel> results = criteria.list();

        return results;
    }
}
