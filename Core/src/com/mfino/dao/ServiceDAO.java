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

import com.mfino.dao.query.ServiceQuery;
import com.mfino.domain.Service;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class ServiceDAO extends BaseDAO<Service>{

	public List<Service> get(ServiceQuery query) {
		
		Criteria serviceCriteria = createCriteria();
		
		if(query.getId()!=null){
			serviceCriteria.add(Restrictions.eq(CmFinoFIX.CRService.FieldName_RecordID, query.getId()));
		}
		  //Before Correcting errors reported by Findbugs:
			/*		if((null != query.getServiceProviderId()) && !("".equals(query.getService
		ProviderId())) && ((null != query.getPartnerId()) 
		&& !("".equals(query.getPartnerId())))){*/
		
		  //After Correcting the errors reported by Findbugs:only null check needed
		if((null != query.getServiceProviderId()) && (null != query.getPartnerId())){
			Criteria serviceProviderFromServicesCriteria = serviceCriteria.createCriteria("ServiceProviderServicesFromServiceID");
			Criteria serviceProviderCriteria = serviceProviderFromServicesCriteria.createCriteria("PartnerByServiceProviderID");
			serviceProviderCriteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_RecordID, query.getServiceProviderId()));
			
			Criteria partnerServiceFromServicesProviderFromServicesCriteria = serviceProviderFromServicesCriteria.createCriteria("PartnerServicesFromServiceProviderServicesID");
			partnerServiceFromServicesProviderFromServicesCriteria.add(Restrictions.eq(CmFinoFIX.CRPartnerServices.FieldName_PartnerServiceStatus, CmFinoFIX.PartnerServiceStatus_Active));
			Criteria partnerCriteria = partnerServiceFromServicesProviderFromServicesCriteria.createCriteria("Partner");
			partnerCriteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_RecordID, query.getPartnerId()));
		}
		
		if (StringUtils.isNotBlank(query.getServiceName())) {
			serviceCriteria.add(Restrictions.eq(CmFinoFIX.CRService.FieldName_ServiceName, query.getServiceName()).ignoreCase());
		}
		
        processBaseQuery(query, serviceCriteria);
        processPaging(query, serviceCriteria);
        
        serviceCriteria.addOrder(Order.desc(CmFinoFIX.CRService.FieldName_RecordID));
//        applyOrder(query, serviceCriteria);
        
		List<Service> results = serviceCriteria.list(); 
		return results;
	}
	
	/**
	 * Returns the Service by Name
	 * @param serviceName
	 * @return
	 */
	public Service getServiceByName(String serviceName) {
		Service s = null;
		if (StringUtils.isNotBlank(serviceName)) {
			ServiceQuery query = new ServiceQuery();
			query.setServiceName(serviceName);
			List<Service> lst = get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				s = lst.get(0);
			}			
		}
		return s;
	}

}
