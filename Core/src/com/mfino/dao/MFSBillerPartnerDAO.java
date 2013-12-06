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
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class MFSBillerPartnerDAO extends BaseDAO<MFSBillerPartner> {
	
	public List<MFSBillerPartner> get(MFSBillerPartnerQuery query) {
		Criteria criteria = createCriteria();
		
		if (query.getMfsBillerId() != null) {
			criteria.createAlias(CmFinoFIX.CRMFSBillerPartner.FieldName_MFSBiller, "mb");
			criteria.add(Restrictions.eq("mb."+CmFinoFIX.CRMFSBiller.FieldName_RecordID, query.getMfsBillerId()));
		}
		if(query.getBillerType() != null){
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFSBillerPartner.FieldName_BillerPartnerType, query.getBillerType()).ignoreCase());
		}
		if(query.getIntegrationCode() != null){
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFSBillerPartner.FieldName_IntegrationCode, query.getIntegrationCode()).ignoreCase());
		}
		if(query.getBillerCode() != null){
			criteria.createAlias(CmFinoFIX.CRMFSBillerPartner.FieldName_MFSBiller, "mb1");
			criteria.add(Restrictions.eq("mb1." + CmFinoFIX.CRMFSBiller.FieldName_MFSBillerCode, query.getBillerCode()).ignoreCase());
		}
		@SuppressWarnings("unchecked")
			List<MFSBillerPartner> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(MFSBillerPartner mb) {
        if (mb.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            mb.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(mb);
    }
    
    public List<Partner> getPartnersByBillerCode(String billerCode) {
    	Criteria criteria = createCriteria();
    	List<Partner> lstPartner = new ArrayList<Partner>();
    	
    	if (StringUtils.isNotBlank(billerCode)) {
    		criteria.createAlias(CmFinoFIX.CRMFSBillerPartner.FieldName_MFSBiller, "mb");
    		criteria.add(Restrictions.eq("mb."+CmFinoFIX.CRMFSBiller.FieldName_MFSBillerCode, billerCode).ignoreCase());
    	}
    	
    	@SuppressWarnings("unchecked")
    	List<MFSBillerPartner> lst = criteria.list();
    	
    	if (CollectionUtils.isNotEmpty(lst)) {
    		for (MFSBillerPartner mfsbp: lst) {
    			lstPartner.add(mfsbp.getPartner());
    		}
    	}
    	
    	return lstPartner;
    }

}
