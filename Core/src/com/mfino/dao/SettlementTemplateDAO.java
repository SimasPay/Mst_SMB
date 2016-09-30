/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SettlementTemplateQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.SettlementTemplate;

/**
 * @author sunil
 *
 */
public class SettlementTemplateDAO extends BaseDAO<SettlementTemplate>{
	
	public List<SettlementTemplate> get(SettlementTemplateQuery query) {
		Criteria criteria = createCriteria();
		
		if (StringUtils.isNotBlank(query.getSettlementName())) {
			criteria.add(Restrictions.eq(SettlementTemplate.FieldName_SettlementName, query.getSettlementName()).ignoreCase());
		}
		
		if (StringUtils.isNotBlank(query.getExactSettlementName())) {
			criteria.add(Restrictions.eq(SettlementTemplate.FieldName_SettlementName, query.getExactSettlementName()).ignoreCase());
		}
		
		if (query.getPartnerId() != null) {
			criteria.createAlias(SettlementTemplate.FieldName_Partner, "p");
			criteria.add(Restrictions.eq("p."+SettlementTemplate.FieldName_RecordID, query.getPartnerId()));
		}
		
        processBaseQuery(query, criteria);
//      // Paging
//      processPaging(query, criteria);
//      //applying Order
//      applyOrder(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<SettlementTemplate> result = criteria.list();
		
		return result;
	}
	
    @Override
    public void save(SettlementTemplate s) {
        if (s.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            s.setMfinoServiceProvider(msp);
        }
        super.save(s);
    }

}
