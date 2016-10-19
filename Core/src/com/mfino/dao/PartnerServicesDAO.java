package com.mfino.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PartnerServicesQuery;
import com.mfino.domain.DistributionChainTemp;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
import com.mfino.fix.CmFinoFIX;

public class PartnerServicesDAO extends BaseDAO<PartnerServices>{

	public List<PartnerServices> get(PartnerServicesQuery query) {
		Criteria partnerServicesCriteria = createCriteria();
		
		if(query.getId()!=null){
			partnerServicesCriteria.add(Restrictions.eq(PartnerServices.FieldName_RecordID, query.getId()));
		}
		
		if (query.getPartnerId() != null) {
			partnerServicesCriteria.createAlias(PartnerServices.FieldName_Partner, "p");
			partnerServicesCriteria.add(Restrictions.eq("p." +Partner.FieldName_RecordID, query.getPartnerId()));
		}
		
		if(null != query.getServiceId()){
			partnerServicesCriteria.createAlias(PartnerServices.FieldName_Service, "service");
			partnerServicesCriteria.add(Restrictions.eq("service." + PartnerServices.FieldName_RecordID, query.getServiceId()));
		}
		
		@SuppressWarnings("unchecked")
		List<PartnerServices> results = partnerServicesCriteria.list(); 
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<PartnerServices> getPartnerServices(Long partnerId, Long serviceProviderId, Long serviceId) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(PartnerServices.FieldName_PartnerServiceStatus, CmFinoFIX.PartnerServiceStatus_Active));
		criteria.createAlias(PartnerServices.FieldName_Partner, "partner");
		criteria.add(Restrictions.eq("partner."+Partner.FieldName_RecordID, partnerId));
		criteria.createAlias(PartnerServices.FieldName_Service, "service");
		criteria.add(Restrictions.eq("service."+Service.FieldName_RecordID, serviceId));
		criteria.createAlias(PartnerServices.FieldName_PartnerByServiceProviderID, "sp");
		criteria.add(Restrictions.eq("sp."+Partner.FieldName_RecordID, serviceProviderId));
		
		List<PartnerServices> partnerServices = criteria.list();
		return partnerServices;
	}
	
	@SuppressWarnings("unchecked")
	public List<PartnerServices> getPartnerServices(Long partnerId, Long serviceProviderId, Long serviceId,List<Integer> status) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.in(PartnerServices.FieldName_PartnerServiceStatus,status));
		criteria.createAlias(PartnerServices.FieldName_Partner, "partner");
		criteria.add(Restrictions.eq("partner."+Partner.FieldName_RecordID, partnerId));
		criteria.createAlias(PartnerServices.FieldName_Service, "service");
		criteria.add(Restrictions.eq("service."+Service.FieldName_RecordID, serviceId));
		criteria.createAlias(PartnerServices.FieldName_PartnerByServiceProviderID, "sp");
		criteria.add(Restrictions.eq("sp."+Partner.FieldName_RecordID, serviceProviderId));
		
		List<PartnerServices> partnerServices = criteria.list();
		return partnerServices;
	}

	@SuppressWarnings("unchecked")
	public List<PartnerServices> getPartnerServices(Partner partner, Partner serviceProvider, Service service) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(PartnerServices.FieldName_Partner, partner));
		criteria.add(Restrictions.eq(PartnerServices.FieldName_PartnerByServiceProviderID, serviceProvider));
		criteria.add(Restrictions.eq(PartnerServices.FieldName_Service, service));
		
		List<PartnerServices> partnerServices = criteria.list();
		return partnerServices;
	}
	
    @Override
    public void save(PartnerServices ps) {
        if (ps.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            ps.setMfinoServiceProvider(msp);
        }
        super.save(ps);
    }	
    
    /**
     * Returns the Partners based on the Service Provider, Service Type and Distribution chain Template for the Partner Id
     * @param dctId
     * @param partnerType
     * @return
     */
    public Set<Partner> getPartnersByDCT(Long serviceProviderId, Long serviceId, Long dctId, Long partnerId) {
    	Set<Partner> result = new HashSet<Partner>();
    	Criteria criteria = createCriteria();
    	
//    	if (serviceProviderId != null && serviceId != null) {
//    		ServiceProviderServiceDAO spsDAO = new ServiceProviderServiceDAO();
//    		List<ServiceProviderServices> lst = spsDAO.getServiceProviderServices(serviceProviderId, serviceId);
//    		if (CollectionUtils.isNotEmpty(lst)) {
//    			criteria.createCriteria(PartnerServices.FieldName_ServiceProviderServices).
//    				add(Restrictions.eq(ServiceProviderServices.FieldName_RecordID, lst.get(0).getID()));
//    		}
//    	}
    	
    	if (serviceProviderId != null) {
    		criteria.createAlias(PartnerServices.FieldName_PartnerByServiceProviderID, "sp");
    		criteria.add(Restrictions.eq("sp."+Partner.FieldName_RecordID, serviceProviderId));
    	}
    	
    	if (serviceId != null) {
    		criteria.createAlias(PartnerServices.FieldName_Service, "service");
    		criteria.add(Restrictions.eq("service."+Service.FieldName_RecordID, serviceId));
    	}
    	
    	if (dctId != null) {
    		criteria.createCriteria(PartnerServices.FieldName_DistributionChainTemplate).
    			add(Restrictions.eq(DistributionChainTemp.FieldName_RecordID, dctId));
    	}
    	if (partnerId != null) {
    		criteria.createAlias(PartnerServices.FieldName_Partner, "partner");
    		criteria.add(Restrictions.ne("partner."+Partner.FieldName_RecordID, partnerId));
    	}

    	criteria.add(Restrictions.eq(PartnerServices.FieldName_PartnerServiceStatus, CmFinoFIX.PartnerServiceStatus_Active));
    	
    	@SuppressWarnings("unchecked")
    	List<PartnerServices> lstPS = criteria.list();
    	
    	if (CollectionUtils.isNotEmpty(lstPS)) {
    		for (PartnerServices ps:lstPS) {
    			result.add(ps.getPartnerByParentid());
    		}
    	}
    	return result;
    }
    
    /**
     * Resturns the List of active services for the given Partner
     * @param partnerId
     * @return
     */
    public List<Service> getServices(Long partnerId) {
    	List<Service> result = null;
    	PartnerServicesQuery query = new PartnerServicesQuery();
    	query.setPartnerId(partnerId);
    	List<PartnerServices> lst = get(query);
    	if (CollectionUtils.isNotEmpty(lst)) {
    		result = new ArrayList<Service>();
    		for (PartnerServices ps: lst) {
    			if (ps.getStatus() == CmFinoFIX.PartnerServiceStatus_Active.longValue()) {
    				result.add(ps.getService());
    			}
    		}
    	}
        return result;
    }

	@SuppressWarnings("unchecked")
	public List<PartnerServices> getPartnerServicesWithSameCollectorPocket(Partner partner, Pocket collectorPocket) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(PartnerServices.FieldName_Partner, partner));
		criteria.add(Restrictions.eq(PartnerServices.FieldName_CollectorPocket, collectorPocket));
		List<PartnerServices> partnerServices = criteria.list();
		return partnerServices;
	}
	
	/**
	 * Returns the PartnerServices based on the Distribution chain template and having the input partnerId as the parentId
	 * @param dctId
	 * @param parentId
	 * @return
	 */
    public List<PartnerServices> getPartnerServicesByDCT(Long dctId, Long partnerId) {
    	Criteria criteria = createCriteria();
    	
    	if (dctId != null) {
    		criteria.createCriteria(PartnerServices.FieldName_DistributionChainTemplate).
    			add(Restrictions.eq(DistributionChainTemp.FieldName_RecordID, dctId));
    	}
    	if (partnerId != null) {
    		criteria.createAlias(PartnerServices.FieldName_PartnerByParentID, "partner");
    		criteria.add(Restrictions.eq("partner."+Partner.FieldName_RecordID, partnerId));
    	}

    	//criteria.add(Restrictions.eq(PartnerServices.FieldName_PartnerServiceStatus, CmFinoFIX.PartnerServiceStatus_Active));
    	
    	@SuppressWarnings("unchecked")
    	List<PartnerServices> lstPS = criteria.list();
    	
    	return lstPS;
    }
    
}

