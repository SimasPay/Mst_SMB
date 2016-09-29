/**
 * 
 */
package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.domain.MfsbillerPartnerMap;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.MfsBiller;
import com.mfino.domain.Partner;

/**
 * @author Bala Sunku
 *
 */
public class MFSBillerPartnerDAO extends BaseDAO<MfsbillerPartnerMap> {
	
	public List<MfsbillerPartnerMap> get(MFSBillerPartnerQuery query) {
		Criteria criteria = createCriteria();
		
		if (query.getMfsBillerId() != null) {
			criteria.createAlias(MfsbillerPartnerMap.FieldName_MFSBiller, "mb");
			criteria.add(Restrictions.eq("mb."+MfsBiller.FieldName_RecordID, query.getMfsBillerId()));
		}
		if(query.getBillerType() != null){
			criteria.add(Restrictions.eq(MfsbillerPartnerMap.FieldName_BillerPartnerType, query.getBillerType()).ignoreCase());
		}
		if(query.getIntegrationCode() != null){
			criteria.add(Restrictions.eq(MfsbillerPartnerMap.FieldName_IntegrationCode, query.getIntegrationCode()).ignoreCase());
		}
		if(query.getBillerCode() != null){
			criteria.createAlias(MfsbillerPartnerMap.FieldName_MFSBiller, "mb1");
			criteria.add(Restrictions.eq("mb1." + MfsBiller.FieldName_MFSBillerCode, query.getBillerCode()).ignoreCase());
		}
		@SuppressWarnings("unchecked")
			List<MfsbillerPartnerMap> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(MfsbillerPartnerMap mb) {
        if (mb.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            mb.setMfinoServiceProvider(msp);
        }
        super.save(mb);
    }
    
    public List<Partner> getPartnersByBillerCode(String billerCode) {
    	Criteria criteria = createCriteria();
    	List<Partner> lstPartner = new ArrayList<Partner>();
    	
    	if (StringUtils.isNotBlank(billerCode)) {
    		criteria.createAlias(MfsbillerPartnerMap.FieldName_MFSBiller, "mb");
    		criteria.add(Restrictions.eq("mb."+MfsBiller.FieldName_MFSBillerCode, billerCode).ignoreCase());
    	}
    	
    	@SuppressWarnings("unchecked")
    	List<MfsbillerPartnerMap> lst = criteria.list();
    	
    	if (CollectionUtils.isNotEmpty(lst)) {
    		for (MfsbillerPartnerMap mfsbp: lst) {
    			lstPartner.add(mfsbp.getPartner());
    		}
    	}
    	
    	return lstPartner;
    }
    
    public MfsbillerPartnerMap getByBillerCode(String billerCode) {
		Criteria criteria = createCriteria();
		
		if(billerCode != null){
			criteria.createAlias(MfsbillerPartnerMap.FieldName_MFSBiller, "mb1");
			criteria.add(Restrictions.eq("mb1." + MfsBiller.FieldName_MFSBillerCode, billerCode).ignoreCase());
		}
		
		@SuppressWarnings("unchecked")
		List<MfsbillerPartnerMap> lst = criteria.list();
		
		if (CollectionUtils.isNotEmpty(lst)) {
			return lst.get(0);
		}
		return null;
	}

}
