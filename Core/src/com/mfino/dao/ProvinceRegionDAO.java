package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.ProvinceRegionQuery;
import com.mfino.domain.BranchCodes;
import com.mfino.domain.ProvinceRegion;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;

/**
 * 
 * @author Srinivaas
 */
public class ProvinceRegionDAO extends BaseDAO<ProvinceRegion> {

	public List<ProvinceRegion> get(ProvinceRegionQuery query) {
		Criteria criteria = createCriteria();
		
		criteria.add(Restrictions.eq(CmFinoFIX.CRProvinceRegion.FieldName_IdProvince, query.getIdProvince()));

		addOrder(QueryConstants.ASC_STRING, CmFinoFIX.CRProvinceRegion.FieldName_RegionId, criteria);		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<ProvinceRegion> results = criteria.list();

		return results;
	}
	
    public  String getProvinceRegionName(String proregRecId) {
    	Long id = null;
    	try{
    		id = Long.valueOf(proregRecId);
    	}catch(Exception e){
    		log.info(MessageText._("Given province region code is not a number : ") + proregRecId);
    		return null;
    	}
    	ProvinceRegion provinceRegion = getById(id);
        if (provinceRegion == null) {
            log.info(MessageText._("No Province Region Code defined with the given code : ") + proregRecId);
            return null;
        }
        return provinceRegion.getDisplayText();
    }

}
