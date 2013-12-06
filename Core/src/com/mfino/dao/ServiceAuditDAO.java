/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionRuleQuery;
import com.mfino.domain.ServiceAudit;
import com.mfino.domain.TransactionRule;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class ServiceAuditDAO extends BaseDAO<ServiceAudit> {
	
	@SuppressWarnings("unchecked")
	public ServiceAudit getServiceAudit(Long serviceProviderId, Long serviceId, Integer sourceType, Long sourceId, Long KYCLevelId) {
		ServiceAudit sa = null;
		Criteria criteria = createCriteria();
		
		if (serviceProviderId != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceAudit.FieldName_ServiceProviderID, serviceProviderId));
		}
		if (serviceId != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceAudit.FieldName_ServiceID, serviceId));
		}
		if (sourceType != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceAudit.FieldName_SourceType, sourceType));
		}
		if (sourceId != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceAudit.FieldName_SourceID, sourceId));
		}
		if (KYCLevelId != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceAudit.FieldName_KYCLevelId, KYCLevelId));
		}
		
		List<ServiceAudit> lst = criteria.list();
		if (CollectionUtils.isNotEmpty(lst)) {
			sa = lst.get(0);
		}
		return sa;
	}
}
